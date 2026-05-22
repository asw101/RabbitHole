package org.alice.ide.declarationseditor;

import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;
import org.lgna.croquet.Operation;

import static org.junit.Assert.assertEquals;

public class DeclarationTabStateOperationProjectContextTest extends ProjectContextTestCase {
  @Test
  public void methodSelectionOperationTracksRenamedDeclaration() {
    DeclarationTabState tabState = new DeclarationTabState();
    Operation operation = tabState.getItemSelectionOperationForMethod(fixture.sceneProcedure);

    fixture.sceneProcedure.name.setValue("renamedStoryAction");

    assertEquals("renamedStoryAction", operation.getImp().getName());
  }
}
