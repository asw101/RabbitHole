package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep23Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.views.CompositeView",
        "org.lgna.croquet.views.CornerSpringPanel",
        "org.lgna.croquet.views.CustomItemStatePopupButton",
        "org.lgna.croquet.views.DefaultRadioButtons",
        "org.lgna.croquet.views.DocumentLabel",
        "org.lgna.croquet.views.DragComponent",
        "org.lgna.croquet.views.DropDown",
        "org.lgna.croquet.views.FauxComboBoxPopupButton",
        "org.lgna.croquet.views.FixedAspectRatioPanel",
        "org.lgna.croquet.views.FixedCenterPanel",
        "org.lgna.croquet.views.FlowPanel",
        "org.lgna.croquet.views.FolderTabbedPane"
    );
  }
}
