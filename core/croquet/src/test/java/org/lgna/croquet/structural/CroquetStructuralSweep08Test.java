package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep08Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.MenuModel",
        "org.lgna.croquet.MessageDialogComposite",
        "org.lgna.croquet.ModalFrameComposite",
        "org.lgna.croquet.Model",
        "org.lgna.croquet.MultipleSelectionListState",
        "org.lgna.croquet.MutableDataSingleSelectListState",
        "org.lgna.croquet.MutableDataTabState",
        "org.lgna.croquet.MutableSplitComposite",
        "org.lgna.croquet.Operation",
        "org.lgna.croquet.OperationInputDialogCoreComposite",
        "org.lgna.croquet.OperationOwningComposite",
        "org.lgna.croquet.OperationUnadornedDialogCoreComposite"
    );
  }
}
