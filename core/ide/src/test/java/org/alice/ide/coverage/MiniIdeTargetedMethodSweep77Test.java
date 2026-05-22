package org.alice.ide.coverage;

public class MiniIdeTargetedMethodSweep77Test extends AbstractMiniIdeNamedClassMethodSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.cascade.ExpressionCascadeManager",
        "org.alice.stageide.sceneeditor.interact.manipulators.CameraZoomMouseWheelManipulator"
    };
  }
}
