package org.lgna.croquet.triggers;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.awt.event.ActionEvent;

import static org.junit.Assert.*;

public class ActionEventTriggerFactoryBehaviorTest extends TriggerTestSupport {
  @Test
  public void createUserActivity_setsTriggerAndExposesEvent() {
    TestViewController view = new TestViewController();
    ActionEvent event = new ActionEvent(view.getAwtComponent(), ActionEvent.ACTION_PERFORMED, "fire");

    UserActivity activity = ActionEventTrigger.createUserActivity(event);

    assertTrue(activity.getTrigger() instanceof ActionEventTrigger);
    ActionEventTrigger trigger = (ActionEventTrigger) activity.getTrigger();
    assertSame(event, trigger.getEvent());
    assertSame(view, trigger.getViewController());
  }
}
