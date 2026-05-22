package org.alice.ide.coverage;

public class MiniIdeSingletonSweep50Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.views.InsertStatementView",
        "org.alice.ide.ast.declaration.views.TypeHeader",
        "org.alice.ide.declarationseditor.BackwardCascade"
    };
  }
}
