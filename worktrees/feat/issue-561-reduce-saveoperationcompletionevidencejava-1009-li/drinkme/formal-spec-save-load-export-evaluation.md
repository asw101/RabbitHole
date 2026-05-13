# Alice save-load/export formal specification

Alice project save, load, export, and backup recovery behavior is specified by two reusable artifacts:

- `eatme/specs/save-load-export/project-archive.feature` defines executable user-facing acceptance criteria for editor archives, player exports, archive validation, resource safety, and backup recovery.
- `eatme/formal/backup-load-recovery/BackupLoadRecovery.tla` and `BackupLoadRecovery.cfg` define a model-checkable TLA+ recovery policy for corrupt primary loads, ordered backups, skipped unloadable backups, user recovery choices, and stale asynchronous completion handling.

The specification covers observable project archive behavior. It does not formalize simple menu clicks, file chooser navigation, dialog copy, or other trivial UI mechanics.

## Finished behavior

### Editor project save

Saving an Alice project creates an editor archive with the `.a3p` extension. A saved editor archive is readable by Alice as an editable project and preserves the project name, program type, and referenced resources.

An editor archive contains these required contract entries:

| Entry | Purpose |
| --- | --- |
| `version.txt` | Archive compatibility version used before project data is accepted. |
| `manifest.json` | Project-level metadata, including the user-visible project name and optional icon reference. |
| `programType.xml` | Editable Alice program type representation. |
| `resources.xml` | Resource metadata used to reconstruct project resources. |
| `resources/*` | Referenced resource bytes, stored under safe relative archive names. |

When thumbnail generation succeeds, the archive also contains `thumbnail.png` and the manifest icon references that entry. When thumbnail generation is unavailable, saving still succeeds and the archive remains a valid editor project.

### Player export

Exporting an Alice project creates a player archive with the `.a3w` extension. A player archive preserves the data needed by the player/export path and by resource readers.

A player archive contains these required contract entries:

| Entry | Purpose |
| --- | --- |
| `version.txt` | Archive compatibility version. |
| `manifest.json` | Player archive metadata, including project name and resource references. |
| `src/<Program>.twe` | Exported Tweedle source for the project. |
| `resources/*` | Referenced image, audio, text, or other project resources. |

Player archive resource readers can read referenced resources without requiring the editor program type to be decoded from the player archive. The player export contract is therefore resource- and manifest-oriented; it does not require `.a3w` files to reopen as editable `.a3p` projects.

### Resource archive paths

Archive resources use safe, distinct, relative entry names. Alice never stores local absolute source paths as archive entry names or manifest resource references.

Resource entry names follow these rules:

1. Entry names are relative to the archive.
2. Entry names are normalized before use.
3. Entry names do not contain `..` traversal segments.
4. Entry names do not contain absolute Unix paths, Windows drive prefixes, empty segments, NUL characters, or control characters.
5. Duplicate source file names are disambiguated so every resource maps to one distinct archive entry.
6. Manifest resource references point to archive-internal entries, not local filesystem paths.

Archives are treated as untrusted input. A resource reference that points outside the archive, such as `../outside.png`, fails predictably and is not used to read from the local filesystem.

### Archive reader selection and validation

Alice selects the archive reader from archive contents and required entries rather than trusting the file extension alone. Required metadata is validated before project state is replaced.

Failure behavior is deterministic:

| Archive condition | Behavior |
| --- | --- |
| Missing `version.txt` | Load fails with an error identifying `version.txt`. |
| Future archive version | Load fails as unsupported and reports the future version. |
| Corrupt `manifest.json` in a player archive | Load fails with an error identifying `manifest.json`; Alice does not fall back to the editor XML reader. |
| Malformed `programType.xml` or `resources.xml` in an editor archive | Load fails with an error identifying the malformed entry. |
| Unsafe resource reference | Load fails with an error identifying the unsafe resource entry. |
| Malformed archive selected while a valid project is open | The current valid project remains active until the user selects recovery or starts a new project. |

### Backup recovery

When a primary project archive cannot load, Alice uses the backup recovery policy instead of silently replacing the current project state.

Recovery behavior is:

1. The failed primary project is marked unloadable for the current recovery attempt.
2. Backup candidates are considered in deterministic newest-first order.
3. Unloadable backups are marked and skipped during candidate selection before any user prompt.
4. The user is prompted only for readable backup candidates.
5. Accepting a readable backup loads exactly that backup as the recovered project.
6. Declining recovery opens the new-project workflow.
7. If no readable backup remains, Alice reports the failed project and failed backups, then opens the new-project workflow exactly once.
8. A background load completion cannot replace project state after a final recovery or new-project outcome has been reached.

The current valid editor state remains unchanged until a readable project has loaded or the user has chosen an explicit final outcome.

## Usage documentation

### Save an editable project

Use Alice's normal save or save-as workflow to create a `.a3p` editor archive. The saved file is the canonical editable project artifact.

Expected result:

- Alice writes the editor archive to the selected path.
- The archive contains version, manifest, program type, resource metadata, and resource entries.
- Alice can reopen the `.a3p` archive as an editable project.
- Optional thumbnails are included only when thumbnail generation succeeds.

### Export a player archive

Use Alice's export workflow to create a `.a3w` player archive.

Expected result:

- Alice writes the player archive to the selected path.
- The archive contains version metadata, a player manifest, Tweedle source, and referenced resources.
- Resource readers can read exported resources from the archive.
- The export does not persist local absolute paths from imported media.

### Open a project archive

Use Alice's normal open workflow to select an `.a3p` project archive. Alice validates archive structure and version metadata before replacing the current project.

Expected result:

- Supported, well-formed editor archives open as editable projects.
- Unsupported future archives fail with a version-specific message.
- Missing or corrupt required entries fail with entry-specific messages.
- A failed load does not overwrite the currently valid project.

### Recover from a corrupt primary project

If the selected primary project cannot load and backups are available, Alice presents backup recovery options in newest-first order.

Example recovery sequence:

1. `world.a3p` fails to load.
2. Alice checks `world.bak/auto20240102_140000.a3p`.
3. If that backup is unloadable, Alice marks it unloadable and checks the next candidate.
4. Alice offers `world.bak/auto20240102_130000.a3p` when it is readable.
5. Accepting the prompt loads that backup as the recovered project.
6. Declining the prompt starts the new-project workflow.

The corrupt primary project is not silently overwritten during this process.

## API and integration documentation

No new public network API, CLI API, plugin API, database API, or SDK surface is introduced. The archive files and recovery outcomes are the stable contract for this lane.

The internal Java integration surfaces are:

| Component | Role in the contract |
| --- | --- |
| `ProjectFileUtilities` | Writes editor archives, writes player exports, handles thumbnails, manages backup directories, and creates autosave artifacts. |
| `ProjectApplication` | Coordinates project load completion, recovery prompts, backup adoption, current-project preservation, and the new-project outcome. |
| `ProjectBackupSelector` | Selects backup candidates in newest-first order while excluding unloadable candidates before any prompt is shown. |
| `ProjectLoadFailurePlan` | Represents the recovery decision after a project load failure. |
| `ProjectLoadFailureDispatchPlan` | Dispatches the selected recovery/new-project outcome exactly once. |
| `UriContentLoader` and `FileProjectLoader` | Perform asynchronous project loading and return completion to the application layer. |
| `IoUtilities` | Selects archive reader/writer paths and enforces archive type/version expectations. |
| `JsonProjectIo` | Reads and writes player/export JSON manifest archive data. |
| `XmlProjectIo` | Reads and writes editor XML project archive data. |
| `DataSourceIo` and `ZipEntryContainer` | Provide archive entry access used by readers and resource loading. |

Executable characterization uses the existing JUnit lane rather than a new Cucumber runner. Each Gherkin scenario maps to focused tests in the existing modules:

| Specification area | Test home |
| --- | --- |
| Save/export archive structure | `core/ide/src/test/java/org/alice/ide/ProjectFileUtilitiesTest.java` |
| Backup recovery I/O | `core/ide/src/test/java/org/alice/ide/ProjectBackupRecoveryIoTest.java` |
| Failure dispatch outcomes | `core/ide/src/test/java/org/alice/ide/ProjectLoadFailureDispatchPlanTest.java` |
| Archive reader/writer validation | `core/story-api-migration/src/test/java/org/lgna/project/io/IoUtilitiesTest.java` |

## Configuration documentation

The feature has no runtime configuration switch. Save, load, export, and backup recovery use Alice's existing desktop file paths, project backup directories, and archive version metadata.

The specification intentionally adds no repository dependency on:

- Cucumber
- TLC
- Apalache
- Additional archive tooling
- A database or migration framework
- A service authentication or authorization layer

No local shell environment variable, including `NODE_OPTIONS`, is part of this feature contract.

## Formal specification usage

### Gherkin acceptance specification

The Gherkin artifact is stored at:

```text
eatme/specs/save-load-export/project-archive.feature
```

Use it as the acceptance contract for modernization work. The scenarios are written at the user-observable outcome level and are implemented through the existing JUnit characterization lane; no Cucumber runner is required for this feature.

The feature file deliberately avoids:

- Step-by-step menu navigation.
- File chooser implementation details.
- Dialog copy assertions.
- Internal ZIP writer algorithms.
- Mandatory thumbnail creation in headless environments.

### TLA+ recovery model

The TLA+ model is stored at:

```text
eatme/formal/backup-load-recovery/BackupLoadRecovery.tla
eatme/formal/backup-load-recovery/BackupLoadRecovery.cfg
```

The model is checkable with TLC when `tla2tools.jar` is available outside the repository:

```bash
java -cp /path/to/tla2tools.jar tlc2.TLC \
  -config eatme/formal/backup-load-recovery/BackupLoadRecovery.cfg \
  eatme/formal/backup-load-recovery/BackupLoadRecovery.tla
```

The repository does not vendor TLC and does not run TLC as part of the normal Maven build. During this handoff, no local `tla2tools` command or `tla2tools.jar` was available, so the model check was not run in this checkout. The model documents the recovery policy and is used when recovery ordering, final outcome behavior, or asynchronous load completion semantics change.

The model proves these contract properties for the supplied configuration:

| Property | Meaning |
| --- | --- |
| `TypeOK` | Recovery state remains inside the modeled state space. |
| `CorruptPrimaryDoesNotReplaceCurrentBeforeFinal` | Failed primary loads do not replace the current valid project before recovery finishes. |
| `LoadedBackupWasReadable` | Only readable backups can become the loaded recovered project. |
| `PromptedBackupsAreReadable` | Alice offers recovery prompts only for readable backup candidates. |
| `UnloadableBackupsSkipped` | Older backups are considered only after newer unreadable backups are marked unloadable during candidate selection. |
| `FinalOutcomeExactlyOne` | Recovery finishes with one loaded-backup or new-project outcome. |
| `FinalProjectMatchesOutcome` | The final project state matches the selected recovery outcome. |
| `NoStaleAsyncCompletion` | Final recovery state has no active load attempt that can later mutate the result. |
| `EventuallyFinal` | Recovery eventually reaches a final state under the model's weak fairness assumption. |

## Examples and tutorials

### Example: inspect an editor archive contract

After saving `Program.a3p`, inspect the archive entries with standard ZIP tooling:

```bash
zipinfo -1 Program.a3p
```

A valid editor archive includes:

```text
version.txt
manifest.json
programType.xml
resources.xml
resources/<resource-entry>
```

If thumbnail generation succeeded, it also includes:

```text
thumbnail.png
```

### Example: inspect a player export contract

After exporting `Program.a3w`, inspect the archive entries:

```bash
zipinfo -1 Program.a3w
```

A valid player archive includes:

```text
version.txt
manifest.json
src/Program.twe
resources/<resource-entry>
```

Resource paths in `manifest.json` are archive-relative and do not contain local absolute paths.

### Tutorial: convert a Gherkin scenario to JUnit characterization

1. Choose one scenario from `project-archive.feature`.
2. Build a synthetic Alice project in the closest existing JUnit test class.
3. Save, export, or load through the existing Java call path.
4. Assert only observable archive entries, resource data, failure messages, or recovery outcomes named by the scenario.
5. Add production changes only when the characterization test demonstrates a behavior gap.

### Tutorial: update the recovery model after policy changes

1. Update `BackupLoadRecovery.tla` when the recovery state machine changes.
2. Update `BackupLoadRecovery.cfg` when a new meaningful backup ordering or readability case must be checked.
3. Run TLC externally if available.
4. Keep the model focused on recovery policy; validate archive bytes and Java threading behavior with JUnit characterization tests.

## Security documentation

Save-load/export crosses a local file/archive trust boundary. Alice treats project archives as untrusted input even when the user selects them from the local filesystem.

Security requirements:

1. Do not read outside the selected archive based on manifest or resource metadata.
2. Do not write archive entries with traversal names or absolute local paths.
3. Do not silently fall back from a corrupt player manifest to an editor XML reader.
4. Do not replace the current valid project after a failed load until recovery or new-project selection is final.
5. Do not silently overwrite the corrupt primary project with a backup.
6. Do not log serialized project content, resource bytes, or sensitive local paths during normal failure handling.
7. Fail closed for missing required entries, unsupported versions, malformed JSON/XML, unsafe resource references, and unreadable backups.

## Artifact placement

`drinkme/` contains rationale, evaluation, and handoff documentation only. It does not contain Alice source copies, user project archives, backups, binary fixtures, or generated runtime artifacts.

`eatme/` contains reusable specifications intended for implementation and validation:

- Gherkin acceptance requirements under `eatme/specs/`.
- TLA+ formal recovery artifacts under `eatme/formal/`.

## Non-goals

This specification does not:

- Merge code.
- Open a pull request.
- Add Cucumber, TLC, Apalache, or other tooling dependencies.
- Add a database.
- Add authentication or authorization services.
- Replace existing JUnit characterization tests.
- Formalize trivial UI flows.
- Change runtime behavior by itself.
- Use upstream Alice issues or pull requests for modernization tracking.
