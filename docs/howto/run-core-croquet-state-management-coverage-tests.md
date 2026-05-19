# How to run the core/croquet state management coverage tests

This guide explains how to run, verify, and troubleshoot the 25 coverage test
files added by Issue #778 for the `core/croquet` state management framework.

## Prerequisites

- JDK 17+ (project default)
- Maven 3.9+
- Tweedle grammar submodule initialized:
  ```sh
  git submodule update --init tweedle-lang
  ```

No display, GPU, or network connection is required. All tests run headlessly.

## Run all coverage tests

```sh
mvn test -pl core/croquet -Dtest="*CoverageTest" -DfailIfNoTests=false -q
```

Expected: 521 tests, 0 failures, 0 errors.

## Run all core/croquet tests (including pre-existing)

```sh
mvn test -pl core/croquet
```

This runs both existing tests and the new coverage tests.

## Run a single test class

```sh
mvn test -pl core/croquet -Dtest=BooleanStateCoverageTest -q
mvn test -pl core/croquet -Dtest=TriggerHierarchyCoverageTest -q
mvn test -pl core/croquet -Dtest=StateEditCoverageTest -q
```

## Run tests in a specific package

```sh
# All trigger coverage tests
mvn test -pl core/croquet -Dtest="org.lgna.croquet.triggers.*CoverageTest" -q

# All codec coverage tests
mvn test -pl core/croquet -Dtest="org.lgna.croquet.codecs.*CoverageTest" -q

# All edit coverage tests
mvn test -pl core/croquet -Dtest="org.lgna.croquet.edits.*CoverageTest" -q

# All preference coverage tests
mvn test -pl core/croquet -Dtest="org.lgna.croquet.preferences.*CoverageTest" -q
```

## Run a single test method

```sh
mvn test -pl core/croquet \
  -Dtest="BooleanStateCoverageTest#encodeAndDecode_false_roundTrips" -q
```

## Compile-only check

To verify all test files compile without running them:

```sh
mvn test-compile -pl core/croquet -q
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
  --min-module-line-percent core/croquet=40.0
```

View the HTML report:

```sh
# Open core/croquet/target/site/jacoco/index.html in a browser
```

## Verify incremental coverage

To check coverage after modifying tests:

```sh
mvn test jacoco:report -pl core/croquet
```

Then open `core/croquet/target/site/jacoco/index.html`.

## Troubleshooting

### "No tests were executed" error

Ensure the Surefire plugin configuration includes the `*CoverageTest` pattern:

```sh
mvn test -pl core/croquet -Dtest="*CoverageTest" -DfailIfNoTests=false
```

The `-DfailIfNoTests=false` flag prevents failure when a class-level filter
matches zero methods (e.g., during selective runs).

### `NullPointerException` in `Application.getActiveInstance()`

This indicates a Swing listener was not properly removed during test setup.
Every state-based coverage test must call the appropriate
`CroquetTestUtils.remove*Listeners()` method in its `@Before` method:

| State type | Cleanup method |
|---|---|
| `BooleanState` | `CroquetTestUtils.removeItemListeners(state)` |
| `BoundedIntegerState`, `BoundedDoubleState` | `CroquetTestUtils.removeSpinnerChangeListeners(state)` |
| `StringState` | `CroquetTestUtils.removeDocumentListeners(state)` |
| `SingleSelectListState` variants | `CroquetTestUtils.removeListSelectionListeners(state)` |

### Compilation errors after merging upstream changes

If upstream changes add new abstract methods to state classes, the concrete
test subclasses may need stubs added. Check for `TestBooleanState`,
`TestBoundedIntegerState`, `TestBoundedDoubleState`, `TestStringState`, and
the list state test subclasses.

### Trigger hierarchy test failures

`TriggerHierarchyCoverageTest` uses `Class.forName()` to load all 21 trigger
classes. If a trigger class is renamed, moved, or deleted upstream, update the
fully qualified class name in the test.

## Test file inventory

| # | File | Package | Lines | Tests |
|---|---|---|---:|---:|
| 1 | `BooleanStateCoverageTest.java` | `o.l.croquet` | 287 | 20 |
| 2 | `BoundedIntegerStateCoverageTest.java` | `o.l.croquet` | 243 | 22 |
| 3 | `BoundedDoubleStateCoverageTest.java` | `o.l.croquet` | 244 | 22 |
| 4 | `StringStateCoverageTest.java` | `o.l.croquet` | 217 | 20 |
| 5 | `EnumConstantStateCoverageTest.java` | `o.l.croquet` | 236 | 22 |
| 6 | `ImmutableDataSingleSelectListStateCoverageTest.java` | `o.l.croquet` | 191 | 22 |
| 7 | `MutableDataSingleSelectListStateCoverageTest.java` | `o.l.croquet` | 222 | 21 |
| 8 | `RefreshableDataSingleSelectListStateCoverageTest.java` | `o.l.croquet` | 181 | 17 |
| 9 | `ItemStateCoverageTest.java` | `o.l.croquet` | 224 | 19 |
| 10 | `ColorStateCoverageTest.java` | `o.l.croquet` | 87 | 11 |
| 11 | `TabStateCoverageTest.java` | `o.l.croquet` | 103 | 15 |
| 12 | `CustomItemStateCoverageTest.java` | `o.l.croquet` | 98 | 13 |
| 13 | `EnumCodecCoverageTest.java` | `o.l.croquet.codecs` | 196 | 22 |
| 14 | `AbstractItemCodecCoverageTest.java` | `o.l.croquet.codecs` | 150 | 13 |
| 15 | `ColorCodecCoverageTest.java` | `o.l.croquet.codecs` | 221 | 28 |
| 16 | `DefaultItemCodecCoverageTest.java` | `o.l.croquet.codecs` | 172 | 21 |
| 17 | `FileCodecCoverageTest.java` | `o.l.croquet.codecs` | 151 | 17 |
| 18 | `SimpleTabCompositeCodecCoverageTest.java` | `o.l.croquet.codecs` | 79 | 9 |
| 19 | `AbstractEditCoverageTest.java` | `o.l.croquet.edits` | 262 | 29 |
| 20 | `StateEditCoverageTest.java` | `o.l.croquet.edits` | 238 | 30 |
| 21 | `StateTrackingMetaStateCoverageTest.java` | `o.l.croquet.meta` | 138 | 12 |
| 22 | `PreferenceStringStateCoverageTest.java` | `o.l.croquet.preferences` | 185 | 18 |
| 23 | `PreferenceMutableDataSingleSelectListStateCoverageTest.java` | `o.l.croquet.preferences` | 114 | 11 |
| 24 | `TriggerBehaviorCoverageTest.java` | `o.l.croquet.triggers` | 235 | 25 |
| 25 | `TriggerHierarchyCoverageTest.java` | `o.l.croquet.triggers` | 474 | 62 |
| | **Total** | | **4,948** | **521** |
