package org.lgna.croquet.triggers;
import org.lgna.croquet.history.UserActivity;
import org.junit.Test;
import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import static org.junit.Assert.*;
public class MouseEventTriggerTest {
  private MouseEvent ev() { return new MouseEvent(new JPanel(), MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 10, 20, 1, false); }
  @Test public void setOnUserActivity_setsTrigger() { UserActivity a=new UserActivity(); MouseEventTrigger.setOnUserActivity(a, ev()); assertNotNull(a.getTrigger()); assertTrue(a.getTrigger() instanceof MouseEventTrigger); }
  @Test public void triggerHasActivity() { UserActivity a=new UserActivity(); MouseEventTrigger.setOnUserActivity(a, ev()); assertSame(a, a.getTrigger().getUserActivity()); }
  @Test public void getEvent() { UserActivity a=new UserActivity(); MouseEvent m=ev(); MouseEventTrigger.setOnUserActivity(a, m); assertSame(m, ((MouseEventTrigger)a.getTrigger()).getEvent()); }
  @Test public void extendsAbstractMouse() { UserActivity a=new UserActivity(); MouseEventTrigger.setOnUserActivity(a, ev()); assertTrue(a.getTrigger() instanceof AbstractMouseEventTrigger); }
  @Test public void extendsTrigger() { UserActivity a=new UserActivity(); MouseEventTrigger.setOnUserActivity(a, ev()); assertTrue(a.getTrigger() instanceof Trigger); }
  @Test public void appendRepr() { UserActivity a=new UserActivity(); MouseEventTrigger.setOnUserActivity(a, ev()); StringBuilder sb=new StringBuilder(); a.getTrigger().appendRepr(sb); assertTrue(sb.toString().contains("MouseEventTrigger")); }
  @Test public void twoActivities() { UserActivity a1=new UserActivity(),a2=new UserActivity(); MouseEventTrigger.setOnUserActivity(a1, ev()); MouseEventTrigger.setOnUserActivity(a2, ev()); assertNotSame(a1.getTrigger(), a2.getTrigger()); }
  @Test public void encode() { UserActivity a=new UserActivity(); MouseEventTrigger.setOnUserActivity(a, ev()); a.getTrigger().encode(null); }
}
