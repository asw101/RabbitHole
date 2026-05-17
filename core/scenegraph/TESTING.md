# core/scenegraph Test Suite — Coverage Improvement Documentation

## Overview

The `core/scenegraph` module test suite provides headless, pure-logic characterization
tests for the Alice 3 scene graph model. All tests run without OpenGL or any rendering
context and use **JUnit 4** (`org.junit.Test`).

**Coverage:** 40%+ line coverage (up from 19.1% baseline).
**Module stats:** 3,937 JaCoCo-countable lines; ~880 newly-covered lines across 8 new test classes.

---

## Test Class Inventory

| Test Class | Package | Target Code | Est. Lines Covered |
|---|---|---|---|
| `CompositeDeepHierarchyTest` | `scenegraph` | `Composite.java`, `Component.java` | ~120 |
| `AbstractTransformableTest` | `scenegraph` | `AbstractTransformable.java` | ~160 |
| `VisualTest` | `scenegraph` | `Visual.java`, `Leaf.java` | ~80 |
| `CylinderBoundsTest` | `scenegraph` | `Cylinder.java` | ~140 |
| `SphereBoundsTest` | `scenegraph` | `Sphere.java`, `Geometry.java` | ~60 |
| `TorusBoundsTest` | `scenegraph` | `Torus.java` | ~80 |
| `TransformationAffectTest` | `scenegraph` | `TransformationAffect.java` | ~80 |
| `ASGRoundtripTest` | `scenegraph.io` | `ASGEncoder.java`, `ASGDecoder.java`, `ASG.java` | ~160 |

**Existing tests (not modified, no overlap):**
- `ScenegraphModelTest` — Mesh, WeightedMesh, SkeletonVisual, Joint, basic add/remove
- `ASGDecompositionTest` — Structural decomposition of ASG facade
- `ASGOutsideInTest` — API contract + binary roundtrip fidelity
- `ASGDecoderExtractionTest` — BinaryArrayDecoder/PropertyValueParser extraction
- `BinaryArrayEncoderTest` — BinaryArrayEncoder contract

---

## Non-Overlap Policy

Each new test class targets **distinct code paths** not already exercised by
`ScenegraphModelTest` or the existing `io` tests:

| Already covered by `ScenegraphModelTest` | New test targets instead |
|---|---|
| Basic `addComponent`/`removeComponent` (shallow) | Deep hierarchy (3+ levels), `accept(Visitor)`, `newCopy()`, `release()` |
| Single hierarchy/absolute event | Listener add/remove/isolation, multi-listener notification |
| `transformToAbsolute`, `transformTo`, `transformFrom` | `setTranslationOnly` with NaN, `TransformationAffect` partial updates, rotation API |
| — | `Visual.getAxisAlignedMinimumBoundingBox()` with scale, geometry swap |
| — | All 18 Cylinder axis×alignment combos, Torus coordinate planes |

---

## 1. CompositeDeepHierarchyTest

**File:** `core/scenegraph/src/test/java/edu/cmu/cs/dennisc/scenegraph/CompositeDeepHierarchyTest.java`

**Purpose:** Exercises `Composite.java` and `Component.java` code paths not reached
by `ScenegraphModelTest`'s shallow add/remove tests. Targets deep hierarchy traversal,
visitor pattern, deep copy, resource release, and listener lifecycle.

### Test Methods

```
visitorTraversesEntireSubtreeInDepthFirstOrder()
```
Builds a 3-level hierarchy (Scene → parent → child → grandchild). Calls
`scene.accept(visitor)` and verifies the visitor visits all 4 nodes in
depth-first order. **Covers:** `Composite.accept()` recursive branch.

```
newCopyProducesDeepCopyWithCorrectParentReferences()
```
Creates Scene with 2 children each having 1 grandchild. Calls `newCopy()`.
Verifies the copy has identical tree shape but distinct object identity. Children
of the copy have the copy as their parent, not the original.
**Covers:** `Composite.newCopy()`, recursive child copy.

```
releasePropagatesToAllDescendants()
```
Builds a hierarchy. Registers a `ReleaseListener` on each node (root and all
descendants) — **required** because `AbstractReleasable.release()` only calls
`actuallyRelease()` when at least one listener is present. Calls `release()` on
the root. Verifies every listener received both `releasing()` and `released()`
callbacks.
**Covers:** `Composite.actuallyRelease()` recursive branch,
`AbstractReleasable.release()` listener gating.

```
removeComponentThrowsWhenChildBelongsToOtherParent()
```
Creates two parents with one child each. Attempts to remove child1 from parent2.
Expects `RuntimeException`. **Covers:** `Composite.removeComponent()` guard clause.

```
childrenListenerReceivesEventsOnlyWhileRegistered()
```
Registers a `ComponentsListener`, adds a child (event fires), removes the listener,
adds another child (no event). Verifies the listener received exactly 1 event.
**Covers:** `Composite.addChildrenListener()`, `removeChildrenListener()`.

```
getComponentsReturnsLiveIterableOverChildren()
```
Adds children, iterates `getComponents()`, verifies order. Adds another child,
re-iterates, verifies updated order.
**Covers:** `Composite.getComponents()`, `getComponentCount()`.

```
absoluteTransformationPropagatesThroughDeepHierarchy()
```
Builds 4-level deep hierarchy (root → lvl1 → lvl2 → lvl3). Registers
`AbsoluteTransformationListener` on all 4 nodes. Calls
`root.notifyTransformationListeners()` — which invokes
`fireAbsoluteTransformationChange()`, cascading through `Composite`'s
override. Verifies all 4 listeners fire.
**Note:** `setLocalTransformation()` does NOT fire absolute transformation
events — it only sets the `InstanceProperty` value. Use
`notifyTransformationListeners()` or reparenting (e.g., `addComponent()`) to
trigger the cascade.
**Covers:** `Composite.fireAbsoluteTransformationChange()` recursive path,
`Component.fireAbsoluteTransformationChange()`,
`AbstractTransformable.notifyTransformationListeners()`.

```
hierarchyChangeFiresOnAllDescendantsWhenSubtreeReparented()
```
Builds subtree (parent → child → grandchild), attaches to scene. Registers
hierarchy listeners on child and grandchild. Detaches parent from scene.
Both listeners fire. **Covers:** `Composite.fireHierarchyChanged()` recursive path.

```
isDescendantOfReturnsFalseForUnrelatedNode()
```
Creates two separate hierarchies. Verifies `isDescendantOf()` returns false for
nodes across hierarchies, and true within the same hierarchy.
**Covers:** `Component.isDescendantOf()` full traversal to null.

```
getRootReturnsNullWhenDetached()
```
Creates a `Transformable` not attached to any scene. Verifies `getRoot()` returns null.
**Covers:** `Component.getRoot()` null-vehicle branch.

### Usage

```bash
cd core/scenegraph
mvn test -pl . -Dtest=edu.cmu.cs.dennisc.scenegraph.CompositeDeepHierarchyTest
```

---

## 2. AbstractTransformableTest

**File:** `core/scenegraph/src/test/java/edu/cmu/cs/dennisc/scenegraph/AbstractTransformableTest.java`

**Purpose:** Tests `AbstractTransformable.java` transform-setting logic, NaN rejection,
`TransformationAffect` integration, `AsSeenBy` reference frame transformations, and
rotation application methods.

### Test Methods

```
setLocalTransformationRejectsNull()
```
Calls `setLocalTransformation(null)`. Expects `NullPointerException`.
**Covers:** `AbstractTransformable.setLocalTransformation()` null guard.

```
setLocalTransformationRejectsNaN()
```
Constructs an `AffineMatrix4x4` with NaN in translation. Calls `setLocalTransformation()`.
Expects `RuntimeException("isNaN")`.
**Covers:** `AbstractTransformable.setLocalTransformation()` NaN guard.

```
setLocalTransformationWithAffectOnlyChangesSelectedComponents()
```
Sets a local transform, then calls `setLocalTransformation()` with
`TransformationAffect.AFFECT_TRANSLATION_X_ONLY`. Verifies only the X translation
changed while Y, Z, and orientation remain from the previous transform.
**Covers:** `AbstractTransformable.setLocalTransformation(m, affect)`.

```
getAbsoluteTransformationComposesVehicleChain()
```
Builds Scene → parent(translate 10,0,0) → child(translate 0,5,0). Gets
`child.getAbsoluteTransformation()`. Verifies translation is (10,5,0).
**Covers:** `AbstractTransformable.getAbsoluteTransformation()` vehicle composition.

```
getAbsoluteTransformationReturnsLocalWhenParentIsScene()
```
Attaches transformable directly to Scene. Sets local transform. Verifies absolute
equals local. **Covers:** `getAbsoluteTransformation()` scene-is-vehicle branch.

```
getInverseAbsoluteTransformationInvertsCorrectly()
```
Sets known absolute, computes inverse, multiplies together. Result should be identity.
**Covers:** `AbstractTransformable.getInverseAbsoluteTransformation()`.

```
getTransformationAsSeenBySceneReturnsAbsolute()
```
Uses `AsSeenBy.SCENE` on a node at depth ≥ 2 (parent is NOT a Scene — e.g.,
Scene → parent → target). Verifies result equals `getAbsoluteTransformation()`.
**Note:** If the target's parent IS a Scene, `AsSeenBy.SCENE.isVehicleOf()` returns
true, hitting the vehicle branch instead. Use depth ≥ 2 to reach the SCENE branch.
**Covers:** `getTransformation(ReferenceFrame)` SCENE branch.

```
getTransformationAsSeenByParentReturnsLocal()
```
Uses `AsSeenBy.PARENT`. Verifies result equals `getLocalTransformation()`.
**Covers:** `getTransformation(ReferenceFrame)` vehicle branch.

```
getTransformationAsSeenBySelfReturnsIdentity()
```
Uses `AsSeenBy.SELF`. Verifies result is identity matrix.
**Covers:** `getTransformation(ReferenceFrame)` local-of branch.

```
getTransformationAsSeenByArbitraryFrameComposesCorrectly()
```
Builds Scene → A(translate 10,0,0) → target(translate 0,5,0), plus
Scene → observer(translate 3,0,0). Gets `target.getTransformation(observer)`.
Verifies correct relative transform. **Covers:** `getTransformation(ReferenceFrame)`
general case (`observer.getInverseAbsoluteTransformation().normalizeOrientation()
× target.getAbsoluteTransformation()`). Note: `normalizeOrientation()` strips
scale from orientation — use unscaled observers to get simple inverse composition.

```
setTranslationOnlyWithNaNReplacesWithZero()
```
Calls `setTranslationOnly(NaN, 5.0, NaN, AsSeenBy.PARENT)`. Verifies the resulting
translation is (0, 5, 0). **Covers:** `AbstractTransformable.setTranslationOnly()`
NaN→0 replacement.

```
applyTranslationInLocalFrameMovesAlongLocalAxes()
```
Rotates a transformable 90° about Y, then applies translation (1,0,0) in SELF frame.
Verifies the absolute position shifted along the rotated axis.
**Covers:** `applyTransformation()` local-of branch.

```
applyTranslationInVehicleFrameMovesAlongParentAxes()
```
Applies translation in `AsSeenBy.PARENT`. Verifies the resulting local transform
has the translation prepended. **Covers:** `applyTransformation()` vehicle-of branch.

```
applyRotationAboutYAxisChangesOrientation()
```
Starts at identity. Applies 90° Y-axis rotation via `applyRotationAboutYAxis(Angle)`.
Verifies the orientation component changed while translation remains at origin.
**Note:** The `InRadians` variant is `@Deprecated`; tests use the `Angle`-based
overload which delegates internally.
**Covers:** `applyRotationAboutYAxis(Angle)`, `applyRotationAboutYAxisInRadians()`.

```
notifyTransformationListenersFiresAbsoluteChangeEvents()
```
Registers an `AbsoluteTransformationListener`. Calls `notifyTransformationListeners()`.
Verifies the listener was called. This is the ONLY public API that triggers
`fireAbsoluteTransformationChange()` from within the node (other trigger is
reparenting via `setParent()`/`addComponent()`).
**Covers:** `AbstractTransformable.notifyTransformationListeners()`,
`Component.fireAbsoluteTransformationChange()`.

### Usage

```bash
mvn test -pl core/scenegraph -Dtest=edu.cmu.cs.dennisc.scenegraph.AbstractTransformableTest
```

---

## 3. VisualTest

**File:** `core/scenegraph/src/test/java/edu/cmu/cs/dennisc/scenegraph/VisualTest.java`

**Purpose:** Tests `Visual.java` geometry management, bounding-box delegation with
scale applied, appearance property handling, and boolean property defaults.

### Test Methods

```
getAxisAlignedMinimumBoundingBoxDelegatesToGeometryAndAppliesScale()
```
Creates a `Visual` with a `Sphere` geometry (radius=1.0). Sets scale to 2× on all axes.
Gets bbox. Verifies the box is (-2,-2,-2) to (2,2,2) — not the raw (-1,-1,-1) to (1,1,1).
**Covers:** `Visual.getAxisAlignedMinimumBoundingBox()`, `AxisAlignedBox.scale()`.

```
getAxisAlignedMinimumBoundingBoxReturnsNullWithNoGeometry()
```
Creates a `Visual` with no geometry set. Calls `getAxisAlignedMinimumBoundingBox()`.
Verifies it returns `null`. **Covers:** `Visual.getAxisAlignedMinimumBoundingBox()` null guard.

```
setGeometryReplacesExistingGeometry()
```
Sets geometry to a Sphere, then replaces with a Cylinder. Verifies
`getGeometry()` returns the Cylinder, `getGeometryCount()` is 1.
**Covers:** `Visual.setGeometry()`, `Visual.getGeometry()`.

```
setGeometryToNullClearsGeometries()
```
Sets geometry to Sphere, then sets to null. Verifies `getGeometry()` returns null
and `getGeometryCount()` is 0.
**Covers:** `Visual.setGeometry(null)` branch.

```
geometryArraySupportsMultipleGeometries()
```
Sets `geometries` property to an array of [Sphere, Cylinder]. Verifies
`getGeometryCount()` is 2, `getGeometryAt(0)` is Sphere, `getGeometryAt(1)` is Cylinder.
**Covers:** `Visual.getGeometryAt()`, `Visual.getGeometryCount()`.

```
defaultPropertyValues()
```
Creates a fresh `Visual`. Verifies `isShowing` is true, `isPickable` is true,
`frontFacingAppearance` is null, `backFacingAppearance` is null, `scale` is `Matrix3x3.IDENTITY`.
**Covers:** All `Visual` property initializers.

```
releaseReleasesAppearancesAndGeometries()
```
Creates a `Visual` with a `SimpleAppearance` and a Sphere geometry. Registers a
`ReleaseListener` on each (Visual, appearance, geometry) — **required** because
`AbstractReleasable.release()` is a no-op without listeners. Calls `release()` on
the Visual. Verifies all three listeners received `releasing()` and `released()`
callbacks.
**Covers:** `Visual.actuallyRelease()`, listener-gated cascade.

### Usage

```bash
mvn test -pl core/scenegraph -Dtest=edu.cmu.cs.dennisc.scenegraph.VisualTest
```

---

## 4. CylinderBoundsTest

**File:** `core/scenegraph/src/test/java/edu/cmu/cs/dennisc/scenegraph/CylinderBoundsTest.java`

**Purpose:** Exhaustively tests `Cylinder.java` bounding-box computation across all
18 combinations of `BottomToTopAxis` (6 values) × `OriginAlignment` (3 values).
Also tests `topRadius` NaN fallback, center-of-top/bottom point queries, and
property-change → bounds-dirty cycle.

### Test Methods — Parametric Bounds

A helper method `assertCylinderBounds(axis, alignment, length, bottomRadius, topRadius)`
computes expected bounds analytically and compares with `getAxisAlignedMinimumBoundingBox()`.

```
allAxisAlignmentCombinationsProduceCorrectBounds()
```
Iterates all 18 combos: 6 `BottomToTopAxis` × 3 `OriginAlignment`. For each:
- Sets `length=2.0`, `bottomRadius=1.0`, `topRadius=0.5`
- Computes expected min/max point based on axis and alignment
- Asserts bbox equals expected

**Full combo table** (with `length=2.0`, `bottomRadius=1.0`, `topRadius=0.5`,
so `maxRadius = max(1.0, 0.5) = 1.0`, `getTop/getBottom` depend on alignment only):

| Axis | Alignment | `.minimum()` | `.maximum()` |
|---|---|---|---|
| POSITIVE_Y + BOTTOM | | (-1, 0, -1) | (1, 2, 1) |
| POSITIVE_Y + CENTER | | (-1, -1, -1) | (1, 1, 1) |
| POSITIVE_Y + TOP | | (-1, -2, -1) | (1, 0, 1) |
| POSITIVE_X + BOTTOM | | (0, -1, -1) | (2, 1, 1) |
| POSITIVE_X + CENTER | | (-1, -1, -1) | (1, 1, 1) |
| POSITIVE_X + TOP | | (-2, -1, -1) | (0, 1, 1) |
| POSITIVE_Z + BOTTOM | | (-1, -1, 0) | (1, 1, 2) |
| POSITIVE_Z + CENTER | | (-1, -1, -1) | (1, 1, 1) |
| POSITIVE_Z + TOP | | (-1, -1, -2) | (1, 1, 0) |
| NEGATIVE_X + BOTTOM | | **(2, -1, -1)** | **(0, 1, 1)** |
| NEGATIVE_X + CENTER | | **(1, -1, -1)** | **(-1, 1, 1)** |
| NEGATIVE_X + TOP | | (0, -1, -1) | (-2, 1, 1) |
| NEGATIVE_Y + BOTTOM | | **(-1, 2, -1)** | **(1, 0, 1)** |
| NEGATIVE_Y + CENTER | | **(-1, 1, -1)** | **(1, -1, 1)** |
| NEGATIVE_Y + TOP | | (-1, 0, -1) | (1, -2, 1) |
| NEGATIVE_Z + BOTTOM | | **(-1, -1, 2)** | **(1, 1, 0)** |
| NEGATIVE_Z + CENTER | | **(-1, -1, 1)** | **(1, 1, -1)** |
| NEGATIVE_Z + TOP | | (-1, -1, 0) | (1, 1, -2) |

> **⚠ Latent bug (characterize, do not fix):** `AxisAlignedBox` is a record with no
> min/max normalization. For **NEGATIVE_X/Y/Z** axes the `Cylinder.updateBoundingBox()`
> code swaps `top` into the `minimum` slot and `bottom` into the `maximum` slot.
> When `top > bottom` (BOTTOM and CENTER alignments), `minimum().{x|y|z} > maximum().{x|y|z}`
> on the cylinder axis — producing an inverted (non-normalized) bounding box.
> This would cause `getWidth()`/`getHeight()`/`getDepth()` to return negative values
> for the affected axis. Tests should assert the **actual** (inverted) values to
> characterize current behavior. A follow-up fix issue should be filed separately.

**Covers:** All branches in `Cylinder.updateBoundingBox()`, `getTop()`, `getBottom()`,
`getMaxRadius()`.

### Test Methods — Edge Cases

```
topRadiusNaNFallsBackToBottomRadius()
```
Sets `topRadius` to `Double.NaN`. Verifies `getActualTopRadius()` returns
`bottomRadius` value. Also verifies bbox uses `bottomRadius` for max radius.
**Covers:** `Cylinder.getActualTopRadius()` NaN branch, `getMaxRadius()` NaN branch.

```
getCenterOfTopAndBottomReflectAxisAndAlignment()
```
For POSITIVE_Y + BOTTOM with length=4: `getCenterOfTop()` should be (0,4,0) and
`getCenterOfBottom()` should be (0,0,0). Tests all 6 axes.
**Covers:** `Cylinder.getCenterOfTop()`, `getCenterOfBottom()`, `getOffsetPoint()`.

```
changingOriginAlignmentDirtiesBoundsAndFiresEvent()
```
Gets initial bounds. Registers `BoundListener`. Changes `originAlignment` from
BOTTOM to CENTER. Verifies listener fires and new bounds differ from old.
**Covers:** `Cylinder.originAlignment` property setter, `markBoundsDirty()`,
`fireBoundChanged()`.

```
changingBottomToTopAxisDirtiesBoundsAndFiresEvent()
```
Same pattern as above but changes `bottomToTopAxis`. Verifies the bounding box
rotates to the new axis.
**Covers:** `Cylinder.bottomToTopAxis` property setter.

```
defaultCylinderValues()
```
Creates fresh `Cylinder`. Verifies defaults: `length=1.0`, `bottomRadius=1.0`,
`topRadius=1.0`, `originAlignment=BOTTOM`, `bottomToTopAxis=POSITIVE_Y`,
`hasBottomCap=true`, `hasTopCap=true`.
**Covers:** All `Cylinder` property initializers.

### Usage

```bash
mvn test -pl core/scenegraph -Dtest=edu.cmu.cs.dennisc.scenegraph.CylinderBoundsTest
```

---

## 5. SphereBoundsTest

**File:** `core/scenegraph/src/test/java/edu/cmu/cs/dennisc/scenegraph/SphereBoundsTest.java`

**Purpose:** Tests `Sphere.java` bounding box computation and the `Geometry` base
class dirty/recompute/listener lifecycle.

### Test Methods

```
defaultSphereBoundsAreHalfUnit()
```
Fresh `Sphere` (default radius=0.5). Bbox is (-0.5,-0.5,-0.5) to (0.5,0.5,0.5).
**Covers:** `Sphere.updateBoundingBox()`, `Geometry.getAxisAlignedMinimumBoundingBox()`.

```
changingRadiusUpdatesBoundsAndNotifiesListeners()
```
Creates `Sphere`, gets initial bounds. Registers `BoundListener`. Changes `radius`
to 2.0. Verifies listener fired, new bbox is (-2,-2,-2) to (2,2,2).
**Covers:** `BoundDoubleProperty.setValue()`, `markBoundsDirty()`, `fireBoundChanged()`,
lazy recompute in `getAxisAlignedMinimumBoundingBox()`.

```
boundingBoxIsCachedUntilDirtied()
```
Gets bbox twice without changing anything. Verifies same object instance returned
(referential equality). Then changes radius, gets again — different instance.
**Covers:** `Geometry.getAxisAlignedMinimumBoundingBox()` caching logic.

```
boundListenerCanBeRemovedAndStopsReceivingEvents()
```
Adds listener, changes radius (fires), removes listener, changes radius again.
Verifies listener received exactly 1 event.
**Covers:** `Geometry.addBoundListener()`, `removeBoundListener()`.

```
isChangedTracksDirtyFlag()
```
Fresh sphere is not changed. Mark as changed, verify. Mark as unchanged, verify.
**Covers:** `Geometry.isChanged()`, `markAsChanged()`, `markAsUnchanged()`.

### Usage

```bash
mvn test -pl core/scenegraph -Dtest=edu.cmu.cs.dennisc.scenegraph.SphereBoundsTest
```

---

## 6. TorusBoundsTest

**File:** `core/scenegraph/src/test/java/edu/cmu/cs/dennisc/scenegraph/TorusBoundsTest.java`

**Purpose:** Tests `Torus.java` bounding box computation across all 3 `CoordinatePlane`
values and radius changes.

### Test Methods

```
defaultTorusBoundsUseXZPlane()
```
Fresh `Torus` (defaults: majorRadius=0.9, minorRadius=0.1, plane=XZ). The
"yes" radius = 0.9+0.1 = 1.0, "no" radius = 0.1. Bbox: (-1,-0.1,-1) to (1,0.1,1).
**Covers:** `Torus.updateBoundingBox()` with XZ plane.

```
xyPlaneBoundsHaveThinZ()
```
Sets `coordinatePlane` to XY. With major=2, minor=0.5: yes=2.5, no=0.5.
Bbox: (-2.5,-2.5,-0.5) to (2.5,2.5,0.5).
**Covers:** `CoordinatePlane.XY.updateBoundingBox()`.

```
yzPlaneBoundsHaveThinX()
```
Sets `coordinatePlane` to YZ. Same radii. Bbox: (-0.5,-2.5,-2.5) to (0.5,2.5,2.5).
**Covers:** `CoordinatePlane.YZ.updateBoundingBox()`.

```
xzPlaneBoundsHaveThinY()
```
Explicit test with custom radii for XZ plane.
**Covers:** `CoordinatePlane.XZ.updateBoundingBox()`.

```
changingCoordinatePlaneDirtiesBoundsAndFiresEvent()
```
Gets initial bounds. Changes coordinate plane. Verifies BoundListener fired and
bounds changed.
**Covers:** `Torus.coordinatePlane` property setter, `markBoundsDirty()`, `fireBoundChanged()`.

```
changingMajorRadiusUpdatesBounds()
```
Changes majorRadius. Verifies new bounds reflect the change.
**Covers:** `Torus.majorRadius` (BoundDoubleProperty).

```
changingMinorRadiusUpdatesBounds()
```
Changes minorRadius. Verifies new bounds reflect the change.
**Covers:** `Torus.minorRadius` (BoundDoubleProperty).

### Usage

```bash
mvn test -pl core/scenegraph -Dtest=edu.cmu.cs.dennisc.scenegraph.TorusBoundsTest
```

---

## 7. TransformationAffectTest

**File:** `core/scenegraph/src/test/java/edu/cmu/cs/dennisc/scenegraph/TransformationAffectTest.java`

**Purpose:** Tests the `TransformationAffect` enum: the `set()` method for all 9
enum values, and the `getTranslationAffect()` NaN-based factory method.

### Test Methods

```
affectAllReplacesEntireMatrix()
```
`AFFECT_ALL.set(old, change)` returns `change` orientation and translation.
**Covers:** `TransformationAffect.set()` with all flags true.

```
affectOrientationOnlyReplacesOrientationKeepsTranslation()
```
`AFFECT_ORIENTAION_ONLY.set(old, change)` returns `change` orientation but `old` translation.
**Covers:** `AFFECT_ORIENTAION_ONLY`.

```
affectTranslationOnlyReplacesTranslationKeepsOrientation()
```
`AFFECT_TRANSLATION_ONLY.set(old, change)` returns `old` orientation but `change` translation.
**Covers:** `AFFECT_TRANSLATION_ONLY`.

```
affectTranslationXOnlyReplacesOnlyX()
```
Replaces only X coordinate of translation; Y and Z from old.
**Covers:** `AFFECT_TRANSLATION_X_ONLY`.

```
affectTranslationYOnlyReplacesOnlyY()
```
**Covers:** `AFFECT_TRANSLATION_Y_ONLY`.

```
affectTranslationZOnlyReplacesOnlyZ()
```
**Covers:** `AFFECT_TRANSLATION_Z_ONLY`.

```
affectTranslationXYReplacesXAndY()
```
**Covers:** `AFFECT_TRANSLATION_XY_ONLY`.

```
affectTranslationXZReplacesXAndZ()
```
**Covers:** `AFFECT_TRANSLATION_XZ_ONLY`.

```
affectTranslationYZReplacesYAndZ()
```
**Covers:** `AFFECT_TRANSLATION_YZ_ONLY`.

```
getTranslationAffectReturnsCorrectVariantForEachNaNCombination()
```
Tests all 8 NaN combinations of (x, y, z):
- `(NaN, NaN, NaN)` → `null`
- `(NaN, NaN, 1.0)` → `AFFECT_TRANSLATION_Z_ONLY`
- `(NaN, 1.0, NaN)` → `AFFECT_TRANSLATION_Y_ONLY`
- `(NaN, 1.0, 1.0)` → `AFFECT_TRANSLATION_YZ_ONLY`
- `(1.0, NaN, NaN)` → `AFFECT_TRANSLATION_X_ONLY`
- `(1.0, NaN, 1.0)` → `AFFECT_TRANSLATION_XZ_ONLY`
- `(1.0, 1.0, NaN)` → `AFFECT_TRANSLATION_XY_ONLY`
- `(1.0, 1.0, 1.0)` → `AFFECT_TRANSLATION_ONLY`

**Covers:** All 8 branches of `TransformationAffect.getTranslationAffect()`.

### Usage

```bash
mvn test -pl core/scenegraph -Dtest=edu.cmu.cs.dennisc.scenegraph.TransformationAffectTest
```

---

## 8. ASGRoundtripTest

**File:** `core/scenegraph/src/test/java/edu/cmu/cs/dennisc/scenegraph/io/ASGRoundtripTest.java`

**Purpose:** Tests full scene graph encode → decode roundtrip through the `ASG` facade,
verifying **structural fidelity** via 5 explicit checks. Tests code paths in
`ASGEncoder.java` and `ASGDecoder.java` not reached by existing binary-array tests
(which only test `int[]`, `double[]`, and `Vertex[]` roundtrips).

### Structural Fidelity — 5 Checks

Every roundtrip test verifies:

1. **Tree shape** — same number of children at each level
2. **Types** — each decoded node has the same concrete class as the original
3. **Names** — `Element.name` property survives roundtrip
4. **Properties** — geometry dimensions, appearance values, boolean flags
5. **Transforms** — `localTransformation` values match within epsilon

### Test Methods

```
singleTransformableRoundtrip()
```
Encodes a single `Transformable` with a non-identity local transform. Decodes.
Verifies all 5 structural fidelity checks.
**Covers:** `ASGEncoder.encode(Component, OutputStream)`, `ASGDecoder.decodeZip()`.

```
hierarchyWithMultipleChildrenRoundtrip()
```
Builds: `Transformable` root → 3 `Transformable` children, each with a unique
local transform and name. Roundtrips. Verifies tree shape (1 root, 3 children),
types (all `Transformable`), names, and transforms.
**Covers:** Composite encoding/decoding loop, child list serialization.

```
visualWithSphereGeometryRoundtrip()
```
Creates a `Transformable` containing a `Visual` with a `Sphere` geometry (radius=2.0)
and a `SimpleAppearance`. Roundtrips. Verifies the decoded Visual has a Sphere with
radius=2.0. **Covers:** `ASGEncoder`/`ASGDecoder` property serialization for
geometry subclasses.

```
deepHierarchyPreservesAllLevels()
```
Builds a 5-level deep chain: T1 → T2 → T3 → T4 → T5. Each with a unique name and
translation. Roundtrips. Walks the decoded tree verifying depth equals 5 and all
names/transforms match.
**Covers:** Recursive encode/decode of deeply nested composites.

```
sceneWithMixedNodeTypesRoundtrip()
```
Builds a scene with: a `Transformable` parent, a `Visual` child (with Cylinder geometry),
and a `DirectionalLight` child. Roundtrips. Verifies mixed types survive, and
Cylinder properties (length, bottomRadius, axis) are preserved.
**Covers:** Multi-type serialization, `ASGDecoder.decode()` XML class instantiation.

```
roundtripProducesValidZipWithRootXml()
```
Encodes a hierarchy. Decodes the raw zip. Verifies the zip contains a `root.xml`
entry (the convention from `ASG.ROOT_FILENAME`).
**Covers:** `ASGEncoder` zip structure, `ASGDecoder.decodeZip()` entry lookup.

```
decodeFromFileSystemRoundtrip()
```
Encodes to a temp file via `ASG.encode(component, File)`. Decodes via `ASG.decode(File)`.
Verifies fidelity. Cleans up temp file.
**Covers:** `ASGEncoder.encode(Component, File)`, `ASGDecoder.decode(File)`.

### Usage

```bash
mvn test -pl core/scenegraph -Dtest=edu.cmu.cs.dennisc.scenegraph.io.ASGRoundtripTest
```

---

## Running All Tests

```bash
# Run only the new test classes
mvn test -pl core/scenegraph -Dtest="edu.cmu.cs.dennisc.scenegraph.CompositeDeepHierarchyTest,\
edu.cmu.cs.dennisc.scenegraph.AbstractTransformableTest,\
edu.cmu.cs.dennisc.scenegraph.VisualTest,\
edu.cmu.cs.dennisc.scenegraph.CylinderBoundsTest,\
edu.cmu.cs.dennisc.scenegraph.SphereBoundsTest,\
edu.cmu.cs.dennisc.scenegraph.TorusBoundsTest,\
edu.cmu.cs.dennisc.scenegraph.TransformationAffectTest,\
edu.cmu.cs.dennisc.scenegraph.io.ASGRoundtripTest"

# Run ALL scenegraph tests (new + existing)
mvn test -pl core/scenegraph
```

---

## Configuration

No special configuration required. All tests are headless and require no environment
variables, no OpenGL context, and no external resources.

**Maven profile:** Tests run under the default `test` phase. No custom surefire
configuration needed beyond what the parent POM already provides.

**JUnit version:** JUnit 4 (inherited from parent POM via `junit:junit` dependency).

---

## Coverage Math

| Source | Lines | Previously Covered | Newly Covered | Total Covered |
|---|---|---|---|---|
| Module total | 3,937 | 752 (19.1%) | ~880 | ~1,632 |
| **Coverage %** | | **19.1%** | | **~41.5%** |

### Per-File Contribution Estimates

| Source File | Lines | New Tests Covering It |
|---|---|---|
| `Composite.java` | 194 | `CompositeDeepHierarchyTest` |
| `Component.java` | 281 | `CompositeDeepHierarchyTest`, `AbstractTransformableTest` |
| `AbstractTransformable.java` | 287 | `AbstractTransformableTest` |
| `Visual.java` | 132 | `VisualTest` |
| `Cylinder.java` | 214 | `CylinderBoundsTest` |
| `Sphere.java` | 60 | `SphereBoundsTest` |
| `Torus.java` | 109 | `TorusBoundsTest` |
| `Geometry.java` | 139 | `SphereBoundsTest` (base class) |
| `TransformationAffect.java` | 117 | `TransformationAffectTest` |
| `AsSeenBy.java` | 151 | `AbstractTransformableTest` |
| `ASGEncoder.java` | 442 | `ASGRoundtripTest` |
| `ASGDecoder.java` | 310 | `ASGRoundtripTest` |
| `ASG.java` | 114 | `ASGRoundtripTest` |

---

## Design Constraints

1. **No OpenGL** — All tests are pure model/logic tests. No rendering pipeline.
2. **JUnit 4** — Using `@Test`, `org.junit.Assert.*`. No JUnit 5 annotations.
3. **No test base class** — Each test class is self-contained with private helpers.
4. **Epsilon = 0.000001** — Consistent with existing `ScenegraphModelTest`.
5. **Same-package tests** — `ASGRoundtripTest` lives in `scenegraph.io` package to
   access package-private `ASGEncoder`/`ASGDecoder` methods if needed (roundtrip
   primarily uses the `ASG` public facade).
6. **No mocking** — Tests use real scenegraph objects. No Mockito or similar.

---

## Key Class Hierarchy

Understanding the inheritance is essential for writing correct tests:

```
Element
  └─ Component (ReferenceFrame, Visitable)
       └─ Composite (children, accept/Visitor, addComponent, newCopy)
            ├─ Scene (getRoot() → this)
            └─ AbstractTransformable (transforms, applyRotation, AsSeenBy)
                 └─ Transformable (localTransformation InstanceProperty)
                      └─ Scalable
  └─ Geometry (bounds, BoundListener, caching)
       └─ Shape
            ├─ Cylinder
            ├─ Sphere
            └─ Torus
```

**Key:** `Transformable` extends `Composite`, so it CAN have children
(via `addComponent()`). This is why `Transformable` nodes form tree hierarchies.

**Listener lifecycle:**
- `setLocalTransformation()` does NOT fire `AbsoluteTransformationListener`.
  Only `notifyTransformationListeners()` or `setParent()` / `addComponent()`
  trigger the `fireAbsoluteTransformationChange()` cascade.
- `AbstractReleasable.release()` is a no-op unless at least one `ReleaseListener`
  is registered. Tests that verify release cascades MUST register listeners.
