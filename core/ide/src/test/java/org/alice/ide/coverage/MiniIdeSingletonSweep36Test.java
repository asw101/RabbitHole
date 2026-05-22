package org.alice.ide.coverage;

public class MiniIdeSingletonSweep36Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.resource.manager.ImportImageResourceOperation",
        "org.alice.ide.resource.manager.ImportResourceOperation",
        "org.alice.ide.resource.manager.RemoveResourceOperation"
    };
  }
}
