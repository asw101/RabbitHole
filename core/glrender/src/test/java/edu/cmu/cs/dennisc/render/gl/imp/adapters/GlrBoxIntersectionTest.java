package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.Box;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector3;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrBox#getIntersectionInSource(Ray, Matrix4x4, int)} —
 * pure math ray-plane intersection for each box face.
 * Sets box bounds via reflection since propertyChanged needs GL.
 */
public class GlrBoxIntersectionTest {

  private GlrBox box;

  @Before
  public void setUp() throws Exception {
    box = new GlrBox();
    Box sgBox = new Box();
    box.initialize(sgBox);
    // Set bounds to [-1, 1] cube
    setField("xMin", -1.0);
    setField("xMax", 1.0);
    setField("yMin", -1.0);
    setField("yMax", 1.0);
    setField("zMin", -1.0);
    setField("zMax", 1.0);
  }

  // ── Default / invalid subElement ──────────────────────────────────

  @Test
  public void invalidSubElement_returnsNaN() {
    Ray ray = new Ray(new Point3(5, 0, 0), Vector3.NEGATIVE_X_AXIS);
    Point3 result = box.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 99);
    assertTrue(result.isNaN());
  }

  @Test
  public void negativeSubElement_returnsNaN() {
    Ray ray = new Ray(new Point3(5, 0, 0), Vector3.NEGATIVE_X_AXIS);
    Point3 result = box.getIntersectionInSource(ray, Matrix4x4.IDENTITY, -1);
    assertTrue(result.isNaN());
  }

  // ── SubElement 0: xMin face (left, normal = -X) ───────────────────

  @Test
  public void subElement0_xMinFace_hitFromRight() {
    Ray ray = new Ray(new Point3(5, 0, 0), Vector3.NEGATIVE_X_AXIS);
    Point3 result = box.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertFalse(result.isNaN());
    assertEquals(-1.0, result.x(), 0.01);
  }

  // ── SubElement 1: xMax face (right, normal = +X) ──────────────────

  @Test
  public void subElement1_xMaxFace_hitFromLeft() {
    Ray ray = new Ray(new Point3(-5, 0, 0), Vector3.POSITIVE_X_AXIS);
    Point3 result = box.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 1);
    assertFalse(result.isNaN());
    assertEquals(1.0, result.x(), 0.01);
  }

  // ── SubElement 2: yMin face (bottom, normal = -Y) ─────────────────

  @Test
  public void subElement2_yMinFace_hitFromAbove() {
    Ray ray = new Ray(new Point3(0, 5, 0), Vector3.NEGATIVE_Y_AXIS);
    Point3 result = box.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 2);
    assertFalse(result.isNaN());
    assertEquals(-1.0, result.y(), 0.01);
  }

  // ── SubElement 3: yMax face (top, normal = +Y) ────────────────────

  @Test
  public void subElement3_yMaxFace_hitFromBelow() {
    Ray ray = new Ray(new Point3(0, -5, 0), Vector3.POSITIVE_Y_AXIS);
    Point3 result = box.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 3);
    assertFalse(result.isNaN());
    assertEquals(1.0, result.y(), 0.01);
  }

  // ── SubElement 4: zMin face (front, normal = -Z) ──────────────────

  @Test
  public void subElement4_zMinFace_hitFromFront() {
    Ray ray = new Ray(new Point3(0, 0, 5), Vector3.NEGATIVE_Z_AXIS);
    Point3 result = box.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 4);
    assertFalse(result.isNaN());
    assertEquals(-1.0, result.z(), 0.01);
  }

  // ── SubElement 5: zMax face (back, normal = +Z) ───────────────────

  @Test
  public void subElement5_zMaxFace_hitFromBack() {
    Ray ray = new Ray(new Point3(0, 0, -5), Vector3.POSITIVE_Z_AXIS);
    Point3 result = box.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 5);
    assertFalse(result.isNaN());
    assertEquals(1.0, result.z(), 0.01);
  }

  // ── Asymmetric box ────────────────────────────────────────────────

  @Test
  public void asymmetricBox_subElement0_hitsAtXMin() throws Exception {
    setField("xMin", -3.0);
    setField("xMax", 2.0);
    Ray ray = new Ray(new Point3(10, 0, 0), Vector3.NEGATIVE_X_AXIS);
    Point3 result = box.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    assertEquals(-3.0, result.x(), 0.01);
  }

  @Test
  public void asymmetricBox_subElement1_hitsAtXMax() throws Exception {
    setField("xMin", -3.0);
    setField("xMax", 2.0);
    Ray ray = new Ray(new Point3(-10, 0, 0), Vector3.POSITIVE_X_AXIS);
    Point3 result = box.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 1);
    assertEquals(2.0, result.x(), 0.01);
  }

  // ── Static normal/tex maps ────────────────────────────────────────

  @Test
  public void staticMaps_exist() throws Exception {
    Field normal3dMap = GlrBox.class.getDeclaredField("normal3dMap");
    normal3dMap.setAccessible(true);
    assertNotNull(normal3dMap.get(null));

    Field texCoordMap = GlrBox.class.getDeclaredField("texCoordMap");
    texCoordMap.setAccessible(true);
    assertNotNull(texCoordMap.get(null));
  }

  @Test
  public void numberOfCorner_isFour() throws Exception {
    Field f = GlrBox.class.getDeclaredField("numberOfCorner");
    f.setAccessible(true);
    assertEquals(4, f.getInt(null));
  }

  // ── Reflection helper ─────────────────────────────────────────────

  private void setField(String name, double value) throws Exception {
    Field f = GlrBox.class.getDeclaredField(name);
    f.setAccessible(true);
    f.setDouble(box, value);
  }
}
