package org.alice.ide.coverage;

public class MiniIdeSingletonSweep39Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.resource.manager.edits.RenameResourceEdit",
        "org.alice.ide.resource.manager.views.ResourceManagerView",
        "org.alice.ide.resource.manager.views.ResourceRenamePanel"
    };
  }
}
