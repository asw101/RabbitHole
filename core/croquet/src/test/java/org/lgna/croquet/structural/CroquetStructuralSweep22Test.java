package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep22Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.views.BorderPanel",
        "org.lgna.croquet.views.BoxUtilities",
        "org.lgna.croquet.views.Button",
        "org.lgna.croquet.views.ButtonWithRightClickCascade",
        "org.lgna.croquet.views.CardBasedTabbedPane",
        "org.lgna.croquet.views.CardPanel",
        "org.lgna.croquet.views.CascadeMenu",
        "org.lgna.croquet.views.CascadeMenuItem",
        "org.lgna.croquet.views.CheckBox",
        "org.lgna.croquet.views.CheckBoxMenuItem",
        "org.lgna.croquet.views.ComboBox",
        "org.lgna.croquet.views.CompassPointSpringPanel",
        "org.lgna.croquet.views.ComponentManager"
    );
  }
}
