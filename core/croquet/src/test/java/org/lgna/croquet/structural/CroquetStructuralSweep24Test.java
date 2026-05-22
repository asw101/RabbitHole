package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep24Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.views.FormPanel",
        "org.lgna.croquet.views.FormRow",
        "org.lgna.croquet.views.GridBagPanel",
        "org.lgna.croquet.views.GridPanel",
        "org.lgna.croquet.views.HierarchyUtilities",
        "org.lgna.croquet.views.HorizontalAlignment",
        "org.lgna.croquet.views.HorizontalMutableSplitPane",
        "org.lgna.croquet.views.HorizontalSplitPane",
        "org.lgna.croquet.views.HorizontalTextPosition",
        "org.lgna.croquet.views.HoverPopupView",
        "org.lgna.croquet.views.HtmlMultiLineLabel",
        "org.lgna.croquet.views.HtmlView",
        "org.lgna.croquet.views.Hyperlink"
    );
  }
}
