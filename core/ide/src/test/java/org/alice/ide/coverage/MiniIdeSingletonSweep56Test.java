package org.alice.ide.coverage;

public class MiniIdeSingletonSweep56Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.code.components.CodeDeclarationView",
        "org.alice.ide.declarationseditor.components.BackwardForwardView",
        "org.alice.ide.declarationseditor.components.DeclarationView"
    };
  }
}
