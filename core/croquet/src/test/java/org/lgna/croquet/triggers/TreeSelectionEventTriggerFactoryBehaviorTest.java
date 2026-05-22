package org.lgna.croquet.triggers;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import static org.junit.Assert.*;

public class TreeSelectionEventTriggerFactoryBehaviorTest extends TriggerTestSupport {
  @Test
  public void createUserActivity_setsTriggerAndExposesEvent() {
    JTree tree = new JTree();
    TreePath path = new TreePath(tree.getModel().getRoot());
    TreeSelectionEvent event = new TreeSelectionEvent(tree, path, true, null, path);

    UserActivity activity = TreeSelectionEventTrigger.createUserActivity(event);

    assertTrue(activity.getTrigger() instanceof TreeSelectionEventTrigger);
    TreeSelectionEventTrigger trigger = (TreeSelectionEventTrigger) activity.getTrigger();
    assertSame(event, trigger.getEvent());
    assertNull(trigger.getViewController());
  }
}
