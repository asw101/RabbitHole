# Crusty proxy review lane for Alice modernization

Date: 2026-05-03

This document specifies the feature to build: a documentation-first, skeptical review lane for Alice modernization. The lane turns parallel branch activity, refactor proposals, class-size pressure, coverage claims, and test strategy into a local, evidence-backed gate artifact before any source-changing workstream is treated as ready for merge discussion.

The implemented increment is this `drinkme/` artifact contract plus a small `core/util` review-helper model for evidence collection, workstream classification, and gate evaluation. It does not change Alice runtime behavior, CI workflow, database schema, hooks, merge authority, remote state, upstream issues, or pull requests.

## Contents

- [Quick start](#quick-start)
- [Feature scope and implementation status](#feature-scope-and-implementation-status)
- [What the lane reviews](#what-the-lane-reviews)
- [Configuration](#configuration)
- [Evidence snapshot](#evidence-snapshot)
- [Workstream classification](#workstream-classification)
- [Risks and overreach](#risks-and-overreach)
- [Gate matrix](#gate-matrix)
- [Coverage policy](#coverage-policy)
- [Class-size policy](#class-size-policy)
- [Usage tutorials](#usage-tutorials)
- [API reference](#api-reference)
- [Database applicability](#database-applicability)
- [External service applicability](#external-service-applicability)
- [Security and artifact hygiene](#security-and-artifact-hygiene)
- [Troubleshooting](#troubleshooting)
- [Next high-value targets](#next-high-value-targets)
- [Evidence appendix](#evidence-appendix)

## Quick start

Use this feature manually whenever a modernization workstream claims to be ready for review, integration, or merge discussion. A later implementation may automate evidence collection, but it must preserve the same artifact boundary and gates.

1. Confirm the workstream is local, inspectable, and not just branch noise.
2. Capture the evidence snapshot.
3. Classify the workstream.
4. Apply the gate matrix.
5. Record risks, blockers, and next targets in `drinkme/`.
6. Stop before merge approval; maintainers approve integration separately.

Minimal review command set:

```bash
git --no-pager status --short --
git --no-pager branch --show-current
git --no-pager worktree list --porcelain
git submodule status tweedle-lang
test -d tweedle-lang/Grammar
```

If `tweedle-lang/Grammar` is missing, initialize the required grammar submodule before broad Maven validation:

```bash
git submodule update --init tweedle-lang
```

## Feature scope and implementation status

The feature is an artifact-first review/gating layer, not an application feature.

| Surface | Status for this feature |
|---|---|
| Durable output | Markdown review artifacts under `drinkme/`. |
| Current implementation | This branch documents the artifact contract and adds focused `org.alice.crustyproxy` review-helper classes under `core/util`. |
| Future implementation boundary | Automation may collect evidence and update artifacts, but must not change Alice runtime behavior as part of this lane. |
| Runtime source changes | No Alice runtime behavior changes. Source-changing modernization branches are reviewed by this lane, not approved by it. |
| Build or CI changes | None introduced by this branch. Existing Maven and GitHub Actions gates are referenced as evidence and validation targets. |
| Git hooks | None installed by this feature. Local hook inventory is evidence only, because hooks can affect reproducibility of local validation. |
| Database/API/external service surface | None; see [API reference](#api-reference), [Database applicability](#database-applicability), and [External service applicability](#external-service-applicability). |
| Merge authority | None. The lane records evidence and blockers; maintainers approve integration. |

## What the lane reviews

The proxy lane reviews modernization readiness, not code style theater.

| Review area | What good looks like | What blocks progress |
|---|---|---|
| Parallel workstreams | Active, stale, superseded, remote-only, and merge-ready branches are separated with local evidence. | Branch names are treated as proof of active work. |
| Test/refactor strategy | Characterization tests exist before behavior-affecting refactors. | Cleanup or class splitting touches behavior without before-state tests. |
| Coverage targets | Coverage starts with reporting-only module baselines and later gates changed modules against regression. | Global percentages are invented before tooling exists. |
| Class-size targets | Large classes identify risky seams that need characterization and careful extraction. | LOC reduction is used as the primary success metric. |
| External service claims | No external service integration is introduced for this lane. Future automation must prove the need for a bounded adapter before adding network calls. | Review tooling grows GitHub, CI, HTTP, or package-registry clients without explicit auth, timeout, retry, and failure-surfacing rules. |
| Risks and overreach | Ambitious work is scoped to named seams with rollback notes and pass/fail gates. | A branch mixes parser, UI, migration, packaging, and infrastructure changes. |
| Merge readiness | CI evidence, focused tests, artifact hygiene, and maintainer approval are all present. | A review artifact or agent declares itself sufficient for merge. |

## Configuration

### Required repository guardrails

These rules are always active for this lane:

- Do not open issues or pull requests against `TheAliceProject/alice3`.
- Do not use the upstream issue database for modernization tracking.
- Do not push to the `upstream-source` remote.
- Do not copy Alice source into `drinkme`; `drinkme` is for investigation artifacts only.
- Preserve current Alice 3 baseline behavior unless a behavior change is explicitly documented and tested.
- Add characterization tests before refactoring behavior.
- Initialize the Tweedle grammar submodule in every checkout or worktree before broad Maven validation.

Evidence: `AGENTS.md:7-18`.

### Build environment

| Setting | Required value | Evidence |
|---|---|---|
| JDK | Java 21 | `README.md:11-17`, `.github/workflows/alice-test-ci.yml:19-33` |
| Maven | 3.9.9 or later; CI pins 3.9.9 | `README.md:11-17`, `.github/workflows/alice-test-ci.yml:26-33` |
| Install4J | Required only for installer builds | `README.md:11-17`, `README.md:55-57` |
| Tweedle grammar | `tweedle-lang/Grammar` must exist before broad Maven validation | `README.md:26-43`, `core/tweedle/pom.xml:61-78` |
| Sims/nonfree modules | Included by root profile unless disabled with `-DincludeSims=false` | `pom.xml:681-696` |
| Installer module | Included only with `-DbuildInstaller=true` | `pom.xml:765-776` |

The saved Node preference for this environment is:

```bash
NODE_OPTIONS=--max-old-space-size=32768
```

This Java/Maven review lane does not require Node. If future documentation tooling or diagram generation uses Node, keep that saved preference unless `/home/azureuser/.amplihack/config` is deliberately changed.

## Evidence snapshot

Capture a snapshot before evaluating any candidate source-changing branch.

| Evidence | Command or source | Pass condition |
|---|---|---|
| Current branch | `git --no-pager branch --show-current` | The lane being reviewed is named. |
| Dirty state | `git --no-pager status --short --` | Unrelated generated files, caches, and source copies are excluded from merge discussion. |
| Worktrees | `git --no-pager worktree list --porcelain` | Active local workstreams are visible and tied to paths. |
| Branch divergence | `git for-each-ref` and `git rev-list --left-right --count develop...BRANCH` | Local/remote deltas and stale refs are identified before recommendations. |
| Tweedle readiness | `git submodule status tweedle-lang` and `test -d tweedle-lang/Grammar` | Broad Maven validation is not attempted until grammar files exist. |
| Reactor scope | Root `pom.xml` modules and profiles | The touched module and dependent modules are known. |
| CI gates | `.github/workflows/*.yml` | Existing tests, Checkstyle, and package checks are reused before adding new gates. |
| Hooks | Local Git common hooks directory | Hook side effects are visible before local validation is treated as reproducible evidence. |
| Test inventory | `git ls-files '*Test.java'` | Existing characterization seams are listed before new tests are proposed. |
| Java size inventory | `git ls-files '*.java'` plus LOC counts | Class-size risks are measured from tracked files only. |

Current local evidence in this worktree:

| Fact | Evidence |
|---|---|
| Current branch is `feat/alice-crusty-proxy-review`. | Local branch query. |
| Current dirty state contains untracked `.claude/`, `core/util/src/main/java/org/alice/crustyproxy/`, `core/util/src/test/java/org/alice/crustyproxy/`, and `drinkme/`; only the review helper, its tests, and the `drinkme/` artifact belong to this lane. | Local `git --no-pager status --short --`. |
| Active local feature worktrees are `feat/alice-code-atlas-bughunt`, `feat/alice-crusty-proxy-review`, `feat/alice-formal-specs`, `feat/alice-qa-outside-in`, and `feat/alice-source-tests-refactor`; each is at `cb8973df0f17f5a02a0700a58b0f951693083ab3`. | Local `git worktree list --porcelain`. |
| Local branch refs include `develop`, `feat/alice-*`, `loop62-*`, `loop63-*`, and `loop64-*`; same-name `origin/loop64-*` refs are not uniformly identical to local refs. | Local `git for-each-ref` over `refs/heads` and `refs/remotes`. |
| The current worktree has `tweedle-lang` as an uninitialized gitlink and `tweedle-lang/Grammar` is missing. | Local `git submodule status tweedle-lang` and `test -d tweedle-lang/Grammar`. |
| The local Git common hooks directory contains active `post-checkout`, `post-commit`, `post-merge`, and `pre-push` hooks; executable sample hooks are present but should not be mistaken for active policy. | Local `git rev-parse --git-common-dir` and hook inventory. |
| Tracked Java inventory is 5,003 Java files: 4,966 main Java files, 37 test Java files, 487,993 main LOC, 7,198 test LOC, 52 tracked main files over 500 lines, and 12 tracked main files over 1,000 lines. | Local tracked Java metrics from `git ls-files '*.java' ':!:tweedle-lang/**'`. |
| Largest tracked Java file is `core/story-api-migration/src/main/java/org/lgna/project/migration/ProjectMigrationManager.java` at 5,914 lines. | Local tracked Java metrics. |
| The review-helper implementation has 13 focused tests covering evidence snapshot, workstream classification, gate evaluation, and no-merge/no-remote side effects. | `core/util/src/test/java/org/alice/crustyproxy/*Test.java`; focused Maven run for those four test classes. |
| Existing CI has no-Sims test, Checkstyle, and NetBeans package lanes. | `.github/workflows/alice-test-ci.yml`, `.github/workflows/alice-checkstyle-ci.yml`, `.github/workflows/alice-netbeans-package-ci.yml`. |
| Root Maven evidence shows Surefire/JUnit and Checkstyle, but no operational JaCoCo/Cobertura/PIT coverage gate. | `pom.xml:243-244`, `pom.xml:424`, `pom.xml:508-519`, `pom.xml:564-581`; local search found no `jacoco`, `cobertura`, `pitest`, or `coverage` plugin references in POMs. |

## Workstream classification

Every branch or worktree gets one status before it is discussed as modernization progress.

| Status | Definition | Required evidence |
|---|---|---|
| Active local worktree | Branch has a checked-out local worktree. | `git worktree list --porcelain`. |
| Candidate branch | Branch exists locally or remotely but has no active worktree. | `git for-each-ref` plus absence from worktree list. |
| Superseded lane | Branch intent appears represented by a newer merged or same-HEAD branch. | Same commit, descendant relationship, or matching reviewed target. |
| Stale branch | Branch has no active worktree and no recent integration signal. | Ref age, divergence, and no active artifact. |
| Remote-only delta | Remote branch differs from local branch of the same name. | Local vs `origin/*` object IDs. |
| Merge-ready | Branch passes all applicable gates and has maintainer approval. | Gate evidence plus explicit approval record. |

Current classification:

| Workstream | Classification | Reason |
|---|---|---|
| `feat/alice-crusty-proxy-review` | Active review/documentation lane | Current worktree; purpose is the skeptical review and gate artifact. |
| `feat/alice-code-atlas-bughunt` | Active investigation lane | Active local worktree at the same baseline commit; findings are evidence, not merge pressure by themselves. |
| `feat/alice-formal-specs` | Active documentation/spec lane | Active local worktree at the same baseline commit; formal claims should feed gates, not bypass them. |
| `feat/alice-qa-outside-in` | Active QA/test-design lane | Active local worktree at the same baseline commit; should produce outside-in test scenarios and acceptance gates. |
| `feat/alice-source-tests-refactor` | Active implementation/test lane | Active local worktree at the same baseline commit; source changes need characterization tests and scoped diffs. |
| `loop62-*`, `loop63-*`, `loop64-*` | Candidate, stale, or superseded until proven otherwise | Many refs have overlapping themes; branch names alone do not prove current work. |
| `origin/loop64-*` vs local `loop64-*` | Remote-only delta risk | Same-name local and remote refs can differ and must be compared before use. |

## Risks and overreach

Ambition is allowed; ambiguity is not. These are the current skepticism points that should block or redirect follow-on work.

| Risk or overreach | Why it matters | Required response | Evidence |
|---|---|---|---|
| Treating same-baseline feature worktrees as completed work | Five active feature worktrees currently point at the same baseline commit, so branch presence is not proof of implementation progress. | Require branch-diff evidence and gate results before calling any lane ready. | Local `git worktree list --porcelain`. |
| Letting branch noise drive the roadmap | `loop62-*`, `loop63-*`, and `loop64-*` refs overlap by theme and same-name local/remote refs can differ. | Classify each branch as active, candidate, stale, superseded, remote-only delta, or merge-ready before using it as evidence. | Local `git for-each-ref` over local and remote refs. |
| Running broad Maven validation before Tweedle readiness | Missing grammar files can produce parser-generation failures that look like source regressions. | Initialize `tweedle-lang`, verify `tweedle-lang/Grammar`, then run broad validation. | `git submodule status tweedle-lang`; `core/tweedle/pom.xml:61-78`. |
| Refactoring large classes for vanity LOC wins | `ProjectMigrationManager.java` is enormous, but it protects serialized project compatibility; blind splitting is a regression factory. | Add characterization tests around named behavior seams before extraction. | Local tracked Java metrics; `ProjectMigrationManager.java` at 5,914 lines. |
| Inventing coverage percentages | No operational coverage gate exists in the inspected Maven/CI surfaces, so percentage targets would be fake precision. | Add reporting-only module baselines first, then gate changed modules against regression. | Root POM and workflow search found Surefire/JUnit and Checkstyle, not JaCoCo/Cobertura/PIT coverage gates. |
| Promoting the proxy lane into merge authority | The lane is evidence and gating infrastructure, not maintainer approval. | Keep approval as an explicit blocker until a maintainer records it outside the artifact. | Gate matrix approval gate; repository guardrails in `AGENTS.md:7-18`. |
| Letting review automation touch untrusted inputs loosely | Branch names, paths, archives, XML/JSON/Tweedle files, command output, and logs are attacker-controlled enough for tooling purposes. | Validate repository-root paths, reject traversal, treat parser/archive failures as fail-closed, and never embed credentials. | `org.alice.crustyproxy` tests cover path traversal and malformed branch rejection. |

## Gate matrix

Apply these pass/fail gates before any merge discussion.

| Gate | Pass condition | Blocks merge when |
|---|---|---|
| Scope gate | Branch has one named modernization seam and a short evidence-backed purpose. | Branch mixes unrelated parser, UI, packaging, migration, and infrastructure changes. |
| Characterization gate | Behavior touched by source changes has tests that would catch regression. | Refactor touches behavior with no before-state characterization. |
| Baseline behavior gate | Existing Alice 3 behavior is preserved or intentionally changed with tests and documentation. | Behavior changes are implied by cleanup/refactor wording. |
| Maven environment gate | Java 21, Maven 3.9.9, and required submodules are available. | Tweedle grammar is missing or Maven version is wrong. |
| No-Sims test gate | `mvn -DincludeSims=false -Dinstall4j.skip clean test` passes for reactor-affecting code changes. | Failures are skipped or dismissed without diagnosis. |
| Focused module test gate | Touched module tests pass with `mvn -pl MODULE -am test` where feasible. | Only broad CI is cited and module failures are unexamined. |
| NetBeans package gate | NetBeans-affecting changes preserve package artifact verification. | NBM, module JAR, source JAR, or docs ZIP expectations are unverified. |
| Checkstyle gate | Existing Checkstyle gate passes or scoped violations are fixed. | Style failures are accepted as modernization noise. |
| Review-helper test gate | Changes to `org.alice.crustyproxy` pass the focused `core/util` review-helper tests. | The lane's own evaluator/classifier/snapshot behavior is untested or failing. |
| Coverage baseline gate | Changed module has reporting-only baseline once coverage tooling exists. | Percentages are invented before tooling and module baselines exist. |
| Class-size gate | Large-class work is tied to a tested behavior seam. | PR is sold mainly as "class got smaller." |
| External service gate | This lane uses local repository evidence only, or a future service adapter has explicit auth, timeout, retry, and error-surfacing behavior. | API clients, GitHub/CI adapters, package-registry calls, or remote fetches are added casually or hide failures behind defaults. |
| Artifact hygiene gate | Generated outputs, runtime caches, copied source, and unrelated files are excluded. | `runs/`, `.claude/runtime/`, build output, copied source, or sensitive logs appear in the merge set. |
| Security gate | Inputs, paths, archives, XML/JSON/Tweedle files, dependencies, and logs are treated as untrusted. | Automation trusts branch names, paths, artifacts, project files, or generated output without validation. |
| Approval gate | Maintainer approval exists for integration. | The proxy lane, an agent, or a favorable artifact tries to self-approve merge. |

## Coverage policy

Coverage is a measured signal, not a slogan.

The lane uses this sequence:

1. Select one Maven coverage tool in a focused build/CI workstream.
2. Add reporting-only CI first; do not fail builds on a percentage.
3. Capture module baselines for `core/ide`, `core/tweedle`, `core/story-api-migration`, `core/model-loading`, `alice-ide`, and `netbeans`.
4. Gate changed modules against no regression from their baseline.
5. Introduce numeric floors only after stable CI data exists.

Do not set a single global repository percentage. With 487,993 tracked main LOC and 7,198 tracked test LOC, a global percentage mostly punishes legacy mass instead of improving the next risky seam.

## Class-size policy

Large classes are risk signals. They are not automatic refactor tickets.

| Trigger | Required response |
|---|---|
| A touched file is over 1,000 lines. | Name the seam, behavior risk, characterization tests, and focused validation gate. |
| A touched file is over 500 lines. | Review for risk and testability, but do not refactor solely because of size. |
| Behavior cannot be characterized without extraction. | Add characterization tests around current observable behavior first. |
| A class owns multiple unrelated modernization risks. | Split only along externally observable behavior boundaries. |
| Test setup requires UI, filesystem, or heavyweight runtime for pure decisions. | Extract pure planner or strategy objects with before/after equivalence tests. |

The top current class-size target is `ProjectMigrationManager.java` in `core/story-api-migration`, because it is 5,914 lines and sits on serialized project compatibility. That makes it high leverage and high risk, not a license for blind splitting.

## Usage tutorials

### Tutorial: Review a source-changing modernization branch

Use this path for a branch that changes Java source, resources, parser behavior, packaging, migration, project IO, model loading, or IDE launch behavior.

1. Capture the evidence snapshot.
2. Identify the touched module and modernization seam.
3. Classify the branch as active, candidate, stale, superseded, remote-only, or merge-ready.
4. Find existing tests for the touched behavior.
5. Require characterization tests before refactoring behavior.
6. Run the focused module gate where feasible.
7. Run the relevant CI-equivalent gate.
8. Record blockers and next targets in `drinkme/`.
9. Stop before merge approval.

Example gate result:

| Gate | Result | Evidence |
|---|---|---|
| Scope | PASS | Branch touches only `core/story-api-migration` migration seam. |
| Characterization | BLOCKED | Migration edge cases are not covered before extraction. |
| Maven environment | BLOCKED | `tweedle-lang/Grammar` missing in current worktree. |
| Approval | BLOCKED | No maintainer approval recorded. |

### Tutorial: Review a documentation-only proxy lane update

Use this path for changes that only update review/gating artifacts under `drinkme/`.

1. Confirm no Alice source is copied into `drinkme/`.
2. Confirm the artifact separates usage, evidence, risks, gates, and targets.
3. Confirm major claims cite local files, local command-derived evidence, or metrics.
4. Confirm the artifact does not claim merge approval.
5. Confirm no source, workflow, build, or dependency behavior changed.

Documentation-only artifacts do not need Maven validation unless they also change build, test, source, or CI files. This branch includes `core/util` review-helper source and tests, so its own focused helper tests are part of the gate.

### Tutorial: Decide whether a large-class refactor is acceptable

1. Identify the large file and its module.
2. Name the behavior or file format at risk.
3. Find or add characterization tests around current behavior.
4. Extract the smallest pure seam that removes test friction.
5. Preserve public behavior and serialized/file formats.
6. Stop when the seam is isolated; do not keep splitting for aesthetics.

Acceptable target statement:

> Extract the migration input classification decision from `ProjectMigrationManager` after adding fixtures that prove current success, malformed input, missing resource, version edge, and compatibility behavior.

Unacceptable target statement:

> Split `ProjectMigrationManager` because it is too long.

### Tutorial: Establish coverage without fake precision

1. Propose a Maven coverage tool in a focused build/CI branch.
2. Add report generation only.
3. Publish module-level reports.
4. Record initial baselines for touched modules.
5. Gate changed modules against no regression.
6. Raise thresholds later only after stable CI history.

The first useful coverage target is "changed module does not regress from its measured baseline," not "the repository must reach X%."

## API reference

The planned crusty proxy lane has no runtime API.

| API surface | Status |
|---|---|
| HTTP endpoints | None. |
| RPC methods | None. |
| CLI commands | None added by this lane. |
| Java helper package | Internal `org.alice.crustyproxy` review helpers under `core/util`; not an Alice runtime API. |
| External API clients | None. |
| Service adapters | None. |
| Request/response schemas | None. |
| Database schema | None. |
| Runtime service interface | None. |
| Versioned protocol | None. |

The lane's durable interface is the Markdown artifact shape:

| Section | Required content |
|---|---|
| Usage | How to apply the proxy lane to documentation-only and source-changing workstreams. |
| Configuration | Required environment, repo guardrails, Maven profiles, and saved preferences. |
| Evidence | Local facts with file, command, hook, ref, or metric citations. |
| Workstream classification | Active/candidate/stale/superseded/remote-only/merge-ready status. |
| Gate matrix | Pass/fail criteria and merge blockers. |
| Coverage policy | Tooling-first baseline process, no invented percentages. |
| Class-size policy | Size as risk signal tied to characterization and seams. |
| Security | Artifact hygiene, untrusted input treatment, and supply-chain review requirements. |
| Targets | Prioritized next work with module, first action, gate, and rationale. |

## Database applicability

Database design is not applicable to this planned proxy-lane feature or its documentation artifact.

| Database surface | Status |
|---|---|
| Database engine or service | None introduced. |
| Schema, table, collection, or index | None introduced. |
| Migration or rollback script | None introduced. |
| Persistence relationship or constraint | None introduced. |
| Query, transaction, or isolation behavior | None introduced. |
| Data retention or archival policy | None introduced by this lane. |

Alice project migration and model loading work may involve serialized files, XML, JSON, archives, or Tweedle inputs. Those are file-format compatibility surfaces, not database surfaces, and remain governed by characterization tests, parser/archive safety, and baseline behavior gates.

## External service applicability

External service integration is not applicable to this increment. The implemented lane reads local repository evidence through injected command and artifact-store boundaries; it does not call GitHub APIs, CI APIs, package registries, HTTP endpoints, remote Git refs, signing services, telemetry sinks, or any other network service.

| External integration concern | Status for this lane |
|---|---|
| API client implementation | None required; do not add one for the current artifact-first review lane. |
| Service adapter | None required; local command and artifact-store abstractions are sufficient. |
| External-call error handling | Not applicable because no external call is made. Local command failures must remain visible evidence, not success-shaped fallbacks. |
| Retry logic | Not applicable because there are no network calls. Do not add retry loops around local evidence collection to hide bad environment state. |
| Resilience | Fail closed on malformed branch names, unsafe artifact paths, missing Tweedle grammar, and failed validation gates. |
| Authentication and secrets | None required. Future GitHub, CI, publishing, or signing automation must use platform identity and must not embed credentials. |

If a later approved workstream adds a real external service, it needs a narrow adapter with explicit ownership, timeouts, bounded retries only for idempotent calls, surfaced error states, tests for failed and rate-limited responses, and artifact evidence showing why local repository evidence is no longer enough.

## Security and artifact hygiene

The proxy lane is local documentation and review infrastructure, so its security model is process security.

| Area | Requirement |
|---|---|
| Authentication | Do not add application auth for this lane. Future GitHub, CI, signing, publishing, or external automation must use platform identity and must not embed credentials. |
| Authorization | Keep merge, publish, push, and upstream actions under maintainer/repository controls. This lane can inspect and recommend only. |
| CI permissions | Future workflow changes must use the narrowest workable `permissions:` block and avoid privileged secrets on untrusted pull-request code. |
| Inputs | Treat branch names, paths, artifacts, XML/JSON/Tweedle content, archives, dependency metadata, command output, and logs as untrusted. |
| Paths | Validate paths against the repository root before reading, writing, or packaging. Reject traversal and absolute-path escape behavior. |
| Parsers and archives | Fail closed on malformed, oversized, path-traversing, external-entity, incompatible-version, missing-resource, or unexpected generated inputs. |
| Artifacts | Keep `drinkme/` text-only for investigation and review. Do not include copied Alice source, generated parser output, LFS/nonfree asset contents, build products, caches, secrets, or sensitive logs. |
| Supply chain | Dependency, Maven repository, plugin, GitHub Action, submodule, installer, signing, and generated-source changes require explicit review evidence. |

## Troubleshooting

| Symptom | Likely cause | Response |
|---|---|---|
| Maven reports missing Tweedle parser classes. | `tweedle-lang/Grammar` is missing in the current checkout or worktree. | Run `git submodule status tweedle-lang` and `test -d tweedle-lang/Grammar`; then run `git submodule update --init tweedle-lang`. |
| A branch looks active but has no worktree. | It may be candidate, stale, superseded, or remote-only branch noise. | Classify it with worktree/ref/divergence evidence before using it as modernization input. |
| A coverage target is requested. | Coverage tooling and baselines are not established. | Add reporting-only module baselines before any percentage gate. |
| A large class is proposed for splitting. | Size is being treated as the goal. | Require named behavior risk, characterization tests, and a seam extraction plan. |
| NetBeans changes pass unit tests but package behavior is uncertain. | Packaging has artifact-shape expectations beyond ordinary tests. | Run CI-equivalent NetBeans package verification for NBM, module JAR, source JAR, and docs ZIP. |
| No-Sims CI passes but Sims-related code changed. | The fast baseline disables Sims/nonfree modules. | Require explicit Sims/nonfree validation for `core-nonfree` or profile dependency changes. |
| `drinkme/` contains generated output or source copies. | Artifact hygiene has drifted. | Remove generated/runtime/source material from the review lane and keep summaries only. |
| A review artifact is cited as merge approval. | The proxy lane is being over-promoted. | Treat the artifact as evidence and gates only; maintainer approval remains required. |

## Next high-value targets

### P0: Initialize and verify the validation environment

| Field | Value |
|---|---|
| Module | `tweedle-lang` / root Maven reactor |
| First action | Run `git submodule update --init tweedle-lang`, then verify `tweedle-lang/Grammar`. |
| Gate | Do not treat Maven failures as source regressions until this passes. |
| Evidence | Current worktree has an uninitialized `tweedle-lang` gitlink and missing `tweedle-lang/Grammar`; `core/tweedle/pom.xml:61-78` requires grammar files. |

### P1: Workstream classification report

| Field | Value |
|---|---|
| Module | `drinkme/` |
| First action | Add a workstream map comparing active `feat/alice-*` worktrees, local `loop62-*`, `loop63-*`, and `loop64-*` refs, and remote `origin/loop64-*` deltas. |
| Gate | Every candidate merge branch has active, candidate, stale, superseded, remote-only, or merge-ready status. |
| Evidence | Active local feature worktrees exist at the same baseline commit; many loop refs require classification before conclusions. |

### P2: Coverage tooling proposal and reporting-only baseline

| Field | Value |
|---|---|
| Module | Root build and CI, design-approved before implementation |
| First action | Propose a reporting-only JaCoCo or equivalent Maven setup. |
| Gate | CI publishes module reports for `core/ide`, `core/tweedle`, `core/story-api-migration`, `core/model-loading`, `alice-ide`, and `netbeans` without failing on percentages. |
| Evidence | Root POM shows Surefire/JUnit and Checkstyle; local POM search found no operational coverage gate. |

### P3: Migration seam characterization around `ProjectMigrationManager`

| Field | Value |
|---|---|
| Module | `core/story-api-migration` |
| First action | Build a fixture matrix around supported project migration inputs and outputs before extraction. |
| Gate | Tests cover success, malformed input, version edge, missing resource, and compatibility scenarios. |
| Evidence | `ProjectMigrationManager.java` is the largest tracked Java file at 5,914 lines and sits on serialized project compatibility. |

### P4: Project IO/recovery seam consolidation

| Field | Value |
|---|---|
| Module | `core/ide` |
| First action | Review current project load/save/recovery tests and define one stable planner boundary for filesystem/UI separation. |
| Gate | Existing project recovery/load/save tests pass, and any planner extraction has equivalence tests. |
| Evidence | `core/ide` already has project load/save/recovery and URI loader test signal; project safety is high leverage. |

### P5: NetBeans generated project compatibility matrix

| Field | Value |
|---|---|
| Module | `netbeans` |
| First action | Extend generated project/package smoke coverage with a no-Sims, story API generated source, library registration, and Ant compile matrix. |
| Gate | CI-equivalent NetBeans package verification still passes and generated project compile smoke remains deterministic. |
| Evidence | `.github/workflows/alice-netbeans-package-ci.yml:32-52` verifies NBM, module JAR, source JAR, docs ZIP, and expected package contents. |

### P6: Model loading/export characterization

| Field | Value |
|---|---|
| Module | `core/model-loading` |
| First action | Add compatibility tests around model export/import edges before modifying `ModelResourceExporter.java` or Collada/glTF paths. |
| Gate | Tests cover representative model resources, missing/invalid resource behavior, and exported archive/file expectations. |
| Evidence | Local class-size metrics identify oversized format-sensitive model loading/export code; format drift would be expensive. |

## Evidence appendix

| Evidence | What it proves |
|---|---|
| `AGENTS.md:7-18` | Repository guardrails: no upstream issue/PR activity, no `upstream-source` pushes, no Alice source in `drinkme`, baseline compatibility, characterization before refactor, Tweedle submodule readiness. |
| `README.md:11-17` | Build tools are Java 21, Maven 3.9.9+, git, git-lfs, and optional Install4J. |
| `README.md:26-43` | Tweedle grammar submodule is required and missing grammar explains parser-generation failures. |
| `README.md:49-71` | Standard local commands include `mvn compile install` and `mvn test`. |
| `pom.xml:101-126` | Root reactor includes core modules, external modules, `alice-ide`, and `netbeans`. |
| `pom.xml:681-696` | `includeSims` profile adds nonfree Sims modules by default unless disabled. |
| `pom.xml:765-776` | `buildInstaller` profile adds installer packaging only when enabled. |
| `pom.xml:243-244`, `pom.xml:424`, `pom.xml:508-519` | Test execution is JUnit/Surefire based. |
| `pom.xml:564-581` | Checkstyle is configured in Maven. |
| `.github/workflows/alice-test-ci.yml:32-33` | CI has a no-Sims test baseline: `mvn -DincludeSims=false -Dinstall4j.skip clean test`. |
| `.github/workflows/alice-checkstyle-ci.yml:26-27` | CI runs Maven Checkstyle with `checkstyle.xml`. |
| `.github/workflows/alice-netbeans-package-ci.yml:32-52` | CI packages NetBeans without Sims and verifies expected artifacts. |
| `core/tweedle/pom.xml:61-78` | Maven enforces presence of Tweedle grammar files before validation proceeds. |
| `core/tweedle/pom.xml:89-107` | ANTLR generates Tweedle parser sources from `../../tweedle-lang/Grammar`. |
| `core/util/src/main/java/org/alice/crustyproxy/CrustyProxyLane.java` | Internal review-helper implementation for evidence snapshots, workstream classification, and pass/fail gate reports. |
| `core/util/src/test/java/org/alice/crustyproxy/*Test.java` | Focused tests lock the proxy lane's no-merge/no-remote behavior, fail-closed input handling, Tweedle gating, coverage policy, class-size policy, and approval blocking. |
| Local `git worktree list --porcelain` | Current active feature worktrees and shared baseline commit. |
| Local `git --no-pager status --short --` | Current dirty state and artifact hygiene boundary for this lane. |
| Local `git for-each-ref` over `refs/heads` and `refs/remotes` | Branch/ref noise, same-baseline feature refs, and local/remote branch deltas that require classification. |
| Local Git common hook inventory | Local validation may be affected by active hooks and should not be mistaken for clean CI behavior without inspection. |
| Local `git submodule status tweedle-lang` plus `test -d tweedle-lang/Grammar` | Current worktree is missing Tweedle grammar, blocking broad Maven validation. |
| Local tracked Java metrics | Current class-size and test-volume risk profile: 5,003 tracked Java files, 37 tracked test files, 52 tracked main files over 500 lines, and 12 tracked main files over 1,000 lines. |
| Focused `core/util` Maven test run | The review-helper test slice passes 13 tests without broad reactor validation. |
