package org.lgna.croquet.triggers;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static org.junit.Assert.*;

public class InputEventTriggerBehaviorTest extends TriggerTestSupport {
  @Test
  public void createUserActivity_mouseEvent_exposesPointAndViewController() {
    TestViewController view = new TestViewController();
    MouseEvent event = createMouseEvent(view.getAwtComponent(), MouseEvent.MOUSE_DRAGGED, MouseEvent.BUTTON1, 4, 7);

    UserActivity activity = InputEventTrigger.createUserActivity(event);

    assertTrue(activity.getTrigger() instanceof InputEventTrigger);
    InputEventTrigger trigger = (InputEventTrigger) activity.getTrigger();
    assertSame(event, trigger.getEvent());
    assertSame(view, trigger.getViewController());
    assertEquals(new Point(4, 7), invokePoint(trigger));
  }

  @Test
  public void createUserActivity_keyEvent_hasNoPoint() {
    TestViewController view = new TestViewController();
    KeyEvent event = createKeyEvent(view.getAwtComponent());

    UserActivity activity = InputEventTrigger.createUserActivity(event);

    InputEventTrigger trigger = (InputEventTrigger) activity.getTrigger();
    assertSame(event, trigger.getEvent());
    assertNull(invokePoint(trigger));
  }
}
