package org.alice.ide.coverage;

public class MiniIdeSingletonSweep42Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.DeclarationDialogLifecycleDelegate",
        "org.alice.ide.ast.declaration.DeclarationNameState",
        "org.alice.ide.ast.declaration.DeclarationValidationDelegate"
    };
  }
}
