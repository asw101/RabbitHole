package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.event.ValueEvent;
import org.lgna.croquet.event.ValueListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ValueHolderTest {

  private ValueHolder<String> holder;
  private List<ValueEvent<String>> events;
  private ValueListener<String> listener;

  @Before
  public void setUp() {
    holder = ValueHolder.createInstance("alpha");
    events = new ArrayList<ValueEvent<String>>();
    listener = new ValueListener<String>() {
      @Override
      public void valueChanged(ValueEvent<String> e) {
        events.add(e);
      }
    };
  }

  // ── Creation ───────────────────────────────────────────────────────

  @Test
  public void createInstance_returnsNonNull() {
    assertNotNull(holder);
  }

  @Test
  public void getValue_returnsInitialValue() {
    assertEquals("alpha", holder.getValue());
  }

  @Test
  public void createInstance_withNullInitialValue() {
    ValueHolder<String> nullHolder = ValueHolder.createInstance(null);

    assertNotNull(nullHolder);
    assertNull(nullHolder.getValue());
  }

  // ── Mutation ───────────────────────────────────────────────────────

  @Test
  public void setValue_changesValue() {
    holder.setValue("beta");

    assertEquals("beta", holder.getValue());
  }

  @Test
  public void setValue_sameValue_isNoOp() {
    holder.addValueListener(listener);

    holder.setValue("alpha");

    assertTrue(events.isEmpty());
    assertEquals("alpha", holder.getValue());
  }

  @Test
  public void setValue_nullWorks() {
    holder.addValueListener(listener);

    holder.setValue(null);

    assertNull(holder.getValue());
    ValueEvent<String> event = onlyEvent();
    assertEquals("alpha", event.getPreviousValue());
    assertNull(event.getNextValue());
  }

  // ── Listener dispatch ──────────────────────────────────────────────

  @Test
  public void addValueListener_andSetValue_firesListener() {
    holder.addValueListener(listener);

    holder.setValue("beta");

    assertEquals(1, events.size());
  }

  @Test
  public void addAndInvokeValueListener_firesImmediatelyWithInitialValue() {
    holder.addAndInvokeValueListener(listener);

    ValueEvent<String> event = onlyEvent();
    assertFalse(event.isPreviousValueValid());
    assertNull(event.getPreviousValue());
    assertEquals("alpha", event.getNextValue());
  }

  @Test
  public void removeValueListener_stopsNotifications() {
    holder.addValueListener(listener);
    holder.removeValueListener(listener);

    holder.setValue("beta");

    assertTrue(events.isEmpty());
  }

  @Test
  public void multipleListeners_allNotified() {
    final AtomicInteger firstCount = new AtomicInteger();
    final AtomicInteger secondCount = new AtomicInteger();

    holder.addValueListener(new ValueListener<String>() {
      @Override
      public void valueChanged(ValueEvent<String> e) {
        firstCount.incrementAndGet();
      }
    });
    holder.addValueListener(new ValueListener<String>() {
      @Override
      public void valueChanged(ValueEvent<String> e) {
        secondCount.incrementAndGet();
      }
    });

    holder.setValue("beta");

    assertEquals(1, firstCount.get());
    assertEquals(1, secondCount.get());
  }

  @Test
  public void listener_receivesCorrectOldAndNewValues() {
    holder.addValueListener(listener);

    holder.setValue("beta");

    ValueEvent<String> event = onlyEvent();
    assertTrue(event.isPreviousValueValid());
    assertEquals("alpha", event.getPreviousValue());
    assertEquals("beta", event.getNextValue());
  }

  @Test
  public void concurrentModificationSafety_addListenerDuringNotification() {
    final AtomicInteger firstCount = new AtomicInteger();
    final AtomicInteger secondCount = new AtomicInteger();
    final ValueListener<String> secondListener = new ValueListener<String>() {
      @Override
      public void valueChanged(ValueEvent<String> e) {
        secondCount.incrementAndGet();
      }
    };

    holder.addValueListener(new ValueListener<String>() {
      @Override
      public void valueChanged(ValueEvent<String> e) {
        firstCount.incrementAndGet();
        holder.addValueListener(secondListener);
      }
    });

    holder.setValue("beta");
    holder.setValue("gamma");

    assertEquals(2, firstCount.get());
    assertEquals(1, secondCount.get());
  }

  // ── Object contract ────────────────────────────────────────────────

  @Test
  public void toString_containsValueInfo() {
    String text = holder.toString();

    assertNotNull(text);
    assertTrue(text.contains("alpha"));
  }

  private ValueEvent<String> onlyEvent() {
    assertEquals(1, events.size());
    return events.get(0);
  }
}
