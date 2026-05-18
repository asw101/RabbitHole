package org.lgna.croquet;

import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.triggers.IterationTrigger;
import org.lgna.croquet.triggers.Trigger;
import org.lgna.croquet.triggers.CascadeAutomaticDeterminationTrigger;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for the {@link org.lgna.croquet.triggers.Trigger} class hierarchy —
 * contract tests exercised through multiple concrete trigger types.
 */
public class TriggerContractTest {

  @Test
  public void trigger_setsActivityTrigger() {
    UserActivity activity = new UserActivity();
    assertNull(activity.getTrigger());
    IterationTrigger.createUserInstance(activity);
    assertNotNull(activity.getTrigger());
  }

  @Test
  public void trigger_getUserActivity_returnsBoundActivity() {
    UserActivity activity = new UserActivity();
    Trigger trigger = IterationTrigger.createUserInstance(activity);
    assertSame(activity, trigger.getUserActivity());
  }

  @Test
  public void trigger_appendRepr_format() {
    UserActivity activity = new UserActivity();
    Trigger trigger = IterationTrigger.createUserInstance(activity);
    StringBuilder sb = new StringBuilder();
    trigger.appendRepr(sb);
    String repr = sb.toString();
    assertTrue(repr.startsWith("IterationTrigger"));
    assertTrue(repr.contains("["));
    assertTrue(repr.contains("]"));
  }

  @Test
  public void trigger_getViewController_defaultNull() {
    UserActivity activity = new UserActivity();
    Trigger trigger = IterationTrigger.createUserInstance(activity);
    assertNull(trigger.getViewController());
  }

  @Test
  public void trigger_encode_noOp() {
    UserActivity activity = new UserActivity();
    Trigger trigger = IterationTrigger.createUserInstance(activity);
    trigger.encode(null); // no-op
  }

  @Test
  public void trigger_cascadeChildDelegatesView() {
    UserActivity parent = new UserActivity();
    IterationTrigger.createUserInstance(parent);
    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    // Cascade delegates to parent's trigger, which returns null
    assertNull(child.getTrigger().getViewController());
  }

  @Test
  public void trigger_replacingTriggerOnActivity() {
    UserActivity activity = new UserActivity();
    Trigger t1 = IterationTrigger.createUserInstance(activity);
    assertSame(t1, activity.getTrigger());

    // Second trigger replaces first
    Trigger t2 = IterationTrigger.createUserInstance(activity);
    assertSame(t2, activity.getTrigger());
    assertNotSame(t1, t2);
  }

  @Test
  public void trigger_childAndParent_independentTriggers() {
    UserActivity parent = new UserActivity();
    IterationTrigger parentTrigger = IterationTrigger.createUserInstance(parent);

    UserActivity child = parent.newChildActivity();
    IterationTrigger childTrigger = IterationTrigger.createUserInstance(child);

    assertNotSame(parentTrigger, childTrigger);
    assertSame(parentTrigger, parent.getTrigger());
    assertSame(childTrigger, child.getTrigger());
  }
}
