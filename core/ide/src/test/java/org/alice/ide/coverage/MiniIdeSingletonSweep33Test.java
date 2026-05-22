package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep33Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.stageide.type.croquet.data.MemberListData",
        "org.alice.stageide.type.croquet.data.SceneFieldListData",
        "org.alice.stageide.type.croquet.views.AssignableTabPane"
    };
  }
}
