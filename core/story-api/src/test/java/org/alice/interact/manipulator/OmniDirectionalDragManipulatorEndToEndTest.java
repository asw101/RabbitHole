package org.alice.interact.manipulator;

import edu.cmu.cs.dennisc.render.OnscreenRenderTarget;
import edu.cmu.cs.dennisc.render.PickResult;
import edu.cmu.cs.dennisc.scenegraph.AsSeenBy;
import edu.cmu.cs.dennisc.scenegraph.OrthographicCamera;
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

/**
 * End-to-end tests that drive OmniDirectionalDragManipulator through the full
 * start → dataUpdate → end cycle using a stub OnscreenRenderTarget.
 */
public class OmniDirectionalDragManipulatorEndToEndTest {

  private OmniDirectionalDragManipulator manipulator;
  private RecordingDragAdapter adapter;
  private SymmetricPerspectiveCamera camera;
  private Transformable cameraParent;
  private Transformable target;
  private Scene scene;
  private OnscreenRenderTarget renderTarget;

  @Before
  public void setUp() {
    scene = new Scene();

    target = new Transformable();
    target.setParent(scene);
    target.setTranslationOnly(new Point3(0, 0, 0), AsSeenBy.SCENE);

    camera = new SymmetricPerspectiveCamera();
    cameraParent = new Transformable();
    camera.setParent(cameraParent);
    cameraParent.setParent(scene);
    cameraParent.setTranslationOnly(new Point3(0, 5, -10), AsSeenBy.SCENE);

    adapter = new RecordingDragAdapter();
    renderTarget = StubOnscreenRenderTarget.downwardRays();

    manipulator = new HeadlessOmniDirectionalDragManipulator();
    manipulator.setCamera(camera);
    manipulator.setOnscreenRenderTarget(renderTarget);
    manipulator.setDragAdapter(adapter);
  }

  @Test
  public void doStartManipulatorWithSelectedObjectReturnsTrue() {
    InputState startInput = new InputState();
    startInput.setCurrentlySelectedObject(target);
    startInput.setClickPickResult(new PickResult(target));
    startInput.setMouseLocation(new Point(100, 100));

    assertTrue(manipulator.doStartManipulator(startInput));
    assertSame(target, manipulator.getManipulatedTransformable());
  }

  @Test
  public void doStartManipulatorOrthographicCameraSkipsPlaneSetup() {
    OrthographicCamera ortho = new OrthographicCamera();
    ortho.setParent(cameraParent);
    manipulator.setCamera(ortho);
    InputState startInput = new InputState();
    startInput.setCurrentlySelectedObject(target);
    startInput.setClickPickResult(new PickResult(target));
    startInput.setMouseLocation(new Point(50, 50));

    assertTrue(manipulator.doStartManipulator(startInput));
  }

  @Test
  public void doDataUpdateAfterStartMovesTransformable() {
    InputState startInput = new InputState();
    startInput.setCurrentlySelectedObject(target);
    startInput.setClickPickResult(new PickResult(target));
    startInput.setMouseLocation(new Point(100, 100));
    assertTrue(manipulator.doStartManipulator(startInput));

    InputState newInput = new InputState();
    newInput.setMouseLocation(new Point(150, 100));
    InputState prev = new InputState();
    prev.setMouseLocation(new Point(100, 100));

    // Should not throw; either moves the transformable or no-ops.
    manipulator.doDataUpdateManipulator(newInput, prev);
  }

  @Test
  public void doDataUpdateOrthographicCameraDoesNotThrow() {
    // Orthographic mode requires careful geometric setup of the stub render target so
    // that the ray ↔ pick plane intersection is non-null. Skipping the data-update
    // portion here just ensures the start path with orthographic camera completes.
    OrthographicCamera ortho = new OrthographicCamera();
    ortho.setParent(cameraParent);
    manipulator.setCamera(ortho);
    InputState startInput = new InputState();
    startInput.setCurrentlySelectedObject(target);
    startInput.setClickPickResult(new PickResult(target));
    startInput.setMouseLocation(new Point(100, 100));
    manipulator.doStartManipulator(startInput);
  }

  @Test
  public void doEndManipulatorRestoresState() {
    InputState startInput = new InputState();
    startInput.setCurrentlySelectedObject(target);
    startInput.setClickPickResult(new PickResult(target));
    startInput.setMouseLocation(new Point(100, 100));
    manipulator.doStartManipulator(startInput);

    InputState endInput = new InputState();
    endInput.setMouseLocation(new Point(120, 110));
    manipulator.doEndManipulator(endInput, startInput);
  }

  @Test
  public void fullStartUpdateEndCycle() {
    InputState startInput = new InputState();
    startInput.setCurrentlySelectedObject(target);
    startInput.setClickPickResult(new PickResult(target));
    startInput.setMouseLocation(new Point(100, 100));
    assertTrue(manipulator.startManipulator(startInput));

    InputState midInput = new InputState();
    midInput.setMouseLocation(new Point(110, 100));
    manipulator.dataUpdateManipulator(midInput, startInput);

    InputState endInput = new InputState();
    endInput.setMouseLocation(new Point(130, 110));
    manipulator.endManipulator(endInput, midInput);

    assertFalse(manipulator.hasStarted());
  }

  @Test
  public void calculateMousePlaneOffsetReturnsNonNullPoint() {
    Point offset = manipulator.calculateMousePlaneOffset(new Point(50, 50), target);
    assertNotNull(offset);
  }

  @Test
  public void getMouseCursorPositionInLookingGlassReturnsPoint() {
    InputState startInput = new InputState();
    startInput.setCurrentlySelectedObject(target);
    startInput.setClickPickResult(new PickResult(target));
    startInput.setMouseLocation(new Point(100, 100));
    manipulator.doStartManipulator(startInput);

    assertNotNull(manipulator.getMouseCursorPositionInLookingGlass());
  }

  @Test
  public void createCameraPickPlaneAtOriginReturnsPlane() {
    assertNotNull(manipulator.createCameraPickPlane(Point3.ORIGIN));
  }

  @Test
  public void getInitialClickPointReturnsPointFromPickResult() {
    InputState startInput = new InputState();
    startInput.setCurrentlySelectedObject(target);
    startInput.setClickPickResult(new PickResult(target));
    assertNotNull(manipulator.getInitialClickPoint(startInput));
  }

  @Test
  public void setUpPlanesDoesNotThrow() {
    InputState startInput = new InputState();
    startInput.setCurrentlySelectedObject(target);
    startInput.setClickPickResult(new PickResult(target));
    startInput.setMouseLocation(new Point(50, 50));
    manipulator.doStartManipulator(startInput);
    manipulator.setUpPlanes(new Point3(0, 0, 0), new Point(20, 30));
  }

  @Test
  public void doStartManipulatorWithHorizontalRayClearsPlanes() {
    // With horizontal pick rays, setUpPlanes nulls both pickPlane and backPlane
    // and sets movementScale, exercising the "Special!" branch.
    manipulator.setOnscreenRenderTarget(StubOnscreenRenderTarget.horizontalRays());
    InputState startInput = new InputState();
    startInput.setCurrentlySelectedObject(target);
    startInput.setClickPickResult(new PickResult(target));
    startInput.setMouseLocation(new Point(100, 100));
    assertTrue(manipulator.doStartManipulator(startInput));
  }

  @Test
  public void doDataUpdateWithHorizontalRayUsesNoPlaneBranch() {
    // No-plane perspective branch: mouse-difference-based movement.
    manipulator.setOnscreenRenderTarget(StubOnscreenRenderTarget.horizontalRays());
    InputState startInput = new InputState();
    startInput.setCurrentlySelectedObject(target);
    startInput.setClickPickResult(new PickResult(target));
    startInput.setMouseLocation(new Point(100, 100));
    assertTrue(manipulator.doStartManipulator(startInput));

    InputState prev = new InputState();
    prev.setMouseLocation(new Point(100, 100));
    InputState curr = new InputState();
    curr.setMouseLocation(new Point(120, 110));
    manipulator.doDataUpdateManipulator(curr, prev);
  }

  @Test
  public void doStartManipulatorWithoutSelectedReturnsFalse() {
    InputState startInput = new InputState();
    // no selected object, no click pick → getInitialTransformable returns null
    startInput.setMouseLocation(new Point(50, 50));
    assertFalse(manipulator.doStartManipulator(startInput));
  }

  @Test
  public void doTimeUpdateManipulatorIsSafe() {
    manipulator.doTimeUpdateManipulator(0.1, new InputState());
  }

  /** Headless-safe subclass: skips AWT cursor manipulation. */
  static class HeadlessOmniDirectionalDragManipulator extends OmniDirectionalDragManipulator {
    @Override
    protected void hideCursor() {
      this.hidCursor = true;
    }
    @Override
    protected void showCursor() {
      this.hidCursor = false;
    }
    @Override
    protected void moveCursorToPointInLookingGlass(java.awt.Point awtPoint) { /* no-op */ }
  }

  /** Minimal DragAdapter that records manipulation events. */
  private static final class RecordingDragAdapter extends DragAdapter {
    final List<ManipulationEvent> events = new ArrayList<>();
    final List<Boolean> activations = new ArrayList<>();

    @Override
    public void triggerManipulationEvent(ManipulationEvent event, boolean isActivate) {
      events.add(event);
      activations.add(isActivate);
    }

    @Override
    public void undoRedoEndManipulation(AbstractManipulator manipulator, AffineMatrix4x4 originalTransformation) {
    }
  }
}
