package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep14Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.croquet.models.projecturi.UriPotentialClearanceIteratingOperation",
        "org.alice.ide.croquet.models.ui.debug.ActiveTransactionHistoryComposite",
        "org.alice.ide.croquet.models.ui.debug.BreakProjectAddNullMethodOperation",
        "org.alice.ide.custom.ArrayCustomExpressionCreatorComposite",
        "org.alice.ide.custom.CustomExpressionCreatorComposite",
        "org.alice.ide.custom.components.ArrayCustomExpressionCreatorView",
        "org.alice.ide.declarationseditor.CodeComposite"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
