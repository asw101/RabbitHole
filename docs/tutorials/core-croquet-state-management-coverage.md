# Tutorial: Trace the core/croquet state management coverage tests

This tutorial walks through the coverage test patterns used in Issue #778 to
characterize the `core/croquet` state management framework. You will learn
the headless testing strategy, codec round-trip pattern, reflection-based
hierarchy testing, and how to extend the suite.

## Prerequisites

- Familiarity with JUnit 4 (`@Test`, `@Before`, `Assert.*`)
- Basic understanding of the croquet state management model (states hold
  values, fire listeners on change, delegate encoding to codecs)
- Repository checked out with `git submodule update --init tweedle-lang`

## Lesson 1: The headless testing problem

The croquet framework is tightly coupled to the Swing `Application` singleton.
Every state object registers Swing listeners during construction. When those
listeners fire, they call `Application.getActiveInstance()`, which returns null
in a headless test environment, causing `NullPointerException`.

**The solution:** After construction, strip the offending listeners using
`CroquetTestUtils`:

```java
@Before
public void setUp() {
  state = new TestBooleanState(TEST_GROUP, false);
  CroquetTestUtils.removeItemListeners(state);
}
```

Each state type has a corresponding cleanup method:

| State type | Method |
|---|---|
| `BooleanState` | `removeItemListeners()` |
| `BoundedIntegerState` | `removeSpinnerChangeListeners()` |
| `BoundedDoubleState` | `removeSpinnerChangeListeners()` |
| `StringState` | `removeDocumentListeners()` |
| `*SingleSelectListState` | `removeListSelectionListeners()` |

This pattern lets you test state behavior — value changes, listener callbacks,
encoding — without booting the full Application.

## Lesson 2: Concrete test subclasses

Most state classes are abstract. To instantiate them, we create minimal
concrete inner classes:

```java
private static class TestBooleanState extends BooleanState {
  TestBooleanState(Group group, boolean initialValue) {
    super(group, UUID.randomUUID(), initialValue);
  }

  @Override
  protected Class<? extends CompletionModel> getClassUsedForLocalization() {
    return TestBooleanState.class;
  }
}
```

The key principle: implement only the required abstract methods. Return simple
defaults. Don't wire up Swing components. The test subclass exists solely to
make the constructor callable.

Each coverage test file follows this pattern. Look for inner classes named
`TestBooleanState`, `TestBoundedIntegerState`, `TestStringState`, etc.

## Lesson 3: Codec round-trip testing

Every state that supports binary encoding should be tested with a
round-trip pattern:

```java
@Test
public void encodeAndDecode_false_roundTrips() {
  ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
  state.encodeValue(encoder, false);
  BinaryDecoder decoder = encoder.createDecoder();
  boolean decoded = state.decodeValue(decoder);
  assertEquals(false, decoded);
}
```

The pattern:
1. Create a `ByteArrayBinaryEncoder`
2. Call `encodeValue(encoder, value)` on the state
3. Create a decoder from `encoder.createDecoder()`
4. Call `decodeValue(decoder)` and assert equality

This pattern is used across `BooleanStateCoverageTest`,
`BoundedIntegerStateCoverageTest`, `BoundedDoubleStateCoverageTest`,
`StringStateCoverageTest`, and `EnumCodecCoverageTest`.

**Boundary values to test:**
- For integers: 0, negative, `MIN_VALUE`, `MAX_VALUE`
- For doubles: 0.0, negative, very small (near epsilon), very large
- For strings: empty, unicode, emoji, whitespace-only, 1000+ characters
- For enums: all values of the enum

## Lesson 4: Listener verification

The croquet framework has two listener styles. Test both:

```java
// Old-school listener (State.ValueListener)
@Test
public void oldSchoolListener_firesOnChange() {
  AtomicBoolean fired = new AtomicBoolean(false);
  state.addValueListener(new State.ValueListener<Boolean>() {
    @Override
    public void changing(State<Boolean> s, Boolean prev, Boolean next) {}
    @Override
    public void changed(State<Boolean> s, Boolean prev, Boolean next) {
      fired.set(true);
    }
  });
  state.setValueTransactionlessly(true);
  assertTrue(fired.get());
}

// New-school listener (ValueListener functional interface)
@Test
public void newSchoolListener_firesOnChange() {
  AtomicBoolean fired = new AtomicBoolean(false);
  state.addNewSchoolValueListener(e -> fired.set(true));
  state.setValueTransactionlessly(true);
  assertTrue(fired.get());
}
```

**Key behaviors to verify:**
- Listener fires once per distinct value change
- Listener does NOT fire when value is set to same value
- Multiple listeners all fire
- `addAndInvokeValueListener` fires immediately on registration
- `changing()` callback receives correct previous/next values

## Lesson 5: Reflection-based hierarchy testing

Some classes cannot be instantiated headlessly (e.g., `ColorState`, `TabState`,
trigger classes with private constructors). For these, we use reflection:

```java
@Test
public void colorState_extendsItemState() {
  assertTrue(ItemState.class.isAssignableFrom(
      Class.forName("org.lgna.croquet.color.ColorState")));
}

@Test
public void colorState_isAbstract() {
  int mods = Class.forName("org.lgna.croquet.color.ColorState").getModifiers();
  assertTrue(Modifier.isAbstract(mods));
}

@Test
public void colorState_hasGetValue() throws NoSuchMethodException {
  Class.forName("org.lgna.croquet.color.ColorState")
      .getMethod("getValue");
}
```

This pattern:
1. Verifies class hierarchy (extends/implements)
2. Verifies modifiers (abstract, public, final)
3. Verifies method presence via `getMethod()`/`getDeclaredMethod()`
4. Verifies constructor presence and visibility
5. Verifies package location

`TriggerHierarchyCoverageTest` applies this pattern to all 21 trigger classes,
making it the most comprehensive hierarchy test (63 tests, 554 lines).

## Lesson 6: Edit testing patterns

`StateEdit` tests verify the undo/redo description machinery:

```java
@Test
public void getTerseDescription_containsArrow() {
  StateEdit<String> edit = new StateEdit<>(null, "old", "new");
  String desc = edit.getTerseDescription();
  assertTrue(desc.contains("→") || desc.contains("->"));
}
```

**Key behaviors to verify:**
- Previous/next values stored correctly
- Description contains class name, values, and separator
- Undo/redo presentation starts with "Undo"/"Redo"
- `toString()` matches detailed description
- Type-specific edits (Integer, Boolean, Double) store correct types

## Lesson 7: Extending the test suite

To add a new coverage test for a state class:

1. **Create the file** following the `*CoverageTest.java` naming convention
2. **Create a concrete test subclass** implementing abstract methods
3. **Strip listeners in `@Before`** using `CroquetTestUtils`
4. **Write round-trip tests** for encode/decode
5. **Write listener tests** for both old-school and new-school
6. **Write boundary tests** for edge-case values
7. **Verify**: `mvn test -pl core/croquet -Dtest=YourNewCoverageTest -q`

For classes that cannot be instantiated headlessly:

1. **Create reflection tests** for hierarchy, methods, and modifiers
2. **Document why** behavioral tests are excluded (in a comment)
3. **Skip `@Before` setup** — no instance to create

## Lesson 8: The MetaState bug characterization

Coverage tests serve as living documentation of framework behavior, including
bugs. `StateTrackingMetaStateCoverageTest` documents a real bug:

```java
@Test
public void metaListener_removeValueListener_hasBug_addsInsteadOfRemoves() {
  // Documents: removeValueListener calls addNewSchoolValueListener
  // instead of removing. This is a pre-existing framework bug.
  ...
}
```

This test will fail when the bug is fixed, signaling that the characterization
needs updating. This is the intended design: characterization tests lock in
current behavior, and their failure under refactoring tells you what changed.

## Summary

| Pattern | When to use | Example file |
|---|---|---|
| Listener stripping | Behavioral tests on state objects | `BooleanStateCoverageTest` |
| Concrete test subclass | Abstract state classes | `StringStateCoverageTest` |
| Codec round-trip | Any encoded value | `EnumCodecCoverageTest` |
| Listener verification | State change notification | `ItemStateCoverageTest` |
| Reflection hierarchy | Non-instantiable classes | `TriggerHierarchyCoverageTest` |
| Edit description | Undo/redo machinery | `StateEditCoverageTest` |
| Bug characterization | Known framework bugs | `StateTrackingMetaStateCoverageTest` |
