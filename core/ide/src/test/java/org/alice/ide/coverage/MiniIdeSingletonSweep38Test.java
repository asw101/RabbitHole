package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep38Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.resource.manager.SelectedResourceOperation",
        "org.alice.ide.resource.manager.edits.AddResourceEdit",
        "org.alice.ide.resource.manager.edits.RemoveResourceEdit"
    };
  }
}
