package org.alice.ide.coverage;

public class MiniIdeSingletonSweep63Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.type.FieldsToolPaletteCoreComposite",
        "org.alice.ide.declarationseditor.type.FilteredMemberState",
        "org.alice.ide.declarationseditor.type.FunctionState"
    };
  }
}
