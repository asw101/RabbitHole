package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep03Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.ast.declaration.AddParameterComposite",
        "org.alice.ide.ast.declaration.DeclarationLikeSubstanceComposite",
        "org.alice.ide.ast.declaration.views.AddParameterView",
        "org.alice.ide.ast.draganddrop.expression.ThisExpressionDragModel",
        "org.alice.ide.ast.draganddrop.statement.AbstractStatementDragModel",
        "org.alice.ide.ast.export.ExportTypeToFileDialogOperation",
        "org.alice.ide.ast.icons.ThisInstanceIconFactory"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
