package org.alice.ide.coverage;

public class MiniIdeSingletonSweep41Test extends AbstractMiniIdeDefaultArgsSweepTest {
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.ide.ast.declaration.AddPredeterminedValueTypeManagedFieldComposite",
        "org.alice.ide.ast.declaration.AddProcedureComposite",
        "org.alice.ide.ast.declaration.AddUnmanagedFieldComposite"
    };
  }
}
