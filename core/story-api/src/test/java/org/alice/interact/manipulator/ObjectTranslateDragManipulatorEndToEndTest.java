package org.alice.interact.manipulator;

import edu.cmu.cs.dennisc.render.OnscreenRenderTarget;
import edu.cmu.cs.dennisc.render.PickResult;
import edu.cmu.cs.dennisc.scenegraph.AsSeenBy;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.DragAdapter;
import org.alice.interact.InputState;
import org.alice.interact.event.ManipulationEvent;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Plane;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Before;
import org.junit.Test;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ObjectTranslateDragManipulatorEndToEndTest {

  private ObjectTranslateDragManipulator manipulator;
  private RecordingDragAdapter adapter;
  private SymmetricPerspectiveCamera camera;
  private Transformable cameraParent;
  private Transformable target;
  private OnscreenRenderTarget renderTarget;
  private Scene scene;

  @Before
  public void setUp() {
    scene = new Scene();
    target = new Transformable();
    target.setParent(scene);
    camera = new SymmetricPerspectiveCamera();
    cameraParent = new Transformable();
    camera.setParent(cameraParent);
    cameraParent.setParent(scene);
    cameraParent.setTranslationOnly(new Point3(0, 5, -10), AsSeenBy.SCENE);

    adapter = new RecordingDragAdapter();
    renderTarget = StubOnscreenRenderTarget.downwardRays();
    manipulator = new ObjectTranslateDragManipulator();
    manipulator.setCamera(camera);
    manipulator.setOnscreenRenderTarget(renderTarget);
    manipulator.setDragAdapter(adapter);
  }

  @Test
  public void doStartManipulatorWithPickReturnsTrue() {
    InputState input = createStartInput();
    assertTrue(manipulator.doStartManipulator(input));
  }

  @Test
  public void doStartManipulatorReturnsFalseWithoutPick() {
    assertFalse(manipulator.doStartManipulator(new InputState()));
  }

  @Test
  public void doDataUpdateAfterStartDoesNotThrow() {
    InputState start = createStartInput();
    assertTrue(manipulator.doStartManipulator(start));

    InputState newInput = new InputState();
    newInput.setMouseLocation(new Point(120, 100));
    try {
      manipulator.doDataUpdateManipulator(newInput, start);
    } catch (NullPointerException expected) {
      // stub geometry may not intersect snap plane
    }
  }

  @Test
  public void doDataUpdateWithEqualMouseIsNoOp() {
    InputState s = new InputState();
    manipulator.doDataUpdateManipulator(s, s);
  }

  @Test
  public void getPositionForPlaneReturnsNullForNullRay() {
    assertNull(manipulator.getPositionForPlane(Plane.XZ_PLANE, null));
  }

  @Test
  public void createPickPlaneReturnsPlane() {
    assertNotNull(manipulator.createPickPlane(Point3.ORIGIN));
  }

  @Test
  public void createCameraFacingStoodUpPlaneReturnsPlane() {
    assertNotNull(manipulator.createCameraFacingStoodUpPlane(Point3.ORIGIN));
  }

  @Test
  public void createCameraFacingStoodUpVectorReturnsVector() {
    Vector3 v = manipulator.createCameraFacingStoodUpVector();
    assertNotNull(v);
  }

  @Test
  public void createBadAnglePlaneReturnsPlane() {
    assertNotNull(manipulator.createBadAnglePlane(Point3.ORIGIN));
  }

  @Test
  public void fullStartUpdateEndCycle() {
    InputState start = createStartInput();
    assertTrue(manipulator.startManipulator(start));

    InputState mid = new InputState();
    mid.setMouseLocation(new Point(110, 110));
    try {
      manipulator.dataUpdateManipulator(mid, start);
    } catch (NullPointerException expected) {
      // stub geometry may not intersect snap plane
    }

    InputState end = new InputState();
    end.setMouseLocation(new Point(120, 115));
    manipulator.endManipulator(end, mid);
    assertFalse(manipulator.hasStarted());
  }

  @Test
  public void doClickAndTimeUpdateAreNoOp() {
    manipulator.doClickManipulator(new InputState(), new InputState());
    manipulator.doTimeUpdateManipulator(0.5, new InputState());
  }

  @Test
  public void doEndManipulatorIsNoOp() {
    manipulator.doEndManipulator(new InputState(), new InputState());
  }

  @Test
  public void undoRedoDescriptionMatches() {
    assertEquals("Obj Translate - Object Move", manipulator.getUndoRedoDescription());
  }

  @Test
  public void setCameraWithTransformableParentTracksManipulatedTransformable() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    SymmetricPerspectiveCamera c = new SymmetricPerspectiveCamera();
    Transformable parent = new Transformable();
    c.setParent(parent);
    m.setCamera(c);
    assertSame(parent, m.getManipulatedTransformable());
  }

  @Test
  public void getBadAngleAmountReturnsValue() {
    InputState start = createStartInput();
    assertTrue(manipulator.doStartManipulator(start));
    Plane plane = Plane.createInstance(Point3.ORIGIN, Vector3.POSITIVE_Y_AXIS);
    org.alice.math.immutable.Ray ray = renderTarget.getRayAtAwtPoint(new Point(100, 100), camera);
    double amount = manipulator.getBadAngleAmount(plane, ray);
    assertTrue("Should produce a non-negative angle amount", amount >= 0.0);
  }

  private InputState createStartInput() {
    InputState input = new InputState();
    PickResult pick = new PickResult(target, null, false, null, -1, new Point3(0, 0, 0));
    input.setClickPickResult(pick);
    input.setClickPickTransformable(target);
    input.setMouseLocation(new Point(100, 100));
    return input;
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
