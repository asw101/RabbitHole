package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep24Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.stageide.ast.source.SourceImportValueCreator",
        "org.alice.stageide.cascade.JointExpressionFillIn",
        "org.alice.stageide.cascade.fillerinners.SourceFillerInner",
        "org.alice.stageide.croquet.models.gallerybrowser.TypeFromUriProducer",
        "org.alice.stageide.croquet.models.gallerybrowser.UriCreator",
        "org.alice.stageide.custom.ColorCustomExpressionCreatorComposite",
        "org.alice.stageide.gallerybrowser.ImportGalleryResourceComposite"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
