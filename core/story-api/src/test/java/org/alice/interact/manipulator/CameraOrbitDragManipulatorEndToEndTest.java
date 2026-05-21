package org.alice.interact.manipulator;

import edu.cmu.cs.dennisc.render.OnscreenRenderTarget;
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

public class CameraOrbitDragManipulatorEndToEndTest {

  private CameraOrbitDragManipulator manipulator;
  private SymmetricPerspectiveCamera camera;
  private Transformable cameraParent;
  private RecordingDragAdapter adapter;

  @Before
  public void setUp() {
    Scene scene = new Scene();
    camera = new SymmetricPerspectiveCamera();
    cameraParent = new Transformable();
    camera.setParent(cameraParent);
    cameraParent.setParent(scene);
    cameraParent.setTranslationOnly(new Point3(0, 5, -10), AsSeenBy.SCENE);

    adapter = new RecordingDragAdapter();
    manipulator = new CameraOrbitDragManipulator();
    manipulator.setCamera(camera);
    manipulator.setDragAdapter(adapter);
  }

  @Test
  public void doStartManipulatorReturnsTrueWithoutClickedTransformable() {
    InputState input = new InputState();
    input.setMouseLocation(new Point(100, 100));
    assertTrue(manipulator.doStartManipulator(input));
  }

  @Test
  public void doStartManipulatorReturnsTrueWithClickedTransformable() {
    Transformable target = new Transformable();
    target.setParent(camera.getRoot());
    InputState input = new InputState();
    input.setMouseLocation(new Point(100, 100));
    input.setClickPickTransformable(target);
    assertTrue(manipulator.doStartManipulator(input));
  }

  @Test
  public void doDataUpdateAfterStartRotatesCamera() {
    InputState start = new InputState();
    start.setMouseLocation(new Point(100, 100));
    assertTrue(manipulator.doStartManipulator(start));

    InputState newInput = new InputState();
    newInput.setMouseLocation(new Point(120, 110));
    manipulator.doDataUpdateManipulator(newInput, start);
  }

  @Test
  public void doEndManipulatorIsSafe() {
    InputState start = new InputState();
    start.setMouseLocation(new Point(100, 100));
    assertTrue(manipulator.doStartManipulator(start));
    manipulator.doEndManipulator(new InputState(), start);
  }

  @Test
  public void doClickAndTimeUpdateAreNoOp() {
    manipulator.doClickManipulator(new InputState(), new InputState());
  }

  @Test
  public void undoRedoDescription() {
    assertEquals("Camera Rotate", manipulator.getUndoRedoDescription());
  }

  @Test
  public void getDesiredCameraViewIsPickCamera() {
    assertEquals(DragAdapter.CameraView.PICK_CAMERA, manipulator.getDesiredCameraView());
  }

  @Test
  public void setPivotPointStoresValue() {
    manipulator.setPivotPoint(new Point3(1, 2, 3));
  }

  @Test
  public void fullStartUpdateEndCycle() {
    InputState start = new InputState();
    start.setMouseLocation(new Point(100, 100));
    assertTrue(manipulator.startManipulator(start));

    InputState mid = new InputState();
    mid.setMouseLocation(new Point(110, 105));
    manipulator.dataUpdateManipulator(mid, start);

    InputState end = new InputState();
    end.setMouseLocation(new Point(120, 110));
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
