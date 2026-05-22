package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep25Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.views.ImmutableEditorPane",
        "org.lgna.croquet.views.ImmutableTextArea",
        "org.lgna.croquet.views.ImmutableTextComponent",
        "org.lgna.croquet.views.ImmutableTextField",
        "org.lgna.croquet.views.ItemDropDown",
        "org.lgna.croquet.views.ItemSelectablePanel",
        "org.lgna.croquet.views.Label",
        "org.lgna.croquet.views.LabeledFormRow",
        "org.lgna.croquet.views.LayerStencil",
        "org.lgna.croquet.views.LineAxisPanel",
        "org.lgna.croquet.views.List",
        "org.lgna.croquet.views.ListDataView"
    );
  }
}
