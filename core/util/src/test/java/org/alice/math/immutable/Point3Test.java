package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Point3Test {

  static final double EPSILON = 1e-10;
  static final Point3 P = new Point3(1, 2, 3);
  static final Point3 Q = new Point3(4, 6, 8);

  @Test
  void minus_point_producesVector() {
    Vector3 v = Q.minus(P);
    assertEquals(3.0, v.x(), EPSILON);
    assertEquals(4.0, v.y(), EPSILON);
    assertEquals(5.0, v.z(), EPSILON);
  }

  @Test
  void plus_vector_producesPoint() {
    Point3 r = P.plus(new Vector3(10, 20, 30));
    assertEquals(11.0, r.x(), EPSILON);
    assertEquals(22.0, r.y(), EPSILON);
    assertEquals(33.0, r.z(), EPSILON);
  }

  @Test
  void minus_vector_producesPoint() {
    Point3 r = Q.minus(new Vector3(1, 1, 1));
    assertEquals(3.0, r.x(), EPSILON);
    assertEquals(5.0, r.y(), EPSILON);
    assertEquals(7.0, r.z(), EPSILON);
  }

  @Test
  void times_scalar() {
    Point3 r = P.times(3.0);
    assertEquals(3.0, r.x(), EPSILON);
    assertEquals(6.0, r.y(), EPSILON);
    assertEquals(9.0, r.z(), EPSILON);
  }

  @Test
  void interpolate_atZero() {
    Point3 r = P.interpolate(Q, 0.0);
    assertEquals(P.x(), r.x(), EPSILON);
    assertEquals(P.y(), r.y(), EPSILON);
    assertEquals(P.z(), r.z(), EPSILON);
  }

  @Test
  void interpolate_atOne() {
    Point3 r = P.interpolate(Q, 1.0);
    assertEquals(Q.x(), r.x(), EPSILON);
    assertEquals(Q.y(), r.y(), EPSILON);
    assertEquals(Q.z(), r.z(), EPSILON);
  }

  @Test
  void interpolate_atHalf() {
    Point3 r = P.interpolate(Q, 0.5);
    assertEquals(2.5, r.x(), EPSILON);
    assertEquals(4.0, r.y(), EPSILON);
    assertEquals(5.5, r.z(), EPSILON);
  }

  @Test
  void distanceSquaredFrom() {
    double d2 = P.distanceSquaredFrom(Q);
    assertEquals(50.0, d2, EPSILON); // 9 + 16 + 25
  }

  @Test
  void distanceFrom() {
    double d = P.distanceFrom(Q);
    assertEquals(Math.sqrt(50.0), d, EPSILON);
  }

  @Test
  void asVector() {
    Vector3 v = P.asVector();
    assertEquals(1.0, v.x(), EPSILON);
    assertEquals(2.0, v.y(), EPSILON);
    assertEquals(3.0, v.z(), EPSILON);
  }

  @Test
  void withX() {
    Point3 r = P.withX(99);
    assertEquals(99.0, r.x(), EPSILON);
    assertEquals(2.0, r.y(), EPSILON);
    assertEquals(3.0, r.z(), EPSILON);
  }

  @Test
  void withY() {
    Point3 r = P.withY(99);
    assertEquals(1.0, r.x(), EPSILON);
    assertEquals(99.0, r.y(), EPSILON);
    assertEquals(3.0, r.z(), EPSILON);
  }

  @Test
  void withZ() {
    Point3 r = P.withZ(99);
    assertEquals(1.0, r.x(), EPSILON);
    assertEquals(2.0, r.y(), EPSILON);
    assertEquals(99.0, r.z(), EPSILON);
  }

  @Test
  void origin_isZero() {
    assertEquals(0.0, Point3.ORIGIN.x(), EPSILON);
    assertEquals(0.0, Point3.ORIGIN.y(), EPSILON);
    assertEquals(0.0, Point3.ORIGIN.z(), EPSILON);
  }

  @Test
  void nan_isNaN() {
    assertTrue(Point3.NaN.isNaN());
    assertFalse(P.isNaN());
  }

  @Test
  void isZero_origin() {
    assertTrue(Point3.ORIGIN.isZero());
    assertFalse(P.isZero());
  }
}
