# Formal Specification Lane

The formal-spec lane is the documentation and characterization layer for Alice
project save, load, export, and backup recovery behavior.

It keeps the user-visible contract readable, the recovery policy precise, and
the executable checks focused on existing Java test infrastructure. It does not
add Cucumber, a Maven TLC plugin, a tracked `eatme/` directory, a tracked
`drinkme/` directory, or runtime configuration.

## Contract layers

The lane has three durable layers:

| Layer | Location | Purpose |
| --- | --- | --- |
| Acceptance contract | This document | Describes the save, load, export, resource safety, and backup recovery behavior that modernization must preserve. |
| Recovery policy | This document and `core/ide` characterization tests | Defines the ordered backup recovery state machine and invariants. |
| Executable characterization | `core/ide` and `core/story-api-migration` JUnit tests | Enforces the contract against the Java implementation. |

Historical investigation handoffs are not part of the tracked repository
surface. Keep new handoff or evidence artifacts outside the repository unless a
specific durable artifact is promoted into `docs/`, `tests/`, `qa/`, `scripts/`,
or the Java module that owns the behavior.

## Why the lane exists

Alice archive handling combines old editable project archives, newer player
exports, resource serialization, and IDE recovery prompts. Those behaviors are
easy to regress when the implementation is modernized.

The formal-spec lane prevents drift by making each behavior visible in the two
durable surfaces that remain after the cleanup:

1. This human-readable contract, including the recovery invariants below.
2. A focused JUnit characterization test in the module that owns the behavior.

## Feature contract

The lane defines the behavior the modernization work preserves. Each item is
tied to focused JUnit characterization so future modernization can detect drift.

- Saving an editable project writes a readable `.a3p` archive with `version.txt`,
  `manifest.json`, `programType.xml`, required resource metadata, and safe
  resource entries.
- Saving includes `thumbnail.png` and a matching manifest icon when thumbnail
  creation succeeds; thumbnail creation failure must not make the archive
  unreadable.
- Exporting a project writes a `.a3w` player archive with manifest metadata,
  Tweedle source references, and safe resource entries.
- Archive readers report missing, future, malformed, or unsafe archive metadata
  predictably instead of silently falling through to the wrong reader.
- Resource entries use safe relative paths and do not persist local filesystem
  paths.
- A corrupt primary project does not replace the current project before the user
  reaches a recovery or new-project outcome.
- Backup recovery considers candidates in newest-first order, skips known
  unloadable candidates, never escapes the backup directory, retries accepted
  candidates that fail to load, and reaches one terminal result.

## Recovery invariants

Backup recovery keeps the named guarantees that were previously captured in
investigation artifacts, but the durable contract now lives here and in the
`core/ide` JUnit characterization tests.

| Invariant | Contract |
| --- | --- |
| Prompted backups are safe | Alice only offers backup candidates that exist under the named backup directory beside the corrupt project. Traversal paths, missing files, candidate symlinks, and symlinked backup directories are rejected. |
| Unloadable backups are skipped | Candidates already known to be unloadable are not reselected. If an accepted backup fails to load, recovery continues with the next safe candidate instead of looping on the failed one. |
| Stale async completion cannot replace state | A corrupt primary project does not become the current project while recovery is pending, and failed load completions do not overwrite a later recovered or terminal state. |
| Recovery eventually reaches a terminal result | Recovery stops after the first successfully loaded backup, or after a user-visible all-backups-failed/new-project outcome when no candidate can be loaded. |

## Implemented coverage

The implemented contract is covered at the Java boundary that owns each
behavior:

| Behavior | Implementation/characterization |
| --- | --- |
| Editable `.a3p` archives include `manifest.json`. | XML project writing emits save manifest metadata when callers do not supply it; `IoUtilitiesTest` validates the low-level archive entry and `ProjectFileUtilitiesTest` validates IDE save-copy output. |
| Editable `.a3p` archives include thumbnail metadata when thumbnail creation succeeds. | Thumbnail data sources are preserved and the saved archive remains readable without a thumbnail entry; `IoUtilitiesTest` validates both cases. |
| Backup recovery cannot follow traversal or out-of-directory backup candidates. | `ProjectBackupSelector` skips missing and symlink candidates; `ProjectBackupSelectorTest` validates candidate and backup-directory symlink rejection. |
| Backup recovery handles corrupt project files and corrupt backup files through real IO. | `ProjectBackupRecoveryIoTest` creates temporary corrupt `.a3p` files and generated readable backups to validate readable-backup recovery and all-backups-fail dispatch without Git LFS or Sims assets. |

## What remains outside the lane

The lane is not a new framework, runtime mode, source generator, or tracked
artifact staging area.

- Historical Gherkin or TLA+ investigation artifacts are not tracked runtime
  surfaces and are not Maven or CI inputs unless a future change explicitly
  promotes them into a durable repository surface.
- Top-level `drinkme/` and `eatme/` directories are not tracked repository
  surfaces.
- Eatme evidence workflow names remain valid when they refer to
  `tools/eatme-*`, `org.alice.tools.Eatme*` classes, test names, or JSON schema
  identifiers. They do not imply a top-level `eatme/` directory.
- Java behavior remains compatible with the current Alice 3 baseline unless a
  behavior change is explicitly documented and covered by characterization
  tests.

## Evidence workflow usage

The evidence workflow is invoked through tools, not by reading files from a
top-level `eatme/` directory. For example:

```bash
tools/eatme-save-project --help
tools/eatme-reopen-project --help
```

Detailed reopen evidence output is documented in
[`../tools-eatme-reopen-project.md`](../tools-eatme-reopen-project.md).

## Ownership rule

When behavior changes, update the smallest complete set of artifacts:

| Change | Required update |
| --- | --- |
| User-visible archive behavior changes | Update this contract and the matching JUnit test. |
| Backup recovery policy changes | Update this contract and the `core/ide` JUnit tests. |
| Archive entry, manifest, version, or resource safety changes | Update this contract and the matching archive test (`IoUtilitiesTest` for low-level I/O, `ProjectFileUtilitiesTest` for IDE save/export copy flows). |
| Only implementation structure changes | Keep specs stable and update or add characterization tests only if the observable contract is affected. |
