package org.alice.ide.coverage;

public class MiniIdeSingletonSweep57Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.components.TypeEditor",
        "org.alice.ide.declarationseditor.events.AddEventListenerMethodInvocationFillIn",
        "org.alice.ide.declarationseditor.events.EventListenerMenuModel"
    };
  }
}
