package org.alice.interact.manipulator;

import edu.cmu.cs.dennisc.render.OnscreenRenderTarget;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.DragAdapter.CameraView;
import org.alice.interact.InputState;
import org.alice.interact.event.ManipulationEvent;
import org.alice.math.immutable.Plane;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

/** Headless-safe characterization tests for OmniDirectionalDragManipulator. */
public class OmniDirectionalDragManipulatorTest {

  @Test
  public void constructionDoesNotThrow() {
    assertNotNull(new OmniDirectionalDragManipulator());
  }

  @Test
  public void desiredCameraViewIsPickCamera() {
    assertEquals(CameraView.PICK_CAMERA, new OmniDirectionalDragManipulator().getDesiredCameraView());
  }

  @Test
  public void setDesiredCameraViewIsNoOp() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    m.setDesiredCameraView(CameraView.TOP_LEFT);
    assertEquals(CameraView.PICK_CAMERA, m.getDesiredCameraView());
  }

  @Test
  public void undoRedoDescriptionMatchesSource() {
    assertEquals("Omni-Drag - Object Move", new OmniDirectionalDragManipulator().getUndoRedoDescription());
  }

  @Test
  public void cameraDefaultsToNull() {
    assertNull(new OmniDirectionalDragManipulator().getCamera());
  }

  @Test
  public void setCameraRoundTrips() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    m.setCamera(camera);
    assertSame(camera, m.getCamera());
  }

  @Test
  public void setCameraTracksMovableParent() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    Transformable parent = new Transformable();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.setParent(parent);

    m.setCamera(camera);

    assertSame(camera, m.getCamera());
    assertSame(parent, m.getManipulatedTransformable());
  }

  @Test
  public void setCameraToNullKeepsExistingTransformable() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    Transformable parent = new Transformable();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.setParent(parent);
    m.setCamera(camera);

    m.setCamera(null);

    assertSame(parent, m.getManipulatedTransformable());
  }

  @Test
  public void onscreenRenderTargetDefaultsToNull() {
    assertNull(new OmniDirectionalDragManipulator().getOnscreenRenderTarget());
  }

  @Test
  public void onscreenRenderTargetRoundTrips() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    OnscreenRenderTarget target = dummyTarget();
    m.setOnscreenRenderTarget(target);
    assertSame(target, m.getOnscreenRenderTarget());
  }

  @Test
  public void initializeEventMessagesCreatesTranslateMainEvent() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    m.initializeEventMessages();
    assertEquals(ManipulationEvent.EventType.Translate, m.getMainManipulationEvent().getType());
  }

  @Test
  public void initializeEventMessagesAddsTranslateDirectionEvents() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
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
    assertFalse(new OmniDirectionalDragManipulator().doStartManipulator(new InputState()));
  }

  @Test
  public void doClickManipulatorIsNoOp() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    m.doClickManipulator(new InputState(), new InputState());
  }

  @Test
  public void doTimeUpdateManipulatorIsNoOp() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    m.doTimeUpdateManipulator(0.1, new InputState());
  }

  @Test
  public void doDataUpdateManipulatorIsNoOpWithEqualMouseLocations() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    InputState s = new InputState();
    m.doDataUpdateManipulator(s, s);
  }

  @Test
  public void doDataUpdateManipulatorIsNoOpWhenTransformableNull() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    InputState cur = new InputState();
    InputState prev = new InputState();
    // Different locations but no transformable: second guard catches it
    m.doDataUpdateManipulator(cur, prev);
  }

  @Test
  public void getInitialTransformableWithEmptyStateReturnsNull() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    assertNull(m.getInitialTransformable(new InputState()));
  }

  @Test
  public void createLevelPickPlaneAtOrigin() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    Plane plane = m.createLevelPickPlane(Point3.ORIGIN);
    assertNotNull(plane);
  }

  @Test
  public void createLevelPickPlaneAtArbitraryPoint() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    Point3 pt = new Point3(1.0, 2.0, 3.0);
    Plane plane = m.createLevelPickPlane(pt);
    assertNotNull(plane);
  }

  @Test
  public void manipulatorConstructionHasNullManipulatedTransformable() {
    assertNull(new OmniDirectionalDragManipulator().getManipulatedTransformable());
  }

  private static OnscreenRenderTarget dummyTarget() {
    return (OnscreenRenderTarget) Proxy.newProxyInstance(
        OmniDirectionalDragManipulatorTest.class.getClassLoader(),
        new Class[]{OnscreenRenderTarget.class},
        (proxy, method, args) -> null);
  }
}
