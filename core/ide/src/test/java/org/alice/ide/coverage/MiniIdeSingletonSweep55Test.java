package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep55Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.declarationseditor.ProceduresSeparator",
        "org.alice.ide.declarationseditor.UnmanagedFieldsSeparator",
        "org.alice.ide.declarationseditor.code.components.AbstractCodeDeclarationView"
    };
  }
}
