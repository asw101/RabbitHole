package org.alice.ide.coverage;

public class MiniIdeSingletonSweep34Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.stageide.type.croquet.views.ContainsTabPane",
        "org.alice.stageide.type.croquet.views.OtherTypeDialogPane",
        "org.alice.stageide.type.croquet.views.renderers.FieldCellRenderer"
    };
  }
}
