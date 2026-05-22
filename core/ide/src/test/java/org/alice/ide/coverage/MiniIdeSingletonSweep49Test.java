package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep49Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.views.EditFieldView",
        "org.alice.ide.ast.declaration.views.FieldView",
        "org.alice.ide.ast.declaration.views.InitialPropertyValuesToolPaletteCoreView"
    };
  }
}
