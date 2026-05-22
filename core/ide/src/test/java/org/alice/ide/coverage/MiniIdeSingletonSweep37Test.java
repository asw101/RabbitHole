package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep37Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.resource.manager.RenameResourceComposite",
        "org.alice.ide.resource.manager.ResourceOperation",
        "org.alice.ide.resource.manager.ResourceSingleSelectTableRowState"
    };
  }
}
