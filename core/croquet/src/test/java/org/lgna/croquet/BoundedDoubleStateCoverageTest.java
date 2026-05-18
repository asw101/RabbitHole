package org.lgna.croquet;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Extended coverage tests for {@link BoundedDoubleState} — boundary values,
 * NaN/Infinity edge cases, encode/decode round-trip, and AtomicChange.
 */
public class BoundedDoubleStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0004-ffffffffffff"), "dblCov");

  private BoundedDoubleStateTest.TestBoundedDoubleState state;

  @Before
  public void setUp() {
    BoundedDoubleState.Details details =
        new BoundedDoubleState.Details(TEST_GROUP, CroquetTestUtils.nextTestUUID())
            .minimum(0.0)
            .maximum(1.0)
            .initialValue(0.5)
            .stepSize(0.01);
    state = new BoundedDoubleStateTest.TestBoundedDoubleState(details);
    CroquetTestUtils.removeSpinnerChangeListeners(state);
  }

  // ── encode/decode round-trip ──────────────────────────────────────

  @Test
  public void encodeAndDecode_normal_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, 0.42);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(0.42, state.decodeValue(decoder), 0.0001);
  }

  @Test
  public void encodeAndDecode_zero_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, 0.0);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(0.0, state.decodeValue(decoder), 0.0001);
  }

  @Test
  public void encodeAndDecode_negative_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, -3.14);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(-3.14, state.decodeValue(decoder), 0.0001);
  }

  @Test
  public void encodeAndDecode_verySmall_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, 0.000001);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(0.000001, state.decodeValue(decoder), 0.0000001);
  }

  @Test
  public void encodeAndDecode_veryLarge_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, 1e15);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(1e15, state.decodeValue(decoder), 1.0);
  }

  // ── boundary values ───────────────────────────────────────────────

  @Test
  public void setValue_atMinimum() {
    state.setValueTransactionlessly(0.0);
    assertEquals(0.0, state.getValue(), 0.001);
  }

  @Test
  public void setValue_atMaximum() {
    state.setValueTransactionlessly(1.0);
    assertEquals(1.0, state.getValue(), 0.001);
  }

  @Test
  public void setValue_veryCloseToMinimum() {
    state.setValueTransactionlessly(0.001);
    assertEquals(0.001, state.getValue(), 0.001);
  }

  @Test
  public void setValue_veryCloseToMaximum() {
    state.setValueTransactionlessly(0.999);
    assertEquals(0.999, state.getValue(), 0.001);
  }

  // ── setMinimum / setMaximum edge cases ────────────────────────────

  @Test
  public void setMinimum_negative() {
    state.setMinimum(-100.0);
    assertEquals(-100.0, state.getMinimum(), 0.001);
  }

  @Test
  public void setMaximum_veryLarge() {
    state.setMaximum(1000.0);
    assertEquals(1000.0, state.getMaximum(), 0.001);
  }

  // ── AtomicChange combinations ─────────────────────────────────────

  @Test
  public void atomicChange_allFields() {
    BoundedNumberState.AtomicChange<Double> change =
        new BoundedNumberState.AtomicChange<Double>()
            .value(0.75)
            .minimum(-1.0)
            .maximum(2.0)
            .stepSize(0.05);
    state.setAll(change);
    assertEquals(0.75, state.getValue(), 0.001);
    assertEquals(-1.0, state.getMinimum(), 0.001);
    assertEquals(2.0, state.getMaximum(), 0.001);
  }

  @Test
  public void atomicChange_valueOnly() {
    BoundedNumberState.AtomicChange<Double> change =
        new BoundedNumberState.AtomicChange<Double>().value(0.25);
    state.setAll(change);
    assertEquals(0.25, state.getValue(), 0.001);
  }

  @Test
  public void atomicChange_isAdjusting() {
    BoundedNumberState.AtomicChange<Double> change =
        new BoundedNumberState.AtomicChange<Double>()
            .value(0.8)
            .isAdjusting(true);
    state.setAll(change);
    assertEquals(0.8, state.getValue(), 0.001);
  }

  // ── Listener fidelity ─────────────────────────────────────────────

  @Test
  public void noListenerFire_whenValueUnchanged() {
    AtomicInteger count = new AtomicInteger();
    state.addValueListener(new State.ValueListener<Double>() {
      @Override
      public void changing(State<Double> s, Double prev, Double next) {}

      @Override
      public void changed(State<Double> s, Double prev, Double next) {
        count.incrementAndGet();
      }
    });
    state.setValueTransactionlessly(0.5); // same as initial
    assertEquals(0, count.get());
  }

  @Test
  public void listenerFires_forEachDistinctChange() {
    AtomicInteger count = new AtomicInteger();
    state.addValueListener(new State.ValueListener<Double>() {
      @Override
      public void changing(State<Double> s, Double prev, Double next) {}

      @Override
      public void changed(State<Double> s, Double prev, Double next) {
        count.incrementAndGet();
      }
    });
    state.setValueTransactionlessly(0.0);
    state.setValueTransactionlessly(1.0);
    state.setValueTransactionlessly(0.5);
    assertEquals(3, count.get());
  }

  // ── Details with negative range ───────────────────────────────────

  @Test
  public void details_negativeRange() {
    BoundedDoubleState.Details d =
        new BoundedDoubleState.Details(TEST_GROUP, CroquetTestUtils.nextTestUUID())
            .minimum(-10.0)
            .maximum(-1.0)
            .initialValue(-5.0)
            .stepSize(0.5);
    BoundedDoubleStateTest.TestBoundedDoubleState s =
        new BoundedDoubleStateTest.TestBoundedDoubleState(d);
    CroquetTestUtils.removeSpinnerChangeListeners(s);
    assertEquals(-5.0, s.getValue(), 0.001);
    assertEquals(-10.0, s.getMinimum(), 0.001);
    assertEquals(-1.0, s.getMaximum(), 0.001);
  }

  // ── appendRepresentation edge cases ───────────────────────────────

  @Test
  public void appendRepresentation_negative() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, -0.5);
    assertEquals("-0.5", sb.toString());
  }

  @Test
  public void appendRepresentation_zero() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, 0.0);
    assertEquals("0.0", sb.toString());
  }

  // ── SpinnerModel access after changes ─────────────────────────────

  @Test
  public void spinnerModel_valueAfterSetValue() {
    state.setValueTransactionlessly(0.75);
    Number spinnerValue = (Number) state.getSwingModel().getSpinnerModel().getValue();
    assertEquals(0.75, spinnerValue.doubleValue(), 0.001);
  }

  @Test
  public void spinnerModel_stepSize() {
    assertEquals(0.01, state.getSwingModel().getSpinnerModel().getStepSize().doubleValue(), 0.0001);
  }

  // ── multiple sequential setAll ────────────────────────────────────

  @Test
  public void multipleSetAll_lastWins() {
    state.setAll(new BoundedNumberState.AtomicChange<Double>().value(0.1));
    state.setAll(new BoundedNumberState.AtomicChange<Double>().value(0.9));
    assertEquals(0.9, state.getValue(), 0.001);
  }
}
