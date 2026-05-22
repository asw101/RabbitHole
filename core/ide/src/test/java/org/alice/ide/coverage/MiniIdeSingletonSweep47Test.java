package org.alice.ide.coverage;

public class MiniIdeSingletonSweep47Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.views.AddFunctionView",
        "org.alice.ide.ast.declaration.views.AddManagedFieldView",
        "org.alice.ide.ast.declaration.views.AddMethodView"
    };
  }
}
