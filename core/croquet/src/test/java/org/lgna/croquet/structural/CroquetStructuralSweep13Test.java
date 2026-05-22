package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep13Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.codecs.FileCodec",
        "org.lgna.croquet.codecs.SimpleTabCompositeCodec",
        "org.lgna.croquet.color.ColorChooserTabComposite",
        "org.lgna.croquet.color.ColorState",
        "org.lgna.croquet.color.views.ColorChooserDialogCoreView",
        "org.lgna.croquet.color.views.ColorChooserTabView",
        "org.lgna.croquet.data.AbstractMutableListData",
        "org.lgna.croquet.data.ListData",
        "org.lgna.croquet.data.RefreshableListData",
        "org.lgna.croquet.edits.AbstractEdit",
        "org.lgna.croquet.edits.Edit",
        "org.lgna.croquet.event.ValueListener",
        "org.lgna.croquet.history.ActivityNode"
    );
  }
}
