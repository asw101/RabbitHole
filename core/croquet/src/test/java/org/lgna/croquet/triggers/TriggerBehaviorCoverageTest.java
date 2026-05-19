package org.lgna.croquet.triggers;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import javax.swing.event.ChangeEvent;

import static org.junit.Assert.*;

/**
 * Behavioral coverage tests for constructable triggers:
 * {@link IterationTrigger}, {@link ChangeEventTrigger}, and
 * {@link CascadeAutomaticDeterminationTrigger} — construction,
 * UserActivity attachment, ViewController, and encode.
 */
public class TriggerBehaviorCoverageTest {

  // ═══════════════════════════════════════════════════════════════════
  // IterationTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void iterationTrigger_constructsWithUserActivity() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    assertNotNull(trigger);
  }

  @Test
  public void iterationTrigger_getUserActivity_returnsSameActivity() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    assertSame(activity, trigger.getUserActivity());
  }

  @Test
  public void iterationTrigger_getViewController_returnsNull() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    assertNull(trigger.getViewController());
  }

  @Test
  public void iterationTrigger_encode_doesNotThrow() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder =
        new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
    trigger.encode(encoder);
  }

  @Test
  public void iterationTrigger_appendRepr_containsClassName() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    StringBuilder sb = new StringBuilder();
    trigger.appendRepr(sb);
    assertTrue(sb.toString().contains("IterationTrigger"));
  }

  @Test
  public void iterationTrigger_setsTriggerOnActivity() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    assertSame(trigger, activity.getTrigger());
  }

  @Test
  public void iterationTrigger_multipleInstances_distinct() {
    UserActivity a1 = new UserActivity();
    UserActivity a2 = new UserActivity();
    IterationTrigger t1 = IterationTrigger.createUserInstance(a1);
    IterationTrigger t2 = IterationTrigger.createUserInstance(a2);
    assertNotSame(t1, t2);
  }

  // ═══════════════════════════════════════════════════════════════════
  // ChangeEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void changeEventTrigger_constructs() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent(new Object());
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    assertNotNull(trigger);
  }

  @Test
  public void changeEventTrigger_getUserActivity() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent(new Object());
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    assertSame(activity, trigger.getUserActivity());
  }

  @Test
  public void changeEventTrigger_getEvent_returnsEvent() {
    UserActivity activity = new UserActivity();
    Object source = new Object();
    ChangeEvent event = new ChangeEvent(source);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    assertSame(event, trigger.getEvent());
  }

  @Test
  public void changeEventTrigger_setsTriggerOnActivity() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent(new Object());
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    assertSame(trigger, activity.getTrigger());
  }

  @Test
  public void changeEventTrigger_encode_doesNotThrow() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent(new Object());
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder =
        new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
    trigger.encode(encoder);
  }

  @Test
  public void changeEventTrigger_appendRepr() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent(new Object());
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    StringBuilder sb = new StringBuilder();
    trigger.appendRepr(sb);
    assertTrue(sb.toString().contains("ChangeEventTrigger"));
  }

  @Test
  public void changeEventTrigger_getViewController_returnsNull() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent(new Object());
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    assertNull(trigger.getViewController());
  }

  @Test
  public void changeEventTrigger_nullEvent() {
    UserActivity activity = new UserActivity();
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, null);
    assertNotNull(trigger);
    assertNull(trigger.getEvent());
  }

  // ═══════════════════════════════════════════════════════════════════
  // CascadeAutomaticDeterminationTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void cascadeTrigger_createsChildActivity() {
    UserActivity child = createCascadeChild();
    assertNotNull(child);
  }

  @Test
  public void cascadeTrigger_childActivityHasTrigger() {
    UserActivity child = createCascadeChild();
    assertNotNull(child.getTrigger());
  }

  @Test
  public void cascadeTrigger_triggerIsCascadeType() {
    UserActivity child = createCascadeChild();
    assertTrue(child.getTrigger() instanceof CascadeAutomaticDeterminationTrigger);
  }

  @Test
  public void cascadeTrigger_getViewController_delegatesToPrevious() {
    UserActivity child = createCascadeChild();
    CascadeAutomaticDeterminationTrigger trigger =
        (CascadeAutomaticDeterminationTrigger) child.getTrigger();
    assertNull(trigger.getViewController());
  }

  @Test
  public void cascadeTrigger_encode_doesNotThrow() {
    UserActivity child = createCascadeChild();
    edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder =
        new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
    child.getTrigger().encode(encoder);
  }

  @Test
  public void cascadeTrigger_appendRepr() {
    UserActivity child = createCascadeChild();
    StringBuilder sb = new StringBuilder();
    child.getTrigger().appendRepr(sb);
    assertTrue(sb.toString().contains("CascadeAutomaticDeterminationTrigger"));
  }

  // ═══════════════════════════════════════════════════════════════════
  // UserActivity integration
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void userActivity_newChildActivity_createsChild() {
    UserActivity parent = new UserActivity();
    UserActivity child = parent.newChildActivity();
    assertNotNull(child);
    assertNotSame(parent, child);
  }

  @Test
  public void userActivity_multipleChildren() {
    UserActivity parent = new UserActivity();
    UserActivity c1 = parent.newChildActivity();
    UserActivity c2 = parent.newChildActivity();
    assertNotSame(c1, c2);
  }

  @Test
  public void userActivity_defaultTrigger_isNull() {
    UserActivity activity = new UserActivity();
    assertNull(activity.getTrigger());
  }

  @Test
  public void userActivity_afterTrigger_triggerSet() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    assertNotNull(activity.getTrigger());
    assertSame(trigger, activity.getTrigger());
  }

  private static UserActivity createCascadeChild() {
    UserActivity parent = new UserActivity();
    IterationTrigger.createUserInstance(parent);
    return CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
  }
}
