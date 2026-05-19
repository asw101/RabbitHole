package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.event.ValueEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Extended tests for {@link ValueEvent} — factory methods, field access,
 * toString, isAdjusting, and edge cases.
 */
public class ValueEventDeepTest {

  // ── createInstance(prev, next) ────────────────────────────────────

  @Test
  public void createInstance_prevNext_isPreviousValueValid_true() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b");
    assertTrue(e.isPreviousValueValid());
  }

  @Test
  public void createInstance_prevNext_previousValue_correct() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b");
    assertEquals("a", e.getPreviousValue());
  }

  @Test
  public void createInstance_prevNext_nextValue_correct() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b");
    assertEquals("b", e.getNextValue());
  }

  @Test
  public void createInstance_prevNext_isAdjusting_false() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b");
    assertFalse(e.isAdjusting());
  }

  // ── createInstance(prev, next, isAdjusting) ───────────────────────

  @Test
  public void createInstance_adjusting_true() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b", true);
    assertTrue(e.isAdjusting());
  }

  @Test
  public void createInstance_adjusting_false() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b", false);
    assertFalse(e.isAdjusting());
  }

  @Test
  public void createInstance_adjusting_previousValid() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b", true);
    assertTrue(e.isPreviousValueValid());
  }

  // ── createInstance(nextValue) ─────────────────────────────────────

  @Test
  public void createInstance_nextOnly_isPreviousValueValid_false() {
    ValueEvent<String> e = ValueEvent.createInstance("only");
    assertFalse(e.isPreviousValueValid());
  }

  @Test
  public void createInstance_nextOnly_previousValue_null() {
    ValueEvent<String> e = ValueEvent.createInstance("only");
    assertNull(e.getPreviousValue());
  }

  @Test
  public void createInstance_nextOnly_nextValue_correct() {
    ValueEvent<String> e = ValueEvent.createInstance("only");
    assertEquals("only", e.getNextValue());
  }

  @Test
  public void createInstance_nextOnly_isAdjusting_false() {
    ValueEvent<String> e = ValueEvent.createInstance("only");
    assertFalse(e.isAdjusting());
  }

  // ── Null values ───────────────────────────────────────────────────

  @Test
  public void createInstance_nullPrevious() {
    ValueEvent<String> e = ValueEvent.createInstance(null, "b");
    assertNull(e.getPreviousValue());
    assertTrue(e.isPreviousValueValid());
  }

  @Test
  public void createInstance_nullNext() {
    ValueEvent<String> e = ValueEvent.createInstance("a", null);
    assertNull(e.getNextValue());
    assertTrue(e.isPreviousValueValid());
  }

  @Test
  public void createInstance_bothNull() {
    ValueEvent<String> e = ValueEvent.createInstance(null, null);
    assertNull(e.getPreviousValue());
    assertNull(e.getNextValue());
  }

  @Test
  public void createInstance_nextOnly_null() {
    ValueEvent<String> e = ValueEvent.createInstance(null);
    assertNull(e.getNextValue());
    assertFalse(e.isPreviousValueValid());
  }

  // ── Integer type ──────────────────────────────────────────────────

  @Test
  public void integerEvent_previousValue() {
    ValueEvent<Integer> e = ValueEvent.createInstance(10, 20);
    assertEquals(Integer.valueOf(10), e.getPreviousValue());
  }

  @Test
  public void integerEvent_nextValue() {
    ValueEvent<Integer> e = ValueEvent.createInstance(10, 20);
    assertEquals(Integer.valueOf(20), e.getNextValue());
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_containsClassName() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b");
    assertTrue(e.toString().contains("ValueEvent"));
  }

  @Test
  public void toString_containsArrow() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b");
    assertTrue(e.toString().contains("->"));
  }

  @Test
  public void toString_prevValid_containsPreviousValue() {
    ValueEvent<String> e = ValueEvent.createInstance("alpha", "beta");
    assertTrue(e.toString().contains("alpha"));
  }

  @Test
  public void toString_prevInvalid_containsNotValid() {
    ValueEvent<String> e = ValueEvent.createInstance("only");
    assertTrue(e.toString().contains("NOT VALID"));
  }

  @Test
  public void toString_adjusting_containsIsAdjusting() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b", true);
    assertTrue(e.toString().contains("isAdjusting=true"));
  }

  @Test
  public void toString_notAdjusting_doesNotContainIsAdjusting() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b", false);
    assertFalse(e.toString().contains("isAdjusting"));
  }

  @Test
  public void toString_containsNextValue() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "beta");
    assertTrue(e.toString().contains("beta"));
  }

  // ── Same value events ─────────────────────────────────────────────

  @Test
  public void sameValues_previousAndNextEqual() {
    ValueEvent<String> e = ValueEvent.createInstance("same", "same");
    assertEquals(e.getPreviousValue(), e.getNextValue());
  }

  // ── Custom type ───────────────────────────────────────────────────

  @Test
  public void customType_event() {
    List<String> prev = new ArrayList<>();
    List<String> next = new ArrayList<>();
    next.add("item");
    ValueEvent<List<String>> e = ValueEvent.createInstance(prev, next);
    assertTrue(e.getPreviousValue().isEmpty());
    assertEquals(1, e.getNextValue().size());
  }
}
