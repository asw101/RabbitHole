package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Point2fTest {

  private static final float EPSILON = 1e-6f;
  private static final Point2f P = new Point2f(1, 2);
  private static final Point2f Q = new Point2f(4, 6);

  @Test
  void minus_point_produces_vector() {
    Vector2f vector = Q.minus(P);

    assertEquals(3.0f, vector.x(), EPSILON);
    assertEquals(4.0f, vector.y(), EPSILON);
  }

  @Test
  void plus_and_minus_vector_shift_point() {
    Point2f plus = P.plus(new Vector2f(10, 20));
    Point2f minus = Q.minus(new Vector2f(1, 1));

    assertEquals(11.0f, plus.x(), EPSILON);
    assertEquals(22.0f, plus.y(), EPSILON);
    assertEquals(3.0f, minus.x(), EPSILON);
    assertEquals(5.0f, minus.y(), EPSILON);
  }

  @Test
  void times_and_distance_methods_follow_tuple_defaults() {
    Point2f scaled = P.times(3.0f);

    assertEquals(3.0f, scaled.x(), EPSILON);
    assertEquals(6.0f, scaled.y(), EPSILON);
    assertEquals(25.0f, P.distanceSquaredFrom(Q), EPSILON);
    assertEquals(5.0f, P.distanceFrom(Q), EPSILON);
  }

  @Test
  void origin_and_nan_constants_reflect_tuple_state() {
    assertTrue(Point2f.ORIGIN.isZero());
    assertFalse(P.isZero());
    assertTrue(Point2f.NaN.isNaN());
    assertFalse(P.isNaN());
  }
}
