package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep07Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.ItemCodec",
        "org.lgna.croquet.ItemState",
        "org.lgna.croquet.IteratingOperation",
        "org.lgna.croquet.LabelMenuSeparatorModel",
        "org.lgna.croquet.LaunchOperationInputDialogCoreComposite",
        "org.lgna.croquet.LaunchOperationUnadornedDialogCoreComposite",
        "org.lgna.croquet.LaunchOperationWizardDialogCoreComposite",
        "org.lgna.croquet.LazyImmutableSplitComposite",
        "org.lgna.croquet.LazyOperationUnadornedDialogCoreComposite",
        "org.lgna.croquet.ListDataComposite",
        "org.lgna.croquet.Manager",
        "org.lgna.croquet.MenuBarComposite",
        "org.lgna.croquet.MenuItemPrepModel"
    );
  }
}
