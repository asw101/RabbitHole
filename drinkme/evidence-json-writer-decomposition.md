# EvidenceJsonWriter decomposition into SaveProofJsonDelegate

The `org.alice.ide.croquet.models.projecturi.EvidenceJsonWriter` class has been decomposed from a 587-line monolith into two focused classes. A new helper class — `SaveProofJsonDelegate` — now owns the save-proof JSON section assembly and blocker inference that previously inflated `EvidenceJsonWriter` beyond its core purpose of general-purpose evidence JSON fragment building.

After decomposition, `EvidenceJsonWriter` is under 425 lines and contains only shared utilities, the `SaveProofSnapshot` record, result/dialog-control/save-action-invocation JSON builders, and the thin `saveProofJson()` facade. The new class holds a single coherent responsibility (save-proof section building) and lives in the same package (`org.alice.ide.croquet.models.projecturi`), requiring no module or POM changes.

## Finished behavior

### SaveProofJsonDelegate

`SaveProofJsonDelegate` owns the 12 private methods that build individual JSON object sections for the save-proof evidence document and the two blocker-inference methods that determine why a save proof was blocked. The class is `final`, package-private, and non-instantiable (private constructor). All methods are `static` and package-private.

| Method | Purpose |
| --- | --- |
| `headerJson(String status, boolean proven, SaveProofSnapshot snap)` | Builds the top-level header section: `schemaVersion`, `scenario`, `workflow`, `runId`, `generatedAtUtc`, `status`, `proofTarget`, and either a `claim` (when proven) or `reportingSummary` (when blocked). References `SaveOperationCompletionEvidence.SAVE_PROOF_SCHEMA_VERSION` and `SAVE_PROOF_WORKFLOW` constants. |
| `blockerJson(boolean proven, String blockerKind, String blockerObserved, String blockerRequired)` | Builds the `blocker` section. Returns `"blocker": null` when proven; otherwise returns a `blocker` object with `kind`, `observed`, and `required` fields. All string values pass through `EvidenceJsonWriter.escapeJson()`. |
| `menuJson(SaveProofSnapshot snap)` | Builds the `menu` section with `fileMenuOpened`, `saveMenuItemInvoked`, and `saveActionIdentityMatched` booleans. |
| `dialogJson(SaveProofSnapshot snap)` | Builds the `dialog` section with `saveDialogObserved`, `dialogType`, `dialogClass`, `dialogShowing`, `ambiguousChooserDiscovery`, and `pollCount`. |
| `controlJson(SaveProofSnapshot snap, Path selectedPath, boolean selectedFileMatchesExpected)` | Builds the `control` section with `selectedPathSet`, `approvedSelection`, `selectedPathMatchesExpected`, `targetInsideProofRoot`, `normalizedSelectedPath`, and `expectedPath`. File paths pass through `EvidenceFileOperations.proofRelativePath()` to prevent absolute path leakage. |
| `writeJson(boolean fileExists, boolean fileNonempty, boolean fileHasExpectedExtension, long fileSizeBytes, SaveProofSnapshot snap)` | Builds the `write` section with `fileWritten`, `fileNonempty`, `fileHasExpectedExtension`, `outputPath`, and `outputSizeBytes`. |
| `readbackJson(SaveProofSnapshot snap)` | Builds the `readback` section with `projectReadable`, `marker` (the constant `SAVE_PROOF_MARKER`), and `markerPresent`. |
| `baselinePreservedJson()` | Returns the static `baselinePreserved` JSON array listing the three characterization test classes that must remain green. |
| `requiresNextEvidenceJson()` | Returns the static `requiresNextEvidence` JSON array describing the conditions for upgrading a blocked proof. |
| `doesNotClaimJson()` | Returns the static `doesNotClaim` JSON array listing eight coverage scopes explicitly excluded from the save-proof claim. |
| `inferBlockerKind(SaveProofSnapshot snap, boolean observedWrite)` | Walks the proof chain in order (menu → save item → dialog → ambiguity → control → path → write → readback → marker) and returns the first failing step as a machine-readable blocker kind string. Returns `"marker_missing"` as the final fallback. |
| `inferBlockerObserved(SaveProofSnapshot snap, boolean observedWrite)` | Mirrors `inferBlockerKind` but returns a human-readable description of what was actually observed at the failing step. |

**Migration**: All 12 methods moved verbatim from `EvidenceJsonWriter` lines 424–586. The only change is visibility: the methods were `private static` on `EvidenceJsonWriter` and are now package-private `static` on `SaveProofJsonDelegate`. Method names drop the `saveProof` prefix since the class name already scopes them (e.g., `saveProofHeaderJson` → `headerJson`).

**Imports**: `SaveProofJsonDelegate` requires `java.time.Instant` (used by `headerJson` for `Instant.now()`) and `java.nio.file.Path` (parameter type for `controlJson`). Same-package classes — `EvidenceJsonWriter`, `EvidenceFileOperations`, `SaveOperationCompletionEvidence` — need no import statements.

**String escaping**: All JSON string interpolation continues through `EvidenceJsonWriter.escapeJson()` and `EvidenceJsonWriter.stringJson()`. `SaveProofJsonDelegate` calls both utility methods as `EvidenceJsonWriter.escapeJson(...)` / `EvidenceJsonWriter.stringJson(...)` (same package, package-private access). No second escape path is introduced.

**File path safety**: All file paths in the JSON output continue through `EvidenceFileOperations.proofRelativePath()`, which strips absolute path prefixes. `SaveProofJsonDelegate` performs no file I/O — file operations remain in `EvidenceFileOperations`.

### EvidenceJsonWriter (after decomposition)

`EvidenceJsonWriter` retains:

- **SaveProofSnapshot record** (lines 20–44): The record stays on `EvidenceJsonWriter` to preserve its qualified name `EvidenceJsonWriter.SaveProofSnapshot`, used by `SaveOperationCompletionEvidence` to construct snapshots. No callers change.
- **Core utilities**: `escapeJson(String)`, `stringJson(String)`, `nullToBlank(String)`, `className(Object)`, `operationSimpleName(String)`, `status(Result)`.
- **Saved-file JSON fragments**: `savedFileJson(File)`, `savedFileExistsJson(File, boolean)`, `savedFileSizeJson(Long)`, `wroteFile(Path, RegularFileState, String)`, `hasExtension(Path, String)`.
- **Result artifact JSON**: `resultJson(...)`, `resultClaimOrSummaryJson(...)`, `nonEmptyProjectFileWrite(String)`.
- **Dialog control target JSON**: `dialogControlTargetJson(...)`.
- **Save action invocation proof JSON**: `saveActionInvocationProofJson(...)`, `saveActionInvocationReason(...)` (both package-private), and the four private helpers `saveActionObserved(String)`, `saveActionReportingSummary(String)`, `saveActionRequiresNextEvidenceJson(String)`, `saveActionDoesNotClaimJson(InvocationTrigger)`.
- **Save proof facade**: `saveProofJson(SaveProofSnapshot)` — the method body retains all local variable computation (path normalization, file state queries, proven-flag evaluation, inline blocker inference fallback) and delegates the 10 section-building calls and 2 blocker-inference calls to `SaveProofJsonDelegate.*`.

The `saveProofJson()` method body changes only in its return statement and its two `inferBlocker*` calls. Where it previously called `saveProofHeaderJson(...)`, it now calls `SaveProofJsonDelegate.headerJson(...)`. The local computation (lines 310–347) is unchanged.

### Save action invocation methods (lines 245–420, unchanged)

The save-action-invocation-proof group — two package-private entry points (`saveActionInvocationProofJson` at L245, `saveActionInvocationReason` at L292) and four private helpers (`saveActionObserved`, `saveActionReportingSummary`, `saveActionRequiresNextEvidenceJson`, `saveActionDoesNotClaimJson` at L364–420) — stays on `EvidenceJsonWriter`. They serve a different JSON schema (`eatme.alice-desktop-save-action-invocation-proof/v1`) and have no relationship to the save-proof section builders.

## Public API changes

**None.** All package-private and public methods on `EvidenceJsonWriter` retain their original signatures. The only callers — `SaveOperationCompletionEvidence` and the test class `EvidenceJsonWriterTest` — continue to call the same methods on `EvidenceJsonWriter`. They are unaware that `saveProofJson()` now delegates internally to `SaveProofJsonDelegate`. The new class is package-private and is not part of the public API.

## File inventory

| File | Lines (approx) | Status |
| --- | --- | --- |
| `core/ide/src/main/java/org/alice/ide/croquet/models/projecturi/EvidenceJsonWriter.java` | ~422 | Modified (12 private methods removed, `saveProofJson` updated to delegate) |
| `core/ide/src/main/java/org/alice/ide/croquet/models/projecturi/SaveProofJsonDelegate.java` | ~175 | New |

## Design constraints

| Constraint | Rationale |
| --- | --- |
| `SaveProofJsonDelegate` is package-private, `final`, with a private constructor | Mirrors `EvidenceJsonWriter`'s static-utility pattern. No public API surface. Not intended for subclassing or external use. |
| Methods are package-private `static`, not `public` | Only `EvidenceJsonWriter.saveProofJson()` calls them. Minimal visibility. |
| No file I/O in `SaveProofJsonDelegate` | File operations stay in `EvidenceFileOperations`. The delegate is a pure string builder. |
| All JSON strings through `EvidenceJsonWriter.escapeJson()` | Single escape path. No second JSON encoding utility introduced. |
| All file paths through `EvidenceFileOperations.proofRelativePath()` | Prevents absolute path leakage into evidence JSON. |
| `SaveProofSnapshot` stays on `EvidenceJsonWriter` | Preserves the qualified name used by callers. Moving it would change the import in `SaveOperationCompletionEvidence`. |

## API reference

### SaveProofJsonDelegate

```
package org.alice.ide.croquet.models.projecturi;

import java.nio.file.Path;
import java.time.Instant;

final class SaveProofJsonDelegate {
  private SaveProofJsonDelegate() {}

  static String headerJson(String status, boolean proven,
      EvidenceJsonWriter.SaveProofSnapshot snap)
  static String blockerJson(boolean proven, String blockerKind,
      String blockerObserved, String blockerRequired)
  static String menuJson(EvidenceJsonWriter.SaveProofSnapshot snap)
  static String dialogJson(EvidenceJsonWriter.SaveProofSnapshot snap)
  static String controlJson(EvidenceJsonWriter.SaveProofSnapshot snap,
      Path selectedPath, boolean selectedFileMatchesExpected)
  static String writeJson(boolean fileExists, boolean fileNonempty,
      boolean fileHasExpectedExtension, long fileSizeBytes,
      EvidenceJsonWriter.SaveProofSnapshot snap)
  static String readbackJson(EvidenceJsonWriter.SaveProofSnapshot snap)
  static String baselinePreservedJson()
  static String requiresNextEvidenceJson()
  static String doesNotClaimJson()
  static String inferBlockerKind(
      EvidenceJsonWriter.SaveProofSnapshot snap, boolean observedWrite)
  static String inferBlockerObserved(
      EvidenceJsonWriter.SaveProofSnapshot snap, boolean observedWrite)
}
```

## Design rationale

### Why a delegate class instead of a region comment

A 587-line class with two unrelated JSON schemas (save-action-invocation-proof and save-proof) has high cognitive load. Extracting the 12 section builders into a separate class makes `EvidenceJsonWriter` scannable: the save-proof assembly becomes a single delegation call-site, and the delegate class is self-contained and independently readable.

### Why not an interface or abstract class

All methods are stateless `static` utilities. There is no polymorphic behavior, no instance state, and no inheritance contract. A `final class` with a private constructor is the correct Java idiom.

### Why same package

`SaveProofJsonDelegate` calls `EvidenceJsonWriter.escapeJson()`, `EvidenceJsonWriter.stringJson()`, `EvidenceJsonWriter.nullToBlank()`, and `EvidenceFileOperations.proofRelativePath()` — all package-private. Moving the delegate to a sub-package would require widening visibility on these utility methods.

### Why no migration guide

Unlike `AstUtilities` where extracted methods had cross-package callers, the 12 save-proof section builders were `private` and had exactly one caller (`saveProofJson`). No external code references them. A migration guide would have zero entries.

## Validation

1. `mvn compile -pl core/ide` — compilation succeeds with no new warnings.
2. `mvn test -pl core/ide -Dtest=EvidenceJsonWriterTest` — all existing tests pass unchanged.
3. `wc -l EvidenceJsonWriter.java` — under 425 lines (target: under 500).
4. `wc -l SaveProofJsonDelegate.java` — approximately 175 lines.
5. No new public API surface. `SaveProofJsonDelegate` is invisible to modules outside `core/ide`.

## Example: how saveProofJson() delegates

Before (single class):

```java
// EvidenceJsonWriter.java — return statement in saveProofJson()
return "{\n"
    + saveProofHeaderJson(status, proven, snap)
    + saveProofBlockerJson(proven, blockerKind, blockerObserved, blockerRequired)
    + saveProofMenuJson(snap)
    + saveProofDialogJson(snap)
    + saveProofControlJson(snap, selectedPath, selectedFileMatchesExpected)
    + saveProofWriteJson(fileExists, fileNonempty, fileHasExpectedExtension, fileSizeBytes, snap)
    + saveProofReadbackJson(snap)
    + saveProofBaselinePreservedJson()
    + saveProofRequiresNextEvidenceJson()
    + saveProofDoesNotClaimJson()
    + "}\n";
```

After (delegate):

```java
// EvidenceJsonWriter.java — return statement in saveProofJson()
return "{\n"
    + SaveProofJsonDelegate.headerJson(status, proven, snap)
    + SaveProofJsonDelegate.blockerJson(proven, blockerKind, blockerObserved, blockerRequired)
    + SaveProofJsonDelegate.menuJson(snap)
    + SaveProofJsonDelegate.dialogJson(snap)
    + SaveProofJsonDelegate.controlJson(snap, selectedPath, selectedFileMatchesExpected)
    + SaveProofJsonDelegate.writeJson(fileExists, fileNonempty, fileHasExpectedExtension, fileSizeBytes, snap)
    + SaveProofJsonDelegate.readbackJson(snap)
    + SaveProofJsonDelegate.baselinePreservedJson()
    + SaveProofJsonDelegate.requiresNextEvidenceJson()
    + SaveProofJsonDelegate.doesNotClaimJson()
    + "}\n";
```

The blocker-inference calls in the same method also change:

```java
// Before:
blockerKind = inferBlockerKind(snap, observedWrite);
blockerObserved = inferBlockerObserved(snap, observedWrite);

// After:
blockerKind = SaveProofJsonDelegate.inferBlockerKind(snap, observedWrite);
blockerObserved = SaveProofJsonDelegate.inferBlockerObserved(snap, observedWrite);
```

No other code in the repository changes.
