package org.alice.ide.coverage;

public class MiniIdeSingletonSweep59Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.events.TransformationEventListenerMenu",
        "org.alice.ide.declarationseditor.events.components.EventListenerComponent",
        "org.alice.ide.declarationseditor.events.components.EventListenersView"
    };
  }
}
