package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import javax.swing.SpinnerNumberModel;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Tests for {@link BoundedIntegerState} — bounded integer state with a
 * Details builder, SpinnerNumberModel/BoundedRangeModel sync, and
 * bidirectional model linkage.
 *
 * <p>The ChangeListener on the SpinnerModel is removed in setUp to
 * avoid Application.getActiveInstance() dependency.</p>
 */
public class BoundedIntegerStateTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-0003-ffffffffffff"), "intTest");

  private TestBoundedIntegerState state;

  @Before
  public void setUp() {
    BoundedIntegerState.Details details =
        new BoundedIntegerState.Details(TEST_GROUP, UUID.randomUUID())
            .minimum(0)
            .maximum(100)
            .initialValue(50)
            .stepSize(1);
    state = new TestBoundedIntegerState(details);
    CroquetTestUtils.removeSpinnerChangeListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsInitialValue() {
    assertEquals(Integer.valueOf(50), state.getValue());
  }

  @Test
  public void constructor_setsMinimum() {
    assertEquals(Integer.valueOf(0), state.getMinimum());
  }

  @Test
  public void constructor_setsMaximum() {
    assertEquals(Integer.valueOf(100), state.getMaximum());
  }

  // ── Details builder ───────────────────────────────────────────────

  @Test
  public void details_customValues() {
    BoundedIntegerState.Details d =
        new BoundedIntegerState.Details(TEST_GROUP, UUID.randomUUID())
            .minimum(10)
            .maximum(200)
            .initialValue(50)
            .stepSize(5)
            .extent(10);
    TestBoundedIntegerState custom = new TestBoundedIntegerState(d);
    CroquetTestUtils.removeSpinnerChangeListeners(custom);
    assertEquals(Integer.valueOf(50), custom.getValue());
    assertEquals(Integer.valueOf(10), custom.getMinimum());
    assertEquals(Integer.valueOf(200), custom.getMaximum());
  }

  // ── setValueTransactionlessly ─────────────────────────────────────

  @Test
  public void setValueTransactionlessly_updatesValue() {
    state.setValueTransactionlessly(75);
    assertEquals(Integer.valueOf(75), state.getValue());
  }

  @Test
  public void setValueTransactionlessly_syncsSpinnerModel() {
    state.setValueTransactionlessly(25);
    assertEquals(25, state.getSwingModel().getSpinnerModel().getValue());
  }

  @Test
  public void setValueTransactionlessly_syncsBoundedRangeModel() {
    state.setValueTransactionlessly(30);
    assertEquals(30, state.getSwingModel().getBoundedRangeModel().getValue());
  }

  // ── setMinimum / setMaximum ───────────────────────────────────────

  @Test
  public void setMinimum_updatesMinimum() {
    state.setMinimum(10);
    assertEquals(Integer.valueOf(10), state.getMinimum());
  }

  @Test
  public void setMaximum_updatesMaximum() {
    state.setMaximum(200);
    assertEquals(Integer.valueOf(200), state.getMaximum());
  }

  // ── BoundedRangeModel access ──────────────────────────────────────

  @Test
  public void getBoundedRangeModel_isNonNull() {
    assertNotNull(state.getSwingModel().getBoundedRangeModel());
  }

  @Test
  public void getBoundedRangeModel_valueMatchesState() {
    assertEquals(50, state.getSwingModel().getBoundedRangeModel().getValue());
  }

  @Test
  public void getBoundedRangeModel_minimumMatchesState() {
    assertEquals(0, state.getSwingModel().getBoundedRangeModel().getMinimum());
  }

  @Test
  public void getBoundedRangeModel_maximumMatchesState() {
    assertEquals(100, state.getSwingModel().getBoundedRangeModel().getMaximum());
  }

  // ── SpinnerModel access ───────────────────────────────────────────

  @Test
  public void getSpinnerModel_isNonNull() {
    assertNotNull(state.getSwingModel().getSpinnerModel());
  }

  @Test
  public void getSpinnerModel_valueMatchesState() {
    assertEquals(50, state.getSwingModel().getSpinnerModel().getValue());
  }

  // ── appendRepresentation ──────────────────────────────────────────

  @Test
  public void appendRepresentation_appendsIntValue() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, 42);
    assertEquals("42", sb.toString());
  }

  // ── getPotentialPrepModelPaths ─────────────────────────────────────

  @Test
  public void getPotentialPrepModelPaths_returnsEmptyList() {
    assertTrue(state.getPotentialPrepModelPaths(null).isEmpty());
  }

  // ── AtomicChange ──────────────────────────────────────────────────

  @Test
  public void setAll_updatesMultipleProperties() {
    BoundedNumberState.AtomicChange<Integer> change =
        new BoundedNumberState.AtomicChange<Integer>()
            .value(75)
            .minimum(10)
            .maximum(200);
    state.setAll(change);
    assertEquals(Integer.valueOf(75), state.getValue());
  }

  @Test
  public void setAll_withStepSize_updatesStepSize() {
    BoundedNumberState.AtomicChange<Integer> change =
        new BoundedNumberState.AtomicChange<Integer>()
            .stepSize(5);
    state.setAll(change);
    assertEquals(5, state.getSwingModel().getSpinnerModel().getStepSize());
  }

  // ── Old-school value listener ─────────────────────────────────────

  @Test
  public void valueListener_firesOnChange() {
    AtomicReference<Integer> captured = new AtomicReference<>();
    State.ValueListener<Integer> listener = new State.ValueListener<Integer>() {
      @Override
      public void changing(State<Integer> s, Integer prev, Integer next) {}

      @Override
      public void changed(State<Integer> s, Integer prev, Integer next) {
        captured.set(next);
      }
    };
    state.addValueListener(listener);
    state.setValueTransactionlessly(75);
    assertEquals(Integer.valueOf(75), captured.get());
  }

  // ── changeValueFromEdit ───────────────────────────────────────────

  @Test
  public void changeValueFromEdit_updatesValue() {
    state.changeValueFromEdit(80);
    assertEquals(Integer.valueOf(80), state.getValue());
  }

  // ── Edge: initial value at minimum ────────────────────────────────

  @Test
  public void initialValueAtMinimum() {
    BoundedIntegerState.Details d =
        new BoundedIntegerState.Details(TEST_GROUP, UUID.randomUUID())
            .minimum(0)
            .maximum(100)
            .initialValue(0);
    TestBoundedIntegerState s = new TestBoundedIntegerState(d);
    CroquetTestUtils.removeSpinnerChangeListeners(s);
    assertEquals(Integer.valueOf(0), s.getValue());
  }

  // ── Edge: initial value at maximum ────────────────────────────────

  @Test
  public void initialValueAtMaximum() {
    BoundedIntegerState.Details d =
        new BoundedIntegerState.Details(TEST_GROUP, UUID.randomUUID())
            .minimum(0)
            .maximum(100)
            .initialValue(100);
    TestBoundedIntegerState s = new TestBoundedIntegerState(d);
    CroquetTestUtils.removeSpinnerChangeListeners(s);
    assertEquals(Integer.valueOf(100), s.getValue());
  }

  // ── addAndInvokeValueListener ───────────────────────────────────

  @Test
  public void addAndInvokeValueListener_firesImmediately() {
    AtomicReference<Integer> captured = new AtomicReference<>();
    state.addAndInvokeValueListener(new State.ValueListener<Integer>() {
      @Override
      public void changing(State<Integer> s, Integer prev, Integer next) {}

      @Override
      public void changed(State<Integer> s, Integer prev, Integer next) {
        captured.set(next);
      }
    });
    assertEquals(Integer.valueOf(50), captured.get());
  }

  // ── addAndInvokeNewSchoolValueListener ────────────────────────────

  @Test
  public void addAndInvokeNewSchoolListener_firesImmediately() {
    AtomicReference<Integer> captured = new AtomicReference<>();
    state.addAndInvokeNewSchoolValueListener(e -> captured.set(e.getNextValue()));
    assertEquals(Integer.valueOf(50), captured.get());
  }

  // ── initializeIfNecessary ─────────────────────────────────────────

  @Test
  public void initializeIfNecessary_doesNotThrow() {
    state.initializeIfNecessary();
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestBoundedIntegerState extends BoundedIntegerState {
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
}
