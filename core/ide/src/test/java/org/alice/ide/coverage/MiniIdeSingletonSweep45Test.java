package org.alice.ide.coverage;

public class MiniIdeSingletonSweep45Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.InsertForEachInArrayLoopComposite",
        "org.alice.ide.ast.declaration.InsertLocalDeclarationStatementComposite",
        "org.alice.ide.ast.declaration.InsertStatementComposite"
    };
  }
}
