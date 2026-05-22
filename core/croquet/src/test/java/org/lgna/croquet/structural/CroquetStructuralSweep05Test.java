package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep05Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.Document",
        "org.lgna.croquet.DocumentFrame",
        "org.lgna.croquet.DragModel",
        "org.lgna.croquet.DropReceptor",
        "org.lgna.croquet.DropRejector",
        "org.lgna.croquet.DropSite",
        "org.lgna.croquet.EditOperation",
        "org.lgna.croquet.Element",
        "org.lgna.croquet.EnumConstantState",
        "org.lgna.croquet.FileDialogValueCreator",
        "org.lgna.croquet.FocusWindowComposite",
        "org.lgna.croquet.FrameComposite",
        "org.lgna.croquet.FrameCompositeWithInternalIsShowingState"
    );
  }
}
