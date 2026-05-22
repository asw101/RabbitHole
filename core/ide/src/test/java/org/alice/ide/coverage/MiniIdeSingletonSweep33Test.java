package org.alice.ide.coverage;

public class MiniIdeSingletonSweep33Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.stageide.type.croquet.data.MemberListData",
        "org.alice.stageide.type.croquet.data.SceneFieldListData",
        "org.alice.stageide.type.croquet.views.AssignableTabPane"
    };
  }
}
