# Croquet Coverage Configuration

How coverage is measured, configured, and validated for `core/croquet`.

## JaCoCo Setup

JaCoCo is configured in the module POM via the `jacoco-maven-plugin`.
Coverage runs automatically during `mvn verify`.

### Commands

```bash
# Full build + coverage
mvn verify -pl core/croquet -Djava.awt.headless=true

# Tests only (no coverage report)
mvn test -pl core/croquet -Djava.awt.headless=true

# Single test with coverage
mvn verify -pl core/croquet -Dtest=BooleanStateTest -Djava.awt.headless=true
```

### Report Locations

| Report | Path |
|--------|------|
| HTML | `core/croquet/target/site/jacoco/index.html` |
| CSV | `core/croquet/target/site/jacoco/jacoco.csv` |
| XML | `core/croquet/target/site/jacoco/jacoco.xml` |

## Coverage Scope

### Included

All classes under `org.lgna.croquet` and sub-packages:

- `org.lgna.croquet` (root) — State types, operations, models, groups
- `org.lgna.croquet.codecs` — Serialization codecs
- `org.lgna.croquet.data` — List data implementations
- `org.lgna.croquet.edits` — Edit/undo abstractions
- `org.lgna.croquet.event` — Value change events
- `org.lgna.croquet.history` — User activity tracking
- `org.lgna.croquet.history.event` — History event types
- `org.lgna.croquet.imp` — Internal implementations
- `org.lgna.croquet.meta` — Meta-state tracking
- `org.lgna.croquet.preferences` — Preference-backed state
- `org.lgna.croquet.resolvers` — Runtime resolvers
- `org.lgna.croquet.triggers` — Action triggers
- `org.lgna.croquet.undo` — Undo history management
- `org.lgna.croquet.undo.event` — Undo event types
- `org.lgna.croquet.views` — View layer (partially covered)

### Exclusions

No source packages are excluded from measurement. All packages contribute
to the 50% line-coverage target.

## Headless Mode

**Required JVM argument:** `-Djava.awt.headless=true`

This is set via Maven Surefire configuration:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <configuration>
    <argLine>-Djava.awt.headless=true ${argLine}</argLine>
  </configuration>
</plugin>
```

The `${argLine}` placeholder preserves the JaCoCo agent argument.

## Coverage Targets

| Metric | Minimum | Rationale |
|--------|---------|-----------|
| Line coverage | 50% | Sprint gate — up from 21.5% baseline |
| Branch coverage | No minimum | Best-effort; focus on line coverage |
| Method coverage | No minimum | Implied by line coverage |

### Coverage Math

| Metric | Value |
|--------|-------|
| Total source lines (module) | ~15,700 |
| Testable source lines (excl. views/icon) | ~10,750 |
| Lines covered at baseline (21.5%) | ~3,380 |
| Lines needed for 50% | ~7,850 |
| New lines to cover | ~3,072+ |
| New test classes | 37 |
| Total test classes | ~100 |

## CI Integration

Tests run in the standard Maven CI pipeline:

```bash
mvn verify -pl core/croquet -Djava.awt.headless=true -B
```

The `-B` (batch mode) flag suppresses interactive prompts. Coverage results
are available in the JaCoCo XML report for downstream tools.

### Failure Conditions

- Any `@Test` method failure → build fails
- Coverage below threshold (if enforced) → build fails at `verify` phase

## Validating Coverage Locally

Quick check from the command line:

```bash
# After mvn verify completes:
python3 -c "
import csv, sys
with open('core/croquet/target/site/jacoco/jacoco.csv') as f:
    reader = csv.DictReader(f)
    missed = covered = 0
    for row in reader:
        missed += int(row['LINE_MISSED'])
        covered += int(row['LINE_COVERED'])
    total = missed + covered
    pct = covered / total * 100 if total else 0
    print(f'Lines: {covered}/{total} ({pct:.1f}%)')
    sys.exit(0 if pct >= 50 else 1)
"
```

## Troubleshooting

| Symptom | Cause | Fix |
|---------|-------|-----|
| 0% coverage despite tests passing | JaCoCo agent not attached | Ensure `${argLine}` in surefire config |
| `java.awt.HeadlessException` | Missing headless flag | Add `-Djava.awt.headless=true` |
| Stale coverage numbers | Old `.exec` file | Run `mvn clean verify` |
| CSV report empty | No `verify` phase run | Use `mvn verify`, not `mvn test` |
