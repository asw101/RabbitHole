package org.alice.interact.manipulator;

import edu.cmu.cs.dennisc.render.OnscreenRenderTarget;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.DragAdapter.CameraView;
import org.alice.interact.InputState;
import org.alice.interact.MovementDirection;
import org.alice.interact.event.ManipulationEvent;
import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

/** Headless-safe characterization tests for HandlelessObjectRotateDragManipulator. */
public class HandlelessObjectRotateDragManipulatorTest {

  @Test
  public void defaultConstructionDoesNotThrow() {
    assertNotNull(new HandlelessObjectRotateDragManipulator());
  }

  @Test
  public void oneArgConstructionWithYAxisDoesNotThrow() {
    assertNotNull(new HandlelessObjectRotateDragManipulator(MovementDirection.UP));
  }

  @Test
  public void oneArgConstructionWithXAxisDoesNotThrow() {
    assertNotNull(new HandlelessObjectRotateDragManipulator(MovementDirection.LEFT));
  }

  @Test
  public void desiredCameraViewIsActiveView() {
    assertEquals(CameraView.ACTIVE_VIEW, new HandlelessObjectRotateDragManipulator().getDesiredCameraView());
  }

  @Test
  public void setDesiredCameraViewIsNoOp() {
    HandlelessObjectRotateDragManipulator m = new HandlelessObjectRotateDragManipulator();
    m.setDesiredCameraView(CameraView.PICK_CAMERA);
    assertEquals(CameraView.ACTIVE_VIEW, m.getDesiredCameraView());
  }

  @Test
  public void undoRedoDescriptionMatchesSource() {
    assertEquals("Object Rotate", new HandlelessObjectRotateDragManipulator().getUndoRedoDescription());
  }

  @Test
  public void cameraDefaultsToNull() {
    assertNull(new HandlelessObjectRotateDragManipulator().getCamera());
  }

  @Test
  public void setCameraRoundTrips() {
    HandlelessObjectRotateDragManipulator m = new HandlelessObjectRotateDragManipulator();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    m.setCamera(camera);
    assertSame(camera, m.getCamera());
  }

  @Test
  public void setCameraTracksMovableParent() {
    HandlelessObjectRotateDragManipulator m = new HandlelessObjectRotateDragManipulator();
    Transformable parent = new Transformable();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.setParent(parent);

    m.setCamera(camera);

    assertSame(camera, m.getCamera());
    assertSame(parent, m.getManipulatedTransformable());
  }

  @Test
  public void setCameraToNullKeepsExistingTransformable() {
    HandlelessObjectRotateDragManipulator m = new HandlelessObjectRotateDragManipulator();
    Transformable parent = new Transformable();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.setParent(parent);
    m.setCamera(camera);

    m.setCamera(null);

    assertSame(parent, m.getManipulatedTransformable());
  }

  @Test
  public void onscreenRenderTargetDefaultsToNull() {
    assertNull(new HandlelessObjectRotateDragManipulator().getOnscreenRenderTarget());
  }

  @Test
  public void onscreenRenderTargetRoundTrips() {
    HandlelessObjectRotateDragManipulator m = new HandlelessObjectRotateDragManipulator();
    OnscreenRenderTarget target = dummyTarget();
    m.setOnscreenRenderTarget(target);
    assertSame(target, m.getOnscreenRenderTarget());
  }

  @Test
  public void initializeEventMessagesWithNullAxisDirectionCreatesRotateMainEventOnly() {
    // Default constructor leaves rotateAxisDirection null
    HandlelessObjectRotateDragManipulator m = new HandlelessObjectRotateDragManipulator();
    m.initializeEventMessages();
    assertEquals(ManipulationEvent.EventType.Rotate, m.getMainManipulationEvent().getType());
    // No additional events when rotateAxisDirection is null
    int count = 0;
    for (ManipulationEvent ignored : m.getManipulationEvents()) {
      count++;
    }
    assertEquals(0, count);
  }

  @Test
  public void initializeEventMessagesWithAxisDirectionAddsRotateEvent() {
    HandlelessObjectRotateDragManipulator m = new HandlelessObjectRotateDragManipulator(MovementDirection.UP);
    m.initializeEventMessages();
    assertEquals(ManipulationEvent.EventType.Rotate, m.getMainManipulationEvent().getType());
    int count = 0;
    for (ManipulationEvent e : m.getManipulationEvents()) {
      assertEquals(ManipulationEvent.EventType.Rotate, e.getType());
      count++;
    }
    assertEquals(1, count);
  }

  @Test
  public void doStartManipulatorReturnsFalseWithEmptyInputState() {
    assertFalse(new HandlelessObjectRotateDragManipulator().doStartManipulator(new InputState()));
  }

  @Test
  public void doClickManipulatorIsNoOp() {
    HandlelessObjectRotateDragManipulator m = new HandlelessObjectRotateDragManipulator();
    m.doClickManipulator(new InputState(), new InputState());
  }

  @Test
  public void doEndManipulatorIsNoOp() {
    HandlelessObjectRotateDragManipulator m = new HandlelessObjectRotateDragManipulator();
    m.doEndManipulator(new InputState(), new InputState());
  }

  @Test
  public void doTimeUpdateManipulatorIsNoOp() {
    HandlelessObjectRotateDragManipulator m = new HandlelessObjectRotateDragManipulator();
    m.doTimeUpdateManipulator(0.1, new InputState());
  }

  @Test
  public void doDataUpdateManipulatorWithEqualLocationsIsNoOp() {
    HandlelessObjectRotateDragManipulator m = new HandlelessObjectRotateDragManipulator();
    InputState s = new InputState();
    // Equal locations: early return, no NullPointerException
    m.doDataUpdateManipulator(s, s);
  }

  @Test
  public void manipulatorConstructionHasNullManipulatedTransformable() {
    assertNull(new HandlelessObjectRotateDragManipulator().getManipulatedTransformable());
  }

  private static OnscreenRenderTarget dummyTarget() {
    return (OnscreenRenderTarget) Proxy.newProxyInstance(
        HandlelessObjectRotateDragManipulatorTest.class.getClassLoader(),
        new Class[]{OnscreenRenderTarget.class},
        (proxy, method, args) -> null);
  }
}
