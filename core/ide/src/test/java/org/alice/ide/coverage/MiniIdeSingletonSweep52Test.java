package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep52Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.DeclarationCompositeFillIn",
        "org.alice.ide.declarationseditor.DeclarationTabState",
        "org.alice.ide.declarationseditor.DeclarationsEditorComposite"
    };
  }
}
