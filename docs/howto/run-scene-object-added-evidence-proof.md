# Run the Scene-Object-Added Evidence Proof

Use this guide to run and review the unit tests for the scene-object-added
proof hook that records evidence when a student adds an object to the Alice
scene via gallery drag-drop.

For the full contract, see the [Scene-Object-Added Evidence
reference](../reference/scene-object-added-evidence.md).

## Before you start

Run commands from the repository root:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
export NODE_OPTIONS=--max-old-space-size=32768
```

## Run the focused proof

Run the unit tests for the evidence hook:

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -DincludeSims=false -Dinstall4j.skip \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dcheckstyle.skip \
  -pl core/ide -am \
  -Dtest=org.alice.tools.EatmeSceneObjectAddedEvidenceTest \
  test
```

Expected output includes:

```text
Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
```

This command does not use a QA workflow timeout. Do not add `--timeout-seconds`,
sleeps, polling loops, or timeout-based success criteria.

## Run the full core/ide test suite

Verify the hook does not break existing tests:

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -DincludeSims=false -Dinstall4j.skip \
  -pl core/ide -am \
  -DfailIfNoTests=false \
  -Dcheckstyle.skip \
  test
```

## Review the proof

Review the test class `EatmeSceneObjectAddedEvidenceTest` for these assertions:

| Assertion | Meaning |
| --- | --- |
| Happy path writes valid JSON | `writeObjectAdded()` creates `scene-object-added.json` with correct schema, class name, and field count. |
| JSON escaping | Special characters in `object_class_name` (quotes, backslashes, tabs, newlines) are escaped without corrupting artifact shape. |
| Null object class name | A null value type name produces an empty string, not a null or exception. |
| Path traversal rejection | Artifact paths containing `../` are rejected with `IllegalArgumentException`. |
| Symlink rejection | Symlinked evidence directories are rejected with `IOException`. |
| Missing directory | A non-existent evidence directory is rejected with `IOException`. |
| No-op when unconfigured | When `org.alice.eatme.evidenceDir` is not set, `recordSceneObjectAdded()` returns silently and writes nothing. |
| Invalid path surfacing | Malformed evidence directory paths produce clear error messages. |

## Review the wiring

Verify that `StorytellingSceneEditor.addField()` calls the hook:

```java
@Override
public void addField(UserType<?> declaringType, UserField field, int index, Statement... statements) {
  super.addField(declaringType, field, index, statements);
  String objectClassName = field.getValueType() != null ? field.getValueType().getName() : null;
  int fieldCountAfter = declaringType.getDeclaredFields().size();
  EatmeSceneObjectAddedEvidence.recordSceneObjectAdded(objectClassName, fieldCountAfter);
  lifecycleManager.handleAddField(field);
}
```

Confirm:

1. The call is after `super.addField()` — the field is already on the type's
   declared fields.
2. The call is before `lifecycleManager.handleAddField()` — evidence failure
   surfaces before the lifecycle manager runs.
3. The import `org.alice.tools.EatmeSceneObjectAddedEvidence` is present.

## Review the evidence artifact

The artifact `scene-object-added.json` must contain:

```text
schema_version=eatme.alice-scene-object-added/v1
object_class_name=<value type name>
scene_field_count_after=<positive integer>
timestamp=<epoch milliseconds>
```

Validate a generated artifact:

```bash
python3 -m json.tool /path/to/evidence/scene-object-added.json
```

## Keep the claim narrow

Accept this proof only as scene-object-added evidence via the
`StorytellingSceneEditor.addField()` seam. Do not cite it for:

- Rendering correctness of the added object.
- Object positioning, orientation, or animation.
- Camera behavior after object addition.
- Gallery catalog completeness.
- Undo/redo behavior.
- Run execution or world execution correctness.
- Active rendering behavior.
- Save behavior.
- Grading, creative assessment, or lesson completion.
- Full desktop UI automation.

Adjacent claims remain owned by their own documents:

| Claim | Use |
| --- | --- |
| Run-window creation/wiring evidence | [Run-Window Creation/Wiring Contract](../reference/run-window-creation-wiring-contract.md). |
| First-lesson code-editor action seam | [First-Lesson Code-Editor Action Proof](../reference/first-lesson-code-editor-action-proof.md). |
| Save menu/dialog/write behavior | [Save Menu Dialog Write Proof](../reference/save-menu-dialog-write-proof.md). |
| Scene editor field manager extraction | [Scene-Editor Field Manager Extraction](../reference/scene-editor-field-manager-extraction.md). |
