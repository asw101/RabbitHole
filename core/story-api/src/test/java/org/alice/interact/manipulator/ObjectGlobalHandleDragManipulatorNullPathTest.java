package org.alice.interact.manipulator;

import edu.cmu.cs.dennisc.render.OnscreenRenderTarget;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.DragAdapter;
import org.alice.interact.InputState;
import org.alice.interact.event.ManipulationEvent;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/** Tests for ObjectGlobalHandleDragManipulator's null/default delegation paths. */
public class ObjectGlobalHandleDragManipulatorNullPathTest {

  private ObjectGlobalHandleDragManipulator manipulator;
  private OnscreenRenderTarget renderTarget;
  private SymmetricPerspectiveCamera camera;

  @Before
  public void setUp() {
    Scene scene = new Scene();
    Transformable parent = new Transformable();
    camera = new SymmetricPerspectiveCamera();
    camera.setParent(parent);
    parent.setParent(scene);
    renderTarget = StubOnscreenRenderTarget.downwardRays();
    manipulator = new ObjectGlobalHandleDragManipulator();
    manipulator.setCamera(camera);
    manipulator.setOnscreenRenderTarget(renderTarget);
    manipulator.setDragAdapter(new RecordingDragAdapter());
  }

  @Test
  public void getDesiredCameraViewWithoutActiveManipulatorReturnsPickCamera() {
    assertEquals(DragAdapter.CameraView.PICK_CAMERA, manipulator.getDesiredCameraView());
  }

  @Test
  public void getOnscreenRenderTargetReturnsStored() {
    assertSame(renderTarget, manipulator.getOnscreenRenderTarget());
  }

  @Test
  public void getCameraReturnsStored() {
    assertSame(camera, manipulator.getCamera());
  }

  @Test
  public void getUndoRedoDescriptionWithoutActiveManipulatorIsHandleDrag() {
    assertEquals("Handle Drag", manipulator.getUndoRedoDescription());
  }

  @Test
  public void hasUpdatedWithoutActiveManipulator() {
    assertFalse(manipulator.hasUpdated());
  }

  @Test
  public void undoRedoBeginEndWithoutActiveManipulatorIsSafe() {
    manipulator.undoRedoBeginManipulation();
    manipulator.undoRedoEndManipulation();
  }

  @Test
  public void getManipulatedTransformableWithoutActiveManipulatorIsNull() {
    assertNull(manipulator.getManipulatedTransformable());
  }

  @Test
  public void doDataAndTimeUpdateWithoutActiveManipulatorAreSafe() {
    manipulator.doDataUpdateManipulator(new InputState(), new InputState());
    manipulator.doTimeUpdateManipulator(0.5, new InputState());
  }

  @Test
  public void triggerAllDeactivateEventsWithoutActiveManipulatorIsSafe() {
    manipulator.triggerAllDeactivateEvents();
  }

  @Test
  public void getMainManipulationEventWithoutActiveManipulatorIsNull() {
    assertNull(manipulator.getMainManipulationEvent());
  }

  @Test
  public void doClickManipulatorWithoutClickHandleIsSafe() {
    manipulator.doClickManipulator(new InputState(), new InputState());
  }

  @Test
  public void doEndManipulatorWithoutActiveManipulatorIsSafe() {
    manipulator.doEndManipulator(new InputState(), new InputState());
  }

  @Test
  public void doStartManipulatorWithoutClickHandleReturnsFalse() {
    assertFalse(manipulator.doStartManipulator(new InputState()));
  }

  @Test
  public void setDesiredCameraViewWithoutActiveManipulatorIsSafe() {
    manipulator.setDesiredCameraView(DragAdapter.CameraView.PICK_CAMERA);
  }

  @Test
  public void setCameraToNullDoesNotThrow() {
    manipulator.setCamera(null);
  }

  @Test
  public void setOnscreenRenderTargetToNullDoesNotThrow() {
    manipulator.setOnscreenRenderTarget(null);
  }

  private static final class RecordingDragAdapter extends DragAdapter {
    final List<ManipulationEvent> events = new ArrayList<>();
    @Override
    public void triggerManipulationEvent(ManipulationEvent event, boolean isActivate) {
      events.add(event);
    }
    @Override
    public void undoRedoEndManipulation(AbstractManipulator m, AffineMatrix4x4 t) {}
  }
}
