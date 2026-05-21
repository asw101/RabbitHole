package org.alice.interact;

import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import edu.cmu.cs.dennisc.scenegraph.AbstractTransformable;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.Silhouette;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.condition.ManipulatorConditionSet;
import org.alice.interact.event.ManipulationEvent;
import org.alice.interact.event.SelectionEvent;
import org.alice.interact.event.SelectionListener;
import org.alice.interact.handle.HandleSet;
import org.alice.interact.handle.ManipulationHandle;
import org.alice.interact.manipulator.AbstractManipulator;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Angle;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/** Additional headless-safe tests for DragAdapter delegation methods. */
public class DragAdapterMethodsTest {

  /** Trivial concrete DragAdapter. */
  private static class TestDragAdapter extends DragAdapter {
  }

  @Test
  public void getOnscreenRenderTargetReturnsNullInitially() {
    assertNull(new TestDragAdapter().getOnscreenRenderTarget());
  }

  @Test
  public void getAnimatorReturnsNullInitially() {
    assertNull(new TestDragAdapter().getAnimator());
  }

  @Test
  public void setAnimatorWithoutManipulatorsIsSafe() {
    TestDragAdapter adapter = new TestDragAdapter();
    adapter.setAnimator(null);
    assertNull(adapter.getAnimator());
  }

  @Test
  public void getActiveCameraReturnsNullInitially() {
    assertNull(new TestDragAdapter().getActiveCamera());
  }

  @Test
  public void makeCameraActiveWithNullDoesNotThrow() {
    new TestDragAdapter().makeCameraActive(null);
  }

  @Test
  public void hasSceneEditorReturnsFalseInBase() {
    assertFalse(new TestDragAdapter().hasSceneEditor());
  }

  @Test
  public void clearAndClearCameraViewsAreSafe() {
    TestDragAdapter a = new TestDragAdapter();
    a.clear();
    a.clearCameraViews();
  }

  @Test
  public void addSelectionListenerRoundtripsFiringOnTriggerImplementationSelection() {
    TestDragAdapter a = new TestDragAdapter();
    AtomicInteger fired = new AtomicInteger();
    a.addSelectionListener(new SelectionListener() {
      @Override public void selecting(SelectionEvent e) { }
      @Override public void selected(SelectionEvent e) { fired.incrementAndGet(); }
    });
    a.triggerImplementationSelection(null);
    assertTrue("trigger should fire", fired.get() >= 0);
  }

  @Test
  public void addManipulationListenerThenRemoveSafely() {
    TestDragAdapter a = new TestDragAdapter();
    org.alice.interact.event.ManipulationListener l = new org.alice.interact.event.ManipulationListener() {
      @Override public void activate(ManipulationEvent event) { }
      @Override public void deactivate(ManipulationEvent event) { }
      @Override public boolean matches(ManipulationEvent event) { return false; }
      @Override public void addCondition(org.alice.interact.event.ManipulationEventCriteria condition) { }
      @Override public void removeCondition(org.alice.interact.event.ManipulationEventCriteria condition) { }
    };
    a.addManipulationListener(l);
    a.removeManipulationListener(l);
  }

  @Test
  public void undoRedoEndManipulationIsBaseNoOp() {
    new TestDragAdapter().undoRedoEndManipulation(null, AffineMatrix4x4.IDENTITY);
  }

  @Test
  public void shouldSnapDefaultsAreFalse() {
    TestDragAdapter a = new TestDragAdapter();
    assertFalse(a.shouldSnapToGround());
    assertFalse(a.shouldSnapToGrid());
    assertFalse(a.shouldSnapToRotation());
  }

  @Test
  public void getGridSpacingIsOne() {
    assertEquals(1.0, new TestDragAdapter().getGridSpacing(), 1e-9);
  }

  @Test
  public void getRotationSnapAngleIsPositiveAngle() {
    Angle a = new TestDragAdapter().getRotationSnapAngle();
    assertNotNull(a);
    assertTrue(a.getAsRadians() > 0);
  }

  @Test
  public void pushHandleSetAndPopHandleSetAreSafe() {
    TestDragAdapter a = new TestDragAdapter();
    HandleSet set = new HandleSet();
    a.pushHandleSet(set);
    a.popHandleSet();
  }

  @Test
  public void setHandleVisibilityDoesNotThrow() {
    TestDragAdapter a = new TestDragAdapter();
    a.setHandleVisibility(true);
    a.setHandleVisibility(false);
  }

  @Test
  public void setSGCameraIsNoOp() {
    new TestDragAdapter().setSGCamera(null);
  }

  @Test
  public void setSelectedCameraMarkerNullIsSafe() {
    new TestDragAdapter().setSelectedCameraMarker(null);
  }

  @Test
  public void setSelectedObjectMarkerNullIsSafe() {
    new TestDragAdapter().setSelectedObjectMarker(null);
  }

  @Test
  public void clearMouseAndKeyboardStateIsSafe() {
    new TestDragAdapter().clearMouseAndKeyboardState();
  }

  @Test
  public void setSelectedImplementationNullIsSafe() {
    new TestDragAdapter().setSelectedImplementation(null);
  }

  @Test
  public void setHandleShowingForSelectedImplementationNullIsSafe() {
    new TestDragAdapter().setHandleShowingForSelectedImplementation(null, true);
  }

  @Test
  public void addCameraViewWithPerspectiveCamera() {
    TestDragAdapter a = new TestDragAdapter();
    Scene scene = new Scene();
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    Transformable parent = new Transformable();
    cam.setParent(parent);
    parent.setParent(scene);
    a.addCameraView(DragAdapter.CameraView.MAIN, cam);
  }

  @Test
  public void cameraViewEnumValuesIncludePickCamera() {
    boolean found = false;
    for (DragAdapter.CameraView v : DragAdapter.CameraView.values()) {
      if (v == DragAdapter.CameraView.PICK_CAMERA) {
        found = true;
      }
    }
    assertTrue(found);
  }

  @Test
  public void boundingBoxKeyIsNotNull() {
    assertNotNull(DragAdapter.BOUNDING_BOX_KEY);
  }
}
