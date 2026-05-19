package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Test;
import org.lgna.story.SThingMarker;

import static org.junit.Assert.*;

/**
 * Tests for {@link ObjectMarkerImp} (via {@link SThingMarker}) and
 * {@link OrthographicCameraImp} (no-arg construction).
 *
 * <p>Covers marker visual creation, setShowing, color/paint, opacity,
 * display enable/disable, and OrthographicCameraImp construction.
 */
public class MarkerImpCoverageTest {

  // ══════════════════════════════════════════════════════════════════════
  //  SThingMarker / ObjectMarkerImp
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void marker_constructsWithoutError() {
    SThingMarker marker = new SThingMarker();
    assertNotNull(marker);
    assertNotNull(marker.getImplementation());
  }

  @Test
  public void marker_abstractionRoundTrips() {
    SThingMarker marker = new SThingMarker();
    assertSame(marker, marker.getImplementation().getAbstraction());
  }

  @Test
  public void marker_sgVisualsNotNull() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    Visual[] visuals = imp.getSgVisuals();
    assertNotNull(visuals);
    assertTrue("Marker should have axis arrow visuals", visuals.length > 0);
  }

  @Test
  public void marker_sgCompositeNotNull() {
    SThingMarker marker = new SThingMarker();
    assertNotNull(marker.getImplementation().getSgComposite());
  }

  @Test
  public void marker_initiallyNotShowing() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    assertFalse("Markers start hidden", imp.isShowing());
  }

  @Test
  public void marker_setShowing_true() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    imp.setShowing(true);
    assertTrue(imp.isShowing());
  }

  @Test
  public void marker_setShowing_toggles() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    imp.setShowing(true);
    assertTrue(imp.isShowing());
    imp.setShowing(false);
    assertFalse(imp.isShowing());
  }

  @Test
  public void marker_setShowing_sameValue_noOp() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    imp.setShowing(false);
    imp.setShowing(false);
    assertFalse(imp.isShowing());
  }

  @Test
  public void marker_sgPaintAppearancesNotNull() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    SimpleAppearance[] appearances = imp.getSgPaintAppearances();
    assertNotNull(appearances);
    assertTrue(appearances.length > 0);
  }

  @Test
  public void marker_sgOpacityAppearancesNotNull() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    SimpleAppearance[] appearances = imp.getSgOpacityAppearances();
    assertNotNull(appearances);
    assertTrue(appearances.length > 0);
  }

  @Test
  public void marker_defaultColorIsCyan() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    Color4f color = imp.getMarkerColor();
    assertEquals(Color4f.CYAN, color);
  }

  @Test
  public void marker_setMarkerColor_roundTrips() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    imp.setMarkerColor(Color4f.RED);
    assertEquals(Color4f.RED, imp.getMarkerColor());
  }

  @Test
  public void marker_defaultOpacityIsHalf() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    assertEquals(0.5f, imp.getMarkerOpacity(), 1e-5f);
  }

  @Test
  public void marker_displayEnabled_defaultTrue() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    assertTrue(imp.getDisplayEnabled());
  }

  @Test
  public void marker_setDisplayVisuals_false_hidesVisuals() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    imp.setShowing(true);
    imp.setDisplayVisuals(false);
    assertFalse(imp.getDisplayEnabled());
  }

  @Test
  public void marker_resizers_isEmpty() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    assertEquals(0, imp.getResizers().length);
  }

  @Test
  public void marker_sgCompositeRegistryWorks() {
    SThingMarker marker = new SThingMarker();
    EntityImp found = EntityImp.getInstance(marker.getImplementation().getSgComposite());
    assertSame(marker.getImplementation(), found);
  }

  @Test
  public void marker_extendsMarkerImp() {
    assertTrue(MarkerImp.class.isAssignableFrom(ObjectMarkerImp.class));
  }

  @Test
  public void marker_localTransformIsIdentity() {
    SThingMarker marker = new SThingMarker();
    assertTrue(marker.getImplementation().getLocalTransformation().isIdentity());
  }

  @Test
  public void marker_applyAnimation_doesNotThrow() {
    SThingMarker marker = new SThingMarker();
    marker.getImplementation().applyAnimation();
  }

  // ══════════════════════════════════════════════════════════════════════
  //  OrthographicCameraImp
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void orthographicCamera_constructsWithoutError() {
    OrthographicCameraImp imp = new OrthographicCameraImp();
    assertNotNull(imp);
  }

  @Test
  public void orthographicCamera_abstractionIsNull() {
    OrthographicCameraImp imp = new OrthographicCameraImp();
    assertNull(imp.getAbstraction());
  }

  @Test
  public void orthographicCamera_sgCameraNotNull() {
    OrthographicCameraImp imp = new OrthographicCameraImp();
    assertNotNull(imp.getSgCamera());
  }

  @Test
  public void orthographicCamera_sgCameraIsOrthographic() {
    OrthographicCameraImp imp = new OrthographicCameraImp();
    assertTrue(imp.getSgCamera() instanceof edu.cmu.cs.dennisc.scenegraph.OrthographicCamera);
  }

  @Test
  public void orthographicCamera_extendsCameraImp() {
    assertTrue(CameraImp.class.isAssignableFrom(OrthographicCameraImp.class));
  }

  @Test
  public void orthographicCamera_sgCompositeNotNull() {
    OrthographicCameraImp imp = new OrthographicCameraImp();
    assertNotNull(imp.getSgComposite());
  }

  @Test
  public void orthographicCamera_localTransformIsIdentity() {
    OrthographicCameraImp imp = new OrthographicCameraImp();
    assertTrue(imp.getLocalTransformation().isIdentity());
  }
}
