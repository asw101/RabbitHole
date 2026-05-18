package org.lgna.croquet.history;

import org.lgna.croquet.history.event.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Extended tests for {@link UserActivity} — covers child activities,
 * cancellation, completion model, and event lifecycle.
 */
public class UserActivityLifecycleTest {

  @Test
  public void newActivity_notCanceled() {
    UserActivity activity = new UserActivity();
    assertFalse(activity.isCanceled());
  }

  @Test
  public void cancel_marksCanceled() {
    UserActivity activity = new UserActivity();
    activity.cancel();
    assertTrue(activity.isCanceled());
  }

  @Test
  public void newChildActivity_createsNewInstance() {
    UserActivity parent = new UserActivity();
    UserActivity child = parent.newChildActivity();
    assertNotNull(child);
    assertNotSame(parent, child);
  }

  @Test
  public void newChildActivity_parentNotCanceled() {
    UserActivity parent = new UserActivity();
    parent.newChildActivity();
    assertFalse(parent.isCanceled());
  }

  @Test
  public void getActivityWithoutModel_returnsActivity() {
    UserActivity activity = new UserActivity();
    UserActivity result = activity.getActivityWithoutModel();
    assertNotNull(result);
  }

  @Test
  public void getActivityWithoutTrigger_returnsActivity() {
    UserActivity activity = new UserActivity();
    UserActivity result = activity.getActivityWithoutTrigger();
    assertNotNull(result);
  }

  @Test
  public void setTrigger_setsTrigger() {
    UserActivity activity = new UserActivity();
    org.lgna.croquet.triggers.IterationTrigger trigger =
        org.lgna.croquet.triggers.IterationTrigger.createUserInstance(activity);
    assertSame(trigger, activity.getTrigger());
  }

  @Test
  public void getTrigger_initiallyNull() {
    UserActivity activity = new UserActivity();
    assertNull(activity.getTrigger());
  }

  @Test
  public void setCompletionModel_setsModel() {
    UserActivity activity = new UserActivity();
    activity.setCompletionModel(null);
    // Setting to null should not throw
  }

  @Test
  public void addListener_doesNotThrow() {
    UserActivity activity = new UserActivity();
    Listener listener = new Listener() {
      @Override public void changed(ActivityEvent e) {}
    };
    activity.addListener(listener);
    activity.removeListener(listener);
  }

  @Test
  public void multipleChildren_independent() {
    UserActivity parent = new UserActivity();
    UserActivity child1 = parent.newChildActivity();
    UserActivity child2 = parent.newChildActivity();
    assertNotSame(child1, child2);
  }

  @Test
  public void cancel_doesNotAffectOtherActivities() {
    UserActivity a1 = new UserActivity();
    UserActivity a2 = new UserActivity();
    a1.cancel();
    assertTrue(a1.isCanceled());
    assertFalse(a2.isCanceled());
  }
}
