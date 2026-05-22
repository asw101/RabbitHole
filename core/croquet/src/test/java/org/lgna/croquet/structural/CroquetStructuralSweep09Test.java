package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep09Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.OperationWizardDialogCoreComposite",
        "org.lgna.croquet.Perspective",
        "org.lgna.croquet.PerspectiveApplication",
        "org.lgna.croquet.PerspectiveDocumentFrame",
        "org.lgna.croquet.PlainStringValue",
        "org.lgna.croquet.PopupCoreComposite",
        "org.lgna.croquet.PopupPrepModel",
        "org.lgna.croquet.PredeterminedMenuModel",
        "org.lgna.croquet.PrepModel",
        "org.lgna.croquet.PushToolBarSeparator",
        "org.lgna.croquet.RefreshableDataSingleSelectListState",
        "org.lgna.croquet.SimpleComposite",
        "org.lgna.croquet.SimpleItemState"
    );
  }
}
