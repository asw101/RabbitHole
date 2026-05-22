package org.alice.ide.coverage;

public class MiniIdeTargetedMethodSweep78Test extends AbstractMiniIdeNamedClassMethodSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.sceneeditor.AbstractSceneEditor",
        "org.alice.stageide.sceneeditor.interact.GlobalDragAdapter"
    };
  }
}
