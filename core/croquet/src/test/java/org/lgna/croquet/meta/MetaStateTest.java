package org.lgna.croquet.meta;

import org.lgna.croquet.event.ValueEvent;
import org.lgna.croquet.event.ValueListener;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Tests for {@link MetaState} — abstract value holder with listener dispatch.
 * Uses a concrete test subclass to exercise the protected API.
 */
public class MetaStateTest {

  @Test
  public void getValue_returnsCurrentValue() {
    TestMetaState state = new TestMetaState("initial");
    assertEquals("initial", state.getValue());
  }

  @Test
  public void addValueListener_firesOnChange() {
    TestMetaState state = new TestMetaState("a");
    AtomicReference<String> captured = new AtomicReference<>();
    state.addValueListener(e -> captured.set(e.getNextValue()));
    state.setValueAndFire("b");
    assertEquals("b", captured.get());
  }

  @Test
  public void addValueListener_eventHasPreviousValue() {
    TestMetaState state = new TestMetaState("old");
    AtomicReference<String> prevCapture = new AtomicReference<>();
    state.addValueListener(e -> prevCapture.set(e.getPreviousValue()));
    state.setValueAndFire("new");
    assertEquals("old", prevCapture.get());
  }

  @Test
  public void noChange_doesNotFireListener() {
    TestMetaState state = new TestMetaState("same");
    AtomicInteger count = new AtomicInteger(0);
    state.addValueListener(e -> count.incrementAndGet());
    state.setValueAndFire("same");
    assertEquals(0, count.get());
  }

  @Test
  public void addAndInvokeValueListener_firesImmediately() {
    TestMetaState state = new TestMetaState("value");
    AtomicReference<String> captured = new AtomicReference<>();
    state.addAndInvokeValueListener(e -> captured.set(e.getNextValue()));
    assertEquals("value", captured.get());
  }

  @Test
  public void addAndInvokeValueListener_alsoFiresOnChange() {
    TestMetaState state = new TestMetaState("a");
    AtomicInteger count = new AtomicInteger(0);
    state.addAndInvokeValueListener(e -> count.incrementAndGet());
    assertEquals(1, count.get()); // immediate invocation
    state.setValueAndFire("b");
    assertEquals(2, count.get()); // change notification
  }

  @Test
  public void removeValueListener_bug_stillAdds() {
    // Characterization: MetaState.removeValueListener has a bug — it calls add instead of remove.
    // This test pins the actual behavior.
    TestMetaState state = new TestMetaState("a");
    AtomicInteger count = new AtomicInteger(0);
    ValueListener<String> listener = e -> count.incrementAndGet();
    state.addValueListener(listener);
    state.removeValueListener(listener);
    state.setValueAndFire("b");
    // Bug: listener fires twice because removeValueListener actually adds it again
    assertEquals(2, count.get());
  }

  @Test
  public void multipleListeners_allFire() {
    TestMetaState state = new TestMetaState("a");
    AtomicInteger count1 = new AtomicInteger(0);
    AtomicInteger count2 = new AtomicInteger(0);
    state.addValueListener(e -> count1.incrementAndGet());
    state.addValueListener(e -> count2.incrementAndGet());
    state.setValueAndFire("b");
    assertEquals(1, count1.get());
    assertEquals(1, count2.get());
  }

  @Test
  public void multipleChanges_fireMultipleTimes() {
    TestMetaState state = new TestMetaState("a");
    AtomicInteger count = new AtomicInteger(0);
    state.addValueListener(e -> count.incrementAndGet());
    state.setValueAndFire("b");
    state.setValueAndFire("c");
    state.setValueAndFire("d");
    assertEquals(3, count.get());
  }

  @Test
  public void nullValue_handledCorrectly() {
    TestMetaState state = new TestMetaState(null);
    assertNull(state.getValue());
  }

  @Test
  public void nullToNonNull_firesListener() {
    TestMetaState state = new TestMetaState(null);
    AtomicReference<String> captured = new AtomicReference<>();
    state.addValueListener(e -> captured.set(e.getNextValue()));
    state.setValueAndFire("value");
    assertEquals("value", captured.get());
  }

  /**
   * Concrete MetaState for testing.
   */
  private static class TestMetaState extends MetaState<String> {
    private String value;

    TestMetaState(String initial) {
      this.value = initial;
      this.setPrevValue(initial);
    }

    @Override
    public String getValue() {
      return value;
    }

    void setValueAndFire(String newValue) {
      this.value = newValue;
      checkValueAndFireIfAppropriate();
    }
  }
}
