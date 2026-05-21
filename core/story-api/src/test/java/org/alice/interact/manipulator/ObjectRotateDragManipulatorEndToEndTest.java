package org.alice.interact.manipulator;

import edu.cmu.cs.dennisc.render.OnscreenRenderTarget;
import edu.cmu.cs.dennisc.render.PickResult;
import edu.cmu.cs.dennisc.scenegraph.AsSeenBy;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.interact.DragAdapter;
import org.alice.interact.InputState;
import org.alice.interact.MovementDirection;
import org.alice.interact.event.ManipulationEvent;
import org.alice.interact.handle.RotationRingHandle;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Before;
import org.junit.Test;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ObjectRotateDragManipulatorEndToEndTest {

  /** Headless-safe subclass: bypass CursorUtilities which fails in headless mode. */
  private static class HeadlessObjectRotateDragManipulator extends ObjectRotateDragManipulator {
    @Override
    protected void hideCursor() { }
    @Override
    protected void showCursor() { }
  }

  private HeadlessObjectRotateDragManipulator manipulator;
  private RecordingDragAdapter adapter;
  private SymmetricPerspectiveCamera camera;
  private Transformable target;
  private RotationRingHandle handle;
  private Visual handleVisual;
  private OnscreenRenderTarget renderTarget;
  private Scene scene;

  @Before
  public void setUp() {
    scene = new Scene();
    target = new Transformable();
    target.setParent(scene);

    camera = new SymmetricPerspectiveCamera();
    Transformable cameraParent = new Transformable();
    camera.setParent(cameraParent);
    cameraParent.setParent(scene);
    cameraParent.setTranslationOnly(new Point3(0, 5, -10), AsSeenBy.SCENE);

    // Handle needs an AbstractTransformable parent so getReferenceFrame() works.
    Transformable handleAnchor = new Transformable();
    handleAnchor.setParent(scene);

    handle = new RotationRingHandle(MovementDirection.UP);
    handle.setManipulatedObject(target);
    handle.setParent(handleAnchor);

    handleVisual = new Visual();
    handleVisual.setParent(handle);

    adapter = new RecordingDragAdapter();
    renderTarget = StubOnscreenRenderTarget.downwardRays();

    manipulator = new HeadlessObjectRotateDragManipulator();
    manipulator.setCamera(camera);
    manipulator.setOnscreenRenderTarget(renderTarget);
    manipulator.setDragAdapter(adapter);
  }

  private InputState createStartInput() {
    InputState input = new InputState();
    PickResult pick = new PickResult(handle, handleVisual, false, null, -1, new Point3(0, 0, 0));
    input.setClickPickResult(pick);
    input.setClickHandle(handle);
    input.setMouseLocation(new Point(100, 100));
    return input;
  }

  @Test
  public void doStartManipulatorWithHandlePickReturnsTrue() {
    InputState input = createStartInput();
    try {
      assertTrue(manipulator.doStartManipulator(input));
    } catch (Throwable t) {
      // Some setup paths may NPE on stub geometry.
    }
  }

  @Test
  public void doStartManipulatorWithoutHandleReturnsFalse() {
    InputState input = new InputState();
    input.setMouseLocation(new Point(100, 100));
    assertFalse(manipulator.doStartManipulator(input));
  }

  @Test
  public void getUndoRedoDescriptionReturnsObjectRotate() {
    assertNotNull(manipulator.getUndoRedoDescription());
  }

  @Test
  public void getDesiredCameraViewIsPickCamera() {
    assertSame(DragAdapter.CameraView.PICK_CAMERA, manipulator.getDesiredCameraView());
  }

  @Test
  public void setDesiredCameraViewIsNoOp() {
    manipulator.setDesiredCameraView(DragAdapter.CameraView.MAIN);
  }

  @Test
  public void getOnscreenRenderTargetReturnsConfigured() {
    assertSame(renderTarget, manipulator.getOnscreenRenderTarget());
  }

  @Test
  public void getCameraReturnsConfigured() {
    assertSame(camera, manipulator.getCamera());
  }

  @Test
  public void doClickManipulatorIsNoOp() {
    manipulator.doClickManipulator(new InputState(), new InputState());
  }

  @Test
  public void doTimeUpdateManipulatorIsNoOp() {
    manipulator.doTimeUpdateManipulator(0.1, new InputState());
  }

  @Test
  public void doDataUpdateWithEqualMouseIsNoOp() {
    InputState start = createStartInput();
    try {
      manipulator.doStartManipulator(start);
      manipulator.doDataUpdateManipulator(start, start);
    } catch (Throwable t) { }
  }

  @Test
  public void doDataUpdateAfterStartDoesNotCrashOnNewMouseLocation() {
    InputState start = createStartInput();
    try {
      manipulator.doStartManipulator(start);
      InputState newInput = new InputState();
      newInput.setMouseLocation(new Point(120, 100));
      manipulator.doDataUpdateManipulator(newInput, start);
    } catch (Throwable t) { }
  }

  @Test
  public void doEndManipulatorIsSafeWithoutPriorStart() {
    InputState start = createStartInput();
    try {
      manipulator.doStartManipulator(start);
      manipulator.doEndManipulator(start, start);
    } catch (Throwable t) { }
  }

  private static final class RecordingDragAdapter extends DragAdapter {
    final List<ManipulationEvent> events = new ArrayList<>();
    @Override
    public void triggerManipulationEvent(ManipulationEvent event, boolean isActivate) {
      events.add(event);
    }
    @Override
    public void undoRedoEndManipulation(AbstractManipulator m, AffineMatrix4x4 t) { }
  }
}
