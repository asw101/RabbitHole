# Testing

RabbitHole uses a mix of unit tests, characterization tests, and focused
desktop proof tests.

## Run the main test lanes

Run everything:

```bash
mvn test
```

Run the no-Sims, headless-friendly lane used in CI:

```bash
mvn -DincludeSims=false -Dinstall4j.skip -Dcheckstyle.skip -Djava.awt.headless=true clean test
```

Run Checkstyle separately:

```bash
mvn checkstyle:check -Dcheckstyle.config.location=checkstyle.xml
```

Run coverage:

```bash
mvn -DincludeSims=false -Dinstall4j.skip -Dcheckstyle.skip -Dmdep.skip=true -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/ast=18.0 \
  --min-module-line-percent core/model-loading=10.0 \
  --min-module-line-percent core/story-api-migration=75.0 \
  --min-module-line-percent core/tweedle=50.0 \
  --min-module-line-percent core/scenegraph=10.0 \
  --min-module-line-percent netbeans=25.0
```

## What the coverage lane measures

The aggregate report is written to:

```text
coverage-report/target/site/jacoco-aggregate/index.html
```

The coverage workflow also uploads:

- `coverage-summary.md`
- `coverage-evidence-manifest.json`
- per-module `target/site/jacoco/` reports when they exist

## Test patterns used in this repository

### Headless guards

GUI tests must skip clearly when there is no real display. Use JUnit `Assume`
guards as the first line of the test instead of `if (...) return`.

Examples used in the repository:

```java
assumeFalse("requires a graphical display", GraphicsEnvironment.isHeadless());
```

```java
assumeTrue("Requires non-headless AWT display",
    SaveMenuDoClickProbe.isNonHeadlessAwtDisplayAvailable());
```

These guards make CI output honest: skipped means skipped, not silently passed.

### GL skip patterns

Most unit tests stay away from real OpenGL work. Instead, they test pure math,
scene data, serialization, and state changes in isolation.

Common patterns:

- keep `scenegraph` tests pure model tests with no rendering pipeline
- keep `story-api` tests headless-safe by avoiding `GlrRenderFactory`
- characterize non-GL seams around rendering code, then leave real GL behavior
  to focused integration or proof tests
- use stubs and artifact checks when you only need to prove contract shape

### Characterization-first refactoring

Modernization changes usually start with a characterization test. That protects
the current Alice 3 behavior before a large class is split or moved.

### Focused module validation

When you touch one area, prefer a targeted Maven command before you run the
whole reactor. For example:

```bash
mvn -pl core/ide -am test
mvn -pl core/scenegraph -am test
mvn -pl core/story-api -am test
```

## core/ide coverage test infrastructure

The `core/ide` module has ~960 test files in
`core/ide/src/test/java/org/alice/ide/coverage/` built on four support classes
(`HeadlessClassExerciseSupport`, `ClassLoadingSweepSupport`,
`SingleClassDefaultArgsSweepTestSupport`, `CompositeCreateViewSweepSupport`).
New coverage follows a three-tier approach. See
[docs/core-ide-coverage.md](core-ide-coverage.md) for the full guide.

### Quick reference

Run core/ide tests with coverage:

```bash
xvfb-run mvn -pl core/ide -am -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false verify
```

Check the JaCoCo report:

```bash
cat core/ide/target/site/jacoco/jacoco.csv | python3 -c "
import csv, sys
r = csv.DictReader(sys.stdin)
missed = covered = 0
for row in r:
    missed += int(row['LINE_MISSED']); covered += int(row['LINE_COVERED'])
total = missed + covered
print(f'{covered}/{total} lines = {100*covered/total:.1f}%')
"
```

### Coverage tiers for adding new tests

| Tier | Pattern | Example | Typical yield |
|------|---------|---------|---------------|
| 1 — Sweep targets | Class names in `small-class-targets.txt` | Add `org.alice.ide.ast.export.TypeInfo` | ~20 lines/class |
| 2 — Headless package sweeps | `*HeadlessSweepTest.java` with explicit class name lists | `SceneeditorLogicHeadlessSweepTest` | ~80–200 lines/package |
| 3 — Targeted unit tests | `*Test.java` testing specific logic paths | `ExpressionCascadeManagerTest` | ~30–100 lines/class |

## Practical advice

- Use headless-safe tests for data models and serialization.
- Add display guards only when a real UI is required.
- Keep proof-artifact tests separate from UI-skip logic.
- Run the coverage lane after larger refactors so you catch dropped coverage
  before review.
- When adding coverage to `core/ide`, prefer Tier 2 headless sweeps for new
  packages and Tier 3 targeted tests for high-LOC logic classes. See
  [docs/core-ide-coverage.md](core-ide-coverage.md).
