package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep15Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.history.event.Listener",
        "org.lgna.croquet.history.event.PopupMenuResizedEvent",
        "org.lgna.croquet.icon.AbstractIcon",
        "org.lgna.croquet.icon.AbstractIconFactory",
        "org.lgna.croquet.icon.AbstractSingleSourceImageIconFactory",
        "org.lgna.croquet.icon.EmptyIconFactory",
        "org.lgna.croquet.icon.IconFactory",
        "org.lgna.croquet.icon.ImageIconFactory",
        "org.lgna.croquet.icon.ResolutionIndependentIconFactory",
        "org.lgna.croquet.icon.SVGIconFactory",
        "org.lgna.croquet.icon.TrimmedIcon",
        "org.lgna.croquet.icon.TrimmedImageIconFactory",
        "org.lgna.croquet.imp.booleanstate.BooleanStateImp"
    );
  }
}
