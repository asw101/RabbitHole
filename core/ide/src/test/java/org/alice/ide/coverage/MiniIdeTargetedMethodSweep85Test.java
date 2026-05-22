package org.alice.ide.coverage;

public class MiniIdeTargetedMethodSweep85Test extends AbstractMiniIdeNamedClassMethodSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.stageide.sceneeditor.SceneEditorLifecycleManager",
        "test.ik.IkProgram"
    };
  }
}
