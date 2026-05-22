package org.alice.ide.coverage;

public class MiniIdeSingletonSweep68Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.croquet.models.ast.IsStatementEnabledState",
        "org.alice.ide.croquet.models.ast.LocalMenuModel",
        "org.alice.ide.croquet.models.ast.ParameterAccessMenuModel"
    };
  }
}
