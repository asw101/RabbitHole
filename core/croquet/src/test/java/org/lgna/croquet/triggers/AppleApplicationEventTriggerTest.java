package org.lgna.croquet.triggers;
import org.lgna.croquet.history.UserActivity;
import org.junit.Test;
import static org.junit.Assert.*;
public class AppleApplicationEventTriggerTest {
  @Test public void setOnUserActivity() { UserActivity a=new UserActivity(); AppleApplicationEventTrigger.setOnUserActivity(a, null); assertNotNull(a.getTrigger()); assertTrue(a.getTrigger() instanceof AppleApplicationEventTrigger); }
  @Test public void returnsActivity() { UserActivity a=new UserActivity(); assertSame(a, AppleApplicationEventTrigger.setOnUserActivity(a, null)); }
  @Test public void getEvent_null() { UserActivity a=new UserActivity(); AppleApplicationEventTrigger.setOnUserActivity(a, null); assertNull(((AppleApplicationEventTrigger)a.getTrigger()).getEvent()); }
  @Test public void extendsEventObject() { UserActivity a=new UserActivity(); AppleApplicationEventTrigger.setOnUserActivity(a, null); assertTrue(a.getTrigger() instanceof EventObjectTrigger); }
  @Test public void extendsTrigger() { UserActivity a=new UserActivity(); AppleApplicationEventTrigger.setOnUserActivity(a, null); assertTrue(a.getTrigger() instanceof Trigger); }
  @Test public void appendRepr() { UserActivity a=new UserActivity(); AppleApplicationEventTrigger.setOnUserActivity(a, null); StringBuilder sb=new StringBuilder(); a.getTrigger().appendRepr(sb); assertTrue(sb.toString().contains("AppleApplicationEventTrigger")); }
  @Test public void getUserActivity() { UserActivity a=new UserActivity(); AppleApplicationEventTrigger.setOnUserActivity(a, null); assertSame(a, a.getTrigger().getUserActivity()); }
  @Test public void twoActivities() { UserActivity a1=new UserActivity(),a2=new UserActivity(); AppleApplicationEventTrigger.setOnUserActivity(a1, null); AppleApplicationEventTrigger.setOnUserActivity(a2, null); assertNotSame(a1.getTrigger(), a2.getTrigger()); }
}
