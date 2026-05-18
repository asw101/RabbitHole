package org.lgna.croquet.triggers;
import org.lgna.croquet.history.UserActivity;
import org.junit.Test;
import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import static org.junit.Assert.*;
public class DropTriggerTest {
  private MouseEvent ev() { return new MouseEvent(new JPanel(), MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 5, 10, 1, false); }
  @Test public void setOnUserActivity() { UserActivity a=new UserActivity(); DropTrigger.setOnUserActivity(a, null, ev(), null); assertNotNull(a.getTrigger()); assertTrue(a.getTrigger() instanceof DropTrigger); }
  @Test public void returnsActivity() { UserActivity a=new UserActivity(); assertSame(a, DropTrigger.setOnUserActivity(a, null, ev(), null)); }
  @Test public void getDropSite_null() { UserActivity a=new UserActivity(); DropTrigger.setOnUserActivity(a, null, ev(), null); assertNull(((DropTrigger)a.getTrigger()).getDropSite()); }
  @Test public void getEvent() { UserActivity a=new UserActivity(); MouseEvent m=ev(); DropTrigger.setOnUserActivity(a, null, m, null); assertSame(m, ((DropTrigger)a.getTrigger()).getEvent()); }
  @Test public void extendsAbstractMouse() { UserActivity a=new UserActivity(); DropTrigger.setOnUserActivity(a, null, ev(), null); assertTrue(a.getTrigger() instanceof AbstractMouseEventTrigger); }
  @Test public void appendRepr() { UserActivity a=new UserActivity(); DropTrigger.setOnUserActivity(a, null, ev(), null); StringBuilder sb=new StringBuilder(); a.getTrigger().appendRepr(sb); assertTrue(sb.toString().contains("DropTrigger")); }
  @Test public void appendReprDropSite() { UserActivity a=new UserActivity(); DropTrigger.setOnUserActivity(a, null, ev(), null); StringBuilder sb=new StringBuilder(); a.getTrigger().appendRepr(sb); assertTrue(sb.toString().contains("dropSite")); }
  @Test public void twoActivities() { UserActivity a1=new UserActivity(),a2=new UserActivity(); DropTrigger.setOnUserActivity(a1, null, ev(), null); DropTrigger.setOnUserActivity(a2, null, ev(), null); assertNotSame(a1.getTrigger(), a2.getTrigger()); }
}
