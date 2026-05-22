package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep06Test {
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.common.ExpressionCreatorPane",
        "org.alice.ide.common.PreviousValueExpressionPane",
        "org.alice.ide.common.SelectedInstanceFactoryExpressionPanel",
        "org.alice.ide.common.ThisPane",
        "org.alice.ide.croquet.codecs.ResourceCodec",
        "org.alice.ide.croquet.codecs.typeeditor.DeclarationCompositeCodec",
        "org.alice.ide.croquet.components.InstanceFactoryPopupButton"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
