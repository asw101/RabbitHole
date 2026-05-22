package org.alice.ide.coverage;

public class MiniIdeSingletonSweep31Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.stageide.type.croquet.AssignableTab",
        "org.alice.stageide.type.croquet.ContainsTab",
        "org.alice.stageide.type.croquet.OtherTypeDialog"
    };
  }
}
