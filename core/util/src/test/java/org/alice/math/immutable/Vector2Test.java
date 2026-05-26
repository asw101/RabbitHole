package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector2Test {

  private static final double EPSILON = 1e-10;
  private static final Vector2 A = new Vector2(1, 2);
  private static final Vector2 B = new Vector2(4, 6);

  @Test
  void arithmetic_methods_transform_components() {
    assertEquals(new Vector2(5, 8), A.plus(B));
    assertEquals(new Vector2(-3, -4), A.minus(B));
    assertEquals(new Vector2(3, 6), A.times(3.0));
    assertEquals(new Vector2(2, 3), B.dividedBy(2.0));
    assertEquals(new Vector2(-1, -2), A.negate());
  }

  @Test
  void magnitude_methods_match_pythagorean_length() {
    assertEquals(Math.sqrt(52.0), B.magnitude(), EPSILON);
    assertEquals(52.0, B.magnitudeSquared(), EPSILON);
    assertEquals(1.0, new Vector2(1, 0).magnitude(), EPSILON);
  }

  @Test
  void tuple_defaults_report_zero_and_nan() {
    assertTrue(Vector2.ZERO.isZero());
    assertFalse(A.isZero());
    assertTrue(Vector2.NaN.isNaN());
    assertFalse(B.isNaN());
  }
}
