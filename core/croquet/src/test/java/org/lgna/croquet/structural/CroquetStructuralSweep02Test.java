package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep02Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.AbstractOwnedByCompositeOperation",
        "org.lgna.croquet.AbstractPerspective",
        "org.lgna.croquet.AbstractSeverityStatusComposite",
        "org.lgna.croquet.AbstractSplitComposite",
        "org.lgna.croquet.AbstractTabComposite",
        "org.lgna.croquet.AbstractWindowComposite",
        "org.lgna.croquet.ActionOperation",
        "org.lgna.croquet.AdornedDialogCoreComposite",
        "org.lgna.croquet.Application",
        "org.lgna.croquet.BooleanState",
        "org.lgna.croquet.BoundedDoubleState",
        "org.lgna.croquet.BoundedIntegerState"
    );
  }
}
