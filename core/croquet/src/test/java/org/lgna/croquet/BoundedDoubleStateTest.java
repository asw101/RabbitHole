package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Tests for {@link BoundedDoubleState} — bounded double state with
 * a Details builder, toInt/toDouble conversion, and SpinnerModel/
 * BoundedRangeModel sync.
 *
 * <p>The ChangeListener on the SpinnerModel is removed in setUp to
 * avoid Application.getActiveInstance() dependency.</p>
 */
public class BoundedDoubleStateTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-0004-ffffffffffff"), "dblTest");

  private TestBoundedDoubleState state;

  @Before
  public void setUp() {
    BoundedDoubleState.Details details =
        new BoundedDoubleState.Details(TEST_GROUP, UUID.randomUUID())
            .minimum(0.0)
            .maximum(1.0)
            .initialValue(0.5)
            .stepSize(0.01);
    state = new TestBoundedDoubleState(details);
    CroquetTestUtils.removeSpinnerChangeListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsInitialValue() {
    assertEquals(0.5, state.getValue(), 0.001);
  }

  @Test
  public void constructor_setsMinimum() {
    assertEquals(0.0, state.getMinimum(), 0.001);
  }

  @Test
  public void constructor_setsMaximum() {
    assertEquals(1.0, state.getMaximum(), 0.001);
  }

  // ── Details builder ───────────────────────────────────────────────

  @Test
  public void details_customValues() {
    BoundedDoubleState.Details d =
        new BoundedDoubleState.Details(TEST_GROUP, UUID.randomUUID())
            .minimum(-10.0)
            .maximum(10.0)
            .initialValue(0.0)
            .stepSize(0.5);
    TestBoundedDoubleState custom = new TestBoundedDoubleState(d);
    CroquetTestUtils.removeSpinnerChangeListeners(custom);
    assertEquals(0.0, custom.getValue(), 0.001);
    assertEquals(-10.0, custom.getMinimum(), 0.001);
    assertEquals(10.0, custom.getMaximum(), 0.001);
  }

  // ── setValueTransactionlessly ─────────────────────────────────────

  @Test
  public void setValueTransactionlessly_updatesValue() {
    state.setValueTransactionlessly(0.75);
    assertEquals(0.75, state.getValue(), 0.001);
  }

  @Test
  public void setValueTransactionlessly_syncsSpinnerModel() {
    state.setValueTransactionlessly(0.25);
    Number spinnerValue = (Number) state.getSwingModel().getSpinnerModel().getValue();
    assertEquals(0.25, spinnerValue.doubleValue(), 0.001);
  }

  // ── setMinimum / setMaximum ───────────────────────────────────────

  @Test
  public void setMinimum_updatesMinimum() {
    state.setMinimum(-5.0);
    assertEquals(-5.0, state.getMinimum(), 0.001);
  }

  @Test
  public void setMaximum_updatesMaximum() {
    state.setMaximum(10.0);
    assertEquals(10.0, state.getMaximum(), 0.001);
  }

  // ── SpinnerModel access ───────────────────────────────────────────

  @Test
  public void getSpinnerModel_isNonNull() {
    assertNotNull(state.getSwingModel().getSpinnerModel());
  }

  // ── BoundedRangeModel access ──────────────────────────────────────

  @Test
  public void getBoundedRangeModel_isNonNull() {
    assertNotNull(state.getSwingModel().getBoundedRangeModel());
  }

  // ── appendRepresentation ──────────────────────────────────────────

  @Test
  public void appendRepresentation_appendsDoubleValue() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, 0.42);
    assertEquals("0.42", sb.toString());
  }

  // ── getPotentialPrepModelPaths ─────────────────────────────────────

  @Test
  public void getPotentialPrepModelPaths_returnsEmptyList() {
    assertTrue(state.getPotentialPrepModelPaths(null).isEmpty());
  }

  // ── AtomicChange ──────────────────────────────────────────────────

  @Test
  public void setAll_updatesMultipleProperties() {
    BoundedNumberState.AtomicChange<Double> change =
        new BoundedNumberState.AtomicChange<Double>()
            .value(0.75)
            .minimum(-1.0)
            .maximum(2.0);
    state.setAll(change);
    assertEquals(0.75, state.getValue(), 0.001);
  }

  @Test
  public void setAll_withStepSize_updatesStepSize() {
    BoundedNumberState.AtomicChange<Double> change =
        new BoundedNumberState.AtomicChange<Double>()
            .stepSize(0.1);
    state.setAll(change);
    assertEquals(0.1, state.getSwingModel().getSpinnerModel().getStepSize().doubleValue(), 0.001);
  }

  // ── Old-school value listener ─────────────────────────────────────

  @Test
  public void valueListener_firesOnChange() {
    AtomicReference<Double> captured = new AtomicReference<>();
    State.ValueListener<Double> listener = new State.ValueListener<Double>() {
      @Override
      public void changing(State<Double> s, Double prev, Double next) {}

      @Override
      public void changed(State<Double> s, Double prev, Double next) {
        captured.set(next);
      }
    };
    state.addValueListener(listener);
    state.setValueTransactionlessly(0.75);
    assertEquals(0.75, captured.get(), 0.001);
  }

  // ── changeValueFromEdit ───────────────────────────────────────────

  @Test
  public void changeValueFromEdit_updatesValue() {
    state.changeValueFromEdit(0.9);
    assertEquals(0.9, state.getValue(), 0.001);
  }

  // ── Edge: initial value at minimum ────────────────────────────────

  @Test
  public void initialValueAtMinimum() {
    BoundedDoubleState.Details d =
        new BoundedDoubleState.Details(TEST_GROUP, UUID.randomUUID())
            .minimum(0.0)
            .maximum(1.0)
            .initialValue(0.0)
            .stepSize(0.01);
    TestBoundedDoubleState s = new TestBoundedDoubleState(d);
    CroquetTestUtils.removeSpinnerChangeListeners(s);
    assertEquals(0.0, s.getValue(), 0.001);
  }

  // ── Edge: initial value at maximum ────────────────────────────────

  @Test
  public void initialValueAtMaximum() {
    BoundedDoubleState.Details d =
        new BoundedDoubleState.Details(TEST_GROUP, UUID.randomUUID())
            .minimum(0.0)
            .maximum(1.0)
            .initialValue(1.0)
            .stepSize(0.01);
    TestBoundedDoubleState s = new TestBoundedDoubleState(d);
    CroquetTestUtils.removeSpinnerChangeListeners(s);
    assertEquals(1.0, s.getValue(), 0.001);
  }

  // ── Multiple value changes ────────────────────────────────────────

  @Test
  public void multipleChanges_lastValueSticks() {
    state.setValueTransactionlessly(0.1);
    state.setValueTransactionlessly(0.2);
    state.setValueTransactionlessly(0.3);
    assertEquals(0.3, state.getValue(), 0.001);
  }

  // ── addAndInvokeValueListener ───────────────────────────────────

  @Test
  public void addAndInvokeValueListener_firesImmediately() {
    AtomicReference<Double> captured = new AtomicReference<>();
    state.addAndInvokeValueListener(new State.ValueListener<Double>() {
      @Override
      public void changing(State<Double> s, Double prev, Double next) {}

      @Override
      public void changed(State<Double> s, Double prev, Double next) {
        captured.set(next);
      }
    });
    assertEquals(0.5, captured.get(), 0.001);
  }

  // ── addAndInvokeNewSchoolValueListener ────────────────────────────

  @Test
  public void addAndInvokeNewSchoolListener_firesImmediately() {
    AtomicReference<Double> captured = new AtomicReference<>();
    state.addAndInvokeNewSchoolValueListener(e -> captured.set(e.getNextValue()));
    assertEquals(0.5, captured.get(), 0.001);
  }

  // ── changing callback ─────────────────────────────────────────────

  @Test
  public void changingCallback_firesBeforeChanged() {
    java.util.List<String> order = new java.util.ArrayList<>();
    state.addValueListener(new State.ValueListener<Double>() {
      @Override
      public void changing(State<Double> s, Double prev, Double next) {
        order.add("changing");
      }

      @Override
      public void changed(State<Double> s, Double prev, Double next) {
        order.add("changed");
      }
    });
    state.setValueTransactionlessly(0.75);
    assertEquals(2, order.size());
    assertEquals("changing", order.get(0));
    assertEquals("changed", order.get(1));
  }

  // ── initializeIfNecessary ─────────────────────────────────────────

  @Test
  public void initializeIfNecessary_doesNotThrow() {
    state.initializeIfNecessary();
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestBoundedDoubleState extends BoundedDoubleState {
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
}
