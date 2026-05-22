package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep01Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.imp.cascade.RtItem",
        "org.lgna.croquet.views.TreePathViewController$BreadcrumbLayout",
        "org.lgna.croquet.views.ToolPaletteTitle$JToolPaletteTitle",
        "org.lgna.croquet.views.ComboBox$ItemInPopupTrackableShape",
        "org.lgna.croquet.views.DropDown$JDropDownButton",
        "org.lgna.croquet.AbstractCascadeMenuModel",
        "org.lgna.croquet.AbstractCompletionModel",
        "org.lgna.croquet.AbstractComposite",
        "org.lgna.croquet.AbstractDialogComposite",
        "org.lgna.croquet.AbstractDropReceptor",
        "org.lgna.croquet.AbstractElement",
        "org.lgna.croquet.AbstractMenuModel",
        "org.lgna.croquet.AbstractModel"
    );
  }
}
