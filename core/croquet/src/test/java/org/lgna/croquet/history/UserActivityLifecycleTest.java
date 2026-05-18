package org.lgna.croquet.history;
import org.lgna.croquet.history.event.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class UserActivityLifecycleTest {
  @Test public void newActivity_notCanceled() { assertFalse(new UserActivity().isCanceled()); }
  @Test public void cancel_marksCanceled() { UserActivity a = new UserActivity(); a.cancel(); assertTrue(a.isCanceled()); }
  @Test public void newChildActivity_createsNewInstance() {
    UserActivity p = new UserActivity(); UserActivity c = p.newChildActivity();
    assertNotNull(c); assertNotSame(p, c);
  }
  @Test public void newChildActivity_parentNotCanceled() {
    UserActivity p = new UserActivity(); p.newChildActivity(); assertFalse(p.isCanceled());
  }
  @Test public void getActivityWithoutModel_returnsActivity() { assertNotNull(new UserActivity().getActivityWithoutModel()); }
  @Test public void getActivityWithoutTrigger_returnsActivity() { assertNotNull(new UserActivity().getActivityWithoutTrigger()); }
  @Test public void setTrigger_setsTrigger() {
    UserActivity a = new UserActivity();
    org.lgna.croquet.triggers.IterationTrigger t = org.lgna.croquet.triggers.IterationTrigger.createUserInstance(a);
    assertSame(t, a.getTrigger());
  }
  @Test public void getTrigger_initiallyNull() { assertNull(new UserActivity().getTrigger()); }
  @Test public void setCompletionModel_null_doesNotThrow() { new UserActivity().setCompletionModel(null); }
  @Test public void addListener_doesNotThrow() {
    UserActivity a = new UserActivity();
    Listener l = new Listener() {
      @Override public void changed(ActivityEvent e) {}
    }; a.addListener(l);
  }
  @Test public void multipleChildren_independent() {
    UserActivity p = new UserActivity(); assertNotSame(p.newChildActivity(), p.newChildActivity());
  }
  @Test public void cancel_doesNotAffectOtherActivities() {
    UserActivity a1 = new UserActivity(); UserActivity a2 = new UserActivity();
    a1.cancel(); assertTrue(a1.isCanceled()); assertFalse(a2.isCanceled());
  }
}
