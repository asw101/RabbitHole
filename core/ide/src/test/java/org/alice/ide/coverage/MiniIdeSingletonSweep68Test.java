package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep68Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.croquet.models.ast.IsStatementEnabledState",
        "org.alice.ide.croquet.models.ast.LocalMenuModel",
        "org.alice.ide.croquet.models.ast.ParameterAccessMenuModel"
    };
  }
}
