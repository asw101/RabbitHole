package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep08Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.croquet.models.IdeDragModel",
        "org.alice.ide.croquet.models.StandardExpressionState",
        "org.alice.ide.croquet.models.ast.DeleteFieldOperation",
        "org.alice.ide.croquet.models.ast.DeleteMemberOperation",
        "org.alice.ide.croquet.models.ast.DeleteMethodOperation",
        "org.alice.ide.croquet.models.ast.FieldInitializerState",
        "org.alice.ide.croquet.models.ast.MethodHeaderMenuModel"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
