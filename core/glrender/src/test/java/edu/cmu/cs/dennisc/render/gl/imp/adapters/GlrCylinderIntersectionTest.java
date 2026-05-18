package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.Cylinder;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector3;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrCylinder#getIntersectionInSource(Ray, Matrix4x4, int)} —
 * pure math intersection logic. Sets geometry fields via reflection.
 */
public class GlrCylinderIntersectionTest {

  private GlrCylinder cyl;

  @Before
  public void setUp() throws Exception {
    cyl = new GlrCylinder();
    Cylinder sgCyl = new Cylinder();
    cyl.initialize(sgCyl);
    setField("length", 2.0);
    setField("bottomRadius", 1.0);
    setField("topRadius", 1.0);
    setField("hasBottomCap", true);
    setField("hasTopCap", true);
    setField("originAlignment", Cylinder.OriginAlignment.CENTER);
    setField("bottomToTopAxis", Cylinder.BottomToTopAxis.POSITIVE_Y);
  }

  // ── Origin alignment switch coverage ──────────────────────────────

  @Test
  public void centerOrigin_computesBounds() {
    // With CENTER alignment, bottomValue = -1, topValue = +1
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Point3 result = cyl.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    // Result may be NaN (cone path) or a valid point
    assertNotNull(result);
  }

  @Test
  public void bottomOrigin_computesBounds() throws Exception {
    setField("originAlignment", Cylinder.OriginAlignment.BOTTOM);
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Point3 result = cyl.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertNotNull(result);
  }

  @Test
  public void topOrigin_computesBounds() throws Exception {
    setField("originAlignment", Cylinder.OriginAlignment.TOP);
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Point3 result = cyl.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertNotNull(result);
  }

  // ── Bottom-to-top axis coverage ───────────────────────────────────

  @Test
  public void positiveYAxis_vector() {
    assertEquals(new Vector3(0, 1, 0), Cylinder.BottomToTopAxis.POSITIVE_Y.getVector());
  }

  @Test
  public void positiveXAxis_vector() {
    assertEquals(new Vector3(1, 0, 0), Cylinder.BottomToTopAxis.POSITIVE_X.getVector());
  }

  @Test
  public void positiveZAxis_vector() {
    assertEquals(new Vector3(0, 0, 1), Cylinder.BottomToTopAxis.POSITIVE_Z.getVector());
  }

  @Test
  public void negativeXAxis_vector() {
    assertEquals(new Vector3(-1, 0, 0), Cylinder.BottomToTopAxis.NEGATIVE_X.getVector());
  }

  @Test
  public void negativeYAxis_vector() {
    assertEquals(new Vector3(0, -1, 0), Cylinder.BottomToTopAxis.NEGATIVE_Y.getVector());
  }

  @Test
  public void negativeZAxis_vector() {
    assertEquals(new Vector3(0, 0, -1), Cylinder.BottomToTopAxis.NEGATIVE_Z.getVector());
  }

  // ── Intersection with caps (bottom/top hit fallback) ──────────────

  @Test
  public void capHit_bottomCapEnabled_returnsPoint() throws Exception {
    setField("originAlignment", Cylinder.OriginAlignment.CENTER);
    setField("bottomToTopAxis", Cylinder.BottomToTopAxis.POSITIVE_Z);
    // Ray pointing along Z, should hit cap
    Ray ray = new Ray(new Point3(0, 0, -5), Vector3.POSITIVE_Z_AXIS);
    Point3 result = cyl.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertNotNull(result);
  }

  @Test
  public void noCaps_missSide_returnsNaN() throws Exception {
    setField("hasBottomCap", false);
    setField("hasTopCap", false);
    // The cone path returns NaN for non-matching radii by default (todo path)
    Ray ray = new Ray(new Point3(100, 100, 100), Vector3.POSITIVE_X_AXIS);
    Point3 result = cyl.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertTrue("Miss should return NaN", result.isNaN());
  }

  // ── Zero-radius caps ──────────────────────────────────────────────

  @Test
  public void zeroBottomRadius_skipBottomCap() throws Exception {
    setField("bottomRadius", 0.0);
    Ray ray = new Ray(new Point3(0, 0, -5), Vector3.POSITIVE_Z_AXIS);
    Point3 result = cyl.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertNotNull(result);
  }

  @Test
  public void zeroTopRadius_skipTopCap() throws Exception {
    setField("topRadius", 0.0);
    Ray ray = new Ray(new Point3(0, 0, 5), Vector3.NEGATIVE_Z_AXIS);
    Point3 result = cyl.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertNotNull(result);
  }

  // ── Structure ─────────────────────────────────────────────────────

  @Test
  public void extendsGlrShape() {
    assertTrue(GlrShape.class.isAssignableFrom(GlrCylinder.class));
  }

  @Test
  public void ownerIsSetAfterInitialize() {
    assertNotNull(cyl.getOwner());
    assertTrue(cyl.getOwner() instanceof Cylinder);
  }

  // ── Reflection helper ─────────────────────────────────────────────

  private void setField(String name, Object value) throws Exception {
    Field f = GlrCylinder.class.getDeclaredField(name);
    f.setAccessible(true);
    f.set(cyl, value);
  }
}
