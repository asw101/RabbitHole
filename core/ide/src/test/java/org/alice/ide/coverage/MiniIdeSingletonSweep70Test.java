package org.alice.ide.coverage;

public class MiniIdeSingletonSweep70Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.croquet.models.ast.cascade.ArgumentCascade",
        "org.alice.ide.croquet.models.ast.cascade.ExpressionListPropertyCascade",
        "org.alice.ide.croquet.models.ast.cascade.ExpressionsCascade"
    };
  }
}
