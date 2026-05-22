package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep07Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.croquet.edits.ast.DeclareMethodEdit",
        "org.alice.ide.croquet.edits.ast.ExpressionPropertyEdit",
        "org.alice.ide.croquet.edits.ast.FillInExpressionListPropertyEdit",
        "org.alice.ide.croquet.edits.ast.FillInMoreEdit",
        "org.alice.ide.croquet.edits.ast.InsertStatementEdit",
        "org.alice.ide.croquet.edits.ast.ParameterEdit",
        "org.alice.ide.croquet.edits.ast.RevertFieldEdit"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
