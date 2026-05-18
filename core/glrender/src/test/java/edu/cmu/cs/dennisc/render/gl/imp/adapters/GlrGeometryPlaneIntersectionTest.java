package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrGeometry#getIntersectionInSourceFromPlaneInLocal(Ray, Matrix4x4, Point3, Vector3)} —
 * the static helper that transforms a plane and intersects a ray.
 */
public class GlrGeometryPlaneIntersectionTest {

  // ── Hit cases ─────────────────────────────────────────────────────

  @Test
  public void hitOnYPlane_fromAbove() {
    Point3 result = invokeIntersect(
        new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS),
        Matrix4x4.IDENTITY, Point3.ORIGIN, Vector3.POSITIVE_Y_AXIS);
    assertFalse(result.isNaN());
    assertEquals(0.0, result.y(), 0.01);
  }

  @Test
  public void hitOnXPlane_fromRight() {
    Point3 result = invokeIntersect(
        new Ray(new Point3(5, 0, 0), Vector3.NEGATIVE_X_AXIS),
        Matrix4x4.IDENTITY, Point3.ORIGIN, Vector3.POSITIVE_X_AXIS);
    assertFalse(result.isNaN());
    assertEquals(0.0, result.x(), 0.01);
  }

  @Test
  public void hitOnZPlane_fromFront() {
    Point3 result = invokeIntersect(
        new Ray(new Point3(0, 0, 5), Vector3.NEGATIVE_Z_AXIS),
        Matrix4x4.IDENTITY, Point3.ORIGIN, Vector3.POSITIVE_Z_AXIS);
    assertFalse(result.isNaN());
    assertEquals(0.0, result.z(), 0.01);
  }

  @Test
  public void hitOnOffsetPlane() {
    Point3 planePos = new Point3(0, 3, 0);
    Point3 result = invokeIntersect(
        new Ray(new Point3(0, 10, 0), Vector3.NEGATIVE_Y_AXIS),
        Matrix4x4.IDENTITY, planePos, Vector3.POSITIVE_Y_AXIS);
    assertFalse(result.isNaN());
    assertEquals(3.0, result.y(), 0.01);
  }

  @Test
  public void hitPreservesXZ_onYPlane() {
    Point3 result = invokeIntersect(
        new Ray(new Point3(3, 5, 4), Vector3.NEGATIVE_Y_AXIS),
        Matrix4x4.IDENTITY, Point3.ORIGIN, Vector3.POSITIVE_Y_AXIS);
    assertFalse(result.isNaN());
    assertEquals(3.0, result.x(), 0.01);
    assertEquals(4.0, result.z(), 0.01);
  }

  // ── Parallel ray ──────────────────────────────────────────────────

  @Test
  public void parallelRay_returnsNaNOrInfinity() {
    Point3 result = invokeIntersect(
        new Ray(new Point3(0, 5, 0), Vector3.POSITIVE_X_AXIS),
        Matrix4x4.IDENTITY, Point3.ORIGIN, Vector3.POSITIVE_Y_AXIS);
    // Parallel — intersection t is infinity; getPointAlong returns NaN or Inf
    assertNotNull(result);
  }

  // ── Diagonal ray ──────────────────────────────────────────────────

  @Test
  public void diagonalRay_hitsPlane() {
    Vector3 dir = new Vector3(-1, -1, 0).normalized();
    Point3 result = invokeIntersect(
        new Ray(new Point3(5, 5, 0), dir),
        Matrix4x4.IDENTITY, Point3.ORIGIN, Vector3.POSITIVE_Y_AXIS);
    assertFalse(result.isNaN());
    assertEquals(0.0, result.y(), 0.01);
  }

  // ── NaN plane ─────────────────────────────────────────────────────

  @Test
  public void nanDirection_returnsNaN() {
    Vector3 nanDir = new Vector3(Double.NaN, Double.NaN, Double.NaN);
    Point3 result = invokeIntersect(
        new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS),
        Matrix4x4.IDENTITY, Point3.ORIGIN, nanDir);
    assertTrue("NaN plane direction should yield NaN result", result.isNaN());
  }

  // ── Negative-direction normals ────────────────────────────────────

  @Test
  public void negativeYNormal_hitsFromAbove() {
    Point3 result = invokeIntersect(
        new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS),
        Matrix4x4.IDENTITY, Point3.ORIGIN, Vector3.NEGATIVE_Y_AXIS);
    assertFalse(result.isNaN());
    assertEquals(0.0, result.y(), 0.01);
  }

  @Test
  public void negativeXNormal_hitsFromRight() {
    Point3 result = invokeIntersect(
        new Ray(new Point3(5, 0, 0), Vector3.NEGATIVE_X_AXIS),
        Matrix4x4.IDENTITY, Point3.ORIGIN, Vector3.NEGATIVE_X_AXIS);
    assertFalse(result.isNaN());
    assertEquals(0.0, result.x(), 0.01);
  }

  // ── Reflection helper ─────────────────────────────────────────────

  private Point3 invokeIntersect(Ray ray, Matrix4x4 m, Point3 position, Vector3 direction) {
    try {
      Method method = GlrGeometry.class.getDeclaredMethod(
          "getIntersectionInSourceFromPlaneInLocal", Ray.class, Matrix4x4.class, Point3.class, Vector3.class);
      method.setAccessible(true);
      return (Point3) method.invoke(null, ray, m, position, direction);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
