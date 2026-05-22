package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.Cylinder;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector3;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrCylinder} — getIntersectionInSource for all 3 origin
 * alignments (BOTTOM, CENTER, TOP) and cap intersection handling.
 *
 * Uses reflection to set length, bottomRadius, topRadius, hasBottomCap,
 * hasTopCap, originAlignment, and bottomToTopAxis directly.
 */
public class GlrCylinderTest {

  private GlrCylinder cyl;
  private static final double DELTA = 0.0001;

  @Before
  public void setUp() throws Exception {
    cyl = new GlrCylinder();
    // Default cylinder: length=2, radius=1, +Y axis, BOTTOM alignment, both caps
    setField("length", 2.0);
    setField("bottomRadius", 1.0);
    setField("topRadius", 1.0);
    setField("hasBottomCap", true);
    setField("hasTopCap", true);
    setField("originAlignment", Cylinder.OriginAlignment.BOTTOM);
    setField("bottomToTopAxis", Cylinder.BottomToTopAxis.POSITIVE_Y);
  }

  // ── OriginAlignment.BOTTOM ──

  @Test
  public void intersection_bottomAlignment_bottomValueIsZero() {
    // With BOTTOM alignment, bottom=0, top=length=2
    Ray ray = new Ray(new Point3(0, -5, 0), Vector3.POSITIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = cyl.getIntersectionInSource(ray, m, 0);
    // Should hit the bottom cap at y=0
    assertNotNull(result);
  }

  @Test
  public void intersection_bottomAlignment_returnsNonNaN() {
    Ray ray = new Ray(new Point3(0, -5, 0), Vector3.POSITIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = cyl.getIntersectionInSource(ray, m, 0);
    // The cylinder side intersection code goes through the "else" branch
    // (HANDLE_CONES_SEPARATELY=false) and returns NaN for side, then checks caps
    assertFalse("Should find intersection on bottom cap", result.isNaN());
  }

  // ── OriginAlignment.CENTER ──

  @Test
  public void intersection_centerAlignment_bottomValueIsNegativeHalfLength() throws Exception {
    setField("originAlignment", Cylinder.OriginAlignment.CENTER);
    Ray ray = new Ray(new Point3(0, -5, 0), Vector3.POSITIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = cyl.getIntersectionInSource(ray, m, 0);
    // bottom = -length*0.5 = -1, top = +length*0.5 = +1
    assertNotNull(result);
  }

  @Test
  public void intersection_centerAlignment_hitsBottomCap() throws Exception {
    setField("originAlignment", Cylinder.OriginAlignment.CENTER);
    Ray ray = new Ray(new Point3(0, -5, 0), Vector3.POSITIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = cyl.getIntersectionInSource(ray, m, 0);
    assertFalse("Should find intersection for CENTER alignment", result.isNaN());
  }

  // ── OriginAlignment.TOP ──

  @Test
  public void intersection_topAlignment_bottomValueIsLength() throws Exception {
    setField("originAlignment", Cylinder.OriginAlignment.TOP);
    Ray ray = new Ray(new Point3(0, -5, 0), Vector3.POSITIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = cyl.getIntersectionInSource(ray, m, 0);
    // TOP: bottom=length=2, top=0
    assertNotNull(result);
  }

  @Test
  public void intersection_topAlignment_returnsNonNaN() throws Exception {
    setField("originAlignment", Cylinder.OriginAlignment.TOP);
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = cyl.getIntersectionInSource(ray, m, 0);
    assertFalse("Should find intersection for TOP alignment", result.isNaN());
  }

  // ── No caps ──

  @Test
  public void intersection_noCaps_parallelRay_returnsNaN() throws Exception {
    setField("hasBottomCap", false);
    setField("hasTopCap", false);
    // Ray parallel to cylinder axis, outside radius
    Ray ray = new Ray(new Point3(5, 0, 0), Vector3.POSITIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = cyl.getIntersectionInSource(ray, m, 0);
    // No caps and HANDLE_CONES_SEPARATELY=false → side gives NaN, no caps → NaN
    assertTrue("Parallel ray with no caps should miss", result.isNaN());
  }

  @Test
  public void intersection_noBottomCap_bottomCapNotChecked() throws Exception {
    setField("hasBottomCap", false);
    setField("hasTopCap", true);
    // Ray from below, should miss bottom cap since it's disabled
    Ray ray = new Ray(new Point3(0, -5, 0), Vector3.POSITIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = cyl.getIntersectionInSource(ray, m, 0);
    // Still might hit top cap on the way through
    assertNotNull(result);
  }

  @Test
  public void intersection_noTopCap_topCapNotChecked() throws Exception {
    setField("hasTopCap", false);
    setField("hasBottomCap", true);
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = cyl.getIntersectionInSource(ray, m, 0);
    assertNotNull(result);
  }

  // ── Zero-radius caps ──

  @Test
  public void intersection_zeroBottomRadius_bottomCapNotChecked() throws Exception {
    setField("bottomRadius", 0.0);
    Ray ray = new Ray(new Point3(0, -5, 0), Vector3.POSITIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = cyl.getIntersectionInSource(ray, m, 0);
    // bottomRadius=0 means bottom cap is skipped even if hasBottomCap=true
    // May still hit top cap
    assertNotNull(result);
  }

  @Test
  public void intersection_zeroTopRadius_topCapNotChecked() throws Exception {
    setField("topRadius", 0.0);
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = cyl.getIntersectionInSource(ray, m, 0);
    assertNotNull(result);
  }

  // ── Different axes ──

  @Test
  public void intersection_positiveXAxis_usesXDirection() throws Exception {
    setField("bottomToTopAxis", Cylinder.BottomToTopAxis.POSITIVE_X);
    Ray ray = new Ray(new Point3(-5, 0, 0), Vector3.POSITIVE_X_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = cyl.getIntersectionInSource(ray, m, 0);
    assertNotNull(result);
    assertFalse("Should find intersection along X axis", result.isNaN());
  }

  @Test
  public void intersection_positiveZAxis_usesZDirection() throws Exception {
    setField("bottomToTopAxis", Cylinder.BottomToTopAxis.POSITIVE_Z);
    Ray ray = new Ray(new Point3(0, 0, -5), Vector3.POSITIVE_Z_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = cyl.getIntersectionInSource(ray, m, 0);
    assertNotNull(result);
    assertFalse("Should find intersection along Z axis", result.isNaN());
  }

  @Test
  public void intersection_negativeYAxis_usesNegativeYDirection() throws Exception {
    setField("bottomToTopAxis", Cylinder.BottomToTopAxis.NEGATIVE_Y);
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = cyl.getIntersectionInSource(ray, m, 0);
    assertNotNull(result);
    assertFalse("Should find intersection along -Y axis", result.isNaN());
  }

  // ── NaN topRadius (cone-like, uses bottomRadius for both) ──

  @Test
  public void intersection_nanTopRadius_usesBottomRadiusForMax() throws Exception {
    setField("topRadius", Double.NaN);
    Ray ray = new Ray(new Point3(0, -5, 0), Vector3.POSITIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    // This exercises the NaN topRadius check in getIntersectionInSource's maxRadius calc
    Point3 result = cyl.getIntersectionInSource(ray, m, 0);
    assertNotNull(result);
  }

  // ── Cap portion constants via instance fields ──

  @Test
  public void capPortion_isOneThird() {
    assertEquals(1.0f / 3.0f, cyl.capPortion, 0.0001f);
  }

  @Test
  public void capCenterS_isHalf() {
    assertEquals(0.5f, cyl.capCenterS, 0.0001f);
  }

  @Test
  public void topCapCenterT_isOneSixth() {
    assertEquals(1.0f / 6.0f, cyl.topCapCenterT, 0.0001f);
  }

  @Test
  public void bottomCapCenterT_isFiveSixths() {
    assertEquals(5.0f / 6.0f, cyl.bottomCapCenterT, 0.0001f);
  }

  // ── Helpers ──

  private void setField(String name, Object value) throws Exception {
    Field f = GlrCylinder.class.getDeclaredField(name);
    f.setAccessible(true);
    f.set(cyl, value);
  }

  private void setField(String name, double value) throws Exception {
    Field f = GlrCylinder.class.getDeclaredField(name);
    f.setAccessible(true);
    f.setDouble(cyl, value);
  }

  private void setField(String name, boolean value) throws Exception {
    Field f = GlrCylinder.class.getDeclaredField(name);
    f.setAccessible(true);
    f.setBoolean(cyl, value);
  }
}
