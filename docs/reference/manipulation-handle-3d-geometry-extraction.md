# ManipulationHandle3D Geometry and Interaction Delegate Extraction

This reference documents the extraction of handle geometry utilities,
interaction animation classes, and a named criterion from
`ManipulationHandle3D` (issue #663) into four top-level files in
`org.alice.interact.handle`. The extraction reduces
`ManipulationHandle3D.java` from 664 lines to ~498 lines (under the
500-line target).

## Contents

- [Motivation](#motivation)
- [Extracted components](#extracted-components)
- [File inventory](#file-inventory)
- [Visibility changes](#visibility-changes)
- [Inlined methods](#inlined-methods)
- [NOT\_3D\_HANDLE\_CRITERION compatibility](#not_3d_handle_criterion-compatibility)
- [Classes that stay inline](#classes-that-stay-inline)
- [Validation commands](#validation-commands)
- [Compatibility rules](#compatibility-rules)
- [Examples](#examples)

## Motivation

`ManipulationHandle3D.java` contained 664 lines including two abstract
interruptible-animation inner classes (`DoubleInterruptibleAnimation` and
`Color4fInterruptibleAnimation`), an anonymous `Criterion<Component>` inner
class, and several geometry-computation methods that are pure functions of
their arguments. These concerns are independent of the handle's core
lifecycle (visibility state, event matching, parent/child management) and
inflate the file well past the 500-line modernization target.

The extraction decomposes ManipulationHandle3D along three axes:

1. **Handle geometry** — axis transforms, bounding-box retrieval, object-scale
   computation, parent-scale inversion, camera-relative opacity, and scalable
   lookup — into a static utility class.
2. **Animation types** — the two interruptible-animation abstract classes that
   subclasses reference as field types — into dedicated top-level files.
3. **Pick criterion** — the anonymous `NOT_3D_HANDLE_CRITERION` class — into a
   named top-level class with a backward-compatible alias.

## Extracted components

Four new files contain the extracted code:

| New file | What moved | Lines saved | Purpose |
| --- | --- | --- | --- |
| `HandleGeometryHelper.java` | `getTransformationForAxis()`, `getManipulatedObjectBox()`, `getObjectScale()`, `calculateCameraRelativeOpacity()`, `invertParentScale()`, `getScalable()` | ~70 | Package-private static utility for handle spatial math |
| `Not3dHandleCriterion.java` | Anonymous `Criterion<Component>` from `NOT_3D_HANDLE_CRITERION` | ~17 | Named criterion class replacing anonymous inner class |
| `DoubleInterruptibleAnimation.java` | `DoubleInterruptibleAnimation` inner class | ~38 | Public abstract animation with cancel/interrupt support |
| `Color4fInterruptibleAnimation.java` | `Color4fInterruptibleAnimation` inner class | ~38 | Public abstract animation with cancel/interrupt support |

**Total: ~166 lines removed from ManipulationHandle3D.java.**

## File inventory

After extraction, these files exist in
`core/story-api/src/main/java/org/alice/interact/handle/`:

| File | Lines (approx) | Description |
| --- | --- | --- |
| `ManipulationHandle3D.java` | ~498 | Handle lifecycle, state, events, visual management; delegates to `HandleGeometryHelper` |
| `HandleGeometryHelper.java` | ~110 | 6 static methods: axis transforms, bounding boxes, scale, opacity, parent-scale inversion, scalable lookup |
| `Not3dHandleCriterion.java` | ~55 | `Criterion<Component>` implementation that rejects 3D handle components by walking the scene graph |
| `DoubleInterruptibleAnimation.java` | ~75 | Abstract `DoubleAnimation` subclass with interrupt/cancel protocol |
| `Color4fInterruptibleAnimation.java` | ~75 | Abstract `Color4fAnimation` subclass with interrupt/cancel protocol |

All new files carry the CMU BSD copyright header matching the original.

## Visibility changes

| Element | Before | After | Reason |
| --- | --- | --- | --- |
| `DoubleInterruptibleAnimation` (class) | `protected static` inner | `public abstract` top-level | Subclasses in same package declare fields of this type; `public` matches the field accessibility pattern used by `RotationRingHandle` and siblings |
| `Color4fInterruptibleAnimation` (class) | `protected static` inner | `protected abstract` top-level (package-private effective) | Only referenced within the `handle` package |
| `NOT_3D_HANDLE_CRITERION` (constant) | `public static final` anonymous class | `public static final` delegating to `Not3dHandleCriterion` | One-liner alias preserves binary compatibility |
| `Not3dHandleCriterion` (class) | N/A (anonymous) | package-private | No external consumers; all 27 usage sites reference the `NOT_3D_HANDLE_CRITERION` constant |
| `HandleGeometryHelper` (class) | N/A | `final` package-private | Pure utility; no reason to expose outside `handle` package |
| `getScalable()` (method) | `private` on `ManipulationHandle3D` | `static` on `HandleGeometryHelper` | Pure function of its argument; no instance state needed |
| `invertParentScale()` (method) | `private` on `ManipulationHandle3D` | `static` on `HandleGeometryHelper` | Reads only from the passed `Transformable` and `Composite` arguments |
| `getObjectScale()` (method) | `protected` on `ManipulationHandle3D` | `static` on `HandleGeometryHelper` | Pure function of object and bounding box |
| `getManipulatedObjectBox()` (method) | `protected` on `ManipulationHandle3D` | `static` on `HandleGeometryHelper` | Pure function of `AbstractTransformable` |
| `getTransformationForAxis()` (method) | `public` on `ManipulationHandle3D` | `static` on `HandleGeometryHelper` | Pure function of `Vector3` axis; no instance state |
| `calculateCameraRelativeOpacity()` (method) | `public` on `ManipulationHandle3D` | `static` on `HandleGeometryHelper` | Pure function of parent position and camera position |

**No existing public API constant or type name was removed.** The
`NOT_3D_HANDLE_CRITERION` constant remains on `ManipulationHandle3D` at its
original fully-qualified name.

## Inlined methods

Two private methods were eliminated by inlining their logic as calls to
`HandleGeometryHelper`:

| Removed method | Lines saved | Replacement |
| --- | --- | --- |
| `getScalable(AbstractTransformable)` | 9 | Call sites use `HandleGeometryHelper.getScalable(object)` directly |
| `invertParentScale(Composite)` | 13 | `setParent()` calls `HandleGeometryHelper.invertParentScale(this, parent)` |

## NOT\_3D\_HANDLE\_CRITERION compatibility

The original `NOT_3D_HANDLE_CRITERION` was defined as an anonymous
`Criterion<Component>` with a recursive `isHandle()` helper. After
extraction:

```java
// ManipulationHandle3D.java — backward-compatible alias
public static final Criterion<Component> NOT_3D_HANDLE_CRITERION =
    new Not3dHandleCriterion();
```

```java
// Not3dHandleCriterion.java — named class (package-private)
final class Not3dHandleCriterion implements Criterion<Component> {

  boolean isHandle(Component c) {
    if (c == null) { return false; }
    Object bonusData = c.getBonusDataFor(PickHint.PICK_HINT_KEY);
    if ((bonusData instanceof PickHint hint)
        && hint.intersects(PickHint.PickType.THREE_D_HANDLE.pickHint())) {
      return true;
    }
    return isHandle(c.getParent());
  }

  @Override
  public boolean accept(Component c) {
    return !isHandle(c);
  }
}
```

All 27 external reference sites use `ManipulationHandle3D.NOT_3D_HANDLE_CRITERION`
and require zero changes.

## Classes that stay inline

Two anonymous listener classes remain in `ManipulationHandle3D` by design:

| Class | Field | Reason |
| --- | --- | --- |
| `PropertyListener` (scale) | `scaleListener` | Captures `this` (enclosing `ManipulationHandle3D`) to call `setScale()`, `resizeToObject()`, `positionRelativeToObject()` |
| `AbsoluteTransformationListener` | `absoluteTransformationListener` | Captures `this` to call `updateCameraRelativeOpacity()` |

Both are tiny lambda-style listeners (3–5 lines each) that depend on the
enclosing handle instance. Extracting them would require passing the handle
as a constructor parameter for negligible line savings.

## Validation commands

All commands assume the repository root as working directory and the
`tweedle-lang` submodule initialized (`git submodule update --init tweedle-lang`).

### Compilation

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am -DfailIfNoTests=false -Dcheckstyle.skip compile
```

### Line count verification

```bash
wc -l core/story-api/src/main/java/org/alice/interact/handle/ManipulationHandle3D.java
# Target: under 500 lines (~498 expected)
```

### Verify new files exist

```bash
ls -la core/story-api/src/main/java/org/alice/interact/handle/{HandleGeometryHelper,Not3dHandleCriterion,DoubleInterruptibleAnimation,Color4fInterruptibleAnimation}.java
```

### Test execution

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test
```

## Compatibility rules

1. **No public API removal.** `ManipulationHandle3D.NOT_3D_HANDLE_CRITERION`
   retains its original type and fully-qualified name. External code that
   references it compiles without changes.

2. **Same package.** All extracted files remain in
   `org.alice.interact.handle`. Package-private access is preserved between
   `ManipulationHandle3D` and its helpers.

3. **No subclass changes.** The 8 subclasses (`RotationRingHandle`,
   `LinearTranslateHandle`, `LinearScaleHandle`, `LinearDragHandle`,
   `StoodUpRotationRingHandle`, `JointRotationRingHandle`, etc.) do not
   override any extracted method. They reference the animation types only as
   field declarations (lines 653–654 in the original), which now resolve to
   the same-package top-level classes.

4. **No reflection dependencies.** No Alice 3 code uses reflection to
   access the extracted inner classes by their inner-class names.

5. **No serialization impact.** None of the extracted classes implement
   `Serializable`.

6. **Null-safety preserved.** `HandleGeometryHelper.getObjectScale()` and
   `HandleGeometryHelper.getManipulatedObjectBox()` retain the original null
   checks. Callers must pass a non-null `AbstractTransformable` or receive
   the documented default (`1.0d` for scale, a unit box for bounding box).

## Examples

### HandleGeometryHelper — axis transformation

```java
// Before (instance method on ManipulationHandle3D):
AffineMatrix4x4 transform = this.getTransformationForAxis(Vector3.POSITIVE_Y_AXIS);

// After (static utility):
AffineMatrix4x4 transform = HandleGeometryHelper.getTransformationForAxis(Vector3.POSITIVE_Y_AXIS);
```

### HandleGeometryHelper — object scale in setManipulatedObject

```java
// Before:
this.setScale(this.getObjectScale());

// After:
AxisAlignedBox bbox = HandleGeometryHelper.getManipulatedObjectBox(this.manipulatedObject);
this.setScale(HandleGeometryHelper.getObjectScale(this.manipulatedObject, bbox));
```

### HandleGeometryHelper — parent scale inversion in setParent

```java
// Before:
@Override
public void setParent(Composite parent) {
  super.setParent(parent);
  invertParentScale(parent);
  this.updateCameraRelativeOpacity();
}

// After:
@Override
public void setParent(Composite parent) {
  super.setParent(parent);
  HandleGeometryHelper.invertParentScale(this, parent);
  this.updateCameraRelativeOpacity();
}
```

### HandleGeometryHelper — camera-relative opacity

```java
// Before:
public float calculateCameraRelativeOpacity(Point3 cameraPosition) { ... }

// After (static, takes parent transformable explicitly):
float opacity = HandleGeometryHelper.calculateCameraRelativeOpacity(
    this.getParentTransformable(), cameraPosition);
```

### DoubleInterruptibleAnimation — subclass field declaration

```java
// Before (inner class reference):
private ManipulationHandle3D.DoubleInterruptibleAnimation opacityAnimation;

// After (top-level class, same package — no qualifier needed):
private DoubleInterruptibleAnimation opacityAnimation;
```

### Not3dHandleCriterion — transparent to consumers

```java
// All existing call sites remain unchanged:
Transformable picked = scene.findFirstAccepting(
    ManipulationHandle3D.NOT_3D_HANDLE_CRITERION, ray);
// Compiles identically. The constant now delegates to Not3dHandleCriterion
// instead of holding an anonymous class.
```
