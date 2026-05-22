package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep63Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.type.FieldsToolPaletteCoreComposite",
        "org.alice.ide.declarationseditor.type.FilteredMemberState",
        "org.alice.ide.declarationseditor.type.FunctionState"
    };
  }
}
