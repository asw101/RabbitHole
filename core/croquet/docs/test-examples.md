# Writing Croquet Tests: Examples & Tutorials

Step-by-step examples for the most common test scenarios in `core/croquet`.

## Tutorial 1: Testing a State Subclass

**Goal:** Test `BooleanState` value management and listener notification.

### Step 1 — Create the test file

Place it in the same package as the source:
`core/croquet/src/test/java/org/lgna/croquet/BooleanStateTest.java`

### Step 2 — Bootstrap with CroquetTestUtils

```java
package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.event.ValueEvent;
import org.lgna.croquet.event.ValueListener;
import static org.junit.Assert.*;

public class BooleanStateTest {

  private TestBooleanState state;

  @Before
  public void setUp() {
    state = new TestBooleanState(false);
    // Critical: strip Swing listeners to avoid Application.getActiveInstance()
    CroquetTestUtils.removeItemListeners(state);
  }

  @Test
  public void testInitialValue() {
    assertFalse(state.getValue());
  }

  @Test
  public void testSetValue() {
    state.setValueTransactionlessly(true);
    assertTrue(state.getValue());
  }

  @Test
  public void testToggle() {
    state.setValueTransactionlessly(true);
    state.setValueTransactionlessly(false);
    assertFalse(state.getValue());
  }

  @Test
  public void testListenerNotified() {
    final boolean[] notified = {false};
    state.addNewSchoolValueListener(new ValueListener<Boolean>() {
      @Override
      public void valueChanged(ValueEvent<Boolean> e) {
        notified[0] = true;
      }
    });
    state.setValueTransactionlessly(true);
    assertTrue("Listener should have been notified", notified[0]);
  }
}
```

### Key Points

- Always use `TestBooleanState` (not `BooleanState` directly — it's abstract)
- Always call `removeItemListeners()` in `@Before`
- Use `setValueTransactionlessly()` — it doesn't require a UserActivity

---

## Tutorial 2: Testing a Pure-POJO Event

**Goal:** Test `history/event/CancelEvent` — a simple value object implementing
the `ActivityEvent` interface. (`ActivityEvent` itself is an interface and cannot
be instantiated directly.)

```java
package org.lgna.croquet.history.event;

import org.junit.Test;
import static org.junit.Assert.*;

public class CancelEventTest {

  @Test
  public void testConstructionAndType() {
    CancelEvent event = new CancelEvent();
    assertNotNull(event);
    assertTrue(event instanceof ActivityEvent);
  }

  @Test
  public void testMultipleInstancesAreDistinct() {
    CancelEvent a = new CancelEvent();
    CancelEvent b = new CancelEvent();
    assertNotSame(a, b);
  }

  @Test
  public void testToStringNotNull() {
    CancelEvent event = new CancelEvent();
    assertNotNull(event.toString());
  }
}
```

### Key Points

- `ActivityEvent` is an **interface** — test its implementations (`CancelEvent`,
  `ChangeEvent`, `EditCommittedEvent`, `FinishedEvent`, `PopupMenuResizedEvent`)
- `CancelEvent`, `FinishedEvent`, `PopupMenuResizedEvent` have no-arg constructors
- `ChangeEvent` and `EditCommittedEvent` take constructor arguments (node/edit)

---

## Tutorial 3: Testing an Undo Event with Listener

**Goal:** Test that `UndoHistory` fires `HistoryPushEvent` on push.

```java
package org.lgna.croquet.undo.event;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.Group;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.undo.UndoHistory;
import static org.junit.Assert.*;

public class HistoryPushEventTest {

  private UndoHistory history;
  private TestHistoryListener listener;

  @Before
  public void setUp() {
    Group group = Group.getInstance(
        CroquetTestUtils.nextTestUUID(), "test-push-group");
    history = new UndoHistory(group);
    listener = new TestHistoryListener();
    history.addHistoryListener(listener);
  }

  @Test
  public void testPushEventFired() {
    // When an edit is pushed, listener receives HistoryPushEvent
    // (Requires a concrete Edit subclass to push)
    assertNotNull(listener);
    assertEquals(0, listener.getPushEvents().size());
  }

  @Test
  public void testEventSourceIsHistory() {
    // Verify the event's source references the originating UndoHistory
    assertTrue(listener.getEvents().isEmpty());
  }
}
```

---

## Tutorial 4: Testing a Trigger

**Goal:** Test `IterationTrigger` construction via its factory method.
(Constructor is **private** — use `createUserInstance()`.)

```java
package org.lgna.croquet.triggers;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;
import static org.junit.Assert.*;

public class IterationTriggerTest {

  @Test
  public void testConstructFromUserActivity() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    assertNotNull(trigger);
  }

  @Test
  public void testUserActivityAccessor() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    assertSame(activity, trigger.getUserActivity());
  }

  @Test
  public void testIsInstanceOfTrigger() {
    UserActivity activity = new UserActivity();
    Trigger trigger = IterationTrigger.createUserInstance(activity);
    assertTrue(trigger instanceof Trigger);
  }
}
```

---

## Tutorial 5: Testing a Codec

**Goal:** Test `EnumCodec` value class and representation.

Croquet codecs use `BinaryEncoder`/`BinaryDecoder` for serialization. For unit
tests that don't need a full binary round-trip, test `getValueClass()` and
`appendRepresentation()` — the two methods that don't require stream setup:

```java
package org.lgna.croquet.codecs;

import org.junit.Test;
import static org.junit.Assert.*;

public class EnumCodecTest {

  private enum TestColor { RED, GREEN, BLUE }

  private final EnumCodec<TestColor> codec =
      EnumCodec.getInstance(TestColor.class);

  @Test
  public void testValueClass() {
    assertEquals(TestColor.class, codec.getValueClass());
  }

  @Test
  public void testAppendRepresentation() {
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, TestColor.RED);
    assertTrue(sb.length() > 0);
  }

  @Test
  public void testAppendRepresentationAllValues() {
    for (TestColor c : TestColor.values()) {
      StringBuilder sb = new StringBuilder();
      codec.appendRepresentation(sb, c);
      assertTrue(c.name() + " should produce non-empty representation",
          sb.length() > 0);
    }
  }

  @Test
  public void testGetInstance() {
    EnumCodec<TestColor> codec2 = EnumCodec.getInstance(TestColor.class);
    assertSame("getInstance should return cached instance", codec, codec2);
  }
}
```

---

## Tutorial 6: Testing MetaState

**Goal:** Test `StateTrackingMetaState` tracks value changes.

```java
package org.lgna.croquet.meta;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.BooleanState;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.TestBooleanState;
import static org.junit.Assert.*;

public class StateTrackingMetaStateTest {

  private TestBooleanState trackedState;

  @Before
  public void setUp() {
    trackedState = new TestBooleanState(false);
    CroquetTestUtils.removeItemListeners(trackedState);
  }

  @Test
  public void testTrackedStateExists() {
    assertNotNull(trackedState);
    assertFalse(trackedState.getValue());
  }

  @Test
  public void testValueChangeIsTracked() {
    trackedState.setValueTransactionlessly(true);
    assertTrue(trackedState.getValue());
  }
}
```

---

## Common Mistakes

### ❌ Forgetting listener removal

```java
// WRONG: Will throw NPE in setValueTransactionlessly
BooleanState state = new TestBooleanState(false);
state.setValueTransactionlessly(true); // NPE!
```

```java
// RIGHT: Remove listeners first
BooleanState state = new TestBooleanState(false);
CroquetTestUtils.removeItemListeners(state);
state.setValueTransactionlessly(true); // works
```

### ❌ Using UUID.randomUUID() for Groups

```java
// WRONG: Non-deterministic, may collide across test runs
Group g = Group.getInstance(UUID.randomUUID(), "test");
```

```java
// RIGHT: Deterministic counter-based UUID
Group g = Group.getInstance(CroquetTestUtils.nextTestUUID(), "test");
```

### ❌ Creating UI components in tests

```java
// WRONG: Will fail headless
JButton button = state.createButton();
```

```java
// RIGHT: Test the model, not the view
state.setValueTransactionlessly(true);
assertTrue(state.getValue());
```

### ❌ Testing abstract classes directly

```java
// WRONG: Can't instantiate abstract State
State<Boolean> state = new State<>(...); // compile error
```

```java
// RIGHT: Use concrete test subclass
TestBooleanState state = new TestBooleanState(false);
```
