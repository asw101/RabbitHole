package org.alice.ide.coverage;

public class MiniIdeSingletonSweep32Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.stageide.type.croquet.SceneFieldsState",
        "org.alice.stageide.type.croquet.TypeNode",
        "org.alice.stageide.type.croquet.TypeTreeState"
    };
  }
}
