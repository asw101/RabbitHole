package org.lgna.croquet.triggers;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public class NullTriggerActivityBehaviorTest extends TriggerTestSupport {
  @Test
  public void createUserActivity_setsNullTriggerOnActivity() {
    UserActivity activity = NullTrigger.createUserActivity();

    assertTrue(activity.getTrigger() instanceof NullTrigger);
    StringBuilder repr = new StringBuilder();
    activity.getTrigger().appendRepr(repr);
    assertTrue(repr.toString().startsWith("NullTrigger["));
  }
}
