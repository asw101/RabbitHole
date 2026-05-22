package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep43Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.EditFieldComposite",
        "org.alice.ide.ast.declaration.FieldComposite",
        "org.alice.ide.ast.declaration.InitialPropertyValuesToolPaletteCoreComposite"
    };
  }
}
