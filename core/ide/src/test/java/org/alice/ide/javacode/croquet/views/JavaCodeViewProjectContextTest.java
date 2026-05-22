package org.alice.ide.javacode.croquet.views;

import org.alice.ide.IDE;
import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class JavaCodeViewProjectContextTest extends ProjectContextTestCase {
  @Test
  public void projectUndoHistoryComesFromLoadedDocument() throws Exception {
    Method method = JavaCodeView.class.getDeclaredMethod("getProjectUndoHistory");
    method.setAccessible(true);

    Object history = method.invoke(null);
    assertSame(IDE.getActiveInstance().getDocumentFrame().getDocument().getUndoHistory(IDE.PROJECT_GROUP), history);
  }

  @Test
  public void setDeclarationRendersLoadedProcedureName() {
    JavaCodeView view = new JavaCodeView(fixture.sceneProcedure);
    assertTrue(view.getText().contains("storyAction"));
  }
}
