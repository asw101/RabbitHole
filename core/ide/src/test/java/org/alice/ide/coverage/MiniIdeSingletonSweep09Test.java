package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep09Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.croquet.models.ast.MethodTemplateMenuModel",
        "org.alice.ide.croquet.models.ast.StatementContextMenu",
        "org.alice.ide.croquet.models.ast.cascade.ExpressionPropertyCascade",
        "org.alice.ide.croquet.models.ast.cascade.expression.FieldAccessOperation",
        "org.alice.ide.croquet.models.ast.cascade.expression.FieldArrayAccessCascade",
        "org.alice.ide.croquet.models.ast.cascade.expression.FieldArrayLengthOperation",
        "org.alice.ide.croquet.models.ast.cascade.expression.FunctionInvocationCascade"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
