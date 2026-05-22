package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep27Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.views.PaintUtilities",
        "org.lgna.croquet.views.Panel",
        "org.lgna.croquet.views.PanelViewController",
        "org.lgna.croquet.views.PasswordField",
        "org.lgna.croquet.views.PlainMultiLineLabel",
        "org.lgna.croquet.views.PopupButton",
        "org.lgna.croquet.views.PopupMenu",
        "org.lgna.croquet.views.PreserveAspectRatioPanel",
        "org.lgna.croquet.views.ProgressBar",
        "org.lgna.croquet.views.PushButton",
        "org.lgna.croquet.views.RadioButton",
        "org.lgna.croquet.views.RootPane"
    );
  }
}
