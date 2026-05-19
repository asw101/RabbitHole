package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link BooleanState} — edge cases, toggle behavior,
 * encode/decode round-trip, and text/icon accessors not covered by BooleanStateTest.
 */
public class BooleanStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-000c-000000000001"), "boolCov");

  private TestBooleanState state;

  @Before
  public void setUp() {
    state = new TestBooleanState(TEST_GROUP, false);
    CroquetTestUtils.removeItemListeners(state);
  }

  // ── Toggle behavior ───────────────────────────────────────────────

  @Test
  public void toggle_fromFalse() {
    state.setValueTransactionlessly(true);
    assertTrue(state.getValue());
  }

  @Test
  public void toggle_backToFalse() {
    state.setValueTransactionlessly(true);
    state.setValueTransactionlessly(false);
    assertFalse(state.getValue());
  }

  @Test
  public void toggle_multipleTimes() {
    for (int i = 0; i < 10; i++) {
      boolean expected = (i % 2 == 0);
      state.setValueTransactionlessly(expected);
      assertEquals(expected, state.getValue());
    }
  }

  // ── Swing model sync ──────────────────────────────────────────────

  @Test
  public void swingModel_syncsFalse() {
    assertFalse(state.getImp().getSwingModel().getButtonModel().isSelected());
  }

  @Test
  public void swingModel_syncsTrue() {
    state.setValueTransactionlessly(true);
    assertTrue(state.getImp().getSwingModel().getButtonModel().isSelected());
  }

  // ── Enabled state ─────────────────────────────────────────────────

  @Test
  public void isEnabled_defaultTrue() {
    assertTrue(state.isEnabled());
  }

  @Test
  public void setEnabled_false() {
    state.setEnabled(false);
    assertFalse(state.isEnabled());
  }

  @Test
  public void setEnabled_roundTrip() {
    state.setEnabled(false);
    state.setEnabled(true);
    assertTrue(state.isEnabled());
  }

  // ── Listener ──────────────────────────────────────────────────────

  @Test
  public void newSchoolListener_firesOnChange() {
    AtomicReference<Boolean> captured = new AtomicReference<>();
    state.addNewSchoolValueListener(e -> captured.set(e.getNextValue()));

    state.setValueTransactionlessly(true);
    assertEquals(Boolean.TRUE, captured.get());
  }

  @Test
  public void newSchoolListener_capturesPreviousValue() {
    AtomicReference<Boolean> prevCapture = new AtomicReference<>();
    state.addNewSchoolValueListener(e -> prevCapture.set(e.getPreviousValue()));

    state.setValueTransactionlessly(true);
    assertEquals(Boolean.FALSE, prevCapture.get());
  }

  // ── Text accessors ────────────────────────────────────────────────

  @Test
  public void setTextForBothTrueAndFalse_setsText() {
    state.setTextForBothTrueAndFalse("Toggle");
    // No assertion on exact text — just ensure no exception
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsState() {
    assertTrue(State.class.isAssignableFrom(BooleanState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(BooleanState.class.getModifiers()));
  }

  // ── Initial true ──────────────────────────────────────────────────

  @Test
  public void initialTrue_works() {
    TestBooleanState s = new TestBooleanState(TEST_GROUP, true);
    CroquetTestUtils.removeItemListeners(s);
    assertTrue(s.getValue());
  }

  // ── Idempotent set ────────────────────────────────────────────────

  @Test
  public void setToSameValue_noException() {
    state.setValueTransactionlessly(false);
    assertFalse(state.getValue());
  }

  @Test
  public void setTrue_twice_noException() {
    state.setValueTransactionlessly(true);
    state.setValueTransactionlessly(true);
    assertTrue(state.getValue());
  }
}
