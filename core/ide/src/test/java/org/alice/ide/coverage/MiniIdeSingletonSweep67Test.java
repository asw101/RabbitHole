package org.alice.ide.coverage;

public class MiniIdeSingletonSweep67Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.croquet.models.ast.DefaultExpressionPropertyCascade",
        "org.alice.ide.croquet.models.ast.DissolveStatementWithBodyOperation",
        "org.alice.ide.croquet.models.ast.InsertStatementCompletionModel"
    };
  }
}
