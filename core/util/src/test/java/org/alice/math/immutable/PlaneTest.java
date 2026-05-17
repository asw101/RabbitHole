package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaneTest {

  static final double EPSILON = 1e-10;
  static final Plane XZ = Plane.XZ_PLANE;

  @Test
  void xzPlane_hasCorrectCoefficients() {
    assertEquals(0.0, XZ.a(), EPSILON);
    assertEquals(1.0, XZ.b(), EPSILON);
    assertEquals(0.0, XZ.c(), EPSILON);
    assertEquals(0.0, XZ.d(), EPSILON);
  }

  @Test
  void isNaN_false() {
    assertFalse(XZ.isNaN());
  }

  @Test
  void isNaN_true() {
    assertTrue(Plane.NaN.isNaN());
  }

  @Test
  void getNormal() {
    Vector3 n = XZ.getNormal();
    assertEquals(0.0, n.x(), EPSILON);
    assertEquals(1.0, n.y(), EPSILON);
    assertEquals(0.0, n.z(), EPSILON);
  }

  @Test
  void getEquation_fillsArray() {
    double[] eq = new double[4];
    XZ.getEquation(eq);
    assertEquals(0.0, eq[0], EPSILON);
    assertEquals(1.0, eq[1], EPSILON);
    assertEquals(0.0, eq[2], EPSILON);
    assertEquals(0.0, eq[3], EPSILON);
  }

  @Test
  void createInstance_pointAndNormal() {
    Plane p = Plane.createInstance(Point3.ORIGIN, Vector3.POSITIVE_Y_AXIS);
    assertEquals(0.0, p.a(), EPSILON);
    assertEquals(1.0, p.b(), EPSILON);
    assertEquals(0.0, p.c(), EPSILON);
    assertEquals(0.0, p.d(), EPSILON);
  }

  @Test
  void createInstance_offsetPlane() {
    Plane p = Plane.createInstance(new Point3(0, 5, 0), Vector3.POSITIVE_Y_AXIS);
    assertEquals(0.0, p.a(), EPSILON);
    assertEquals(1.0, p.b(), EPSILON);
    assertEquals(0.0, p.c(), EPSILON);
    assertEquals(-5.0, p.d(), EPSILON);
  }

  @Test
  void createInstance_fromMatrix() {
    AffineMatrix4x4 m = new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, Point3.ORIGIN);
    Plane p = Plane.createInstance(m);
    assertFalse(p.isNaN());
  }

  @Test
  void createInstance_unnormalizedNormal() {
    // Normal with magnitude != 1 should still create a valid plane
    Plane p = Plane.createInstance(Point3.ORIGIN, new Vector3(0, 2, 0));
    assertFalse(p.isNaN());
    assertEquals(1.0, p.getNormal().magnitude(), 0.02);
  }

  @Test
  void evaluate_pointOnPlane() {
    double result = XZ.evaluate(new Point3(5, 0, 3));
    assertEquals(0.0, result, EPSILON);
  }

  @Test
  void evaluate_pointAbovePlane() {
    double result = XZ.evaluate(new Point3(0, 10, 0));
    assertEquals(10.0, result, EPSILON);
  }

  @Test
  void distanceTo_pointOnPlane() {
    double d = XZ.distanceTo(new Point3(5, 0, 3));
    assertEquals(0.0, d, EPSILON);
  }

  @Test
  void distanceTo_pointAbovePlane() {
    double d = XZ.distanceTo(new Point3(0, 7, 0));
    assertEquals(7.0, d, EPSILON);
  }

  @Test
  void projected_pointAbove() {
    Point3 p = XZ.projected(new Point3(3, 10, 5));
    assertEquals(3.0, p.x(), EPSILON);
    assertEquals(0.0, p.y(), EPSILON);
    assertEquals(5.0, p.z(), EPSILON);
  }

  @Test
  void projected_pointOnPlane() {
    Point3 p = XZ.projected(new Point3(3, 0, 5));
    assertEquals(3.0, p.x(), EPSILON);
    assertEquals(0.0, p.y(), EPSILON);
    assertEquals(5.0, p.z(), EPSILON);
  }

  @Test
  void intersect_perpendicular() {
    Ray ray = new Ray(new Point3(0, 10, 0), new Vector3(0, -1, 0));
    double t = XZ.intersect(ray);
    assertEquals(10.0, t, EPSILON);
  }

  @Test
  void intersect_parallel_returnsNaN() {
    Ray ray = new Ray(new Point3(0, 10, 0), new Vector3(1, 0, 0));
    double t = XZ.intersect(ray);
    assertTrue(Double.isNaN(t));
  }

  @Test
  void getIntersection_valid() {
    Ray ray = new Ray(new Point3(0, 10, 0), new Vector3(0, -1, 0));
    Point3 p = XZ.getIntersection(ray);
    assertNotNull(p);
    assertEquals(0.0, p.x(), EPSILON);
    assertEquals(0.0, p.y(), EPSILON);
    assertEquals(0.0, p.z(), EPSILON);
  }

  @Test
  void getIntersection_parallel_returnsNull() {
    Ray ray = new Ray(new Point3(0, 10, 0), new Vector3(1, 0, 0));
    Point3 p = XZ.getIntersection(ray);
    assertNull(p);
  }

  @Test
  void getIntersection_behind_returnsNull() {
    Ray ray = new Ray(new Point3(0, 10, 0), new Vector3(0, 1, 0));
    Point3 p = XZ.getIntersection(ray);
    assertNull(p);
  }
}
