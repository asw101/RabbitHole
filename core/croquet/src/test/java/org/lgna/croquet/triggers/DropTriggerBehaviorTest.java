package org.lgna.croquet.triggers;

import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.awt.event.MouseEvent;

import static org.junit.Assert.*;

public class DropTriggerBehaviorTest extends TriggerTestSupport {
  @Test
  public void setOnUserActivity_storesDropSiteAndAppendsRepresentation() {
    TestViewController view = new TestViewController();
    UserActivity activity = new UserActivity();
    TestDropSite site = new TestDropSite("drop-target");
    MouseEvent event = createMouseEvent(view.getAwtComponent(), MouseEvent.MOUSE_RELEASED, MouseEvent.BUTTON1, 3, 4);

    UserActivity returned = DropTrigger.setOnUserActivity(activity, view, event, site);

    assertSame(activity, returned);
    assertTrue(activity.getTrigger() instanceof DropTrigger);
    DropTrigger trigger = (DropTrigger) activity.getTrigger();
    assertSame(site, trigger.getDropSite());
    assertSame(view, trigger.getViewController());
    StringBuilder repr = new StringBuilder();
    trigger.appendRepr(repr);
    assertTrue(repr.toString().contains("dropSite=drop-target"));

    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    trigger.encode(encoder);
    assertNotNull(encoder.createDecoder());
  }
}
