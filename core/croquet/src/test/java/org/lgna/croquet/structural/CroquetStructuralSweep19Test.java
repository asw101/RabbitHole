package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep19Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.triggers.AppleApplicationEventTrigger",
        "org.lgna.croquet.triggers.CascadeAutomaticDeterminationTrigger",
        "org.lgna.croquet.triggers.ChangeEventTrigger",
        "org.lgna.croquet.triggers.ComponentEventTrigger",
        "org.lgna.croquet.triggers.DocumentEventTrigger",
        "org.lgna.croquet.triggers.DragTrigger",
        "org.lgna.croquet.triggers.DropTrigger",
        "org.lgna.croquet.triggers.EventObjectTrigger",
        "org.lgna.croquet.triggers.InputEventTrigger",
        "org.lgna.croquet.triggers.ItemEventTrigger",
        "org.lgna.croquet.triggers.IterationTrigger",
        "org.lgna.croquet.triggers.KeyEventTrigger"
    );
  }
}
