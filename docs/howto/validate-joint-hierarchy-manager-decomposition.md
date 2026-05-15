# Validate Joint Hierarchy Manager Decomposition

> Verifies that the extraction of `JointImpWrapper` and `IkChainHelper` from
> `JointHierarchyManager` is correct and complete.

## Prerequisites

- Java 17+ and Maven on PATH
- The `tweedle-lang` submodule initialized:
  ```bash
  git submodule update --init tweedle-lang
  ```

## Quick Validation

Run the module test suite (the acceptance gate for this refactoring):

```bash
mvn -pl core/story-api -am -DfailIfNoTests=false -Dcheckstyle.skip test
```

All tests should pass with `BUILD SUCCESS`.

## Step-by-Step Verification

### 1. Confirm Line Count Target

```bash
wc -l core/story-api/src/main/java/org/lgna/story/implementation/JointHierarchyManager.java
```

Expected: **≤500 lines** (actual: 350).

### 2. Confirm New Files Exist

```bash
ls -la core/story-api/src/main/java/org/lgna/story/implementation/{JointImpWrapper,IkChainHelper}.java
```

Both files should be present.

### 3. Verify Public API Is Unchanged

The `JointHierarchyManager` is package-private with no public methods.
Its API boundary is the set of package-visible methods called by
`JointedModelImp`. Confirm no callers outside the package reference it:

```bash
# Should find only JointedModelImp and JointHierarchyManager itself:
grep -rn "JointHierarchyManager" \
  --include="*.java" core/story-api/src/main/java/
```

### 4. Verify JointData Is Still Accessible

`JointedModelImp` references `JointHierarchyManager.JointData` at three
call sites. Confirm the type is still resolvable:

```bash
grep -rn "JointHierarchyManager\.JointData" \
  --include="*.java" core/story-api/src/main/java/
```

Expected: matches in `JointedModelImp.java` (lines ~413, ~425, ~432).

### 5. Verify Package-Private Visibility

The extracted classes should **not** be `public`:

```bash
head -10 core/story-api/src/main/java/org/lgna/story/implementation/JointImpWrapper.java
head -10 core/story-api/src/main/java/org/lgna/story/implementation/IkChainHelper.java
```

Each class declaration should be `class JointImpWrapper` / `class IkChainHelper`
(no `public` modifier).

### 6. Verify IkChainHelper Uses Static Methods

All methods in `IkChainHelper` should be `static` (no instance state):

```bash
grep -En "static.*List|static.*void|private enum" \
  core/story-api/src/main/java/org/lgna/story/implementation/IkChainHelper.java
```

Expected: `getInclusiveListOfJointsBetween` is package-visible static;
`updateJointsBetween` and `updateJointsUpToAndExcludingCommonAncestor` are
private static; `AddOp` is a private enum.

### 7. Verify JointImpWrapper Constructor Injection

The wrapper should accept a `JointedModelResourceBinder<?>` parameter:

```bash
grep -n "JointImpWrapper(" \
  core/story-api/src/main/java/org/lgna/story/implementation/JointImpWrapper.java
```

Expected: constructor signature includes `JointedModelResourceBinder<?>`.

### 8. Run the Decomposition-Specific Test

```bash
mvn -pl core/story-api -am -DfailIfNoTests=false \
  -Dtest=JointedModelImpDecompositionTest test -q
```

### 9. Full Build Smoke Test

To confirm no ripple effects across the project:

```bash
mvn -DfailIfNoTests=false -Dcheckstyle.skip compile
```

## What to Look For if Tests Fail

| Symptom | Likely Cause |
|---|---|
| `cannot find symbol: JointImpWrapper` | Class not in `org.lgna.story.implementation` package or file not created |
| `cannot find symbol: IkChainHelper` | Same as above |
| `resourceBinder.isSims()` not accessible | `JointedModelResourceBinder<?>` not passed to `JointImpWrapper` constructor |
| `cannot find symbol: AddOp` in `JointHierarchyManager` | `AddOp` enum not removed from manager or `IkChainHelper` not imported |
| `NullPointerException` in IK chain | `Function<JointId, JointImp>` parameter not passed or joint not in map |
| `Lists` not found in `IkChainHelper` | Missing `import edu.cmu.cs.dennisc.java.util.Lists` — needed for `Lists.newLinkedList()` (3 call sites) |
| `JointHierarchyManager.JointData` not found | `JointData` accidentally moved out of `JointHierarchyManager` |
| `getInclusiveListOfJointsBetween` signature mismatch | `JointedModelImp` call site not updated to pass `Function` parameter, or manager delegation missing |

## Rollback

If the decomposition introduces regressions, revert the commit. Both new
files will be removed and the manager restored to 650 lines:

```bash
git revert <commit-sha>
```

No other modules or configuration files are affected.
