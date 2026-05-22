package org.alice.ide.declarationseditor;

import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;

import static org.junit.Assert.assertSame;

public class DeclarationCompositeProjectContextTest extends ProjectContextTestCase {
  @Test
  public void getInstanceRoutesMethodDeclarationsToCodeComposite() {
    assertSame(CodeComposite.getInstance(fixture.sceneProcedure), DeclarationComposite.getInstance(fixture.sceneProcedure));
  }

  @Test
  public void getInstanceRoutesTypeDeclarationsToTypeComposite() {
    assertSame(TypeComposite.getInstance(fixture.sceneType), DeclarationComposite.getInstance(fixture.sceneType));
  }
}
