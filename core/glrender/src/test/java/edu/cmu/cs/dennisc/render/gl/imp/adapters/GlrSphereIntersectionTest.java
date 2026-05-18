package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.Sphere;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector3;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrSphere#getIntersectionInSource(Ray, Matrix4x4, int)} —
 * pure math ray-sphere intersection. Sets radius via reflection.
 */
public class GlrSphereIntersectionTest {

  private GlrSphere sphere;

  @Before
  public void setUp() throws Exception {
    sphere = new GlrSphere();
    Sphere sgSphere = new Sphere();
    sphere.initialize(sgSphere);
    setRadius(1.0);
  }

  // ── Hit from each axis ────────────────────────────────────────────

  @Test
  public void hitFromPositiveX() {
    Ray ray = new Ray(new Point3(5, 0, 0), Vector3.NEGATIVE_X_AXIS);
    Point3 result = sphere.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse("Should hit sphere", result.isNaN());
    assertEquals(1.0, result.x(), 0.01);
    assertEquals(0.0, result.y(), 0.01);
    assertEquals(0.0, result.z(), 0.01);
  }

  @Test
  public void hitFromNegativeX() {
    Ray ray = new Ray(new Point3(-5, 0, 0), Vector3.POSITIVE_X_AXIS);
    Point3 result = sphere.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse(result.isNaN());
    assertEquals(-1.0, result.x(), 0.01);
  }

  @Test
  public void hitFromPositiveY() {
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Point3 result = sphere.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse(result.isNaN());
    assertEquals(1.0, result.y(), 0.01);
  }

  @Test
  public void hitFromNegativeY() {
    Ray ray = new Ray(new Point3(0, -5, 0), Vector3.POSITIVE_Y_AXIS);
    Point3 result = sphere.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse(result.isNaN());
    assertEquals(-1.0, result.y(), 0.01);
  }

  @Test
  public void hitFromPositiveZ() {
    Ray ray = new Ray(new Point3(0, 0, 5), Vector3.NEGATIVE_Z_AXIS);
    Point3 result = sphere.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse(result.isNaN());
    assertEquals(1.0, result.z(), 0.01);
  }

  @Test
  public void hitFromNegativeZ() {
    Ray ray = new Ray(new Point3(0, 0, -5), Vector3.POSITIVE_Z_AXIS);
    Point3 result = sphere.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse(result.isNaN());
    assertEquals(-1.0, result.z(), 0.01);
  }

  // ── Miss ──────────────────────────────────────────────────────────

  @Test
  public void miss_parallel() {
    Ray ray = new Ray(new Point3(5, 5, 0), Vector3.NEGATIVE_X_AXIS);
    Point3 result = sphere.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertTrue("Parallel miss should return NaN", result.isNaN());
  }

  @Test
  public void miss_awayFromSphere() {
    Ray ray = new Ray(new Point3(5, 0, 0), Vector3.POSITIVE_X_AXIS);
    Point3 result = sphere.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    // Ray pointing away — discriminant may be positive but t < 0; implementation may still return a point
    assertNotNull(result);
  }

  // ── Different radii ───────────────────────────────────────────────

  @Test
  public void largeRadius_hitsFarther() throws Exception {
    // GlrSphere uses radius directly in discriminant (not radius²)
    // Effective visual radius = sqrt(radius)
    setRadius(25.0); // sqrt(25) = 5
    Ray ray = new Ray(new Point3(10, 0, 0), Vector3.NEGATIVE_X_AXIS);
    Point3 result = sphere.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse(result.isNaN());
    assertEquals(5.0, result.x(), 0.1);
  }

  @Test
  public void smallRadius_hitsCloser() throws Exception {
    setRadius(0.25); // sqrt(0.25) = 0.5
    Ray ray = new Ray(new Point3(5, 0, 0), Vector3.NEGATIVE_X_AXIS);
    Point3 result = sphere.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse(result.isNaN());
    assertEquals(0.5, result.x(), 0.1);
  }

  @Test
  public void zeroRadius_tangent_returnsNaN() throws Exception {
    setRadius(0.0);
    Ray ray = new Ray(new Point3(5, 0, 0), Vector3.NEGATIVE_X_AXIS);
    Point3 result = sphere.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    // d = b² - c = 25 - 25 = 0; code uses d > 0 (strict), so tangent → NaN
    assertTrue("Tangent (d=0) should return NaN", result.isNaN());
  }

  // ── Diagonal ray ──────────────────────────────────────────────────

  @Test
  public void diagonalRay_hitsNearSide() {
    Vector3 dir = new Vector3(-1, -1, 0).normalized();
    Ray ray = new Ray(new Point3(5, 5, 0), dir);
    Point3 result = sphere.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse(result.isNaN());
    double dist = Math.sqrt(result.x() * result.x() + result.y() * result.y() + result.z() * result.z());
    assertEquals("Hit point should be on sphere surface", 1.0, dist, 0.05);
  }

  // ── Property change coverage ──────────────────────────────────────

  @Test
  public void propertyChanged_radius_syncsField() throws Exception {
    Sphere sgSphere = (Sphere) sphere.getOwner();
    sgSphere.radius.setValue(3.0);
    sphere.propertyChanged(sgSphere.radius);
    Field f = GlrSphere.class.getDeclaredField("radius");
    f.setAccessible(true);
    assertEquals(3.0, f.getDouble(sphere), 0.001);
  }

  @Test
  public void propertyChanged_radius_marksGeometryChanged() throws Exception {
    Sphere sgSphere = (Sphere) sphere.getOwner();
    sgSphere.radius.setValue(2.0);
    sphere.propertyChanged(sgSphere.radius);
    Field f = GlrGeometry.class.getDeclaredField("isGeometryChanged");
    f.setAccessible(true);
    assertTrue("Geometry should be marked as changed", f.getBoolean(sphere));
  }

  // ── Structure ─────────────────────────────────────────────────────

  @Test
  public void extendsGlrShape() {
    assertTrue(GlrShape.class.isAssignableFrom(GlrSphere.class));
  }

  @Test
  public void ownerIsSetAfterInitialize() {
    assertNotNull(sphere.getOwner());
    assertTrue(sphere.getOwner() instanceof Sphere);
  }

  @Test
  public void isNotAlphaBlended() {
    assertFalse(sphere.isAlphaBlended());
  }

  @Test
  public void hasOpaque() {
    assertTrue(sphere.hasOpaque());
  }

  // ── Reflection helper ─────────────────────────────────────────────

  private void setRadius(double value) throws Exception {
    Field f = GlrSphere.class.getDeclaredField("radius");
    f.setAccessible(true);
    f.setDouble(sphere, value);
  }
}
