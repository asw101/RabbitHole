package org.alice.ide.coverage;

public class MiniIdeTargetedMethodSweep90Test extends AbstractMiniIdeNamedClassMethodSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.stageide.sceneeditor.interact.manipulators.Camera2DDragManipulator",
        "org.alice.ide.ast.type.merge.croquet.views.icons.ActionStatusIcon"
    };
  }
}
