package org.alice.ide.codeeditor;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link CodeEditor} — limited to class structure verification since
 * the class is deeply GUI-bound (extends CodePanelWithDropReceptor, requires factory/code).
 */
public class CodeEditorTest {

  @Test
  public void codeEditor_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.codeeditor.CodeEditor");
    assertNotNull(cls);
  }

  @Test
  public void codeEditor_hasInnerTrackableShapeClass() throws ClassNotFoundException {
    // Verify the inner class StatementListIndexTrackableShape exists
    Class<?>[] innerClasses = CodeEditor.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> inner : innerClasses) {
      if (inner.getSimpleName().equals("StatementListIndexTrackableShape")) {
        found = true;
        break;
      }
    }
    assertTrue("StatementListIndexTrackableShape inner class should exist", found);
  }

  @Test
  public void codeEditor_hasGetCodeMethod() throws NoSuchMethodException {
    assertNotNull(CodeEditor.class.getMethod("getCode"));
  }

  @Test
  public void codeEditor_hasGetTrackableShapeMethod() throws NoSuchMethodException {
    // getTrackableShape(DropSite) is declared
    assertNotNull(CodeEditor.class.getMethod("getTrackableShape", org.lgna.croquet.DropSite.class));
  }
}
