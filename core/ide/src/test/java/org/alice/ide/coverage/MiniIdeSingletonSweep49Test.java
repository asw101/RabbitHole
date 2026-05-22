package org.alice.ide.coverage;

public class MiniIdeSingletonSweep49Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.views.EditFieldView",
        "org.alice.ide.ast.declaration.views.FieldView",
        "org.alice.ide.ast.declaration.views.InitialPropertyValuesToolPaletteCoreView"
    };
  }
}
