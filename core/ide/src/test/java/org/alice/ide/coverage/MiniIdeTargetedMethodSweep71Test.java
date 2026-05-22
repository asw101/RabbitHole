package org.alice.ide.coverage;

public class MiniIdeTargetedMethodSweep71Test extends AbstractMiniIdeNamedClassMethodSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.stageide.properties.uicontroller.ModelSizePropertyController",
        "org.alice.ide.codedrop.CodePanelWithDropReceptor"
    };
  }
}
