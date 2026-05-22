package org.alice.ide.coverage;

public class MiniIdeTargetedMethodSweep75Test extends AbstractMiniIdeNamedClassMethodSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.swing.BasicTreeViewer",
        "org.alice.ide.declarationseditor.TypeMenu"
    };
  }
}
