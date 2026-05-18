# CroquetTestUtils API Reference

`org.lgna.croquet.CroquetTestUtils` — shared test infrastructure for headless
croquet unit testing.

## Overview

The central challenge when unit-testing croquet is the `Application.getActiveInstance()`
singleton. Swing-backed state objects register listeners during construction that
call into Application. `CroquetTestUtils` provides methods to strip those listeners,
plus deterministic UUID generation and reusable codecs.

This class is `final` with a private constructor — all members are `static`.

## Methods

### `nextTestUUID()`

```java
public static UUID nextTestUUID()
```

Returns a deterministic UUID based on an `AtomicLong` counter. Avoids
`SecureRandom` overhead and guarantees no collisions within a single JVM.

**Use for:** Group construction, any code path that registers a UUID in the
static Group registry.

```java
Group g = Group.getInstance(CroquetTestUtils.nextTestUUID(), "test-group");
```

---

### `removeItemListeners(BooleanState)`

```java
public static void removeItemListeners(BooleanState state)
```

Removes all `ItemListener` instances from the state's underlying
`DefaultButtonModel`. Call immediately after constructing a `BooleanState`
in tests.

```java
TestBooleanState state = new TestBooleanState(false);
CroquetTestUtils.removeItemListeners(state);
state.setValueTransactionlessly(true); // no NPE
```

---

### `removeSpinnerChangeListeners(BoundedNumberState<?>)`

```java
public static void removeSpinnerChangeListeners(BoundedNumberState<?> state)
```

Removes all `ChangeListener` instances from the state's `SpinnerNumberModel`.
Required for `BoundedIntegerState` and `BoundedDoubleState` tests.

```java
BoundedIntegerState state = createTestBoundedIntState();
CroquetTestUtils.removeSpinnerChangeListeners(state);
state.setValueTransactionlessly(42); // no NPE
```

---

### `removeDocumentListeners(StringState)`

```java
public static void removeDocumentListeners(StringState state)
```

Removes all `DocumentListener` instances from the state's `Document` model.
Required for `StringState` tests.

```java
StringState state = createTestStringState();
CroquetTestUtils.removeDocumentListeners(state);
state.setValueTransactionlessly("hello"); // no NPE
```

---

### `removeListSelectionListeners(SingleSelectListState<?, ?>)`

```java
public static void removeListSelectionListeners(SingleSelectListState<?, ?> state)
```

Removes all `ListSelectionListener` instances from the state's
`DefaultListSelectionModel`. Required for list-based state tests.

---

### `STRING_CODEC`

```java
public static final ItemCodec<String> STRING_CODEC
```

A reusable `ItemCodec<String>` implementation for test fixtures. Supports
`decodeValue(BinaryDecoder)`, `encodeValue(BinaryEncoder, String)`,
`getValueClass()`, and `appendRepresentation(StringBuilder, String)`.

```java
ItemCodec<String> codec = CroquetTestUtils.STRING_CODEC;
assertEquals(String.class, codec.getValueClass());
// Use with BinaryEncoder/BinaryDecoder for round-trip testing
```

## Test Helpers

### TestBooleanState

```java
package org.lgna.croquet;

public class TestBooleanState extends BooleanState {
    public TestBooleanState(boolean initialValue);
}
```

Concrete `BooleanState` that uses `CroquetTestUtils.nextTestUUID()` for its
Group and strips Item listeners on construction. Directly instantiable in tests.

### TestListDataListener

```java
package org.lgna.croquet.data;

public class TestListDataListener implements ListDataListener {
    public List<ListDataEvent> getEvents();
    public void clear();
}
```

Captures `ListDataEvent` instances for assertion. Thread-safe via
`CopyOnWriteArrayList`.

### TestActivityNode

```java
package org.lgna.croquet.history;

public class TestActivityNode extends ActivityNode {
    public TestActivityNode(UserActivity parent);
}
```

Instantiable `ActivityNode` subclass for testing tree navigation. Placed in
the `history` package to access package-private constructors.

### TestPrepStep

```java
package org.lgna.croquet.history;

public class TestPrepStep extends PrepStep<CompletionModel> {
    public TestPrepStep(UserActivity activity);
}
```

Instantiable `PrepStep` subclass for testing preparation-step lifecycle.

### TestHistoryListener

```java
package org.lgna.croquet.undo.event;

public class TestHistoryListener implements HistoryListener {
    public List<HistoryEvent> getEvents();
    public List<HistoryPushEvent> getPushEvents();
    public List<HistoryClearEvent> getClearEvents();
    public List<HistoryInsertionIndexEvent> getIndexEvents();
    public void clear();
}
```

Captures all `HistoryEvent` subtypes for `UndoHistory` listener testing.

## Design Decisions

| Decision | Rationale |
|----------|-----------|
| JUnit 4 (not 5) | Matches existing Alice 3 test infrastructure |
| No Mockito | Minimize dependencies; hand-rolled test doubles are sufficient |
| Deterministic UUIDs | Reproducible tests; no flaky ordering |
| In-memory only | No temp files, no I/O, no network |
| Listener stripping | Only viable headless strategy without refactoring production code |
