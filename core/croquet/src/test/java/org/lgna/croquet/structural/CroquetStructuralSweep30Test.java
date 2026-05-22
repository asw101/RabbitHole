package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep30Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.views.TreePathViewController",
        "org.lgna.croquet.views.VerticalAlignment",
        "org.lgna.croquet.views.VerticalMutableSplitPane",
        "org.lgna.croquet.views.VerticalSplitPane",
        "org.lgna.croquet.views.VerticalTextPosition",
        "org.lgna.croquet.views.ViewController",
        "org.lgna.croquet.views.imp.DropDownButtonUI",
        "org.lgna.croquet.views.imp.JDragProxy",
        "org.lgna.croquet.views.imp.JDragView",
        "org.lgna.croquet.views.imp.JDropProxy",
        "org.lgna.croquet.views.imp.JProxy",
        "org.lgna.croquet.views.imp.ScrollingPopupMenuUtilities",
        "org.lgna.croquet.views.renderers.ItemCodecListCellRenderer"
    );
  }
}
