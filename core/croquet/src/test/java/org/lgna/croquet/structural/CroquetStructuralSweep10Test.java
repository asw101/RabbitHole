package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep10Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.SimpleModalFrameComposite",
        "org.lgna.croquet.SimpleOperationInputDialogCoreComposite",
        "org.lgna.croquet.SimpleOperationUnadornedDialogCoreComposite",
        "org.lgna.croquet.SimpleOperationWizardDialogCoreComposite",
        "org.lgna.croquet.SimpleTabComposite",
        "org.lgna.croquet.SimpleTabState",
        "org.lgna.croquet.SingleSelectListState",
        "org.lgna.croquet.SingleSelectTableRowState",
        "org.lgna.croquet.SingleSelectTreeState",
        "org.lgna.croquet.SingleThreadIteratingOperation",
        "org.lgna.croquet.SingleValueCreatorInputDialogCoreComposite",
        "org.lgna.croquet.SplitComposite"
    );
  }
}
