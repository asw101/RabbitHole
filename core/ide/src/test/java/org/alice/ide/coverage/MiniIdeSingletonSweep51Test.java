package org.alice.ide.coverage;

public class MiniIdeSingletonSweep51Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.BackwardForwardComposite",
        "org.alice.ide.declarationseditor.BackwardOperation",
        "org.alice.ide.declarationseditor.ClassesSeparator"
    };
  }
}
