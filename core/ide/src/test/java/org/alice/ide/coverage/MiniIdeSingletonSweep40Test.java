package org.alice.ide.coverage;

public class MiniIdeSingletonSweep40Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.AddFieldComposite",
        "org.alice.ide.ast.declaration.AddFunctionComposite",
        "org.alice.ide.ast.declaration.AddMethodComposite"
    };
  }
}
