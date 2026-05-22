package org.lgna.croquet.triggers;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import javax.swing.event.PopupMenuEvent;

import static org.junit.Assert.*;

public class PopupMenuEventTriggerFactoryBehaviorTest extends TriggerTestSupport {
  @Test
  public void createUserActivity_setsTriggerAndExposesViewController() {
    TestViewController view = new TestViewController();
    PopupMenuEvent event = new PopupMenuEvent(view.getAwtComponent());

    UserActivity activity = PopupMenuEventTrigger.createUserActivity(view, event);

    assertTrue(activity.getTrigger() instanceof PopupMenuEventTrigger);
    PopupMenuEventTrigger trigger = (PopupMenuEventTrigger) activity.getTrigger();
    assertSame(event, trigger.getEvent());
    assertSame(view, trigger.getViewController());
  }
}
