package org.lgna.croquet.triggers;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import static org.junit.Assert.*;

public class AppleApplicationEventTriggerBehaviorTest extends TriggerTestSupport {
  @Test
  public void setOnUserActivity_reusesActivityAndStoresEvent() {
    UserActivity activity = new UserActivity();
    UserActivity returned = AppleApplicationEventTrigger.setOnUserActivity(activity, null);

    assertSame(activity, returned);
    assertTrue(activity.getTrigger() instanceof AppleApplicationEventTrigger);
    AppleApplicationEventTrigger trigger = (AppleApplicationEventTrigger) activity.getTrigger();
    assertNull(trigger.getEvent());
  }
}
