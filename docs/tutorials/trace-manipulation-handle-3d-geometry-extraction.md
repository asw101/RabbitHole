# Tutorial: Trace the ManipulationHandle3D Geometry Extraction

This tutorial walks through the extraction of handle geometry utilities,
interruptible animation classes, and a named pick criterion from
`ManipulationHandle3D` (issue #663). You will trace each design decision —
why geometry methods became a static utility, why animation types became
public top-level classes, and why the pick criterion got a named class.

For the full contract, see the [ManipulationHandle3D Geometry Extraction
reference](../reference/manipulation-handle-3d-geometry-extraction.md).

For validation steps, see the [Validation how-to](../howto/validate-manipulation-handle-3d-geometry-extraction.md).

## Contents

- [Goal](#goal)
- [1. Understand the pre-extraction structure](#1-understand-the-pre-extraction-structure)
- [2. Trace the HandleGeometryHelper extraction](#2-trace-the-handlegeometryhelper-extraction)
- [3. Trace the animation class extractions](#3-trace-the-animation-class-extractions)
- [4. Trace the Not3dHandleCriterion extraction](#4-trace-the-not3dhandlecriterion-extraction)
- [5. Understand the inlined methods](#5-understand-the-inlined-methods)
- [6. Understand why listeners stay inline](#6-understand-why-listeners-stay-inline)
- [7. Run the validation](#7-run-the-validation)

## Goal

After this tutorial you will be able to explain:

- Why 6 geometry methods became static utilities instead of remaining
  instance methods
- Why `DoubleInterruptibleAnimation` is public while
  `Color4fInterruptibleAnimation` is package-private
- Why `NOT_3D_HANDLE_CRITERION` keeps its constant on ManipulationHandle3D
  while the implementation moves to a named class
- Why `getScalable()` and `invertParentScale()` were inlined rather than
  just delegated
- Why the two anonymous listener fields remain as inner declarations

## 1. Understand the pre-extraction structure

Open `ManipulationHandle3D.java` and identify the four extraction regions:

```
Lines  89–106:  NOT_3D_HANDLE_CRITERION (anonymous Criterion<Component>)
Lines 108–145:  Color4fInterruptibleAnimation (protected static abstract inner class)
Lines 147–185:  DoubleInterruptibleAnimation (protected static abstract inner class)
Lines 210–218:  getScalable() (private helper)
Lines 300–312:  invertParentScale() (private helper)
Lines 451–462:  calculateCameraRelativeOpacity() (public, pure function)
Lines 483–495:  getTransformationForAxis() (public, pure function)
Lines 536–559:  getObjectScale() (protected, pure function)
Lines 576–583:  getManipulatedObjectBox() (protected, pure function)
```

**Key insight:** The inner classes are `protected static` — they do not
reference the enclosing instance. The geometry methods are pure functions
that take their inputs as parameters (or could be refactored to do so).
Both characteristics make them ideal extraction candidates.

## 2. Trace the HandleGeometryHelper extraction

Six methods move to a package-private `final class HandleGeometryHelper`
with all-static methods:

| Method | Original signature | Static signature |
| --- | --- | --- |
| `getTransformationForAxis` | `public AffineMatrix4x4 getTransformationForAxis(Vector3 axis)` | `static AffineMatrix4x4 getTransformationForAxis(Vector3 axis)` |
| `getManipulatedObjectBox` | `protected AxisAlignedBox getManipulatedObjectBox()` | `static AxisAlignedBox getManipulatedObjectBox(AbstractTransformable object)` |
| `getObjectScale` | `protected double getObjectScale()` | `static double getObjectScale(AbstractTransformable object, AxisAlignedBox bbox)` |
| `calculateCameraRelativeOpacity` | `public float calculateCameraRelativeOpacity(Point3 cameraPosition)` | `static float calculateCameraRelativeOpacity(AbstractTransformable parentTransformable, Point3 cameraPosition)` |
| `invertParentScale` | `private void invertParentScale(Composite parent)` | `static void invertParentScale(Transformable target, Composite parent)` |
| `getScalable` | `private Scalable getScalable(AbstractTransformable object)` | `static Scalable getScalable(AbstractTransformable object)` |

**Why static?** Every method is a pure function. None reads or writes
instance fields — they only operate on their explicit parameters. Making
them static documents this contract: no hidden dependencies, no mutation
of enclosing state.

**Why one utility class?** These 6 methods share a single concern: spatial
math for 3D manipulation handles. A developer fixing an axis-transform bug
should see the bounding-box and scale logic nearby, because they are
coupled through the handle positioning pipeline.

**Why package-private?** `HandleGeometryHelper` is an implementation detail
of the `handle` package. No code outside this package should call these
methods directly — they are called via `ManipulationHandle3D`'s remaining
instance methods that delegate to the helper.

## 3. Trace the animation class extractions

Both animation inner classes follow the same pattern — they extend a
framework animation class and add interrupt/cancel protocol:

```java
// Shared pattern (both classes):
private boolean doEpilogue = true;
private boolean isActive = true;
private T target;  // Color4f or double

public void cancel() {
  this.doEpilogue = false;
  this.complete(null);
  this.doEpilogue = true;
}
```

**Why separate files?** Each animation class stands alone. They share no
state, no inheritance relationship, and no mutual dependency. One file per
class is the natural decomposition.

**Why `DoubleInterruptibleAnimation` is `public`:** Subclasses like
`RotationRingHandle` declare fields of type `DoubleInterruptibleAnimation`.
These subclasses are in the same package, so package-private would suffice
at runtime. However, the original was `protected static` (visible to
subclasses including potential out-of-package ones), and the field
declarations in subclasses use the unqualified type name. Making it
`public` preserves the widest original access.

**Why `Color4fInterruptibleAnimation` stays `protected` (effective
package-private):** This type is only referenced as a field type within
`ManipulationHandle3D` itself (line 654). No subclass declares a field of
this type. The narrower visibility is appropriate.

**No behavioral change:** The `cancel()` → `complete(null)` → `epilogue()`
chain works identically whether the class is inner or top-level.

## 4. Trace the Not3dHandleCriterion extraction

The original was an anonymous class with a recursive helper:

```java
public static final Criterion<Component> NOT_3D_HANDLE_CRITERION =
    new Criterion<Component>() {
      protected boolean isHandle(Component c) { ... }

      @Override
      public boolean accept(Component c) {
        return !isHandle(c);
      }
    };
```

**Problem with anonymous classes:** The `isHandle()` method is declared
on the anonymous class but cannot be invoked by external code (no type
to cast to). This works but prevents testing the helper in isolation.

**Solution:** Extract to a named class, keep the constant as a one-liner:

```java
// ManipulationHandle3D.java:
public static final Criterion<Component> NOT_3D_HANDLE_CRITERION =
    new Not3dHandleCriterion();
```

All 27 usage sites reference the constant by its original qualified name
(`ManipulationHandle3D.NOT_3D_HANDLE_CRITERION`). None casts or
reflects on the anonymous type. The change is transparent.

**Why package-private?** No code outside the `handle` package needs to
construct a `Not3dHandleCriterion` directly. The constant is the API.

## 5. Understand the inlined methods

Two methods were eliminated entirely rather than delegated:

### `getScalable()` (9 lines → 0)

```java
// BEFORE: private method on ManipulationHandle3D
private Scalable getScalable(AbstractTransformable object) {
  if (object instanceof Scalable scalable) { return scalable; }
  if (object != null) { return object.getBonusDataFor(Scalable.KEY); }
  return null;
}
```

This is called at exactly 2 sites within `setManipulatedObject()`. After
extraction, both sites call `HandleGeometryHelper.getScalable(object)`
directly. No wrapper method remains on `ManipulationHandle3D`.

### `invertParentScale()` (13 lines → 0)

```java
// BEFORE: private method reading instance fields
private void invertParentScale(Composite parent) {
  OrthogonalMatrix3x3 local = localTransformation.getValue().orientation().normalized();
  // ... modifies localTransformation
}
```

Called at exactly 1 site (`setParent()`). After extraction, the call site
becomes:

```java
HandleGeometryHelper.invertParentScale(this, parent);
```

The static method receives the `Transformable` explicitly instead of
reading `localTransformation` implicitly.

**Why inline instead of delegate?** For methods with a single call site,
a one-line delegation wrapper `private void invertParentScale(Composite p)
{ HandleGeometryHelper.invertParentScale(this, p); }` wastes a line for
zero readability benefit. The direct call at the use site is equally clear.

## 6. Understand why listeners stay inline

Two anonymous listener instances remain as field initializers:

```java
private final PropertyListener scaleListener = e -> {
  ManipulationHandle3D.this.setScale(ManipulationHandle3D.this.getObjectScale());
  ManipulationHandle3D.this.resizeToObject();
  ManipulationHandle3D.this.positionRelativeToObject();
};

private final AbsoluteTransformationListener absoluteTransformationListener =
    new AbsoluteTransformationListener() {
      @Override
      public void absoluteTransformationChanged(...) {
        ManipulationHandle3D.this.updateCameraRelativeOpacity();
      }
    };
```

Both capture `ManipulationHandle3D.this` and call instance methods.
Extracting them would require:

1. A constructor parameter for the enclosing handle
2. Storing it as a field
3. Net zero line savings (added field + constructor offset the extraction)

They stay.

**Note:** After extraction, the `scaleListener` lambda changes slightly
because `getObjectScale()` is no longer a method on `this`:

```java
private final PropertyListener scaleListener = e -> {
  AxisAlignedBox bbox = HandleGeometryHelper.getManipulatedObjectBox(this.manipulatedObject);
  this.setScale(HandleGeometryHelper.getObjectScale(this.manipulatedObject, bbox));
  this.resizeToObject();
  this.positionRelativeToObject();
};
```

This is a delegation change, not a behavioral change.

## 7. Run the validation

Follow the [Validation how-to](../howto/validate-manipulation-handle-3d-geometry-extraction.md)
to verify compilation, line count, and test passage.

The key checks:

```bash
# Compile
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am -DfailIfNoTests=false -Dcheckstyle.skip compile

# Line count (must be under 500)
wc -l core/story-api/src/main/java/org/alice/interact/handle/ManipulationHandle3D.java

# Verify new files exist
ls -la core/story-api/src/main/java/org/alice/interact/handle/{HandleGeometryHelper,Not3dHandleCriterion,DoubleInterruptibleAnimation,Color4fInterruptibleAnimation}.java

# Verify no subclass changes
git diff --name-only | grep -v ManipulationHandle3D
# Expected: only the 4 new files, no other .java changes

# Tests pass
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am -DfailIfNoTests=false test
```
