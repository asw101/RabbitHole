# How to run the coverage sprint tests

This guide explains how to run, verify, and troubleshoot the tests added by
the Issue #751 coverage sprint across `core/story-api`, `core/util`, and
`core/ast`.

## Prerequisites

- JDK 17+ (project default)
- Maven 3.9+
- Tweedle grammar submodule initialized:
  ```sh
  git submodule update --init tweedle-lang
  ```

## Run all three modules

```sh
mvn test -pl core/story-api,core/util,core/ast
```

Expected: all tests pass. No tests require a display, GPU, or network.

## Run a single module

```sh
mvn test -pl core/story-api
mvn test -pl core/util
mvn test -pl core/ast
```

## Run a specific test class

```sh
mvn test -pl core/util -Dtest=DurationBasedAnimationTest
mvn test -pl core/story-api -Dtest=ProgramImpTest
mvn test -pl core/ast -Dtest=VirtualMachineHeadlessRuntimeEventTest
```

## Measure coverage with JaCoCo

Run the full no-Sims coverage profile and generate the summary:

```sh
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/story-api=65.0 \
  --min-module-line-percent core/util=65.0 \
  --min-module-line-percent core/ast=65.0
```

The module ratchets (`=65.0`) are the conservative floors. Measured coverage
should exceed 70% for each module. If a ratchet fails, the command exits
non-zero.

## View per-module HTML coverage reports

After the coverage build, open the HTML reports:

| Module | Report path |
| --- | --- |
| `core/story-api` | `core/story-api/target/site/jacoco/index.html` |
| `core/util` | `core/util/target/site/jacoco/index.html` |
| `core/ast` | `core/ast/target/site/jacoco/index.html` |
| Aggregate | `coverage-report/target/site/jacoco-aggregate/index.html` |

## Verify incremental coverage after adding tests

To check coverage after writing a batch of tests without running the full
reactor:

```sh
mvn test -pl core/util jacoco:report -pl core/util
# Then open core/util/target/site/jacoco/index.html
```

## Troubleshooting

### Tests fail with `HeadlessException`

All sprint tests guard against headless environments. If a test fails with
`HeadlessException`, it is either missing a headless guard or exercising a
code path that unexpectedly requires AWT. Fix: add
`assumeFalse(GraphicsEnvironment.isHeadless())` to the failing test method.

### Tweedle parser classes missing

```
[ERROR] package org.lgna.project.ast.tweedle does not exist
```

Initialize the grammar submodule:
```sh
git submodule update --init tweedle-lang
```

### JaCoCo CSV is missing

Ensure you are using the `-Pcoverage` profile. Without it, JaCoCo does not
instrument classes and does not generate reports.

### Coverage is below the ratchet

If coverage drops below the ratchet floor after a code change:

1. Check if new source lines were added without corresponding tests.
2. Run the per-module HTML report to identify uncovered lines.
3. Add targeted tests for the uncovered paths.
4. See [Expand coverage ratchets](./expand-coverage-ratchets.md) for the
   ratchet adjustment workflow.

## Test inventory cross-reference

For the full test class inventory, estimated line coverage, and exclusion
lists, see the reference documentation:

- [core/story-api coverage sprint](../reference/core-story-api-coverage-sprint.md)
- [core/util coverage sprint](../reference/core-util-coverage-sprint.md)
- [core/ast coverage sprint](../reference/core-ast-coverage-sprint.md)
- [Coverage reporting and ratchets](../reference/coverage-reporting.md)
