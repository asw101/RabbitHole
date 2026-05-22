package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep26Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.views.Menu",
        "org.lgna.croquet.views.MenuBar",
        "org.lgna.croquet.views.MenuItem",
        "org.lgna.croquet.views.MenuItemContainer",
        "org.lgna.croquet.views.MenuItemContainerUtilities",
        "org.lgna.croquet.views.MenuTextSeparator",
        "org.lgna.croquet.views.MigPanel",
        "org.lgna.croquet.views.MultiLineLabel",
        "org.lgna.croquet.views.MultipleSelectionListView",
        "org.lgna.croquet.views.MutableList",
        "org.lgna.croquet.views.MutableSplitPane",
        "org.lgna.croquet.views.OperationButton",
        "org.lgna.croquet.views.PageAxisPanel"
    );
  }
}
