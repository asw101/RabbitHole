# TransformAnimator Inner Class Extraction

This reference documents the extraction of inner classes from
`TransformAnimator` (issue #639) into three top-level package-private files in
`org.lgna.story.implementation`. The extraction reduces `TransformAnimator.java`
from 616 lines to ~368 lines (well under the 500-line target).

## Contents

- [Motivation](#motivation)
- [Extracted classes](#extracted-classes)
- [File inventory](#file-inventory)
- [Visibility changes](#visibility-changes)
- [Field access fix](#field-access-fix)
- [Classes that stay inline](#classes-that-stay-inline)
- [Validation commands](#validation-commands)
- [Compatibility rules](#compatibility-rules)
- [Examples](#examples)

## Motivation

`TransformAnimator.java` contained 616 lines including a 6-class orientation
data hierarchy, two Hermite-interpolation animation classes, and a placement
animation class — all as `private static` inner classes. These inner classes
are self-contained data holders and animation implementations with no dependency
on the enclosing `TransformAnimator` instance. Extracting them into separate
files reduces cognitive load and brings the file under the 500-line target.

## Extracted classes

Three new files contain the extracted classes:

| New file | Classes | Lines saved | Purpose |
| --- | --- | --- | --- |
| `OrientationData.java` | `OrientationData`, `PreSetOrientationData`, `LocalOrientationData`, `TurnToFaceOrientationData`, `OrientToUprightData`, `OrientToPointAtData` | ~144 | Quaternion-interpolated orientation target hierarchy for animate-orientation methods |
| `SmoothPositionAnimations.java` | `SmoothAffineMatrix4x4Animation`, `SmoothPositionAnimation` | ~54 | Hermite-cubic smooth position animations co-located for package-private field access |
| `PlaceAnimation.java` | `PlaceAnimation` | ~41 | Spatial placement animation using `TransformOperations.PlaceData` |

**Total: ~239 lines removed from TransformAnimator.java.**

## File inventory

After extraction, these files exist in
`core/story-api/src/main/java/org/lgna/story/implementation/`:

| File | Lines | Description |
| --- | --- | --- |
| `TransformAnimator.java` | ~368 | Animate methods, anonymous inline animations, delegation to extracted classes |
| `OrientationData.java` | ~201 | 6-class hierarchy: 2 abstract bases → 3 concrete + 1 sub-concrete (`TurnToFace` extends `Local`) |
| `SmoothPositionAnimations.java` | ~116 | Parent + child animation classes sharing package-private fields |
| `PlaceAnimation.java` | ~97 | Self-contained placement animation |

All new files carry the CMU BSD copyright header matching the original.

## Visibility changes

| Element | Before | After | Reason |
| --- | --- | --- | --- |
| `OrientationData` (class) | `private static` inner | package-private top-level | Used by `TransformAnimator` in same package |
| `PreSetOrientationData` (class) | `private static` inner | package-private top-level | Subclassed by sibling classes in same file |
| `LocalOrientationData` (class) | `private static` inner | package-private top-level | Instantiated by `TransformAnimator` |
| `TurnToFaceOrientationData` (class) | `private static` inner | package-private top-level | Instantiated by `TransformAnimator` |
| `OrientToUprightData` (class) | `private static` inner | package-private top-level | Instantiated by `TransformAnimator` |
| `OrientToPointAtData` (class) | `private static` inner | package-private top-level | Instantiated by `TransformAnimator` |
| `SmoothAffineMatrix4x4Animation` (class) | `private static` inner | package-private top-level | Extended by `SmoothPositionAnimation` in same file |
| `SmoothPositionAnimation` (class) | `private static` inner | package-private top-level | Instantiated by `TransformAnimator` |
| `PlaceAnimation` (class) | `private static` inner | package-private top-level | Instantiated by `TransformAnimator` |
| `OrientationData.subject` (field) | `private` | `private` (unchanged) | Accessed via existing `public getSubject()` getter |

**No field visibility was widened. No public API surface changed.**

## Field access fix

In `TransformAnimator.animateOrientationOnly()`, the anonymous
`DurationBasedAnimation` subclass previously accessed `data.subject` directly
via Java's inner-class enclosing access:

```java
// BEFORE (inner class could access private field of enclosing class):
@Override
public Animated getAnimated() {
  return data.subject;  // direct private field access
}
```

After extraction, `OrientationData` is no longer an inner class, so the
private field is inaccessible. The fix uses the existing public getter:

```java
// AFTER:
@Override
public Animated getAnimated() {
  return data.getSubject();  // uses existing public getter
}
```

This is the only cross-class access change in the extraction.

## Classes that stay inline

Two anonymous inner classes remain in `TransformAnimator` by design:

| Class | Location | Reason |
| --- | --- | --- |
| `TranslateAnimation` | `animateApplyTranslation()` | Captures `owner` via closure; extracting would require constructor parameter for marginal savings |
| `RotateAnimation` | `animateApplyRotationInRadians()` | Captures `owner` via closure; same rationale |

These are local classes (declared inside method bodies) that capture the
enclosing `owner` field. Extracting them would require passing the owner
as a constructor parameter, adding complexity for negligible line reduction.

## Validation commands

All commands assume the repository root as working directory and the
`tweedle-lang` submodule initialized.

### Compilation

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am -DfailIfNoTests=false -Dcheckstyle.skip compile
```

### Line count verification

```bash
wc -l core/story-api/src/main/java/org/lgna/story/implementation/TransformAnimator.java
# Target: under 500 lines (~368 expected)
```

### Test execution

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test
```

### Verify new files exist

```bash
ls -la core/story-api/src/main/java/org/lgna/story/implementation/{OrientationData,SmoothPositionAnimations,PlaceAnimation}.java
```

## Compatibility rules

1. **No public API change.** `TransformAnimator` is package-private. All
   extracted classes are package-private. No downstream code references
   these classes by name.

2. **Same package.** All extracted files remain in
   `org.lgna.story.implementation`. Package-private access is preserved.

3. **No reflection dependencies.** No Alice 3 code uses reflection to
   access these inner classes by name.

4. **No serialization impact.** None of the extracted classes implement
   `Serializable`.

5. **Co-location rule.** `SmoothPositionAnimation` extends
   `SmoothAffineMatrix4x4Animation` and accesses its package-private fields
   (`m1`, `xHermite`, `yHermite`, `zHermite`). Both must remain in
   `SmoothPositionAnimations.java`.

## Examples

### Using OrientationData (from TransformAnimator)

```java
// TransformAnimator creates orientation data and delegates animation:
void animateLocalOrientationOnly(OrthogonalMatrix3x3 localOrientation, double duration, Style style) {
  animateOrientationOnly(new LocalOrientationData(owner, localOrientation), duration, style);
}

void animateOrientationOnlyToFace(EntityImp target, Point3 offset, double duration, Style style) {
  animateOrientationOnly(new TurnToFaceOrientationData(owner, target), duration, style);
}
```

### SmoothPositionAnimation instantiation

```java
// Hermite-smooth position animation via extracted class:
owner.perform(new SmoothPositionAnimation(owner, AffineMatrix4x4.IDENTITY, target, duration, style));
```

### PlaceAnimation instantiation

```java
// Spatial placement animation via extracted class:
owner.perform(new PlaceAnimation(placeData, duration, style));
```
