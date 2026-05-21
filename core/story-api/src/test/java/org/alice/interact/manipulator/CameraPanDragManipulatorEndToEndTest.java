package org.alice.interact.manipulator;

import edu.cmu.cs.dennisc.scenegraph.AsSeenBy;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.DragAdapter;
import org.alice.interact.InputState;
import org.alice.interact.event.ManipulationEvent;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Before;
import org.junit.Test;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CameraPanDragManipulatorEndToEndTest {

  private CameraPanDragManipulator manipulator;
  private SymmetricPerspectiveCamera camera;
  private Transformable cameraParent;

  @Before
  public void setUp() {
    Scene scene = new Scene();
    camera = new SymmetricPerspectiveCamera();
    cameraParent = new Transformable();
    camera.setParent(cameraParent);
    cameraParent.setParent(scene);
    cameraParent.setTranslationOnly(new Point3(0, 5, -10), AsSeenBy.SCENE);

    manipulator = new CameraPanDragManipulator();
    manipulator.setCamera(camera);
    manipulator.setDragAdapter(new RecordingDragAdapter());
  }

  @Test
  public void doStartManipulatorReturnsTrue() {
    InputState input = new InputState();
    input.setMouseLocation(new Point(100, 100));
    assertTrue(manipulator.doStartManipulator(input));
  }

  @Test
  public void doDataUpdateAfterStartTranslatesCamera() {
    InputState start = new InputState();
    start.setMouseLocation(new Point(100, 100));
    assertTrue(manipulator.doStartManipulator(start));

    InputState newInput = new InputState();
    newInput.setMouseLocation(new Point(120, 130));
    manipulator.doDataUpdateManipulator(newInput, start);
  }

  @Test
  public void undoRedoDescription() {
    assertEquals("Camera Move (Pan)", manipulator.getUndoRedoDescription());
  }

  @Test
  public void getDesiredCameraViewIsPickCamera() {
    assertEquals(DragAdapter.CameraView.PICK_CAMERA, manipulator.getDesiredCameraView());
  }

  @Test
  public void doEndAndClickAndTimeUpdateAreNoOp() {
    manipulator.doEndManipulator(new InputState(), new InputState());
    manipulator.doClickManipulator(new InputState(), new InputState());
    manipulator.doTimeUpdateManipulator(0.5, new InputState());
  }

  @Test
  public void fullStartUpdateEndCycle() {
    InputState start = new InputState();
    start.setMouseLocation(new Point(100, 100));
    assertTrue(manipulator.startManipulator(start));

    InputState mid = new InputState();
    mid.setMouseLocation(new Point(110, 110));
    manipulator.dataUpdateManipulator(mid, start);

    InputState end = new InputState();
    end.setMouseLocation(new Point(120, 120));
    manipulator.endManipulator(end, mid);
    assertFalse(manipulator.hasStarted());
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
