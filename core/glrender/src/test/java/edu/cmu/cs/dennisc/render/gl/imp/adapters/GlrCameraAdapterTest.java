package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.OrthographicCamera;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Ray;
import org.junit.Test;

import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests for camera adapter classes — property pipeline, letterboxing,
 * projection matrix, and ray construction.
 * Uses AdapterFactory to create properly initialized adapters.
 */
public class GlrCameraAdapterTest {

  // ═══════════════════════════════════════════════════════════════════
  // GlrSymmetricPerspectiveCamera
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void perspectiveCamera_adapterCreation() {
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    GlrAbstractCamera<?> adapter = AdapterFactory.getAdapterFor(cam);
    assertNotNull(adapter);
    assertTrue(adapter instanceof GlrSymmetricPerspectiveCamera);
  }

  @Test
  public void perspectiveCamera_ownerSet() {
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    GlrAbstractCamera<?> adapter = AdapterFactory.getAdapterFor(cam);
    assertSame(cam, adapter.getOwner());
  }

  @Test
  public void perspectiveCamera_propertyChanged_verticalView() throws Exception {
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    GlrSymmetricPerspectiveCamera adapter =
        (GlrSymmetricPerspectiveCamera) AdapterFactory.getAdapterFor(cam);
    // Trigger property sync
    adapter.propertyChanged(cam.verticalViewingAngle);
    Field f = GlrSymmetricPerspectiveCamera.class.getDeclaredField("verticalView");
    f.setAccessible(true);
    assertNotNull(f.get(adapter));
  }

  @Test
  public void perspectiveCamera_propertyChanged_horizontalView() throws Exception {
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    GlrSymmetricPerspectiveCamera adapter =
        (GlrSymmetricPerspectiveCamera) AdapterFactory.getAdapterFor(cam);
    adapter.propertyChanged(cam.horizontalViewingAngle);
    Field f = GlrSymmetricPerspectiveCamera.class.getDeclaredField("horizontalView");
    f.setAccessible(true);
    assertNotNull(f.get(adapter));
  }

  @Test
  public void perspectiveCamera_getActualProjectionMatrix_afterPropertySync() throws Exception {
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    GlrSymmetricPerspectiveCamera adapter =
        (GlrSymmetricPerspectiveCamera) AdapterFactory.getAdapterFor(cam);
    adapter.propertyChanged(cam.verticalViewingAngle);
    adapter.propertyChanged(cam.horizontalViewingAngle);
    adapter.propertyChanged(cam.nearClippingPlaneDistance);
    adapter.propertyChanged(cam.farClippingPlaneDistance);

    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    Matrix4x4 projection = adapter.getActualProjectionMatrix(viewport);
    assertNotNull(projection);
  }

  @Test
  public void perspectiveCamera_getRayAtViewportPixel() throws Exception {
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    GlrSymmetricPerspectiveCamera adapter =
        (GlrSymmetricPerspectiveCamera) AdapterFactory.getAdapterFor(cam);
    adapter.propertyChanged(cam.verticalViewingAngle);
    adapter.propertyChanged(cam.horizontalViewingAngle);
    adapter.propertyChanged(cam.nearClippingPlaneDistance);
    adapter.propertyChanged(cam.farClippingPlaneDistance);

    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    Ray ray = adapter.getRayAtViewportPixel(400, 300, viewport);
    assertNotNull(ray);
    assertNotNull(ray.origin());
    assertNotNull(ray.direction());
  }

  @Test
  public void perspectiveCamera_letterboxing_squareViewport() throws Exception {
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    GlrSymmetricPerspectiveCamera adapter =
        (GlrSymmetricPerspectiveCamera) AdapterFactory.getAdapterFor(cam);
    adapter.propertyChanged(cam.verticalViewingAngle);
    adapter.propertyChanged(cam.horizontalViewingAngle);

    Method performLetterboxing = GlrSymmetricPerspectiveCamera.class
        .getDeclaredMethod("performLetterboxing", Rectangle.class);
    performLetterboxing.setAccessible(true);

    Rectangle result = (Rectangle) performLetterboxing.invoke(adapter, new Rectangle(0, 0, 800, 800));
    assertNotNull(result);
    assertTrue(result.width > 0);
    assertTrue(result.height > 0);
  }

  @Test
  public void perspectiveCamera_letterboxing_wideViewport() throws Exception {
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    GlrSymmetricPerspectiveCamera adapter =
        (GlrSymmetricPerspectiveCamera) AdapterFactory.getAdapterFor(cam);
    adapter.propertyChanged(cam.verticalViewingAngle);
    adapter.propertyChanged(cam.horizontalViewingAngle);

    Method performLetterboxing = GlrSymmetricPerspectiveCamera.class
        .getDeclaredMethod("performLetterboxing", Rectangle.class);
    performLetterboxing.setAccessible(true);

    Rectangle result = (Rectangle) performLetterboxing.invoke(adapter, new Rectangle(0, 0, 1920, 600));
    assertNotNull(result);
  }

  // ═══════════════════════════════════════════════════════════════════
  // GlrOrthographicCamera
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void orthographicCamera_adapterCreation() {
    OrthographicCamera cam = new OrthographicCamera();
    GlrAbstractCamera<?> adapter = AdapterFactory.getAdapterFor(cam);
    assertNotNull(adapter);
    assertTrue(adapter instanceof GlrOrthographicCamera);
  }

  @Test
  public void orthographicCamera_ownerSet() {
    OrthographicCamera cam = new OrthographicCamera();
    GlrAbstractCamera<?> adapter = AdapterFactory.getAdapterFor(cam);
    assertSame(cam, adapter.getOwner());
  }

  @Test
  public void orthographicCamera_letterboxing_returnsInput() throws Exception {
    OrthographicCamera cam = new OrthographicCamera();
    GlrOrthographicCamera adapter =
        (GlrOrthographicCamera) AdapterFactory.getAdapterFor(cam);

    Method performLetterboxing = GlrOrthographicCamera.class
        .getDeclaredMethod("performLetterboxing", Rectangle.class);
    performLetterboxing.setAccessible(true);

    Rectangle input = new Rectangle(0, 0, 800, 600);
    Rectangle result = (Rectangle) performLetterboxing.invoke(adapter, input);
    // Orthographic letterboxing returns input unchanged
    assertSame(input, result);
  }

  @Test
  public void orthographicCamera_propertyChanged_nearFarPlane() {
    OrthographicCamera cam = new OrthographicCamera();
    GlrOrthographicCamera adapter =
        (GlrOrthographicCamera) AdapterFactory.getAdapterFor(cam);
    // Should not throw — delegated to super for non-picturePlane properties
    adapter.propertyChanged(cam.nearClippingPlaneDistance);
    adapter.propertyChanged(cam.farClippingPlaneDistance);
  }

  @Test
  public void orthographicCamera_propertyChanged_picturePlane_noOp() {
    OrthographicCamera cam = new OrthographicCamera();
    GlrOrthographicCamera adapter =
        (GlrOrthographicCamera) AdapterFactory.getAdapterFor(cam);
    // picturePlane property is explicitly skipped in propertyChanged
    adapter.propertyChanged(cam.picturePlane);
  }

  // ═══════════════════════════════════════════════════════════════════
  // Hierarchy checks
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void perspectiveCamera_extendsAbstractPerspectiveCamera() {
    assertTrue(GlrAbstractPerspectiveCamera.class.isAssignableFrom(GlrSymmetricPerspectiveCamera.class));
  }

  @Test
  public void orthographicCamera_extendsAbstractNearPlaneAndFarPlaneCamera() {
    assertTrue(GlrAbstractNearPlaneAndFarPlaneCamera.class.isAssignableFrom(GlrOrthographicCamera.class));
  }

  @Test
  public void bothCameras_extendGlrAbstractCamera() {
    assertTrue(GlrAbstractCamera.class.isAssignableFrom(GlrSymmetricPerspectiveCamera.class));
    assertTrue(GlrAbstractCamera.class.isAssignableFrom(GlrOrthographicCamera.class));
  }
}
