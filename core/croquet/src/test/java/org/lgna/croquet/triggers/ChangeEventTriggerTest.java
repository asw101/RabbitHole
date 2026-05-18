package org.lgna.croquet.triggers;

import org.lgna.croquet.history.UserActivity;
import org.junit.Test;

import javax.swing.event.ChangeEvent;

import static org.junit.Assert.*;

/**
 * Tests for {@link ChangeEventTrigger} — trigger wrapping a Swing {@link ChangeEvent}.
 * Covers factory, event storage, UserActivity binding, and repr.
 */
public class ChangeEventTriggerTest {

  @Test
  public void createUserInstance_returnsNonNull() {
    UserActivity activity = new UserActivity();
    ChangeEvent swingEvent = new ChangeEvent(this);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, swingEvent);
    assertNotNull(trigger);
  }

  @Test
  public void createUserInstance_setsActivityTrigger() {
    UserActivity activity = new UserActivity();
    ChangeEvent swingEvent = new ChangeEvent(this);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, swingEvent);
    assertSame(trigger, activity.getTrigger());
  }

  @Test
  public void getUserActivity_returnsBoundActivity() {
    UserActivity activity = new UserActivity();
    ChangeEvent swingEvent = new ChangeEvent(this);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, swingEvent);
    assertSame(activity, trigger.getUserActivity());
  }

  @Test
  public void getEvent_returnsSwingEvent() {
    UserActivity activity = new UserActivity();
    ChangeEvent swingEvent = new ChangeEvent(this);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, swingEvent);
    assertSame(swingEvent, trigger.getEvent());
  }

  @Test
  public void extendsEventObjectTrigger() {
    UserActivity activity = new UserActivity();
    ChangeEvent swingEvent = new ChangeEvent(this);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, swingEvent);
    assertTrue(trigger instanceof EventObjectTrigger);
  }

  @Test
  public void extendsTrigger() {
    UserActivity activity = new UserActivity();
    ChangeEvent swingEvent = new ChangeEvent(this);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, swingEvent);
    assertTrue(trigger instanceof Trigger);
  }

  @Test
  public void appendRepr_containsClassName() {
    UserActivity activity = new UserActivity();
    ChangeEvent swingEvent = new ChangeEvent(this);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, swingEvent);
    StringBuilder sb = new StringBuilder();
    trigger.appendRepr(sb);
    assertTrue(sb.toString().contains("ChangeEventTrigger"));
  }

  @Test
  public void getViewController_nullViewController() {
    UserActivity activity = new UserActivity();
    ChangeEvent swingEvent = new ChangeEvent(this);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, swingEvent);
    // ViewController passed as null in ChangeEventTrigger constructor
    // getViewController falls through to event source lookup
    // Since source is 'this' (not a Component), returns null
    assertNull(trigger.getViewController());
  }

  @Test
  public void twoTriggers_differentEvents() {
    UserActivity a1 = new UserActivity();
    UserActivity a2 = new UserActivity();
    ChangeEvent e1 = new ChangeEvent("source1");
    ChangeEvent e2 = new ChangeEvent("source2");
    ChangeEventTrigger t1 = ChangeEventTrigger.createUserInstance(a1, e1);
    ChangeEventTrigger t2 = ChangeEventTrigger.createUserInstance(a2, e2);
    assertNotSame(t1.getEvent(), t2.getEvent());
  }
}
