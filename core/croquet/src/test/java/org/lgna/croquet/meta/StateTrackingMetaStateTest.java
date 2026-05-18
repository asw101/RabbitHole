package org.lgna.croquet.meta;

import org.lgna.croquet.*;
import org.lgna.croquet.event.ValueListener;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Tests for {@link StateTrackingMetaState} — meta-state that derives its
 * value from a tracked {@link State} and fires when the derived value changes.
 */
public class StateTrackingMetaStateTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-a001-ffffffffffff"), "metaTrackTest");

  private TestBooleanState boolState;
  private LengthMetaState metaState;

  @Before
  public void setUp() {
    boolState = new TestBooleanState(TEST_GROUP, false);
    CroquetTestUtils.removeItemListeners(boolState);
    metaState = new LengthMetaState(boolState);
  }

  @Test
  public void getValue_returnsInitialDerivedValue() {
    // BooleanState false => "false" length = 5
    assertEquals(Integer.valueOf(5), metaState.getValue());
  }

  @Test
  public void getValue_updatesWhenStateChanges() {
    boolState.setValueTransactionlessly(true);
    // BooleanState true => "true" length = 4
    assertEquals(Integer.valueOf(4), metaState.getValue());
  }

  @Test
  public void listener_firesWhenDerivedValueChanges() {
    AtomicReference<Integer> captured = new AtomicReference<>();
    metaState.addValueListener(e -> captured.set(e.getNextValue()));
    boolState.setValueTransactionlessly(true);
    assertEquals(Integer.valueOf(4), captured.get());
  }

  @Test
  public void listener_previousValueIsCorrect() {
    AtomicReference<Integer> prevCapture = new AtomicReference<>();
    metaState.addValueListener(e -> prevCapture.set(e.getPreviousValue()));
    boolState.setValueTransactionlessly(true);
    assertEquals(Integer.valueOf(5), prevCapture.get());
  }

  @Test
  public void listener_noFireWhenDerivedValueUnchanged() {
    AtomicInteger count = new AtomicInteger(0);
    metaState.addValueListener(e -> count.incrementAndGet());
    // Setting the same value shouldn't change the derived value
    boolState.setValueTransactionlessly(false);
    assertEquals(0, count.get());
  }

  @Test
  public void addAndInvokeValueListener_firesImmediately() {
    AtomicReference<Integer> captured = new AtomicReference<>();
    metaState.addAndInvokeValueListener(e -> captured.set(e.getNextValue()));
    assertEquals(Integer.valueOf(5), captured.get());
  }

  @Test
  public void multipleChanges_allFire() {
    AtomicInteger count = new AtomicInteger(0);
    metaState.addValueListener(e -> count.incrementAndGet());
    boolState.setValueTransactionlessly(true);  // 5 -> 4, fires
    boolState.setValueTransactionlessly(false); // 4 -> 5, fires
    assertEquals(2, count.get());
  }

  /**
   * StateTrackingMetaState that derives the string length of the boolean value.
   */
  private static class LengthMetaState extends StateTrackingMetaState<Integer, Boolean> {
    LengthMetaState(State<Boolean> state) {
      super(state);
    }

    @Override
    protected Integer getValue(State<Boolean> state) {
      return String.valueOf(state.getValue()).length();
    }
  }
}
