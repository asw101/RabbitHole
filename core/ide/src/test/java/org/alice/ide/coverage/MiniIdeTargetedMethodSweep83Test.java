package org.alice.ide.coverage;

public class MiniIdeTargetedMethodSweep83Test extends AbstractMiniIdeNamedClassMethodSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.stageide.oneshot.MethodInvocationBlank",
        "org.alice.stageide.sceneeditor.viewmanager.MarkerUtilities"
    };
  }
}
