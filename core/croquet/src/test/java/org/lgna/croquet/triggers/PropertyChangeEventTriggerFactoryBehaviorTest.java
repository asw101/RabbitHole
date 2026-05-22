package org.lgna.croquet.triggers;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.beans.PropertyChangeEvent;

import static org.junit.Assert.*;

public class PropertyChangeEventTriggerFactoryBehaviorTest extends TriggerTestSupport {
  @Test
  public void createUserActivity_setsTriggerAndExposesViewController() {
    TestViewController view = new TestViewController();
    PropertyChangeEvent event = new PropertyChangeEvent(view.getAwtComponent(), "value", 1, 2);

    UserActivity activity = PropertyChangeEventTrigger.createUserActivity(view, event);

    assertTrue(activity.getTrigger() instanceof PropertyChangeEventTrigger);
    PropertyChangeEventTrigger trigger = (PropertyChangeEventTrigger) activity.getTrigger();
    assertSame(event, trigger.getEvent());
    assertSame(view, trigger.getViewController());
  }
}
