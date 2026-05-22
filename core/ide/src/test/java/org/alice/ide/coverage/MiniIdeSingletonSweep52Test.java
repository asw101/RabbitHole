package org.alice.ide.coverage;

public class MiniIdeSingletonSweep52Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.DeclarationCompositeFillIn",
        "org.alice.ide.declarationseditor.DeclarationTabState",
        "org.alice.ide.declarationseditor.DeclarationsEditorComposite"
    };
  }
}
