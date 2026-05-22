package org.alice.ide.declarationseditor;

import org.alice.ide.declarationseditor.code.components.CodeDeclarationView;
import org.alice.ide.testing.ProjectContextTestCase;
import org.alice.ide.testing.TestIdeBootstrap;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CodeCompositeViewProjectContextTest extends ProjectContextTestCase {
  @Test
  public void regularProcedureUsesCodeDeclarationView() {
    CodeComposite composite = CodeComposite.getInstance(fixture.sceneProcedure);

    TestIdeBootstrap.onEdt(() -> {
      assertTrue(composite.getView() instanceof CodeDeclarationView);
      assertSame(fixture.sceneType, composite.getType());
      return null;
    });
  }
}
