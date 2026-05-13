# Scene-Object-Added Evidence

This reference defines the proof hook that records evidence when a student adds
an object to the Alice scene via gallery drag-drop. The hook writes a structured
JSON proof artifact to the eatme evidence directory, enabling the eatme harness
to verify the "Building a Scene" lesson step without external UI automation.

The implementation is `EatmeSceneObjectAddedEvidence` in the `org.alice.tools`
package. It is wired into `StorytellingSceneEditor.addField()` between
`super.addField()` and `lifecycleManager.handleAddField()`, the exact point
where a gallery-dropped object has been committed to the scene type's declared
fields. When the `org.alice.eatme.evidenceDir` system property is unset, the
hook is a silent no-op with zero production overhead.

## Contents

- [Scope](#scope)
- [Usage](#usage)
- [Artifact API](#artifact-api)
- [Java API](#java-api)
- [Configuration](#configuration)
- [Path safety](#path-safety)
- [Evidence schema](#evidence-schema)
- [Wiring location](#wiring-location)
- [Evidence boundaries](#evidence-boundaries)
- [Examples](#examples)

## Scope

The proof covers exactly this scene-modification seam:

```text
Gallery drag-drop
  -> GalleryDragModel.drop()
  -> StorytellingSceneEditor.addField(declaringType, field, index, statements)
  -> super.addField() [field committed to declaringType.getDeclaredFields()]
  -> EatmeSceneObjectAddedEvidence.recordSceneObjectAdded(objectClassName, fieldCountAfter)
  -> scene-object-added.json
  -> lifecycleManager.handleAddField(field)
```

The proof verifies that a field was added to the active scene type's declared
fields and records the object's class name and the post-add field count. It does
not verify rendering, animation, object positioning, camera behavior, gallery
catalog contents, undo/redo, Save, grading, or lesson completion.

## Usage

The hook activates only when the `org.alice.eatme.evidenceDir` system property
points to an existing directory. Run Alice with the property set:

```bash
java -Dorg.alice.eatme.evidenceDir=/tmp/eatme-evidence \
  -jar alice.jar
```

After dragging an object from the gallery into the scene, the proof artifact
appears at:

```text
/tmp/eatme-evidence/scene-object-added.json
```

Each subsequent object-add overwrites the artifact (latest-write-wins).

For automated testing:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar

NODE_OPTIONS=--max-old-space-size=32768 \
mvn -DincludeSims=false -Dinstall4j.skip \
  -pl core/ide -am \
  -DfailIfNoTests=false \
  -Dcheckstyle.skip \
  test
```

## Artifact API

The success artifact is:

```text
scene-object-added.json
```

It is written inside the directory specified by `org.alice.eatme.evidenceDir`.
Each successful object-add overwrites the previous artifact.

Required fields:

| Field | Type | Meaning |
| --- | --- | --- |
| `schema_version` | string | `eatme.alice-scene-object-added/v1`. |
| `timestamp` | number | Epoch milliseconds (`System.currentTimeMillis()`) of the add event. |
| `object_class_name` | string | JSON-escaped value type name of the added field (e.g., `"SBiped"`). Empty string if the type or name is null. |
| `scene_field_count_after` | number | Count of `declaringType.getDeclaredFields()` after `super.addField()` committed the new field. |

## Java API

`EatmeSceneObjectAddedEvidence` is a final utility class in the `org.alice.tools`
package with a private constructor.

### Public API

| Method | Signature | Purpose |
| --- | --- | --- |
| `recordSceneObjectAdded` | `public static void recordSceneObjectAdded(String objectClassName, int sceneFieldCountAfter)` | Entry point called from `StorytellingSceneEditor.addField()`. Reads `org.alice.eatme.evidenceDir`, returns silently if unset/blank, otherwise writes the evidence artifact. Throws `IllegalStateException` wrapping any `IOException`, `SecurityException`, or `IllegalArgumentException` on write failure. |

### Package-visible API

| Method | Signature | Purpose |
| --- | --- | --- |
| `writeObjectAdded` | `static Path writeObjectAdded(Path evidenceDir, String objectClassName, int fieldCountAfter)` | Testable core that validates the directory, builds the JSON, and writes atomically. After the atomic write, performs a post-write verification via `Files.readAttributes()` (single syscall): checks `isRegularFile()` and `size() > 0`. Throws `IOException` if verification fails. Returns the artifact path on success. |

The class reuses package-visible helpers from `EatmeRunWindowEvidence`:

| Helper | Usage |
| --- | --- |
| `EatmeRunWindowEvidence.artifactPath(evidenceDir, relativeName)` | Validates the artifact path is a single relative file name inside the evidence directory. Rejects traversal, nesting, and absolute paths. |
| `EatmeRunWindowEvidence.escapeJson(value)` | JSON-escapes the `object_class_name` so quotes, backslashes, tabs, newlines, carriage returns, and control characters cannot corrupt the artifact shape. |

Directory validation (`validateEvidenceDir`) and atomic writing
(`writeArtifactAtomically`) are replicated locally following the same pattern as
`EatmeRunWindowEvidence`, since those methods are private in the original class.

## Configuration

### Hook configuration

| Setting | Value | Purpose |
| --- | --- | --- |
| `org.alice.eatme.evidenceDir` | Absolute path to an existing directory | Shared eatme evidence directory. When unset or blank, the hook is a silent no-op. |

### Build prerequisites

| Setting | Value | Purpose |
| --- | --- | --- |
| `NODE_OPTIONS` | `--max-old-space-size=32768` | Preserves the repository's Node-backed orchestration memory setting. |
| `tweedle-lang` submodule | Initialized with `git submodule update --init tweedle-lang` | Required before Maven reactor validation. |
| Maven flags | `-DincludeSims=false -Dinstall4j.skip -DfailIfNoTests=false -Dcheckstyle.skip` | Keeps validation bounded to the focused no-Sims `core/ide` characterization surface. |

The system property `org.alice.eatme.evidenceDir` is intentionally distinct from
`org.alice.eatme.runWindowEvidenceDir` used by `EatmeRunWindowEvidence`. Both
hooks can point to the same directory or different directories. When both write
to the same directory, their artifact names (`run-window-created.json` vs
`scene-object-added.json`) are distinct and do not conflict.

## Path safety

The artifact path validation reuses `EatmeRunWindowEvidence.artifactPath()`:

| Unsafe input | Required behavior |
| --- | --- |
| `../scene-object-added.json` | Reject parent traversal. |
| `nested/scene-object-added.json` | Reject nested artifact paths. |
| `/tmp/scene-object-added.json` | Reject absolute paths. |
| Empty artifact name | Reject missing artifact names. |

The `object_class_name` is JSON-escaped via `EatmeRunWindowEvidence.escapeJson()`
before writing so untrusted AST-derived metadata cannot corrupt the artifact shape.

Directory validation checks:

| Check | Behavior |
| --- | --- |
| Symlink on evidence directory path | Rejected with `IOException`. |
| Non-existent directory | Rejected with `IOException`. |
| Symlink on artifact path | Refused to overwrite with `IOException`. |

The atomic write uses `Files.createTempFile` + `Files.move(ATOMIC_MOVE)` with a
symlink pre-check, following the same pattern as `EatmeRunWindowEvidence`.

## Evidence schema

Representative artifact:

```json
{
  "schema_version": "eatme.alice-scene-object-added/v1",
  "timestamp": 1747156700123,
  "object_class_name": "SBiped",
  "scene_field_count_after": 4
}
```

Field semantics:

- `schema_version` is always `eatme.alice-scene-object-added/v1`.
- `timestamp` is generated at write time via `System.currentTimeMillis()` as
  epoch milliseconds (a JSON number, not a string).
- `object_class_name` comes from `field.getValueType().getName()`. When
  `field`, `field.getValueType()`, or the name is null, the value is an empty
  string `""`.
- `scene_field_count_after` comes from `declaringType.getDeclaredFields().size()`
  after `super.addField()` has committed the field. This count includes the
  camera, ground, and all user-added fields declared on the scene type.

## Wiring location

The hook is wired in `StorytellingSceneEditor.addField()`:

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

The call is placed after `super.addField()` so that:

1. The field is already committed to `declaringType.getDeclaredFields()`.
2. `getDeclaredFields().size()` reflects the post-add count.
3. If evidence writing fails, `lifecycleManager.handleAddField()` is not reached
   — this is acceptable because evidence write failure in an eatme-configured
   environment should surface immediately rather than be silently swallowed.

When `org.alice.eatme.evidenceDir` is not set, `recordSceneObjectAdded` returns
immediately without reading any AST state, making the production overhead zero.

## Evidence boundaries

Use this evidence only for scene-object-added detection via the
`StorytellingSceneEditor.addField()` seam. This proof may claim only:

- A field was added to the active scene type's declared fields through the
  gallery drag-drop path.
- The added field's value type name was recorded.
- The post-add declared field count was recorded.
- The evidence was written atomically to the configured evidence directory.

This proof must not claim:

- Rendering correctness of the added object.
- Object positioning, orientation, or animation.
- Camera behavior after object addition.
- Gallery catalog completeness or correctness.
- Undo/redo behavior after object addition.
- Run execution or world execution correctness.
- Active rendering behavior.
- Save behavior.
- Grading, scoring, or creative assessment.
- Lesson completion.
- Full desktop UI automation.
- That the object is visually present in the scene viewport.

Adjacent claims stay in their own lanes:

| Claim | Use instead |
| --- | --- |
| Run-window creation/wiring evidence | [Run-Window Creation/Wiring Contract](./run-window-creation-wiring-contract.md). |
| First-lesson procedure/code-editor action seam | [First-Lesson Code-Editor Action Proof](./first-lesson-code-editor-action-proof.md). |
| Save menu/dialog/write behavior | [Save Menu Dialog Write Proof](./save-menu-dialog-write-proof.md). |
| Scene editor field manager extraction | [Scene-Editor Field Manager Extraction](./scene-editor-field-manager-extraction.md). |

## Examples

### Configure the eatme harness

Set the evidence directory before launching Alice:

```bash
export EATME_EVIDENCE_DIR=/tmp/eatme-evidence
mkdir -p "$EATME_EVIDENCE_DIR"
java -Dorg.alice.eatme.evidenceDir="$EATME_EVIDENCE_DIR" -jar alice.jar
```

### Verify evidence after object add

After dragging an object from the gallery:

```bash
python3 -m json.tool /tmp/eatme-evidence/scene-object-added.json
```

Expected shape:

```json
{
  "schema_version": "eatme.alice-scene-object-added/v1",
  "timestamp": 1747156700123,
  "object_class_name": "SBiped",
  "scene_field_count_after": 4
}
```

### Run the unit tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -DincludeSims=false -Dinstall4j.skip \
  -DfailIfNoTests=false \
  -Dcheckstyle.skip \
  -pl core/ide -am \
  -Dtest=org.alice.tools.EatmeSceneObjectAddedEvidenceTest \
  test
```

### Verify no-op when unconfigured

Without the system property, the hook does nothing:

```bash
mvn -DincludeSims=false -Dinstall4j.skip \
  -DfailIfNoTests=false \
  -Dcheckstyle.skip \
  -pl core/ide -am \
  -Dtest=org.alice.tools.EatmeSceneObjectAddedEvidenceTest \
  test
```

No evidence file is created. The test verifies this explicitly.

### Reviewing the artifact in the eatme harness

The eatme harness reads the artifact and asserts:

```python
import json
with open("scene-object-added.json") as f:
    evidence = json.load(f)

assert evidence["schema_version"] == "eatme.alice-scene-object-added/v1"
assert isinstance(evidence["timestamp"], int)  # epoch millis
assert evidence["object_class_name"]  # non-empty
assert evidence["scene_field_count_after"] >= 1
```
