package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep17Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.imp.dialog.DialogContentComposite",
        "org.lgna.croquet.imp.dialog.GatedCommitDialogContentComposite",
        "org.lgna.croquet.imp.dialog.WizardDialogContentComposite",
        "org.lgna.croquet.imp.dialog.views.DialogContentPane",
        "org.lgna.croquet.imp.dialog.views.GatedCommitDialogContentPane",
        "org.lgna.croquet.imp.dialog.views.WizardDialogContentPane",
        "org.lgna.croquet.imp.frame.AbstractIsFrameShowingState",
        "org.lgna.croquet.imp.launch.LazyLaunchOperationFactory",
        "org.lgna.croquet.imp.liststate.SingleSelectListStateMenuModel",
        "org.lgna.croquet.imp.liststate.SingleSelectListStateSwingModel",
        "org.lgna.croquet.imp.operation.OperationImp",
        "org.lgna.croquet.imp.operation.OperationSwingModel"
    );
  }
}
