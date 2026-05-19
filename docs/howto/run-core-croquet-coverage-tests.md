# Run the Core Croquet Coverage Tests

How to run, verify, and extend the `core/croquet` unit test suite after the
70%+ coverage push (issue #794).

## Prerequisites

1. JDK 17+ installed and on `PATH`.
2. Tweedle grammar submodule initialized:
   ```bash
   git submodule update --init tweedle-lang
   ```

## Run all core/croquet tests

```bash
mvn test -pl core/croquet -Djava.awt.headless=true
```

Expected output:
```
Tests run: 2250+, Failures: 0, Errors: 0, Skipped: 0
```

## Verify line count

```bash
find core/croquet/src/test/java -name "*.java" -exec wc -l {} + | tail -1
```

Expected: **27,000+ total**.

## Run a single work package

### WP1: Operations

```bash
mvn test -pl core/croquet \
  -Dtest="ValueHolderTest,EditOperationTest,OwnedByCompositeOperationCoverageTest,AbstractOwnedByCompositeOperationCoverageTest,OwnedByCompositeOperationSubKeyTest,OperationImpDeepTest" \
  -Djava.awt.headless=true
```

### WP2: MenuModels

```bash
mvn test -pl core/croquet \
  -Dtest="StaticMenuModelTest,PredeterminedMenuModelTest,LabelMenuSeparatorModelTest,PopupPrepModelSwingModelTest" \
  -Djava.awt.headless=true
```

### WP3: State deepening

```bash
mvn test -pl core/croquet \
  -Dtest="BoundedIntegerStateDeepTest,BoundedDoubleStateDeepTest,SingleSelectListStateDeepTest,StringStateDeepTest,BooleanStateDeepTest,ItemStateDeepTest,ColorStateCoverageTest,MultipleSelectionListStateDeepTest" \
  -Djava.awt.headless=true
```

### WP4: Codecs & data

```bash
mvn test -pl core/croquet \
  -Dtest="EnumCodecDeepTest,ImmutableListDataExtendedTest,MutableListDataDeepTest" \
  -Djava.awt.headless=true
```

## Generate coverage report

```bash
mvn test jacoco:report -pl core/croquet -Djava.awt.headless=true
```

Open `core/croquet/target/site/jacoco/index.html` in a browser to verify
70%+ line coverage.

## Adding new tests

Follow these patterns from the existing suite:

1. **Headless state tests** — use `CroquetTestUtils.remove*Listeners()` in `@Before`.
2. **Reflection coverage** — for classes that need `Application.getActiveInstance()`.
3. **Concrete stubs** — for abstract classes, create a minimal `Test*` subclass.
4. **Deterministic UUIDs** — use `CroquetTestUtils.nextTestUUID()` or fixed UUIDs.

See [Core Croquet Test Coverage](../testing/core-croquet-coverage.md) for the
full pattern reference.

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| `NullPointerException` at `Application.getActiveInstance()` | Add listener removal in `@Before` |
| `HeadlessException` | Pass `-Djava.awt.headless=true` |
| `NoSuchMethodException` in reflection test | Method was renamed — update test |
| UUID collision between test classes | Use unique UUIDs per class |
