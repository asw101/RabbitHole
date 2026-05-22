package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep47Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.views.AddFunctionView",
        "org.alice.ide.ast.declaration.views.AddManagedFieldView",
        "org.alice.ide.ast.declaration.views.AddMethodView"
    };
  }
}
