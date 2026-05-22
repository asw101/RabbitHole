package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep10Test {
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.croquet.models.ast.cascade.statement.CommentInsertOperation",
        "org.alice.ide.croquet.models.ast.cascade.statement.ProcedureInvocationInsertCascade",
        "org.alice.ide.croquet.models.ast.cascade.statement.TemplateAssignmentInsertCascade",
        "org.alice.ide.croquet.models.ast.declaration.OtherTypesMenuModel",
        "org.alice.ide.croquet.models.ast.declaration.TypeFillIn",
        "org.alice.ide.croquet.models.cascade.ExpressionBlank",
        "org.alice.ide.croquet.models.cascade.KeywordMenuModel"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
