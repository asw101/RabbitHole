# AbstractTransformableImp Extraction

This reference describes the extraction of three helper classes from the 977-line
`AbstractTransformableImp` into focused companions: `TransformOperations`,
`VehicleManager`, and `TransformAnimator`. This is RabbitHole issue #581,
reducing the class to under 500 lines while preserving the full public and
protected API surface.

The extraction is a pure internal refactor. All public and protected method
signatures on `AbstractTransformableImp` remain identical. Subclass overrides
in `SymmetricPerspectiveCameraImp`, `VrUserImp`, and `JointImp` compile and
behave without change.

## Contents

- [Motivation](#motivation)
- [Architecture](#architecture)
- [Class responsibilities](#class-responsibilities)
  - [VehicleManager](#vehiclemanager)
  - [TransformOperations](#transformoperations)
  - [TransformAnimator](#transformanimator)
  - [AbstractTransformableImp changes](#abstracttransformableimp-changes)
- [Public API](#public-api)
- [Protected subclass API](#protected-subclass-api)
- [Package-private delegation facades](#package-private-delegation-facades)
- [Package-private collaboration](#package-private-collaboration)
- [Delegation pattern](#delegation-pattern)
- [Visibility rules](#visibility-rules)
- [Security boundary](#security-boundary)
- [Error handling contract](#error-handling-contract)
- [Configuration](#configuration)
- [Validation](#validation)
- [Migration guide for subclass authors](#migration-guide-for-subclass-authors)
- [Examples](#examples)
- [Acceptance criteria](#acceptance-criteria)
- [Claim boundaries](#claim-boundaries)

## Motivation

`AbstractTransformableImp.java` accumulated 977 lines mixing four distinct
concerns:

1. **Transform/position queries** — `getLocalTransformation`, `setLocalPosition`,
   `getDistanceTo`, `getDistanceAbove`, bounding-box calculations.
2. **Vehicle/parent management** — `postCheckSetVehicle`, `StandIn` pool,
   `calculateTurnToFaceAxes`.
3. **Animation/interpolation** — `animateApplyTranslation`,
   `animateApplyRotationInRadians`, `animatePositionOnly`,
   `animateTransformation`, plus inner classes `TranslateAnimation`,
   `RotateAnimation`, `SmoothPositionAnimation`, `PlaceAnimation`, and four
   `OrientationData` variants.
4. **Subclass extension points** — `VantagePointData`, `PreSetVantagePointData`,
   `animateVantagePoint`.

Extracting concerns 1–3 into focused helper classes reduces
`AbstractTransformableImp` to ~386 lines, improving readability and making each
concern independently testable.

## Architecture

```text
AbstractTransformableImp (~386 lines, delegation facade)
├── TransformAnimator (package-private, ~390 lines)
│   ├── TranslateAnimation         (local inner class)
│   ├── RotateAnimation            (local inner class)
│   ├── OrientationData hierarchy  (private static inner classes)
│   │   ├── PreSetOrientationData
│   │   ├── LocalOrientationData
│   │   ├── TurnToFaceOrientationData
│   │   ├── OrientToUprightData
│   │   └── OrientToPointAtData
│   └── animateApplyTranslation / animateApplyRotation / animateOrientation*
├── TransformOperations (package-private, ~370 lines)
│   ├── SmoothAffineMatrix4x4Animation (private static inner class)
│   ├── SmoothPositionAnimation    (private static inner class)
│   ├── PlaceData / PlaceAnimation (private static inner classes)
│   ├── place / animatePlace
│   ├── setPositionOnly / animatePositionOnly
│   ├── setTransformation / animateTransformation
│   └── getDistanceTo / getDistanceAbove / getDistanceBelow / etc.
└── VehicleManager (package-private static utility, ~104 lines)
    ├── StandIn pool (acquireStandIn / releaseStandIn)
    └── calculateTurnToFaceAxes
```

Subclass-visible extension points remain on `AbstractTransformableImp`:

```text
AbstractTransformableImp
├── VantagePointData (protected abstract static inner class)
├── PreSetVantagePointData (protected static inner class)
└── animateVantagePoint(VantagePointData, duration, style)
```

## Class responsibilities

### VehicleManager

**File:** `core/story-api/src/main/java/org/lgna/story/implementation/VehicleManager.java`
**Visibility:** Package-private (no `public` modifier on class)
**Pattern:** Static utility — no instance state, all methods are `static`

| Responsibility | Methods |
|---|---|
| StandIn object pool | `acquireStandIn(EntityImp)`, `releaseStandIn(StandInImp)` |
| Turn-to-face math | `calculateTurnToFaceAxes(AbstractTransformableImp, EntityImp)` |

The `StandIn` pool (`DefaultPool<StandInImp>`) is a `private static` field.
Both `acquireStandIn` and `releaseStandIn` are package-private static methods.

`calculateTurnToFaceAxes` computes the local orientation matrix that would make
a subject face a target, using two temporary `StandIn` objects to resolve
coordinate frames. It returns the subject's existing orientation unchanged when
the target is directly above/below (X and Z within epsilon of zero).

### TransformOperations

**File:** `core/story-api/src/main/java/org/lgna/story/implementation/TransformOperations.java`
**Visibility:** Package-private
**Pattern:** Instance helper — holds a reference to the owning
`AbstractTransformableImp`

| Responsibility | Methods |
|---|---|
| Spatial placement | `place(SpatialRelationImp, EntityImp, double, ReferenceFrame)` and overloads |
| Animated placement | `animatePlace(SpatialRelationImp, EntityImp, double, ReferenceFrame, boolean, double, Style)` |
| Position-only moves | `setPositionOnly(EntityImp, Point3)`, `setPositionOnly(EntityImp)`, `animatePositionOnly(...)` |
| Full transform | `setTransformation(ReferenceFrame, AffineMatrix4x4)`, `animateTransformation(...)` and overloads |
| Distance queries | `getDistanceTo(EntityImp)`, `getDistanceAbove(EntityImp, ReferenceFrame)`, `getDistanceBelow(...)`, `getDistanceToTheLeftOf(...)`, `getDistanceToTheRightOf(...)`, `getDistanceBehind(...)`, `getDistanceInFrontOf(...)` |

Contains the `PlaceData` and `PlaceAnimation` private inner classes,
`SmoothAffineMatrix4x4Animation` (abstract base), `SmoothPositionAnimation`,
and bounding-box helper methods `getMax`/`getMin`/`differenceToEpsilon`.

**Construction:**

```java
// Inside AbstractTransformableImp
private final TransformOperations transformOps = new TransformOperations(this);
```

### TransformAnimator

**File:** `core/story-api/src/main/java/org/lgna/story/implementation/TransformAnimator.java`
**Visibility:** Package-private
**Pattern:** Instance helper — holds a reference to the owning
`AbstractTransformableImp`

| Responsibility | Methods |
|---|---|
| Translation animation | `animateApplyTranslation(Point3, ReferenceFrame, double, Style)` and `(x,y,z,...)` overload |
| Rotation animation | `animateApplyRotationInRadians(Vector3, double, ReferenceFrame, double, Style)`, `animateApplyRotationInRevolutions(...)` |
| Orientation animation | `animateOrientationOnly(EntityImp, Orientation, double, Style)`, `animateOrientationOnlyToFace(...)`, `animateOrientationToUpright(...)`, `animateOrientationToPointAt(...)` |
| Local orientation | `setLocalOrientationOnly(OrthogonalMatrix3x3)`, `animateLocalOrientationOnly(OrthogonalMatrix3x3, double, Style)` |
| Orientation set | `setOrientationOnlyToPointAt(ReferenceFrame)` |

Contains all `OrientationData` inner class variants (`LocalOrientationData`,
`TurnToFaceOrientationData`, `OrientToUprightData`, `OrientToPointAtData`),
`TranslateAnimation`, and `RotateAnimation` as local classes.

**Note on local class capture:** `TranslateAnimation` and `RotateAnimation`
are local classes in the original source that reference
`AbstractTransformableImp.this`. After extraction, they reference the `owner`
field instead (e.g., `owner.applyTranslation(...)`, `owner.applyRotationInRadians(...)`).

**Construction:**

```java
// Inside AbstractTransformableImp
private final TransformAnimator animator = new TransformAnimator(this);
```

### AbstractTransformableImp changes

The class shrinks from 977 to ~386 lines. It retains:

1. **Identity methods** — `getSgComposite()`, `isFacing()`, `applyAnimation()`
2. **Local transform accessors** — `getLocalTransformation()`, `getLocalPosition()`,
   `getLocalOrientation()`, `setLocalTransformation()`, `setLocalOrientation()`
3. **Direct apply methods** — `applyTranslation(...)`, `applyRotationInRadians(...)`,
   `applyRotationInRevolutions(...)`
4. **Vehicle hook** — `postCheckSetVehicle(EntityImp)`
5. **Subclass extension points** — `VantagePointData`, `PreSetVantagePointData`,
   `animateVantagePoint()`
6. **Delegation facades** — one-liner methods that forward to `animator`,
   `transformOps`, or `VehicleManager`

Two new `private final` fields are added:

```java
private final TransformAnimator animator = new TransformAnimator(this);
private final TransformOperations transformOps = new TransformOperations(this);
```

## Public API

**No public method signatures change.** Every public method that existed on
`AbstractTransformableImp` before the extraction still exists with identical
parameter types and return types. Callers see zero difference.

| Method | Delegates to |
|---|---|
| `animateApplyTranslation(...)` | `animator.animateApplyTranslation(...)` |
| `animateApplyRotationInRevolutions(...)` | `animator.animateApplyRotationInRevolutions(...)` |
| `animateLocalOrientationOnly(...)` | `animator.animateLocalOrientationOnly(...)` |
| `animateOrientationOnly(...)` | `animator.animateOrientationOnly(...)` |
| `animateOrientationOnlyToFace(...)` | `animator.animateOrientationOnlyToFace(...)` |
| `animateOrientationToUpright(...)` | `animator.animateOrientationToUpright(...)` |
| `animateOrientationToPointAt(...)` | `animator.animateOrientationToPointAt(...)` |
| `setOrientationOnlyToPointAt(...)` | `animator.setOrientationOnlyToPointAt(...)` |
| `animatePositionOnly(...)` | `transformOps.animatePositionOnly(...)` |
| `place(...)` | `transformOps.place(...)` |
| `animatePlace(...)` | `transformOps.animatePlace(...)` |
| `setTransformation(...)` | `transformOps.setTransformation(...)` |
| `animateTransformation(...)` | `transformOps.animateTransformation(...)` |
| `getDistanceTo(...)` | `transformOps.getDistanceTo(...)` |
| `getDistanceAbove(...)` | `transformOps.getDistanceAbove(...)` |
| `getDistanceBelow(...)` | `transformOps.getDistanceBelow(...)` |
| `getDistanceToTheLeftOf(...)` | `transformOps.getDistanceToTheLeftOf(...)` |
| `getDistanceToTheRightOf(...)` | `transformOps.getDistanceToTheRightOf(...)` |
| `getDistanceBehind(...)` | `transformOps.getDistanceBehind(...)` |
| `getDistanceInFrontOf(...)` | `transformOps.getDistanceInFrontOf(...)` |

In addition to the delegated methods above, several **convenience overloads**
stay directly on `AbstractTransformableImp` without delegating to helpers.
They chain to other public methods that already delegate:

| Convenience overload | Calls |
|---|---|
| `animateApplyTranslation(double x, double y, double z, ...)` | `animateApplyTranslation(new Point3(x,y,z), ...)` |
| `place(SpatialRelationImp, EntityImp, double)` | `place(..., target)` (adds `asSeenBy = target`) |
| `place(SpatialRelationImp, EntityImp)` | `place(..., DEFAULT_PLACE_ALONG_AXIS_OFFSET)` |
| `setTransformation(ReferenceFrame)` | `setTransformation(target, AffineMatrix4x4.IDENTITY)` |
| `animateTransformation(ReferenceFrame, AffineMatrix4x4)` | `animateTransformation(..., DEFAULT_IS_SMOOTH, DEFAULT_DURATION, DEFAULT_STYLE)` |

## Protected subclass API

`VantagePointData` and `PreSetVantagePointData` remain as `protected` inner
classes of `AbstractTransformableImp`. They **cannot** move to a helper because:

1. Subclasses `SymmetricPerspectiveCameraImp` and `VrUserImp` instantiate
   `PreSetVantagePointData` directly.
2. `animateVantagePoint` accesses `data.subject` as a `private` field —
   this works because Java allows an enclosing class to read private members
   of its inner classes. Moving `VantagePointData` out would break this.

```java
// SymmetricPerspectiveCameraImp.java — unchanged
PreSetVantagePointData data = new PreSetVantagePointData(this, other);
animateVantagePoint(data, duration, style);
```

## Package-private delegation facades

Some package-private methods are inherited by subclasses (`StandInImp`,
`JointImp`) or called from same-package classes (`JointedModelImp`). These
methods must remain on `AbstractTransformableImp` as delegation facades even
though they are not public:

| Method | Caller(s) | Delegates to |
|---|---|---|
| `setPositionOnly(EntityImp)` | `StandInImp` (inherits; called by `VehicleManager.calculateTurnToFaceAxes`) | `transformOps.setPositionOnly(this, target)` |
| `setLocalOrientationOnly(OrthogonalMatrix3x3)` | `JointImp` (inherits; called by `JointedModelImp` animation callbacks) | `animator.setLocalOrientationOnly(localOrientation)` |

Without these facades, subclass instances that inherit from
`AbstractTransformableImp` would lose the methods.

## Package-private collaboration

The helpers access their owner via a stored reference and same-package
visibility:

```java
// TransformAnimator.java
class TransformAnimator {
    private final AbstractTransformableImp owner;

    TransformAnimator(AbstractTransformableImp owner) {
        this.owner = owner;
    }

    void animateApplyTranslation(Point3 translation, ReferenceFrame asSeenBy,
                                 double duration, Style style) {
        // Uses owner.applyTranslation(), owner.applyAnimation(),
        // owner.adjustDurationIfNecessary(), owner.perform()
        // — all accessible via same-package rules
    }
}
```

Key cross-references between helpers:

| Caller | Callee | Method |
|---|---|---|
| `TransformAnimator` | `VehicleManager` | `calculateTurnToFaceAxes()` (for `TurnToFaceOrientationData`) |
| `TransformAnimator` | `AbstractTransformableImp` | `perform()`, `adjustDurationIfNecessary()`, `applyAnimation()`, `applyTranslation()`, `applyRotationInRadians()` |
| `TransformOperations` | `AbstractTransformableImp` | `getSgComposite()`, `getTransformation()`, `getAxisAlignedMinimumBoundingBox()`, `perform()`, `adjustDurationIfNecessary()`, `applyAnimation()` |
| `VehicleManager` | `AbstractTransformableImp` | `getLocalOrientation()`, `getLocalTransformation()`, `getVehicle()` (on `subject` parameter); `setVehicle()`, `setLocalTransformation()`, `setPositionOnly()`, `applyTranslation()`, `getTransformation()` (on StandInImp instances) |

## Delegation pattern

Every delegation facade follows an identical pattern — a one-line forward:

```java
// In AbstractTransformableImp
public void animateApplyTranslation(Point3 translation, ReferenceFrame asSeenBy,
                                    double duration, Style style) {
    this.animator.animateApplyTranslation(translation, asSeenBy, duration, style);
}
```

This keeps the public type hierarchy unchanged while moving implementation
weight out of the god class.

## Visibility rules

| Symbol | Before | After |
|---|---|---|
| `AbstractTransformableImp` | `public abstract` | `public abstract` (unchanged) |
| `TransformAnimator` | n/a | package-private |
| `TransformOperations` | n/a | package-private |
| `VehicleManager` | n/a | package-private |
| `s_standInPool` | `private static` on ATI | `private static` on `VehicleManager` |
| `VantagePointData` | `protected abstract static` | `protected abstract static` (unchanged) |
| `PreSetVantagePointData` | `protected static` | `protected static` (unchanged) |
| `OrientationData` | `private abstract static` | `private abstract static` on `TransformAnimator` |
| `PlaceData` | `private static` | `private static` on `TransformOperations` |

No new `public` API surface is introduced. The three helper classes have no
`public` modifier, making them invisible outside
`org.lgna.story.implementation`.

## Security boundary

- **No new public types.** All helpers are package-private.
- **No I/O changes.** No file, network, or serialization operations are added.
- **No reflection.** Object pool uses `DefaultPool<StandInImp>` (existing
  pattern), unchanged.
- **No threading changes.** Animation scheduling via `perform()` is identical.
- **No new dependencies.** All imports come from existing project modules.

## Error handling contract

All assertion checks are preserved identically:

- `assert !translation.isNaN()` in `animateApplyTranslation`
- `assert duration >= 0` in animation entry points
- `assert style != null` and `assert asSeenBy != null`
- `Logger.severe("bounding box is NaN", ...)` in `PlaceData.calculateTranslation1`
- Null-guard on `offset` in `setTransformation` and `animateTransformation`

No new exceptions are thrown. No existing assertions are removed.

## Configuration

No new configuration is introduced. The extraction is a compile-time structural
change with no runtime knobs.

Existing constants preserved in their new locations:

| Constant | Location |
|---|---|
| `DEFAULT_IS_SMOOTH` (`true`) | `TransformOperations` |
| `DEFAULT_PLACE_ALONG_AXIS_OFFSET` (`0.0`) | `TransformOperations` |
| `DEFAULT_DURATION`, `DEFAULT_STYLE`, `RIGHT_NOW` | Inherited from `EntityImp` (unchanged) |

## Validation

Build and test command:

```bash
mvn -pl core/story-api -am -DfailIfNoTests=false -Dcheckstyle.skip test
```

Expected result: **119 tests, 0 failures, 0 errors.**

Additional verification:

```bash
# Confirm line count target met
wc -l core/story-api/src/main/java/org/lgna/story/implementation/AbstractTransformableImp.java
# Expected: ~386 lines (under 500)

# Confirm new files created
wc -l core/story-api/src/main/java/org/lgna/story/implementation/{TransformAnimator,TransformOperations,VehicleManager}.java
# Expected: ~390, ~370, ~104 lines respectively
```

## Migration guide for subclass authors

**Nothing changes for subclass authors.** This section exists only to document
the preservation guarantees.

### Subclasses that override or extend transform behavior

| Subclass | Usage | Impact |
|---|---|---|
| `SymmetricPerspectiveCameraImp` | Overrides `animateVantagePoint` via `PreSetVantagePointData` | None — `VantagePointData`/`PreSetVantagePointData` remain on `AbstractTransformableImp` |
| `VrUserImp` | Overrides `animateVantagePoint` via `PreSetVantagePointData` | None — same as above |
| `JointImp` | Extends `AbstractTransformableImp`, uses `getLocalTransformation()` | None — all local-transform accessors stay in place |

### If you were extending OrientationData

`OrientationData` and its subclasses were always `private` — no external
subclass could have extended them. Their move to `TransformAnimator` has no
visibility impact.

## Examples

### Calling animated translation (unchanged)

```java
// Before and after — identical API
AbstractTransformableImp entity = ...;
entity.animateApplyTranslation(
    new Point3(1.0, 0.0, 0.0),
    someReferenceFrame,
    0.5,   // duration in seconds
    Style.BEGIN_AND_END_GENTLY
);
```

### Placement (unchanged)

```java
entity.place(SpatialRelationImp.ABOVE, targetEntity);

entity.animatePlace(
    SpatialRelationImp.IN_FRONT_OF,
    targetEntity,
    0.0,           // along-axis offset
    targetEntity,  // asSeenBy
    true,          // smooth
    1.0,           // duration
    Style.BEGIN_AND_END_GENTLY
);
```

### Distance queries (unchanged)

```java
double dist = entity.getDistanceTo(other);
double above = entity.getDistanceAbove(other, asSeenBy);
double behind = entity.getDistanceBehind(other, asSeenBy);
```

### Subclass vantage point (unchanged)

```java
// In SymmetricPerspectiveCameraImp
PreSetVantagePointData data = new PreSetVantagePointData(this, other);
this.animateVantagePoint(data, duration, style);
```

## Acceptance criteria

| # | Criterion | Verification |
|---|---|---|
| 1 | `AbstractTransformableImp` < 500 lines | `wc -l` reports ~386 |
| 2 | `TransformOperations.java` created | File exists, ~370 lines |
| 3 | `VehicleManager.java` created | File exists, ~104 lines |
| 4 | `TransformAnimator.java` created | File exists, ~390 lines |
| 5 | Maven build passes | `mvn -pl core/story-api -am test` — 0 failures |
| 6 | No public/protected signature changes | All facades delegate with identical signatures |
| 7 | Subclass overrides compile | `SymmetricPerspectiveCameraImp`, `VrUserImp` unchanged |
| 8 | No new public API surface | All helpers are package-private |
| 9 | Total line count roughly preserved | ~386 + 390 + 370 + 104 ≈ 1250 (vs. 977 original + added structure) |

## Claim boundaries

This extraction:

- **Does** reduce `AbstractTransformableImp` from 977 to ~386 lines
- **Does** isolate animation, position/placement, and vehicle logic into
  focused classes
- **Does** preserve all public and protected method signatures
- **Does not** change runtime behavior, animation timing, or coordinate math
- **Does not** introduce new public types visible outside the package
- **Does not** affect the `VantagePointData`/`PreSetVantagePointData` subclass
  extension point
- **Does not** add or remove any test — this is a structural refactor verified
  by existing tests
