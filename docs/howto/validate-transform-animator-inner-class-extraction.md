# Validate TransformAnimator Inner Class Extraction

Use this guide to verify the extraction of inner classes from
`TransformAnimator` into three new top-level files (issue #639).

For the full contract, see the [TransformAnimator Inner Class Extraction
reference](../reference/transform-animator-inner-class-extraction.md).

## When to use this guide

Use this guide when:

- Reviewing changes that extract inner classes from `TransformAnimator`
- Modifying `OrientationData.java`, `SmoothPositionAnimations.java`, or
  `PlaceAnimation.java`
- Changing visibility of fields or methods in any of the extracted classes
- Verifying that `TransformAnimator.java` is under 500 lines

## Before you start

Run commands from the repository root:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
export NODE_OPTIONS=--max-old-space-size=32768
```

## Step 1: Verify new files exist

```bash
ls -la core/story-api/src/main/java/org/lgna/story/implementation/{OrientationData,SmoothPositionAnimations,PlaceAnimation}.java
```

All three files must exist. Each must have:

- The CMU BSD copyright header
- `package org.lgna.story.implementation;`
- Package-private class declarations (no `public` modifier)

## Step 2: Verify compilation

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am -DfailIfNoTests=false -Dcheckstyle.skip compile
```

Compilation must succeed with zero errors. Key things the compiler verifies:

- `OrientationData.getSubject()` is accessible from `TransformAnimator`
- `SmoothPositionAnimation` can access `SmoothAffineMatrix4x4Animation` fields
  (`m1`, `xHermite`, `yHermite`, `zHermite`) via package-private visibility
- `PlaceAnimation` can access `TransformOperations.PlaceData.subject`

## Step 3: Verify line count

```bash
wc -l core/story-api/src/main/java/org/lgna/story/implementation/TransformAnimator.java
```

Target: under 500 lines. Expected: ~368 lines (616 − 248 extracted lines).

## Step 4: Verify the field access fix

```bash
grep -n 'data\.subject' \
  core/story-api/src/main/java/org/lgna/story/implementation/TransformAnimator.java
```

This must return zero matches. The old `data.subject` direct access (formerly
at line 361) must be replaced with `data.getSubject()`:

```bash
grep -n 'data\.getSubject()' \
  core/story-api/src/main/java/org/lgna/story/implementation/TransformAnimator.java
```

This must return one match inside `animateOrientationOnly()`.

## Step 5: Verify class visibility

```bash
grep -c 'public class' \
  core/story-api/src/main/java/org/lgna/story/implementation/OrientationData.java \
  core/story-api/src/main/java/org/lgna/story/implementation/SmoothPositionAnimations.java \
  core/story-api/src/main/java/org/lgna/story/implementation/PlaceAnimation.java
```

All three files must show `0` — no `public class` declarations. All extracted
classes are package-private.

## Step 6: Verify co-location of smooth animation classes

```bash
grep -c 'class.*Animation' \
  core/story-api/src/main/java/org/lgna/story/implementation/SmoothPositionAnimations.java
```

Must return `2` — both `SmoothAffineMatrix4x4Animation` and
`SmoothPositionAnimation` are in the same file. This preserves the child
class's access to the parent's package-private fields.

## Step 7: Run tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test
```

All existing tests must pass. No new tests are required for this pure
structural extraction — the extracted code has identical semantics.

## Step 8: Verify no inner class remnants

```bash
grep -n 'private.*static.*class.*\(OrientationData\|PreSetOrientationData\|LocalOrientationData\|TurnToFaceOrientationData\|OrientToUprightData\|OrientToPointAtData\|SmoothAffineMatrix4x4Animation\|SmoothPositionAnimation\|PlaceAnimation\)' \
  core/story-api/src/main/java/org/lgna/story/implementation/TransformAnimator.java
```

Must return zero matches. All 9 inner class declarations have been moved to
their own files (6 orientation classes, 2 smooth animation classes,
1 placement animation class).

## Summary checklist

| Check | Pass criteria |
| --- | --- |
| Three new files exist | `OrientationData.java`, `SmoothPositionAnimations.java`, `PlaceAnimation.java` |
| Compilation succeeds | `mvn compile` zero errors |
| Line count under 500 | `wc -l TransformAnimator.java` < 500 |
| `data.subject` → `data.getSubject()` | grep confirms no direct field access |
| Package-private visibility | No `public class` in extracted files |
| Co-location preserved | Two classes in `SmoothPositionAnimations.java` |
| Tests pass | `mvn test` zero failures |
| No inner class remnants | grep confirms all 9 classes removed |
