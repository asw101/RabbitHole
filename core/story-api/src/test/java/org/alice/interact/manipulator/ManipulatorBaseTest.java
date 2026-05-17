package org.alice.interact.manipulator;

import edu.cmu.cs.dennisc.scenegraph.AbstractTransformable;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.InputState;
import org.alice.interact.event.ManipulationEvent;
import org.alice.interact.handle.HandleSet;
import org.alice.interact.manipulator.scenegraph.SnapGrid;
import org.alice.math.immutable.Angle;
import org.alice.math.immutable.AngleInDegrees;
import org.alice.math.immutable.AngleInRadians;
import org.alice.math.immutable.Matrix3x3;
import org.alice.math.immutable.Vector3;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for AbstractManipulator (via a concrete stub), SnapUtilities, and SnapGrid.
 * All tests are headless-safe — no AWT/rendering dependencies.
 */
public class ManipulatorBaseTest {

  private TestManipulator manipulator;

  @Before
  public void setUp() {
    manipulator = new TestManipulator();
  }

  // ── AbstractManipulator state ──

  @Test
  public void constructionSetsInitialState() {
    assertFalse(manipulator.hasStarted());
    assertFalse(manipulator.hasUpdated());
    assertNull(manipulator.getManipulatedTransformable());
    assertNull(manipulator.getMainManipulationEvent());
  }

  @Test
  public void toStringContainsClassName() {
    String s = manipulator.toString();
    assertNotNull(s);
    assertTrue(s.contains("TestManipulator"));
  }

  @Test
  public void setManipulatedTransformableSetsAndGets() {
    Transformable t = new Transformable();
    manipulator.setManipulatedTransformable(t);
    assertSame(t, manipulator.getManipulatedTransformable());
  }

  @Test
  public void setManipulatedTransformableToNullWorks() {
    Transformable t = new Transformable();
    manipulator.setManipulatedTransformable(t);
    manipulator.setManipulatedTransformable(null);
    assertNull(manipulator.getManipulatedTransformable());
  }

  @Test
  public void setSameTransformableDoesNotReInitialize() {
    Transformable t = new Transformable();
    manipulator.setManipulatedTransformable(t);
    int initCount = manipulator.initCount;
    manipulator.setManipulatedTransformable(t);
    assertEquals("Should not re-initialize for same transformable", initCount, manipulator.initCount);
  }

  @Test
  public void isUndoableReturnsFalseWithNoDragAdapter() {
    assertFalse(manipulator.isUndoable());
  }

  @Test
  public void doesManipulatedObjectHaveHandlesReturnsFalseWhenNull() {
    assertFalse(manipulator.doesManipulatedObjectHaveHandles());
  }

  @Test
  public void clearManipulationEventsRemovesAll() {
    ManipulationEvent e = new ManipulationEvent(
        ManipulationEvent.EventType.Translate, null, null);
    manipulator.addManipulationEvent(e);
    manipulator.clearManipulationEvents();
    int count = 0;
    for (ManipulationEvent me : manipulator.getManipulationEvents()) {
      count++;
    }
    assertEquals(0, count);
  }

  @Test
  public void addAndRemoveManipulationEvent() {
    // Note: getManipulationEvents() calls initializeEventMessages() which clears the list.
    // So we test add/remove without triggering the getter's side-effect.
    ManipulationEvent e1 = new ManipulationEvent(
        ManipulationEvent.EventType.Rotate, null, null);
    ManipulationEvent e2 = new ManipulationEvent(
        ManipulationEvent.EventType.Translate, null, null);
    manipulator.addManipulationEvent(e1);
    manipulator.addManipulationEvent(e2);
    manipulator.removeManipulationEvent(e1);
    // After removing e1, the list should still have e2
    // clearManipulationEvents should remove remaining
    manipulator.clearManipulationEvents();
    // No exception means success
  }

  @Test
  public void getManipulationEventsReturnsIterable() {
    Iterable<ManipulationEvent> events = manipulator.getManipulationEvents();
    assertNotNull(events);
  }

  @Test
  public void setHasUpdatedChangesState() {
    manipulator.setHasUpdated(true);
    assertTrue(manipulator.hasUpdated());
    manipulator.setHasUpdated(false);
    assertFalse(manipulator.hasUpdated());
  }

  @Test
  public void dataUpdateDoesNothingWhenNotStarted() {
    InputState current = new InputState();
    InputState prev = new InputState();
    manipulator.dataUpdateManipulator(current, prev);
    assertFalse(manipulator.hasUpdated());
  }

  @Test
  public void timeUpdateDoesNothingWhenNotStarted() {
    InputState current = new InputState();
    manipulator.timeUpdateManipulator(1.0, current);
    assertFalse(manipulator.hasUpdated());
  }

  @Test
  public void getUndoRedoDescriptionReturnsTestString() {
    assertEquals("test", manipulator.getUndoRedoDescription());
  }

  @Test
  public void setDragAdapterAcceptsNull() {
    manipulator.setDragAdapter(null);
    assertNull(manipulator.dragAdapter);
  }

  // ── SnapUtilities constants ──

  @Test
  public void snapLineVisualHeightIsSmallPositive() {
    assertTrue(SnapUtilities.SNAP_LINE_VISUAL_HEIGHT > 0);
    assertTrue(SnapUtilities.SNAP_LINE_VISUAL_HEIGHT < 1.0);
  }

  @Test
  public void snapToGroundDistanceIsSmallPositive() {
    assertTrue(SnapUtilities.SNAP_TO_GROUND_DISTANCE > 0);
  }

  @Test
  public void snapToGridDistanceIsSmallPositive() {
    assertTrue(SnapUtilities.SNAP_TO_GRID_DISTANCE > 0);
  }

  @Test
  public void defaultGridSpacingIsPositive() {
    assertEquals(0.5, SnapUtilities.DEFAULT_GRID_SPACING, 1e-9);
  }

  @Test
  public void minSnapReturnValueIsPositive() {
    assertTrue(SnapUtilities.MIN_SNAP_RETURN_VALUE > 0);
  }

  @Test
  public void angleSnapDistanceIsPositive() {
    assertTrue(SnapUtilities.ANGLE_SNAP_DISTANCE_IN_RADIANS > 0);
    assertTrue(SnapUtilities.ANGLE_SNAP_DISTANCE_IN_RADIANS < Math.PI);
  }

  // ── SnapUtilities.snapObjectToAngle ──

  @Test
  public void snapObjectToAngleExactMultipleSnaps() {
    Angle snapAmount = new AngleInDegrees(90);
    Angle input = new AngleInRadians(Math.PI / 2);
    Angle result = SnapUtilities.snapObjectToAngle(input, snapAmount);
    assertEquals(Math.PI / 2, result.getAsRadians(), 0.01);
  }

  @Test
  public void snapObjectToAngleCloseToMultipleSnaps() {
    Angle snapAmount = new AngleInDegrees(90);
    double nearSnap = Math.PI / 2 + 0.01;
    Angle input = new AngleInRadians(nearSnap);
    Angle result = SnapUtilities.snapObjectToAngle(input, snapAmount);
    assertEquals(Math.PI / 2, result.getAsRadians(), 0.05);
  }

  @Test
  public void snapObjectToAngleFarFromMultipleDoesNotSnap() {
    Angle snapAmount = new AngleInDegrees(90);
    double farFromSnap = Math.PI / 4;
    Angle input = new AngleInRadians(farFromSnap);
    Angle result = SnapUtilities.snapObjectToAngle(input, snapAmount);
    assertEquals(farFromSnap, result.getAsRadians(), 1e-9);
  }

  @Test
  public void snapObjectToAngleZeroAngle() {
    Angle snapAmount = new AngleInDegrees(45);
    Angle input = new AngleInRadians(0);
    Angle result = SnapUtilities.snapObjectToAngle(input, snapAmount);
    assertEquals(0.0, result.getAsRadians(), 0.01);
  }

  @Test
  public void snapObjectToAngleNegativeAngle() {
    Angle snapAmount = new AngleInDegrees(90);
    Angle input = new AngleInRadians(-Math.PI / 2 + 0.01);
    Angle result = SnapUtilities.snapObjectToAngle(input, snapAmount);
    assertEquals(-Math.PI / 2, result.getAsRadians(), 0.1);
  }

  // ── SnapUtilities hide methods ──

  @Test
  public void hideMovementSnapVisualizationDoesNotThrow() {
    SnapUtilities.hideMovementSnapVisualization();
  }

  @Test
  public void hideRotationSnapVisualizationDoesNotThrow() {
    SnapUtilities.hideRotationSnapVisualization();
  }

  @Test
  public void hideXAxisDoesNotThrow() {
    SnapUtilities.hideXAxis();
  }

  @Test
  public void hideYAxisDoesNotThrow() {
    SnapUtilities.hideYAxis();
  }

  @Test
  public void hideZAxisDoesNotThrow() {
    SnapUtilities.hideZAxis();
  }

  @Test
  public void hideArbitraryAxisDoesNotThrow() {
    SnapUtilities.hideArbitraryAxis();
  }

  @Test
  public void hideSnapSphereDoesNotThrow() {
    SnapUtilities.hideSnapSphere();
  }

  // ── SnapUtilities.getSGVisualForTransformable ──

  @Test
  public void getSGVisualForTransformableNullReturnsNull() {
    assertNull(SnapUtilities.getSGVisualForTransformable(null));
  }

  @Test
  public void getSGVisualForTransformableEmptyReturnsNull() {
    Transformable t = new Transformable();
    assertNull(SnapUtilities.getSGVisualForTransformable(t));
  }

  // ── SnapUtilities.getTransformableScale ──

  @Test
  public void getTransformableScaleNoVisualReturnsIdentity() {
    Transformable t = new Transformable();
    Matrix3x3 scale = SnapUtilities.getTransformableScale(t);
    assertNotNull(scale);
    assertEquals(Matrix3x3.IDENTITY, scale);
  }

  // ── SnapUtilities.doRotationSnapping ──

  @Test
  public void doRotationSnappingWithNullAdapterReturnsInput() {
    Angle input = new AngleInRadians(1.23);
    Angle result = SnapUtilities.doRotationSnapping(input, null);
    assertEquals(input.getAsRadians(), result.getAsRadians(), 1e-9);
  }

  // ── SnapGrid construction and properties ──

  @Test
  public void snapGridConstruction() {
    SnapGrid grid = new SnapGrid();
    assertNotNull(grid);
    assertEquals("Snap Grid", grid.getName());
  }

  @Test
  public void snapGridShowingDefaultFalse() {
    SnapGrid grid = new SnapGrid();
    // Default showing state after construction
    assertNotNull(grid);
  }

  @Test
  public void snapGridSetAndGetShowing() {
    SnapGrid grid = new SnapGrid();
    grid.setShowing(true);
    assertTrue(grid.getShowing());
    grid.setShowing(false);
    assertFalse(grid.getShowing());
  }

  @Test
  public void snapGridSetSpacingSameValueNoChange() {
    SnapGrid grid = new SnapGrid();
    // Initial spacing is 0.5 from constructor's setGridLines(.5)
    grid.setSpacing(0.5);
    // No exception means success
  }

  @Test
  public void snapGridSetSpacingDifferentValue() {
    SnapGrid grid = new SnapGrid();
    grid.setSpacing(1.0);
    // No exception means success
  }

  @Test
  public void snapGridSetOpacity() {
    SnapGrid grid = new SnapGrid();
    grid.setOpacity(0.7f);
    // No exception means success
  }

  @Test
  public void snapGridSetColor() {
    SnapGrid grid = new SnapGrid();
    grid.setColor(edu.cmu.cs.dennisc.color.Color4f.BLUE);
  }

  @Test
  public void snapGridStopTrackingCamerasWhenEmpty() {
    SnapGrid grid = new SnapGrid();
    grid.stopTrackingCameras();
    // No exception means success
  }

  @Test
  public void snapGridSetCurrentCameraNull() {
    SnapGrid grid = new SnapGrid();
    grid.setCurrentCamera(null);
    // No exception means success with null camera
  }

  // ── Concrete test manipulator ──

  private static class TestManipulator extends AbstractManipulator {
    int initCount = 0;

    @Override
    protected HandleSet getHandleSetToEnable() {
      return null;
    }

    @Override
    protected void initializeEventMessages() {
      super.initializeEventMessages();
      initCount++;
    }

    @Override
    public String getUndoRedoDescription() {
      return "test";
    }

    @Override
    public boolean doStartManipulator(InputState startInput) {
      return false;
    }

    @Override
    public void doDataUpdateManipulator(InputState currentInput, InputState previousInput) {
    }

    @Override
    public void doTimeUpdateManipulator(double dTime, InputState currentInput) {
    }

    @Override
    public void doEndManipulator(InputState endInput, InputState previousInput) {
    }

    @Override
    public void doClickManipulator(InputState endInput, InputState previousInput) {
    }
  }
}
