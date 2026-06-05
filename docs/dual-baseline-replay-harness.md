# Dual-baseline replay harness

The dual-baseline replay harness compares deterministic RabbitHole project,
source, archive, manifest, and resource summaries against a local preserved
Alice baseline checkout when one is configured.

It is a headless compatibility lane. It does not clone upstream repositories,
require external services, or commit generated Alice binary
payloads.

## Contents

- [Quick start](#quick-start)
- [What it proves](#what-it-proves)
- [Summary format](#summary-format)
- [Fallback mode](#fallback-mode)
- [Strict baseline mode](#strict-baseline-mode)
- [Baseline process contract](#baseline-process-contract)
- [Configuration reference](#configuration-reference)
- [Test-scope Java API](#test-scope-java-api)
- [Relationship to RabbitHole baseline parity](#relationship-to-rabbithole-baseline-parity)
- [Relationship to eatme](#relationship-to-eatme)
- [CI behavior](#ci-behavior)
- [Troubleshooting](#troubleshooting)

## Quick start

Run it in the default CI-safe fallback mode:

```bash
git submodule update --init tweedle-lang
mvn -pl core/story-api-migration \
  -DincludeSims=false \
  -Dinstall4j.skip \
  -Dcheckstyle.skip \
  -Djava.awt.headless=true \
  -Dtest=DualBaselineReplayHarnessTest \
  test
```

Run the strict comparison against a local preserved baseline checkout:

```bash
git submodule update --init tweedle-lang
mvn -pl core/story-api-migration \
  -DincludeSims=false \
  -Dinstall4j.skip \
  -Dcheckstyle.skip \
  -Djava.awt.headless=true \
  -Dtest=DualBaselineReplayHarnessTest \
  -Drabbithole.baseline.checkout=/absolute/path/to/preserved-alice-baseline \
  test
```

The baseline path can also come from the environment:

```bash
export RABBITHOLE_BASELINE_CHECKOUT=/absolute/path/to/preserved-alice-baseline
mvn -pl core/story-api-migration \
  -DincludeSims=false \
  -Dinstall4j.skip \
  -Dcheckstyle.skip \
  -Djava.awt.headless=true \
  -Dtest=DualBaselineReplayHarnessTest \
  test
```

The Maven system property wins when both are set.

## What it proves

The harness replays the same generated cases through two summary providers:

| Provider | Source |
| --- | --- |
| RabbitHole | The current checkout under test. |
| Baseline | The optional local checkout configured by `rabbithole.baseline.checkout` or `RABBITHOLE_BASELINE_CHECKOUT`. |

For each replay case, both providers emit normalized UTF-8 text. The comparator
requires exact text equality. A mismatch fails the test with a compact unified
diff that names the replay case and the differing section.

The generated cases are intentionally small and deterministic:

| Case | Coverage |
| --- | --- |
| `empty-project` | Minimal project save, reopen, archive entry, and manifest shape. |
| `project-with-text-resource` | Resource identity, type, hash, and archive placement for a single generated resource. |
| `project-with-multiple-resources` | Stable ordering and hashing across multiple generated resources. |
| `project-with-generated-source-shape` | Source tree paths, source hashes, and archive/source relationship summaries. |

The cases are generated in memory during the test run. Temporary `.a3p`, `.a3w`,
source trees, and resource payloads are deleted with the JUnit temporary
directory.

## Summary format

`ReplaySummaryWriter` writes one normalized summary per replay case. The summary
is text-only and stable across platforms.

Sections are emitted in this order:

1. `case` — replay case id and summary schema version.
2. `source-tree` — generated source paths, sorted lexicographically.
3. `source-hashes` — SHA-256 hashes of normalized source file text.
4. `archives` — `.a3p` and `.a3w` entry names, sorted lexicographically.
5. `manifest` — stable manifest fields such as project type and archive type.
6. `resources` — resource identity, declared type, normalized archive path, byte
   length, and SHA-256 payload hash.

Normalization rules:

| Data | Rule |
| --- | --- |
| Text encoding | UTF-8. |
| Line endings | Normalize to `\n` before hashing or comparing. |
| Paths | Use `/`; reject absolute paths, `..`, Windows drive-letter paths, and backslash paths. |
| Ordering | Sort paths, archive entries, manifest keys, and resources lexicographically. |
| Hashing | Use SHA-256. Hash text after line-ending normalization; hash binary resources by bytes. |
| Omitted fields | Do not include absolute paths, temporary directories, usernames, timestamps, JVM object ids, ZIP timestamps, compression metadata, or environment dumps. |

Example summary shape:

```text
case: project-with-text-resource
summary-schema: rabbithole.dual-baseline-summary/v1

[source-tree]
src/ProjectWithTextResource.twe

[source-hashes]
src/ProjectWithTextResource.twe sha256:4f7b...

[archives:a3p]
manifest.json
programType.xml
resources.xml
resources/generated-text-resource.txt
version.txt

[archives:a3w]
manifest.json
resources/generated-text-resource.txt
src/ProjectWithTextResource.twe
version.txt

[manifest:a3p]
archiveType=a3p
projectName=ProjectWithTextResource

[resources]
generated-text-resource.txt type=text/plain bytes=42 sha256:9b13...
```

Hashes in real summaries are full 64-character SHA-256 hex strings.

## Fallback mode

Fallback mode is active when no baseline checkout is configured.

In fallback mode, the harness still generates every replay case, runs the
RabbitHole summary provider, and validates that:

- summaries are deterministic across repeated runs;
- summaries contain only normalized UTF-8 text;
- unsafe archive paths are rejected;
- no absolute paths, temp roots, timestamps, or binary payloads appear in the
  text;
- the comparison seam works through fake matching and mismatching providers.

Fallback mode is the mode used by normal CI. It proves the harness and summary
contract remain healthy without requiring a preserved baseline checkout on the
runner.

## Strict baseline mode

Strict mode is active when a baseline checkout path is configured.

The configured path must be a local checkout that can run the baseline replay
summary script for the same `DualBaselineReplayHarnessTest` cases. The current
checkout starts the baseline provider through `ProcessBuilder` with an argument
list and an explicit working directory; it never builds a shell command from the
configured path.

Strict mode fails when:

- the configured path does not exist;
- the configured path is not a directory;
- the baseline checkout cannot run `scripts/rabbithole-replay-summary`;
- the baseline process exits non-zero or times out;
- the baseline emits malformed summary text;
- RabbitHole and baseline summaries differ.

Missing configuration is not a failure. Invalid explicit configuration is a
failure, because it means the user asked for strict comparison and supplied an
unusable baseline.

## Baseline process contract

`BaselineReplayRunner` runs the preserved baseline checkout as a local process.
The current checkout owns case generation and comparison; the baseline process
only receives a case id and returns the normalized summary text for that case.

The invocation shape is an argument list, not a shell command:

```text
<baseline-checkout>/scripts/rabbithole-replay-summary \
  --case <case-id> \
  --schema rabbithole.dual-baseline-summary/v1
```

The runner sets the process working directory to the configured baseline
checkout. `<case-id>` must be one of the case ids generated by
`ReplayCaseFactory`; arbitrary file paths or unregistered ids are rejected before
process launch.

Case selection is owned by the current checkout. `DualBaselineReplayHarnessTest`
iterates the deterministic `ReplayCaseFactory` cases and launches one baseline
process per case. The baseline checkout does not discover cases, read a manifest,
or choose which cases to skip.

### stdout contract

The baseline process writes exactly one complete normalized summary to stdout.
The first two non-empty lines must identify the case and schema:

```text
case: <case-id>
summary-schema: rabbithole.dual-baseline-summary/v1
```

The remaining stdout must follow the [summary format](#summary-format). Stdout
is decoded as UTF-8, normalized to `\n`, and rejected if it contains binary
control bytes, absolute paths, traversal paths, local temp roots, timestamps, or
environment dumps.

### stderr and exit contract

Stderr is diagnostic only. It is included in the JUnit failure message when the
process exits non-zero, times out, or emits malformed stdout. Stderr must not be
parsed as compatibility data.

Exit code `0` means stdout contains a complete summary. Any non-zero exit code
fails strict mode for that replay case.

### Timeout contract

Each baseline process has a per-case timeout controlled by
`rabbithole.dualBaseline.timeoutSeconds`. A timeout fails strict mode and reports
the case id, timeout value, process command arguments, and captured stderr. The
runner must terminate the timed-out process and must not compare partial stdout.

## Configuration reference

| Setting | Type | Default | Description |
| --- | --- | --- | --- |
| `rabbithole.baseline.checkout` | Maven system property | unset | Absolute or relative path to the local preserved baseline checkout. Takes precedence over the environment variable. |
| `RABBITHOLE_BASELINE_CHECKOUT` | Environment variable | unset | Local preserved baseline checkout path used when the Maven property is unset. |
| `rabbithole.dualBaseline.timeoutSeconds` | Maven system property | `120` | Maximum time allowed for the external baseline summary process. |

## Test-scope Java API

The harness lives under:

```text
core/story-api-migration/src/test/java/org/lgna/project/io/compat/
```

The API is test-scope only. It is not part of the production classpath.

| Type | Purpose |
| --- | --- |
| `ReplayCase` | Immutable replay scenario with a stable id, generated project, and optional generated source inputs. |
| `ReplaySourceInput` | Deterministic generated source input used by source tree and source hash summary sections. |
| `ReplaySummary` | Normalized UTF-8 summary text for one replay case. |
| `ReplaySummaryProvider` | Seam implemented by RabbitHole, real baseline, and test fake providers. |
| `ReplayCaseFactory` | Creates deterministic replay cases without committed binary fixtures. |
| `ReplaySummaryWriter` | Writes source tree, source hash, archive entry, manifest, and resource summaries. |
| `ReplaySummaryComparator` | Performs exact text comparison and produces compact unified diff failures. |
| `RabbitHoleReplayRunner` | Serializes and reopens generated projects through RabbitHole I/O, then summarizes the results. |
| `BaselineMode` | Models baseline availability as available or unavailable with a reason. |
| `BaselineCheckout` | Resolves and validates baseline configuration from system property or environment variable. |
| `BaselineReplayRunner` | Runs the configured preserved baseline checkout in a separate local process and collects summaries. |
| `DualBaselineReplayHarnessTest` | JUnit coverage for fallback determinism, fake baseline match/mismatch behavior, invalid baseline handling, and summary hygiene. |

### Provider contract

`ReplaySummaryProvider` accepts a `ReplayCase` and returns one
`ReplaySummary`. Providers must be deterministic, must use the shared
`ReplaySummaryWriter`, and must surface errors as test failures instead of
returning partial or success-shaped summaries.

Provider implementations must not:

- contact the network;
- read credentials;
- clone or fetch upstream repositories;
- write committed binary fixtures;
- include machine-local paths or timestamps in summary text.

### Comparator contract

`ReplaySummaryComparator` compares normalized text exactly. It does not ignore
sections, reorder lines, or apply fuzzy matching. Compatibility differences must
be reviewed deliberately, not hidden by tolerant comparison.

Mismatch failures include:

- replay case id;
- provider names;
- first differing section when identifiable;
- a compact unified diff around the changed lines.

## Relationship to RabbitHole baseline parity

[`RabbitHole baseline parity`](rabbithole-baseline-parity.md) compares current
RabbitHole behavior against committed text snapshots in the `netbeans` module.
It is the lightweight generated-output snapshot lane for NetBeans project
generation and archive shape.

The dual-baseline replay harness is stricter when a preserved baseline checkout
is configured: it compares current RabbitHole output directly against a local
baseline process for the same generated cases. It also has a fallback mode so CI
can keep validating deterministic summary generation when the baseline checkout
is unavailable.

Use this harness when changing project I/O, archive entry layout, manifest
serialization, resource identity handling, or generated source summary behavior
in `core/story-api-migration`.

## Relationship to eatme

`eatme` is an outside-in workflow for desktop-oriented save, reopen, run, and
evidence artifacts. It produces pipeline evidence such as JSON proof files and
round-tripped project artifacts for user-facing flows.

The dual-baseline replay harness is not `eatme`.

| Harness | Purpose | Artifacts |
| --- | --- | --- |
| `eatme` | Prove broader save/reopen/run workflows and collect evidence artifacts. | JSON evidence and temporary project artifacts. |
| Dual-baseline replay | Compare deterministic headless summaries against a preserved baseline checkout when available. | Normalized UTF-8 text summaries only. |

Use `eatme` for outside-in workflow evidence. Use the dual-baseline replay
harness for headless compatibility confidence against the preserved Alice
baseline.

## CI behavior

CI runs `DualBaselineReplayHarnessTest` in fallback mode through the existing
Maven test lanes. CI does not set
`RABBITHOLE_BASELINE_CHECKOUT` or download a baseline checkout.

This keeps pull requests independent of external upstream services while still
protecting the deterministic replay summary contract. Developers with a local
preserved baseline checkout can run strict mode before opening or merging a
pull request that changes project I/O behavior.

## Troubleshooting

### The harness says the baseline is unavailable

No baseline checkout path was configured. The test is running in fallback mode.
Set either `-Drabbithole.baseline.checkout=...` or
`RABBITHOLE_BASELINE_CHECKOUT=...` to enable strict comparison.

### The configured baseline path fails immediately

Configured baseline paths are strict. Check that the path exists, is a directory,
and points to a checkout that contains `scripts/rabbithole-replay-summary`.

### The comparison diff shows only ordering changes

Ordering changes are compatibility evidence. Fix the provider to sort paths,
entries, manifest keys, or resources before writing summaries unless the new
order is intentional and the baseline contract is being changed deliberately.

### A summary contains a local path or timestamp

Treat this as a harness bug. Summaries must be reviewable and portable, so local
paths, temp roots, usernames, timestamps, and JVM object ids are excluded from
the summary format.

### Maven reports missing Tweedle parser classes

Initialize the Tweedle grammar submodule and rerun the focused test:

```bash
git submodule update --init tweedle-lang
mvn -pl core/story-api-migration \
  -DincludeSims=false \
  -Dinstall4j.skip \
  -Dcheckstyle.skip \
  -Djava.awt.headless=true \
  -Dtest=DualBaselineReplayHarnessTest \
  test
```
