package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.event.ValueEvent;
import org.lgna.croquet.event.ValueListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class ValueHolderDeepTest {

  private ValueHolder<Integer> holder;
  private List<ValueEvent<Integer>> events;
  private ValueListener<Integer> listener;

  @Before
  public void setUp() {
    holder = ValueHolder.createInstance(0);
    events = new ArrayList<>();
    listener = events::add;
  }

  // ── Numeric type instantiation ─────────────────────────────────────

  @Test
  public void createInstance_withIntegerType() {
    ValueHolder<Integer> h = ValueHolder.createInstance(42);
    assertEquals(Integer.valueOf(42), h.getValue());
  }

  @Test
  public void createInstance_withDoubleType() {
    ValueHolder<Double> h = ValueHolder.createInstance(3.14);
    assertEquals(Double.valueOf(3.14), h.getValue());
  }

  @Test
  public void createInstance_withBooleanType() {
    ValueHolder<Boolean> h = ValueHolder.createInstance(true);
    assertTrue(h.getValue());
  }

  // ── Rapid successive changes ───────────────────────────────────────

  @Test
  public void setValue_manyTimes_allEventsDelivered() {
    holder.addValueListener(listener);

    for (int i = 1; i <= 50; i++) {
      holder.setValue(i);
    }

    assertEquals(50, events.size());
    assertEquals(Integer.valueOf(50), holder.getValue());
  }

  @Test
  public void setValue_alternatingValues_correctEventCount() {
    holder.addValueListener(listener);

    holder.setValue(1);
    holder.setValue(0);
    holder.setValue(1);
    holder.setValue(0);

    assertEquals(4, events.size());
  }

  // ── Event carries correct values ───────────────────────────────────

  @Test
  public void events_chainCorrectly_previousMatchesPriorNext() {
    holder.addValueListener(listener);

    holder.setValue(10);
    holder.setValue(20);
    holder.setValue(30);

    assertEquals(3, events.size());

    assertEquals(Integer.valueOf(0), events.get(0).getPreviousValue());
    assertEquals(Integer.valueOf(10), events.get(0).getNextValue());

    assertEquals(Integer.valueOf(10), events.get(1).getPreviousValue());
    assertEquals(Integer.valueOf(20), events.get(1).getNextValue());

    assertEquals(Integer.valueOf(20), events.get(2).getPreviousValue());
    assertEquals(Integer.valueOf(30), events.get(2).getNextValue());
  }

  // ── Null transitions ──────────────────────────────────────────────

  @Test
  public void setValue_nullToNonNull_firesEvent() {
    ValueHolder<String> h = ValueHolder.createInstance(null);
    List<ValueEvent<String>> captured = new ArrayList<>();
    h.addValueListener(captured::add);

    h.setValue("hello");

    assertEquals(1, captured.size());
    assertNull(captured.get(0).getPreviousValue());
    assertEquals("hello", captured.get(0).getNextValue());
  }

  @Test
  public void setValue_nullToNull_noEvent() {
    ValueHolder<String> h = ValueHolder.createInstance(null);
    List<ValueEvent<String>> captured = new ArrayList<>();
    h.addValueListener(captured::add);

    h.setValue(null);

    assertTrue(captured.isEmpty());
  }

  // ── Same-value no-op ─────────────────────────────────────────────

  @Test
  public void setValue_sameValue_isNoOp() {
    holder.addValueListener(listener);
    holder.setValue(0);
    assertTrue(events.isEmpty());
    assertEquals(Integer.valueOf(0), holder.getValue());
  }

  @Test
  public void setValue_nonNullToNull_firesEvent() {
    ValueHolder<String> h = ValueHolder.createInstance("hello");
    List<ValueEvent<String>> captured = new ArrayList<>();
    h.addValueListener(captured::add);

    h.setValue(null);

    assertNull(h.getValue());
    assertEquals(1, captured.size());
    assertEquals("hello", captured.get(0).getPreviousValue());
    assertNull(captured.get(0).getNextValue());
  }

  // ── Remove listener stops notifications ────────────────────────────

  @Test
  public void removeValueListener_stopsNotifications() {
    holder.addValueListener(listener);
    holder.removeValueListener(listener);
    holder.setValue(99);
    assertTrue(events.isEmpty());
  }

  // ── Add listener during notification ───────────────────────────────

  @Test
  public void addListenerDuringNotification_doesNotFail() {
    final AtomicInteger firstCount = new AtomicInteger();
    final AtomicInteger secondCount = new AtomicInteger();
    final ValueListener<Integer> secondListener = e -> secondCount.incrementAndGet();

    holder.addValueListener(e -> {
      firstCount.incrementAndGet();
      holder.addValueListener(secondListener);
    });

    holder.setValue(1);
    holder.setValue(2);

    assertEquals(2, firstCount.get());
    assertEquals(1, secondCount.get());
  }

  // ── Remove during iteration ────────────────────────────────────────

  @Test
  public void removeListenerDuringNotification_doesNotFail() {
    final AtomicInteger callCount = new AtomicInteger();
    final ValueListener<Integer>[] selfRef = new ValueListener[1];

    selfRef[0] = e -> {
      callCount.incrementAndGet();
      holder.removeValueListener(selfRef[0]);
    };

    holder.addValueListener(selfRef[0]);
    holder.setValue(1);
    holder.setValue(2);

    assertEquals(1, callCount.get());
  }

  // ── addAndInvokeValueListener ──────────────────────────────────────

  @Test
  public void addAndInvokeValueListener_subsequentChangesAlsoDelivered() {
    List<ValueEvent<Integer>> captured = new ArrayList<>();
    holder.addAndInvokeValueListener(captured::add);

    holder.setValue(5);

    assertEquals(2, captured.size());
    assertFalse(captured.get(0).isPreviousValueValid());
    assertTrue(captured.get(1).isPreviousValueValid());
  }

  @Test
  public void addAndInvokeValueListener_nullValue() {
    ValueHolder<String> h = ValueHolder.createInstance(null);
    List<ValueEvent<String>> captured = new ArrayList<>();
    h.addAndInvokeValueListener(captured::add);

    assertEquals(1, captured.size());
    assertNull(captured.get(0).getNextValue());
  }

  // ── Class structure ────────────────────────────────────────────────

  @Test
  public void class_isFinal() {
    assertTrue(Modifier.isFinal(ValueHolder.class.getModifiers()));
  }

  @Test
  public void constructor_isPrivate() throws Exception {
    java.lang.reflect.Constructor<?>[] ctors = ValueHolder.class.getDeclaredConstructors();
    for (java.lang.reflect.Constructor<?> ctor : ctors) {
      assertTrue(Modifier.isPrivate(ctor.getModifiers()));
    }
  }

  @Test
  public void valueListeners_field_exists() throws Exception {
    Field f = ValueHolder.class.getDeclaredField("valueListeners");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void value_field_exists() throws Exception {
    Field f = ValueHolder.class.getDeclaredField("value");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  // ── toString robustness ────────────────────────────────────────────

  @Test
  public void toString_withNull_doesNotThrow() {
    ValueHolder<String> h = ValueHolder.createInstance(null);
    String text = h.toString();
    assertNotNull(text);
    assertTrue(text.contains("null"));
  }

  @Test
  public void toString_withObject_containsObjectRepresentation() {
    ValueHolder<Integer> h = ValueHolder.createInstance(42);
    assertTrue(h.toString().contains("42"));
  }

  // ── Multiple listeners ordering ────────────────────────────────────

  @Test
  public void multipleListeners_firesInAddOrder() {
    List<String> order = new ArrayList<>();

    holder.addValueListener(e -> order.add("first"));
    holder.addValueListener(e -> order.add("second"));
    holder.addValueListener(e -> order.add("third"));

    holder.setValue(1);

    assertEquals(3, order.size());
    assertEquals("first", order.get(0));
    assertEquals("second", order.get(1));
    assertEquals("third", order.get(2));
  }

  // ── Same listener added twice ──────────────────────────────────────

  @Test
  public void sameListener_addedTwice_firesForEachRegistration() {
    AtomicInteger count = new AtomicInteger();
    ValueListener<Integer> l = e -> count.incrementAndGet();

    holder.addValueListener(l);
    holder.addValueListener(l);
    holder.setValue(1);

    assertEquals(2, count.get());
  }

  // ── Remove non-existent listener ───────────────────────────────────

  @Test
  public void removeValueListener_notAdded_doesNotThrow() {
    holder.removeValueListener(e -> {});
  }

  // ── Generics with complex type ─────────────────────────────────────

  @Test
  public void createInstance_withListType() {
    List<String> list = new ArrayList<>();
    list.add("a");
    ValueHolder<List<String>> h = ValueHolder.createInstance(list);
    assertSame(list, h.getValue());
  }
}
