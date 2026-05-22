package org.alice.ide.declarationseditor;

import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DeclarationTabStateDataProjectContextTest extends ProjectContextTestCase {
  @Test
  public void selectingCodeCompositeAddsOwningTypeTab() {
    DeclarationTabState tabState = new DeclarationTabState();
    CodeComposite codeComposite = CodeComposite.getInstance(fixture.sceneProcedure);

    tabState.setValueTransactionlessly(codeComposite);

    assertTrue(tabState.getData().contains(TypeComposite.getInstance(fixture.sceneType)));
    assertTrue(tabState.getData().contains(codeComposite));
  }
}
