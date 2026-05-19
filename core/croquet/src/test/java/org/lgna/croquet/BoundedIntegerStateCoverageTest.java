package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link BoundedIntegerState} — boundary values,
 * clamping, Swing model sync, and edge cases.
 */
public class BoundedIntegerStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-000d-000000000001"), "intCov");

  private static class TestBoundedIntegerState extends BoundedIntegerState {
    TestBoundedIntegerState(Details details) {
      super(details);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestBoundedIntegerState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }
  }

  private TestBoundedIntegerState state;

  @Before
  public void setUp() {
    BoundedIntegerState.Details details = new BoundedIntegerState.Details(TEST_GROUP, CroquetTestUtils.nextTestUUID())
        .minimum(0).maximum(100).initialValue(50).stepSize(1);
    state = new TestBoundedIntegerState(details);
    CroquetTestUtils.removeSpinnerChangeListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsInitialValue() {
    assertEquals(Integer.valueOf(50), state.getValue());
  }

  @Test
  public void constructor_atMinimum() {
    BoundedIntegerState.Details d = new BoundedIntegerState.Details(TEST_GROUP, CroquetTestUtils.nextTestUUID())
        .minimum(0).maximum(100).initialValue(0).stepSize(1);
    TestBoundedIntegerState s = new TestBoundedIntegerState(d);
    CroquetTestUtils.removeSpinnerChangeListeners(s);
    assertEquals(Integer.valueOf(0), s.getValue());
  }

  @Test
  public void constructor_atMaximum() {
    BoundedIntegerState.Details d = new BoundedIntegerState.Details(TEST_GROUP, CroquetTestUtils.nextTestUUID())
        .minimum(0).maximum(100).initialValue(100).stepSize(1);
    TestBoundedIntegerState s = new TestBoundedIntegerState(d);
    CroquetTestUtils.removeSpinnerChangeListeners(s);
    assertEquals(Integer.valueOf(100), s.getValue());
  }

  // ── Value manipulation ────────────────────────────────────────────

  @Test
  public void setValueTransactionlessly_changes() {
    state.setValueTransactionlessly(75);
    assertEquals(Integer.valueOf(75), state.getValue());
  }

  @Test
  public void setValueTransactionlessly_toMinimum() {
    state.setValueTransactionlessly(0);
    assertEquals(Integer.valueOf(0), state.getValue());
  }

  @Test
  public void setValueTransactionlessly_toMaximum() {
    state.setValueTransactionlessly(100);
    assertEquals(Integer.valueOf(100), state.getValue());
  }

  @Test
  public void setValueTransactionlessly_roundTrip() {
    state.setValueTransactionlessly(10);
    state.setValueTransactionlessly(50);
    assertEquals(Integer.valueOf(50), state.getValue());
  }

  // ── Spinner model sync ────────────────────────────────────────────

  @Test
  public void spinnerModel_syncedToValue() {
    state.setValueTransactionlessly(25);
    Number spinnerVal = (Number) state.getSwingModel().getSpinnerModel().getValue();
    assertEquals(25, spinnerVal.intValue());
  }

  @Test
  public void spinnerModel_minimum() {
    Number min = (Number) ((javax.swing.SpinnerNumberModel) state.getSwingModel().getSpinnerModel()).getMinimum();
    assertEquals(0, min.intValue());
  }

  @Test
  public void spinnerModel_maximum() {
    Number max = (Number) ((javax.swing.SpinnerNumberModel) state.getSwingModel().getSpinnerModel()).getMaximum();
    assertEquals(100, max.intValue());
  }

  // ── Negative range ────────────────────────────────────────────────

  @Test
  public void negativeRange_works() {
    BoundedIntegerState.Details d = new BoundedIntegerState.Details(TEST_GROUP, CroquetTestUtils.nextTestUUID())
        .minimum(-50).maximum(50).initialValue(0).stepSize(1);
    TestBoundedIntegerState s = new TestBoundedIntegerState(d);
    CroquetTestUtils.removeSpinnerChangeListeners(s);
    assertEquals(Integer.valueOf(0), s.getValue());
    s.setValueTransactionlessly(-25);
    assertEquals(Integer.valueOf(-25), s.getValue());
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsBoundedNumberState() {
    assertTrue(BoundedNumberState.class.isAssignableFrom(BoundedIntegerState.class));
  }

  @Test
  public void class_extendsState() {
    assertTrue(State.class.isAssignableFrom(BoundedIntegerState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(BoundedIntegerState.class.getModifiers()));
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
