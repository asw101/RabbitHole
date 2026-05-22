package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep16Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.imp.booleanstate.BooleanStateMenuItemPrepModel",
        "org.lgna.croquet.imp.booleanstate.BooleanStateSwingModel",
        "org.lgna.croquet.imp.cascade.AbstractItemNode",
        "org.lgna.croquet.imp.cascade.BlankNode",
        "org.lgna.croquet.imp.cascade.BlankOwnerNode",
        "org.lgna.croquet.imp.cascade.CancelNode",
        "org.lgna.croquet.imp.cascade.CascadeNode",
        "org.lgna.croquet.imp.cascade.FillInNode",
        "org.lgna.croquet.imp.cascade.ItemNode",
        "org.lgna.croquet.imp.cascade.MenuNode",
        "org.lgna.croquet.imp.cascade.RootNode",
        "org.lgna.croquet.imp.cascade.RtRoot",
        "org.lgna.croquet.imp.cascade.SeparatorNode"
    );
  }
}
