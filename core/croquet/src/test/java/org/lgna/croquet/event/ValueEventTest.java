package org.lgna.croquet.event;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link ValueEvent} — immutable value-change event with
 * previous/next values and adjusting flag.
 */
public class ValueEventTest {

  // ── Factory: createInstance(prev, next, isAdjusting) ──────────────

  @Test
  public void createInstance_full_setsValues() {
    ValueEvent<String> e = ValueEvent.createInstance("old", "new", false);
    assertEquals("old", e.getPreviousValue());
    assertEquals("new", e.getNextValue());
    assertFalse(e.isAdjusting());
    assertTrue(e.isPreviousValueValid());
  }

  @Test
  public void createInstance_full_adjusting() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b", true);
    assertTrue(e.isAdjusting());
  }

  // ── Factory: createInstance(prev, next) ────────────────────────────

  @Test
  public void createInstance_twoArg_defaultNotAdjusting() {
    ValueEvent<Integer> e = ValueEvent.createInstance(1, 2);
    assertEquals(Integer.valueOf(1), e.getPreviousValue());
    assertEquals(Integer.valueOf(2), e.getNextValue());
    assertFalse(e.isAdjusting());
    assertTrue(e.isPreviousValueValid());
  }

  // ── Factory: createInstance(next) ─────────────────────────────────

  @Test
  public void createInstance_nextOnly_previousInvalid() {
    ValueEvent<String> e = ValueEvent.createInstance("value");
    assertFalse(e.isPreviousValueValid());
    assertNull(e.getPreviousValue());
    assertEquals("value", e.getNextValue());
    assertFalse(e.isAdjusting());
  }

  // ── Null values ───────────────────────────────────────────────────

  @Test
  public void createInstance_nullPrevAndNext() {
    ValueEvent<String> e = ValueEvent.createInstance(null, null);
    assertNull(e.getPreviousValue());
    assertNull(e.getNextValue());
    assertTrue(e.isPreviousValueValid());
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_containsClassName() {
    ValueEvent<String> e = ValueEvent.createInstance("old", "new");
    assertTrue(e.toString().contains("ValueEvent"));
  }

  @Test
  public void toString_containsArrow() {
    ValueEvent<String> e = ValueEvent.createInstance("old", "new");
    assertTrue(e.toString().contains("->"));
  }

  @Test
  public void toString_containsValues() {
    ValueEvent<String> e = ValueEvent.createInstance("alpha", "bravo");
    String s = e.toString();
    assertTrue(s.contains("alpha"));
    assertTrue(s.contains("bravo"));
  }

  @Test
  public void toString_invalidPrevious_showsNotValid() {
    ValueEvent<String> e = ValueEvent.createInstance("value");
    assertTrue(e.toString().contains("NOT VALID"));
  }

  @Test
  public void toString_adjusting_showsFlag() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b", true);
    assertTrue(e.toString().contains("isAdjusting=true"));
  }

  @Test
  public void toString_notAdjusting_noFlag() {
    ValueEvent<String> e = ValueEvent.createInstance("a", "b", false);
    assertFalse(e.toString().contains("isAdjusting"));
  }
}
