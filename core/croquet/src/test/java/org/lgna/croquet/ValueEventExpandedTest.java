package org.lgna.croquet;

import org.lgna.croquet.event.ValueEvent;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Expanded tests for {@link ValueEvent} — additional branch coverage for
 * factory methods, immutability, and toString formatting.
 */
public class ValueEventExpandedTest {

  // ── createInstance(prev, next, isAdjusting) ──────────────────────

  @Test
  public void createInstance_full_previousValid() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b", false);
    assertTrue(e.isPreviousValueValid());
  }

  @Test
  public void createInstance_full_nullPrev() {
    ValueEvent<String> e = ValueEvent.createInstance(null, "b", false);
    assertNull(e.getPreviousValue());
    assertTrue(e.isPreviousValueValid());
  }

  @Test
  public void createInstance_full_nullNext() {
    ValueEvent<String> e = ValueEvent.createInstance("a", null, false);
    assertNull(e.getNextValue());
  }

  @Test
  public void createInstance_full_bothNull() {
    ValueEvent<String> e = ValueEvent.createInstance(null, null, true);
    assertNull(e.getPreviousValue());
    assertNull(e.getNextValue());
    assertTrue(e.isAdjusting());
  }

  // ── createInstance(prev, next) ────────────────────────────────────

  @Test
  public void createInstance_twoArg_previousValid() {
    ValueEvent<Integer> e = ValueEvent.createInstance(10, 20);
    assertTrue(e.isPreviousValueValid());
    assertFalse(e.isAdjusting());
  }

  // ── createInstance(next) ─────────────────────────────────────────

  @Test
  public void createInstance_nextOnly_nullNext() {
    ValueEvent<String> e = ValueEvent.createInstance((String) null);
    assertNull(e.getNextValue());
    assertFalse(e.isPreviousValueValid());
  }

  // ── Integer events ───────────────────────────────────────────────

  @Test
  public void integerEvent_storesValues() {
    ValueEvent<Integer> e = ValueEvent.createInstance(1, 2);
    assertEquals(Integer.valueOf(1), e.getPreviousValue());
    assertEquals(Integer.valueOf(2), e.getNextValue());
  }

  // ── Boolean events ───────────────────────────────────────────────

  @Test
  public void booleanEvent_trueToFalse() {
    ValueEvent<Boolean> e = ValueEvent.createInstance(true, false);
    assertEquals(Boolean.TRUE, e.getPreviousValue());
    assertEquals(Boolean.FALSE, e.getNextValue());
  }

  // ── toString patterns ────────────────────────────────────────────

  @Test
  public void toString_adjusting_containsFlag() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b", true);
    assertTrue(e.toString().contains("isAdjusting=true"));
  }

  @Test
  public void toString_notAdjusting_noFlag() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b", false);
    assertFalse(e.toString().contains("isAdjusting"));
  }

  @Test
  public void toString_invalidPrevious() {
    ValueEvent<String> e = ValueEvent.createInstance("only");
    assertTrue(e.toString().contains("NOT VALID"));
  }

  @Test
  public void toString_containsArrow() {
    ValueEvent<String> e = ValueEvent.createInstance("x", "y");
    assertTrue(e.toString().contains("->"));
  }

  @Test
  public void toString_containsClassName() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b");
    assertTrue(e.toString().contains("ValueEvent"));
  }

  @Test
  public void toString_containsValues() {
    ValueEvent<String> e = ValueEvent.createInstance("hello", "world");
    String s = e.toString();
    assertTrue(s.contains("hello"));
    assertTrue(s.contains("world"));
  }

  // ── Enum values ──────────────────────────────────────────────────

  private enum TestEnum { A, B }

  @Test
  public void enumEvent_storesValues() {
    ValueEvent<TestEnum> e = ValueEvent.createInstance(TestEnum.A, TestEnum.B);
    assertEquals(TestEnum.A, e.getPreviousValue());
    assertEquals(TestEnum.B, e.getNextValue());
  }

  // ── List values ──────────────────────────────────────────────────

  @Test
  public void listEvent_storesReference() {
    java.util.List<String> list = java.util.Arrays.asList("a", "b");
    ValueEvent<java.util.List<String>> e = ValueEvent.createInstance(null, list);
    assertSame(list, e.getNextValue());
  }
}
