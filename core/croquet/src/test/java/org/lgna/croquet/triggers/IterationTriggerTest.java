package org.lgna.croquet.triggers;

import org.lgna.croquet.history.UserActivity;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link IterationTrigger} — trigger created via factory method
 * with a UserActivity. Covers factory, UserActivity binding, and repr.
 */
public class IterationTriggerTest {

  @Test
  public void createUserInstance_returnsNonNull() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    assertNotNull(trigger);
  }

  @Test
  public void createUserInstance_setsActivityTrigger() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    assertSame(trigger, activity.getTrigger());
  }

  @Test
  public void getUserActivity_returnsBoundActivity() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    assertSame(activity, trigger.getUserActivity());
  }

  @Test
  public void getViewController_returnsNull() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    assertNull(trigger.getViewController());
  }

  @Test
  public void appendRepr_containsClassName() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    StringBuilder sb = new StringBuilder();
    trigger.appendRepr(sb);
    assertTrue(sb.toString().contains("IterationTrigger"));
  }

  @Test
  public void appendRepr_containsBrackets() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    StringBuilder sb = new StringBuilder();
    trigger.appendRepr(sb);
    assertTrue(sb.toString().contains("["));
    assertTrue(sb.toString().contains("]"));
  }

  @Test
  public void extendsTrigger() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    assertTrue(trigger instanceof Trigger);
  }

  @Test
  public void twoTriggers_differentActivities() {
    UserActivity a1 = new UserActivity();
    UserActivity a2 = new UserActivity();
    IterationTrigger t1 = IterationTrigger.createUserInstance(a1);
    IterationTrigger t2 = IterationTrigger.createUserInstance(a2);
    assertNotSame(t1, t2);
    assertNotSame(t1.getUserActivity(), t2.getUserActivity());
  }

  @Test
  public void encode_doesNotThrow() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    // encode is a no-op in base Trigger — just verify it doesn't throw
    trigger.encode(null);
  }
}
