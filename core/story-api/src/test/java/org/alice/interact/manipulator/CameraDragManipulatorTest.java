package org.alice.interact.manipulator;

import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.DragAdapter.CameraView;
import org.junit.Test;

import static org.junit.Assert.*;

/** Headless-safe characterization tests for camera drag manipulators. */
public class CameraDragManipulatorTest {

  @Test
  public void cameraMoveUsesPickCameraView() {
    assertEquals(CameraView.PICK_CAMERA, new CameraMoveDragManipulator().getDesiredCameraView());
  }

  @Test
  public void cameraOrbitUsesPickCameraView() {
    assertEquals(CameraView.PICK_CAMERA, new CameraOrbitDragManipulator().getDesiredCameraView());
  }

  @Test
  public void cameraPanUsesPickCameraView() {
    assertEquals(CameraView.PICK_CAMERA, new CameraPanDragManipulator().getDesiredCameraView());
  }

  @Test
  public void cameraTiltUsesPickCameraView() {
    assertEquals(CameraView.PICK_CAMERA, new CameraTiltDragManipulator().getDesiredCameraView());
  }

  @Test
  public void cameraMoveDescriptionMatchesSourceText() {
    assertEquals("Camera Move (Drag)", new CameraMoveDragManipulator().getUndoRedoDescription());
  }

  @Test
  public void cameraOrbitDescriptionMatchesSourceText() {
    assertEquals("Camera Rotate", new CameraOrbitDragManipulator().getUndoRedoDescription());
  }

  @Test
  public void cameraPanDescriptionMatchesSourceText() {
    assertEquals("Camera Move (Pan)", new CameraPanDragManipulator().getUndoRedoDescription());
  }

  @Test
  public void cameraTiltDescriptionMatchesSourceText() {
    assertEquals("Camera Rotate", new CameraTiltDragManipulator().getUndoRedoDescription());
  }

  @Test
  public void setCameraOnMoveManipulatorTracksParentTransformable() {
    CameraMoveDragManipulator manipulator = new CameraMoveDragManipulator();
    Transformable parent = new Transformable();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.setParent(parent);

    manipulator.setCamera(camera);

    assertSame(parent, manipulator.getManipulatedTransformable());
  }

  @Test
  public void setCameraOnPanManipulatorTracksParentTransformable() {
    CameraPanDragManipulator manipulator = new CameraPanDragManipulator();
    Transformable parent = new Transformable();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.setParent(parent);

    manipulator.setCamera(camera);

    assertSame(parent, manipulator.getManipulatedTransformable());
  }
}
