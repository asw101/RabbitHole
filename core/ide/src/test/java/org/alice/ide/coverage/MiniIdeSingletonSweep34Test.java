package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep34Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.stageide.type.croquet.views.ContainsTabPane",
        "org.alice.stageide.type.croquet.views.OtherTypeDialogPane",
        "org.alice.stageide.type.croquet.views.renderers.FieldCellRenderer"
    };
  }
}
