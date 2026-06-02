# tools/eatme-reopen-project

Headless CLI hook that reopens a previously saved Alice 3 project (`.a3p`),
verifies the reopen selector method survives the persistence round-trip, and
writes structured evidence artifacts for downstream eatme pipeline stages.

## Synopsis

```bash
tools/eatme-reopen-project \
  --saved-project <path-to-saved.a3p> \
  --reopen-selector scene.<methodName> \
  --evidence-dir <output-directory> \
  --json
```

All four arguments are required.

## Prerequisites

The Alice build must be packaged before running any eatme tool:

```bash
mvn -DincludeSims=false -Dinstall4j.skip clean package -DskipTests
```

This populates `alice-ide/target/lib/` which the launcher requires.

## Arguments

| Argument | Required | Description |
|---|---|---|
| `--saved-project` | Yes | Path to a previously saved `.a3p` project file (typically the output of `eatme-save-project`). May be absolute or relative. |
| `--reopen-selector` | Yes | Qualified scene method selector in `scene.<methodName>` format. The method must exist in the project's scene type. |
| `--evidence-dir` | Yes | Directory where evidence artifacts are written. Created if it does not exist. |
| `--json` | Yes | Emit structured JSON result to stdout. Required for pipeline consumption. |

## How it works

1. **Parse arguments** — validates all four flags are present and well-formed.
2. **Validate saved project** — confirms `--saved-project` points to a regular file.
3. **Extract method name** — strips `scene.` prefix from `--reopen-selector` and
   validates the remainder matches `[A-Za-z_][A-Za-z0-9_]*`.
4. **Read project** — loads the `.a3p` via `IoUtilities.readProject()`.
5. **Verify reopen selector** — finds the scene type (first program field typed
   by an `SScene` subtype), then confirms the named method exists and has no
   required parameters.
6. **Write reopened project** — round-trips the project to
   `<evidence-dir>/reopened.a3p` via `IoUtilities.writeProject()`.
7. **Re-read verification** — reads the freshly written `reopened.a3p` back and
   confirms the reopen selector method is still present. This proves the
   persistence round-trip preserved the method.
8. **Write reopen evidence** — writes `<evidence-dir>/reopen-evidence.json`
   with reopen metadata.
9. **Write reopened state** — writes `<evidence-dir>/reopened-state.json`
   with state verification results.
10. **Emit result JSON** — prints the pipeline result to stdout.

## Output artifacts

### stdout — Pipeline result JSON

Schema: `eatme.alice-project-reopen-result/v1`

```json
{
  "schema_version": "eatme.alice-project-reopen-result/v1",
  "status": "reopened",
  "source_saved_project_artifact": "relative/path/to/saved-project.a3p",
  "reopen_selector": "scene.eatmeFirstLessonStep",
  "reopened_project_artifact": "reopened.a3p",
  "reopen_artifact": "reopen-evidence.json",
  "reopened_state_artifact": "reopened-state.json",
  "state_verification": "passed"
}
```

| Field | Type | Description |
|---|---|---|
| `schema_version` | string | Always `eatme.alice-project-reopen-result/v1`. |
| `status` | string | Always `reopened` on success. |
| `source_saved_project_artifact` | string | The `--saved-project` value as provided (may be absolute or relative). |
| `reopen_selector` | string | The qualified selector that was verified (e.g., `scene.eatmeFirstLessonStep`). |
| `reopened_project_artifact` | string | Simple filename under `--evidence-dir` — always `reopened.a3p`. |
| `reopen_artifact` | string | Simple filename under `--evidence-dir` — always `reopen-evidence.json`. |
| `reopened_state_artifact` | string | Simple filename under `--evidence-dir` — always `reopened-state.json`. |
| `state_verification` | string | Always `passed` — indicates the round-trip preserved the scene method. |

### evidence-dir/reopened.a3p

The round-tripped project archive produced by `IoUtilities.writeProject()`.

### evidence-dir/reopen-evidence.json

Schema: `eatme.alice-project-reopen-artifact/v1`

```json
{
  "schema_version": "eatme.alice-project-reopen-artifact/v1",
  "reopen_selector": "scene.eatmeFirstLessonStep",
  "scene_type": "Scene",
  "method_name": "eatmeFirstLessonStep",
  "reopen_mode": "headless_project_persistence_roundtrip",
  "source_saved_project": "relative/path/to/saved-project.a3p",
  "reopened_project": "reopened.a3p",
  "readable_after_reopen": true
}
```

| Field | Type | Description |
|---|---|---|
| `schema_version` | string | Always `eatme.alice-project-reopen-artifact/v1`. |
| `reopen_selector` | string | The qualified selector. |
| `scene_type` | string | Name of the Alice scene type that owns the method. |
| `method_name` | string | The bare method name extracted from the selector. |
| `reopen_mode` | string | Always `headless_project_persistence_roundtrip`. |
| `source_saved_project` | string | Relative path from run directory to source `.a3p`. |
| `reopened_project` | string | Simple filename of the reopened project artifact. |
| `readable_after_reopen` | boolean | `true` when the round-tripped file was successfully re-read. |

### evidence-dir/reopened-state.json

Schema: `eatme.alice-project-reopen-state/v1`

```json
{
  "schema_version": "eatme.alice-project-reopen-state/v1",
  "scene_type_matches": true,
  "method_present_after_roundtrip": true,
  "state_verification": "passed"
}
```

| Field | Type | Description |
|---|---|---|
| `schema_version` | string | Always `eatme.alice-project-reopen-state/v1`. |
| `scene_type_matches` | boolean | `true` when the scene type in the reopened project matches the original. |
| `method_present_after_roundtrip` | boolean | `true` when the reopen selector method exists in the reopened project. |
| `state_verification` | string | `passed` when all checks succeed. |

## Exit codes

| Code | Meaning |
|---|---|
| 0 | Success — result JSON on stdout, artifacts written. |
| 2 | Argument error or project validation error — message on stderr. |
| 3 | Unexpected runtime error — message on stderr prefixed with `project reopen failed: `. |

## Examples

### Basic reopen after save

```bash
# Step 1: Save the project
tools/eatme-save-project \
  --project my-world/edited.a3p \
  --save-selector scene.eatmeFirstLessonStep \
  --evidence-dir evidence/save \
  --json

# Step 2: Reopen the saved project
tools/eatme-reopen-project \
  --saved-project evidence/save/saved-project.a3p \
  --reopen-selector scene.eatmeFirstLessonStep \
  --evidence-dir evidence/reopen \
  --json
```

Result directory after both steps:

```
evidence/
├── save/
│   ├── saved-project.a3p
│   └── project-save.json
└── reopen/
    ├── reopened.a3p
    ├── reopen-evidence.json
    └── reopened-state.json
```

### Pipeline integration (capture JSON output)

```bash
result=$(tools/eatme-reopen-project \
  --saved-project evidence/save/saved-project.a3p \
  --reopen-selector scene.eatmeFirstLessonStep \
  --evidence-dir evidence/reopen \
  --json)

echo "$result" | python3 -c "
import json, sys
r = json.load(sys.stdin)
assert r['status'] == 'reopened'
assert r['state_verification'] == 'passed'
print('Reopen verified:', r['reopened_project_artifact'])
"
```

### Error: missing method

```bash
tools/eatme-reopen-project \
  --saved-project evidence/save/saved-project.a3p \
  --reopen-selector scene.nonExistentMethod \
  --evidence-dir evidence/reopen \
  --json
# Exit code 2
# stderr: reopen selector does not name a scene method in the project: scene.nonExistentMethod
```

### Error: missing saved project file

```bash
tools/eatme-reopen-project \
  --saved-project /nowhere/missing.a3p \
  --reopen-selector scene.eatmeFirstLessonStep \
  --evidence-dir evidence/reopen \
  --json
# Exit code 2
# stderr: project file does not exist: /nowhere/missing.a3p
```

## Security considerations

- **Path traversal protection**: Artifact filenames are validated to be
  single-segment relative names. Paths containing `..`, absolute paths, or
  multi-segment paths are rejected with `IllegalArgumentException`.
- **JSON injection protection**: All string values are escaped via `escapeJson()`
  which handles `\`, `"`, and all control characters below `U+0020`.
- **Selector injection protection**: Method names must match
  `[A-Za-z_][A-Za-z0-9_]*`, preventing arbitrary input from reaching the
  AST query layer.
- **Input validation**: The saved project file is checked with
  `Files.isRegularFile()` before any read attempt.
- **Output verification**: All written artifacts are verified non-empty after
  write. Missing or zero-byte files cause an `IOException`.
- **No network access**: The tool operates entirely on local files.
- **No credential handling**: No secrets are read or written.
- **Library stdout isolation**: `System.out` is redirected to a silent stream
  during project I/O to prevent library debug output from polluting the JSON
  result.

## Java API

**Class:** `org.alice.tools.EatmeReopenProject`
**Module:** `core/ide`

### Public entry points

```java
// CLI entry point — calls System.exit on failure
public static void main(String[] args)

// Testable entry point — returns exit code, writes to provided streams
static int run(String[] args, PrintStream out, PrintStream err)

// Path validation — rejects traversal attacks
static Path artifactPath(Path evidenceDir, String relativePath)

// JSON string escaping
static String escapeJson(String value)
```

### Internal records

```java
record Arguments(Path savedProject, String reopenSelector, Path evidenceDir)
record ProjectReopen(
    String reopenSelector,
    String sceneType,
    String methodName,
    String sourceSavedProject,
    String reopenedProject,
    String reopenArtifact,
    String reopenedStateArtifact)
```

## Testing

Tests are in `core/ide/src/test/java/org/alice/tools/EatmeReopenProjectTest.java`.

Run with:

```bash
mvn -pl core/ide -am test -Dtest=EatmeReopenProjectTest
```

Test cases:

| Test | What it verifies |
|---|---|
| `reopensProjectAndWritesEatmeProofArtifacts` | Happy path: reads saved `.a3p`, writes all 3 artifacts, produces valid result JSON with correct schema and status. |
| `acceptsDifferentSceneReopenSelectorWhenMethodExists` | Different method name works, JSON contains correct selector. |
| `rejectsMissingSceneMethodWithoutProofArtifacts` | Exit 2 when method not found, no artifacts written. |
| `rejectsArtifactPathThatNormalizesToEvidenceDirectory` | `artifactPath()` rejects `foo/..`. |
| `rejectsParentArtifactPath` | `artifactPath()` rejects `../reopened.a3p`. |
| `rejectsMissingSavedProjectWithoutProofArtifacts` | Exit 2 when `.a3p` does not exist, no artifacts written. |
| `escapesJsonControlCharacters` | `escapeJson()` handles all control chars. |

## Relationship to eatme-save-project

`eatme-reopen-project` is the counterpart to `eatme-save-project`. Together they
prove the save→reopen round-trip:

```
eatme-save-project          eatme-reopen-project
  project.a3p ──────►  saved-project.a3p ──────►  reopened.a3p
  (source)              (evidence/save)            (evidence/reopen)
```

The `source_saved_project_artifact` field in the reopen result JSON links back
to the save step's output, enabling pipeline tools to trace the full provenance
chain.
