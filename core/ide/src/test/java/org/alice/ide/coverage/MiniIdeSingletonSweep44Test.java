package org.alice.ide.coverage;

public class MiniIdeSingletonSweep44Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.InsertEachInArrayComposite",
        "org.alice.ide.ast.declaration.InsertEachInArrayTogetherComposite",
        "org.alice.ide.ast.declaration.InsertEachInComposite"
    };
  }
}
