package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep06Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.GapToolBarSeparator",
        "org.lgna.croquet.GatedCommitDialogCoreComposite",
        "org.lgna.croquet.HoverPopupElement",
        "org.lgna.croquet.HtmlStringValue",
        "org.lgna.croquet.ImmutableCascade",
        "org.lgna.croquet.ImmutableCascadeFillIn",
        "org.lgna.croquet.ImmutableDataSingleSelectListState",
        "org.lgna.croquet.ImmutableDataTabState",
        "org.lgna.croquet.ImmutableSplitComposite",
        "org.lgna.croquet.ImportValueCreator",
        "org.lgna.croquet.Initializer",
        "org.lgna.croquet.InputDialogCoreComposite"
    );
  }
}
