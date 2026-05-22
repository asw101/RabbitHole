package org.alice.ide.coverage;

public class MiniIdeSingletonSweep53Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.FieldsSeparator",
        "org.alice.ide.declarationseditor.ForwardCascade",
        "org.alice.ide.declarationseditor.ForwardOperation"
    };
  }
}
