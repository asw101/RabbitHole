# Tutorial: Trace the TransformAnimator Inner Class Extraction

This tutorial walks through the extraction of inner classes from
`TransformAnimator` (issue #639). You will trace each design decision —
why the orientation hierarchy is a single file, why two animation classes
must share a file, and why some inner classes stay inline.

For the full contract, see the [TransformAnimator Inner Class Extraction
reference](../reference/transform-animator-inner-class-extraction.md).

For validation steps, see the [Validation how-to](../howto/validate-transform-animator-inner-class-extraction.md).

## Contents

- [Goal](#goal)
- [1. Understand the pre-extraction structure](#1-understand-the-pre-extraction-structure)
- [2. Trace the OrientationData extraction](#2-trace-the-orientationdata-extraction)
- [3. Trace the SmoothPositionAnimations extraction](#3-trace-the-smoothpositionanimations-extraction)
- [4. Trace the PlaceAnimation extraction](#4-trace-the-placeanimation-extraction)
- [5. Trace the data.subject field access fix](#5-trace-the-datasubject-field-access-fix)
- [6. Understand why TranslateAnimation and RotateAnimation stay](#6-understand-why-translateanimation-and-rotateanimation-stay)
- [7. Run the validation](#7-run-the-validation)

## Goal

After this tutorial you will be able to explain:

- Why 6 orientation data classes live in one file instead of 6 separate files
- Why `SmoothPositionAnimation` and `SmoothAffineMatrix4x4Animation` must
  be co-located in a single file
- Why `data.subject` changed to `data.getSubject()` and how this is the
  only semantic change in the extraction
- Why `TranslateAnimation` and `RotateAnimation` were not extracted
- How package-private visibility replaces inner-class private access

## 1. Understand the pre-extraction structure

Open `TransformAnimator.java` and identify the three groups of inner classes:

```
Lines 202–345:  OrientationData hierarchy (6 classes)
Lines 433–486:  SmoothAffineMatrix4x4Animation + SmoothPositionAnimation
Lines 520–560:  PlaceAnimation
```

All are `private static` inner classes. They do not reference the enclosing
`TransformAnimator` instance — they receive their dependencies via
constructors. This makes them ideal extraction candidates.

**Key insight:** The `private static` modifier means these classes use no
enclosing instance. The only reason they were inner classes was locality of
declaration, not a structural dependency on the enclosing class.

## 2. Trace the OrientationData extraction

The 6-class hierarchy forms a template method pattern:

```
OrientationData (abstract)
├── PreSetOrientationData (abstract, adds m0/m1 caching)
│   ├── LocalOrientationData (sets local transform)
│   │   └── TurnToFaceOrientationData (computes facing axes via VehicleManager)
│   ├── OrientToUprightData (sets axes relative to reference frame)
│   └── OrientToPointAtData (sets axes toward target point)
```

**Why one file?** These 6 classes form a cohesive hierarchy. Spreading them
across 6 files would scatter a single concept. The hierarchy is the unit of
understanding — a developer modifying orientation animation needs all 6
classes together.

**Visibility change:** Each class moves from `private static` (inner) to
package-private (top-level). The `subject` field stays `private` on
`OrientationData` — the existing `public getSubject()` getter provides
access. No field visibility is widened.

## 3. Trace the SmoothPositionAnimations extraction

`SmoothAffineMatrix4x4Animation` is the parent class:

```java
abstract static class SmoothAffineMatrix4x4Animation extends DurationBasedAnimation {
  final AffineMatrix4x4 m1;
  final HermiteCubic xHermite;
  final HermiteCubic yHermite;
  final HermiteCubic zHermite;
  // ...
}
```

`SmoothPositionAnimation` extends it and accesses the parent's fields
directly:

```java
static class SmoothPositionAnimation extends SmoothAffineMatrix4x4Animation {
  protected void setPortion(double portion) {
    double x = this.xHermite.evaluate(portion);  // parent field
    double y = this.yHermite.evaluate(portion);  // parent field
    double z = this.zHermite.evaluate(portion);  // parent field
    // ...
  }

  protected void epilogue() {
    this.subject.getSgComposite().setTranslationOnly(this.m1.translation(), ...);  // parent field
  }
}
```

**Why co-located?** The fields `m1`, `xHermite`, `yHermite`, `zHermite` have
package-private visibility (no modifier = package-private). When both classes
were inner classes, the child could access the parent's fields. After
extraction, they must be in the **same package** to maintain access — and
placing them in the same file signals their tight coupling.

If a future refactoring adds another `SmoothAffineMatrix4x4Animation`
subclass (e.g., `SmoothOrientationAnimation`), it belongs in this same file.

## 4. Trace the PlaceAnimation extraction

`PlaceAnimation` is self-contained:

```java
static class PlaceAnimation extends DurationBasedAnimation {
  private final TransformOperations.PlaceData placeData;
  // ...
}
```

It depends on `TransformOperations.PlaceData`, a nested static class inside
`TransformOperations` (same package). The `placeData.subject` field is
package-private and was already cross-class access (not inner-class enclosing
access), so no fix is needed after extraction.

**Why its own file?** It has no inheritance relationship with the other
extracted classes and no shared state. A single-class file is the cleanest
option.

## 5. Trace the data.subject field access fix

This is the only semantic change in the extraction. In
`TransformAnimator.animateOrientationOnly()`, an anonymous
`DurationBasedAnimation` subclass accesses the orientation data:

```java
// BEFORE: inner-class enclosing access to private field
owner.perform(new DurationBasedAnimation(duration, style) {
  @Override
  public Animated getAnimated() {
    return data.subject;  // ← private field, accessible because
                          //   OrientationData was an inner class
  }
  // ...
});
```

After extracting `OrientationData` to a top-level class, `data.subject` is
no longer accessible from `TransformAnimator` because `subject` is `private`.

The fix uses the existing `public getSubject()` getter:

```java
// AFTER: uses public getter
return data.getSubject();
```

**Why not widen the field?** Making `subject` package-private would work but
breaks the encapsulation principle. The getter already exists and is the
intended API. Using it is the correct fix.

## 6. Understand why TranslateAnimation and RotateAnimation stay

Two local classes remain inside `TransformAnimator` methods:

```java
void animateApplyTranslation(...) {
  // ...
  class TranslateAnimation extends DurationBasedAnimation {
    // captures `owner` from enclosing method scope
  }
}
```

These are **local classes** (declared inside method bodies), not static inner
classes. They capture `owner` from the enclosing scope via closure. Extracting
them would require:

1. Adding an `AbstractTransformableImp owner` constructor parameter
2. Storing it as a field
3. Zero net line reduction (the constructor and field add lines back)

The cost exceeds the benefit. They stay.

## 7. Run the validation

Follow the [Validation how-to](../howto/validate-transform-animator-inner-class-extraction.md)
to verify compilation, line count, and test passage.

The key checks:

```bash
# Compile
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am -DfailIfNoTests=false -Dcheckstyle.skip compile

# Line count (must be under 500)
wc -l core/story-api/src/main/java/org/lgna/story/implementation/TransformAnimator.java

# No direct field access remains
grep -n 'data\.subject' \
  core/story-api/src/main/java/org/lgna/story/implementation/TransformAnimator.java
# Expected: 0 matches

# Tests pass
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am -DfailIfNoTests=false test
```
