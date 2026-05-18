package org.lgna.croquet;

import org.lgna.croquet.event.ValueEvent;
import org.lgna.croquet.event.ValueListener;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Tests for {@link ValueHolder} — lightweight value container with
 * listener dispatch. Covers factory, get/set, listeners, and the known
 * removeValueListener bug.
 */
public class ValueHolderTest {

  // ── Factory ──────────────────────────────────────────────────────

  @Test
  public void createInstance_returnsNonNull() {
    ValueHolder<String> holder = ValueHolder.createInstance("hello");
    assertNotNull(holder);
  }

  @Test
  public void createInstance_nullValue() {
    ValueHolder<String> holder = ValueHolder.createInstance(null);
    assertNull(holder.getValue());
  }

  // ── getValue / setValue ───────────────────────────────────────────

  @Test
  public void getValue_returnsInitialValue() {
    ValueHolder<String> holder = ValueHolder.createInstance("test");
    assertEquals("test", holder.getValue());
  }

  @Test
  public void setValue_changesValue() {
    ValueHolder<String> holder = ValueHolder.createInstance("old");
    holder.setValue("new");
    assertEquals("new", holder.getValue());
  }

  @Test
  public void setValue_sameValue_noOp() {
    ValueHolder<String> holder = ValueHolder.createInstance("same");
    holder.setValue("same"); // Objects.equals => no listener fire
    assertEquals("same", holder.getValue());
  }

  @Test
  public void setValue_null() {
    ValueHolder<String> holder = ValueHolder.createInstance("value");
    holder.setValue(null);
    assertNull(holder.getValue());
  }

  @Test
  public void setValue_nullToNull_noOp() {
    ValueHolder<String> holder = ValueHolder.createInstance(null);
    AtomicInteger count = new AtomicInteger(0);
    holder.addValueListener(e -> count.incrementAndGet());
    holder.setValue(null);
    assertEquals(0, count.get());
  }

  // ── Integer values ───────────────────────────────────────────────

  @Test
  public void integerHolder_getValue() {
    ValueHolder<Integer> holder = ValueHolder.createInstance(42);
    assertEquals(Integer.valueOf(42), holder.getValue());
  }

  @Test
  public void integerHolder_setValue() {
    ValueHolder<Integer> holder = ValueHolder.createInstance(1);
    holder.setValue(2);
    assertEquals(Integer.valueOf(2), holder.getValue());
  }

  // ── Listener dispatch ────────────────────────────────────────────

  @Test
  public void addValueListener_firesOnChange() {
    ValueHolder<String> holder = ValueHolder.createInstance("old");
    AtomicReference<String> nextCapture = new AtomicReference<>();
    holder.addValueListener(e -> nextCapture.set(e.getNextValue()));
    holder.setValue("new");
    assertEquals("new", nextCapture.get());
  }

  @Test
  public void addValueListener_eventHasPreviousValue() {
    ValueHolder<String> holder = ValueHolder.createInstance("prev");
    AtomicReference<String> prevCapture = new AtomicReference<>();
    holder.addValueListener(e -> prevCapture.set(e.getPreviousValue()));
    holder.setValue("next");
    assertEquals("prev", prevCapture.get());
  }

  @Test
  public void addValueListener_sameValue_doesNotFire() {
    ValueHolder<String> holder = ValueHolder.createInstance("x");
    AtomicInteger count = new AtomicInteger(0);
    holder.addValueListener(e -> count.incrementAndGet());
    holder.setValue("x");
    assertEquals(0, count.get());
  }

  @Test
  public void multipleListeners_allFire() {
    ValueHolder<String> holder = ValueHolder.createInstance("a");
    AtomicInteger count1 = new AtomicInteger(0);
    AtomicInteger count2 = new AtomicInteger(0);
    holder.addValueListener(e -> count1.incrementAndGet());
    holder.addValueListener(e -> count2.incrementAndGet());
    holder.setValue("b");
    assertEquals(1, count1.get());
    assertEquals(1, count2.get());
  }

  @Test
  public void multipleChanges_fireMultipleTimes() {
    ValueHolder<String> holder = ValueHolder.createInstance("a");
    AtomicInteger count = new AtomicInteger(0);
    holder.addValueListener(e -> count.incrementAndGet());
    holder.setValue("b");
    holder.setValue("c");
    holder.setValue("d");
    assertEquals(3, count.get());
  }

  // ── addAndInvokeValueListener ────────────────────────────────────

  @Test
  public void addAndInvokeValueListener_firesImmediately() {
    ValueHolder<String> holder = ValueHolder.createInstance("value");
    AtomicReference<String> captured = new AtomicReference<>();
    holder.addAndInvokeValueListener(e -> captured.set(e.getNextValue()));
    assertEquals("value", captured.get());
  }

  @Test
  public void addAndInvokeValueListener_immediateEvent_previousInvalid() {
    ValueHolder<String> holder = ValueHolder.createInstance("val");
    AtomicReference<Boolean> prevValid = new AtomicReference<>();
    holder.addAndInvokeValueListener(e -> prevValid.set(e.isPreviousValueValid()));
    assertFalse(prevValid.get());
  }

  @Test
  public void addAndInvokeValueListener_alsoFiresOnSubsequentChange() {
    ValueHolder<String> holder = ValueHolder.createInstance("a");
    AtomicInteger count = new AtomicInteger(0);
    holder.addAndInvokeValueListener(e -> count.incrementAndGet());
    assertEquals(1, count.get());
    holder.setValue("b");
    assertEquals(2, count.get());
  }

  // ── removeValueListener — known bug ──────────────────────────────

  @Test
  public void removeValueListener_bug_addsInsteadOfRemoves() {
    // Characterization: removeValueListener has a bug — it calls add instead of remove.
    // Pin the actual behavior.
    ValueHolder<String> holder = ValueHolder.createInstance("a");
    AtomicInteger count = new AtomicInteger(0);
    ValueListener<String> listener = e -> count.incrementAndGet();
    holder.addValueListener(listener);
    holder.removeValueListener(listener); // bug: actually adds listener again
    holder.setValue("b");
    // Listener fires twice because it's registered twice
    assertEquals(2, count.get());
  }
}
