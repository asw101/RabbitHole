package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Point2Test {

  private static final double EPSILON = 1e-10;
  private static final Point2 P = new Point2(1, 2);
  private static final Point2 Q = new Point2(4, 6);

  @Test
  void minus_point_produces_vector() {
    Vector2 vector = Q.minus(P);

    assertEquals(3.0, vector.x(), EPSILON);
    assertEquals(4.0, vector.y(), EPSILON);
  }

  @Test
  void plus_and_minus_vector_shift_point() {
    Point2 plus = P.plus(new Vector2(10, 20));
    Point2 minus = Q.minus(new Vector2(1, 1));

    assertEquals(11.0, plus.x(), EPSILON);
    assertEquals(22.0, plus.y(), EPSILON);
    assertEquals(3.0, minus.x(), EPSILON);
    assertEquals(5.0, minus.y(), EPSILON);
  }

  @Test
  void times_and_distance_methods_follow_tuple_defaults() {
    Point2 scaled = P.times(3.0);

    assertEquals(3.0, scaled.x(), EPSILON);
    assertEquals(6.0, scaled.y(), EPSILON);
    assertEquals(25.0, P.distanceSquaredFrom(Q), EPSILON);
    assertEquals(5.0, P.distanceFrom(Q), EPSILON);
  }

  @Test
  void origin_and_nan_constants_reflect_tuple_state() {
    assertTrue(Point2.ORIGIN.isZero());
    assertFalse(P.isZero());
    assertTrue(Point2.NaN.isNaN());
    assertFalse(P.isNaN());
  }
}
