package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep59Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.events.TransformationEventListenerMenu",
        "org.alice.ide.declarationseditor.events.components.EventListenerComponent",
        "org.alice.ide.declarationseditor.events.components.EventListenersView"
    };
  }
}
