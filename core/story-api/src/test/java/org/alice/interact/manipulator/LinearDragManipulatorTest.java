package org.alice.interact.manipulator;

import edu.cmu.cs.dennisc.render.OnscreenRenderTarget;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.DragAdapter.CameraView;
import org.alice.interact.InputState;
import org.alice.interact.event.ManipulationEvent;
import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

/** Headless-safe configuration tests for LinearDragManipulator. */
public class LinearDragManipulatorTest {

  @Test
  public void desiredCameraViewIsAlwaysPickCamera() {
    assertEquals(CameraView.PICK_CAMERA, new LinearDragManipulator().getDesiredCameraView());
  }

  @Test
  public void setDesiredCameraViewDoesNotChangeReturnedValue() {
    LinearDragManipulator manipulator = new LinearDragManipulator();
    manipulator.setDesiredCameraView(CameraView.TOP_LEFT);
    assertEquals(CameraView.PICK_CAMERA, manipulator.getDesiredCameraView());
  }

  @Test
  public void undoRedoDescriptionMatchesSourceText() {
    assertEquals("LinearDrag - Object Move", new LinearDragManipulator().getUndoRedoDescription());
  }

  @Test
  public void onscreenRenderTargetDefaultsToNull() {
    assertNull(new LinearDragManipulator().getOnscreenRenderTarget());
  }

  @Test
  public void onscreenRenderTargetRoundTripsThroughSetter() {
    LinearDragManipulator manipulator = new LinearDragManipulator();
    OnscreenRenderTarget target = dummyOnscreenRenderTarget();
    manipulator.setOnscreenRenderTarget(target);
    assertSame(target, manipulator.getOnscreenRenderTarget());
  }

  @Test
  public void setCameraTracksParentTransformable() {
    LinearDragManipulator manipulator = new LinearDragManipulator();
    Transformable cameraParent = new Transformable();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.setParent(cameraParent);

    manipulator.setCamera(camera);

    assertSame(camera, manipulator.getCamera());
    assertSame(cameraParent, manipulator.getManipulatedTransformable());
  }

  @Test
  public void settingCameraToNullDoesNotClearExistingManipulatedTransformable() {
    LinearDragManipulator manipulator = new LinearDragManipulator();
    Transformable cameraParent = new Transformable();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.setParent(cameraParent);
    manipulator.setCamera(camera);

    manipulator.setCamera(null);

    assertSame(cameraParent, manipulator.getManipulatedTransformable());
  }

  @Test
  public void initializeEventMessagesCreatesTranslateMainEvent() {
    LinearDragManipulator manipulator = new LinearDragManipulator();
    manipulator.initializeEventMessages();
    assertEquals(ManipulationEvent.EventType.Translate, manipulator.getMainManipulationEvent().getType());
  }

  @Test
  public void startManipulatorReturnsFalseWithDefaultInputState() {
    assertFalse(new LinearDragManipulator().doStartManipulator(new InputState()));
  }

  @Test
  public void emptyHandleStateProducesNoSecondaryManipulationEvents() {
    LinearDragManipulator manipulator = new LinearDragManipulator();
    int count = 0;
    for (ManipulationEvent ignored : manipulator.getManipulationEvents()) {
      count++;
    }
    assertEquals(0, count);
  }

  private static OnscreenRenderTarget dummyOnscreenRenderTarget() {
    return (OnscreenRenderTarget) Proxy.newProxyInstance(
        LinearDragManipulatorTest.class.getClassLoader(),
        new Class[]{OnscreenRenderTarget.class},
        (proxy, method, args) -> null);
  }
}
