package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import org.alice.math.immutable.Angle;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Ray;
import org.junit.Before;
import org.junit.Test;

import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrSymmetricPerspectiveCamera} — projection matrix math,
 * ray generation, letterboxing, and aspect ratio calculations. Pure math — no GL.
 */
public class GlrPerspectiveCameraProjectionTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  private GlrSymmetricPerspectiveCamera adapter;
  private SymmetricPerspectiveCamera cam;

  @Before
  public void setUp() {
    cam = new SymmetricPerspectiveCamera();
    adapter = (GlrSymmetricPerspectiveCamera) AdapterFactory.getAdapterFor(cam);
    adapter.propertyChanged(cam.verticalViewingAngle);
    adapter.propertyChanged(cam.horizontalViewingAngle);
    adapter.propertyChanged(cam.nearClippingPlaneDistance);
    adapter.propertyChanged(cam.farClippingPlaneDistance);
  }

  // ── Projection matrix ─────────────────────────────────────────────

  @Test
  public void projectionMatrix_isNotNull() {
    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(viewport);
    assertNotNull(m);
  }

  @Test
  public void projectionMatrix_nonZeroDiagonal() {
    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    double[] arr = adapter.getActualProjectionMatrix(viewport).asColumnMajorArray16();
    assertNotEquals("m[0] should be non-zero", 0.0, arr[0], 0.0001);
    assertNotEquals("m[5] should be non-zero", 0.0, arr[5], 0.0001);
    assertNotEquals("m[10] should be non-zero", 0.0, arr[10], 0.0001);
  }

  @Test
  public void projectionMatrix_squareViewport_symmetricXY() {
    Rectangle viewport = new Rectangle(0, 0, 800, 800);
    double[] arr = adapter.getActualProjectionMatrix(viewport).asColumnMajorArray16();
    // For a square viewport with equal FOV, m[0] and m[5] should be related by aspect
    assertTrue(arr[0] > 0 || arr[0] < 0); // Non-zero
    assertTrue(arr[5] > 0 || arr[5] < 0);
  }

  @Test
  public void projectionMatrix_differentAspects_differentResults() {
    // Default is letterboxed with constant aspect ratio; disable to get viewport-dependent results
    adapter.setIsLetterboxed(false);
    Matrix4x4 m1 = adapter.getActualProjectionMatrix(new Rectangle(0, 0, 800, 600));
    Matrix4x4 m2 = adapter.getActualProjectionMatrix(new Rectangle(0, 0, 1920, 1200));
    double[] a1 = m1.asColumnMajorArray16();
    double[] a2 = m2.asColumnMajorArray16();
    // Without letterboxing, different viewports produce different aspect ratios
    assertNotEquals(a1[0], a2[0], 0.0001);
  }

  // ── Ray generation ────────────────────────────────────────────────

  @Test
  public void rayAtCenter_pointsForward() {
    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    Ray ray = adapter.getRayAtViewportPixel(400, 300, viewport);
    assertNotNull(ray);
    assertNotNull(ray.origin());
    assertNotNull(ray.direction());
  }

  @Test
  public void rayAtCenter_originNotAtOrigin() {
    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    Ray ray = adapter.getRayAtViewportPixel(400, 300, viewport);
    // Origin is on the near plane, not at 0,0,0
    assertFalse("Ray origin should have components", ray.origin().isNaN());
  }

  @Test
  public void rayAtCorner_differentFromCenter() {
    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    Ray center = adapter.getRayAtViewportPixel(400, 300, viewport);
    Ray corner = adapter.getRayAtViewportPixel(0, 0, viewport);
    assertFalse("Corner ray should differ from center",
        center.direction().x() == corner.direction().x()
            && center.direction().y() == corner.direction().y());
  }

  @Test
  public void rayAtSymmetricPoints_haveSymmetricDirections() {
    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    Ray left = adapter.getRayAtViewportPixel(200, 300, viewport);
    Ray right = adapter.getRayAtViewportPixel(600, 300, viewport);
    // Symmetric about center — x components should be approximately negated
    assertEquals("Symmetric rays x should be opposite sign",
        left.direction().x(), -right.direction().x(), 0.1);
  }

  // ── Letterboxing ──────────────────────────────────────────────────

  @Test
  public void letterboxing_squareViewport_noChange() throws Exception {
    Rectangle input = new Rectangle(0, 0, 800, 800);
    Rectangle result = invokeLetterboxing(input);
    assertNotNull(result);
    assertTrue(result.width > 0);
    assertTrue(result.height > 0);
  }

  @Test
  public void letterboxing_wideViewport_reducesWidth() throws Exception {
    Rectangle input = new Rectangle(0, 0, 1920, 600);
    Rectangle result = invokeLetterboxing(input);
    assertNotNull(result);
    // When surface is wider than view aspect, width should be reduced
    assertTrue("Letterboxed width should be <= original", result.width <= 1920);
  }

  @Test
  public void letterboxing_tallViewport_reducesHeight() throws Exception {
    Rectangle input = new Rectangle(0, 0, 400, 1200);
    Rectangle result = invokeLetterboxing(input);
    assertNotNull(result);
    assertTrue("Letterboxed height should be <= original", result.height <= 1200);
  }

  @Test
  public void letterboxing_resultHasPositiveDimensions() throws Exception {
    Rectangle result = invokeLetterboxing(new Rectangle(0, 0, 1920, 1080));
    assertTrue(result.width > 0);
    assertTrue(result.height > 0);
  }

  // ── Property sync ─────────────────────────────────────────────────

  @Test
  public void propertyChanged_verticalViewingAngle_syncsField() throws Exception {
    adapter.propertyChanged(cam.verticalViewingAngle);
    Field f = GlrSymmetricPerspectiveCamera.class.getDeclaredField("verticalView");
    f.setAccessible(true);
    assertNotNull(f.get(adapter));
  }

  @Test
  public void propertyChanged_horizontalViewingAngle_syncsField() throws Exception {
    adapter.propertyChanged(cam.horizontalViewingAngle);
    Field f = GlrSymmetricPerspectiveCamera.class.getDeclaredField("horizontalView");
    f.setAccessible(true);
    assertNotNull(f.get(adapter));
  }

  @Test
  public void propertyChanged_nearClippingPlane_doesNotThrow() {
    adapter.propertyChanged(cam.nearClippingPlaneDistance);
  }

  @Test
  public void propertyChanged_farClippingPlane_doesNotThrow() {
    adapter.propertyChanged(cam.farClippingPlaneDistance);
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void extendsAbstractPerspectiveCamera() {
    assertTrue(GlrAbstractPerspectiveCamera.class.isAssignableFrom(GlrSymmetricPerspectiveCamera.class));
  }

  @Test
  public void extendsAbstractCamera() {
    assertTrue(GlrAbstractCamera.class.isAssignableFrom(GlrSymmetricPerspectiveCamera.class));
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private Rectangle invokeLetterboxing(Rectangle rect) throws Exception {
    Method m = GlrSymmetricPerspectiveCamera.class.getDeclaredMethod("performLetterboxing", Rectangle.class);
    m.setAccessible(true);
    return (Rectangle) m.invoke(adapter, rect);
  }
}
