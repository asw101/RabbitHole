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

/** Headless-safe configuration tests for ObjectRotateDragManipulator. */
public class ObjectRotateManipulatorTest {

  @Test
  public void desiredCameraViewIsAlwaysPickCamera() {
    assertEquals(CameraView.PICK_CAMERA, new ObjectRotateDragManipulator().getDesiredCameraView());
  }

  @Test
  public void setDesiredCameraViewDoesNotChangeReturnedValue() {
    ObjectRotateDragManipulator manipulator = new ObjectRotateDragManipulator();
    manipulator.setDesiredCameraView(CameraView.TOP_LEFT);
    assertEquals(CameraView.PICK_CAMERA, manipulator.getDesiredCameraView());
  }

  @Test
  public void undoRedoDescriptionMatchesSourceText() {
    assertEquals("Object Rotate", new ObjectRotateDragManipulator().getUndoRedoDescription());
  }

  @Test
  public void onscreenRenderTargetDefaultsToNull() {
    assertNull(new ObjectRotateDragManipulator().getOnscreenRenderTarget());
  }

  @Test
  public void onscreenRenderTargetRoundTripsThroughSetter() {
    ObjectRotateDragManipulator manipulator = new ObjectRotateDragManipulator();
    OnscreenRenderTarget target = dummyOnscreenRenderTarget();
    manipulator.setOnscreenRenderTarget(target);
    assertSame(target, manipulator.getOnscreenRenderTarget());
  }

  @Test
  public void setCameraTracksMovableParent() {
    ObjectRotateDragManipulator manipulator = new ObjectRotateDragManipulator();
    Transformable cameraParent = new Transformable();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.setParent(cameraParent);

    manipulator.setCamera(camera);

    assertSame(camera, manipulator.getCamera());
    assertSame(cameraParent, manipulator.getManipulatedTransformable());
  }

  @Test
  public void settingCameraToNullDoesNotClearExistingManipulatedTransformable() {
    ObjectRotateDragManipulator manipulator = new ObjectRotateDragManipulator();
    Transformable cameraParent = new Transformable();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.setParent(cameraParent);
    manipulator.setCamera(camera);

    manipulator.setCamera(null);

    assertSame(cameraParent, manipulator.getManipulatedTransformable());
  }

  @Test
  public void initializeEventMessagesCreatesRotateMainEvent() {
    ObjectRotateDragManipulator manipulator = new ObjectRotateDragManipulator();
    manipulator.initializeEventMessages();
    assertEquals(ManipulationEvent.EventType.Rotate, manipulator.getMainManipulationEvent().getType());
  }

  @Test
  public void doStartManipulatorReturnsFalseWithDefaultInputState() {
    assertFalse(new ObjectRotateDragManipulator().doStartManipulator(new InputState()));
  }

  @Test
  public void doClickAndTimeUpdateAreNoOps() {
    ObjectRotateDragManipulator manipulator = new ObjectRotateDragManipulator();
    manipulator.doClickManipulator(new InputState(), new InputState());
    manipulator.doTimeUpdateManipulator(0.1, new InputState());
  }

  private static OnscreenRenderTarget dummyOnscreenRenderTarget() {
    return (OnscreenRenderTarget) Proxy.newProxyInstance(
        ObjectRotateManipulatorTest.class.getClassLoader(),
        new Class[]{OnscreenRenderTarget.class},
        (proxy, method, args) -> null);
  }
}
