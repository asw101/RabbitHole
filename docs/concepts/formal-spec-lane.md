# Formal Specification Lane

The formal-spec lane is the documentation and characterization layer for Alice
project save, load, export, and backup recovery behavior.

It keeps the user-visible contract readable, the recovery policy precise, and
the executable checks focused on existing Java test infrastructure. It does not
add Cucumber, a Maven TLC plugin, or runtime configuration.

## Contract layers

The lane has three durable layers:

| Layer | Location | Purpose |
| --- | --- | --- |
| Acceptance contract | [`../../eatme/specs/save-load-export/project-archive.feature`](../../eatme/specs/save-load-export/project-archive.feature) | Describes the save, load, export, resource safety, and backup recovery behavior in Gherkin. |
| Formal recovery model | [`../../eatme/formal/backup-load-recovery/BackupLoadRecovery.tla`](../../eatme/formal/backup-load-recovery/BackupLoadRecovery.tla) and [`BackupLoadRecovery.cfg`](../../eatme/formal/backup-load-recovery/BackupLoadRecovery.cfg) | Defines the ordered backup recovery state machine and invariants. |
| Executable characterization | `core/ide` and `core/story-api-migration` JUnit tests | Enforces the contract against the Java implementation. |

The [`../../drinkme/formal-spec-save-load-export-evaluation.md`](../../drinkme/formal-spec-save-load-export-evaluation.md)
handoff records investigation context. It is not runtime input and is not the
product documentation surface.

## Why the lane exists

Alice archive handling combines old editable project archives, newer player
exports, resource serialization, and IDE recovery prompts. Those behaviors are
easy to regress when the implementation is modernized.

The formal-spec lane prevents drift by making each behavior visible in one of
three forms:

1. A human-readable scenario in the Gherkin feature.
2. A recovery rule or invariant in the TLA+ model.
3. A focused JUnit test in the module that owns the behavior.

## Target feature contract

The lane defines the behavior the modernization work will build and preserve.
The currently implemented target items are tied to focused JUnit
characterization so future modernization can detect drift.

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
  unloadable candidates, never escapes the backup directory, offers the newest
  readable backup, and reaches one terminal result.

## Coverage added from review

The architect review identified places where the intended contract needed
focused implementation and characterization:

| Target behavior | Implementation/characterization |
| --- | --- |
| Editable `.a3p` archives include `manifest.json`. | XML project writing now emits save manifest metadata when callers do not supply it; `IoUtilitiesTest` validates the archive entry and manifest contents. |
| Editable `.a3p` archives include thumbnail metadata when thumbnail creation succeeds. | Thumbnail data sources are preserved and the saved archive remains readable without a thumbnail entry; `IoUtilitiesTest` validates both cases. |
| Backup recovery cannot follow traversal or out-of-directory backup candidates. | `ProjectBackupSelector` skips missing and escaping candidates; `ProjectBackupSelectorTest` validates symlink escape rejection. |

## What remains outside the lane

The lane is not a new framework, runtime mode, or source generator.

- `.feature` files are not executed by Cucumber.
- TLA+ files are not wired into Maven or CI.
- `drinkme/` files remain investigation and handoff material.
- Java behavior remains compatible with the current Alice 3 baseline unless a
  behavior change is explicitly documented and covered by characterization
  tests.

## Ownership rule

When behavior changes, update the smallest complete set of artifacts:

| Change | Required update |
| --- | --- |
| User-visible archive behavior changes | Update the Gherkin scenario and the matching JUnit test. |
| Backup recovery policy changes | Update the Gherkin scenario, TLA+ model/config, and `core/ide` JUnit tests. |
| Archive entry, manifest, version, or resource safety changes | Update the Gherkin scenario and `IoUtilitiesTest`. |
| Only implementation structure changes | Keep specs stable and update or add characterization tests only if the observable contract is affected. |
