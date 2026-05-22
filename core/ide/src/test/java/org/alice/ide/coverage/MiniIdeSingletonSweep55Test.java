package org.alice.ide.coverage;

public class MiniIdeSingletonSweep55Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.ProceduresSeparator",
        "org.alice.ide.declarationseditor.UnmanagedFieldsSeparator",
        "org.alice.ide.declarationseditor.code.components.AbstractCodeDeclarationView"
    };
  }
}
