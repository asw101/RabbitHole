# Solver JacobianMath Extraction

This reference documents the extraction of SVD/Jacobian math from
`Solver.java` (557 lines) into a new stateless utility class
`JacobianMath.java` in `org.lgna.ik.core.solver`. The extraction reduces
Solver to ~493 lines (under the 500-line target).

Issue: #690

## Contents

- [Motivation](#motivation)
- [Architecture](#architecture)
- [JacobianMath API](#jacobianmath-api)
  - [computePseudoInverses](#computepseudoinverses)
  - [createVelocityColumn](#createvelocitycolumn)
  - [reduceAndInvertSofSvdBasically](#reduceandinvertsofsvdbasically)
  - [reduceAndInvertSofSvdByDamping](#reduceandinvertsofsvdbydamping)
  - [reduceAndInvertSofSvdByClampingSmallEntries](#reduceandinvertsofsvdbyclampingsmallentries)

- [Solver call-site changes](#solver-call-site-changes)
- [Inner class preservation](#inner-class-preservation)
- [Configuration](#configuration)
- [Examples](#examples)
- [Validation](#validation)
- [Characterization tests](#characterization-tests)
- [Risks and mitigations](#risks-and-mitigations)
- [Acceptance criteria](#acceptance-criteria)
- [Claim boundaries](#claim-boundaries)

## Motivation

`Solver.java` contained 557 lines mixing two distinct concerns:

1. **IK orchestration** — chain management, constraint preparation, Jacobian
   construction, angle-speed calculation, null-space projection, and
   default-pose blending.
2. **SVD/Jacobian linear algebra** — singular value decomposition, damped and
   basic pseudo-inverse computation, and velocity column assembly.

The SVD helpers (`reduceAndInvertSofSvdBasically`,
`reduceAndInvertSofSvdByDamping`, `reduceAndInvertSofSvdByClampingSmallEntries`)
are pure functions with no dependency on Solver state. The `invertJacobian`
method orchestrates SVD decomposition and pseudo-inverse assembly — all matrix
math that can stand alone. The `createDesiredVelocitiesColumn` method converts
velocity vectors into a JAMA `Matrix` column — another stateless operation.

Two of the three SVD helpers (`reduceAndInvertSofSvdBasically` and
`reduceAndInvertSofSvdByDamping`) already appear duplicated in `SvdInfo.java`
inside `org.lgna.ik.core.enforcer`, confirming this is a natural extraction
boundary.

Separating the math into `JacobianMath` yields:

- **Testability**: SVD inversion and velocity assembly can be unit-tested with
  known matrices, independent of chain/constraint setup.
- **Readability**: Solver focuses on IK orchestration; JacobianMath focuses on
  linear algebra.
- **Line count**: Solver drops from 557 to ~493 lines.

## Architecture

```text
Solver.java (~493 lines, IK orchestration)
├── prepareConstraints()
├── constructJacobianEntries()
├── createJacobian()
├── invertJacobian()               ← 3-line wrapper, delegates to JacobianMath
├── createDesiredVelocitiesColumn() ← 5-line wrapper, delegates to JacobianMath
├── calculateAngleSpeeds()
├── solve()
├── projectToNullSpace()
├── addAngleSpeedsTowardsDefaultPoseInNullSpace()
├── createBoneSpeedsFromThetadot()
├── createThetadotFromBoneSpeeds()
├── createJacobianMatrix()
│
├── inner: Constraint        (unchanged)
├── inner: JacobianAndInverse (unchanged, public)
└── inner: DesiredVelocity   (unchanged, package-private)

JacobianMath.java (~70 lines, stateless SVD/matrix math)
├── computePseudoInverses(Matrix, double) → Matrix[2]
├── createVelocityColumn(Vector3[]) → Matrix
├── reduceAndInvertSofSvdBasically(Matrix) → void
├── reduceAndInvertSofSvdByDamping(Matrix, double) → void
└── reduceAndInvertSofSvdByClampingSmallEntries(Matrix, double) → void
```

No new dependencies are introduced. `JacobianMath` uses only `Jama.Matrix`,
`Jama.SingularValueDecomposition`, and `org.alice.math.immutable.Vector3` —
all already on the module classpath.

## JacobianMath API

`JacobianMath` is a stateless utility class in `org.lgna.ik.core.solver`.
All methods are `static`. The class has a private constructor and cannot be
instantiated.

### computePseudoInverses

```java
public static Matrix[] computePseudoInverses(Matrix jacobian, double dampingConstant)
```

Computes two pseudo-inverse matrices from a Jacobian via SVD decomposition:

- **`result[0]`** — damped pseudo-inverse (for motion). Uses
  `reduceAndInvertSofSvdByDamping` with the supplied damping constant.
- **`result[1]`** — basic pseudo-inverse (for null-space projection). Uses
  `reduceAndInvertSofSvdBasically`.

The method handles the under-determined case (rows < columns) by transposing
before SVD and transposing the results back — preserving the original
`invertJacobian` behavior exactly.

**Parameters:**

| Name | Type | Description |
| --- | --- | --- |
| `jacobian` | `Jama.Matrix` | The Jacobian matrix (m × n). |
| `dampingConstant` | `double` | SVD damping factor. Pass `IkConstants.SVD_DAMPING_CONSTANT` (0.1) for the standard IK solver behavior. |

**Returns:** `Matrix[2]` — `[pseudoInverseForMotion, pseudoInverseForNullspace]`.

**Throws:** Nothing. Behavior on zero singular values is preserved from the
original code (division by zero in `reduceAndInvertSofSvdBasically` — a
pre-existing condition not addressed by this extraction).

### createVelocityColumn

```java
public static Matrix createVelocityColumn(Vector3[] velocities)
```

Packs an array of 3D velocity vectors into a single-column JAMA `Matrix`
of dimension `(velocities.length × 3) × 1`.

Each `Vector3` contributes three consecutive rows: `(x, y, z)`.

**Parameters:**

| Name | Type | Description |
| --- | --- | --- |
| `velocities` | `Vector3[]` | Array of velocity vectors. Must not be null. |

**Returns:** `Matrix` — column vector of dimension `(3n × 1)`.

**Design note:** This method accepts `Vector3[]` rather than
`Solver.DesiredVelocity[]` to keep JacobianMath decoupled from Solver's inner
classes. Solver provides a thin wrapper that extracts the `velocity` field from
each `DesiredVelocity` before calling this method.

### reduceAndInvertSofSvdBasically

```java
static void reduceAndInvertSofSvdBasically(Matrix s)
```

Inverts the diagonal of an SVD `S` matrix in place: `s[i][i] = 1.0 / s[i][i]`.

Package-private. Used internally by `computePseudoInverses` for the null-space
pseudo-inverse. Exposed at package scope for direct testing.

**Warning:** Division by zero occurs if any singular value is zero. This is a
pre-existing condition preserved from the original `Solver` implementation.

### reduceAndInvertSofSvdByDamping

```java
static void reduceAndInvertSofSvdByDamping(Matrix s, double svdDampingConstant)
```

Inverts the diagonal of an SVD `S` matrix in place using Tikhonov (damped)
regularization:

```
s[i][i] = d / (d² + λ²)
```

where `d` is the original singular value and `λ` is `svdDampingConstant`.

Package-private. Used internally by `computePseudoInverses` for the motion
pseudo-inverse. The damping prevents numerical instability near singular
configurations.

### reduceAndInvertSofSvdByClampingSmallEntries

```java
static void reduceAndInvertSofSvdByClampingSmallEntries(Matrix s, double threshold)
```

Inverts the diagonal of an SVD `S` matrix in place, clamping entries below
`threshold` to zero instead of inverting them.

Package-private. Not currently called by any production code — preserved from
the original `Solver` for completeness. May be useful for alternative IK
strategies that require hard singular-value cutoff rather than damping.

## Solver call-site changes

Two methods in `Solver.java` are modified to delegate to `JacobianMath`:

### invertJacobian (before)

```java
private JacobianAndInverse invertJacobian(Matrix jacobian) {
    // 35 lines of SVD decomposition, transposition, pseudo-inverse assembly
}
```

### invertJacobian (after)

```java
private JacobianAndInverse invertJacobian(Matrix jacobian) {
    Matrix[] inverses = JacobianMath.computePseudoInverses(
        jacobian, IkConstants.SVD_DAMPING_CONSTANT);
    return new JacobianAndInverse(jacobian, inverses[0], inverses[1]);
}
```

### createDesiredVelocitiesColumn (before)

```java
private Matrix createDesiredVelocitiesColumn(DesiredVelocity[] desiredVelocities2) {
    // 13 lines unpacking DesiredVelocity into a column matrix
}
```

### createDesiredVelocitiesColumn (after)

```java
private Matrix createDesiredVelocitiesColumn(DesiredVelocity[] desiredVelocities2) {
    Vector3[] vecs = new Vector3[desiredVelocities2.length];
    for (int i = 0; i < vecs.length; i++) {
        vecs[i] = desiredVelocities2[i].velocity;
    }
    return JacobianMath.createVelocityColumn(vecs);
}
```

## Inner class preservation

All three inner classes remain in `Solver.java`:

| Inner class | Visibility | Reason |
| --- | --- | --- |
| `Constraint` | `private` | Only used within Solver for constraint aggregation. |
| `JacobianAndInverse` | `public` | Referenced by `JointedModelIkEnforcer` as `Solver.JacobianAndInverse`. Moving it would break the downstream import. |
| `DesiredVelocity` | package-private | Only used within `Solver` and would couple `JacobianMath` to Solver internals if exposed. |

## Configuration

No new configuration. `JacobianMath.computePseudoInverses` receives the
damping constant as a parameter. The default value remains
`IkConstants.SVD_DAMPING_CONSTANT` (0.1), unchanged.

## Examples

### Using JacobianMath directly (test or advanced usage)

```java
import Jama.Matrix;
import org.lgna.ik.core.solver.JacobianMath;

// A 2×3 Jacobian (under-determined system)
Matrix jacobian = new Matrix(new double[][] {
    {1.0, 0.0, 0.5},
    {0.0, 1.0, 0.3}
});

Matrix[] inverses = JacobianMath.computePseudoInverses(jacobian, 0.1);
Matrix pseudoInverseForMotion    = inverses[0];  // 3×2
Matrix pseudoInverseForNullspace = inverses[1];  // 3×2

// Verify dimensions
assert pseudoInverseForMotion.getRowDimension() == 3;
assert pseudoInverseForMotion.getColumnDimension() == 2;
```

### Assembling a velocity column

```java
import org.alice.math.immutable.Vector3;
import org.lgna.ik.core.solver.JacobianMath;

Vector3[] velocities = {
    new Vector3(1.0, 0.0, 0.0),
    new Vector3(0.0, 0.5, -0.3)
};

Matrix column = JacobianMath.createVelocityColumn(velocities);
// column is 6×1: [1.0, 0.0, 0.0, 0.0, 0.5, -0.3]ᵀ
```

### Standard Solver usage (unchanged)

```java
Solver solver = new Solver();
solver.setJointWeights(weights);
solver.addChain(chain);
solver.setDesiredEndEffectorLinearVelocity(chain, linearVelocity);

// solve() internally calls invertJacobian → JacobianMath.computePseudoInverses
Map<Bone, Map<Axis, Double>> speeds = solver.solve();
```

The public API of `Solver` is completely unchanged. Callers do not need any
modifications.

## Validation

- `mvn compile -pl core/story-api` succeeds.
- `mvn test -pl core/story-api` passes — no behavioral change.
- `JacobianMathTest` provides characterization coverage for the extracted math.
- `wc -l Solver.java` reports ≤ 500 lines.
- `JointedModelIkEnforcer` compiles without import changes (inner class
  `JacobianAndInverse` stays in `Solver`).

## Characterization tests

`JacobianMathTest.java` is located at
`core/story-api/src/test/java/org/lgna/ik/core/solver/JacobianMathTest.java`.

| Test | What it verifies |
| --- | --- |
| `computePseudoInverses_identity_returnsIdentity` | Identity Jacobian yields identity pseudo-inverses (both motion and null-space). |
| `computePseudoInverses_underdetermined_transposes` | Under-determined (m < n) Jacobian produces correctly transposed inverses. |
| `computePseudoInverses_dampingReducesSingularity` | Damped inverse is finite when basic inverse would be near-singular. |
| `createVelocityColumn_packsCorrectly` | Vector3 array is packed into a (3n × 1) column in x,y,z order. |
| `reduceAndInvertSofSvdBasically_invertsdiagonal` | Diagonal inversion: `[2, 4]` → `[0.5, 0.25]`. |
| `reduceAndInvertSofSvdByDamping_dampsDiagonal` | Damped inversion: `d / (d² + λ²)` matches expected values. |
| `reduceAndInvertSofSvdByClampingSmallEntries_clampsBelow` | Entries below threshold become zero; entries above are inverted. |

These tests use known matrices with hand-computed expected values. They serve
as characterization tests: if any math changes in the future, these tests will
detect the regression.

## Risks and mitigations

| Risk | Mitigation |
| --- | --- |
| `JointedModelIkEnforcer` imports `Solver.JacobianAndInverse` | Inner class stays in Solver — no import breakage. |
| `SvdInfo.java` has duplicate SVD helpers | Out of scope. Confirms the extraction pattern is safe. A future PR could redirect `SvdInfo` to use `JacobianMath`. |
| Division-by-zero in `reduceAndInvertSofSvdBasically` | Pre-existing. Preserved as-is. Documented in Javadoc. |
| Extraction changes SVD computation order | No. Method bodies are functionally identical to the originals. Characterization tests confirm identical outputs. |

## Acceptance criteria

1. `Solver.java` is ≤ 500 lines (target: ~493).
2. `JacobianMath.java` compiles and contains all extracted SVD/matrix methods.
3. `Solver`'s public API is unchanged — no method signatures added or removed.
4. `JacobianMathTest` passes with all characterization tests green.
5. `mvn test -pl core/story-api` passes with no regressions.
6. `JointedModelIkEnforcer` compiles without changes.

## Claim boundaries

This extraction claims only the following scope:

- **In scope**: SVD pseudo-inverse computation, velocity column assembly, and
  the three `reduceAndInvertSofSvd*` helper methods.
- **Out of scope**: Jacobian construction (`createJacobianMatrix`), constraint
  preparation, chain management, null-space projection, default-pose blending,
  `SvdInfo.java` deduplication, and any changes to `JacobianAndInverse` or
  `DesiredVelocity` inner classes.
