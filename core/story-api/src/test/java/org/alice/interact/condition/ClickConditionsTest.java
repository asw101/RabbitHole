package org.alice.interact.condition;

import org.alice.interact.InputState;
import org.alice.interact.PickHint;
import org.junit.Test;

import java.awt.Point;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClickConditionsTest {
  private static final class ExposedClickedObjectCondition extends ClickedObjectCondition {
    private ExposedClickedObjectCondition() {
      super(1, new PickCondition(new PickHint(PickHint.PickType.NOTHING)));
    }

    public void applyUpdate(InputState current, InputState previous) {
      super.update(current, previous);
    }
  }

  private static final class ExposedDoubleClickedObjectCondition extends DoubleClickedObjectCondition {
    private ExposedDoubleClickedObjectCondition() {
      super(1, new PickCondition(new PickHint(PickHint.PickType.NOTHING)));
    }

    public void applyUpdate(InputState current, InputState previous) {
      super.update(current, previous);
    }
  }

  private static InputState state(boolean mouseDown, int x, int y, long time) {
    InputState state = new InputState();
    state.setMouseState(1, mouseDown);
    state.setMouseLocation(new Point(x, y));
    state.setTimeCaptured(time);
    return state;
  }

  @Test
  public void clickedIsFalseWithoutMouseDownHistory() {
    assertFalse(new ExposedClickedObjectCondition().clicked(state(false, 0, 0, 20), state(true, 0, 0, 10)));
  }

  @Test
  public void clickedIsTrueForShortStationaryClick() {
    ExposedClickedObjectCondition condition = new ExposedClickedObjectCondition();
    InputState previousUp = state(false, 0, 0, 0);
    InputState pressed = state(true, 10, 10, 100);
    condition.applyUpdate(pressed, previousUp);

    assertTrue(condition.clicked(state(false, 10, 10, 200), pressed));
  }

  @Test
  public void clickedIsFalseWhenHeldTooLong() {
    ExposedClickedObjectCondition condition = new ExposedClickedObjectCondition();
    InputState previousUp = state(false, 0, 0, 0);
    InputState pressed = state(true, 10, 10, 100);
    condition.applyUpdate(pressed, previousUp);

    assertFalse(condition.clicked(state(false, 10, 10, 500), pressed));
  }

  @Test
  public void clickedIsFalseWhenMouseMovesTooFar() {
    ExposedClickedObjectCondition condition = new ExposedClickedObjectCondition();
    InputState previousUp = state(false, 0, 0, 0);
    InputState pressed = state(true, 10, 10, 100);
    condition.applyUpdate(pressed, previousUp);

    assertFalse(condition.clicked(state(false, 20, 20, 200), pressed));
  }

  @Test
  public void clickedIsFalseWhenMouseIsStillDown() {
    ExposedClickedObjectCondition condition = new ExposedClickedObjectCondition();
    InputState previousUp = state(false, 0, 0, 0);
    InputState pressed = state(true, 10, 10, 100);
    condition.applyUpdate(pressed, previousUp);

    assertFalse(condition.clicked(state(true, 10, 10, 150), pressed));
  }

  @Test
  public void doubleClickRequiresTwoValidClicks() {
    ExposedDoubleClickedObjectCondition condition = new ExposedDoubleClickedObjectCondition();
    InputState up0 = state(false, 0, 0, 0);
    InputState down1 = state(true, 10, 10, 100);
    InputState up1 = state(false, 10, 10, 150);
    InputState down2 = state(true, 11, 10, 200);
    InputState up2 = state(false, 11, 10, 240);

    condition.applyUpdate(down1, up0);
    condition.applyUpdate(up1, down1);
    condition.applyUpdate(down2, up1);
    condition.applyUpdate(up2, down2);

    assertTrue(condition.clicked(up2, down2));
  }

  @Test
  public void doubleClickFailsWhenClicksAreTooFarApartInTime() {
    ExposedDoubleClickedObjectCondition condition = new ExposedDoubleClickedObjectCondition();
    InputState up0 = state(false, 0, 0, 0);
    InputState down1 = state(true, 10, 10, 100);
    InputState up1 = state(false, 10, 10, 150);
    InputState down2 = state(true, 10, 10, 500);
    InputState up2 = state(false, 10, 10, 520);

    condition.applyUpdate(down1, up0);
    condition.applyUpdate(up1, down1);
    condition.applyUpdate(down2, up1);
    condition.applyUpdate(up2, down2);

    assertFalse(condition.clicked(up2, down2));
  }

  @Test
  public void doubleClickFailsWhenSecondClickMovesTooFar() {
    ExposedDoubleClickedObjectCondition condition = new ExposedDoubleClickedObjectCondition();
    InputState up0 = state(false, 0, 0, 0);
    InputState down1 = state(true, 10, 10, 100);
    InputState up1 = state(false, 10, 10, 150);
    InputState down2 = state(true, 20, 20, 200);
    InputState up2 = state(false, 20, 20, 240);

    condition.applyUpdate(down1, up0);
    condition.applyUpdate(up1, down1);
    condition.applyUpdate(down2, up1);
    condition.applyUpdate(up2, down2);

    assertFalse(condition.clicked(up2, down2));
  }

  @Test
  public void doubleClickIsFalseAfterOnlyOneClick() {
    ExposedDoubleClickedObjectCondition condition = new ExposedDoubleClickedObjectCondition();
    InputState up0 = state(false, 0, 0, 0);
    InputState down1 = state(true, 10, 10, 100);
    InputState up1 = state(false, 10, 10, 150);

    condition.applyUpdate(down1, up0);
    condition.applyUpdate(up1, down1);

    assertFalse(condition.clicked(up1, down1));
  }
}
