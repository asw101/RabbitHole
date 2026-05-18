# core/croquet Test Suite

Unit tests for the `org.lgna.croquet` framework module.
JUnit 4, headless-safe (`-Djava.awt.headless=true`).

## Quick Start

```bash
# Run all croquet tests
mvn test -pl core/croquet -Djava.awt.headless=true

# Run a single test class
mvn test -pl core/croquet -Dtest=BooleanStateTest -Djava.awt.headless=true

# Run with coverage report
mvn verify -pl core/croquet -Djava.awt.headless=true
# Report: core/croquet/target/site/jacoco/index.html
```

## Test Organization

Tests mirror the main source layout under `core/croquet/src/test/java/org/lgna/croquet/`.

| Package | Test Classes | What's Covered |
|---------|-------------|----------------|
| `(root)` | 30+ | State types, operations, models, codecs, value holders |
| `codecs/` | 4 | EnumCodec, DefaultItemCodec, ColorCodec, FileCodec |
| `data/` | 6 | ListData implementations, mutable/immutable/refreshable |
| `edits/` | 1 | StateEdit undo/redo lifecycle |
| `event/` | 1 | ValueEvent construction and properties |
| `history/` | 4 | UserActivity, ActivityNode, PrepStep trees |
| `history/event/` | 7 | Activity, Cancel, Change, EditCommitted, Finished, PopupMenuResized events; Listener contract |
| `icon/` | 2 | Icon factories, TrimmedIcon rendering |
| `imp/` | 2 | BooleanStateImp, SingleSelectListStateSwingModel |
| `meta/` | 2 | MetaState, StateTrackingMetaState |
| `preferences/` | 2 | PreferenceBooleanState, PreferenceManagerCodec |
| `resolvers/` | 1 | RuntimeResolver |
| `triggers/` | 5 | Trigger base, IterationTrigger, ChangeEventTrigger, CascadeAutomatic, EventObjectTrigger |
| `undo/` | 1 | UndoHistory stack operations |
| `undo/event/` | 4 | HistoryEvent, HistoryClearEvent, HistoryPushEvent, HistoryInsertionIndexEvent |
| `views/` | 9 | Layout builders, hierarchy handlers, folder tabs |

**Existing:** 63 test classes (21.5% line coverage).
**After sprint:** ~100 test classes, **50%+ line coverage target**.

## Test Utilities

### CroquetTestUtils

Shared helper at `org/lgna/croquet/CroquetTestUtils.java`. Solves the main
headless-testing challenge: Swing listener registration that calls
`Application.getActiveInstance()`.

```java
import static org.lgna.croquet.CroquetTestUtils.*;

// Deterministic UUID (no SecureRandom overhead)
UUID id = CroquetTestUtils.nextTestUUID();

// Decouple BooleanState from Application
CroquetTestUtils.removeItemListeners(booleanState);

// Decouple BoundedNumberState from Application
CroquetTestUtils.removeSpinnerChangeListeners(boundedState);

// Decouple StringState from Application
CroquetTestUtils.removeDocumentListeners(stringState);

// Decouple list selection state from Application
CroquetTestUtils.removeListSelectionListeners(listState);

// Reusable String codec for test fixtures
ItemCodec<String> codec = CroquetTestUtils.STRING_CODEC;
```

### Package-Private Test Helpers

Some classes have package-private constructors. Test-only subclasses provide
access:

| Helper | Package | Purpose |
|--------|---------|---------|
| `TestBooleanState` | `o.l.croquet` | Concrete BooleanState with test Group |
| `TestListDataListener` | `o.l.croquet.data` | Captures ListData events |
| `TestActivityNode` | `o.l.croquet.history` | Instantiable ActivityNode subclass |
| `TestPrepStep` | `o.l.croquet.history` | Instantiable PrepStep subclass |
| `TestHistoryListener` | `o.l.croquet.undo.event` | Captures UndoHistory events |

## Test Patterns

### Pattern 1: State Lifecycle

All State subclass tests follow the same structure:

```java
@Test
public void testInitialValue() {
    // State is created with a known initial value
    assertEquals(expected, state.getValue());
}

@Test
public void testSetAndGetValue() {
    state.setValueTransactionlessly(newValue);
    assertEquals(newValue, state.getValue());
}

@Test
public void testValueChangeNotification() {
    ValueListener<T> listener = mock(ValueListener.class);
    state.addNewSchoolValueListener(listener);
    state.setValueTransactionlessly(newValue);
    verify(listener).valueChanged(any(ValueEvent.class));
}
```

Applies to: `BooleanStateTest`, `StringStateTest`, `BoundedIntegerStateTest`,
`BoundedDoubleStateTest`, `SingleSelectListStateTest`,
`MultipleSelectionListStateTest`, `EnumConstantStateTest`,
`ItemStateContractTest`, `BooleanStateExpandedTest`.

### Pattern 2: Pure-POJO Event Tests

Event classes in `history/event/` and `undo/event/` are simple value objects.
`ActivityEvent` is an **interface** — tests target its concrete implementations
(`CancelEvent`, `ChangeEvent`, `EditCommittedEvent`, `FinishedEvent`,
`PopupMenuResizedEvent`). Tests verify construction, field access, and identity:

```java
@Test
public void testCancelEventIsActivityEvent() {
    CancelEvent event = new CancelEvent();
    assertNotNull(event);
    assertTrue(event instanceof ActivityEvent);
}

@Test
public void testChangeEventHoldsNode() {
    ChangeEvent<ActivityNode> event = new ChangeEvent<>(node);
    assertSame(node, event.getNode());
}
```

Applies to: all 7 `history/event/` tests, all 4 `undo/event/` tests.

### Pattern 3: Trigger Construction

Trigger tests verify that subclasses can be
instantiated without the Application singleton. Note: most trigger constructors
are **private** — use the static `createUserInstance()` factory method:

```java
@Test
public void testConstructFromUserActivity() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    assertNotNull(trigger);
    assertNotNull(trigger.getUserActivity());
}

@Test
public void testNullTriggerCreatesActivity() {
    UserActivity activity = NullTrigger.createUserActivity();
    assertNotNull(activity);
}
```

Applies to: `TriggerBaseTest`, `IterationTriggerTest`,
`ChangeEventTriggerTest`, `CascadeAutomaticDeterminationTriggerTest`,
`EventObjectTriggerTest`.

### Pattern 4: Codec Round-Trip

Codec tests verify encode→decode identity using binary streams.
Croquet codecs use `BinaryEncoder`/`BinaryDecoder` (not string-based):

```java
@Test
public void testValueClass() {
    assertEquals(MyEnum.class, codec.getValueClass());
}

@Test
public void testAppendRepresentation() {
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, testValue);
    assertTrue(sb.length() > 0);
}
```

Applies to: `EnumCodecTest`, `ColorCodecExtendedTest`,
`DefaultItemCodecExtendedTest`, `FileCodecExtendedTest`,
`CodecExtendedTest`.

### Pattern 5: Edit Undo/Redo

```java
@Test
public void testDoAndUndo() {
    edit.doOrRedo(true);
    assertEquals(newValue, state.getValue());
    edit.undo();
    assertEquals(oldValue, state.getValue());
}
```

Applies to: `StateEditTest`, `AbstractEditTest`, `EditInterfaceTest`.

## Headless Testing

All tests run with `-Djava.awt.headless=true`. Key techniques:

1. **Listener removal** — `CroquetTestUtils.remove*Listeners()` strips
   Swing listeners that trigger `Application.getActiveInstance()`.

2. **Test subclasses** — `TestBooleanState`, `TestActivityNode`, etc.
   provide constructors that bypass Application dependency.

3. **UUID isolation** — `CroquetTestUtils.nextTestUUID()` generates
   deterministic, collision-free UUIDs for Group registration.

4. **No UI instantiation** — Tests never create JFrame, JPanel, or
   other visible components. State objects are tested through their
   model layer only.

## Coverage Targets

| Metric | Target | Scope |
|--------|--------|-------|
| Line coverage | ≥ 50% | `org.lgna.croquet` (all packages) |
| Branch coverage | Best effort | Focus on State/Edit/Event branches |
| Excluded | — | `views/` (Swing rendering), `icon/` (image loading) |

### Measuring Coverage

```bash
# Generate JaCoCo report
mvn verify -pl core/croquet -Djava.awt.headless=true

# View results
open core/croquet/target/site/jacoco/index.html

# Or check from CLI
grep -A2 'TOTAL' core/croquet/target/site/jacoco/jacoco.csv 2>/dev/null
```

### High-Value Coverage Targets

These packages yield the most coverage per test:

| Package | Source LOC | Why High-Value |
|---------|-----------|----------------|
| `history/event/` | 375 | Pure POJOs, zero deps, 100% testable |
| `undo/event/` | 301 | Pure POJOs, zero deps, 100% testable |
| `triggers/` | 1,506 | 9 classes with UserActivity constructors |
| `meta/` | 166 | Small, self-contained state tracking |
| `(root)` States | 3,000+ | Core framework, many testable methods |

## Adding New Tests

### Checklist

1. Place test in the **same package** as the source class
2. Name it `{ClassName}Test.java` or `{ClassName}ExtendedTest.java`
3. Use JUnit 4 (`@Test`, `@Before`, `@After`)
4. Use `CroquetTestUtils.nextTestUUID()` for any Group creation
5. Call appropriate `CroquetTestUtils.remove*Listeners()` after
   constructing Swing-backed state objects
6. No `@Rule TemporaryFolder` — tests are purely in-memory
7. No external network or file I/O

### Template

```java
package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MyNewFeatureTest {

  @Before
  public void setUp() {
    // Use CroquetTestUtils for UUIDs and listener cleanup
  }

  @Test
  public void testBasicBehavior() {
    // Arrange → Act → Assert
  }
}
```

## Troubleshooting

| Problem | Cause | Fix |
|---------|-------|-----|
| `NullPointerException` in `Application.getActiveInstance()` | Swing listener auto-registration | Call `CroquetTestUtils.remove*Listeners()` |
| `Group UUID already registered` | Static registry collision across tests | Use `CroquetTestUtils.nextTestUUID()` |
| Test hangs | AWT EventQueue blocked | Ensure `-Djava.awt.headless=true` is set |
| `ClassNotFoundException` for generated parser | Tweedle grammar submodule missing | Run `git submodule update --init tweedle-lang` |

## File Inventory

<details>
<summary>All test files — existing + planned (click to expand)</summary>

**Root package** (`org/lgna/croquet/`)
- `AbstractCompletionModelTest.java`
- `AbstractElementTest.java`, `AbstractElementExtendedTest.java`
- `AbstractModelTest.java`, `AbstractModelExtendedTest.java`
- `AbstractEditTest.java` *(planned)*
- `ActionOperationTest.java`
- `BooleanStateTest.java`, `BooleanStateExpandedTest.java` *(planned)*
- `BoundedDoubleStateTest.java`
- `BoundedIntegerStateTest.java`
- `CancelExceptionTest.java`, `CancelExceptionExtendedTest.java`
- `CodecExtendedTest.java`
- `CompositeLocalizationDelegateTest.java`
- `CompositeResourceManagerTest.java`
- `CompositeTabManagerTest.java`
- `CompositeViewLifecycleTest.java`
- `CroquetTestUtils.java` (helper)
- `CustomItemStateTest.java` *(planned)*
- `DataIndexPairTest.java`
- `EditInterfaceTest.java` *(planned)*
- `EnumConstantStateTest.java` *(planned)*
- `GroupTest.java`, `GroupExtendedTest.java`
- `InitializerTest.java` *(planned)*
- `InternalStateTypesTest.java`
- `ItemCodecArraysTest.java`
- `ItemStateContractTest.java` *(planned)*
- `IteratingOperationTest.java`
- `MultipleSelectionListStateTest.java`
- `OperationTest.java`, `OperationExtendedTest.java`
- `PlainStringValueTest.java` *(planned)*
- `SingleSelectListStateTest.java`
- `SingleThreadIteratingOperationTest.java`
- `StateContractTest.java` *(planned)*
- `StateBaseContractTest.java` *(planned)*
- `StringStateTest.java`, `StringStateExtendedTest.java`
- `StringValueTest.java`
- `TestBooleanState.java` (helper)
- `UndoHistoryExpandedTest.java` *(planned)*
- `ValueEventExpandedTest.java` *(planned)*
- `ValueHolderTest.java` *(planned)*

**codecs/**
- `ColorCodecExtendedTest.java`
- `DefaultItemCodecExtendedTest.java`
- `EnumCodecTest.java`
- `FileCodecExtendedTest.java`

**data/**
- `AbstractMutableListDataExtendedTest.java`
- `ImmutableListDataTest.java`, `ImmutableListDataExtendedTest.java`
- `ListDataContractTest.java`
- `MutableListDataTest.java`
- `RefreshableListDataTest.java`
- `TestListDataListener.java` (helper)

**edits/**
- `StateEditTest.java`

**event/**
- `ValueEventTest.java`

**history/** *(planned tests marked)*
- `ActivityNodeTest.java` *(planned)*
- `PrepStepTest.java` *(planned)*
- `TestActivityNode.java` (helper) *(planned)*
- `TestPrepStep.java` (helper) *(planned)*
- `UserActivityTest.java`, `UserActivityExtendedTest.java`

**history/event/** *(all planned)*
- `ActivityEventTest.java`
- `CancelEventTest.java`
- `ChangeEventTest.java`
- `EditCommittedEventTest.java`
- `FinishedEventTest.java`
- `ListenerContractTest.java`
- `PopupMenuResizedEventTest.java`

**icon/**
- `AbstractIconFactoryTest.java`
- `TrimmedIconTest.java`

**imp/booleanstate/**
- `BooleanStateImpTest.java`

**imp/liststate/**
- `SingleSelectListStateSwingModelTest.java`

**meta/** *(all planned)*
- `MetaStateTest.java`
- `StateTrackingMetaStateTest.java`

**preferences/**
- `PreferenceBooleanStateTest.java`
- `PreferenceManagerCodecTest.java`

**resolvers/** *(planned)*
- `RuntimeResolverTest.java`

**triggers/** *(all planned)*
- `CascadeAutomaticDeterminationTriggerTest.java`
- `ChangeEventTriggerTest.java`
- `EventObjectTriggerTest.java`
- `IterationTriggerTest.java`
- `TriggerBaseTest.java`

**undo/**
- `UndoHistoryTest.java`

**undo/event/** *(all planned)*
- `HistoryClearEventTest.java`
- `HistoryEventTest.java`
- `HistoryInsertionIndexEventTest.java`
- `HistoryPushEventTest.java`
- `TestHistoryListener.java` (helper)

**views/**
- `AwtHierarchyHandlerTest.java`
- `BorderPanelBuilderTest.java`
- `BoxUtilitiesTest.java`
- `FolderTabRendererTest.java`
- `FolderTabbedPaneExtractionTest.java`
- `FolderTitlesPanelTest.java`
- `FrameHeadlessTest.java`
- `SpringUtilitiesTest.java`
- `ToolPaletteLayoutTest.java`

</details>
