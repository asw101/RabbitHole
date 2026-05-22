package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector3;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrBox} — getIntersectionInSource for all 6 face subElements
 * plus default case, static normal/texCoord maps, and static initializer.
 *
 * Uses reflection to set xMin/xMax/yMin/yMax/zMin/zMax since propertyChanged()
 * requires a fully initialized owner.
 */
public class GlrBoxTest {

  private GlrBox box;
  private static final double DELTA = 0.0001;

  @Before
  public void setUp() throws Exception {
    box = new GlrBox();
    // Set box extents: -1 to +1 on all axes (unit cube centered at origin)
    setField("xMin", -1.0);
    setField("xMax", 1.0);
    setField("yMin", -1.0);
    setField("yMax", 1.0);
    setField("zMin", -1.0);
    setField("zMax", 1.0);
  }

  // ── getIntersectionInSource — subElement 0: xMin face (left, -X) ──

  @Test
  public void intersection_subElement0_xMinFace_returnsPoint() {
    Ray ray = new Ray(new Point3(-5, 0, 0), Vector3.POSITIVE_X_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 0);
    assertFalse("Should find intersection on xMin face", result.isNaN());
  }

  @Test
  public void intersection_subElement0_xMinFace_planeAtNegativeOne() {
    // Ray along +X hitting the -X plane at x=-1
    Ray ray = new Ray(new Point3(-5, 0, 0), Vector3.POSITIVE_X_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 0);
    assertEquals(-1.0, result.x(), DELTA);
  }

  // ── subElement 1: xMax face (right, +X) ──

  @Test
  public void intersection_subElement1_xMaxFace_returnsPoint() {
    Ray ray = new Ray(new Point3(5, 0, 0), Vector3.NEGATIVE_X_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 1);
    assertFalse("Should find intersection on xMax face", result.isNaN());
  }

  @Test
  public void intersection_subElement1_xMaxFace_planeAtPositiveOne() {
    Ray ray = new Ray(new Point3(5, 0, 0), Vector3.NEGATIVE_X_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 1);
    assertEquals(1.0, result.x(), DELTA);
  }

  // ── subElement 2: yMin face (bottom, -Y) ──

  @Test
  public void intersection_subElement2_yMinFace_returnsPoint() {
    Ray ray = new Ray(new Point3(0, -5, 0), Vector3.POSITIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 2);
    assertFalse("Should find intersection on yMin face", result.isNaN());
  }

  @Test
  public void intersection_subElement2_yMinFace_planeAtNegativeOne() {
    Ray ray = new Ray(new Point3(0, -5, 0), Vector3.POSITIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 2);
    assertEquals(-1.0, result.y(), DELTA);
  }

  // ── subElement 3: yMax face (top, +Y) ──

  @Test
  public void intersection_subElement3_yMaxFace_returnsPoint() {
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 3);
    assertFalse("Should find intersection on yMax face", result.isNaN());
  }

  @Test
  public void intersection_subElement3_yMaxFace_planeAtPositiveOne() {
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 3);
    assertEquals(1.0, result.y(), DELTA);
  }

  // ── subElement 4: zMin face (front, -Z) ──

  @Test
  public void intersection_subElement4_zMinFace_returnsPoint() {
    Ray ray = new Ray(new Point3(0, 0, -5), Vector3.POSITIVE_Z_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 4);
    assertFalse("Should find intersection on zMin face", result.isNaN());
  }

  @Test
  public void intersection_subElement4_zMinFace_planeAtNegativeOne() {
    Ray ray = new Ray(new Point3(0, 0, -5), Vector3.POSITIVE_Z_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 4);
    assertEquals(-1.0, result.z(), DELTA);
  }

  // ── subElement 5: zMax face (back, +Z) ──

  @Test
  public void intersection_subElement5_zMaxFace_returnsPoint() {
    Ray ray = new Ray(new Point3(0, 0, 5), Vector3.NEGATIVE_Z_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 5);
    assertFalse("Should find intersection on zMax face", result.isNaN());
  }

  @Test
  public void intersection_subElement5_zMaxFace_planeAtPositiveOne() {
    Ray ray = new Ray(new Point3(0, 0, 5), Vector3.NEGATIVE_Z_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 5);
    assertEquals(1.0, result.z(), DELTA);
  }

  // ── default subElement returns NaN ──

  @Test
  public void intersection_defaultSubElement_returnsNaN() {
    Ray ray = new Ray(new Point3(0, 0, -5), Vector3.POSITIVE_Z_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 6);
    assertTrue("Default sub-element should return NaN", result.isNaN());
  }

  @Test
  public void intersection_negativeSubElement_returnsNaN() {
    Ray ray = new Ray(new Point3(0, 0, -5), Vector3.POSITIVE_Z_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, -1);
    assertTrue("Negative sub-element should return NaN", result.isNaN());
  }

  @Test
  public void intersection_largeSubElement_returnsNaN() {
    Ray ray = new Ray(new Point3(0, 0, -5), Vector3.POSITIVE_Z_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 100);
    assertTrue("Large sub-element should return NaN", result.isNaN());
  }

  // ── Asymmetric box extents ──

  @Test
  public void intersection_asymmetricBox_xMinAtCustomValue() throws Exception {
    setField("xMin", -3.0);
    setField("xMax", 2.0);
    Ray ray = new Ray(new Point3(-10, 0, 0), Vector3.POSITIVE_X_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 0);
    assertEquals(-3.0, result.x(), DELTA);
  }

  @Test
  public void intersection_asymmetricBox_yMaxAtCustomValue() throws Exception {
    setField("yMax", 5.0);
    Ray ray = new Ray(new Point3(0, 10, 0), Vector3.NEGATIVE_Y_AXIS);
    Matrix4x4 m = AffineMatrix4x4.IDENTITY;
    Point3 result = box.getIntersectionInSource(ray, m, 3);
    assertEquals(5.0, result.y(), DELTA);
  }

  // ── With transformation matrix ──

  @Test
  public void intersection_withTranslation_shiftsFace() {
    Ray ray = new Ray(new Point3(-10, 0, 0), Vector3.POSITIVE_X_AXIS);
    Matrix4x4 m = new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(5, 0, 0));
    Point3 result = box.getIntersectionInSource(ray, m, 0);
    // xMin plane position=(-1,0,0) transformed by translate(5,0,0) → (4,0,0)
    // Plane normal = NEGATIVE_X_AXIS transformed → still (-1,0,0)
    assertFalse("Should find intersection with translated face", result.isNaN());
  }

  // ── Static maps ──

  @Test
  @SuppressWarnings("unchecked")
  public void normal3dMap_containsSixEntries() throws Exception {
    Field f = GlrBox.class.getDeclaredField("normal3dMap");
    f.setAccessible(true);
    Map<String, int[]> map = (Map<String, int[]>) f.get(null);
    assertEquals(6, map.size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void normal3dMap_leftNormalIsNegativeX() throws Exception {
    Field f = GlrBox.class.getDeclaredField("normal3dMap");
    f.setAccessible(true);
    Map<String, int[]> map = (Map<String, int[]>) f.get(null);
    int[] left = map.get("left");
    assertArrayEquals(new int[]{-1, 0, 0}, left);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void normal3dMap_rightNormalIsPositiveX() throws Exception {
    Field f = GlrBox.class.getDeclaredField("normal3dMap");
    f.setAccessible(true);
    Map<String, int[]> map = (Map<String, int[]>) f.get(null);
    int[] right = map.get("right");
    assertArrayEquals(new int[]{1, 0, 0}, right);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void texCoordMap_containsSixEntries() throws Exception {
    Field f = GlrBox.class.getDeclaredField("texCoordMap");
    f.setAccessible(true);
    Map<String, float[][]> map = (Map<String, float[][]>) f.get(null);
    assertEquals(6, map.size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void texCoordMap_eachEntryHasFourCorners() throws Exception {
    Field f = GlrBox.class.getDeclaredField("texCoordMap");
    f.setAccessible(true);
    Map<String, float[][]> map = (Map<String, float[][]>) f.get(null);
    for (Map.Entry<String, float[][]> entry : map.entrySet()) {
      assertEquals("Face " + entry.getKey() + " should have 4 corners", 4, entry.getValue().length);
      for (float[] corner : entry.getValue()) {
        assertEquals("Each corner should have 2 tex coords", 2, corner.length);
      }
    }
  }

  @Test
  public void numberOfCorner_isFour() throws Exception {
    Field f = GlrBox.class.getDeclaredField("numberOfCorner");
    f.setAccessible(true);
    assertEquals(4, f.getInt(null));
  }

  // ── Helpers ──

  private void setField(String name, double value) throws Exception {
    Field f = GlrBox.class.getDeclaredField(name);
    f.setAccessible(true);
    f.setDouble(box, value);
  }
}
