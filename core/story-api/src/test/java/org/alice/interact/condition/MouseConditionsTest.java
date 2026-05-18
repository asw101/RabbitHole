package org.alice.interact.condition;

import org.alice.interact.InputState;
import org.alice.interact.PickHint;
import org.junit.Test;

import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MouseConditionsTest {
  private static final class ExposedMouseCondition extends MouseCondition {
    private ExposedMouseCondition() {
      super(1, new PickCondition(new PickHint(PickHint.PickType.NOTHING)));
    }

    public boolean test(InputState state) {
      return super.testState(state);
    }
  }

  private static final class ExposedMouseWheelCondition extends MouseWheelCondition {
    public boolean test(InputState state) {
      return super.testState(state);
    }
  }

  private static InputState buttonState(boolean down) {
    InputState state = new InputState();
    state.setMouseState(1, down);
    state.setMouseLocation(new Point(0, 0));
    return state;
  }

  private static InputState wheelState(int amount, MouseWheelEvent event) {
    InputState state = new InputState();
    state.setMouseWheelState(amount);
    state.setInputEvent(event);
    return state;
  }

  private static MouseWheelEvent wheelEvent(int rotation) {
    return new MouseWheelEvent(new Canvas(), MouseWheelEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0,
        1, 1, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, rotation);
  }

  @Test
  public void mouseConditionMatchesButtonDown() {
    assertTrue(new ExposedMouseCondition().test(buttonState(true)));
  }

  @Test
  public void mouseConditionRejectsButtonUp() {
    assertFalse(new ExposedMouseCondition().test(buttonState(false)));
  }

  @Test
  public void mouseConditionRejectsDragEvents() {
    InputState state = buttonState(true);
    state.setIsDragEvent(true);
    assertFalse(new ExposedMouseCondition().test(state));
  }

  @Test
  public void mouseConditionClickedIsAlwaysFalse() {
    assertFalse(new ExposedMouseCondition().clicked(buttonState(false), buttonState(true)));
  }

  @Test
  public void mousePressConditionJustStartedOnPress() {
    MousePressCondition condition = new MousePressCondition(1, new PickCondition(new PickHint(PickHint.PickType.NOTHING)));
    assertTrue(condition.justStarted(buttonState(true), buttonState(false)));
  }

  @Test
  public void mousePressConditionStateChangedOnPress() {
    MousePressCondition condition = new MousePressCondition(1, new PickCondition(new PickHint(PickHint.PickType.NOTHING)));
    assertTrue(condition.stateChanged(buttonState(true), buttonState(false)));
  }

  @Test
  public void mousePressConditionJustEndedAfterRelease() {
    MousePressCondition condition = new MousePressCondition(1, new PickCondition(new PickHint(PickHint.PickType.NOTHING)));
    InputState up = buttonState(false);
    InputState down = buttonState(true);
    condition.justStarted(down, up);
    assertTrue(condition.justEnded(up, down));
  }

  @Test
  public void mouseWheelConditionMatchesNonZeroWheelMovement() {
    assertTrue(new ExposedMouseWheelCondition().test(wheelState(1, wheelEvent(1))));
  }

  @Test
  public void mouseWheelConditionRejectsZeroWheelMovement() {
    assertFalse(new ExposedMouseWheelCondition().test(wheelState(0, wheelEvent(0))));
  }

  @Test
  public void mouseWheelConditionStateChangedTracksDistinctEvents() {
    MouseWheelCondition condition = new MouseWheelCondition();
    assertTrue(condition.stateChanged(wheelState(1, wheelEvent(1)), wheelState(1, wheelEvent(1))));
  }
}
