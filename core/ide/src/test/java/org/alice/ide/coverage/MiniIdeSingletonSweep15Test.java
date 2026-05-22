package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep15Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.declarationseditor.DeclarationComposite",
        "org.alice.ide.declarationseditor.DeclarationCompositeHistory",
        "org.alice.ide.declarationseditor.DeclarationMenu",
        "org.alice.ide.declarationseditor.HighlightFieldOperation",
        "org.alice.ide.declarationseditor.ProcedureTabSelection",
        "org.alice.ide.declarationseditor.TypeComposite",
        "org.alice.ide.declarationseditor.TypeMenu"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
