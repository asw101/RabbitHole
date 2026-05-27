package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TupleDefaultMethodsTest {
  @Test
  void tuple3DefaultMethodsCoverZeroNaNAndFloatListRepresentations() {
    Tuple3 zero = new Tuple3() {
      @Override
      public double x() {
        return 0.0;
      }

      @Override
      public double y() {
        return 0.0;
      }

      @Override
      public double z() {
        return 0.0;
      }
    };
    Tuple3 close = new Tuple3() {
      @Override
      public double x() {
        return 0.000001;
      }

      @Override
      public double y() {
        return -0.000001;
      }

      @Override
      public double z() {
        return 0.000002;
      }
    };
    Tuple3 nan = new Tuple3() {
      @Override
      public double x() {
        return Double.NaN;
      }

      @Override
      public double y() {
        return 1.0;
      }

      @Override
      public double z() {
        return 2.0;
      }
    };

    assertTrue(zero.isZero());
    assertTrue(close.isWithinEpsilonOfZero(0.01));
    assertTrue(close.isWithinReasonableEpsilonOfZero());
    assertTrue(nan.isNaN());
    assertEquals(List.of(0.000001f, -0.000001f, 0.000002f), close.asFloatList());
  }

  @Test
  void tuple2fDefaultMethodsComputeDistancesAndEpsilonChecks() {
    Tuple2f origin = new Point2f(0.0f, 0.0f);
    Tuple2f close = new Point2f(0.000001f, -0.000001f);
    Tuple2f far = new Point2f(3.0f, 4.0f);

    assertTrue(origin.isZero());
    assertTrue(close.isWithinEpsilonOf(origin, 0.01f));
    assertTrue(close.isWithinReasonableEpsilonOf(origin));
    assertFalse(far.isWithinEpsilonOf(origin, 0.01f));
    assertEquals(25.0f, origin.distanceSquaredFrom(far), 1.0e-6f);
    assertEquals(5.0f, origin.distanceFrom(far), 1.0e-6f);
  }

  @Test
  void tuple3fDefaultMethodsComputeDistanceAndListConversions() {
    Tuple3f origin = Vector3f.ZERO;
    Tuple3f close = new Vector3f(0.000001f, -0.000001f, 0.0000015f);
    Tuple3f far = new Vector3f(2.0f, 3.0f, 6.0f);

    assertTrue(origin.isZero());
    assertTrue(close.isWithinEpsilonOf(origin, 0.01f));
    assertTrue(close.isWithinReasonableEpsilonOf(origin));
    assertFalse(far.isWithinEpsilonOf(origin, 0.01f));
    assertEquals(49.0, origin.distanceSquaredFrom(far), 1.0e-10);
    assertEquals(7.0, origin.distanceFrom(far), 1.0e-10);
    assertEquals(List.of(2.0f, 3.0f, 6.0f), far.asFloatList());
  }
}
