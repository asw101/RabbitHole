# Validate COLLADA Exporter Decomposition

> Verifies that the extraction of `ColladaParser`, `ColladaJointExtractor`, and
> `ColladaMeshProcessor` from `JointedModelColladaExporter` is correct and
> complete.

## Prerequisites

- Java 17+ and Maven on PATH
- The `tweedle-lang` submodule initialized:
  ```bash
  git submodule update --init tweedle-lang
  ```

## Quick Validation

Run the module test suite (the acceptance gate for this refactoring):

```bash
mvn -pl core/model-loading -am -DfailIfNoTests=false -Dcheckstyle.skip test
```

All tests should pass with `BUILD SUCCESS`.

## Step-by-Step Verification

### 1. Confirm Line Count Target

```bash
wc -l core/model-loading/src/main/java/org/lgna/story/resourceutilities/JointedModelColladaExporter.java
```

Expected: **≤500 lines** (target: ~350).

### 2. Confirm New Files Exist

```bash
ls -la core/model-loading/src/main/java/org/lgna/story/resourceutilities/{ColladaParser,ColladaJointExtractor,ColladaMeshProcessor}.java
```

All three files should be present.

### 3. Verify Public API Is Unchanged

The `JointedModelExporter` interface contract is the compatibility boundary.
No callers outside the package should need changes:

```bash
# Should find only JointedModelColladaExporter, JointedModelGltfExporter,
# JointedModelAliceExporter:
grep -rn "implements JointedModelExporter" \
  --include="*.java" core/ external/
```

### 4. Verify No Subclass Breakage

`createCollada()` is `protected`. Confirm no external subclasses exist:

```bash
grep -rn "extends JointedModelColladaExporter" \
  --include="*.java" core/ external/
```

Expected: **no matches**. If matches exist, verify the subclass still
compiles and that `createCollada()` remains accessible.

### 5. Verify Package-Private Visibility

The extracted classes should **not** be `public`:

```bash
head -5 core/model-loading/src/main/java/org/lgna/story/resourceutilities/ColladaParser.java
head -5 core/model-loading/src/main/java/org/lgna/story/resourceutilities/ColladaJointExtractor.java
head -5 core/model-loading/src/main/java/org/lgna/story/resourceutilities/ColladaMeshProcessor.java
```

Each class declaration should be `class ColladaXxx` (no `public` modifier).

### 6. Full Build Smoke Test

To confirm no ripple effects across the project:

```bash
mvn -DfailIfNoTests=false -Dcheckstyle.skip compile
```

## What to Look For if Tests Fail

| Symptom | Likely Cause |
|---|---|
| `cannot find symbol` on extracted method | Method visibility changed or signature drifted during extraction |
| `NullPointerException` in `createSkeletonNodes` | Null guard on root joint was lost during move to `ColladaJointExtractor` |
| `meshNameMap` is empty | `initializeMeshNameMap()` no longer called before mesh processor runs |
| Coordinate flip regressions | `FLIP_COORDINATE_SPACE` constant not accessible to extracted class |
| JAXB marshalling errors | `ObjectFactory` instance not shared with delegates |

## Rollback

If the decomposition introduces regressions, the exporter can be reverted to
its monolithic form by reverting the commit. The three new files will be
removed and the exporter restored to 1181 lines:

```bash
git revert <commit-sha>
```

No other modules or configuration files are affected.
