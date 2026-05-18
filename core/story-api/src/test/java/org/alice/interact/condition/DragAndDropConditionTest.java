package org.alice.interact.condition;

import org.alice.interact.InputState;
import org.junit.Test;

import java.awt.Point;

import static org.junit.Assert.*;

/** Headless-safe lifecycle tests for DragAndDropCondition. */
public class DragAndDropConditionTest {

  @Test
  public void justStartedReturnsTrueWhenDragBegins() {
    DragAndDropCondition condition = new DragAndDropCondition();
    assertTrue(condition.justStarted(state(true, 10, 10), state(false, 10, 10)));
  }

  @Test
  public void justStartedReturnsFalseWithoutTransition() {
    DragAndDropCondition condition = new DragAndDropCondition();
    condition.justStarted(state(true, 10, 10), state(false, 10, 10));
    assertFalse(condition.justStarted(state(true, 10, 10), state(true, 10, 10)));
  }

  @Test
  public void isRunningIsFalseBeforeConditionHasStarted() {
    DragAndDropCondition condition = new DragAndDropCondition();
    assertFalse(condition.isRunning(state(true, 10, 10), state(true, 10, 10)));
  }

  @Test
  public void isRunningIsTrueAfterStartWhileBothStatesAreDragging() {
    DragAndDropCondition condition = new DragAndDropCondition();
    InputState previous = state(false, 10, 10);
    InputState current = state(true, 10, 10);
    condition.justStarted(current, previous);
    assertTrue(condition.isRunning(state(true, 20, 20), current));
  }

  @Test
  public void stateChangedReturnsTrueWhenMouseMovesDuringDrag() {
    DragAndDropCondition condition = new DragAndDropCondition();
    condition.justStarted(state(true, 10, 10), state(false, 10, 10));
    assertTrue(condition.stateChanged(state(true, 11, 10), state(true, 10, 10)));
  }

  @Test
  public void stateChangedReturnsFalseWhenNothingChanges() {
    DragAndDropCondition condition = new DragAndDropCondition();
    condition.justStarted(state(true, 10, 10), state(false, 10, 10));
    assertFalse(condition.stateChanged(state(true, 10, 10), state(true, 10, 10)));
  }

  @Test
  public void justEndedReturnsTrueWhenDragStopsAfterStarting() {
    DragAndDropCondition condition = new DragAndDropCondition();
    InputState previous = state(false, 10, 10);
    InputState current = state(true, 10, 10);
    condition.justStarted(current, previous);
    assertTrue(condition.justEnded(state(false, 10, 10), current));
  }

  @Test
  public void justEndedReturnsFalseWithoutPriorStart() {
    DragAndDropCondition condition = new DragAndDropCondition();
    assertFalse(condition.justEnded(state(false, 10, 10), state(true, 10, 10)));
  }

  @Test
  public void conditionCanBeStartedAgainAfterEnding() {
    DragAndDropCondition condition = new DragAndDropCondition();
    InputState idle = state(false, 0, 0);
    InputState drag = state(true, 0, 0);
    assertTrue(condition.justStarted(drag, idle));
    assertTrue(condition.justEnded(idle, drag));
    assertTrue(condition.justStarted(state(true, 5, 5), idle));
  }

  private static InputState state(boolean isDrag, int x, int y) {
    InputState state = new InputState();
    state.setIsDragEvent(isDrag);
    state.setMouseLocation(new Point(x, y));
    return state;
  }
}
