package org.alice.ide.coverage;

public class MiniIdeSingletonSweep60Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.events.components.EventsContentPanel",
        "org.alice.ide.declarationseditor.events.components.StickyBottomPanel",
        "org.alice.ide.declarationseditor.type.AbstractManagedFieldState"
    };
  }
}
