package org.lgna.croquet.triggers;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import static org.junit.Assert.*;

public class IterationTriggerBehaviorTest extends TriggerTestSupport {
  @Test
  public void createUserInstance_reusesActivityAndBuildsRepr() {
    UserActivity activity = new UserActivity();

    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);

    assertSame(activity, trigger.getUserActivity());
    assertSame(trigger, activity.getTrigger());
    StringBuilder repr = new StringBuilder();
    trigger.appendRepr(repr);
    assertTrue(repr.toString().startsWith("IterationTrigger["));
  }
}
