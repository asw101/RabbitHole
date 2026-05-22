package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep20Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.triggers.MouseEventTrigger",
        "org.lgna.croquet.triggers.NullTrigger",
        "org.lgna.croquet.triggers.PopupMenuEventTrigger",
        "org.lgna.croquet.triggers.PropertyChangeEventTrigger",
        "org.lgna.croquet.triggers.TreeSelectionEventTrigger",
        "org.lgna.croquet.triggers.Trigger",
        "org.lgna.croquet.triggers.WindowEventTrigger",
        "org.lgna.croquet.undo.UndoHistory",
        "org.lgna.croquet.undo.event.HistoryClearEvent",
        "org.lgna.croquet.undo.event.HistoryEvent",
        "org.lgna.croquet.undo.event.HistoryInsertionIndexEvent",
        "org.lgna.croquet.undo.event.HistoryListener",
        "org.lgna.croquet.undo.event.HistoryPushEvent"
    );
  }
}
