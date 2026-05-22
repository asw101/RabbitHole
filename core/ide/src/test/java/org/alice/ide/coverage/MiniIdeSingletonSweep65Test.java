package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep65Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.croquet.models.MenuBarComposite",
        "org.alice.ide.croquet.models.ResponsibleModel",
        "org.alice.ide.croquet.models.ast.CenterCameraOnOperation"
    };
  }
}
