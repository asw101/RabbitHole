package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep14Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.history.DragStep",
        "org.lgna.croquet.history.EmptyPrepStep",
        "org.lgna.croquet.history.ListSelectionStatePrepStep",
        "org.lgna.croquet.history.MenuItemSelectStep",
        "org.lgna.croquet.history.MenuSelection",
        "org.lgna.croquet.history.PrepStep",
        "org.lgna.croquet.history.UserActivity",
        "org.lgna.croquet.history.event.ActivityEvent",
        "org.lgna.croquet.history.event.CancelEvent",
        "org.lgna.croquet.history.event.ChangeEvent",
        "org.lgna.croquet.history.event.EditCommittedEvent",
        "org.lgna.croquet.history.event.FinishedEvent"
    );
  }
}
