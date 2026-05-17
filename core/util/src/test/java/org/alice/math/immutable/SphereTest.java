package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SphereTest {

  static final double EPSILON = 1e-9;
  static final Sphere UNIT_SPHERE = new Sphere(Point3.ORIGIN, 1.0);

  // --- Record accessors ---

  @Test
  void center_returnsConstructedCenter() {
    Point3 c = new Point3(1, 2, 3);
    Sphere s = new Sphere(c, 5.0);
    assertEquals(c, s.center());
  }

  @Test
  void radius_returnsConstructedRadius() {
    Sphere s = new Sphere(Point3.ORIGIN, 42.0);
    assertEquals(42.0, s.radius());
  }

  // --- intersect: ray hits sphere ---

  @Test
  void intersect_rayAlongXAxisHitsUnitSphere() {
    Ray ray = new Ray(new Point3(-5, 0, 0), new Vector3(1, 0, 0));
    double t = UNIT_SPHERE.intersect(ray);
    assertFalse(Double.isNaN(t));
    assertEquals(4.0, t, EPSILON);
  }

  @Test
  void intersect_rayAlongYAxisHitsUnitSphere() {
    Ray ray = new Ray(new Point3(0, -5, 0), new Vector3(0, 1, 0));
    double t = UNIT_SPHERE.intersect(ray);
    assertFalse(Double.isNaN(t));
    assertEquals(4.0, t, EPSILON);
  }

  @Test
  void intersect_rayAlongZAxisHitsUnitSphere() {
    Ray ray = new Ray(new Point3(0, 0, -5), new Vector3(0, 0, 1));
    double t = UNIT_SPHERE.intersect(ray);
    assertFalse(Double.isNaN(t));
    assertEquals(4.0, t, EPSILON);
  }

  // --- intersect: ray misses sphere ---

  @Test
  void intersect_parallelRayMissesSphere() {
    Ray ray = new Ray(new Point3(0, 5, 0), new Vector3(1, 0, 0));
    double t = UNIT_SPHERE.intersect(ray);
    assertTrue(Double.isNaN(t));
  }

  @Test
  void intersect_rayPointingAwayMisses() {
    Ray ray = new Ray(new Point3(-5, 0, 0), new Vector3(-1, 0, 0));
    double t = UNIT_SPHERE.intersect(ray);
    assertTrue(Double.isNaN(t));
  }

  // --- intersect: ray origin inside sphere ---

  @Test
  void intersect_rayFromInsideSphere() {
    Ray ray = new Ray(Point3.ORIGIN, new Vector3(1, 0, 0));
    double t = UNIT_SPHERE.intersect(ray);
    assertFalse(Double.isNaN(t));
    assertEquals(1.0, t, EPSILON);
  }

  // --- intersect: tangent ray ---

  @Test
  void intersect_tangentRayTouchesSphere() {
    Ray ray = new Ray(new Point3(-5, 1, 0), new Vector3(1, 0, 0));
    double t = UNIT_SPHERE.intersect(ray);
    // Tangent: discriminant ≈ 0, t should be valid
    assertFalse(Double.isNaN(t));
  }

  // --- intersect with offset center ---

  @Test
  void intersect_offsetSphere() {
    Point3 center = new Point3(10, 0, 0);
    Sphere s = new Sphere(center, 2.0);
    Ray ray = new Ray(new Point3(0, 0, 0), new Vector3(1, 0, 0));
    double t = s.intersect(ray);
    assertFalse(Double.isNaN(t));
    assertEquals(8.0, t, EPSILON);
  }

  // --- intersect with larger radius ---

  @Test
  void intersect_largerRadius() {
    Sphere s = new Sphere(Point3.ORIGIN, 5.0);
    Ray ray = new Ray(new Point3(-10, 0, 0), new Vector3(1, 0, 0));
    double t = s.intersect(ray);
    assertFalse(Double.isNaN(t));
    assertEquals(5.0, t, EPSILON);
  }

  // --- record equality ---

  @Test
  void equals_sameCenterAndRadius() {
    Sphere s1 = new Sphere(Point3.ORIGIN, 1.0);
    Sphere s2 = new Sphere(Point3.ORIGIN, 1.0);
    assertEquals(s1, s2);
  }

  @Test
  void equals_differentRadiusNotEqual() {
    Sphere s1 = new Sphere(Point3.ORIGIN, 1.0);
    Sphere s2 = new Sphere(Point3.ORIGIN, 2.0);
    assertNotEquals(s1, s2);
  }

  @Test
  void equals_differentCenterNotEqual() {
    Sphere s1 = new Sphere(Point3.ORIGIN, 1.0);
    Sphere s2 = new Sphere(new Point3(1, 0, 0), 1.0);
    assertNotEquals(s1, s2);
  }

  // --- intersect: behind sphere both solutions negative ---

  @Test
  void intersect_rayBehindSphereReturnsNaN() {
    Ray ray = new Ray(new Point3(5, 0, 0), new Vector3(1, 0, 0));
    double t = UNIT_SPHERE.intersect(ray);
    assertTrue(Double.isNaN(t));
  }

  // --- record toString includes fields ---

  @Test
  void toString_containsCenterAndRadius() {
    String s = UNIT_SPHERE.toString();
    assertNotNull(s);
    assertTrue(s.contains("center"));
    assertTrue(s.contains("radius"));
  }
}
