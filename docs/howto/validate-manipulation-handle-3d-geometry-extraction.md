# How to Validate the ManipulationHandle3D Geometry Extraction

This how-to provides step-by-step validation commands for the
ManipulationHandle3D extraction (issue #663).

For background, see the
[reference](../reference/manipulation-handle-3d-geometry-extraction.md).

For a guided walkthrough, see the
[tutorial](../tutorials/trace-manipulation-handle-3d-geometry-extraction.md).

## Prerequisites

- Repository root as working directory
- Tweedle grammar submodule initialized:
  ```bash
  git submodule update --init tweedle-lang
  ```

## 1. Verify new files exist

```bash
ls -la core/story-api/src/main/java/org/alice/interact/handle/{HandleGeometryHelper,Not3dHandleCriterion,DoubleInterruptibleAnimation,Color4fInterruptibleAnimation}.java
```

All four files must exist. Each must contain the CMU BSD copyright header.

## 2. Verify line count

```bash
wc -l core/story-api/src/main/java/org/alice/interact/handle/ManipulationHandle3D.java
```

**Pass criterion:** Under 500 lines (~498 expected).

## 3. Compile

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am -DfailIfNoTests=false -Dcheckstyle.skip compile
```

**Pass criterion:** `BUILD SUCCESS` with zero compilation errors.

## 4. Verify no subclass modifications

```bash
git diff --name-only -- '*.java' | sort
```

Expected changed/added files:

| File | Status |
| --- | --- |
| `.../handle/ManipulationHandle3D.java` | Modified |
| `.../handle/HandleGeometryHelper.java` | Added |
| `.../handle/Not3dHandleCriterion.java` | Added |
| `.../handle/DoubleInterruptibleAnimation.java` | Added |
| `.../handle/Color4fInterruptibleAnimation.java` | Added |

No other `.java` files should appear. Subclasses (`RotationRingHandle`,
`LinearTranslateHandle`, `LinearScaleHandle`, `LinearDragHandle`,
`StoodUpRotationRingHandle`, `JointRotationRingHandle`) must be unchanged.

## 5. Verify NOT_3D_HANDLE_CRITERION constant preserved

```bash
grep -n 'NOT_3D_HANDLE_CRITERION' \
  core/story-api/src/main/java/org/alice/interact/handle/ManipulationHandle3D.java
```

**Pass criterion:** The constant declaration is present on ManipulationHandle3D.

## 6. Verify no inner class remnants

```bash
grep -n 'class.*InterruptibleAnimation' \
  core/story-api/src/main/java/org/alice/interact/handle/ManipulationHandle3D.java
```

**Pass criterion:** Zero matches. Both animation classes are now in
separate files.

## 7. Verify HandleGeometryHelper is package-private

```bash
grep -n 'class HandleGeometryHelper' \
  core/story-api/src/main/java/org/alice/interact/handle/HandleGeometryHelper.java
```

**Pass criterion:** Line reads `final class HandleGeometryHelper` (no
`public` modifier).

## 8. Run tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test
```

**Pass criterion:** `BUILD SUCCESS`. All existing tests pass.

## 9. Verify static method signatures

```bash
grep -n 'static.*getTransformationForAxis\|static.*getManipulatedObjectBox\|static.*getObjectScale\|static.*calculateCameraRelativeOpacity\|static.*invertParentScale\|static.*getScalable' \
  core/story-api/src/main/java/org/alice/interact/handle/HandleGeometryHelper.java
```

**Pass criterion:** Six static method signatures present.

## Quick all-in-one gate

```bash
git submodule update --init tweedle-lang && \
wc -l core/story-api/src/main/java/org/alice/interact/handle/ManipulationHandle3D.java && \
ls core/story-api/src/main/java/org/alice/interact/handle/{HandleGeometryHelper,Not3dHandleCriterion,DoubleInterruptibleAnimation,Color4fInterruptibleAnimation}.java && \
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am -DfailIfNoTests=false -Dcheckstyle.skip compile -q && \
echo "GATE: PASS"
```
