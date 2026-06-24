---
title: Eatme Object Transform Workflow
description: Usage, configuration, API, and evidence reference for the deterministic Eatme object-transform validation workflow.
last_updated: 2026-06-19
review_schedule: quarterly
owner: maintainers
doc_type: reference
related:
  - ./howto/verify-ci-gui-eatme-xvfb-readiness.md
  - ./reference/ci-gui-eatme-xvfb-validation.md
---

# tools/eatme-object-transform

This document describes the contract for a deterministic non-interactive CLI
hook that validates the Alice objects-first path from a
source project through transform, placement, procedure edit, run, save, and
reopen. The workflow writes bounded evidence artifacts for every step or writes
explicit failure evidence before exiting nonzero.

Use this command when an Eatme or outside-in validation lane needs one stable
proof that object authoring still works across Alice project mutation,
persistence, and reload boundaries.

## Synopsis

```bash
tools/eatme-object-transform \
  --source-project <path-to-source.a3p> \
  --out-dir <evidence-directory> \
  --timeout-seconds 300 \
  --json
```

`--source-project`, `--out-dir`, and `--json` are required.
`--timeout-seconds` is optional and defaults to `300` seconds per workflow step.

## Prerequisites

Run commands from the repository root.

Initialize the Tweedle grammar submodule before broad Maven validation:

```bash
git submodule update --init tweedle-lang
```

Package Alice before running any Eatme wrapper:

```bash
mvn -DincludeSims=false -Dinstall4j.skip clean package -DskipTests
```

This populates `alice-ide/target/lib/`, which the launcher requires.

Run GUI-backed validation under Xvfb with headless mode disabled:

```bash
scripts/validate-gui-with-xvfb.sh \
  --timeout-seconds 1800 \
  --expect success \
  -- \
  env NODE_OPTIONS=--max-old-space-size=32768 \
    JAVA_TOOL_OPTIONS=-Djava.awt.headless=false \
    tools/eatme-object-transform \
      --source-project core/resources/src/application/resources/starter-projects/magicMinimum.a3p \
      --out-dir qa/outside-in/alice-desktop/evidence/eatme-object-transform-local \
      --timeout-seconds 300 \
      --json
```

## Arguments

| Argument | Required | Default | Description |
| --- | --- | --- | --- |
| `--source-project` | Yes | - | Readable regular `.a3p` project used as the read-only workflow input. May be absolute or relative. |
| `--out-dir` | Yes | - | Only directory where workflow evidence and mutated `.a3p` outputs are written. Created if it does not exist. |
| `--timeout-seconds` | No | `300` | Per-step timeout for transform, placement, edit, run, save, and reopen. Must be a positive integer. |
| `--json` | Yes | - | Emits the final structured workflow result to stdout for direct invocations. Required by the workflow contract even when pipelines consume the artifact file. |

Unexpected positional arguments, missing required flags, non-`.a3p` source
paths, unreadable source projects, invalid timeout values, and unwritable output
directories are validation failures.

`--source-project` is never modified. Every generated or mutated project is
written under `--out-dir` using the fixed artifact names listed below.

## Deterministic workflow

The workflow uses fixed inputs so automation can compare evidence across runs.

| Setting | Value |
| --- | --- |
| Object | `alice-gallery://animals/bunny` |
| Transform mode | `objects_first_default_pose_v1` |
| Procedure selector | `scene.eatmeObjectTransformStep` |
| Edit spec | `append-comment:eatme object transform proof` |
| Save selector | `scene.eatmeObjectTransformStep` |
| Reopen selector | `scene.eatmeObjectTransformStep` |

The Java entry point is `org.alice.tools.EatmeObjectTransformWorkflow`. It
orchestrates the existing Eatme object placement, procedure edit, world run,
project save, and project reopen seams without changing their standalone CLI
behavior.

## How it works

1. **Validate inputs** - resolves canonical paths, verifies the source `.a3p`,
   creates `--out-dir`, and confirms evidence artifacts cannot escape that
   directory.
2. **Preflight display state** - confirms GUI-backed steps have a display and
   `java.awt.headless=false` when those checks are required.
3. **Run transform** - writes the deterministic object-transform proof and a
   transformed project.
4. **Run placement** - places the configured gallery object and verifies
   placement evidence.
5. **Run edit** - targets the configured scene method and applies the configured
   edit spec.
6. **Run world** - invokes the configured scene method and captures bounded
   runtime evidence.
7. **Run save** - writes a saved `.a3p` and save evidence.
8. **Run reopen** - reloads the saved project and verifies the selector survived
   the persistence round trip.
9. **Verify artifacts** - checks every required artifact exists and is non-empty.
10. **Write final status** - writes `object-transform-workflow.json` and
    `status.txt` on success, or failure evidence and `status.txt` on failure.

Each step is bounded by `--timeout-seconds`. A timeout is a workflow failure; it
is never reported as a skipped or successful validation. GUI-backed runs should
also use an outer process timeout such as
`scripts/validate-gui-with-xvfb.sh --timeout-seconds 1800` because Swing and
Croquet work may not interrupt cleanly inside the Java process.

## Output artifacts

All artifact names are stable. Paths below are relative to `--out-dir`.

```text
object-transform-workflow.json
status.txt
transform/object-transform.json
transform/transformed-project.a3p
placement/placement.json
placement/scene.diff.json
placement/placed-project.a3p
edit/first-lesson-code-editor-action-proof.json
edit/edited-project.a3p
run/world-run.json
run/runtime.log
save/project-save.json
save/saved-project.a3p
reopen/reopen-evidence.json
reopen/reopened-state.json
reopen/reopened.a3p
```

On failure, these additional artifacts are written when `--out-dir` is usable:

```text
object-transform-workflow-failure.json
status.txt
<failed-step>/step-failure.json
```

If `--out-dir` itself cannot be created or written, the command exits nonzero
and reports the failure on stderr because no trustworthy evidence directory is
available.

JSON and status artifacts are safe writes: the workflow writes each file to a
temporary file in the same directory, flushes it, then moves it into its final
name. `status.txt` with `outcome=passed` is written only after every required
artifact exists, is non-empty, and has passed workflow verification. If any
artifact or status write fails, the workflow exits nonzero and reports
`failure_kind=artifact-write-failure` when the evidence directory is usable.

### stdout - workflow result JSON

Schema: `eatme.object-transform-workflow-result/v1`

Direct `tools/eatme-object-transform --json` invocations emit the final workflow
JSON to stdout. When running through `scripts/validate-gui-with-xvfb.sh`, do not
parse the wrapper stdout as pure JSON; the Xvfb harness may write its own
launcher output. Pipeline consumers should read
`out-dir/object-transform-workflow.json` after the wrapped command exits.

```json
{
  "schema_version": "eatme.object-transform-workflow-result/v1",
  "status": "passed",
  "source_project": "core/resources/src/application/resources/starter-projects/magicMinimum.a3p",
  "object": "alice-gallery://animals/bunny",
  "selector": "scene.eatmeObjectTransformStep",
  "timeout_seconds": 300,
  "workflow_artifact": "object-transform-workflow.json",
  "status_artifact": "status.txt",
  "steps": [
    {
      "name": "transform",
      "status": "passed",
      "duration_millis": 812,
      "artifacts": [
        "transform/object-transform.json",
        "transform/transformed-project.a3p"
      ]
    },
    {
      "name": "placement",
      "status": "passed",
      "duration_millis": 1431,
      "artifacts": [
        "placement/placement.json",
        "placement/scene.diff.json",
        "placement/placed-project.a3p"
      ]
    },
    {
      "name": "edit",
      "status": "passed",
      "duration_millis": 564,
      "artifacts": [
        "edit/first-lesson-code-editor-action-proof.json",
        "edit/edited-project.a3p"
      ]
    },
    {
      "name": "run",
      "status": "passed",
      "duration_millis": 2190,
      "artifacts": [
        "run/world-run.json",
        "run/runtime.log"
      ]
    },
    {
      "name": "save",
      "status": "passed",
      "duration_millis": 488,
      "artifacts": [
        "save/project-save.json",
        "save/saved-project.a3p"
      ]
    },
    {
      "name": "reopen",
      "status": "passed",
      "duration_millis": 721,
      "artifacts": [
        "reopen/reopen-evidence.json",
        "reopen/reopened-state.json",
        "reopen/reopened.a3p"
      ]
    }
  ]
}
```

| Field | Type | Description |
| --- | --- | --- |
| `schema_version` | string | Always `eatme.object-transform-workflow-result/v1`. |
| `status` | string | `passed` on success. Failure results use the failure schema. |
| `source_project` | string | Source `.a3p` path as a stable relative path when possible. |
| `object` | string | Deterministic gallery object identifier. |
| `selector` | string | Scene selector used for edit, run, save, and reopen checks. |
| `timeout_seconds` | integer | Per-step timeout used by the run. |
| `workflow_artifact` | string | Always `object-transform-workflow.json`. |
| `status_artifact` | string | Always `status.txt`. |
| `steps` | array | Ordered step summaries for transform, placement, edit, run, save, and reopen. |

### out-dir/object-transform-workflow.json

The same JSON object emitted to stdout on success. Pipeline tools should prefer
this file when collecting evidence after the process exits.

### out-dir/status.txt

Success status:

```text
outcome=passed
executionStatus=executed
executionClaim=eatme-object-transform-workflow-executed
sourceProject=core/resources/src/application/resources/starter-projects/magicMinimum.a3p
workflowArtifact=object-transform-workflow.json
```

Failure status:

```text
outcome=failed
executionStatus=executed
executionClaim=eatme-object-transform-workflow-failed
failureArtifact=object-transform-workflow-failure.json
failedStep=placement
failureKind=timeout
```

`status.txt` is the canonical summary for outside-in evidence collection. Do not
report a run as successful unless it contains `outcome=passed`.

### out-dir/object-transform-workflow-failure.json

Schema: `eatme.object-transform-workflow-failure/v1`

```json
{
  "schema_version": "eatme.object-transform-workflow-failure/v1",
  "status": "failed",
  "failure_kind": "timeout",
  "failed_step": "placement",
  "message": "step timed out after 300 seconds",
  "source_project": "core/resources/src/application/resources/starter-projects/magicMinimum.a3p",
  "timeout_seconds": 300,
  "completed_steps": [
    "transform"
  ],
  "failure_artifact": "placement/step-failure.json"
}
```

| Field | Type | Description |
| --- | --- | --- |
| `schema_version` | string | Always `eatme.object-transform-workflow-failure/v1`. |
| `status` | string | Always `failed`. |
| `failure_kind` | string | Stable failure category from the taxonomy below. |
| `failed_step` | string | Workflow step that failed, or `preflight` for argument, project, output, or display failures. |
| `message` | string | Stable diagnostic message suitable for CI logs. |
| `completed_steps` | array | Step names that completed and passed artifact verification before the failure. |
| `failure_artifact` | string | Step-local failure artifact when a step had begun. |

### Failure taxonomy

| `failure_kind` | Typical `failed_step` | Meaning |
| --- | --- | --- |
| `argument-error` | `preflight` | Required arguments were missing, malformed, or unexpected. |
| `package-preflight` | `preflight` | `alice-ide/target/lib` was missing before Java launch. |
| `source-project-validation` | `preflight` | `--source-project` was missing, unreadable, not a regular `.a3p`, or otherwise invalid. |
| `output-directory-failure` | `preflight` | `--out-dir` could not be created, canonicalized, or proven writable. |
| `display-preflight` | `preflight` | GUI-backed execution lacked `DISPLAY` or required `java.awt.headless=false` state. |
| `timeout` | current workflow step | A bounded step exceeded `--timeout-seconds`. |
| `transform-failure` | `transform` | Deterministic object transform did not complete successfully. |
| `placement-failure` | `placement` | Gallery object placement or placement evidence failed. |
| `edit-failure` | `edit` | Procedure selector edit or edit proof failed. |
| `run-failure` | `run` | World run invocation or runtime evidence failed. |
| `save-failure` | `save` | Project save or save evidence failed. |
| `reopen-failure` | `reopen` | Saved project reopen or reopened-state verification failed. |
| `missing-artifact` | current workflow step | A required artifact was absent or empty after a step completed. |
| `artifact-write-failure` | current workflow step or `finalize` | JSON, status, project, log, or failure evidence could not be safely written. |
| `exception` | current workflow step or `preflight` | Unexpected runtime exception not covered by a narrower category. |

`package-preflight` and `output-directory-failure` may be reported only on
stderr when the wrapper or preflight cannot establish a trustworthy `--out-dir`
for JSON evidence.

### step-failure.json

Schema: `eatme.object-transform-step-failure/v1`

```json
{
  "schema_version": "eatme.object-transform-step-failure/v1",
  "step": "placement",
  "status": "failed",
  "failure_kind": "missing-artifact",
  "message": "required artifact was not written: placement/scene.diff.json",
  "required_artifacts": [
    "placement/placement.json",
    "placement/scene.diff.json",
    "placement/placed-project.a3p"
  ]
}
```

## Exit codes

| Code | Meaning |
| ---: | --- |
| `0` | Success; stdout JSON and all required evidence artifacts were written. |
| `2` | Argument, package, source project, output directory, display preflight, timeout, or expected validation failure. |
| `3` | Unexpected runtime failure from the Java workflow entry point. |

## Examples

### Local full-path validation under Xvfb

```bash
git submodule update --init tweedle-lang
mvn -DincludeSims=false -Dinstall4j.skip clean package -DskipTests

rm -rf qa/outside-in/alice-desktop/evidence/eatme-object-transform-local

scripts/validate-gui-with-xvfb.sh \
  --timeout-seconds 1800 \
  --expect success \
  -- \
  env NODE_OPTIONS=--max-old-space-size=32768 \
    JAVA_TOOL_OPTIONS=-Djava.awt.headless=false \
    tools/eatme-object-transform \
      --source-project core/resources/src/application/resources/starter-projects/magicMinimum.a3p \
      --out-dir qa/outside-in/alice-desktop/evidence/eatme-object-transform-local \
      --timeout-seconds 300 \
      --json
```

Inspect the final evidence:

```bash
python3 -m json.tool \
  qa/outside-in/alice-desktop/evidence/eatme-object-transform-local/object-transform-workflow.json

grep '^outcome=passed$' \
  qa/outside-in/alice-desktop/evidence/eatme-object-transform-local/status.txt

test -s qa/outside-in/alice-desktop/evidence/eatme-object-transform-local/reopen/reopened.a3p
```

### Pipeline integration

```bash
scripts/validate-gui-with-xvfb.sh \
  --timeout-seconds 1800 \
  --expect success \
  -- \
  env NODE_OPTIONS=--max-old-space-size=32768 \
    JAVA_TOOL_OPTIONS=-Djava.awt.headless=false \
    tools/eatme-object-transform \
      --source-project core/resources/src/application/resources/starter-projects/magicMinimum.a3p \
      --out-dir qa/outside-in/alice-desktop/evidence/eatme-object-transform-ci \
      --timeout-seconds 300 \
      --json

python3 -c "
import json, sys
with open('qa/outside-in/alice-desktop/evidence/eatme-object-transform-ci/object-transform-workflow.json', encoding='utf-8') as f:
    r = json.load(f)
assert r['schema_version'] == 'eatme.object-transform-workflow-result/v1'
assert r['status'] == 'passed'
assert [s['name'] for s in r['steps']] == ['transform', 'placement', 'edit', 'run', 'save', 'reopen']
print('Eatme object-transform evidence:', r['workflow_artifact'])
"
```

### Failure handling

```bash
tools/eatme-object-transform \
  --source-project /nowhere/missing.a3p \
  --out-dir qa/outside-in/alice-desktop/evidence/eatme-object-transform-failure \
  --timeout-seconds 300 \
  --json
# Exit code 2
```

Inspect the failure evidence:

```bash
python3 -m json.tool \
  qa/outside-in/alice-desktop/evidence/eatme-object-transform-failure/object-transform-workflow-failure.json

cat qa/outside-in/alice-desktop/evidence/eatme-object-transform-failure/status.txt
```

## Java API

**Class:** `org.alice.tools.EatmeObjectTransformWorkflow`
**Module:** `core/ide`

### Public entry points

```java
// CLI entry point. Exits with the workflow exit code.
public static void main(String[] args)

// Testable entry point. Returns an exit code and writes to supplied streams.
static int run(String[] args, PrintStream out, PrintStream err)
```

### Internal helper contracts

```java
record Arguments(
    Path sourceProject,
    Path outDir,
    int timeoutSeconds,
    boolean json)

record StepResult(
    String name,
    String status,
    long durationMillis,
    List<String> artifacts)

record WorkflowFailure(
    String failureKind,
    String failedStep,
    String message,
    List<String> completedSteps)
```

The workflow helper types are internal to `org.alice.tools`. Tests may exercise
them from the same package, but downstream automation should depend on the CLI
contract and JSON schemas, not Java internals.

## Configuration

### Wrapper launcher

`tools/eatme-object-transform` uses the standard Eatme launcher shape:

```bash
java \
  -ea \
  -Dorg.alice.ide.rootDirectory=./core/resources/target/distribution \
  -Dedu.cmu.cs.dennisc.java.util.logging.Logger.Level=WARNING \
  -cp "alice-ide/target/*:alice-ide/target/lib/*" \
  org.alice.tools.EatmeObjectTransformWorkflow \
  "$@"
```

The wrapper validates `alice-ide/target/lib` before launching Java. If the
package step has not been run, it exits `2` with a clear stderr message.
After the package preflight passes, the wrapper forwards `"$@"` to
`org.alice.tools.EatmeObjectTransformWorkflow` unchanged and exits with the Java
process exit code.

`tools/eatme-transform-object` is the per-phase transform hook consumed by the
objects-first Eatme flow. It uses the same packaged-Alice launcher shape, accepts
`--project`, `--object-identifier`, `--evidence-dir`, and `--json`; optional
`--target-position x,y,z` and `--scale` override the default bounded target
(`1.5,0.0,-2.0`, scale `1.25`). It emits
`eatme.alice-object-transform-result/v1` with `object-transform.json` plus
`transformed-project.a3p` under the supplied evidence directory. The artifact
records the requested target transform as evidence and persists a scene method
marker in the transformed project; it does not claim arbitrary visual rendering
correctness.

### Environment

| Variable or property | Required | Description |
| --- | --- | --- |
| `NODE_OPTIONS=--max-old-space-size=32768` | Recommended in outside-in lanes | Keeps Node-backed QA orchestration consistent with repository defaults. |
| `JAVA_TOOL_OPTIONS=-Djava.awt.headless=false` | Required for GUI-backed runs | Ensures Alice desktop and Croquet code can use the X display. |
| `DISPLAY` | Required for GUI-backed runs | Supplied by `scripts/validate-gui-with-xvfb.sh` or the CI Xvfb action. |
| `org.alice.ide.rootDirectory` | Set by wrapper | Points Alice to `./core/resources/target/distribution`. |

## Security considerations

- Reads only the declared `--source-project`.
- Treats `--source-project` as immutable input.
- Writes all mutated `.a3p` files and evidence only under the canonical
  `--out-dir`.
- Rejects path traversal for workflow-managed artifact names.
- Does not construct shell commands from user input.
- Does not read or write credentials, environment dumps, or project internals
  outside the narrow evidence schemas.
- Treats missing artifacts, invalid projects, failed saves, failed reopens,
  display failures, and timeouts as explicit failures.
- Does not open issues or pull requests against upstream Alice repositories.

## Testing

Expected focused tests and contracts:

| Check | What it verifies |
| --- | --- |
| `EatmeObjectTransformWorkflowTest` | Argument parsing, invalid source project failures, timeout configuration, missing artifact failure evidence, and JSON/status output shape. |
| `EatmeObjectTransformCommandTest` | Wrapper precondition, packaged classpath contract, Java entry point, and argument forwarding. |
| `EatmeObjectTransformWorkflowXvfbTest` | Xvfb-backed full workflow success for transform, placement, edit, run, save, and reopen with `java.awt.headless=false`. |
| `test-eatme-integration-documentation-contract.sh` | Documentation discoverability plus launcher-contract assertions for `tools/eatme-object-transform`. |

Run focused tests:

```bash
mvn -DincludeSims=false -Dinstall4j.skip \
  -pl core/ide -am test \
  -Dtest='EatmeObjectTransform*Test' \
  -Dsurefire.failIfNoSpecifiedTests=false
```

Run the full-path test under Xvfb:

```bash
scripts/validate-gui-with-xvfb.sh \
  --timeout-seconds 1800 \
  --expect success \
  -- \
  mvn -DincludeSims=false -Dinstall4j.skip \
    -pl core/ide -am test \
    -Djava.awt.headless=false \
    -Dtest=EatmeObjectTransformWorkflowXvfbTest
```

## Relationship to individual Eatme wrappers

`eatme-object-transform` is the end-to-end orchestration wrapper. The individual
wrappers remain available for narrower proof points:

```text
eatme-object-transform
  source-project.a3p
    |-- transform  -> transform/transformed-project.a3p
    |-- placement  -> placement/placed-project.a3p
    |-- edit       -> edit/edited-project.a3p
    |-- run        -> run/world-run.json
    |-- save       -> save/saved-project.a3p
    `-- reopen     -> reopen/reopened.a3p
```

Use the individual wrappers when validating one seam. Use
`eatme-object-transform` when validating that the objects-first path completes
without hanging and leaves durable evidence across project save and reopen.
