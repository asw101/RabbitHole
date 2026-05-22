package org.alice.ide.coverage;

public class MiniIdeSingletonSweep48Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.views.AddProcedureView",
        "org.alice.ide.ast.declaration.views.DeclarationLikeSubstanceView",
        "org.alice.ide.ast.declaration.views.DeclarationView"
    };
  }
}
