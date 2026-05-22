package org.alice.ide.declarationseditor;

import org.alice.ide.ast.declaration.AddFunctionComposite;
import org.alice.ide.ast.declaration.AddProcedureComposite;
import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TypeCompositeContainsProjectContextTest extends ProjectContextTestCase {
  @Test
  public void containsRecognizesAddMemberLaunchOperations() {
    TypeComposite composite = TypeComposite.getInstance(fixture.sceneType);

    assertTrue(composite.contains(AddProcedureComposite.getInstance(fixture.sceneType).getLaunchOperation()));
    assertTrue(composite.contains(AddFunctionComposite.getInstance(fixture.sceneType).getLaunchOperation()));
    assertNotNull(composite.getImportOperation());
    assertNotNull(composite.getExportOperation());
  }
}
