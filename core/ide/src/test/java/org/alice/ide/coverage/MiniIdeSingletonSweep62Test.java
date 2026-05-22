package org.alice.ide.coverage;

public class MiniIdeSingletonSweep62Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.type.FieldMenuModel",
        "org.alice.ide.declarationseditor.type.FieldState",
        "org.alice.ide.declarationseditor.type.FieldsComposite"
    };
  }
}
