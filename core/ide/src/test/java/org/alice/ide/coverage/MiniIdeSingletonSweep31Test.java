package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep31Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.stageide.type.croquet.AssignableTab",
        "org.alice.stageide.type.croquet.ContainsTab",
        "org.alice.stageide.type.croquet.OtherTypeDialog"
    };
  }
}
