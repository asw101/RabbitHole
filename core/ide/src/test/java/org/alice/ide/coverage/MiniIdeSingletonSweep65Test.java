package org.alice.ide.coverage;

public class MiniIdeSingletonSweep65Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.croquet.models.MenuBarComposite",
        "org.alice.ide.croquet.models.ResponsibleModel",
        "org.alice.ide.croquet.models.ast.CenterCameraOnOperation"
    };
  }
}
