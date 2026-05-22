package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep69Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.croquet.models.ast.RevertFieldOperation",
        "org.alice.ide.croquet.models.ast.SceneEditorUpdatingPropertyState",
        "org.alice.ide.croquet.models.ast.cascade.AbstractArgumentCascade"
    };
  }
}
