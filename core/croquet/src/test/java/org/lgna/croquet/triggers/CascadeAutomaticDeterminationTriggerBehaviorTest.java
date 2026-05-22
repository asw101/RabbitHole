package org.lgna.croquet.triggers;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import static org.junit.Assert.*;

public class CascadeAutomaticDeterminationTriggerBehaviorTest extends TriggerTestSupport {
  @Test
  public void createChildActivity_delegatesViewControllerAndPopupBehavior() {
    UserActivity parent = new UserActivity();
    TestViewController view = new TestViewController();
    TestTrigger parentTrigger = new TestTrigger(parent, view);

    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);

    assertTrue(parent.getChildActivities().contains(child));
    assertTrue(child.getTrigger() instanceof CascadeAutomaticDeterminationTrigger);
    Trigger trigger = child.getTrigger();
    assertSame(view, trigger.getViewController());
    trigger.showPopupMenu(null);
    assertEquals(1, parentTrigger.getPopupCount());
  }
}
