package org.alice.ide.coverage;

public class MiniIdeSingletonSweep38Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.resource.manager.SelectedResourceOperation",
        "org.alice.ide.resource.manager.edits.AddResourceEdit",
        "org.alice.ide.resource.manager.edits.RemoveResourceEdit"
    };
  }
}
