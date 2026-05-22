package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.OrthographicCamera;
import org.alice.math.immutable.ClippedZPlane;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector3;
import org.junit.Before;
import org.junit.Test;

import java.awt.Rectangle;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrOrthographicCamera} — projection matrix,
 * getRayAtViewportPixel, performLetterboxing, and getActualPicturePlane.
 *
 * Uses a real OrthographicCamera as owner. The orthographic projection
 * is pure math with no GL calls.
 */
public class GlrOrthographicCameraTest {

  private GlrOrthographicCamera adapter;
  private OrthographicCamera camera;
  private static final double DELTA = 0.0001;

  @Before
  public void setUp() throws Exception {
    camera = new OrthographicCamera();
    camera.nearClippingPlaneDistance.setValue(0.125);
    camera.farClippingPlaneDistance.setValue(256.0);
    camera.picturePlane.setValue(ClippedZPlane.DEFAULT);

    adapter = new GlrOrthographicCamera();
    setOwner(adapter, camera);
  }

  // ── getActualPicturePlane ──

  @Test
  public void actualPicturePlane_completesFromViewport() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    ClippedZPlane pp = adapter.getActualPicturePlane(vp);
    assertNotNull(pp);
    // DEFAULT has halfHeight=0.1, halfWidth=NaN
    // completeFrom(800/600) → halfWidth = 0.1 * (800/600)
    double expectedHalfWidth = 0.1 * (800.0 / 600.0);
    assertEquals(expectedHalfWidth, pp.halfWidth(), DELTA);
    assertEquals(0.1, pp.halfHeight(), DELTA);
  }

  @Test
  public void actualPicturePlane_squareViewport() {
    Rectangle vp = new Rectangle(0, 0, 500, 500);
    ClippedZPlane pp = adapter.getActualPicturePlane(vp);
    // aspect=1 → halfWidth = halfHeight = 0.1
    assertEquals(0.1, pp.halfWidth(), DELTA);
    assertEquals(0.1, pp.halfHeight(), DELTA);
  }

  @Test
  public void actualPicturePlane_extremelyWideViewport() {
    Rectangle vp = new Rectangle(0, 0, 2000, 100);
    ClippedZPlane pp = adapter.getActualPicturePlane(vp);
    // halfWidth = 0.1 * 20 = 2.0
    assertEquals(2.0, pp.halfWidth(), DELTA);
    assertEquals(0.1, pp.halfHeight(), DELTA);
  }

  // ── getActualProjectionMatrix ──

  @Test
  public void projectionMatrix_isNotNull() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(vp);
    assertNotNull(m);
  }

  @Test
  public void projectionMatrix_orthographicFormat() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(vp);
    double[] arr = m.asColumnMajorArray16();

    ClippedZPlane pp = adapter.getActualPicturePlane(vp);
    double left = pp.getXMinimum();
    double right = pp.getXMaximum();
    double bottom = pp.getYMinimum();
    double top = pp.getYMaximum();
    double near = 0.125;
    double far = 256.0;

    // Standard OpenGL orthographic matrix:
    // m[0][0] = 2/(right-left)
    assertEquals(2.0 / (right - left), arr[0], DELTA);
    // m[1][1] = 2/(top-bottom)
    assertEquals(2.0 / (top - bottom), arr[5], DELTA);
    // m[2][2] = -2/(far-near)
    assertEquals(-2.0 / (far - near), arr[10], DELTA);
  }

  @Test
  public void projectionMatrix_translationComponents() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(vp);
    double[] arr = m.asColumnMajorArray16();

    ClippedZPlane pp = adapter.getActualPicturePlane(vp);
    double left = pp.getXMinimum();
    double right = pp.getXMaximum();
    double bottom = pp.getYMinimum();
    double top = pp.getYMaximum();
    double near = 0.125;
    double far = 256.0;

    // Translation: tx = -(right+left)/(right-left)
    double expectedTx = -(right + left) / (right - left);
    // ty = -(top+bottom)/(top-bottom)
    double expectedTy = -(top + bottom) / (top - bottom);
    // tz = -(far+near)/(far-near)
    double expectedTz = -(far + near) / (far - near);

    // For symmetric picture plane, left=-right, bottom=-top → tx=0, ty=0
    assertEquals(expectedTx, arr[12], DELTA);
    assertEquals(expectedTy, arr[13], DELTA);
    assertEquals(expectedTz, arr[14], DELTA);
  }

  @Test
  public void projectionMatrix_symmetricPlane_txTyAreZero() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(vp);
    double[] arr = m.asColumnMajorArray16();
    // Symmetric around origin → tx=0, ty=0
    assertEquals(0.0, arr[12], DELTA);
    assertEquals(0.0, arr[13], DELTA);
  }

  @Test
  public void projectionMatrix_differentNearFar_changesZScale() {
    camera.nearClippingPlaneDistance.setValue(1.0);
    camera.farClippingPlaneDistance.setValue(100.0);
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(vp);
    double[] arr = m.asColumnMajorArray16();
    assertEquals(-2.0 / (100.0 - 1.0), arr[10], DELTA);
  }

  // ── getRayAtViewportPixel ──

  @Test
  public void rayAtCenter_directionIsNegativeZ() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    int cx = vp.width / 2;
    int cy = vp.height / 2;
    Ray ray = adapter.getRayAtViewportPixel(cx, cy, vp);
    assertNotNull(ray);
    // Orthographic rays are always along -Z
    assertEquals(0.0, ray.direction().x(), DELTA);
    assertEquals(0.0, ray.direction().y(), DELTA);
    assertEquals(-1.0, ray.direction().z(), DELTA);
  }

  @Test
  public void rayAtCenter_originAtViewCenter() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    int cx = vp.width / 2;
    int cy = vp.height / 2;
    Ray ray = adapter.getRayAtViewportPixel(cx, cy, vp);
    // At center: xPortion=0.5, yPortion=0.5
    // x = left + (right-left)*0.5 = 0 (symmetric)
    // y = bottom + (top-bottom)*0.5 = 0 (symmetric)
    assertEquals(0.0, ray.origin().x(), DELTA);
    assertEquals(0.0, ray.origin().y(), DELTA);
    assertEquals(camera.nearClippingPlaneDistance.getValue(), ray.origin().z(), DELTA);
  }

  @Test
  public void rayAtTopLeft_originAtCorner() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    Ray ray = adapter.getRayAtViewportPixel(0, 0, vp);
    ClippedZPlane pp = adapter.getActualPicturePlane(vp);
    // xPortion=0 → x = left = -halfWidth
    assertEquals(pp.getXMinimum(), ray.origin().x(), DELTA);
    // yPortion=0 → y = bottom = -halfHeight
    assertEquals(pp.getYMinimum(), ray.origin().y(), DELTA);
  }

  @Test
  public void rayAtBottomRight_originAtCorner() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    Ray ray = adapter.getRayAtViewportPixel(800, 600, vp);
    ClippedZPlane pp = adapter.getActualPicturePlane(vp);
    // xPortion=1 → x = right = halfWidth
    assertEquals(pp.getXMaximum(), ray.origin().x(), DELTA);
    // yPortion=1 → y = top = halfHeight
    assertEquals(pp.getYMaximum(), ray.origin().y(), DELTA);
  }

  @Test
  public void rayWithLetterboxedViewport_accountsForOffset() {
    Rectangle vp = new Rectangle(100, 50, 600, 500);
    int cx = vp.x + vp.width / 2;
    int cy = vp.y + vp.height / 2;
    Ray ray = adapter.getRayAtViewportPixel(cx, cy, vp);
    // Center of offset viewport → xPortion=0.5, yPortion=0.5
    assertEquals(0.0, ray.origin().x(), DELTA);
    assertEquals(0.0, ray.origin().y(), DELTA);
  }

  @Test
  public void allRays_directionIsNegativeZ() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    // Check several pixels — all should have -Z direction
    int[][] pixels = {{0, 0}, {400, 300}, {800, 600}, {200, 100}};
    for (int[] px : pixels) {
      Ray ray = adapter.getRayAtViewportPixel(px[0], px[1], vp);
      assertEquals("Ray at (" + px[0] + "," + px[1] + ") should be -Z",
          Vector3.NEGATIVE_Z_AXIS, ray.direction());
    }
  }

  // ── performLetterboxing ──

  @Test
  public void performLetterboxing_returnsInputUnchanged() {
    Rectangle input = new Rectangle(10, 20, 800, 600);
    Rectangle result = adapter.performLetterboxing(input);
    // Orthographic camera's performLetterboxing just returns the input
    assertEquals(input, result);
  }

  @Test
  public void performLetterboxing_identityForAnyRect() {
    Rectangle rect = new Rectangle(0, 0, 1920, 1080);
    Rectangle result = adapter.performLetterboxing(rect);
    assertSame("Should return same rect instance", rect, result);
  }

  // ── isLetterboxed default ──

  @Test
  public void isLetterboxed_defaultTrue() {
    GlrOrthographicCamera fresh = new GlrOrthographicCamera();
    assertTrue(fresh.isLetterboxed());
  }

  // ── Custom picture plane ──

  @Test
  public void customPicturePlane_affectsProjection() {
    camera.picturePlane.setValue(new ClippedZPlane(1.0, 1.0));
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(vp);
    double[] arr = m.asColumnMajorArray16();
    // With halfWidth=1, halfHeight=1: left=-1, right=1 → 2/(1-(-1)) = 1.0
    assertEquals(1.0, arr[0], DELTA);
    assertEquals(1.0, arr[5], DELTA);
  }

  // ── Helpers ──

  @SuppressWarnings("unchecked")
  private <T> void setOwner(GlrOrthographicCamera adapter, T owner) throws Exception {
    Field f = findField(adapter.getClass(), "owner");
    f.setAccessible(true);
    f.set(adapter, owner);
  }

  private Field findField(Class<?> clazz, String name) {
    while (clazz != null) {
      try {
        return clazz.getDeclaredField(name);
      } catch (NoSuchFieldException e) {
        clazz = clazz.getSuperclass();
      }
    }
    throw new RuntimeException("Field '" + name + "' not found in hierarchy");
  }
}
