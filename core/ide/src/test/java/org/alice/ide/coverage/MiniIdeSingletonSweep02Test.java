package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep02Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.ThemeUtilities",
        "org.alice.ide.ast.CurrentThisExpression",
        "org.alice.ide.ast.FieldInitializerInstanceCreationArgument0State",
        "org.alice.ide.ast.IncompleteAstUtilities",
        "org.alice.ide.ast.PreviousValueExpression",
        "org.alice.ide.ast.code.edits.SwapParametersEdit",
        "org.alice.ide.ast.declaration.AddManagedFieldComposite"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
