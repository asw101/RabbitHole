package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link BoundedDoubleState} — boundary values,
 * Swing model sync, small step sizes, and negative ranges.
 */
public class BoundedDoubleStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-000e-000000000001"), "dblCov");

  private static class TestBoundedDoubleState extends BoundedDoubleState {
    TestBoundedDoubleState(Details details) {
      super(details);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestBoundedDoubleState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }
  }

  private TestBoundedDoubleState state;

  @Before
  public void setUp() {
    BoundedDoubleState.Details details = new BoundedDoubleState.Details(TEST_GROUP, CroquetTestUtils.nextTestUUID())
        .minimum(0.0).maximum(1.0).initialValue(0.5).stepSize(0.1);
    state = new TestBoundedDoubleState(details);
    CroquetTestUtils.removeSpinnerChangeListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsInitialValue() {
    assertEquals(0.5, state.getValue(), 0.001);
  }

  @Test
  public void constructor_atMinimum() {
    BoundedDoubleState.Details d = new BoundedDoubleState.Details(TEST_GROUP, CroquetTestUtils.nextTestUUID())
        .minimum(0.0).maximum(1.0).initialValue(0.0).stepSize(0.1);
    TestBoundedDoubleState s = new TestBoundedDoubleState(d);
    CroquetTestUtils.removeSpinnerChangeListeners(s);
    assertEquals(0.0, s.getValue(), 0.001);
  }

  @Test
  public void constructor_atMaximum() {
    BoundedDoubleState.Details d = new BoundedDoubleState.Details(TEST_GROUP, CroquetTestUtils.nextTestUUID())
        .minimum(0.0).maximum(1.0).initialValue(1.0).stepSize(0.1);
    TestBoundedDoubleState s = new TestBoundedDoubleState(d);
    CroquetTestUtils.removeSpinnerChangeListeners(s);
    assertEquals(1.0, s.getValue(), 0.001);
  }

  // ── Value manipulation ────────────────────────────────────────────

  @Test
  public void setValueTransactionlessly_changes() {
    state.setValueTransactionlessly(0.75);
    assertEquals(0.75, state.getValue(), 0.001);
  }

  @Test
  public void setValueTransactionlessly_toMinimum() {
    state.setValueTransactionlessly(0.0);
    assertEquals(0.0, state.getValue(), 0.001);
  }

  @Test
  public void setValueTransactionlessly_toMaximum() {
    state.setValueTransactionlessly(1.0);
    assertEquals(1.0, state.getValue(), 0.001);
  }

  @Test
  public void setValueTransactionlessly_roundTrip() {
    state.setValueTransactionlessly(0.1);
    state.setValueTransactionlessly(0.5);
    assertEquals(0.5, state.getValue(), 0.001);
  }

  // ── Spinner model sync ────────────────────────────────────────────

  @Test
  public void spinnerModel_syncedToValue() {
    state.setValueTransactionlessly(0.25);
    Number spinnerVal = (Number) state.getSwingModel().getSpinnerModel().getValue();
    assertEquals(0.25, spinnerVal.doubleValue(), 0.001);
  }

  // ── Negative range ────────────────────────────────────────────────

  @Test
  public void negativeRange_works() {
    BoundedDoubleState.Details d = new BoundedDoubleState.Details(TEST_GROUP, CroquetTestUtils.nextTestUUID())
        .minimum(-10.0).maximum(10.0).initialValue(0.0).stepSize(0.5);
    TestBoundedDoubleState s = new TestBoundedDoubleState(d);
    CroquetTestUtils.removeSpinnerChangeListeners(s);
    assertEquals(0.0, s.getValue(), 0.001);
    s.setValueTransactionlessly(-5.0);
    assertEquals(-5.0, s.getValue(), 0.001);
  }

  // ── Small step size ───────────────────────────────────────────────

  @Test
  public void smallStepSize_works() {
    BoundedDoubleState.Details d = new BoundedDoubleState.Details(TEST_GROUP, CroquetTestUtils.nextTestUUID())
        .minimum(0.0).maximum(0.01).initialValue(0.005).stepSize(0.001);
    TestBoundedDoubleState s = new TestBoundedDoubleState(d);
    CroquetTestUtils.removeSpinnerChangeListeners(s);
    assertEquals(0.005, s.getValue(), 0.0001);
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsBoundedNumberState() {
    assertTrue(BoundedNumberState.class.isAssignableFrom(BoundedDoubleState.class));
  }

  @Test
  public void class_extendsState() {
    assertTrue(State.class.isAssignableFrom(BoundedDoubleState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(BoundedDoubleState.class.getModifiers()));
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
}
