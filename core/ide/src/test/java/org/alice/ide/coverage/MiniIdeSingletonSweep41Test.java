package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeSingletonSweep41Test extends AbstractMiniIdeDefaultArgsSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.AddPredeterminedValueTypeManagedFieldComposite",
        "org.alice.ide.ast.declaration.AddProcedureComposite",
        "org.alice.ide.ast.declaration.AddUnmanagedFieldComposite"
    };
  }
}
