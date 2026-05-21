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

/** Headless-safe characterization tests for MouseRelativeObjectDragManipulator. */
public class MouseRelativeObjectDragManipulatorTest {

  @Test
  public void constructionDoesNotThrow() {
    assertNotNull(new MouseRelativeObjectDragManipulator());
  }

  @Test
  public void desiredCameraViewIsPickCamera() {
    assertEquals(CameraView.PICK_CAMERA, new MouseRelativeObjectDragManipulator().getDesiredCameraView());
  }

  @Test
  public void setDesiredCameraViewIsNoOp() {
    MouseRelativeObjectDragManipulator m = new MouseRelativeObjectDragManipulator();
    m.setDesiredCameraView(CameraView.TOP_LEFT);
    assertEquals(CameraView.PICK_CAMERA, m.getDesiredCameraView());
  }

  @Test
  public void undoRedoDescriptionMatchesSource() {
    assertEquals("Object Move", new MouseRelativeObjectDragManipulator().getUndoRedoDescription());
  }

  @Test
  public void cameraDefaultsToNull() {
    assertNull(new MouseRelativeObjectDragManipulator().getCamera());
  }

  @Test
  public void setCameraRoundTrips() {
    MouseRelativeObjectDragManipulator m = new MouseRelativeObjectDragManipulator();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    m.setCamera(camera);
    assertSame(camera, m.getCamera());
  }

  @Test
  public void setCameraWithParentDoesNotSetManipulatedTransformable() {
    // MouseRelativeObjectDragManipulator.setCamera does not call setManipulatedTransformable
    MouseRelativeObjectDragManipulator m = new MouseRelativeObjectDragManipulator();
    Transformable parent = new Transformable();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.setParent(parent);

    m.setCamera(camera);

    assertSame(camera, m.getCamera());
    // No parent tracking in setCamera for this manipulator
    assertNull(m.getManipulatedTransformable());
  }

  @Test
  public void onscreenRenderTargetDefaultsToNull() {
    assertNull(new MouseRelativeObjectDragManipulator().getOnscreenRenderTarget());
  }

  @Test
  public void onscreenRenderTargetRoundTrips() {
    MouseRelativeObjectDragManipulator m = new MouseRelativeObjectDragManipulator();
    OnscreenRenderTarget target = dummyTarget();
    m.setOnscreenRenderTarget(target);
    assertSame(target, m.getOnscreenRenderTarget());
  }

  @Test
  public void initializeEventMessagesCreatesTranslateMainEvent() {
    MouseRelativeObjectDragManipulator m = new MouseRelativeObjectDragManipulator();
    m.initializeEventMessages();
    assertEquals(ManipulationEvent.EventType.Translate, m.getMainManipulationEvent().getType());
  }

  @Test
  public void initializeEventMessagesAddsTranslateDirectionEvents() {
    MouseRelativeObjectDragManipulator m = new MouseRelativeObjectDragManipulator();
    m.initializeEventMessages();
    int count = 0;
    for (ManipulationEvent e : m.getManipulationEvents()) {
      assertEquals(ManipulationEvent.EventType.Translate, e.getType());
      count++;
    }
    assertEquals(4, count);
  }

  @Test
  public void doStartManipulatorReturnsFalseWithEmptyInputState() {
    assertFalse(new MouseRelativeObjectDragManipulator().doStartManipulator(new InputState()));
  }

  @Test
  public void doClickManipulatorIsNoOp() {
    MouseRelativeObjectDragManipulator m = new MouseRelativeObjectDragManipulator();
    m.doClickManipulator(new InputState(), new InputState());
  }

  @Test
  public void doTimeUpdateManipulatorIsNoOp() {
    MouseRelativeObjectDragManipulator m = new MouseRelativeObjectDragManipulator();
    m.doTimeUpdateManipulator(0.1, new InputState());
  }

  @Test
  public void doDataUpdateManipulatorWithEqualLocationsIsNoOp() {
    MouseRelativeObjectDragManipulator m = new MouseRelativeObjectDragManipulator();
    InputState s = new InputState();
    m.doDataUpdateManipulator(s, s);
  }

  @Test
  public void doDataUpdateManipulatorWithNullTransformableIsNoOp() {
    MouseRelativeObjectDragManipulator m = new MouseRelativeObjectDragManipulator();
    m.doDataUpdateManipulator(new InputState(), new InputState());
  }

  @Test
  public void manipulatorConstructionHasNullManipulatedTransformable() {
    assertNull(new MouseRelativeObjectDragManipulator().getManipulatedTransformable());
  }

  private static OnscreenRenderTarget dummyTarget() {
    return (OnscreenRenderTarget) Proxy.newProxyInstance(
        MouseRelativeObjectDragManipulatorTest.class.getClassLoader(),
        new Class[]{OnscreenRenderTarget.class},
        (proxy, method, args) -> null);
  }
}
