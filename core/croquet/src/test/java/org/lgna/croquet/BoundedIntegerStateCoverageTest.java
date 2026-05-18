package org.lgna.croquet;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Extended coverage tests for {@link BoundedIntegerState} — boundary values,
 * encode/decode round-trip, clamping behavior, AtomicChange, and listener edge cases.
 */
public class BoundedIntegerStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0003-ffffffffffff"), "intCov");

  private BoundedIntegerStateTest.TestBoundedIntegerState state;

  @Before
  public void setUp() {
    BoundedIntegerState.Details details =
        new BoundedIntegerState.Details(TEST_GROUP, CroquetTestUtils.nextTestUUID())
            .minimum(0)
            .maximum(100)
            .initialValue(50)
            .stepSize(1);
    state = new BoundedIntegerStateTest.TestBoundedIntegerState(details);
    CroquetTestUtils.removeSpinnerChangeListeners(state);
  }

  // ── encode/decode round-trip ──────────────────────────────────────

  @Test
  public void encodeAndDecode_midRange_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, 42);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(Integer.valueOf(42), state.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecode_zero_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, 0);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(Integer.valueOf(0), state.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecode_negative_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, -999);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(Integer.valueOf(-999), state.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecode_maxInt_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, Integer.MAX_VALUE);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(Integer.valueOf(Integer.MAX_VALUE), state.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecode_minInt_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, Integer.MIN_VALUE);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(Integer.valueOf(Integer.MIN_VALUE), state.decodeValue(decoder));
  }

  // ── boundary values ───────────────────────────────────────────────

  @Test
  public void setValue_atMinimum() {
    state.setValueTransactionlessly(0);
    assertEquals(Integer.valueOf(0), state.getValue());
  }

  @Test
  public void setValue_atMaximum() {
    state.setValueTransactionlessly(100);
    assertEquals(Integer.valueOf(100), state.getValue());
  }

  @Test
  public void setValue_oneAboveMinimum() {
    state.setValueTransactionlessly(1);
    assertEquals(Integer.valueOf(1), state.getValue());
  }

  @Test
  public void setValue_oneBelowMaximum() {
    state.setValueTransactionlessly(99);
    assertEquals(Integer.valueOf(99), state.getValue());
  }

  // ── AtomicChange edge cases ───────────────────────────────────────

  @Test
  public void atomicChange_valueOnly() {
    BoundedNumberState.AtomicChange<Integer> change =
        new BoundedNumberState.AtomicChange<Integer>().value(25);
    state.setAll(change);
    assertEquals(Integer.valueOf(25), state.getValue());
  }

  @Test
  public void atomicChange_minimumOnly() {
    BoundedNumberState.AtomicChange<Integer> change =
        new BoundedNumberState.AtomicChange<Integer>().minimum(10);
    state.setAll(change);
    assertEquals(Integer.valueOf(10), state.getMinimum());
  }

  @Test
  public void atomicChange_maximumOnly() {
    BoundedNumberState.AtomicChange<Integer> change =
        new BoundedNumberState.AtomicChange<Integer>().maximum(200);
    state.setAll(change);
    assertEquals(Integer.valueOf(200), state.getMaximum());
  }

  @Test
  public void atomicChange_extentSetting() {
    BoundedNumberState.AtomicChange<Integer> change =
        new BoundedNumberState.AtomicChange<Integer>().extent(5);
    state.setAll(change);
    assertEquals(5, state.getSwingModel().getBoundedRangeModel().getExtent());
  }

  @Test
  public void atomicChange_isAdjusting() {
    BoundedNumberState.AtomicChange<Integer> change =
        new BoundedNumberState.AtomicChange<Integer>()
            .value(75)
            .isAdjusting(true);
    state.setAll(change);
    assertEquals(Integer.valueOf(75), state.getValue());
  }

  // ── Spinner model sync ────────────────────────────────────────────

  @Test
  public void spinnerModel_minimumMatchesAfterSetMinimum() {
    state.setMinimum(5);
    assertEquals(5, ((Number) state.getSwingModel().getSpinnerModel().getMinimum()).intValue());
  }

  @Test
  public void spinnerModel_maximumMatchesAfterSetMaximum() {
    state.setMaximum(500);
    assertEquals(500, ((Number) state.getSwingModel().getSpinnerModel().getMaximum()).intValue());
  }

  // ── Listener count tracking ───────────────────────────────────────

  @Test
  public void noListenerFire_whenValueUnchanged() {
    AtomicInteger count = new AtomicInteger();
    state.addValueListener(new State.ValueListener<Integer>() {
      @Override
      public void changing(State<Integer> s, Integer prev, Integer next) {}

      @Override
      public void changed(State<Integer> s, Integer prev, Integer next) {
        count.incrementAndGet();
      }
    });
    state.setValueTransactionlessly(50); // same as initial
    assertEquals(0, count.get());
  }

  @Test
  public void listenerFires_forEachDistinctChange() {
    AtomicInteger count = new AtomicInteger();
    state.addValueListener(new State.ValueListener<Integer>() {
      @Override
      public void changing(State<Integer> s, Integer prev, Integer next) {}

      @Override
      public void changed(State<Integer> s, Integer prev, Integer next) {
        count.incrementAndGet();
      }
    });
    state.setValueTransactionlessly(0);
    state.setValueTransactionlessly(100);
    state.setValueTransactionlessly(50);
    assertEquals(3, count.get());
  }

  // ── Details with negative range ───────────────────────────────────

  @Test
  public void details_negativeMinimum() {
    BoundedIntegerState.Details d =
        new BoundedIntegerState.Details(TEST_GROUP, CroquetTestUtils.nextTestUUID())
            .minimum(-50)
            .maximum(50)
            .initialValue(0);
    BoundedIntegerStateTest.TestBoundedIntegerState s =
        new BoundedIntegerStateTest.TestBoundedIntegerState(d);
    CroquetTestUtils.removeSpinnerChangeListeners(s);
    assertEquals(Integer.valueOf(0), s.getValue());
    assertEquals(Integer.valueOf(-50), s.getMinimum());
    assertEquals(Integer.valueOf(50), s.getMaximum());
  }

  // ── appendRepresentation edge cases ───────────────────────────────

  @Test
  public void appendRepresentation_negative() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, -42);
    assertEquals("-42", sb.toString());
  }

  @Test
  public void appendRepresentation_zero() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, 0);
    assertEquals("0", sb.toString());
  }

  // ── multiple sequential setAll ────────────────────────────────────

  @Test
  public void multipleSetAll_lastWins() {
    BoundedNumberState.AtomicChange<Integer> c1 =
        new BoundedNumberState.AtomicChange<Integer>().value(10);
    BoundedNumberState.AtomicChange<Integer> c2 =
        new BoundedNumberState.AtomicChange<Integer>().value(90);
    state.setAll(c1);
    state.setAll(c2);
    assertEquals(Integer.valueOf(90), state.getValue());
  }
}
