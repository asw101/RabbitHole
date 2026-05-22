package org.lgna.croquet.triggers;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.awt.event.ItemEvent;

import static org.junit.Assert.*;

public class ItemEventTriggerFactoryBehaviorTest extends TriggerTestSupport {
  @Test
  public void createUserActivity_setsTriggerAndExposesEvent() {
    TestComboBoxView view = new TestComboBoxView();
    ItemEvent event = new ItemEvent(view.getAwtComponent(), ItemEvent.ITEM_STATE_CHANGED, "beta", ItemEvent.SELECTED);

    UserActivity activity = ItemEventTrigger.createUserActivity(event);

    assertTrue(activity.getTrigger() instanceof ItemEventTrigger);
    ItemEventTrigger trigger = (ItemEventTrigger) activity.getTrigger();
    assertSame(event, trigger.getEvent());
    assertSame(view, trigger.getViewController());
  }
}
