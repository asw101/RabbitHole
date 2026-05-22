package org.alice.ide.coverage;

public class MiniIdeSingletonSweep66Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.croquet.models.ast.ConvertDoInOrderToDoTogetherOperation",
        "org.alice.ide.croquet.models.ast.ConvertDoTogetherToDoInOrderOperation",
        "org.alice.ide.croquet.models.ast.ConvertStatementWithBodyOperation"
    };
  }
}
