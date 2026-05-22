package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep29Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.views.TabbedPane",
        "org.lgna.croquet.views.Table",
        "org.lgna.croquet.views.TextArea",
        "org.lgna.croquet.views.TextComponent",
        "org.lgna.croquet.views.TextField",
        "org.lgna.croquet.views.ToggleButton",
        "org.lgna.croquet.views.ToggleButtonLabelCombo",
        "org.lgna.croquet.views.ToolBarView",
        "org.lgna.croquet.views.ToolPaletteTitle",
        "org.lgna.croquet.views.TrackableShape",
        "org.lgna.croquet.views.Tree",
        "org.lgna.croquet.views.TreeDirectoryViewController"
    );
  }
}
