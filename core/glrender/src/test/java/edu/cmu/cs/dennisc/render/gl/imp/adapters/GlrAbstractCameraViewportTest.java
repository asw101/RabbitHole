package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.OrthographicCamera;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import org.junit.Test;

import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrAbstractCamera} — viewport calculation, letterboxing flag,
 * background/layer property sync. Exercises the abstract camera via concrete subclasses.
 */
public class GlrAbstractCameraViewportTest {

  // ── Viewport calculation ──────────────────────────────────────────

  @Test
  public void getActualViewport_defaultLetterboxed_perspective() {
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    GlrSymmetricPerspectiveCamera adapter =
        (GlrSymmetricPerspectiveCamera) AdapterFactory.getAdapterFor(cam);
    adapter.propertyChanged(cam.verticalViewingAngle);
    adapter.propertyChanged(cam.horizontalViewingAngle);
    Rectangle vp = adapter.getActualViewport(800, 600);
    assertNotNull(vp);
    assertTrue(vp.width > 0);
    assertTrue(vp.height > 0);
  }

  @Test
  public void getActualViewport_defaultLetterboxed_orthographic() {
    OrthographicCamera cam = new OrthographicCamera();
    GlrOrthographicCamera adapter =
        (GlrOrthographicCamera) AdapterFactory.getAdapterFor(cam);
    Rectangle vp = adapter.getActualViewport(800, 600);
    assertNotNull(vp);
    assertEquals(800, vp.width);
    assertEquals(600, vp.height);
  }

  @Test
  public void getActualViewport_wideScreen() {
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    GlrSymmetricPerspectiveCamera adapter =
        (GlrSymmetricPerspectiveCamera) AdapterFactory.getAdapterFor(cam);
    adapter.propertyChanged(cam.verticalViewingAngle);
    adapter.propertyChanged(cam.horizontalViewingAngle);
    Rectangle vp = adapter.getActualViewport(1920, 1080);
    assertNotNull(vp);
    assertTrue(vp.width > 0);
    assertTrue(vp.height > 0);
  }

  // ── Letterbox flag ────────────────────────────────────────────────

  @Test
  public void isLetterboxed_defaultTrue() {
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    GlrAbstractCamera<?> adapter = AdapterFactory.getAdapterFor(cam);
    assertTrue(adapter.isLetterboxed());
  }

  @Test
  public void setIsLetterboxed_false_disablesLetterboxing() {
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    GlrAbstractCamera<?> adapter = AdapterFactory.getAdapterFor(cam);
    adapter.setIsLetterboxed(false);
    assertFalse(adapter.isLetterboxed());
  }

  @Test
  public void setIsLetterboxed_toggle() {
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    GlrAbstractCamera<?> adapter = AdapterFactory.getAdapterFor(cam);
    adapter.setIsLetterboxed(false);
    assertFalse(adapter.isLetterboxed());
    adapter.setIsLetterboxed(true);
    assertTrue(adapter.isLetterboxed());
  }

  // ── Viewport without letterboxing ─────────────────────────────────

  @Test
  public void getActualViewport_notLetterboxed_fullSurface() {
    OrthographicCamera cam = new OrthographicCamera();
    GlrOrthographicCamera adapter =
        (GlrOrthographicCamera) AdapterFactory.getAdapterFor(cam);
    adapter.setIsLetterboxed(false);
    Rectangle vp = adapter.getActualViewport(1920, 1080);
    // Without letterboxing, viewport should match surface dimensions
    assertEquals(1920, vp.width);
    assertEquals(1080, vp.height);
  }

  // ── propertyChanged for background ────────────────────────────────

  @Test
  public void propertyChanged_background_doesNotThrow() {
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    GlrAbstractCamera<?> adapter = AdapterFactory.getAdapterFor(cam);
    adapter.propertyChanged(cam.background);
  }

  // ── Specified viewport ────────────────────────────────────────────

  @Test
  public void specifiedViewport_defaultNull() throws Exception {
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    GlrAbstractCamera<?> adapter = AdapterFactory.getAdapterFor(cam);
    Field f = GlrAbstractCamera.class.getDeclaredField("specifiedViewport");
    f.setAccessible(true);
    assertNull(f.get(adapter));
  }

  // ── Type checks ───────────────────────────────────────────────────

  @Test
  public void perspectiveCam_isAbstractCamera() {
    assertTrue(GlrAbstractCamera.class.isAssignableFrom(GlrSymmetricPerspectiveCamera.class));
  }

  @Test
  public void orthographicCam_isAbstractCamera() {
    assertTrue(GlrAbstractCamera.class.isAssignableFrom(GlrOrthographicCamera.class));
  }
}
