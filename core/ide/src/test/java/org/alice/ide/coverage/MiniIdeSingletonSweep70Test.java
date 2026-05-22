package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep70Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.croquet.models.ast.cascade.ArgumentCascade",
        "org.alice.ide.croquet.models.ast.cascade.ExpressionListPropertyCascade",
        "org.alice.ide.croquet.models.ast.cascade.ExpressionsCascade"
    };
  }
}
