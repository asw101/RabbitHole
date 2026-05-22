package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep11Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.StandardMenuItemPrepModel",
        "org.lgna.croquet.State",
        "org.lgna.croquet.StaticMenuModel",
        "org.lgna.croquet.StencilModel",
        "org.lgna.croquet.StringState",
        "org.lgna.croquet.StringValue",
        "org.lgna.croquet.TabComposite",
        "org.lgna.croquet.TabState",
        "org.lgna.croquet.ToolBarComposite",
        "org.lgna.croquet.ToolBarSeparator",
        "org.lgna.croquet.ToolPaletteCoreComposite",
        "org.lgna.croquet.Triggerable",
        "org.lgna.croquet.UnadornedDialogCoreComposite"
    );
  }
}
