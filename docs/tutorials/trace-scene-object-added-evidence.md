# Trace the Scene-Object-Added Evidence Hook

This tutorial walks through the scene-object-added evidence hook from the
gallery drag-drop trigger to the JSON artifact on disk. Use it to understand
how the eatme harness verifies the "Building a Scene" lesson step.

For the full contract, see the [Scene-Object-Added Evidence
reference](../reference/scene-object-added-evidence.md).

## Prerequisites

Familiarity with:

- Alice scene editor and gallery drag-drop
- `StorytellingSceneEditor` class hierarchy
- `EatmeRunWindowEvidence` pattern (property-gated JSON evidence)

## Step 1: Trace the trigger

Gallery drag-drop ends at `GalleryDragModel.drop()`, which eventually calls
`StorytellingSceneEditor.addField()`. This is the standard path for adding any
object to the scene — biped models, props, cameras, and markers all flow
through this method.

The method signature:

```java
public void addField(UserType<?> declaringType, UserField field,
                     int index, Statement... statements)
```

- `declaringType` is the active scene type (e.g., `MyScene`).
- `field` is the new `UserField` representing the dropped object.
- `index` is the insertion position in the declared fields list.
- `statements` are initializer statements for the field.

## Step 2: Trace the hook insertion point

Inside `StorytellingSceneEditor.addField()`:

```java
super.addField(declaringType, field, index, statements);
EatmeSceneObjectAddedEvidence.recordObjectAdded(declaringType, field);
lifecycleManager.handleAddField(field);
```

Why this position matters:

1. **After `super.addField()`**: The field is committed to
   `declaringType.getDeclaredFields()`. This guarantees that
   `getDeclaredFields().size()` reflects the post-add count, including the
   new object.

2. **Before `lifecycleManager.handleAddField()`**: If evidence writing fails
   (misconfigured directory, symlink, etc.), the exception propagates before
   the lifecycle manager processes the field. In production (property unset),
   the hook returns immediately and the lifecycle manager runs normally.

## Step 3: Trace the property gate

`recordObjectAdded()` reads `org.alice.eatme.evidenceDir`:

```java
String evidenceDir = System.getProperty("org.alice.eatme.evidenceDir");
if (evidenceDir == null || evidenceDir.isBlank()) {
  return;  // silent no-op — zero production overhead
}
```

This is the same pattern as `EatmeRunWindowEvidence.recordRunWindowCreated()`.
When the property is not set, no AST state is read, no I/O occurs, and no
exception can be thrown. The hook is invisible in production.

## Step 3b: Trace the delegation to writeObjectAdded

When the property is set, `recordObjectAdded` extracts the data and delegates
to the testable core method:

```java
String objectClassName = typeName(field);
int fieldCountAfter = declaringType.getDeclaredFields().size();
writeObjectAdded(Path.of(evidenceDir), objectClassName, fieldCountAfter);
```

`Path.of(evidenceDir)` converts the string property to a `Path`.
`writeObjectAdded` is package-visible so tests can call it directly without
setting a system property. All directory validation, JSON construction, and
atomic writing happen inside `writeObjectAdded`.

## Step 4: Trace the data extraction

The two values passed to `writeObjectAdded` are:

```java
String objectClassName = typeName(field);
int fieldCountAfter = declaringType.getDeclaredFields().size();
```

- **`objectClassName`**: Derived from `field.getValueType().getName()`. Null-safe:
  if `field` is null, `field.getValueType()` is null, or the name is null, the
  result is an empty string `""`.

- **`fieldCountAfter`**: The count of all declared fields on the scene type
  *after* `super.addField()` committed the new field. This includes built-in
  fields (camera, ground) and all user-added fields.

## Step 5: Trace the directory validation

The evidence directory is validated identically to `EatmeRunWindowEvidence`:

```java
Path evidencePath = evidenceDir.toAbsolutePath().normalize();
if (Files.isSymbolicLink(evidencePath)) {
  throw new IOException("evidence path must not be a symbolic link");
}
Path evidenceRoot = evidencePath.toRealPath();
if (!Files.isDirectory(evidenceRoot)) {
  throw new IOException("evidence path is not a directory");
}
```

Symlinks are rejected to prevent evidence from being redirected outside the
intended directory. The directory must already exist — the hook does not create
it.

## Step 6: Trace the artifact path validation

The artifact name `scene-object-added.json` is validated via
`EatmeRunWindowEvidence.artifactPath()`:

```java
Path artifact = EatmeRunWindowEvidence.artifactPath(evidenceRoot,
    "scene-object-added.json");
```

This rejects:
- Absolute paths (`/tmp/scene-object-added.json`)
- Parent traversal (`../scene-object-added.json`)
- Nested paths (`nested/scene-object-added.json`)
- Empty names

Since the artifact name is a compile-time constant, this validation is a
defense-in-depth measure against future refactoring errors.

## Step 7: Trace the JSON construction

The JSON is built with string concatenation (matching the `EatmeRunWindowEvidence`
pattern — no JSON library dependency):

```json
{
  "schema_version": "eatme.alice-scene-object-added/v1",
  "timestamp": "2026-05-13T16:45:00.123Z",
  "object_class_name": "SBiped",
  "scene_field_count_after": 4
}
```

`object_class_name` passes through `EatmeRunWindowEvidence.escapeJson()` to
prevent JSON injection from unusual type names. The other fields are either
constants or integers and need no escaping.

## Step 8: Trace the atomic write

The artifact is written atomically to prevent partial reads:

```java
Path tempArtifact = Files.createTempFile(evidenceRoot, artifactName, ".tmp");
Files.writeString(tempArtifact, content, StandardCharsets.UTF_8);
Files.move(tempArtifact, artifact, ATOMIC_MOVE, REPLACE_EXISTING);
```

Before writing, a symlink check on the artifact path prevents overwriting
symlinks. The temp file is created in the same directory to ensure
`ATOMIC_MOVE` succeeds (same filesystem). A `finally` block cleans up the temp
file if the move fails.

After the atomic move, a post-write verification confirms the artifact exists
and is non-empty:

```java
if (!Files.isRegularFile(artifact, LinkOption.NOFOLLOW_LINKS) || Files.size(artifact) == 0) {
  throw new IOException("Scene-object-added evidence artifact was not written: " + artifact);
}
```

This defense-in-depth check mirrors `EatmeRunWindowEvidence.writeRunWindowCreated()`
(line 68). It catches silent write failures that the atomic move alone cannot
detect — for example, a filesystem that reports success but writes zero bytes.

## Step 9: Trace the test coverage

`EatmeSceneObjectAddedEvidenceTest` uses JUnit 4 with `@Rule TemporaryFolder`:

| Test | What it proves |
| --- | --- |
| Happy path | Writes valid JSON with correct schema version, class name, field count, and ISO timestamp. |
| JSON escaping | Characters like `"`, `\`, `\t`, `\n` in type names are escaped. |
| Null handling | Null value type name produces empty string, not crash. |
| Path traversal | `../malicious.json` is rejected by `artifactPath()`. |
| Symlink rejection | Symlinked evidence directory is rejected. |
| Missing directory | Non-existent directory is rejected. |
| No-op when unconfigured | Without system property, no file is written. |
| Invalid path | Malformed paths surface descriptive errors. |

## Summary

The data flow:

```text
Gallery drag-drop
  └─> StorytellingSceneEditor.addField()
        ├─> super.addField()           [field committed to AST]
        ├─> EatmeSceneObjectAddedEvidence.recordObjectAdded()
        │     ├─> Check system property (no-op if unset)
        │     ├─> Extract object_class_name + field count
        │     ├─> Validate evidence directory (no symlinks)
        │     ├─> Validate artifact path (single segment)
        │     ├─> Build JSON with escapeJson()
        │     ├─> Atomic write (temp + move)
        │     └─> Post-write verification (isRegularFile + size > 0)
        └─> lifecycleManager.handleAddField()
```

Key design choices:

1. **Property-gated**: Zero overhead in production.
2. **Latest-write-wins**: Each add overwrites the artifact — the eatme harness
   checks after each step, not all steps at once.
3. **Reuses `EatmeRunWindowEvidence` helpers**: `artifactPath()` and
   `escapeJson()` are package-visible and shared.
4. **Fail-loud when configured**: If the property is set but the directory is
   invalid, the exception propagates rather than being swallowed.
