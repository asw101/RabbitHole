package org.alice.ide.coverage;

public class MiniIdeSingletonSweep69Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.croquet.models.ast.RevertFieldOperation",
        "org.alice.ide.croquet.models.ast.SceneEditorUpdatingPropertyState",
        "org.alice.ide.croquet.models.ast.cascade.AbstractArgumentCascade"
    };
  }
}
