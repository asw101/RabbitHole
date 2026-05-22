package org.lgna.croquet.triggers;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import javax.swing.event.ChangeEvent;

import static org.junit.Assert.*;

public class ChangeEventTriggerFactoryBehaviorTest extends TriggerTestSupport {
  @Test
  public void createUserInstance_reusesProvidedActivity() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent(new Object());

    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);

    assertSame(activity, trigger.getUserActivity());
    assertSame(event, trigger.getEvent());
    assertNull(trigger.getViewController());
  }
}
