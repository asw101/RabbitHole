package org.alice.ide.coverage;

public class MiniIdeSingletonSweep61Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.type.ConstructorState",
        "org.alice.ide.declarationseditor.type.ConstructorsComposite",
        "org.alice.ide.declarationseditor.type.ConstructorsToolPaletteCoreComposite"
    };
  }
}
