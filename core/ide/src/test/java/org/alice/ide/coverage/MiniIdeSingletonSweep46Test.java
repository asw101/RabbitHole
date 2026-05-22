package org.alice.ide.coverage;

public class MiniIdeSingletonSweep46Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.ManagedEditFieldComposite",
        "org.alice.ide.ast.declaration.UnmanagedEditFieldComposite",
        "org.alice.ide.ast.declaration.views.AddFieldView"
    };
  }
}
