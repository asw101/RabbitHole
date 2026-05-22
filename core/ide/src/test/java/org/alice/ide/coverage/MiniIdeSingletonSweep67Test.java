package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep67Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.croquet.models.ast.DefaultExpressionPropertyCascade",
        "org.alice.ide.croquet.models.ast.DissolveStatementWithBodyOperation",
        "org.alice.ide.croquet.models.ast.InsertStatementCompletionModel"
    };
  }
}
