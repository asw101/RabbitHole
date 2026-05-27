package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AngleInRadians;
import org.alice.math.immutable.ClippedZPlane;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CameraDefaultsTest {
  private static final double TOLERANCE = 1.0e-9;

  @Test
  public void orthographicCameraStartsWithClipPlaneDefaultsAndCopyingLayerArray() {
    OrthographicCamera camera = new OrthographicCamera();
    Layer layer = new Layer();

    camera.postRenderLayers.setValue(new Layer[]{layer});
    Layer[] stored = camera.postRenderLayers.getValue();

    assertEquals(0.125, camera.nearClippingPlaneDistance.getValue(), TOLERANCE);
    assertEquals(256.0, camera.farClippingPlaneDistance.getValue(), TOLERANCE);
    assertEquals(ClippedZPlane.DEFAULT, camera.picturePlane.getValue());
    assertEquals(1, stored.length);
    assertTrue(stored[0] instanceof Layer);
  }

  @Test
  public void symmetricPerspectiveCameraTracksConfiguredEffectiveAngles() {
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    AngleInRadians horizontal = new AngleInRadians(0.75);
    AngleInRadians vertical = new AngleInRadians(0.25);

    camera.setEffectiveHorizontalViewingAngle(horizontal);
    camera.setEffectiveVerticalViewingAngle(vertical);

    assertEquals(SymmetricPerspectiveCamera.DEFAULT_VERTICAL_VIEW_ANGLE, camera.verticalViewingAngle.getValue());
    assertTrue(camera.horizontalViewingAngle.getValue().isNaN());
    assertEquals(horizontal, camera.getEffectiveHorizontalViewingAngle());
    assertEquals(vertical, camera.getEffectiveVerticalViewingAngle());
  }
}
