package org.alice.interact.manipulator;

import org.alice.interact.DragAdapter;
import org.alice.interact.InputState;
import org.alice.interact.condition.MovementDescription;
import org.alice.interact.event.ManipulationEvent;
import org.alice.interact.handle.HandleSet;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/** Headless-safe lifecycle tests for AbstractManipulator. */
public class AbstractManipulatorTest {
  private LifecycleManipulator manipulator;
  private RecordingDragAdapter dragAdapter;

  @Before
  public void setUp() {
    manipulator = new LifecycleManipulator();
    dragAdapter = new RecordingDragAdapter();
    manipulator.setDragAdapter(dragAdapter);
    manipulator.primeEvents();
  }

  @Test
  public void startManipulatorReturnsTrueWhenHookReturnsTrue() {
    manipulator.startReturns = true;
    assertTrue(manipulator.startManipulator(new InputState()));
    assertTrue(manipulator.hasStarted());
  }

  @Test
  public void startManipulatorReturnsFalseWhenHookReturnsFalse() {
    manipulator.startReturns = false;
    assertFalse(manipulator.startManipulator(new InputState()));
    assertFalse(manipulator.hasStarted());
  }

  @Test
  public void startManipulatorActivatesMainManipulationEvent() {
    manipulator.startReturns = true;

    manipulator.startManipulator(new InputState());

    assertEquals(1, dragAdapter.activations.size());
    assertTrue(dragAdapter.activations.get(0));
    assertEquals(ManipulationEvent.EventType.Translate, dragAdapter.events.get(0).getType());
  }

  @Test
  public void dataUpdateWhileStartedCallsHookAndMarksManipulatorUpdated() {
    manipulator.startManipulator(new InputState());

    manipulator.dataUpdateManipulator(new InputState(), new InputState());

    assertEquals(1, manipulator.dataUpdates);
    assertTrue(manipulator.hasUpdated());
  }

  @Test
  public void timeUpdateWhileStartedCallsHookAndMarksManipulatorUpdated() {
    manipulator.startManipulator(new InputState());

    manipulator.timeUpdateManipulator(0.25, new InputState());

    assertEquals(1, manipulator.timeUpdates);
    assertTrue(manipulator.hasUpdated());
  }

  @Test
  public void clickManipulatorInvokesClickAndEndHooks() {
    manipulator.clickMarksUpdated = true;
    dragAdapter.sceneEditor = true;

    manipulator.clickManipulator(new InputState(), new InputState());

    assertEquals(1, manipulator.clickCalls);
    assertEquals(1, manipulator.endCalls);
    assertFalse(manipulator.hasStarted());
  }

  @Test
  public void clickManipulatorTriggersUndoWhenUpdatedInSceneEditor() {
    manipulator.clickMarksUpdated = true;
    dragAdapter.sceneEditor = true;

    manipulator.clickManipulator(new InputState(), new InputState());

    assertEquals(1, dragAdapter.undoCalls);
  }

  @Test
  public void endManipulatorResetsStartedStateAndDeactivatesEvents() {
    manipulator.startManipulator(new InputState());
    manipulator.dataUpdateManipulator(new InputState(), new InputState());
    dragAdapter.sceneEditor = true;

    manipulator.endManipulator(new InputState(), new InputState());

    assertFalse(manipulator.hasStarted());
    assertTrue(dragAdapter.activations.contains(Boolean.FALSE));
  }

  @Test
  public void isUndoableRequiresSceneEditorAndUpdate() {
    manipulator.setHasUpdated(true);
    dragAdapter.sceneEditor = false;
    assertFalse(manipulator.isUndoable());
    dragAdapter.sceneEditor = true;
    assertTrue(manipulator.isUndoable());
  }

  private static final class LifecycleManipulator extends AbstractManipulator {
    private boolean startReturns = true;
    private boolean clickMarksUpdated;
    private int dataUpdates;
    private int timeUpdates;
    private int endCalls;
    private int clickCalls;

    private void primeEvents() {
      this.setMainManipulationEvent(new ManipulationEvent(ManipulationEvent.EventType.Translate, null, null));
      this.addManipulationEvent(new ManipulationEvent(
          ManipulationEvent.EventType.Rotate,
          new MovementDescription(org.alice.interact.MovementDirection.LEFT),
          null));
    }

    @Override
    protected HandleSet getHandleSetToEnable() {
      return null;
    }

    @Override
    public String getUndoRedoDescription() {
      return "lifecycle";
    }

    @Override
    public boolean doStartManipulator(InputState startInput) {
      return startReturns;
    }

    @Override
    public void doDataUpdateManipulator(InputState currentInput, InputState previousInput) {
      dataUpdates++;
      setHasUpdated(true);
    }

    @Override
    public void doTimeUpdateManipulator(double dTime, InputState currentInput) {
      timeUpdates++;
      setHasUpdated(true);
    }

    @Override
    public void doEndManipulator(InputState endInput, InputState previousInput) {
      endCalls++;
    }

    @Override
    public void doClickManipulator(InputState endInput, InputState previousInput) {
      clickCalls++;
      if (clickMarksUpdated) {
        setHasUpdated(true);
      }
    }
  }

  private static final class RecordingDragAdapter extends DragAdapter {
    private final List<ManipulationEvent> events = new ArrayList<>();
    private final List<Boolean> activations = new ArrayList<>();
    private boolean sceneEditor;
    private int undoCalls;

    @Override
    public void triggerManipulationEvent(ManipulationEvent event, boolean isActivate) {
      events.add(event);
      activations.add(isActivate);
    }

    @Override
    public boolean hasSceneEditor() {
      return sceneEditor;
    }

    @Override
    public void undoRedoEndManipulation(AbstractManipulator manipulator, AffineMatrix4x4 originalTransformation) {
      undoCalls++;
    }
  }
}
