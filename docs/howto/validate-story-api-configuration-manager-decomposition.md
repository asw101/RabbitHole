# Validate StoryApiConfigurationManager Decomposition

> Verifies that the extraction of `StoryTypeComparator` and
> `JointMethodAugmentor` from `StoryApiConfigurationManager` is correct
> and complete.

## Prerequisites

- Java 17+ and Maven on PATH
- The `tweedle-lang` submodule initialized:
  ```bash
  git submodule update --init tweedle-lang
  ```

## Quick Validation

Compile the affected module and its dependencies (the acceptance gate for
this refactoring):

```bash
mvn compile -pl core/ide -am -q
```

Expected: `BUILD SUCCESS` with no errors.

## Step-by-Step Verification

### 1. Confirm Line Count Target

```bash
wc -l core/ide/src/main/java/org/alice/stageide/StoryApiConfigurationManager.java
```

Expected: **≤500 lines** (target: ~450).

### 2. Confirm New Files Exist

```bash
ls -la core/ide/src/main/java/org/alice/stageide/{StoryTypeComparator,JointMethodAugmentor}.java
```

Both files should be present.

### 3. Verify Package-Private Visibility

The extracted classes should **not** be `public`:

```bash
head -50 core/ide/src/main/java/org/alice/stageide/StoryTypeComparator.java
head -50 core/ide/src/main/java/org/alice/stageide/JointMethodAugmentor.java
```

- `StoryTypeComparator` declaration: `enum StoryTypeComparator` (no `public`).
- `JointMethodAugmentor` declaration: `final class JointMethodAugmentor` (no `public`).

### 4. Verify Delegation in StoryApiConfigurationManager

Confirm the two delegation methods exist and are concise:

```bash
grep -A3 "getTypeComparator\|augmentTypeIfNecessary" \
  core/ide/src/main/java/org/alice/stageide/StoryApiConfigurationManager.java
```

Expected:
- `getTypeComparator()` returns `StoryTypeComparator.SINGLETON`
- `augmentTypeIfNecessary(UserType<?> rv)` delegates to `JointMethodAugmentor.augment(rv)`

### 5. Verify BIPED_RESOURCE_TYPE Remains in Parent

The `protected static` constant must stay in `StoryApiConfigurationManager`
for the nonfree subclass:

```bash
grep "BIPED_RESOURCE_TYPE" \
  core/ide/src/main/java/org/alice/stageide/StoryApiConfigurationManager.java
```

Expected: `protected static final JavaType BIPED_RESOURCE_TYPE = ...`

### 6. Verify No Stale Inner Class

The `TypeComparator` inner enum should no longer exist in the manager:

```bash
grep -n "private static enum TypeComparator" \
  core/ide/src/main/java/org/alice/stageide/StoryApiConfigurationManager.java
```

Expected: **no matches**.

### 7. Verify JointMethodAugmentor Has Static Entry Point

```bash
grep -n "static.*augment\|JOINTED_MODEL_TYPE" \
  core/ide/src/main/java/org/alice/stageide/JointMethodAugmentor.java
```

Expected:
- `static UserType<?> augment(UserType<?> rv)` — package-visible static entry point
- `private static final JavaType JOINTED_MODEL_TYPE` — constant

### 8. Verify StoryTypeComparator Sort Ranks

```bash
grep -c "mapTypeToValue.put" \
  core/ide/src/main/java/org/alice/stageide/StoryTypeComparator.java
```

Expected: **11** entries (4 primitives + 1 SThing + 2 color/paint + 3 spatial + 1 SJoint).

### 9. Verify Removed Imports

Confirm the manager no longer imports types used only by extracted code:

```bash
grep -E "import.*FieldTemplate|import.*Visibility|import.*DynamicResource|import.*JointArrayId|import.*JointId;|import java.lang.reflect.Field" \
  core/ide/src/main/java/org/alice/stageide/StoryApiConfigurationManager.java
```

Expected: **no matches** (all six imports moved to `JointMethodAugmentor`).

### 10. Full Build Smoke Test

To confirm no ripple effects across the project:

```bash
mvn -DfailIfNoTests=false -Dcheckstyle.skip compile
```

## What to Look For if Compilation Fails

| Symptom | Likely Cause |
|---|---|
| `cannot find symbol: StoryTypeComparator` | File not in `org.alice.stageide` package or not created |
| `cannot find symbol: JointMethodAugmentor` | Same as above |
| `cannot find symbol: JOINTED_MODEL_TYPE` in manager | Constant not fully removed from `StoryApiConfigurationManager` or augmentor not imported |
| `incompatible types` on `getTypeComparator()` | `StoryTypeComparator` does not implement `Comparator<AbstractType<?,?,?>>` |
| `cannot find symbol: BIPED_RESOURCE_TYPE` in nonfree | Field accidentally moved out of `StoryApiConfigurationManager` |
| `cannot find symbol: FieldTemplate` in augmentor | Missing import in `JointMethodAugmentor` |
| `cannot find symbol: DynamicResource` in augmentor | Missing import in `JointMethodAugmentor` |
| `method augment not found` | Method not declared `static` or wrong visibility |

## Rollback

If the refactoring needs to be reverted:

```bash
git checkout HEAD -- \
  core/ide/src/main/java/org/alice/stageide/StoryApiConfigurationManager.java
git rm core/ide/src/main/java/org/alice/stageide/StoryTypeComparator.java \
       core/ide/src/main/java/org/alice/stageide/JointMethodAugmentor.java
```

This restores the original 587-line monolith with the inner enum and
augmentation logic inline.
