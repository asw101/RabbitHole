# Coverage reporting

Alice coverage CI runs the no-Sims Maven reactor with the `coverage` profile:

```sh
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py --output coverage-summary.md --min-aggregate-line-percent 8.0
```

The Maven profile writes the aggregate report to
`coverage-report/target/site/jacoco-aggregate/` and per-module reports to each
module's `target/site/jacoco/` directory when that module has JaCoCo execution
data. The summary script prints aggregate and module line coverage, writes the
same Markdown to `coverage-summary.md`, and appends it to the GitHub Actions job
summary.

## Gate

The current CI gate is an honest ratchet, not the 70% mission target. It fails
only when the aggregate no-Sims line coverage report is missing or falls below
8.0%. The observed baseline before this gate was approximately 8.29% aggregate
line coverage, so the floor catches report breakage or meaningful regression
without pretending the project is already near 70%.

## Path to 70%

Raise the `--min-aggregate-line-percent` value only after durable tests increase
real aggregate coverage. Prefer characterization tests around behavior already
being modernized, especially save/load/export, Tweedle parsing, story migration,
model loading, and IDE service boundaries. Do not raise the gate by excluding
production code or by adding tests that only execute constructors without
asserting behavior.
