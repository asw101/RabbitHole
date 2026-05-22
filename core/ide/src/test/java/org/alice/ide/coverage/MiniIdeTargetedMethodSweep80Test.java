package org.alice.ide.coverage;

public class MiniIdeTargetedMethodSweep80Test extends AbstractMiniIdeNamedClassMethodSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.type.merge.croquet.views.MembersView",
        "org.alice.ide.instancefactory.croquet.InstanceFactoryState"
    };
  }
}
