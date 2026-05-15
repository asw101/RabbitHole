# SaveProofJsonDelegate — Evidence JSON Extraction

## Overview

`SaveProofJsonDelegate` is a package-private utility class that owns the ten
JSON section builders and two blocker-inference helpers (twelve methods total)
previously inlined in `EvidenceJsonWriter`.  The extraction reduces `EvidenceJsonWriter` from 587
lines to ~426 lines while preserving byte-identical JSON output.

**Module:** `core/ide`  
**Package:** `org.alice.ide.croquet.models.projecturi`  
**Issue:** #684  

---

## Motivation

`EvidenceJsonWriter` had grown to 587 lines—well past the 500-line threshold
used by the Alice modernization project.  The "save-proof" section builders
(lines 424–586) form a cohesive group: they accept a `SaveProofSnapshot` and
produce JSON fragments that are concatenated by `saveProofJson()`.  Extracting
them into their own class keeps `EvidenceJsonWriter` focused on coordination and
the core utilities (`escapeJson`, `stringJson`, `nullToBlank`) that every JSON
builder depends on.

---

## Architecture

```
EvidenceJsonWriter (≤500 lines)
 ├── SaveProofSnapshot          (record — stays here)
 ├── escapeJson / stringJson / nullToBlank  (core utilities — stay here)
 ├── saveActionInvocationProofJson (save-action JSON — stays here)
 ├── saveProofJson()            (orchestrator — stays here, delegates below)
 └── ...other saved-file/completion JSON methods

SaveProofJsonDelegate (new, ~175 lines)
 ├── headerJson()
 ├── blockerJson()
 ├── menuJson()
 ├── dialogJson()
 ├── controlJson()
 ├── writeJson()
 ├── readbackJson()
 ├── baselinePreservedJson()
 ├── requiresNextEvidenceJson()
 ├── doesNotClaimJson()
 ├── inferBlockerKind()
 └── inferBlockerObserved()
```

### Key design decisions

| Decision | Rationale |
|----------|-----------|
| **Package-private, `final`, private constructor** | Not part of the public API; construction is meaningless for a static-method-only utility |
| **Drop `saveProof` prefix** | The class name already scopes to save-proof; `SaveProofJsonDelegate.headerJson()` reads better than `SaveProofJsonDelegate.saveProofHeaderJson()` |
| **Delegate to `EvidenceJsonWriter.escapeJson()`** | Zero duplicated escaping logic; the delegate never has its own escape implementation |
| **`SaveProofSnapshot` stays on `EvidenceJsonWriter`** | The record is shared with `saveProofJson()` and future callers; moving it would create a circular reference |
| **`java.time.Instant` moves to delegate** | Only `headerJson()` calls `Instant.now()`; removing the import from `EvidenceJsonWriter` keeps its import list minimal |
| **Preserve `String.format("\\u%04x")` escaping** | Main-branch `String.format` approach kept (not the worktree's `HEX` char-array variant); a fast-path early-return was added for strings that need no escaping |

---

## API Reference

All methods are `static` and package-private. The class is in the same package
as `EvidenceJsonWriter` and `SaveOperationCompletionEvidence`.

### JSON Section Builders

#### `headerJson(String status, boolean proven, SaveProofSnapshot snap)`

Returns the opening JSON fields: `schemaVersion`, `scenario`, `workflow`,
`runId`, `generatedAtUtc`, `status`, `proofTarget`, and either a `claim`
(when proven) or `reportingSummary` (when blocked).

**Example output (proven):**
```json
  "schemaVersion": "eatme.alice-desktop-save-menu-dialog-write-readback-proof/v1",
  "scenario": "StageIdeSaveMenuDoClickToWriteProofTest",
  "workflow": "save-menu-dialog-write-proof",
  "runId": "abc-123",
  "generatedAtUtc": "2026-05-15T20:30:00Z",
  "status": "proven",
  "proofTarget": "single rendered desktop Save path: menu, dialog, control, write, readback",
  "claim": "AWT Robot opened File, clicked the production Save menu item, ...",
```

#### `blockerJson(boolean proven, String blockerKind, String blockerObserved, String blockerRequired)`

Returns `"blocker": null,` when proven, or a JSON object with `kind`,
`observed`, and `required` when blocked.

#### `menuJson(SaveProofSnapshot snap)`

Returns the `"menu"` object: `fileMenuOpened`, `saveMenuItemInvoked`,
`saveActionIdentityMatched`.

#### `dialogJson(SaveProofSnapshot snap)`

Returns the `"dialog"` object: `saveDialogObserved`, `dialogType`,
`dialogClass`, `dialogShowing`, `ambiguousChooserDiscovery`, `pollCount`.

#### `controlJson(SaveProofSnapshot snap, Path selectedPath, boolean selectedFileMatchesExpected)`

Returns the `"control"` object.  Paths are redacted via
`EvidenceFileOperations.proofRelativePath()` — no absolute paths appear in
output.

#### `writeJson(boolean fileExists, boolean fileNonempty, boolean fileHasExpectedExtension, long fileSizeBytes, SaveProofSnapshot snap)`

Returns the `"write"` object with file-existence and size fields.

#### `readbackJson(SaveProofSnapshot snap)`

Returns the `"readback"` object: `projectReadable`, `marker`, `markerPresent`.

#### `baselinePreservedJson()`

Returns the `"baselinePreserved"` array listing the three baseline test classes.

#### `requiresNextEvidenceJson()`

Returns the `"requiresNextEvidence"` array describing conditions for further
evidence collection.

#### `doesNotClaimJson()`

Returns the `"doesNotClaim"` array enumerating what the save proof explicitly
does not cover (Save As, all variants, grading, etc.).

### Blocker Inference Helpers

#### `inferBlockerKind(SaveProofSnapshot snap, boolean observedWrite)`

Returns a string identifying the first failing step in the save-proof pipeline.
Evaluation order mirrors the proof pipeline: file menu → save item → dialog →
ambiguity → control → target path → write → readback → marker.

**Return values (in priority order):**

| Value | Meaning |
|-------|---------|
| `file_menu_not_showing` | Robot could not open the File menu |
| `save_item_not_attributed` | Save click not attributed to Robot |
| `dialog_not_observed` | No JFileChooser found |
| `ambiguous_chooser_discovery` | Multiple JFileChoosers found |
| `chooser_control_failed` | Dialog not showing / not controlled |
| `target_path_rejected` | Path outside proof root or wrong extension |
| `write_not_observed` | No file written at target |
| `readback_failed` | Written file unreadable as Alice project |
| `marker_missing` | Readback succeeded but marker not found |

#### `inferBlockerObserved(SaveProofSnapshot snap, boolean observedWrite)`

Returns a human-readable description of the blocker condition. Same evaluation
order as `inferBlockerKind`.

---

## Usage

### How `EvidenceJsonWriter.saveProofJson()` calls the delegate

After extraction, `saveProofJson()` replaces twelve inline method calls with
qualified static calls to `SaveProofJsonDelegate`:

```java
// Before (EvidenceJsonWriter, internal calls):
return "{\n"
    + saveProofHeaderJson(status, proven, snap)
    + saveProofBlockerJson(proven, blockerKind, blockerObserved, blockerRequired)
    + saveProofMenuJson(snap)
    // ...

// After (EvidenceJsonWriter delegates to SaveProofJsonDelegate):
return "{\n"
    + SaveProofJsonDelegate.headerJson(status, proven, snap)
    + SaveProofJsonDelegate.blockerJson(proven, blockerKind, blockerObserved, blockerRequired)
    + SaveProofJsonDelegate.menuJson(snap)
    // ...
```

The blocker-inference calls in the same method follow the same pattern:

```java
// Before:
blockerKind = inferBlockerKind(snap, observedWrite);
blockerObserved = inferBlockerObserved(snap, observedWrite);

// After:
blockerKind = SaveProofJsonDelegate.inferBlockerKind(snap, observedWrite);
blockerObserved = SaveProofJsonDelegate.inferBlockerObserved(snap, observedWrite);
```

### What stays on `EvidenceJsonWriter`

- `SaveProofSnapshot` record (lines 20–44)
- Core utilities: `escapeJson()`, `stringJson()`, `nullToBlank()`, `className()`,
  `operationSimpleName()`, `status()`
- Saved-file JSON builders: `savedFileJson()`, `savedFileExistsJson()`,
  `savedFileSizeJson()`, `wroteFile()`, `hasExtension()`
- Result artifact JSON: `resultJson()`, `resultClaimOrSummaryJson()`,
  `nonEmptyProjectFileWrite()`
- Dialog control target JSON: `dialogControlTargetJson()`
- Save-action JSON: `saveActionInvocationProofJson()`, `saveActionInvocationReason()`
- Save-action private helpers: `saveActionObserved()`, `saveActionReportingSummary()`,
  `saveActionRequiresNextEvidenceJson()`, `saveActionDoesNotClaimJson()`
- The `saveProofJson()` orchestrator itself

---

## Testing

### Existing tests — EvidenceJsonWriterTest (548 lines)

The existing 548-line test suite (`EvidenceJsonWriterTest.java`) continues to
pass unchanged.  It exercises `saveProofJson()` through `EvidenceJsonWriter`'s
public-facing API, so the delegation is transparent.  No test changes are
needed.

### New tests — SaveProofJsonDelegateTest

A new test class covers all twelve delegate methods directly:

| Test method | What it verifies |
|-------------|------------------|
| `headerJsonProvenContainsClaim` | Proven status includes `claim` field |
| `headerJsonBlockedContainsReportingSummary` | Blocked status includes `reportingSummary` |
| `headerJsonEscapesScenario` | Special characters in scenario are escaped |
| `blockerJsonNullWhenProven` | Returns `"blocker": null` for proven state |
| `blockerJsonObjectWhenBlocked` | Returns blocker object with kind/observed/required |
| `menuJsonReflectsSnapshot` | Boolean fields map correctly from snapshot |
| `dialogJsonIncludesDialogType` | Always outputs `"dialogType": "Swing JFileChooser"` |
| `dialogJsonIncludesPollCount` | Numeric `pollCount` appears in output |
| `controlJsonRedactsPaths` | Paths go through `proofRelativePath()` |
| `writeJsonIncludesSize` | `outputSizeBytes` field present |
| `readbackJsonIncludesMarker` | Marker constant appears in output |
| `baselinePreservedJsonListsThreeTests` | Lists exactly three baseline test classes |
| `requiresNextEvidenceJsonNonEmpty` | Array contains guidance items |
| `doesNotClaimJsonListsExclusions` | Array contains exclusion items |
| `inferBlockerKindFileMenuFirst` | File menu failure takes priority |
| `inferBlockerKindMarkerLast` | Marker missing is lowest priority |
| `inferBlockerObservedMatchesKind` | Each kind has a corresponding human message |

### Running the tests

```bash
# Run both test classes
mvn test -pl core/ide \
  -Dtest="EvidenceJsonWriterTest,SaveProofJsonDelegateTest" \
  -DfailIfNoTests=false

# Verify line count
wc -l core/ide/src/main/java/org/alice/ide/croquet/models/projecturi/EvidenceJsonWriter.java
# Expected: ≤500
```

---

## Security Considerations

| Concern | Mitigation |
|---------|------------|
| JSON injection | All string values pass through `EvidenceJsonWriter.escapeJson()` — no new escape logic in delegate |
| Path disclosure | Paths redacted via `EvidenceFileOperations.proofRelativePath()` — delegate never calls `Path.toAbsolutePath()` |
| Visibility | Package-private; `final` class with `private` constructor — not accessible outside the package |
| I/O | Delegate performs zero I/O — pure `String`/`StringBuilder` operations only |
| Credentials | No credentials, tokens, or PII in output — only structural proof metadata |

---

## Configuration

No new configuration is required.  The delegate is a pure refactoring — it
reads no system properties, environment variables, or config files.

The existing `SaveOperationCompletionEvidence` constants used by the delegate
(`SAVE_PROOF_SCHEMA_VERSION`, `SAVE_PROOF_WORKFLOW`, `SAVE_PROOF_MARKER`) are
unchanged.

---

## Migration Notes

- **Binary compatible**: No public API changes.  `EvidenceJsonWriter`'s
  package-private static methods retain the same signatures and behavior.
- **JSON output identical**: The extraction is purely structural.  Every byte of
  JSON output is identical before and after.
- **Import changes**: `java.time.Instant` moves from `EvidenceJsonWriter` to
  `SaveProofJsonDelegate`.  `java.nio.file.Path` is also imported in the
  delegate (`controlJson` accepts a `Path` parameter).  `java.io.File` and
  `java.nio.file.Path` remain on `EvidenceJsonWriter` as well.
- **No downstream changes**: No callers outside the package reference the
  extracted methods (they were `private`).
