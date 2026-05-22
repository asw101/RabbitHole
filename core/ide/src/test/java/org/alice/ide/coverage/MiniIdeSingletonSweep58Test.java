package org.alice.ide.coverage;

public class MiniIdeSingletonSweep58Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.events.KeyboardEventListenerMenu",
        "org.alice.ide.declarationseditor.events.MouseEventListenerMenu",
        "org.alice.ide.declarationseditor.events.TimeEventListenerMenu"
    };
  }
}
