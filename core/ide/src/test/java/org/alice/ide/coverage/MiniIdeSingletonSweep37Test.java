package org.alice.ide.coverage;

public class MiniIdeSingletonSweep37Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.resource.manager.RenameResourceComposite",
        "org.alice.ide.resource.manager.ResourceOperation",
        "org.alice.ide.resource.manager.ResourceSingleSelectTableRowState"
    };
  }
}
