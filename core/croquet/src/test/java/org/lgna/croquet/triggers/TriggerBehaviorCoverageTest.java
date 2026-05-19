package org.lgna.croquet.triggers;

import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import javax.swing.event.ChangeEvent;

import static org.junit.Assert.*;

/**
 * Behavioral coverage tests for triggers that can be constructed headlessly:
 * {@link IterationTrigger}, {@link ChangeEventTrigger}, and
 * {@link CascadeAutomaticDeterminationTrigger}.
 */
public class TriggerBehaviorCoverageTest {

  // ═══════════════════════════════════════════════════════════════════
  //  IterationTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void iterationTrigger_createUserInstance_returnsNonNull() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    assertNotNull(trigger);
  }

  @Test
  public void iterationTrigger_setsOnActivity() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    assertSame(trigger, activity.getTrigger());
  }

  @Test
  public void iterationTrigger_getUserActivity() {
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
  public void iterationTrigger_encode_noException() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    trigger.encode(encoder);
    // Encode is no-op for base Trigger — just verify no exception
  }

  @Test
  public void iterationTrigger_appendRepr_includesClassName() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    StringBuilder sb = new StringBuilder();
    trigger.appendRepr(sb);
    assertTrue(sb.toString().contains("IterationTrigger"));
  }

  @Test
  public void iterationTrigger_appendRepr_hasBrackets() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    StringBuilder sb = new StringBuilder();
    trigger.appendRepr(sb);
    assertTrue(sb.toString().contains("["));
    assertTrue(sb.toString().contains("]"));
  }

  @Test
  public void iterationTrigger_extendsAbstractTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(IterationTrigger.class));
  }

  // ═══════════════════════════════════════════════════════════════════
  //  ChangeEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void changeEventTrigger_createUserInstance_returnsNonNull() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent(this);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    assertNotNull(trigger);
  }

  @Test
  public void changeEventTrigger_setsOnActivity() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent(this);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    assertSame(trigger, activity.getTrigger());
  }

  @Test
  public void changeEventTrigger_getUserActivity() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent(this);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    assertSame(activity, trigger.getUserActivity());
  }

  @Test
  public void changeEventTrigger_getEvent_returnsEvent() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent(this);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    assertSame(event, trigger.getEvent());
  }

  @Test
  public void changeEventTrigger_getViewController_returnsNull() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent(this);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    assertNull(trigger.getViewController());
  }

  @Test
  public void changeEventTrigger_encode_noException() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent(this);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    trigger.encode(encoder);
  }

  @Test
  public void changeEventTrigger_extendsEventObjectTrigger() {
    assertTrue(EventObjectTrigger.class.isAssignableFrom(ChangeEventTrigger.class));
  }

  @Test
  public void changeEventTrigger_extendsTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(ChangeEventTrigger.class));
  }

  // ═══════════════════════════════════════════════════════════════════
  //  CascadeAutomaticDeterminationTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void cascadeTrigger_createChildActivity_returnsChildActivity() {
    UserActivity parent = new UserActivity();
    // The parent needs a trigger set for CascadeAutomaticDeterminationTrigger
    IterationTrigger parentTrigger = IterationTrigger.createUserInstance(parent);

    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    assertNotNull(child);
  }

  @Test
  public void cascadeTrigger_childActivity_hasTriggerSet() {
    UserActivity parent = new UserActivity();
    IterationTrigger.createUserInstance(parent);

    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    assertNotNull(child.getTrigger());
    assertTrue(child.getTrigger() instanceof CascadeAutomaticDeterminationTrigger);
  }

  @Test
  public void cascadeTrigger_childActivity_isDifferentFromParent() {
    UserActivity parent = new UserActivity();
    IterationTrigger.createUserInstance(parent);

    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    assertNotSame(parent, child);
  }

  @Test
  public void cascadeTrigger_getViewController_delegatesToPrevious() {
    UserActivity parent = new UserActivity();
    IterationTrigger.createUserInstance(parent);

    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    CascadeAutomaticDeterminationTrigger trigger = (CascadeAutomaticDeterminationTrigger) child.getTrigger();
    // IterationTrigger returns null for getViewController, so cascade should too
    assertNull(trigger.getViewController());
  }

  @Test
  public void cascadeTrigger_extendsTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(CascadeAutomaticDeterminationTrigger.class));
  }

  @Test
  public void cascadeTrigger_encode_noException() {
    UserActivity parent = new UserActivity();
    IterationTrigger.createUserInstance(parent);

    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    CascadeAutomaticDeterminationTrigger trigger = (CascadeAutomaticDeterminationTrigger) child.getTrigger();
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    trigger.encode(encoder);
  }

  // ═══════════════════════════════════════════════════════════════════
  //  Multiple trigger creation
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void multipleIterationTriggers_differentActivities() {
    UserActivity a1 = new UserActivity();
    UserActivity a2 = new UserActivity();
    IterationTrigger t1 = IterationTrigger.createUserInstance(a1);
    IterationTrigger t2 = IterationTrigger.createUserInstance(a2);
    assertNotSame(t1, t2);
    assertSame(t1, a1.getTrigger());
    assertSame(t2, a2.getTrigger());
  }

  @Test
  public void multipleChangeEventTriggers_differentActivities() {
    UserActivity a1 = new UserActivity();
    UserActivity a2 = new UserActivity();
    ChangeEvent e1 = new ChangeEvent(new Object());
    ChangeEvent e2 = new ChangeEvent(new Object());
    ChangeEventTrigger t1 = ChangeEventTrigger.createUserInstance(a1, e1);
    ChangeEventTrigger t2 = ChangeEventTrigger.createUserInstance(a2, e2);
    assertNotSame(t1, t2);
    assertSame(e1, t1.getEvent());
    assertSame(e2, t2.getEvent());
  }
}
