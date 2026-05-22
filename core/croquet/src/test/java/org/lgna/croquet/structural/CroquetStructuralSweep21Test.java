package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep21Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.views.AbstractButton",
        "org.lgna.croquet.views.AbstractLabel",
        "org.lgna.croquet.views.AbstractMenu",
        "org.lgna.croquet.views.AbstractPopupButton",
        "org.lgna.croquet.views.AbstractSplitPane",
        "org.lgna.croquet.views.AbstractTextField",
        "org.lgna.croquet.views.AbstractWindow",
        "org.lgna.croquet.views.AwtAdapter",
        "org.lgna.croquet.views.AwtComponentView",
        "org.lgna.croquet.views.AwtContainerView",
        "org.lgna.croquet.views.AxisPanel",
        "org.lgna.croquet.views.BooleanStateButton"
    );
  }
}
