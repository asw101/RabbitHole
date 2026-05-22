package org.lgna.croquet.triggers;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.awt.Point;
import java.awt.event.MouseEvent;

import static org.junit.Assert.*;

public class MouseEventTriggerFactoryBehaviorTest extends TriggerTestSupport {
  @Test
  public void createUserActivity_setsTriggerAndExposesPoint() {
    TestViewController view = new TestViewController();
    MouseEvent event = createMouseEvent(view.getAwtComponent(), MouseEvent.MOUSE_RELEASED, MouseEvent.BUTTON1, 8, 9);

    UserActivity activity = MouseEventTrigger.createUserActivity(view, event);

    assertTrue(activity.getTrigger() instanceof MouseEventTrigger);
    MouseEventTrigger trigger = (MouseEventTrigger) activity.getTrigger();
    assertSame(event, trigger.getEvent());
    assertSame(view, trigger.getViewController());
    assertEquals(new Point(8, 9), invokePoint(trigger));
  }

  @Test
  public void setOnUserActivity_reusesProvidedActivity() {
    TestViewController view = new TestViewController();
    MouseEvent event = createMouseEvent(view.getAwtComponent(), MouseEvent.MOUSE_PRESSED, MouseEvent.BUTTON1, 1, 2);
    UserActivity activity = new UserActivity();

    UserActivity returned = MouseEventTrigger.setOnUserActivity(activity, event);

    assertSame(activity, returned);
    assertTrue(activity.getTrigger() instanceof MouseEventTrigger);
  }
}
