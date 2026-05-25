package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import edu.cmu.cs.dennisc.scenegraph.OrthographicCamera;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector3;
import org.junit.Before;
import org.junit.Test;

import java.awt.Rectangle;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrOrthographicCamera} — orthographic projection matrix,
 * ray generation, letterboxing (identity), and propertyChanged coverage.
 */
public class GlrOrthographicCameraProjectionTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  private GlrOrthographicCamera adapter;
  private OrthographicCamera cam;

  @Before
  public void setUp() {
    cam = new OrthographicCamera();
    adapter = (GlrOrthographicCamera) AdapterFactory.getAdapterFor(cam);
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
  public void projectionMatrix_squareViewport() {
    Rectangle viewport = new Rectangle(0, 0, 600, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(viewport);
    assertNotNull(m);
  }

  @Test
  public void projectionMatrix_wideViewport() {
    Rectangle viewport = new Rectangle(0, 0, 1920, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(viewport);
    assertNotNull(m);
  }

  // ── Ray generation ────────────────────────────────────────────────

  @Test
  public void rayAtCenter_pointsNegativeZ() {
    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    Ray ray = adapter.getRayAtViewportPixel(400, 300, viewport);
    assertNotNull(ray);
    assertEquals("Ortho ray should point in -Z", -1.0, ray.direction().z(), 0.01);
    assertEquals(0.0, ray.direction().x(), 0.01);
    assertEquals(0.0, ray.direction().y(), 0.01);
  }

  @Test
  public void rayAtCorner_sameDirection() {
    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    Ray center = adapter.getRayAtViewportPixel(400, 300, viewport);
    Ray corner = adapter.getRayAtViewportPixel(0, 0, viewport);
    // Orthographic rays all point the same direction
    assertEquals(center.direction().x(), corner.direction().x(), 0.001);
    assertEquals(center.direction().y(), corner.direction().y(), 0.001);
    assertEquals(center.direction().z(), corner.direction().z(), 0.001);
  }

  @Test
  public void rayAtCorner_differentOriginFromCenter() {
    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    Ray center = adapter.getRayAtViewportPixel(400, 300, viewport);
    Ray corner = adapter.getRayAtViewportPixel(0, 0, viewport);
    // Origins should differ (different positions on the picture plane)
    assertFalse("Corner and center should have different origins",
        center.origin().x() == corner.origin().x()
            && center.origin().y() == corner.origin().y());
  }

  @Test
  public void rayAtDifferentPixels_originChanges() {
    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    Ray r1 = adapter.getRayAtViewportPixel(100, 100, viewport);
    Ray r2 = adapter.getRayAtViewportPixel(700, 500, viewport);
    assertNotEquals("X origins should differ", r1.origin().x(), r2.origin().x(), 0.001);
  }

  // ── Letterboxing (identity for orthographic) ──────────────────────

  @Test
  public void letterboxing_returnsInputUnchanged() throws Exception {
    Method m = GlrOrthographicCamera.class.getDeclaredMethod("performLetterboxing", Rectangle.class);
    m.setAccessible(true);
    Rectangle input = new Rectangle(0, 0, 800, 600);
    Rectangle result = (Rectangle) m.invoke(adapter, input);
    assertSame("Orthographic letterboxing should return same object", input, result);
  }

  @Test
  public void letterboxing_wideViewport_stillReturnsSame() throws Exception {
    Method m = GlrOrthographicCamera.class.getDeclaredMethod("performLetterboxing", Rectangle.class);
    m.setAccessible(true);
    Rectangle input = new Rectangle(0, 0, 1920, 100);
    Rectangle result = (Rectangle) m.invoke(adapter, input);
    assertSame(input, result);
  }

  // ── propertyChanged ───────────────────────────────────────────────

  @Test
  public void propertyChanged_picturePlane_isSkipped() {
    // picturePlane property is explicitly skipped — should not throw
    adapter.propertyChanged(cam.picturePlane);
  }

  @Test
  public void propertyChanged_nearClippingPlane_delegatesToSuper() {
    adapter.propertyChanged(cam.nearClippingPlaneDistance);
  }

  @Test
  public void propertyChanged_farClippingPlane_delegatesToSuper() {
    adapter.propertyChanged(cam.farClippingPlaneDistance);
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void extendsAbstractNearPlaneAndFarPlaneCamera() {
    assertTrue(GlrAbstractNearPlaneAndFarPlaneCamera.class.isAssignableFrom(GlrOrthographicCamera.class));
  }

  @Test
  public void extendsAbstractCamera() {
    assertTrue(GlrAbstractCamera.class.isAssignableFrom(GlrOrthographicCamera.class));
  }

  @Test
  public void ownerIsSetAfterCreation() {
    assertSame(cam, adapter.getOwner());
  }
}
