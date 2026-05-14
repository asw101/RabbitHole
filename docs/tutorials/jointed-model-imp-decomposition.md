# Tutorial: Trace the JointedModelImp Decomposition

This tutorial walks through the delegation pattern used to extract three
manager classes from `JointedModelImp`. By the end, you will understand how
the facade delegates to each manager and how `setNewResource()` orchestrates
all three.

For the full reference, see the [JointedModelImp Decomposition
reference](../reference/jointed-model-imp-decomposition.md). For a validation
checklist, see the
[how-to guide](../howto/validate-jointed-model-imp-decomposition.md).

## Prerequisites

- Familiarity with the scene graph model (`edu.cmu.cs.dennisc.scenegraph`)
- Repository checked out with `git submodule update --init tweedle-lang`
- Basic understanding of the `JointedModelResource` and `JointId` types

## 1. Understand the before state

Before extraction, `JointedModelImp.java` was 955 lines containing:

- **Two public inner interfaces** — `VisualData<R>` and
  `JointImplementationAndVisualDataFactory<R>` (lines 79–107)
- **One public inner interface** — `TreeWalkObserver` (lines 682–688)
- **One private inner class** — `JointImpWrapper` (lines 109–289, 180 lines)
- **Two private static inner classes** — `JointData` and
  `StraightenTreeWalkObserver` (lines 838–891)
- **One private enum** — `AddOp` (lines 712–734)
- **Constructor** — 33 lines of initialization mixing resource, visual, and
  joint setup (lines 291–324)
- **Mixed methods** — Reflection, factory delegation, scale management, bounds,
  tree walk, IK chains, and animation all interleaved

The class was hard to navigate because unrelated concerns were adjacent.
Finding all bounding-box logic required scanning past tree walk code and IK
chain computation.

## 2. Trace the resource binder

Open `JointedModelResourceBinder.java`. This is the simplest manager:

```java
class JointedModelResourceBinder<R extends JointedModelResource> {
    private JointImplementationAndVisualDataFactory<R> factory;
    private JointArrayId[] jointArrayIds;
    // ...
}
```

Key observations:

- **Package-private class** — No `public` modifier. Only accessible within
  `org.lgna.story.implementation`.
- **Owns the factory** — The `factory` field moved here from `JointedModelImp`.
- **`setFactory()` is the mutation point** — Called only from
  `JointedModelImp.setNewResource()`.
- **Reflection stays encapsulated** — `getAllJointIds()` uses
  `ReflectionUtilities.getPublicStaticFinalFields()` to discover joint IDs.
  This reflection detail does not leak to other managers.

Trace the `isSims()` delegation path:

```
JointedModelImp → resourceBinder.isSims() → factory.isSims()
JointHierarchyManager → isSimsSupplier.get() → resourceBinder.isSims()
```

The hierarchy manager gets a `Supplier<Boolean>` reference at construction,
avoiding a direct dependency on the resource binder from `JointImpWrapper`.

## 3. Trace the visual manager

Open `JointedModelVisualManager.java`. This manager owns everything related
to how the model appears on screen:

```java
class JointedModelVisualManager<R extends JointedModelResource> {
    private final JointedModelImp<?, R> owner;
    private final JointedModelResourceBinder<R> resourceBinder;
    private VisualData<R> visualData;
    private JointedModelVisualization visualization;
    // ...
}
```

Trace the scale property chain. Before extraction:

```java
// OLD: In JointedModelImp.getScaleProperties()
return new InstanceProperty[] {this.visualData.getSgVisuals()[0].scale};
```

After extraction:

```java
// NEW: In JointedModelImp.getScaleProperties()
return this.visualManager.getScaleProperties(this.sgScalable);

// NEW: In JointedModelVisualManager.getScaleProperties()
if (sgScalable != null) {
    return new InstanceProperty[] {sgScalable.scale};
} else {
    return new InstanceProperty[] {visualData.getSgVisuals()[0].scale};
}
```

The `sgScalable` field stays on `JointedModelImp` (it is `final` and set in
the constructor) and is passed as a parameter to the visual manager methods.
This avoids duplicating the field.

## 4. Trace the hierarchy manager

Open `JointHierarchyManager.java`. This is the largest manager because it
absorbs `JointImpWrapper`, the tree walk machinery, and IK chain logic:

```java
class JointHierarchyManager<R extends JointedModelResource> {
    private final JointedModelImp<?, R> owner;
    private final JointedModelResourceBinder<R> resourceBinder;
    private final Supplier<Boolean> isSimsSupplier;
    private final Map<JointId, JointImpWrapper> mapIdToJoint = Maps.newHashMap();
    private final Map<JointArrayId, JointId[]> mapArrayIdToJointIdArray = Maps.newHashMap();
    // ...
}
```

Trace the `JointImpWrapper.copyOnto()` method. Before:

```java
// OLD: In JointedModelImp.JointImpWrapper
@Override
protected void copyOnto(JointImp newJoint) {
    internalJointImp.copyOnto(newJoint);
    if (JointedModelImp.this.factory.isSims()) {  // direct outer-class access
        AdapterFactory.getAdapterFor(newJoint.getSgComposite());
    }
    // ...
}
```

After:

```java
// NEW: In JointHierarchyManager.JointImpWrapper
@Override
protected void copyOnto(JointImp newJoint) {
    internalJointImp.copyOnto(newJoint);
    if (isSimsSupplier.get()) {  // supplier from resource binder
        AdapterFactory.getAdapterFor(newJoint.getSgComposite());
    }
    // ...
}
```

The `Supplier<Boolean>` breaks the direct dependency from `JointImpWrapper`
to the factory field.

The animation methods (`animateStraightenOutJoints`, `strikePose`) also use
the `owner` reference to call inherited methods like `adjustDurationIfNecessary()`,
`perform()`, and `getProgram()`. These stay in HierarchyManager but route
through `owner`:

```java
// In JointHierarchyManager.animateStraightenOutJoints()
double adjusted = owner.adjustDurationIfNecessary(duration);
// ...
owner.perform(new StraightenOutJointsAnimation(adjusted, style));

// In JointHierarchyManager.strikePose()
ProgramImp program = owner.getProgram();
program.perform(new PoseAnimation(duration, style, owner, pose), null);
```

## 5. Trace setNewResource orchestration

This is the key method that coordinates all three managers. Open
`JointedModelImp.java` and find `setNewResource()`:

```java
public void setNewResource(JointedModelResource resource) {
    if (resource == this.getResource()) {
        return;                                           // 1. Guard
    }

    // 2. Save visual state
    Composite originalParent = visualManager.getVisualData().getSGParent();
    VisualData<?> oldVisualData = visualManager.getVisualData();
    Dimension3 oldScale = this.getScale();
    InstanceProperty[] oldScaleProperties = this.getScaleProperties();

    // 3. Update factory
    resourceBinder.setFactory(
        (JointImplementationAndVisualDataFactory<R>)
            resource.getImplementationAndVisualFactory());

    float originalOpacity = this.opacity.getValue();
    Paint originalPaint = this.paint.getValue();

    // 4. Replace visual data
    visualManager.replaceVisualData();

    // 5. Rebuild skeleton (only if joints exist)
    if (hierarchyManager.hasJoints()) {
        hierarchyManager.updateSkeleton();
    }

    // 6. Restore visual state
    visualManager.attachToParent(originalParent);
    oldVisualData.setSGParent(null);
    this.opacity.setValue(originalOpacity);
    this.paint.setValue(originalPaint);

    // 7. Migrate listeners and reapply scale
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

The order matters:
- **ResourceBinder first** — The other managers call `resourceBinder.getFactory()`
  and need the updated factory.
- **VisualManager second** — Creates new `VisualData` from the updated factory.
- **HierarchyManager third** — Rebuilds joints using the updated factory and
  matches them to existing wrappers.

## 6. Trace setScale coordination

`setScale()` is the one method that touches both managers — but only in the
fallback path. When `sgScalable` is non-null (a `Scalable` SG node was
provided at construction), the method uses the single SG property directly.
When `sgScalable` is null, the method must scale individual visuals AND joints:

```java
// In JointedModelImp
@Override
public void setScale(Dimension3 scale) {
    if (this.sgScalable != null) {
        this.sgScalable.scale.setValue(scale);
    } else {
        visualManager.setScaleOnVisuals(scale);
        hierarchyManager.setScaleOnJoints(scale);
    }
}
```

The `sgScalable != null` path is a one-liner that doesn't involve either
manager. The dual-manager coordination only happens in the `else` branch,
matching the original code where both the `visualData.getSgVisuals()` loop
and the `mapIdToJoint.values()` loop run when there is no aggregate scalable.

This is why `setScale()` stays on the facade — the `else` branch must
orchestrate two managers that know nothing about each other.

## 7. Verify your understanding

Run the characterization test and trace the assertions:

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am \
  -DfailIfNoTests=false \
  -Dcheckstyle.skip \
  -Dtest=JointedModelImpDecompositionTest \
  test
```

The test verifies:
- Manager field references are non-null after construction
- `TreeWalkObserver` is accessible as `JointedModelImp.TreeWalkObserver`
- Joint lookups return consistent wrapper instances
- Scale application reaches both visuals and joints

## Summary

| Before | After |
| --- | --- |
| 955 lines in one file | ~370 lines facade + 3 managers |
| Three concerns interleaved | Each manager owns one concern |
| `JointImpWrapper` references `JointedModelImp.this.factory` | `JointImpWrapper` uses `Supplier<Boolean>` |
| `setNewResource()` modifies fields directly | `setNewResource()` orchestrates three managers |
| `setScale()` touches `visualData` and `mapIdToJoint` | `setScale()` delegates to two managers (when `sgScalable` is null) |
| Reflection scattered in constructor | Reflection encapsulated in `JointedModelResourceBinder` |
