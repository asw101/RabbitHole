package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.Layer;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import org.alice.math.immutable.AngleInRevolutions;
import org.junit.Before;
import org.junit.Test;
import org.lgna.story.SBox;
import org.lgna.story.SCamera;

import static org.junit.Assert.*;

/**
 * Headless tests for SCamera and CameraImp (SymmetricPerspectiveCameraImp).
 * SCamera creates its implementation internally — no rendering context needed.
 */
public class CameraImpTest {

  private SCamera camera;
  private SymmetricPerspectiveCameraImp imp;

  @Before
  public void setUp() {
    camera = new SCamera();
    imp = camera.getImplementation();
  }

  // ── Construction ──────────────────────────────────────

  @Test
  public void cameraConstructsNonNull() {
    assertNotNull(camera);
    assertNotNull(imp);
  }

  @Test
  public void getImplementation_returnsSameInstance() {
    assertSame(imp, camera.getImplementation());
  }

  @Test
  public void sgCompositeIsNotNull() {
    assertNotNull(imp.getSgComposite());
  }

  @Test
  public void sgCameraIsNotNull() {
    assertNotNull(imp.getSgCamera());
  }

  @Test
  public void sgCameraIsSymmetricPerspective() {
    assertTrue(imp.getSgCamera() instanceof SymmetricPerspectiveCamera);
  }

  // ── Static defaults ───────────────────────────────────

  @Test
  public void defaultOrientationIsNotNull() {
    assertNotNull(SCamera.DEFAULT_ORIENTATION);
  }

  @Test
  public void defaultPositionIsNotNull() {
    assertNotNull(SCamera.DEFAULT_POSITION);
  }

  // ── Far clipping plane ────────────────────────────────

  @Test
  public void farClippingPlane_getReturnsDefault() {
    Double far = camera.getFarClippingPlaneDistance();
    assertNotNull(far);
  }

  @Test
  public void farClippingPlane_setThenGet() {
    camera.setFarClippingPlaneDistance(500.0);
    assertEquals(500.0, camera.getFarClippingPlaneDistance(), 0.001);
  }

  @Test
  public void farClippingPlane_setToSmallValue() {
    camera.setFarClippingPlaneDistance(1.0);
    assertEquals(1.0, camera.getFarClippingPlaneDistance(), 0.001);
  }

  // ── Near clipping plane ───────────────────────────────

  @Test
  public void nearClippingPlane_getReturnsDefault() {
    Double near = camera.getNearClippingPlaneDistance();
    assertNotNull(near);
  }

  @Test
  public void nearClippingPlane_setThenGet() {
    camera.setNearClippingPlaneDistance(0.5);
    assertEquals(0.5, camera.getNearClippingPlaneDistance(), 0.001);
  }

  // ── Horizontal viewing angle ──────────────────────────

  @Test
  public void horizontalViewingAngle_setClearsVerticalAndStoresValue() {
    imp.getSgCamera().verticalViewingAngle.setValue(new AngleInRevolutions(0.125));

    camera.setHorizontalViewingAngle(0.25);

    assertTrue(Double.isNaN(imp.getSgCamera().verticalViewingAngle.getValue().getAsRevolutions()));
    assertEquals(0.25, imp.getSgCamera().horizontalViewingAngle.getValue().getAsRevolutions(), 0.001);
  }

  @Test
  public void horizontalViewingAngleGetterUsesEffectiveCameraValue() {
    imp.getSgCamera().setEffectiveHorizontalViewingAngle(new AngleInRevolutions(0.25));

    assertEquals(0.25, camera.getHorizontalViewingAngle(), 0.001);
  }

  @Test
  public void horizontalViewingAngle_nullIsNoOp() {
    camera.setHorizontalViewingAngle(null);
    // Null input is a no-op per the implementation
  }

  // ── Vertical viewing angle ────────────────────────────

  @Test
  public void verticalViewingAngle_setClearsHorizontalAndStoresValue() {
    imp.getSgCamera().horizontalViewingAngle.setValue(new AngleInRevolutions(0.25));

    camera.setVerticalViewingAngle(0.125);

    assertTrue(Double.isNaN(imp.getSgCamera().horizontalViewingAngle.getValue().getAsRevolutions()));
    assertEquals(0.125, imp.getSgCamera().verticalViewingAngle.getValue().getAsRevolutions(), 0.001);
  }

  @Test
  public void verticalViewingAngleGetterUsesEffectiveCameraValue() {
    imp.getSgCamera().setEffectiveVerticalViewingAngle(new AngleInRevolutions(0.125));

    assertEquals(0.125, camera.getVerticalViewingAngle(), 0.001);
  }

  @Test
  public void verticalViewingAngle_nullIsNoOp() {
    camera.setVerticalViewingAngle(null);
    // Null input is a no-op per the implementation
  }

  // ── Horizontal overrides vertical ─────────────────────

  @Test
  public void setHorizontalClearsVertical_noThrow() {
    camera.setVerticalViewingAngle(0.125);
    camera.setHorizontalViewingAngle(0.25);
    // Exercises the code path that clears vertical when horizontal is set
  }

  @Test
  public void setVerticalClearsHorizontal_noThrow() {
    camera.setHorizontalViewingAngle(0.25);
    camera.setVerticalViewingAngle(0.125);
    // Exercises the code path that clears horizontal when vertical is set
  }

  // ── VR Hands ──────────────────────────────────────────

  @Test
  public void leftHandIsNotNull() {
    assertNotNull(camera.getLeftHand());
  }

  @Test
  public void rightHandIsNotNull() {
    assertNotNull(camera.getRightHand());
  }

  @Test
  public void leftAndRightHandAreDifferent() {
    assertNotSame(camera.getLeftHand(), camera.getRightHand());
  }

  // ── CameraImp.getPostRenderLayer ──────────────────────

  @Test
  public void getPostRenderLayer_createsLayerIfNeeded() {
    Layer layer = imp.getPostRenderLayer();
    assertNotNull(layer);
  }

  @Test
  public void getPostRenderLayer_returnsSameLayerOnSecondCall() {
    Layer first = imp.getPostRenderLayer();
    Layer second = imp.getPostRenderLayer();
    assertSame(first, second);
  }

  // ── setVehicle ────────────────────────────────────────

  @Test
  public void setVehicle_nullDoesNotThrow() {
    camera.setVehicle(null);
    assertNull(imp.getVehicle());
  }

  @Test
  public void setVehicle_assignsImplementationVehicle() {
    SBox vehicle = new SBox();

    camera.setVehicle(vehicle);

    assertSame(vehicle.getImplementation(), imp.getVehicle());
  }

  // ── Abstraction ───────────────────────────────────────

  @Test
  public void getAbstraction_returnsCamera() {
    assertSame(camera, imp.getAbstraction());
  }
}
