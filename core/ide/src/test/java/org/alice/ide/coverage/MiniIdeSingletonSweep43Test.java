package org.alice.ide.coverage;

public class MiniIdeSingletonSweep43Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.EditFieldComposite",
        "org.alice.ide.ast.declaration.FieldComposite",
        "org.alice.ide.ast.declaration.InitialPropertyValuesToolPaletteCoreComposite"
    };
  }
}
