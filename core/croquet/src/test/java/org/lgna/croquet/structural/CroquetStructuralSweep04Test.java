package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep04Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.CascadeMenuModel",
        "org.lgna.croquet.CascadeRoot",
        "org.lgna.croquet.CascadeSeparator",
        "org.lgna.croquet.CascadeUnfilledInCancel",
        "org.lgna.croquet.CascadeWithInternalBlank",
        "org.lgna.croquet.CompletionModel",
        "org.lgna.croquet.Composite",
        "org.lgna.croquet.CustomItemState",
        "org.lgna.croquet.CustomItemStateWithInternalBlank",
        "org.lgna.croquet.CustomSingleSelectTreeState",
        "org.lgna.croquet.DefaultCustomItemState",
        "org.lgna.croquet.DefaultSingleSelectTreeState"
    );
  }
}
