package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep40Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.AddFieldComposite",
        "org.alice.ide.ast.declaration.AddFunctionComposite",
        "org.alice.ide.ast.declaration.AddMethodComposite"
    };
  }
}
