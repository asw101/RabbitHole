package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep28Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.views.ScreenElement",
        "org.lgna.croquet.views.ScrollPane",
        "org.lgna.croquet.views.SideBySideScrollPane",
        "org.lgna.croquet.views.SingleComponentPanel",
        "org.lgna.croquet.views.Slider",
        "org.lgna.croquet.views.Spinner",
        "org.lgna.croquet.views.SplitPane",
        "org.lgna.croquet.views.SpringPanel",
        "org.lgna.croquet.views.SpringUtilities",
        "org.lgna.croquet.views.StatusLabel",
        "org.lgna.croquet.views.SubduedTextField",
        "org.lgna.croquet.views.SwingAdapter",
        "org.lgna.croquet.views.SwingComponentView"
    );
  }
}
