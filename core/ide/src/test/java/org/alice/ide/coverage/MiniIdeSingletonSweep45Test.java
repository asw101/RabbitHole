package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep45Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.InsertForEachInArrayLoopComposite",
        "org.alice.ide.ast.declaration.InsertLocalDeclarationStatementComposite",
        "org.alice.ide.ast.declaration.InsertStatementComposite"
    };
  }
}
