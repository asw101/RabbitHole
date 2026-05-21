package org.alice.interact.manipulator;

import edu.cmu.cs.dennisc.render.OnscreenRenderTarget;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.DragAdapter.CameraView;
import org.alice.interact.InputState;
import org.alice.interact.event.ManipulationEvent;
import org.alice.math.immutable.Plane;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

/** Headless-safe characterization tests for ObjectTranslateDragManipulator. */
public class ObjectTranslateDragManipulatorTest {

  @Test
  public void constructionDoesNotThrow() {
    assertNotNull(new ObjectTranslateDragManipulator());
  }

  @Test
  public void desiredCameraViewIsPickCamera() {
    assertEquals(CameraView.PICK_CAMERA, new ObjectTranslateDragManipulator().getDesiredCameraView());
  }

  @Test
  public void setDesiredCameraViewIsNoOp() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    m.setDesiredCameraView(CameraView.TOP_LEFT);
    assertEquals(CameraView.PICK_CAMERA, m.getDesiredCameraView());
  }

  @Test
  public void undoRedoDescriptionMatchesSource() {
    assertEquals("Obj Translate - Object Move", new ObjectTranslateDragManipulator().getUndoRedoDescription());
  }

  @Test
  public void cameraDefaultsToNull() {
    assertNull(new ObjectTranslateDragManipulator().getCamera());
  }

  @Test
  public void setCameraRoundTrips() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    m.setCamera(camera);
    assertSame(camera, m.getCamera());
  }

  @Test
  public void setCameraTracksTransformableParent() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    Transformable parent = new Transformable();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.setParent(parent);

    m.setCamera(camera);

    assertSame(camera, m.getCamera());
    assertSame(parent, m.getManipulatedTransformable());
  }

  @Test
  public void setCameraToNullLeavesManipulatedTransformableUnchanged() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    Transformable parent = new Transformable();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.setParent(parent);
    m.setCamera(camera);

    m.setCamera(null);

    assertSame(parent, m.getManipulatedTransformable());
  }

  @Test
  public void onscreenRenderTargetDefaultsToNull() {
    assertNull(new ObjectTranslateDragManipulator().getOnscreenRenderTarget());
  }

  @Test
  public void onscreenRenderTargetRoundTrips() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    OnscreenRenderTarget target = dummyTarget();
    m.setOnscreenRenderTarget(target);
    assertSame(target, m.getOnscreenRenderTarget());
  }

  @Test
  public void initializeEventMessagesCreatesTranslateMainEvent() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    m.initializeEventMessages();
    assertEquals(ManipulationEvent.EventType.Translate, m.getMainManipulationEvent().getType());
  }

  @Test
  public void initializeEventMessagesAddsTranslateDirectionEvents() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
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
    assertFalse(new ObjectTranslateDragManipulator().doStartManipulator(new InputState()));
  }

  @Test
  public void doClickManipulatorIsNoOp() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    m.doClickManipulator(new InputState(), new InputState());
  }

  @Test
  public void doEndManipulatorIsNoOp() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    m.doEndManipulator(new InputState(), new InputState());
  }

  @Test
  public void doTimeUpdateManipulatorIsNoOp() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    m.doTimeUpdateManipulator(0.1, new InputState());
  }

  @Test
  public void doDataUpdateManipulatorWithEqualLocationsIsNoOp() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    InputState s = new InputState();
    m.doDataUpdateManipulator(s, s);
  }

  @Test
  public void doDataUpdateManipulatorWithNullTransformableIsNoOp() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    m.doDataUpdateManipulator(new InputState(), new InputState());
  }

  @Test
  public void createPickPlaneAtOriginHasYAxisNormal() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    Plane plane = m.createPickPlane(Point3.ORIGIN);
    assertNotNull(plane);
    // Normal should be POSITIVE_Y_AXIS
    Vector3 normal = plane.getNormal();
    assertEquals(0.0, normal.x(), 1e-9);
    assertEquals(1.0, normal.y(), 1e-9);
    assertEquals(0.0, normal.z(), 1e-9);
  }

  @Test
  public void createPickPlaneAtArbitraryPoint() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    Plane plane = m.createPickPlane(new Point3(5.0, 2.0, -3.0));
    assertNotNull(plane);
  }

  @Test
  public void manipulatorConstructionHasNullManipulatedTransformable() {
    assertNull(new ObjectTranslateDragManipulator().getManipulatedTransformable());
  }

  private static OnscreenRenderTarget dummyTarget() {
    return (OnscreenRenderTarget) Proxy.newProxyInstance(
        ObjectTranslateDragManipulatorTest.class.getClassLoader(),
        new Class[]{OnscreenRenderTarget.class},
        (proxy, method, args) -> null);
  }
}
