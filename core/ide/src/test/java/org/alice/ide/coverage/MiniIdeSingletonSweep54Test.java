package org.alice.ide.coverage;

public class MiniIdeSingletonSweep54Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.FunctionsSeparator",
        "org.alice.ide.declarationseditor.HistoryCascade",
        "org.alice.ide.declarationseditor.ManagedFieldsSeparator"
    };
  }
}
