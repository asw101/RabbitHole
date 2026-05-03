# Run Alice desktop outside-in QA

Use the Alice desktop outside-in QA lane to validate the scenario catalog and collect reviewable evidence for user-like workflows: launch, instructor/student setup, scene creation, run/debug-like behavior, save/load, and export.

## Contents

- [Prerequisites](#prerequisites)
- [Validate the scenario catalog](#validate-the-scenario-catalog)
- [Validate a custom scenario catalog](#validate-a-custom-scenario-catalog)
- [List available scenarios](#list-available-scenarios)
- [Run the real Alice launch scenario](#run-the-real-alice-launch-scenario)
- [Prepare evidence for manual workflows](#prepare-evidence-for-manual-workflows)
- [Choose a custom evidence directory](#choose-a-custom-evidence-directory)
- [Configure scenario and Xvfb runs](#configure-scenario-and-xvfb-runs)
- [Review evidence](#review-evidence)
- [Troubleshooting](#troubleshooting)

## Prerequisites

Run commands from the repository root.

Alice desktop QA uses the same build and launch prerequisites as the main project:

```bash
java -version
mvn -version
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
```

The real desktop launch scenario uses Xvfb when available. Manual scenarios do not require Xvfb; they generate structured evidence checklists.

No browser surface is part of this lane, so Playwright is not required. Virtual TTY tools are only useful for terminal wrappers and are not used for Swing GUI interaction.

## Validate the scenario catalog

Validate all checked-in scenario YAML files before running or reviewing them:

```bash
qa/outside-in/alice-desktop/runners/validate-scenarios.sh
```

Expected output:

```text
Validated 6 scenario(s) in .../qa/outside-in/alice-desktop/scenarios
```

## Validate a custom scenario catalog

Use `ALICE_QA_SCENARIO_DIR` when testing a local catalog before moving it into the checked-in `scenarios/` directory:

```bash
ALICE_QA_SCENARIO_DIR=/tmp/alice-scenarios \
qa/outside-in/alice-desktop/runners/validate-scenarios.sh
```

List the same active catalog through either entry point:

```bash
ALICE_QA_SCENARIO_DIR=/tmp/alice-scenarios \
qa/outside-in/alice-desktop/runners/validate-scenarios.sh --list

ALICE_QA_SCENARIO_DIR=/tmp/alice-scenarios \
qa/outside-in/alice-desktop/runners/run-scenario.sh list
```

## List available scenarios

List scenario IDs, automation modes, and titles:

```bash
qa/outside-in/alice-desktop/runners/run-scenario.sh list
```

Use the scenario ID from the first column when running a scenario.

You can also run a scenario by its checked-in YAML path. The path must point directly to a `.yaml` file inside the active scenario directory:

```bash
qa/outside-in/alice-desktop/runners/run-scenario.sh run \
  qa/outside-in/alice-desktop/scenarios/save-load.yaml
```

## Run the real Alice launch scenario

The launch scenario starts the real Alice desktop through the documented Maven path under Xvfb:

```bash
qa/outside-in/alice-desktop/runners/run-scenario.sh run alice-desktop-launch
```

The scenario runs:

```bash
cd alice-ide
mvn exec:java -Dalice-ide
```

The runner writes evidence to:

```text
qa/outside-in/alice-desktop/evidence/alice-desktop-launch/<timestamp>/
```

A completed launch evidence run includes an environment summary, Xvfb log, Alice launch log, status file, and screenshot. The runner checks process, window-readiness, and screenshot-capture status; it does not deeply classify every line in `launch.log` as a semantic pass/fail oracle. Review `status.txt`, `launch.log`, and the screenshot before treating the launch evidence as accepted.

If launch evidence is collected in CI or another disposable workspace, pass an explicit evidence directory:

```bash
qa/outside-in/alice-desktop/runners/run-scenario.sh run alice-desktop-launch \
  --evidence-dir /tmp/alice-qa-evidence \
  --timeout-seconds 180
```

## Prepare evidence for manual workflows

Manual workflows are still executable: the runner creates a checklist with preconditions, user actions, expected outcomes, evidence requirements, and fallback notes.

Example:

```bash
qa/outside-in/alice-desktop/runners/run-scenario.sh run alice-desktop-save-load
```

The runner writes:

```text
qa/outside-in/alice-desktop/evidence/alice-desktop-save-load/<timestamp>/manual-evidence-checklist.txt
qa/outside-in/alice-desktop/evidence/alice-desktop-save-load/<timestamp>/environment.txt
qa/outside-in/alice-desktop/evidence/alice-desktop-save-load/<timestamp>/status.txt
```

Follow the checklist while using Alice, then place the required screenshots, project files, logs, or exported artifacts in the same run directory. Generating `manual-evidence-checklist.txt` only prepares the scenario; the manual scenario is complete only after a human performs the workflow and adds the required evidence artifacts.

For example, a save/load evidence directory should contain the generated checklist plus the saved project, before/after screenshots, and notes comparing the reopened project with the saved state.

## Choose a custom evidence directory

Use `--evidence-dir` when evidence should live outside the repository, such as a session artifact directory or CI workspace:

```bash
qa/outside-in/alice-desktop/runners/run-scenario.sh run alice-desktop-scene-creation \
  --evidence-dir /tmp/alice-qa-evidence
```

This creates:

```text
/tmp/alice-qa-evidence/alice-desktop-scene-creation/<timestamp>/
```

## Configure scenario and Xvfb runs

The runner accepts these environment variables:

| Variable | Purpose | Example |
| --- | --- | --- |
| `ALICE_QA_SCENARIO_DIR` | Override the checked-in scenario catalog directory. | `ALICE_QA_SCENARIO_DIR=/tmp/scenarios` |
| `ALICE_QA_DISPLAY` | Reuse a specific X display instead of selecting a free display from `:90` through `:120`. | `ALICE_QA_DISPLAY=:99` |
| `ALICE_QA_SCREEN` | Set Xvfb screen geometry. Defaults to `1280x900x24`. | `ALICE_QA_SCREEN=1600x1000x24` |
| `ALICE_QA_READY_WAIT_SECONDS` | Override the scenario readiness wait before screenshot capture. | `ALICE_QA_READY_WAIT_SECONDS=60` |

Example:

```bash
ALICE_QA_SCREEN=1600x1000x24 \
ALICE_QA_READY_WAIT_SECONDS=60 \
qa/outside-in/alice-desktop/runners/run-scenario.sh run alice-desktop-launch
```

Override the launch timeout when a workstation is slow:

```bash
qa/outside-in/alice-desktop/runners/run-scenario.sh run alice-desktop-launch \
  --timeout-seconds 180
```

The QA lane itself does not require Node.js. If a surrounding QA orchestrator invokes Node-based tooling around this lane, use:

```bash
export NODE_OPTIONS=--max-old-space-size=32768
```

## Review evidence

Every run directory is timestamped and self-contained. Review these files first:

| File | Meaning |
| --- | --- |
| `environment.txt` | Java, Maven, OS, repository root, timestamp, and display information. |
| `status.txt` | Run status. Real launch runs record display, readiness, process status, screenshot status, and timeout; manual runs record that human evidence is still required. |
| `launch.log` | Maven/Alice startup output for real launch scenarios. |
| `xvfb.log` | Xvfb startup and display output. |
| `screenshot.png` or `screenshot.xwd` | Captured desktop state. |
| `manual-evidence-checklist.txt` | Repeatable checklist for manual scenarios. |

Generated evidence is ignored by Git. Commit scenario definitions, schema changes, runner changes, and documentation; do not commit local evidence artifacts.

Accept a run only when the generated status file agrees with the expected automation mode and the listed evidence artifacts are present. For manual scenarios, `status.txt` records checklist generation; it is not a pass result until a human adds the required artifacts.

## Troubleshooting

### Missing Tweedle parser classes

Initialize the Tweedle grammar submodule in the current checkout or worktree:

```bash
git submodule status tweedle-lang
test -d tweedle-lang/Grammar
git submodule update --init tweedle-lang
```

### Xvfb is unavailable

Install Xvfb for the local environment, or keep the generated manual fallback checklist and collect launch evidence from a supported desktop environment.

### No Alice window is detected

Check the run directory:

```bash
sed -n '1,120p' qa/outside-in/alice-desktop/evidence/alice-desktop-launch/*/status.txt
sed -n '1,160p' qa/outside-in/alice-desktop/evidence/alice-desktop-launch/*/launch.log
```

If renderer initialization fails, preserve the logs and provide a manual launch screenshot as fallback evidence.
