package org.alice.interact.condition;

import org.alice.interact.InputState;
import org.alice.interact.PickHint;
import org.junit.Test;

import java.awt.Point;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MouseDragConditionTest {
  private static final class ExposedMouseDragCondition extends MouseDragCondition {
    private ExposedMouseDragCondition(int mouseButton, PickCondition pickCondition) {
      super(mouseButton, pickCondition);
    }

    public boolean test(InputState state) {
      return super.testState(state);
    }
  }

  private static InputState state(boolean mouseDown, int x, int y) {
    InputState state = new InputState();
    state.setMouseState(1, mouseDown);
    state.setMouseLocation(new Point(x, y));
    return state;
  }

  private static ExposedMouseDragCondition newCondition() {
    return new ExposedMouseDragCondition(1, new PickCondition(new PickHint(PickHint.PickType.NOTHING)));
  }

  @Test
  public void testStateIsFalseWhenMouseButtonIsUp() {
    assertFalse(newCondition().test(state(false, 0, 0)));
  }

  @Test
  public void justStartedDoesNotFireOnInitialMouseDown() {
    MouseDragCondition condition = newCondition();
    assertFalse(condition.justStarted(state(true, 0, 0), state(false, 0, 0)));
  }

  @Test
  public void justStartedFiresAfterMovingPastThreshold() {
    MouseDragCondition condition = newCondition();
    InputState previousUp = state(false, 0, 0);
    InputState pressed = state(true, 0, 0);
    condition.justStarted(pressed, previousUp);

    assertTrue(condition.justStarted(state(true, 3, 0), pressed));
  }

  @Test
  public void justStartedRequiresAtLeastTwoPixelsOfMovement() {
    MouseDragCondition condition = newCondition();
    InputState previousUp = state(false, 0, 0);
    InputState pressed = state(true, 0, 0);
    condition.justStarted(pressed, previousUp);

    assertFalse(condition.justStarted(state(true, 1, 1), pressed));
  }

  @Test
  public void isRunningBecomesTrueAfterDragStarts() {
    MouseDragCondition condition = newCondition();
    InputState previousUp = state(false, 0, 0);
    InputState pressed = state(true, 0, 0);
    InputState dragged = state(true, 3, 0);
    condition.justStarted(pressed, previousUp);
    condition.justStarted(dragged, pressed);

    assertTrue(condition.isRunning(state(true, 4, 0), dragged));
  }

  @Test
  public void justEndedFiresWhenMouseReleasedAfterRunning() {
    MouseDragCondition condition = newCondition();
    InputState previousUp = state(false, 0, 0);
    InputState pressed = state(true, 0, 0);
    InputState dragged = state(true, 3, 0);
    condition.justStarted(pressed, previousUp);
    condition.justStarted(dragged, pressed);

    assertTrue(condition.justEnded(state(false, 3, 0), dragged));
  }

  @Test
  public void stateChangedTracksMouseMovementWhileDown() {
    MouseDragCondition condition = newCondition();
    assertTrue(condition.stateChanged(state(true, 5, 5), state(true, 0, 0)));
  }

  @Test
  public void invalidCurrentStateClearsPendingStart() {
    MouseDragCondition condition = newCondition();
    InputState previousUp = state(false, 0, 0);
    InputState pressed = state(true, 0, 0);
    condition.justStarted(pressed, previousUp);
    condition.stateChanged(state(false, 0, 0), pressed);

    assertFalse(condition.justStarted(state(true, 3, 0), state(true, 0, 0)));
  }

  @Test
  public void clickedIsAlwaysFalse() {
    assertFalse(newCondition().clicked(state(false, 0, 0), state(true, 0, 0)));
  }
}
