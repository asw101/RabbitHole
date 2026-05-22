package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep03Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.BoundedNumberState",
        "org.lgna.croquet.CancelException",
        "org.lgna.croquet.CardOwnerComposite",
        "org.lgna.croquet.Cascade",
        "org.lgna.croquet.CascadeBlank",
        "org.lgna.croquet.CascadeBlankChild",
        "org.lgna.croquet.CascadeBlankOwner",
        "org.lgna.croquet.CascadeCancel",
        "org.lgna.croquet.CascadeFillIn",
        "org.lgna.croquet.CascadeItem",
        "org.lgna.croquet.CascadeItemMenuCombo",
        "org.lgna.croquet.CascadeLabelSeparator",
        "org.lgna.croquet.CascadeLineSeparator"
    );
  }
}
