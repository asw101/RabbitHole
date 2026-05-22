package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep58Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.events.KeyboardEventListenerMenu",
        "org.alice.ide.declarationseditor.events.MouseEventListenerMenu",
        "org.alice.ide.declarationseditor.events.TimeEventListenerMenu"
    };
  }
}
