package org.alice.ide.coverage;

public class MiniIdeSingletonSweep35Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.stageide.type.croquet.views.renderers.MemberCellRenderer",
        "org.alice.stageide.type.croquet.views.renderers.TypeCellRenderer",
        "org.alice.ide.resource.manager.ImportAudioResourceOperation"
    };
  }
}
