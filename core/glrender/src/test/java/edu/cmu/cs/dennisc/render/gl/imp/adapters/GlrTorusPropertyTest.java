package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.Torus;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector3;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrTorus} — intersection plane selection per coordinate plane
 * and propertyChanged coverage for majorRadius, minorRadius, coordinatePlane.
 */
public class GlrTorusPropertyTest {

  private GlrTorus torus;
  private Torus sgTorus;

  @Before
  public void setUp() {
    torus = new GlrTorus();
    sgTorus = new Torus();
    torus.initialize(sgTorus);
  }

  // ── Intersection per coordinate plane ─────────────────────────────

  @Test
  public void intersection_XZ_plane_usesPositiveY() {
    sgTorus.coordinatePlane.setValue(Torus.CoordinatePlane.XZ);
    torus.propertyChanged(sgTorus.coordinatePlane);
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Point3 result = torus.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse(result.isNaN());
    assertEquals(0.0, result.y(), 0.01);
  }

  @Test
  public void intersection_XY_plane_usesPositiveZ() {
    sgTorus.coordinatePlane.setValue(Torus.CoordinatePlane.XY);
    torus.propertyChanged(sgTorus.coordinatePlane);
    Ray ray = new Ray(new Point3(0, 0, 5), Vector3.NEGATIVE_Z_AXIS);
    Point3 result = torus.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse(result.isNaN());
    assertEquals(0.0, result.z(), 0.01);
  }

  @Test
  public void intersection_YZ_plane_usesPositiveX() {
    sgTorus.coordinatePlane.setValue(Torus.CoordinatePlane.YZ);
    torus.propertyChanged(sgTorus.coordinatePlane);
    Ray ray = new Ray(new Point3(5, 0, 0), Vector3.NEGATIVE_X_AXIS);
    Point3 result = torus.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse(result.isNaN());
    assertEquals(0.0, result.x(), 0.01);
  }

  @Test
  public void intersection_defaultPlane_isXZ() {
    // Default coordinatePlane for Torus is XZ
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Point3 result = torus.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse(result.isNaN());
    assertEquals(0.0, result.y(), 0.01);
  }

  @Test
  public void intersection_subElementIgnored() {
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Point3 r0 = torus.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    Point3 r5 = torus.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 5);
    assertEquals(r0.x(), r5.x(), 0.001);
    assertEquals(r0.y(), r5.y(), 0.001);
  }

  // ── propertyChanged coverage ──────────────────────────────────────

  @Test
  public void propertyChanged_majorRadius_marksGeometryChanged() throws Exception {
    sgTorus.majorRadius.setValue(2.0);
    torus.propertyChanged(sgTorus.majorRadius);
    assertGeometryChanged();
  }

  @Test
  public void propertyChanged_minorRadius_marksGeometryChanged() throws Exception {
    sgTorus.minorRadius.setValue(0.5);
    torus.propertyChanged(sgTorus.minorRadius);
    assertGeometryChanged();
  }

  @Test
  public void propertyChanged_coordinatePlane_marksGeometryChanged() throws Exception {
    sgTorus.coordinatePlane.setValue(Torus.CoordinatePlane.YZ);
    torus.propertyChanged(sgTorus.coordinatePlane);
    assertGeometryChanged();
  }

  // ── Structure ─────────────────────────────────────────────────────

  @Test
  public void extendsGlrShape() {
    assertTrue(GlrShape.class.isAssignableFrom(GlrTorus.class));
  }

  @Test
  public void ownerIsSetAfterInitialize() {
    assertSame(sgTorus, torus.getOwner());
  }

  @Test
  public void isNotAlphaBlended() {
    assertFalse(torus.isAlphaBlended());
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private void assertGeometryChanged() throws Exception {
    Field f = GlrGeometry.class.getDeclaredField("isGeometryChanged");
    f.setAccessible(true);
    assertTrue("Geometry should be marked as changed", f.getBoolean(torus));
  }
}
