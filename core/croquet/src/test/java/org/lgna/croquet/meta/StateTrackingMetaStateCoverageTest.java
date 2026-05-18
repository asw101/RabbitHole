package org.lgna.croquet.meta;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.*;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link StateTrackingMetaState} — delegation to underlying
 * State, listener forwarding, getValue, and MetaState base class behavior.
 */
public class StateTrackingMetaStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0040-ffffffffffff"), "metaCov");

  private TestBooleanState underlyingState;
  private TestStateTrackingMetaState metaState;

  @Before
  public void setUp() {
    underlyingState = new TestBooleanState(TEST_GROUP, false);
    CroquetTestUtils.removeItemListeners(underlyingState);
    metaState = new TestStateTrackingMetaState(underlyingState);
  }

  // ── getValue delegation ───────────────────────────────────────────

  @Test
  public void getValue_delegatesToUnderlyingState() {
    assertEquals("false", metaState.getValue());
  }

  @Test
  public void getValue_reflectsUnderlyingStateChange() {
    underlyingState.setValueTransactionlessly(true);
    assertEquals("true", metaState.getValue());
  }

  @Test
  public void getValue_afterMultipleChanges() {
    underlyingState.setValueTransactionlessly(true);
    underlyingState.setValueTransactionlessly(false);
    assertEquals("false", metaState.getValue());
  }

  // ── listener forwarding ───────────────────────────────────────────

  @Test
  public void metaListener_firesOnUnderlyingStateChange() {
    AtomicReference<String> captured = new AtomicReference<>();
    metaState.addValueListener(e -> captured.set(e.getNextValue()));
    underlyingState.setValueTransactionlessly(true);
    assertEquals("true", captured.get());
  }

  @Test
  public void metaListener_firesCorrectPrevValue() {
    AtomicReference<String> capturedPrev = new AtomicReference<>();
    metaState.addValueListener(e -> capturedPrev.set(e.getPreviousValue()));
    underlyingState.setValueTransactionlessly(true);
    assertEquals("false", capturedPrev.get());
  }

  @Test
  public void metaListener_doesNotFire_whenSameValue() {
    AtomicInteger count = new AtomicInteger();
    metaState.addValueListener(e -> count.incrementAndGet());
    underlyingState.setValueTransactionlessly(false); // same
    assertEquals(0, count.get());
  }

  @Test
  public void metaListener_removeValueListener_hasBug_addsInsteadOfRemoves() {
    // Characterization: MetaState.removeValueListener() calls add() instead of
    // remove() — a pre-existing production bug. This test pins the actual behavior.
    AtomicInteger count = new AtomicInteger();
    org.lgna.croquet.event.ValueListener<String> listener = e -> count.incrementAndGet();
    metaState.addValueListener(listener);
    metaState.removeValueListener(listener);
    underlyingState.setValueTransactionlessly(true);
    // Bug: listener fires twice because removeValueListener adds it again
    assertEquals(2, count.get());
  }

  @Test
  public void addAndInvokeListener_firesImmediately() {
    AtomicReference<String> captured = new AtomicReference<>();
    metaState.addAndInvokeValueListener(e -> captured.set(e.getNextValue()));
    assertEquals("false", captured.get());
  }

  // ── multiple listeners ────────────────────────────────────────────

  @Test
  public void multipleListeners_allFire() {
    AtomicInteger count = new AtomicInteger();
    metaState.addValueListener(e -> count.incrementAndGet());
    metaState.addValueListener(e -> count.incrementAndGet());
    underlyingState.setValueTransactionlessly(true);
    assertEquals(2, count.get());
  }

  // ── MetaState base class ──────────────────────────────────────────

  @Test
  public void metaState_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(MetaState.class.getModifiers()));
  }

  @Test
  public void stateTrackingMetaState_extendsMetaState() {
    assertTrue(MetaState.class.isAssignableFrom(StateTrackingMetaState.class));
  }

  @Test
  public void stateTrackingMetaState_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(StateTrackingMetaState.class.getModifiers()));
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestStateTrackingMetaState extends StateTrackingMetaState<String, Boolean> {
    TestStateTrackingMetaState(State<Boolean> state) {
      super(state);
    }

    @Override
    protected String getValue(State<Boolean> state) {
      return String.valueOf(state.getValue());
    }
  }
}
