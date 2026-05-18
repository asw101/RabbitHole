package org.lgna.croquet;

import org.lgna.croquet.event.ValueEvent;
import org.lgna.croquet.event.ValueListener;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Tests for the {@link State} abstract class contract — tested through
 * {@link BooleanState} / {@link TestBooleanState}. Covers value management,
 * listener dispatch, codec methods, and state initialization.
 */
public class StateContractTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-f010-ffffffffffff"), "stateContractTest");

  private TestBooleanState state;

  @Before
  public void setUp() {
    state = new TestBooleanState(TEST_GROUP, false);
    CroquetTestUtils.removeItemListeners(state);
  }

  // ── getValue / setValueTransactionlessly ─────────────────────────

  @Test
  public void getValue_returnsCurrentValue() {
    assertEquals(Boolean.FALSE, state.getValue());
  }

  @Test
  public void setValueTransactionlessly_updatesValue() {
    state.setValueTransactionlessly(true);
    assertEquals(Boolean.TRUE, state.getValue());
  }

  // ── Old-school ValueListener ─────────────────────────────────────

  @Test
  public void addValueListener_firesChanged() {
    AtomicReference<Boolean> captured = new AtomicReference<>();
    state.addValueListener(new State.ValueListener<Boolean>() {
      @Override public void changing(State<Boolean> s, Boolean prev, Boolean next) {}
      @Override public void changed(State<Boolean> s, Boolean prev, Boolean next) {
        captured.set(next);
      }
    });
    state.setValueTransactionlessly(true);
    assertEquals(Boolean.TRUE, captured.get());
  }

  @Test
  public void addValueListener_firesChanging() {
    AtomicReference<Boolean> captured = new AtomicReference<>();
    state.addValueListener(new State.ValueListener<Boolean>() {
      @Override public void changing(State<Boolean> s, Boolean prev, Boolean next) {
        captured.set(prev);
      }
      @Override public void changed(State<Boolean> s, Boolean prev, Boolean next) {}
    });
    state.setValueTransactionlessly(true);
    assertEquals(Boolean.FALSE, captured.get());
  }

  @Test
  public void removeValueListener_stops_firing() {
    AtomicInteger count = new AtomicInteger(0);
    State.ValueListener<Boolean> listener = new State.ValueListener<Boolean>() {
      @Override public void changing(State<Boolean> s, Boolean prev, Boolean next) {}
      @Override public void changed(State<Boolean> s, Boolean prev, Boolean next) {
        count.incrementAndGet();
      }
    };
    state.addValueListener(listener);
    state.removeValueListener(listener);
    state.setValueTransactionlessly(true);
    assertEquals(0, count.get());
  }

  @Test
  public void addAndInvokeValueListener_firesImmediately() {
    AtomicReference<Boolean> captured = new AtomicReference<>();
    state.addAndInvokeValueListener(new State.ValueListener<Boolean>() {
      @Override public void changing(State<Boolean> s, Boolean prev, Boolean next) {}
      @Override public void changed(State<Boolean> s, Boolean prev, Boolean next) {
        captured.set(next);
      }
    });
    assertEquals(Boolean.FALSE, captured.get());
  }

  // ── New-school ValueListener ─────────────────────────────────────

  @Test
  public void addNewSchoolValueListener_firesOnChange() {
    AtomicReference<Boolean> captured = new AtomicReference<>();
    state.addNewSchoolValueListener(e -> captured.set(e.getNextValue()));
    state.setValueTransactionlessly(true);
    assertEquals(Boolean.TRUE, captured.get());
  }

  @Test
  public void addAndInvokeNewSchoolValueListener_firesImmediately() {
    AtomicReference<Boolean> captured = new AtomicReference<>();
    state.addAndInvokeNewSchoolValueListener(e -> captured.set(e.getNextValue()));
    assertEquals(Boolean.FALSE, captured.get());
  }

  // ── changeValueFromEdit ──────────────────────────────────────────

  @Test
  public void changeValueFromEdit_updatesValue() {
    state.changeValueFromEdit(true);
    assertTrue(state.getValue());
  }

  @Test
  public void changeValueFromEdit_firesListeners() {
    AtomicInteger count = new AtomicInteger(0);
    state.addValueListener(new State.ValueListener<Boolean>() {
      @Override public void changing(State<Boolean> s, Boolean prev, Boolean next) {}
      @Override public void changed(State<Boolean> s, Boolean prev, Boolean next) {
        count.incrementAndGet();
      }
    });
    state.changeValueFromEdit(true);
    assertEquals(1, count.get());
  }

  // ── appendRepresentation ─────────────────────────────────────────

  @Test
  public void appendRepresentation_boolean() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, true);
    assertEquals("true", sb.toString());
  }

  // ── isEnabled ────────────────────────────────────────────────────

  @Test
  public void isEnabled_defaultTrue() {
    assertTrue(state.isEnabled());
  }

  @Test
  public void setEnabled_changesState() {
    state.setEnabled(false);
    assertFalse(state.isEnabled());
  }

  // ── initializeIfNecessary ────────────────────────────────────────

  @Test
  public void initializeIfNecessary_doesNotThrow() {
    state.initializeIfNecessary();
  }

  @Test
  public void initializeIfNecessary_idempotent() {
    state.initializeIfNecessary();
    state.initializeIfNecessary();
  }
}
