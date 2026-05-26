package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector2fTest {

  private static final float EPSILON = 1e-6f;
  private static final Vector2f A = new Vector2f(1, 2);
  private static final Vector2f B = new Vector2f(4, 6);

  @Test
  void arithmetic_methods_transform_components() {
    assertEquals(new Vector2f(5, 8), A.plus(B));
    assertEquals(new Vector2f(-3, -4), A.minus(B));
    assertEquals(new Vector2f(3, 6), A.times(3.0f));
    assertEquals(new Vector2f(2, 3), B.dividedBy(2.0f));
    assertEquals(new Vector2f(-1, -2), A.negate());
  }

  @Test
  void magnitude_methods_match_pythagorean_length() {
    assertEquals((float) Math.sqrt(52.0), B.magnitude(), EPSILON);
    assertEquals(52.0f, B.magnitudeSquared(), EPSILON);
    assertEquals(1.0f, new Vector2f(1, 0).magnitude(), EPSILON);
  }

  @Test
  void tuple_defaults_report_zero_and_nan() {
    assertTrue(Vector2f.ZERO.isZero());
    assertFalse(A.isZero());
    assertTrue(Vector2f.NaN.isNaN());
    assertFalse(B.isNaN());
  }
}
