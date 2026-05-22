package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep12Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.UnsupportedGenerationException",
        "org.lgna.croquet.ValueConverter",
        "org.lgna.croquet.ValueCreator",
        "org.lgna.croquet.ValueCreatorInputDialogCoreComposite",
        "org.lgna.croquet.ValueCreatorOwningComposite",
        "org.lgna.croquet.WizardDialogCoreComposite",
        "org.lgna.croquet.WizardPageComposite",
        "org.lgna.croquet.YesNoConfirmDialogComposite",
        "org.lgna.croquet.codecs.AbstractItemCodec",
        "org.lgna.croquet.codecs.ColorCodec",
        "org.lgna.croquet.codecs.DefaultItemCodec",
        "org.lgna.croquet.codecs.EnumCodec"
    );
  }
}
