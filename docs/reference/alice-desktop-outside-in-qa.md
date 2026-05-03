# Alice desktop outside-in QA reference

This reference describes the Alice desktop outside-in QA lane: file layout, runner commands, scenario schema, automation modes, configuration, and evidence artifacts.

## Contents

- [Directory layout](#directory-layout)
- [Scenario catalog](#scenario-catalog)
- [Runner commands](#runner-commands)
- [Environment variables](#environment-variables)
- [Scenario schema](#scenario-schema)
- [Automation modes](#automation-modes)
- [Evidence contract](#evidence-contract)
- [Workflow evidence requirements](#workflow-evidence-requirements)
- [Extension rules](#extension-rules)

## Directory layout

| Path | Purpose |
| --- | --- |
| `qa/outside-in/alice-desktop/README.md` | Local entry point for the QA lane. |
| `qa/outside-in/alice-desktop/scenarios/` | User-like acceptance scenario YAML files. |
| `qa/outside-in/alice-desktop/schema/scenario.schema.json` | JSON Schema for the scenario model. |
| `qa/outside-in/alice-desktop/runners/validate-scenarios.sh` | Catalog validator and scenario JSON dumper. |
| `qa/outside-in/alice-desktop/runners/run-scenario.sh` | Scenario listing, validation, real launch execution, and manual checklist generation. |
| `qa/outside-in/alice-desktop/evidence/` | Local generated evidence. Contents are ignored by Git except placeholder files. |

## Scenario catalog

| Scenario ID | Workflow | Automation mode | Purpose |
| --- | --- | --- | --- |
| `alice-desktop-launch` | `launch` | `xvfb-real-alice` | Starts the real Alice desktop through Maven under Xvfb and captures launch evidence. |
| `alice-desktop-instructor-student-setup` | `instructor-student-setup` | `manual-evidence-required` | Covers instructor starter-project preparation and student project opening/saving. |
| `alice-desktop-scene-creation` | `scene-creation` | `manual-evidence-required` | Covers creating or selecting a starter scene and saving it as an Alice project. |
| `alice-desktop-run-debug` | `run-debug` | `manual-evidence-required` | Covers program run controls plus the closest baseline debug-like control, such as fast-forward or statement execution. |
| `alice-desktop-save-load` | `save-load` | `manual-evidence-required` | Covers saving an `.a3p` project, reopening it, and checking persistence. |
| `alice-desktop-export` | `export` | `manual-evidence-required` | Covers the current Alice export path and verification of the exported artifact. |

## Runner commands

Run commands from the repository root.

### Validate all scenarios

```bash
qa/outside-in/alice-desktop/runners/validate-scenarios.sh
```

### List scenarios

```bash
qa/outside-in/alice-desktop/runners/validate-scenarios.sh --list
qa/outside-in/alice-desktop/runners/run-scenario.sh list
```

### Dump a scenario as JSON

```bash
qa/outside-in/alice-desktop/runners/validate-scenarios.sh --dump-json alice-desktop-launch
```

### Run a scenario

```bash
qa/outside-in/alice-desktop/runners/run-scenario.sh run <scenario-id>
```

### Run with a custom evidence directory

```bash
qa/outside-in/alice-desktop/runners/run-scenario.sh run <scenario-id> \
  --evidence-dir /tmp/alice-qa-evidence
```

### Run with a custom timeout

```bash
qa/outside-in/alice-desktop/runners/run-scenario.sh run alice-desktop-launch \
  --timeout-seconds 180
```

`--timeout-seconds` applies to `xvfb-real-alice` execution. Manual and supporting-evidence scenarios write checklists immediately.

## Environment variables

| Variable | Applies to | Description |
| --- | --- | --- |
| `ALICE_QA_SCENARIO_DIR` | Validator | Overrides the directory containing scenario YAML files. |
| `ALICE_QA_DISPLAY` | Xvfb runs | Reuses a specific X display instead of selecting one automatically. |
| `ALICE_QA_SCREEN` | Xvfb runs | Sets Xvfb screen geometry. Default: `1280x900x24`. |
| `ALICE_QA_READY_WAIT_SECONDS` | Xvfb runs | Overrides the scenario readiness wait before screenshot capture. |
| `NODE_OPTIONS` | Surrounding Node tooling | Use `--max-old-space-size=32768` when a larger QA orchestrator invokes Node-based helpers around this lane. The lane itself does not require Node. |

Example:

```bash
export NODE_OPTIONS=--max-old-space-size=32768
ALICE_QA_DISPLAY=:99 \
ALICE_QA_SCREEN=1600x1000x24 \
qa/outside-in/alice-desktop/runners/run-scenario.sh run alice-desktop-launch
```

## Scenario schema

Each scenario is a YAML file whose file name matches its ID without the `alice-desktop-` prefix.

Example:

```yaml
id: alice-desktop-save-load
title: Save and load an Alice project
workflow: save-load
automationMode: manual-evidence-required
preconditions:
  - Java 21 is available.
  - Maven dependencies are available.
  - The tweedle-lang submodule is initialized.
userActions:
  - Launch Alice.
  - Create or open a small project.
  - Save the project as an a3p file in a known evidence location.
  - Close the project or restart Alice.
  - Open the saved a3p file.
  - Verify the loaded project matches the saved state.
expectedOutcomes:
  - Alice writes a usable a3p project file.
  - Alice opens the saved a3p project without uncaught application errors.
  - The visible scene or program state after loading matches the saved project.
evidence:
  required:
    - Save operation log or manual notes.
    - Saved a3p project file.
    - Screenshot before saving.
    - Screenshot after reopening.
    - Notes comparing the saved and loaded state.
fallback:
  mode: manual-evidence-required
  notes:
    - Link lower-level Maven or JUnit characterization evidence for project loading when available.
    - Manual evidence remains required until stable full GUI save and open automation exists.
supportingEvidence:
  - alice-desktop-launch
```

### Required fields

| Field | Type | Description |
| --- | --- | --- |
| `id` | string | Scenario ID. Must match `alice-desktop-[a-z0-9-]+`. |
| `title` | string | Human-readable scenario title. |
| `workflow` | enum | Covered workflow. |
| `automationMode` | enum | How the runner handles the scenario. |
| `preconditions` | string list | Required starting conditions. |
| `userActions` | string list | User-like actions, written from outside the implementation. |
| `expectedOutcomes` | string list | Observable outcomes that show success. |
| `evidence.required` | string list | Files, screenshots, logs, artifacts, or notes required for review. |
| `fallback.mode` | enum | Fallback automation mode. |
| `fallback.notes` | string list | Specific fallback instructions. |

### Optional fields

| Field | Type | Description |
| --- | --- | --- |
| `automation.cwd` | string | Working directory for command-backed automation. Required for `xvfb-real-alice`. |
| `automation.command` | string | Command executed by the runner. Required for `xvfb-real-alice`. |
| `automation.timeoutSeconds` | positive integer | Default timeout for command-backed automation. Required for `xvfb-real-alice`. |
| `automation.readyWaitSeconds` | positive integer | Wait before screenshot capture. Required for `xvfb-real-alice`. |
| `supportingEvidence` | string list | Scenario IDs or evidence sources that support this scenario. |
| `tags` | string list | Additional scenario labels. |

### Workflow values

```text
instructor-student-setup
launch
scene-creation
run-debug
save-load
export
```

## Automation modes

| Mode | Runner behavior |
| --- | --- |
| `xvfb-real-alice` | Starts Xvfb, launches Alice through the scenario command, waits for readiness, captures environment data, logs, status, and screenshot. This is a launch evidence check, not a full semantic oracle for every startup log condition. |
| `command-wrapper` | Reserved for future terminal-only workflows. The current runner prepares the scenario evidence structure and checklist instead of executing terminal wrapper steps. |
| `manual-evidence-required` | Writes a structured checklist for human execution and evidence collection. Checklist generation does not complete the scenario. |
| `unit-evidence-linked` | Writes a checklist that links lower-level characterization evidence to the outside-in scenario. |

## Evidence contract

Every run creates:

```text
<evidence-dir>/<scenario-id>/<timestamp>/
```

The default evidence directory is:

```text
qa/outside-in/alice-desktop/evidence/
```

All runs include:

| Artifact | Description |
| --- | --- |
| `environment.txt` | UTC timestamp, repository root, display, Java version, Maven version, and OS details. |

`xvfb-real-alice` runs also include:

| Artifact | Description |
| --- | --- |
| `launch.log` | Alice Maven launch output. |
| `xvfb.log` | Xvfb output. |
| `status.txt` | Scenario ID, automation mode, display, readiness status, process status, screenshot status, and timeout. |
| `screenshot.png` or `screenshot.xwd` | Captured desktop image. |
| `screenshot.log` | Screenshot command output. |

For launch runs, `status.txt` records whether the process stayed alive, whether a visible window was detected when a detector is available, and whether screenshot capture succeeded. Acceptance still requires reviewing the generated evidence, especially `launch.log`; the runner does not currently scan the log for every possible uncaught application exception.

Manual and supporting-evidence runs include:

| Artifact | Description |
| --- | --- |
| `status.txt` | Scenario ID, automation mode, generated checklist name, and `manual-evidence-required` outcome. |
| `manual-evidence-checklist.txt` | Scenario preconditions, actions, outcomes, required evidence, and fallback notes. This file prepares the work; it is not proof that the workflow has been executed. |

Manual and supporting-evidence scenarios are complete only after a human performs the workflow or links the supporting characterization evidence and places the required artifacts in the same timestamped run directory.

## Workflow evidence requirements

| Workflow | Required evidence |
| --- | --- |
| Launch | Launch log, desktop screenshot, exit/status/timeout record, Java/Maven/display environment summary. |
| Instructor/student setup | Instructor launch log, starter project screenshot, starter `.a3p`, student launch or open log, loaded project screenshot, student copy `.a3p`. |
| Scene creation | Screenshot before scene creation, screenshot after object or scene appears, saved `.a3p`, notes identifying the selected template or object. |
| Run/debug | Screenshot before run, screenshot or screen capture during execution, notes naming run/debug-like controls, launch or run log, saved `.a3p`. |
| Save/load | Save log or notes, saved `.a3p`, screenshot before saving, screenshot after reopening, comparison notes. |
| Export | Export log or notes, screenshot before export, screenshot after export completion, exported artifact, file listing or checksum. |

## Extension rules

When adding or changing scenarios:

1. Keep the scenario user-like. Describe what the instructor, student, or Alice user does and observes.
2. Prefer real Alice execution through the runner when it is stable.
3. Use `manual-evidence-required` when Swing GUI interaction is not stable enough to automate.
4. Do not introduce Playwright unless Alice exposes a browser/web surface.
5. Do not use a virtual TTY for Swing GUI interaction.
6. Preserve Alice 3 baseline behavior unless a behavior change is explicitly documented and tested.
7. Validate the catalog before committing:

```bash
qa/outside-in/alice-desktop/runners/validate-scenarios.sh
```
