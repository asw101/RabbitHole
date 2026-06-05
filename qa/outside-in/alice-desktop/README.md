# Alice desktop outside-in QA

This directory defines executable acceptance coverage for Alice desktop
workflows without changing product modules. It keeps scenario intent, execution
wrappers, schemas, and evidence requirements in one repository-owned QA area.

## What belongs here

| Area | Owns | Does not own |
| --- | --- | --- |
| `scenarios/` | User-like workflows, expected outcomes, evidence requirements, and automation mode | Java implementation details or brittle internal UI assumptions |
| `contracts/` | Declarative claim-boundary records | Runner behavior or assessment implementation |
| `schema/` | Scenario structure and allowed field values | Product behavior |
| `runners/` | Thin wrappers around existing Maven and Alice commands | New build systems, hidden dependencies, or product behavior changes |
| `evidence/` | Local run artifacts produced by the runner | Source-controlled product assets |
| `tests/` | Shell contracts for the QA runner, schema, probes, and artifact boundaries | Generated run output |

Generated evidence is ignored by Git. Commit only scenario definitions, schema
changes, runner changes, contract fixtures, and the `.gitignore` files that keep
local output untracked.

## Scenario model

Each scenario uses the same durable fields:

- `id`
- `name`
- `title`
- `workflow`
- `automationMode`
- `preconditions`
- `userActions`
- `expectedOutcomes`
- `evidence.required`
- `fallback`
- `steps`
- `agents`

The `name` field is human-readable and must match `title`. The `steps` field is
the repository runner step list, and `agents` identifies the local QA executor.
The JSON schema and `validate-scenarios.sh` enforce the field contract for every
scenario file.

Allowed `automationMode` values are:

| Mode | Meaning |
| --- | --- |
| `xvfb-real-alice` | Attempts to run the real Alice desktop under Xvfb and captures logs or screenshots. |
| `manual-evidence-required` | Produces an executable checklist with required evidence, but does not automate GUI interaction or mark the scenario complete. |
| `gated-command-smoke` | Produces checklist evidence by default; executes the configured CLI smoke only when `ALICE_QA_RUN_GATED_SMOKES=1`. |

Scenario YAML intentionally uses a strict subset: simple mappings, nested
mappings, scalar values, and scalar lists. Do not use anchors, aliases, tags,
multiline scalars, flow-style collections, or tabs for indentation. The JSON
Schema is the published contract; the dependency-free validator must stay in
parity with it.

## Commands

Run all commands from the repository root.

```bash
qa/outside-in/alice-desktop/runners/validate-scenarios.sh
qa/outside-in/alice-desktop/runners/validate-scenarios.sh --list
qa/outside-in/alice-desktop/runners/validate-scenarios.sh --dump-json
qa/outside-in/alice-desktop/runners/run-scenario.sh list
qa/outside-in/alice-desktop/runners/run-scenario.sh run alice-desktop-launch
qa/outside-in/alice-desktop/runners/run-scenario.sh run qa/outside-in/alice-desktop/scenarios/launch.yaml
bash qa/outside-in/alice-desktop/tests/test-save-menu-dialog-negative-artifact-contract.sh
python3 alice_qa.py alice-qa validate
python3 alice_qa.py alice-qa run alice-desktop-launch
python3 alice_qa.py alice-qa save-negative-contract
```

The wrapper delegates to the checked-in runner scripts and preserves their exit
codes. It is a convenience entry point, not a separate source of truth.

## Supported workflow names

```text
archive-fixture-smoke
export
exported-project-ant-build-smoke
failure-path-smoke
file-loader-smoke
first-lesson-live-procedure-target-observation
future-ui-smoke
generated-listener-runtime-dispatch-smoke
instructor-student-setup
launch
menu-action-smoke
migration-hotspot-characterization-smoke
model-export-boundary-smoke
netbeans-package-smoke
open-load-save
package-install-smoke
post-open-runtime-display-accessibility-evidence
post-project-open-window-state-smoke
procedure-edit-handoff-smoke
procedure-edit-seam-smoke
project-io-smoke
runtime-event-dispatch-smoke
run-debug
run-window-contract
save-load
save-menu-dialog-write-proof
save-negative-artifact-contract
scene-creation
select-project-atk-exec-smoke
select-project-interaction-smoke
select-project-inventory
select-project-widget-introspection
silver-thread-launch-build-run
tweedle-decoder-boundary-smoke
tweedle-decoder-this-call-smoke
wizard-palette-completion-smoke
```

## Claim boundaries

Outside-in QA evidence is intentionally narrow. Scenario success only proves the
claim documented by that scenario and its artifact contract. Do not promote a
passing smoke command into claims about rendering correctness, grading,
creative assessment, full lesson completion, full project interaction, or broad
desktop automation unless the scenario and artifact schema explicitly prove
that claim.

The save negative contract is deliberately bounded:

```bash
python3 alice_qa.py alice-qa save-negative-contract
```

It delegates to:

```text
qa/outside-in/alice-desktop/tests/test-save-menu-dialog-negative-artifact-contract.sh
```

The wrapper subcommand accepts no extra arguments. Proves invalid Save proof
artifacts fail closed with explicit diagnostics; it is not desktop Save
completion evidence. In other words, success proves only that invalid Save proof
artifacts fail closed.

## Learner-world boundary

RabbitHole learner-world QA supports setup/open/save evidence review only. The
`alice-desktop-instructor-student-setup` scenario lets a reviewer collect
instructor starter-project setup evidence, student open evidence, and student
save evidence. It is a manual evidence workflow; checklist generation is not a
pass result and does not evaluate the learner's work.

The checked-in boundary record is:

```text
qa/outside-in/alice-desktop/contracts/learner-world-assessment-boundary.json
```

Do not use learner-world QA evidence to claim grading, rubric scoring,
correctness assessment, or creative assessment. Any assessment capability needs
a reviewed assessment contract, evidence mapping, privacy and audit controls,
and a separate implementation change.

## Maintenance rules

1. Add or update a scenario in `scenarios/`.
2. Keep `schema/scenario.schema.json`, `runners/validate-scenarios.sh`, and
   `runners/run-scenario.sh` in parity.
3. Add shell contract coverage under `tests/` for new runner or artifact
   behavior.
4. Run `qa/outside-in/alice-desktop/runners/validate-scenarios.sh`.
5. Do not commit generated logs, screenshots, local evidence, or session output.
