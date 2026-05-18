package org.lgna.croquet.triggers;

import org.lgna.croquet.history.UserActivity;
import org.junit.Test;

import javax.swing.event.ChangeEvent;
import java.util.EventObject;

import static org.junit.Assert.*;

/**
 * Tests for {@link EventObjectTrigger} — abstract trigger that wraps an {@link EventObject}.
 * Tested through concrete subclass {@link ChangeEventTrigger}.
 */
public class EventObjectTriggerTest {

  @Test
  public void getEvent_returnsStoredEvent() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent("testSource");
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    assertSame(event, trigger.getEvent());
  }

  @Test
  public void getEvent_nullEvent() {
    UserActivity activity = new UserActivity();
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, null);
    assertNull(trigger.getEvent());
  }

  @Test
  public void getUserActivity_returnsActivity() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent("source");
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    assertSame(activity, trigger.getUserActivity());
  }

  @Test
  public void getViewController_noComponentSource_returnsNull() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent("stringSource");
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    assertNull(trigger.getViewController());
  }

  @Test
  public void appendRepr_includesSubclassName() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent("src");
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    StringBuilder sb = new StringBuilder();
    trigger.appendRepr(sb);
    String repr = sb.toString();
    assertTrue(repr.contains("ChangeEventTrigger"));
    assertTrue(repr.contains("["));
    assertTrue(repr.contains("]"));
  }

  @Test
  public void eventSource_isAccessible() {
    String source = "mySource";
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent(source);
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    assertSame(source, trigger.getEvent().getSource());
  }

  @Test
  public void encode_noOp_doesNotThrow() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent("src");
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);
    trigger.encode(null); // base Trigger.encode is a no-op
  }
}
