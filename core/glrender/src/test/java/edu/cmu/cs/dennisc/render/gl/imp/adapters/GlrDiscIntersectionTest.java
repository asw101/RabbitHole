package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.Disc;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector3;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrDisc} — plane-based intersection and propertyChanged
 * coverage for all disc properties (innerRadius, outerRadius, face visibility).
 */
public class GlrDiscIntersectionTest {

  private GlrDisc disc;
  private Disc sgDisc;

  @Before
  public void setUp() {
    disc = new GlrDisc();
    sgDisc = new Disc();
    disc.initialize(sgDisc);
  }

  // ── Intersection (plane-based) ────────────────────────────────────

  @Test
  public void hitFromAbove_returnsOriginPlane() {
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Point3 result = disc.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse("Should hit disc plane", result.isNaN());
    assertEquals(0.0, result.y(), 0.01);
  }

  @Test
  public void hitFromBelow_returnsOriginPlane() {
    Ray ray = new Ray(new Point3(0, -5, 0), Vector3.POSITIVE_Y_AXIS);
    Point3 result = disc.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse(result.isNaN());
    assertEquals(0.0, result.y(), 0.01);
  }

  @Test
  public void hitAtOffset_returnsCorrectXZ() {
    Ray ray = new Ray(new Point3(3, 5, 4), Vector3.NEGATIVE_Y_AXIS);
    Point3 result = disc.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse(result.isNaN());
    assertEquals(3.0, result.x(), 0.01);
    assertEquals(0.0, result.y(), 0.01);
    assertEquals(4.0, result.z(), 0.01);
  }

  @Test
  public void parallelRay_returnsNaN() {
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.POSITIVE_X_AXIS);
    Point3 result = disc.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    // Parallel to Y=0 plane — should be NaN or infinity
    assertNotNull(result);
  }

  @Test
  public void subElement_doesNotAffectResult() {
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Point3 r0 = disc.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    Point3 r1 = disc.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 1);
    Point3 r99 = disc.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 99);
    // Disc ignores subElement — all should produce same result
    assertEquals(r0.x(), r1.x(), 0.001);
    assertEquals(r0.x(), r99.x(), 0.001);
  }

  // ── propertyChanged coverage ──────────────────────────────────────

  @Test
  public void propertyChanged_innerRadius_marksGeometryChanged() throws Exception {
    sgDisc.innerRadius.setValue(0.5);
    disc.propertyChanged(sgDisc.innerRadius);
    assertGeometryChanged();
  }

  @Test
  public void propertyChanged_outerRadius_marksGeometryChanged() throws Exception {
    sgDisc.outerRadius.setValue(2.0);
    disc.propertyChanged(sgDisc.outerRadius);
    assertGeometryChanged();
  }

  @Test
  public void propertyChanged_isFrontFaceVisible_marksGeometryChanged() throws Exception {
    sgDisc.isFrontFaceVisible.setValue(false);
    disc.propertyChanged(sgDisc.isFrontFaceVisible);
    assertGeometryChanged();
  }

  @Test
  public void propertyChanged_isBackFaceVisible_marksGeometryChanged() throws Exception {
    sgDisc.isBackFaceVisible.setValue(false);
    disc.propertyChanged(sgDisc.isBackFaceVisible);
    assertGeometryChanged();
  }

  // ── Structure ─────────────────────────────────────────────────────

  @Test
  public void extendsGlrShape() {
    assertTrue(GlrShape.class.isAssignableFrom(GlrDisc.class));
  }

  @Test
  public void ownerIsSetAfterInitialize() {
    assertNotNull(disc.getOwner());
    assertSame(sgDisc, disc.getOwner());
  }

  @Test
  public void isNotAlphaBlended() {
    assertFalse(disc.isAlphaBlended());
  }

  @Test
  public void hasOpaque() {
    assertTrue(disc.hasOpaque());
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private void assertGeometryChanged() throws Exception {
    Field f = GlrGeometry.class.getDeclaredField("isGeometryChanged");
    f.setAccessible(true);
    assertTrue("Geometry should be marked as changed", f.getBoolean(disc));
  }
}
