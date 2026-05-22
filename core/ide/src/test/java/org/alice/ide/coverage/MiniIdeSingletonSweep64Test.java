package org.alice.ide.coverage;

public class MiniIdeSingletonSweep64Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.croquet.models.AliceMenuBar",
        "org.alice.ide.croquet.models.ExpressionState",
        "org.alice.ide.croquet.models.FilteredListPropertySingleSelectListState"
    };
  }
}
