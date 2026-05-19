# Tutorial: Trace the Core Croquet Coverage Push

Guided walkthrough of the test strategies, headless patterns, and coverage
verification used in the core/croquet 70%+ coverage push (issue #794).

## What you'll learn

- How croquet tests run headlessly without `Application.getActiveInstance()`
- How to test abstract classes via concrete stubs
- How reflection-based coverage works for UI-bound classes
- How to verify coverage targets are met

## Step 1: Understand the headless pattern

Open `CroquetTestUtils.java`:

```
core/croquet/src/test/java/org/lgna/croquet/CroquetTestUtils.java
```

This utility class is the key to headless testing. Most croquet State subclasses
register Swing listeners in their constructor that call back to
`Application.getActiveInstance()`. In a test environment, the Application
singleton is null, causing `NullPointerException`.

The fix: remove those listeners immediately after construction.

```java
@Before
public void setUp() {
    state = new TestBooleanState(TEST_GROUP, false);
    CroquetTestUtils.removeItemListeners(state);  // ← prevents NPE
}
```

There are four removal helpers, one per Swing model type:

| Helper | State Type | Swing Model |
|--------|-----------|-------------|
| `removeItemListeners` | `BooleanState` | `DefaultButtonModel` |
| `removeSpinnerChangeListeners` | `BoundedNumberState<?>` | `SpinnerNumberModel` |
| `removeDocumentListeners` | `StringState` | `AbstractDocument` |
| `removeListSelectionListeners` | `SingleSelectListState<?,?>` | `DefaultListSelectionModel` |

## Step 2: Trace a direct-instantiation test

Open `EditOperationTest.java`:

```
core/croquet/src/test/java/org/lgna/croquet/EditOperationTest.java
```

`EditOperation` has a 2-argument constructor `EditOperation(Group, Edit)` that
doesn't trigger the Application singleton. This allows direct behavioral testing:

```java
@Test
public void constructor_setsGroup() {
    EditOperation op = new EditOperation(TEST_GROUP, testEdit);
    assertEquals(TEST_GROUP, op.getGroup());
}
```

**Key insight:** Not all Operation subclasses need reflection. Check the
constructor chain before deciding on a strategy.

## Step 3: Trace a reflection-based test

Open `OwnedByCompositeOperationCoverageTest.java`:

```
core/croquet/src/test/java/org/lgna/croquet/OwnedByCompositeOperationCoverageTest.java
```

`OwnedByCompositeOperation` requires an `OperationOwningComposite` in its
constructor, which pulls in `Composite<V>` → the full UI composite stack.
We can't instantiate it headlessly, so we test structurally:

```java
@Test
public void hierarchy_extendsAbstractOperation() {
    assertTrue(AbstractOwnedByCompositeOperation.class
        .isAssignableFrom(OwnedByCompositeOperation.class));
}

@Test
public void method_fire_exists() throws Exception {
    Method m = OwnedByCompositeOperation.class.getDeclaredMethod(
        "fire", Trigger.class);
    assertNotNull(m);
    assertEquals(void.class, m.getReturnType());
}
```

**Key insight:** Reflection tests are intentionally brittle — they break when
the public API changes. This is a feature: they serve as change detectors.

## Step 4: Trace a concrete-stub test

Open `StaticMenuModelTest.java`:

```
core/croquet/src/test/java/org/lgna/croquet/StaticMenuModelTest.java
```

`StaticMenuModel` is abstract. To test it, create a minimal concrete stub:

```java
private static class TestStaticMenuModel extends StaticMenuModel {
    TestStaticMenuModel(Group group) {
        super(group, CroquetTestUtils.nextTestUUID());
    }

    @Override
    protected void localize() { }

    @Override
    protected String findDefaultLocalizedText() {
        return "Test Menu";
    }
}
```

Then test the concrete behavior inherited from the abstract class:

```java
@Test
public void constructor_setsGroup() {
    TestStaticMenuModel model = new TestStaticMenuModel(TEST_GROUP);
    assertEquals(TEST_GROUP, model.getGroup());
}
```

## Step 5: Trace a value-class test

Open `OwnedByCompositeOperationSubKeyTest.java`:

```
core/croquet/src/test/java/org/lgna/croquet/OwnedByCompositeOperationSubKeyTest.java
```

`OwnedByCompositeOperationSubKey` is a simple value class with `equals()`,
`hashCode()`, and getters. Test the full equality contract:

- Reflexive: `x.equals(x)` is true
- Symmetric: `x.equals(y)` ↔ `y.equals(x)`
- Null-safe: `x.equals(null)` is false
- Type-safe: `x.equals("string")` is false
- Hash consistency: equal objects have equal hashes

## Step 6: Verify coverage

After all tests pass:

```bash
mvn test jacoco:report -pl core/croquet -Djava.awt.headless=true
open core/croquet/target/site/jacoco/index.html
```

Check the following thresholds:

| Package | Target |
|---------|--------|
| `org.lgna.croquet` | 70%+ line coverage |
| `org.lgna.croquet.codecs` | 70%+ line coverage |
| `org.lgna.croquet.data` | 70%+ line coverage |
| `org.lgna.croquet.edits` | 60%+ line coverage |
| `org.lgna.croquet.history` | 60%+ line coverage |
| `org.lgna.croquet.icon` | 60%+ line coverage |

## Summary

The coverage push uses four strategies in priority order:

1. **Direct instantiation** — preferred when constructors don't hit Application
2. **Concrete stubs** — for abstract classes with simple abstract methods
3. **Reflection** — for classes deep in the UI stack
4. **Behavioral deepening** — adding tests to existing thin files

All strategies share `CroquetTestUtils` for headless isolation and
deterministic UUID generation.
