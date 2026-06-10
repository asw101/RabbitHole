# RabbitHole baseline parity

`RabbitHoleBaselineParityTest` is a focused JUnit baseline harness for generated
NetBeans package output and Alice project archive shapes. It compares current
RabbitHole behavior against committed UTF-8 text snapshots and runs through the
normal Maven test lane; there is no separate runner or binary fixture corpus.
Pure Java source-shape assertions live in the focused generated-source tests
described in
[Generated Source Validation](reference/generated-source-validation.md).
This harness may record generated-source file presence or hashes only to prove
package parity; it does not own imports, declarations, or snippet syntax.

## What it covers

The fixture is generated in memory for every test run. It creates a minimal
`Program` Alice project with a referenced generated image resource, then verifies:

- generated project file summaries from `ProjectCodeGenerator.generateCode(...)`
- editable `.a3p` archive entry lists and manifest summaries from `IoUtilities.writeProject(...)`
- player `.a3w` export entry lists and manifest summaries from `IoUtilities.exportProject(...)`

The snapshots live under:

```text
netbeans/src/test/resources/org/alice/netbeans/project/parity/
```

Do not check in generated `.a3p`, `.a3w`, `.zip`, image, audio, or other binary
payloads for this harness.

## Normalization

The harness keeps snapshots deterministic by recording only stable text:

- ZIP entry names are normalized to `/` separators and sorted.
- ZIP timestamps, entry ordering, compression metadata, and file sizes are not
  snapshotted.
- Manifest summaries omit volatile fields such as generated identifiers and
  creation timestamps.
- Java source entries are summarized only as package artifacts. Hashes are
  computed after line-ending normalization so parity changes remain reviewable
  without making this harness the owner of imports, declarations, or Java syntax
  snippets.
- Resource payloads are represented by size and hash in the generated-source
  summary, not by embedding binary content.

## Updating snapshots

Normal test runs are read-only. Snapshot writes require an explicit property:

```bash
git submodule update --init tweedle-lang
mvn -pl netbeans -am \
  -DincludeSims=false \
  -Dinstall4j.skip \
  -Dcheckstyle.skip \
  -Djava.awt.headless=true \
  -Dtest=RabbitHoleBaselineParityTest \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Drabbithole.baseline.updateSnapshots=true \
  -Drabbithole.baseline.snapshotDir=netbeans/src/test/resources/org/alice/netbeans/project/parity \
  test
```

If Maven is run from the `netbeans/` module directory, use the module-relative
snapshot directory instead:

```bash
-Drabbithole.baseline.snapshotDir=src/test/resources/org/alice/netbeans/project/parity
```

After updating, review the snapshot diff as a behavior change and run the focused
test again without `-Drabbithole.baseline.updateSnapshots=true`.

## Relationship to eatme

This harness is low-level deterministic parity coverage for generator and archive
contracts. It complements `eatme`, which remains the broader outside-in workflow
for save, reopen, run, and evidence artifacts. Use this baseline when changing
NetBeans project generation, Alice project save/export behavior, manifest fields,
or resource archive shape. Use the focused compiler and Story API tests for pure
generated Java source-shape changes.
