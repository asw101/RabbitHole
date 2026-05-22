package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep35Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.stageide.type.croquet.views.renderers.MemberCellRenderer",
        "org.alice.stageide.type.croquet.views.renderers.TypeCellRenderer",
        "org.alice.ide.resource.manager.ImportAudioResourceOperation"
    };
  }
}
