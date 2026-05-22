package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeTargetedMethodSweep88Test extends AbstractMiniIdeNamedClassMethodSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.x.components.StatementListPropertyView",
        "org.alice.stageide.sceneeditor.views.InstanceFactorySelectionPanel"
    };
  }
}
