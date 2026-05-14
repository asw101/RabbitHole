# JointedModelImp Decomposition

This reference documents the extraction of joint hierarchy management, resource
binding, and visual/skin operations from `JointedModelImp` into three
package-private delegate classes (issue #577). The extraction reduces
`JointedModelImp.java` from 955 lines to approximately 370 lines while
preserving all existing public and protected API contracts.

## Contents

- [Motivation](#motivation)
- [Extracted responsibilities](#extracted-responsibilities)
- [File inventory](#file-inventory)
- [Manager creation order](#manager-creation-order)
- [Delegate pattern](#delegate-pattern)
- [JointedModelResourceBinder](#jointedmodelresourcebinder)
- [JointedModelVisualManager](#jointedmodelvisualmanager)
- [JointHierarchyManager](#jointhierarchymanager)
- [Retained on JointedModelImp](#retained-on-jointedmodelimp)
- [Visibility changes](#visibility-changes)
- [setNewResource orchestration](#setnewresource-orchestration)
- [Validation commands](#validation-commands)
- [Compatibility rules](#compatibility-rules)
- [Non-claims](#non-claims)

## Motivation

`JointedModelImp.java` at 955 lines mixed three distinct concerns:

1. **Resource binding** — Reflection-based joint ID discovery, factory
   delegation, `isSims()` queries, `DynamicResource` handling.
2. **Joint hierarchy management** — `JointImpWrapper` inner class, joint maps,
   tree walks, IK chain computation, pose/straighten animations.
3. **Visual/skin lifecycle** — `VisualData` creation and parenting, scale
   property management, bounding box computation, visualization overlay.

Each concern could be understood, tested, and modified independently.
Extracting them into delegates follows the same pattern established by the
[NonCachingTextRenderer pipeline extraction](./noncaching-text-renderer-pipeline-extraction.md)
and [StorytellingSceneEditor inner class extraction](./storytelling-scene-editor-inner-class-extraction.md).

## Extracted responsibilities

| Manager class | Concern | Key methods extracted |
| --- | --- | --- |
| `JointedModelResourceBinder<R>` | Factory delegation, resource queries | `getAllJointIds()`, `getJointArrayIds()`, `createJointImps()`, `getResource()`, `getOriginalJointOrientation()`, `getOriginalJointTransformation()`, `createJointImplementation()` |
| `JointedModelVisualManager<R>` | Visual lifecycle, scale, bounds | `getVisualData()`, `getSgVisuals()`, `getSgAppearances()`, `getScaleProperties()`, `getScale()`, `setScaleOnVisuals()`, `getAxisAlignedMinimumBoundingBox()` (all overloads), `updateCumulativeBound()`, `getVisualization()`, `showVisualization()`, `hideVisualization()` |
| `JointHierarchyManager<R>` | Joint maps, tree walk, IK, pose | `JointImpWrapper` (moved as inner class), `getJointImplementation()` (both overloads), `getJoints()`, `getRootJointImps()`, `treeWalk()`, `setAllJointPivotsVisible()`, `fillInJointArrays()`, `findJointsMatching()`, `matchNewDataToExistingJoints()`, `updateSkeleton()`, `getInclusiveListOfJointsBetween()`, `straightenOutJoints()`, `animateStraightenOutJoints()`, `strikePose()`, `setScaleOnJoints()` |

## File inventory

| File | Role | Approx lines |
| --- | --- | --- |
| `JointedModelImp.java` | Public/protected API facade, `@Override` delegates, constructor orchestration. | ~370 |
| `JointedModelResourceBinder.java` | Package-private delegate. Factory delegation, reflection-based joint discovery, `isSims()` supplier. | ~160 |
| `JointedModelVisualManager.java` | Package-private delegate. Visual data lifecycle, scale properties, bounding boxes, visualization overlay. | ~200 |
| `JointHierarchyManager.java` | Package-private delegate. `JointImpWrapper`, joint maps, tree walk, IK chains, pose/straighten. | ~380 |

All source files reside in
`core/story-api/src/main/java/org/lgna/story/implementation/`.

## Manager creation order

Managers are created in the `JointedModelImp` constructor in this order:

1. **`JointedModelResourceBinder`** — Created first. Provides factory access
   and joint ID discovery that the other managers depend on.
2. **`JointedModelVisualManager`** — Created second. Calls
   `factory.createVisualData()` through the resource binder.
3. **`JointHierarchyManager`** — Created last. Uses the resource binder's
   `createJointImps()` to build the joint map, then parents orphaned joints
   to the scene graph composite.

```java
// In JointedModelImp constructor
this.resourceBinder = new JointedModelResourceBinder<>(factory);
this.visualManager = new JointedModelVisualManager<>(this, resourceBinder);
this.hierarchyManager = new JointHierarchyManager<>(this, resourceBinder);

this.visualManager.attachToParent(getSgComposite());
this.hierarchyManager.parentOrphanedJoints(getSgComposite());

for (Visual sgVisual : this.visualManager.getSgVisuals()) {
    putInstance(sgVisual);
}
for (SimpleAppearance sgAppearance : this.visualManager.getSgAppearances()) {
    putInstance(sgAppearance);
}
```

## Delegate pattern

Each manager takes a back-reference to `JointedModelImp` and/or
`JointedModelResourceBinder` in its constructor. All state that was previously
private to `JointedModelImp` moves to the appropriate manager as private
fields.

```java
// JointedModelResourceBinder constructor
JointedModelResourceBinder(JointImplementationAndVisualDataFactory<R> factory) {
    this.factory = Objects.requireNonNull(factory);
}

// JointedModelVisualManager constructor
JointedModelVisualManager(JointedModelImp<?, R> owner,
                          JointedModelResourceBinder<R> resourceBinder) {
    this.owner = Objects.requireNonNull(owner);
    this.resourceBinder = Objects.requireNonNull(resourceBinder);
    this.visualData = resourceBinder.getFactory().createVisualData();
}

// JointHierarchyManager constructor
JointHierarchyManager(JointedModelImp<?, R> owner,
                      JointedModelResourceBinder<R> resourceBinder) {
    this.owner = Objects.requireNonNull(owner);
    this.resourceBinder = Objects.requireNonNull(resourceBinder);
    // builds joint map, wrappers, parent/child links
    initializeJoints();
}
```

## JointedModelResourceBinder

Owns the `JointImplementationAndVisualDataFactory<R>` reference and all
reflection-based joint discovery logic.

**Moved fields:**
- `factory` — `JointImplementationAndVisualDataFactory<R>`
- `jointArrayIds` — cached `JointArrayId[]`

**Key methods:**

| Method | Purpose |
| --- | --- |
| `getFactory()` | Returns the current factory (package-private). |
| `setFactory(factory)` | Replaces the factory during `setNewResource()`. |
| `getResource()` | Delegates to `factory.getResource()`. |
| `isSims()` | Delegates to `factory.isSims()`. Returns a `boolean`. |
| `getAllJointIds()` | Reflection-based discovery of `JointId` fields and `DynamicResource` joints. |
| `getJointArrayIds()` | Cached `JointArrayId[]` from reflection. |
| `createJointImps(owner)` | Creates `JointImp` instances for all joint IDs with parent linkage. Called by the hierarchy manager during initialization. |
| `createJointImplementation(owner, jointId)` | Single joint factory delegation. |
| `getJointArrayIdsFromFactory(owner, arrayId)` | Delegates to `factory.getJointArrayIds(owner, arrayId)`. Called during `getAllJointIds()` to include factory-provided joints in the full joint list. |
| `getOriginalJointOrientation(jointId)` | Factory delegation. |
| `getOriginalJointTransformation(jointId)` | Factory delegation. |

**`isSims()` as Supplier:** `JointImpWrapper.copyOnto()` needs to call
`isSims()`. Rather than passing the full resource binder, the hierarchy
manager receives a `Supplier<Boolean>` from the resource binder:

```java
// In JointHierarchyManager constructor
this.isSimsSupplier = resourceBinder::isSims;
```

## JointedModelVisualManager

Owns the `VisualData<R>` lifecycle, scale properties, bounding box
computation, and the `JointedModelVisualization` overlay.

**Moved fields:**
- `visualData` — `VisualData<R>`
- `visualization` — `JointedModelVisualization`

**Key methods:**

| Method | Purpose |
| --- | --- |
| `getVisualData()` | Returns the current `VisualData<R>`. |
| `getSgVisuals()` | Delegates to `visualData.getSgVisuals()`. |
| `getSgAppearances()` | Delegates to `visualData.getSgAppearances()`. |
| `getScaleProperties(sgScalable)` | Returns scale `InstanceProperty[]` from `sgScalable` or first visual. |
| `getScale(sgScalable)` | Returns current `Dimension3` scale. |
| `setScaleOnVisuals(scale)` | Applies scale to visuals (not joints — that is the hierarchy manager). |
| `attachToParent(parent)` | Calls `visualData.setSGParent(parent)`. |
| `replaceVisualData()` | Creates new `VisualData` from the resource binder's current factory. |
| `getAxisAlignedMinimumBoundingBox(...)` | All bounding box overloads. |
| `updateCumulativeBound(rv, trans, ignoreJointOrientations)` | Walks visuals to accumulate bounds. |
| `getVisualization()` | Lazy-creates `JointedModelVisualization` using stored `owner` reference. |
| `showVisualization(parent)` | Parents the visualization leaf. |
| `hideVisualization()` | Un-parents the visualization leaf. |

## JointHierarchyManager

Owns the joint map, `JointImpWrapper` inner class, tree walk, IK chain
computation, and pose/straighten animation logic.

**Moved inner classes:**
- `JointImpWrapper` — Moved from `JointedModelImp` into
  `JointHierarchyManager` as a private inner class.
- `JointData` — Moved as a private static inner class.
- `StraightenTreeWalkObserver` — Moved as a private static inner class.
- `AddOp` — Moved as a private enum.

**Moved fields:**
- `mapIdToJoint` — `Map<JointId, JointImpWrapper>`
- `mapArrayIdToJointIdArray` — `Map<JointArrayId, JointId[]>`

**Key methods:**

| Method | Purpose |
| --- | --- |
| `initializeJoints()` | Creates joint imps, wraps them, links parent/child. |
| `fillInJointArrays()` | Populates `mapArrayIdToJointIdArray` using `findJointsMatching()`. Iterates `resourceBinder.getJointArrayIds()` for array IDs. |
| `findJointsMatching(prefix)` | Filters `mapIdToJoint` keys by name prefix, returns sorted `JointId[]`. |
| `getJointIdArray(JointArrayId)` | Returns cached joint array from `mapArrayIdToJointIdArray`. |
| `getJointImplementation(JointId)` | Map lookup by `JointId`. |
| `getJointImplementation(String)` | Linear scan by name for `DynamicJointId`. |
| `getJoints()` | Returns all joints via tree walk. |
| `setAllJointPivotsVisible(boolean)` | Iterates all wrappers. |
| `getRootJointImps()` | Returns joints with null parent. |
| `treeWalk(TreeWalkObserver)` | Public tree walk entry point. |
| `updateSkeleton()` | Creates new joint imps and matches to existing wrappers. |
| `getInclusiveListOfJointsBetween(...)` | IK chain computation (both overloads). |
| `straightenOutJoints()` | Immediate joint straightening. |
| `animateStraightenOutJoints(duration, style)` | Animated joint straightening. |
| `strikePose(pose, duration, style)` | Pose animation delegation. |
| `setScaleOnJoints(scale)` | Applies scale to all joint wrappers. |
| `parentOrphanedJoints(parent)` | Parents root joints to the scene graph. |

**`TreeWalkObserver` stays on `JointedModelImp`:** The
`JointedModelImp.TreeWalkObserver` interface is referenced externally (e.g.,
by `JointedModelVisualization`). It remains as a public interface on
`JointedModelImp` to preserve the public API surface.

**`animateStraightenOutJoints` back-reference:** The anonymous
`StraightenOutJointsAnimation` class calls `getAnimated()` which returns the
`JointedModelImp` instance. The hierarchy manager receives the owner reference
for this purpose:

```java
@Override
public Animated getAnimated() {
    return JointHierarchyManager.this.owner;
}
```

**Animation method routing:** `animateStraightenOutJoints()` calls
`adjustDurationIfNecessary()` and `perform()`, both inherited by the facade
from `TransformableImp`. The hierarchy manager routes these through the `owner`
reference:

```java
double duration = owner.adjustDurationIfNecessary(duration);
// ...
owner.perform(new StraightenOutJointsAnimation(duration, style));
```

Similarly, `strikePose()` calls `owner.getProgram()` and
`program.perform(...)`, passing `owner` as the animated model to
`PoseAnimation`.

## Retained on JointedModelImp

The following stay on `JointedModelImp` as thin delegations or because they
are part of the public/protected API contract. This table highlights
`@Override` methods, orchestration methods, and public interfaces. All other
public and protected methods listed under the three managers (e.g.,
`getJointImplementation()`, `treeWalk()`, `straightenOutJoints()`,
`strikePose()`, `createJointImplementation()`) also remain as one-line
delegations on the facade per [compatibility rule #1](#compatibility-rules).

| Member | Reason |
| --- | --- |
| `VisualData<R>` interface | Public inner interface (`JointedModelImp.VisualData`). |
| `JointImplementationAndVisualDataFactory<R>` interface | Public inner interface. |
| `TreeWalkObserver` interface | Public inner interface, externally referenced. |
| `getAbstraction()` | `@Override` from `ModelImp`. |
| `getResource()` | Thin delegation to resource binder. |
| `getVisualResource()` | Thin delegation to resource binder (`factory.getResource()`). Returns `JointedModelResource`. |
| `getSgVisuals()` | `@Override`, delegates to visual manager. |
| `getSgPaintAppearances()` | `@Override`, delegates to visual manager. |
| `getSgOpacityAppearances()` | `@Override`, delegates to visual manager. |
| `getScaleProperties()` | `@Override`, delegates to visual manager. |
| `getScale()` | `@Override`, delegates to visual manager. |
| `setScale(Dimension3)` | `@Override`, orchestrates visual + hierarchy managers. |
| `getAxisAlignedMinimumBoundingBox(...)` | `@Override` overloads, delegate to visual manager. |
| `getDynamicAxisAlignedMinimumBoundingBox(...)` | `@Override` overloads, delegate to visual manager. |
| `getSize()` / `setSize(Dimension3)` | `@Override`, delegate to visual manager. |
| `updateCumulativeBound(...)` | `@Override`, delegates to visual manager. |
| `showVisualization()` / `hideVisualization()` | `@Override`, delegates to visual manager. |
| `getFrontOffsetForJoint()` / `getTopOffsetForJoint()` | `protected`, pass `this` as reference frame. |
| `setNewResource(JointedModelResource)` | Orchestrates all three managers. |
| `abstraction` field | Final, stays with the facade. |
| `sgScalable` field | Final, passed to visual manager methods. |

## Visibility changes

The following fields widen from `private` to package-private to support
delegate access:

| Field/Method | Old visibility | New visibility | Accessed by |
| --- | --- | --- | --- |
| None | — | — | — |

No visibility changes are required. Each manager owns its own state. The
`JointedModelImp` reference passed to managers provides access to the public
and protected methods already on the facade.

## setNewResource orchestration

`setNewResource()` remains on `JointedModelImp` because it coordinates all
three managers. The method:

1. Saves current visual state (parent, opacity, paint, scale, scale listeners).
2. Calls `resourceBinder.setFactory(newFactory)` to update the factory.
3. Calls `visualManager.replaceVisualData(...)` to swap visual data.
4. Calls `hierarchyManager.updateSkeleton()` to rebuild joints **only if joints
   exist** (preserves the `!mapIdToJoint.isEmpty()` guard from the original).
5. Restores visual parent, opacity, paint.
6. Migrates scale property listeners (preserves the `assert oldProp != null` guard).
7. Calls `setScale(oldScale)` to reapply scale.

```java
public void setNewResource(JointedModelResource resource) {
    if (resource == this.getResource()) {
        return;
    }
    Composite originalParent = visualManager.getVisualData().getSGParent();
    VisualData<?> oldVisualData = visualManager.getVisualData();
    Dimension3 oldScale = this.getScale();
    InstanceProperty[] oldScaleProperties = this.getScaleProperties();

    resourceBinder.setFactory(
        (JointImplementationAndVisualDataFactory<R>)
            resource.getImplementationAndVisualFactory());

    float originalOpacity = this.opacity.getValue();
    Paint originalPaint = this.paint.getValue();

    visualManager.replaceVisualData();

    if (hierarchyManager.hasJoints()) {
      hierarchyManager.updateSkeleton();
    }

    visualManager.attachToParent(originalParent);
    oldVisualData.setSGParent(null);
    this.opacity.setValue(originalOpacity);
    this.paint.setValue(originalPaint);

    InstanceProperty<?>[] newScaleProperties = this.getScaleProperties();
    for (int i = 0; i < oldScaleProperties.length; i++) {
        InstanceProperty<?> oldProp = oldScaleProperties[i];
        assert oldProp != null : i;
        for (PropertyListener propListener : oldProp.getPropertyListeners()) {
            newScaleProperties[i].addPropertyListener(propListener);
        }
    }
    this.setScale(oldScale);
}
```

## Validation commands

Compile:

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am -DfailIfNoTests=false -Dcheckstyle.skip compile
```

Test:

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am -DfailIfNoTests=false -Dcheckstyle.skip test
```

Line count check:

```bash
wc -l core/story-api/src/main/java/org/lgna/story/implementation/JointedModelImp.java
wc -l core/story-api/src/main/java/org/lgna/story/implementation/JointedModelResourceBinder.java
wc -l core/story-api/src/main/java/org/lgna/story/implementation/JointedModelVisualManager.java
wc -l core/story-api/src/main/java/org/lgna/story/implementation/JointHierarchyManager.java
```

Expected: `JointedModelImp.java` under 500 lines (target ~370).

Characterization tests:

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am \
  -DfailIfNoTests=false \
  -Dcheckstyle.skip \
  -Dtest=JointedModelImpDecompositionTest \
  test
```

## Compatibility rules

1. **All public and protected method signatures stay on `JointedModelImp`.**
   Callers see no API change.
2. **All `@Override` methods remain on the facade.** They delegate to the
   appropriate manager.
3. **`JointedModelImp.TreeWalkObserver` keeps its qualified name.**
   External references like `JointedModelImp.TreeWalkObserver` compile without
   change.
4. **`JointedModelImp.VisualData` and
   `JointedModelImp.JointImplementationAndVisualDataFactory` keep their
   qualified names.** These are public inner interfaces.
5. **All `assert` and `throw` guards are preserved verbatim.** No behavioral
   change to error handling.
6. **No synchronization changes.** The single-threaded scene graph model is
   unchanged.
7. **Dead code preserved as-is.** The unused `R resource` local in
   `treeWalk(JointImp, TreeWalkObserver)` is kept to avoid unrelated churn.

## Non-claims

This extraction does NOT:

- Change any behavior observable through the public `SJointedModel` API.
- Add new public classes or methods. All three managers are package-private.
- Modify the factory interfaces (`VisualData`, `JointImplementationAndVisualDataFactory`).
- Remove dead code or rename existing methods.
- Change the order of scene graph operations.
- Affect the IK solver (`org.lgna.ik.core.solver`).
- Address any other issue in the modernization backlog.
