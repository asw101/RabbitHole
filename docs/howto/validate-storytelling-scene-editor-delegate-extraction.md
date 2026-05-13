# Validate the StorytellingSceneEditor Delegate Extraction

How to verify, extend, or review the extraction of field-management and
camera/marker helper methods from `StorytellingSceneEditor` into
`SceneEditorFieldManager` and `SceneEditorCameraHelper`.

## Prerequisites

- Java 17+ and Maven installed
- `tweedle-lang` submodule initialized:
  ```bash
  git submodule update --init tweedle-lang
  ```

## Step 1: Verify file existence and visibility

Confirm both delegate files exist and are package-private:

```bash
# Files exist
ls core/ide/src/main/java/org/alice/stageide/sceneeditor/SceneEditorFieldManager.java
ls core/ide/src/main/java/org/alice/stageide/sceneeditor/SceneEditorCameraHelper.java

# Package-private (no 'public' modifier on class declaration)
grep '^class SceneEditorFieldManager' \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/SceneEditorFieldManager.java
grep '^final class SceneEditorCameraHelper' \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/SceneEditorCameraHelper.java
```

Expected: Both `grep` commands match (no `public` keyword before `class`).

## Step 2: Verify forwarding stubs on StorytellingSceneEditor

Check that the override methods delegate to `fieldManager`:

```bash
grep -n 'fieldManager\.' \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/StorytellingSceneEditor.java
```

Expected: 6 lines matching `fieldManager.getDoStatementsForCopyField`,
`fieldManager.getDoStatementsForAddField`, `fieldManager.getUndoStatementsForAddField`,
`fieldManager.getRiders`, `fieldManager.getDoStatementsForRemoveField`,
`fieldManager.getUndoStatementsForRemoveField`.

Check that camera methods delegate to `SceneEditorCameraHelper`:

```bash
grep -n 'SceneEditorCameraHelper\.' \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/StorytellingSceneEditor.java
```

Expected: 6 lines matching the static camera/marker helper calls.

## Step 3: Verify asSetVehicleCall is shared

The static `asSetVehicleCall` method lives on `SceneEditorFieldManager` and is
called from both the delegate and `StorytellingSceneEditor`:

```bash
grep -n 'asSetVehicleCall' \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/SceneEditorFieldManager.java \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/StorytellingSceneEditor.java
```

Expected: Definition in `SceneEditorFieldManager`, usage in both files.

## Step 4: Verify removed private methods

Confirm the original private methods no longer exist on SSE:

```bash
grep -n 'private.*asSetVehicleCall\|private.*isSetVehicleInvocation\|private.*replaceReferencesInExpression' \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/StorytellingSceneEditor.java
```

Expected: No output (all three private methods removed from SSE).

## Step 5: Run the characterization tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/ide -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dcheckstyle.skip \
  -Dtest=StorytellingSceneEditorCharacterizationTest \
  test
```

Expected: 80 tests, all pass. The reflection-based tests see the same public
API surface because the forwarding stubs preserve method signatures exactly.

## Step 6: Run the full core/ide test suite

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -DincludeSims=false -Dinstall4j.skip \
  -pl core/ide -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dcheckstyle.skip \
  test
```

Expected: 414+ tests, 0 failures, 0 errors.

## Step 7: Verify line counts

```bash
wc -l core/ide/src/main/java/org/alice/stageide/sceneeditor/StorytellingSceneEditor.java \
     core/ide/src/main/java/org/alice/stageide/sceneeditor/SceneEditorFieldManager.java \
     core/ide/src/main/java/org/alice/stageide/sceneeditor/SceneEditorCameraHelper.java
```

Expected:
- `StorytellingSceneEditor.java`: ~1072 lines (down from 1259)
- `SceneEditorFieldManager.java`: ~257 lines
- `SceneEditorCameraHelper.java`: ~91 lines

## Adding a new field-management method

If `AbstractSceneEditor` gains a new field-management override:

1. Add the implementation to `SceneEditorFieldManager`.
2. Add a forwarding stub on `StorytellingSceneEditor` with the `@Override`.
3. Run steps 5–7 above.

## Adding a new camera/marker helper

If a new stateless camera utility is needed:

1. Add the static method to `SceneEditorCameraHelper`.
2. Add a forwarding public method on `StorytellingSceneEditor`.
3. Add a characterization test for the new public method
   (see [StorytellingSceneEditor Characterization](../reference/storytelling-scene-editor-characterization.md)).
4. Run steps 5–7 above.

## Review checklist

- [ ] Both delegates are package-private (not public)
- [ ] `SceneEditorCameraHelper` is `final` with private constructor
- [ ] `SceneEditorFieldManager` has a `final AbstractSceneEditor` field
- [ ] All `@Override` annotations remain on `StorytellingSceneEditor`
- [ ] No new public methods added to `StorytellingSceneEditor`
- [ ] 80 characterization tests pass
- [ ] 414+ core/ide tests pass
- [ ] `StorytellingSceneEditor` under 1100 lines
