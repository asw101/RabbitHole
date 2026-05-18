package org.alice.ide.codeeditor;

import org.lgna.croquet.DropSite;
import org.lgna.croquet.views.TrackableShape;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Extended structural and reflection-based tests for {@link CodeEditor} — verifying
 * inner classes, method signatures, field declarations, and class hierarchy.
 */
public class CodeEditorDeepTest {

  // ---- Class hierarchy ----

  @Test
  public void codeEditor_extendsCodePanelWithDropReceptor() {
    Class<?> superclass = CodeEditor.class.getSuperclass();
    assertEquals("org.alice.ide.codedrop.CodePanelWithDropReceptor", superclass.getName());
  }

  @Test
  public void codeEditor_isNotAbstract() {
    assertFalse("CodeEditor should be concrete",
        Modifier.isAbstract(CodeEditor.class.getModifiers()));
  }

  @Test
  public void codeEditor_isPublic() {
    assertTrue("CodeEditor should be public",
        Modifier.isPublic(CodeEditor.class.getModifiers()));
  }

  // ---- Inner class: StatementListIndexTrackableShape ----

  @Test
  public void codeEditor_hasStatementListIndexTrackableShapeInnerClass() {
    Class<?>[] innerClasses = CodeEditor.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> inner : innerClasses) {
      if ("StatementListIndexTrackableShape".equals(inner.getSimpleName())) {
        found = true;
        break;
      }
    }
    assertTrue("Should have StatementListIndexTrackableShape inner class", found);
  }

  @Test
  public void statementListIndexTrackableShape_implementsTrackableShape() {
    Class<?>[] innerClasses = CodeEditor.class.getDeclaredClasses();
    for (Class<?> inner : innerClasses) {
      if ("StatementListIndexTrackableShape".equals(inner.getSimpleName())) {
        assertTrue("StatementListIndexTrackableShape should implement TrackableShape",
            TrackableShape.class.isAssignableFrom(inner));
        return;
      }
    }
    fail("StatementListIndexTrackableShape not found");
  }

  @Test
  public void statementListIndexTrackableShape_hasGetBlockStatementMethod() throws Exception {
    Class<?>[] innerClasses = CodeEditor.class.getDeclaredClasses();
    for (Class<?> inner : innerClasses) {
      if ("StatementListIndexTrackableShape".equals(inner.getSimpleName())) {
        Method m = inner.getMethod("getBlockStatement");
        assertNotNull(m);
        return;
      }
    }
    fail("StatementListIndexTrackableShape not found");
  }

  @Test
  public void statementListIndexTrackableShape_hasGetIndexMethod() throws Exception {
    Class<?>[] innerClasses = CodeEditor.class.getDeclaredClasses();
    for (Class<?> inner : innerClasses) {
      if ("StatementListIndexTrackableShape".equals(inner.getSimpleName())) {
        Method m = inner.getMethod("getIndex");
        assertNotNull(m);
        assertEquals(int.class, m.getReturnType());
        return;
      }
    }
    fail("StatementListIndexTrackableShape not found");
  }

  @Test
  public void statementListIndexTrackableShape_hasIsInViewMethod() throws Exception {
    Class<?>[] innerClasses = CodeEditor.class.getDeclaredClasses();
    for (Class<?> inner : innerClasses) {
      if ("StatementListIndexTrackableShape".equals(inner.getSimpleName())) {
        Method m = inner.getMethod("isInView");
        assertNotNull(m);
        assertEquals(boolean.class, m.getReturnType());
        return;
      }
    }
    fail("StatementListIndexTrackableShape not found");
  }

  // ---- Key public methods ----

  @Test
  public void codeEditor_hasGetCodeMethod() throws Exception {
    Method m = CodeEditor.class.getMethod("getCode");
    assertNotNull(m);
    assertEquals(org.lgna.project.ast.AbstractCode.class, m.getReturnType());
  }

  @Test
  public void codeEditor_hasGetTrackableShapeMethod() throws Exception {
    Method m = CodeEditor.class.getMethod("getTrackableShape", DropSite.class);
    assertNotNull(m);
  }

  // ---- Constructor ----

  @Test
  public void codeEditor_hasExpectedConstructor() {
    Constructor<?>[] constructors = CodeEditor.class.getDeclaredConstructors();
    boolean foundExpected = false;
    for (Constructor<?> ctor : constructors) {
      Class<?>[] params = ctor.getParameterTypes();
      if (params.length == 2) {
        // First param should be AbstractProjectEditorAstI18nFactory, second should be AbstractCode
        if (params[1].getSimpleName().equals("AbstractCode")) {
          foundExpected = true;
          break;
        }
      }
    }
    assertTrue("CodeEditor should have constructor(AbstractProjectEditorAstI18nFactory, AbstractCode)", foundExpected);
  }

  // ---- Fields ----

  @Test
  public void codeEditor_hasCodeField() throws Exception {
    Field f = CodeEditor.class.getDeclaredField("code");
    assertNotNull(f);
  }

  @Test
  public void codeEditor_hasRootStatementListPropertyPaneField() throws Exception {
    Field f = CodeEditor.class.getDeclaredField("rootStatementListPropertyPane");
    assertNotNull(f);
  }

  @Test
  public void codeEditor_hasHeaderField() throws Exception {
    Field f = CodeEditor.class.getDeclaredField("header");
    assertNotNull(f);
  }

  @Test
  public void codeEditor_hasBodyPaneField() throws Exception {
    Field f = CodeEditor.class.getDeclaredField("bodyPane");
    assertNotNull(f);
  }

  // ---- Static helper methods (declared in CodeEditor) ----

  @Test
  public void codeEditor_hasConvertYMethod() throws Exception {
    Method m = CodeEditor.class.getDeclaredMethod("convertY",
        org.lgna.croquet.views.AwtComponentView.class, int.class, org.lgna.croquet.views.AwtComponentView.class);
    assertTrue("convertY should be static", Modifier.isStatic(m.getModifiers()));
    assertTrue("convertY should be private", Modifier.isPrivate(m.getModifiers()));
  }

  @Test
  public void codeEditor_hasCapMinimumMethod() throws Exception {
    Method m = CodeEditor.class.getDeclaredMethod("capMinimum",
        int.class, int.class, StatementListPropertyPaneInfo[].class, int.class);
    assertTrue("capMinimum should be static", Modifier.isStatic(m.getModifiers()));
    assertTrue("capMinimum should be private", Modifier.isPrivate(m.getModifiers()));
  }

  @Test
  public void codeEditor_hasCapMaximumMethod() throws Exception {
    Method m = CodeEditor.class.getDeclaredMethod("capMaximum",
        int.class, int.class, StatementListPropertyPaneInfo[].class, int.class);
    assertTrue("capMaximum should be static", Modifier.isStatic(m.getModifiers()));
    assertTrue("capMaximum should be private", Modifier.isPrivate(m.getModifiers()));
  }

  // ---- Listener fields ----

  @Test
  public void codeEditor_hasTypeFeedbackListenerField() throws Exception {
    Field f = CodeEditor.class.getDeclaredField("typeFeedbackListener");
    assertNotNull(f);
    assertTrue("typeFeedbackListener should be private",
        Modifier.isPrivate(f.getModifiers()));
  }

  @Test
  public void codeEditor_hasFormatterListenerField() throws Exception {
    Field f = CodeEditor.class.getDeclaredField("formatterListener");
    assertNotNull(f);
    assertTrue("formatterListener should be private",
        Modifier.isPrivate(f.getModifiers()));
  }
}
