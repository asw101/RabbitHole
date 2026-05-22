package org.lgna.croquet.triggers;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.awt.event.KeyEvent;

import static org.junit.Assert.*;

public class KeyEventTriggerFactoryBehaviorTest extends TriggerTestSupport {
  @Test
  public void createUserActivity_setsTriggerAndExposesViewController() {
    TestViewController view = new TestViewController();
    KeyEvent event = createKeyEvent(view.getAwtComponent());

    UserActivity activity = KeyEventTrigger.createUserActivity(view, event);

    assertTrue(activity.getTrigger() instanceof KeyEventTrigger);
    KeyEventTrigger trigger = (KeyEventTrigger) activity.getTrigger();
    assertSame(event, trigger.getEvent());
    assertSame(view, trigger.getViewController());
  }
}
