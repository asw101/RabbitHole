# Alice desktop outside-in QA

This lane defines executable acceptance coverage for Alice desktop workflows without changing product modules. It keeps scenario intent, execution wrappers, and evidence requirements in one repo-owned QA area.

For user-facing instructions, see [Run Alice desktop outside-in QA](../../../docs/howto/alice-desktop-outside-in-qa.md). For the complete scenario schema and runner interface, see the [Alice desktop outside-in QA reference](../../../docs/reference/alice-desktop-outside-in-qa.md).

## What belongs here

| Area | Owns | Does not own |
| --- | --- | --- |
| `scenarios/` | User-like workflows, expected outcomes, evidence requirements, automation mode | Java implementation details or brittle internal UI assumptions |
| `schema/` | Scenario structure and allowed field values | Business logic |
| `runners/` | Thin wrappers around existing Maven/Alice commands | New build systems, hidden dependencies, or product behavior changes |
| `evidence/` | Local run artifacts produced by the runner | Source-controlled product assets |

Generated evidence is ignored by Git. Commit only scenario definitions, schema changes, runner changes, and the placeholder files that keep the directory structure visible.

## Scenario model

Each scenario uses the same fields:

- `id`
- `title`
- `workflow`
- `automationMode`
- `preconditions`
- `userActions`
- `expectedOutcomes`
- `evidence.required`
- `fallback`

Allowed `automationMode` values are:

| Mode | Meaning |
| --- | --- |
| `xvfb-real-alice` | Attempts to run the real Alice desktop under Xvfb and captures logs/screenshots. |
| `command-wrapper` | Reserved for future terminal-only workflow wrappers. The current runner prepares evidence structure and a checklist instead of executing command-wrapper steps. |
| `manual-evidence-required` | Produces an executable checklist with required evidence, but does not automate GUI interaction or mark the scenario complete. |
| `unit-evidence-linked` | References lower-level Maven/JUnit characterization evidence as supporting evidence only. |

Do not use Playwright here unless Alice later exposes a browser/web UI.

## Commands

Run all commands from the repository root.

```bash
qa/outside-in/alice-desktop/runners/validate-scenarios.sh
qa/outside-in/alice-desktop/runners/run-scenario.sh list
qa/outside-in/alice-desktop/runners/run-scenario.sh run alice-desktop-launch
```

The launch scenario uses the documented Alice desktop path:

```bash
cd alice-ide
mvn exec:java -Dalice-ide
```

The runner records evidence under `qa/outside-in/alice-desktop/evidence/<scenario-id>/<timestamp>/`. For Xvfb runs it captures an environment summary, Xvfb log, Alice launch log, screenshot (`screenshot.png` or `screenshot.xwd`), and status file. For manual scenarios it creates a status file and structured checklist so the workflow is repeatable and reviewable; the scenario is complete only after a human performs the workflow and adds the required evidence artifacts.

## Baseline preconditions

- Java 21 is available.
- Maven 3.9.9 or later is available.
- The Tweedle grammar submodule is initialized:

```bash
git submodule update --init tweedle-lang
```

If Maven reports missing generated Tweedle parser classes, first check:

```bash
git submodule status tweedle-lang
test -d tweedle-lang/Grammar
```
