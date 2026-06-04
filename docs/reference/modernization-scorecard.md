# Alice Modernization Scorecard

The Alice modernization scorecard is the repo-owned reference view of current
modernization evidence. It reports what is measured, what is ratcheted, and
what remains blocked without treating the long-term 70% line coverage target
as current reality.

## Generation

Refresh this checked-in scorecard from the repository root:

```sh
python3 scripts/generate-modernization-scorecard.py \
  --output docs/reference/modernization-scorecard.md
```

Reviewers can reproduce this scorecard from an Alice modernization checkout
with the repository script, then compare the checked-in scorecard with the
generated output.

See the [Modernization scorecard generator reference](./modernization-scorecard-generator.md)
for the durable CLI contract, output-path safety behavior, examples, and
review workflow.

## Coverage ratchets

Coverage ratchets are executable CI floors, not the long-term target. They are
parsed from `.github/workflows/alice-coverage-ci.yml`, which runs the no-Sims
coverage summary with Git LFS disabled.

| Scope | Current CI floor | Source |
| --- | ---: | --- |
| Aggregate reactor | 8.0% | `--min-aggregate-line-percent 8.0` |
| `core/ast` | 18.0% | `--min-module-line-percent core/ast=18.0` |
| `core/model-loading` | 10.0% | `--min-module-line-percent core/model-loading=10.0` |
| `core/scenegraph` | 10.0% | `--min-module-line-percent core/scenegraph=10.0` |
| `core/story-api-migration` | 75.0% | `--min-module-line-percent core/story-api-migration=75.0` |
| `core/tweedle` | 50.0% | `--min-module-line-percent core/tweedle=50.0` |
| `netbeans` | 25.0% | `--min-module-line-percent netbeans=25.0` |

When these workflow values change, the generated scorecard changes with them.
Do not manually maintain a second ratchet table.

## Aggregate coverage state

Aggregate coverage is measured only when this file exists in the inspected
checkout:

```text
coverage-report/target/site/jacoco-aggregate/jacoco.csv
```

| Measurement | State | Meaning |
| --- | --- | --- |
| Aggregate JaCoCo CSV | Missing | The no-Sims Maven coverage lane has not produced aggregate coverage data in this checkout. |
| Aggregate line coverage | Not measured | The scorecard cannot report a current aggregate percent without the CSV. |
| Aggregate CI ratchet | 8.0% | The CI floor is still reported because it comes from the workflow. |

Missing aggregate coverage is a blocker for claiming coverage progress, but it
is not a scorecard generation failure.

## Module coverage state

Module coverage is measured from `target/site/jacoco/jacoco.csv` files under
module directories. A module with a CI ratchet is listed even when its local
CSV is missing, because losing a ratcheted module report is itself a gap.

| Module | CI floor | Expected CSV | State |
| --- | ---: | --- | --- |
| `core/ast` | 18.0% | `core/ast/target/site/jacoco/jacoco.csv` | Missing measurement |
| `core/model-loading` | 10.0% | `core/model-loading/target/site/jacoco/jacoco.csv` | Missing measurement |
| `core/scenegraph` | 10.0% | `core/scenegraph/target/site/jacoco/jacoco.csv` | Missing measurement |
| `core/story-api-migration` | 75.0% | `core/story-api-migration/target/site/jacoco/jacoco.csv` | Missing measurement |
| `core/tweedle` | 50.0% | `core/tweedle/target/site/jacoco/jacoco.csv` | Missing measurement |
| `netbeans` | 25.0% | `netbeans/target/site/jacoco/jacoco.csv` | Missing measurement |

Rows with no line totals are ignored rather than converted to false zero
coverage. This matches the coverage summary contract.

## 70% target status

The 70% line coverage goal is the modernization mission target. It is not the
current CI ratchet and is not considered met unless measured aggregate JaCoCo
coverage is at least `70.0%`.

| Target | State | Evidence |
| --- | --- | --- |
| 70.0% aggregate line coverage | Not claimable | Aggregate JaCoCo CSV is missing, so the scorecard cannot claim the 70% target from current measured data. |

## Production hotspots over 500 lines

A production hotspot is a tracked Java file with more than 500 physical lines
using this deterministic filter:

1. Start from `git ls-files '*.java'`.
2. Include paths containing `/src/main/java/`.
3. Exclude paths containing `/target/`, `/build/`, `/generated/`, `/generated-sources/`, `/drinkme/`, or `/src/main/java/test/`.
4. Include only files with line count greater than 500.
5. Sort by descending line count, then by path.

Current scorecard state for this checkout: 4 production-root Java hotspots over 500 lines.

| File | Lines |
| --- | ---: |
| `core/story-api-migration/src/main/java/org/lgna/project/migration/TextMigrationRegistryV3134.java` | 2000 |
| `core/story-api-migration/src/main/java/org/lgna/project/migration/TextMigrationRegistryV3159.java` | 1933 |
| `core/story-api-migration/src/main/java/org/lgna/project/migration/TextMigrationRegistryLateVersions.java` | 1387 |
| `core/story-api/src/main/java/org/lgna/story/implementation/GroundMeshData.java` | 661 |

Hotspot rows are not automatic refactor instructions. A hotspot is a blocker
only when its size prevents safe characterization, review, or focused
modernization work. Production refactors still require behavior
characterization before code moves.

## QA journey automation gaps

Journey status comes from:

```sh
qa/outside-in/alice-desktop/runners/validate-scenarios.sh --dump-json
```

The validator returns the normalized scenario catalog. The scorecard groups
scenarios by `automationMode` and treats manual evidence and gated command
smokes as remaining evidence gaps.

| Automation mode | Count | Scorecard category |
| --- | ---: | --- |
| `xvfb-real-alice` | 8 | Automated real Alice journey |
| `gated-command-smoke` | 21 | Gated command smoke coverage |
| `manual-evidence-required` | 8 | Manual evidence gap |

Manual evidence gaps:

| Scenario | Workflow |
| --- | --- |
| `alice-desktop-export` | `export` |
| `alice-desktop-instructor-student-setup` | `instructor-student-setup` |
| `alice-desktop-open-load-save` | `open-load-save` |
| `alice-desktop-procedure-edit-seam-smoke` | `procedure-edit-seam-smoke` |
| `alice-desktop-run-debug` | `run-debug` |
| `alice-desktop-save-load` | `save-load` |
| `alice-desktop-save-negative-artifact-contract` | `save-negative-artifact-contract` |
| `alice-desktop-scene-creation` | `scene-creation` |

Gated command smoke gaps:

| Scenario | Workflow |
| --- | --- |
| `alice-desktop-archive-fixture-smoke` | `archive-fixture-smoke` |
| `alice-desktop-exported-project-smoke` | `exported-project-ant-build-smoke` |
| `alice-desktop-failure-path-smoke` | `failure-path-smoke` |
| `alice-desktop-file-loader-smoke` | `file-loader-smoke` |
| `alice-desktop-future-ui-smoke` | `future-ui-smoke` |
| `alice-desktop-generated-listener-runtime-dispatch-smoke` | `generated-listener-runtime-dispatch-smoke` |
| `alice-desktop-issue-reporting-smoke` | `issue-reporting-smoke` |
| `alice-desktop-menu-action-smoke` | `menu-action-smoke` |
| `alice-desktop-migration-hotspot-characterization-smoke` | `migration-hotspot-characterization-smoke` |
| `alice-desktop-model-export-boundary-smoke` | `model-export-boundary-smoke` |
| `alice-desktop-netbeans-package-smoke` | `netbeans-package-smoke` |
| `alice-desktop-package-install-smoke` | `package-install-smoke` |
| `alice-desktop-procedure-edit-handoff-smoke` | `procedure-edit-handoff-smoke` |
| `alice-desktop-project-io-smoke` | `project-io-smoke` |
| `alice-desktop-run-window-contract` | `run-window-contract` |
| `alice-desktop-runtime-event-dispatch-smoke` | `runtime-event-dispatch-smoke` |
| `alice-desktop-save-menu-dialog-write-proof` | `save-menu-dialog-write-proof` |
| `alice-desktop-silver-thread-launch-build-run` | `silver-thread-launch-build-run` |
| `alice-desktop-tweedle-decoder-boundary-smoke` | `tweedle-decoder-boundary-smoke` |
| `alice-desktop-tweedle-decoder-this-call-smoke` | `tweedle-decoder-this-call-smoke` |
| `alice-desktop-wizard-palette-completion-smoke` | `wizard-palette-completion-smoke` |

Gated command smokes are automation coverage, but they remain evidence gaps
when `ALICE_QA_RUN_GATED_SMOKES=1` has not been used or when the required
artifacts are unavailable in the local environment.

## Corpus gaps

The scorecard looks for a checked-in, LFS-independent corpus manifest at:

```text
docs/reference/modernization-corpus-manifest.json
```

| Corpus signal | State | Meaning |
| --- | --- | --- |
| LFS-independent corpus manifest | Present | Found 2 representative checked-in corpus manifest entries. |
| Git LFS payloads | Not required | The scorecard does not fetch or inspect large binary project files. |

Corpus coverage is representative manifest evidence only; it is not full historical archive coverage and does not depend on local LFS payload availability.

## Remaining blockers

| Blocker | Current state | Required movement |
| --- | --- | --- |
| Aggregate coverage measurement | Missing aggregate JaCoCo CSV | Run the no-Sims coverage lane and regenerate the scorecard. |
| Ratcheted module measurements | Missing module JaCoCo CSVs for 6 ratcheted modules in this checkout | Run the no-Sims coverage lane and confirm each ratcheted module still emits a report. |
| 70% target evidence | Not claimable | Produce aggregate measured coverage at or above 70.0% before marking the target met. |
| Production hotspots | 4 files over 500 lines | Characterize behavior first; refactor only protected hotspots in focused changes. |
| Manual QA journeys | 8 scenarios require manual evidence | Add stable automation or collect accepted manual evidence for each workflow. |
| Gated QA smokes | 21 smokes are gated by local prerequisites | Run with `ALICE_QA_RUN_GATED_SMOKES=1` where prerequisites exist, or attach equivalent CI evidence. |
| Corpus manifest | Present | Keep manifest entries mapped to representative modernization journeys. |

## Interpretation notes

Use the scorecard as an evidence index, not as a single pass/fail badge.

Coverage ratchets answer "what regressions does CI prevent today?" The 70%
target status answers "can the project honestly claim the mission target
today?" Those are different questions. A low aggregate ratchet can be healthy
when it matches measured coverage with margin, and the 70% target must remain
not met or not claimable until current aggregate data proves otherwise.

Missing coverage reports are explicit blockers because they prevent
measurement. They are not interpreted as zero coverage and are not hidden
behind stale values.

Hotspots identify review and characterization risk. They do not authorize
production refactors by themselves. Follow the protected hotspot rule in
[Testing](../testing.md) before moving
production behavior.

Journey gaps are derived from the outside-in QA scenario catalog, not from
prose summaries. A manual scenario remains a gap until there is accepted
evidence or a stable automation mode. A gated smoke remains partial coverage
until it runs in an environment with the required prerequisites.

Corpus gaps are intentionally conservative. The scorecard can run without Git
LFS, so representative corpus coverage must be described by a small checked-in
manifest rather than inferred from local binary payloads.
