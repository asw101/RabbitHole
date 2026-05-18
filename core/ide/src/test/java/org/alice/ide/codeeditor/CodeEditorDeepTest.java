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

  private static Class<?> findInnerClass(String simpleName) {
    for (Class<?> inner : CodeEditor.class.getDeclaredClasses()) {
      if (simpleName.equals(inner.getSimpleName())) {
        return inner;
      }
    }
    return null;
  }

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
    assertNotNull("Should have StatementListIndexTrackableShape inner class",
        findInnerClass("StatementListIndexTrackableShape"));
  }

  @Test
  public void statementListIndexTrackableShape_implementsTrackableShape() {
    Class<?> inner = findInnerClass("StatementListIndexTrackableShape");
    assertNotNull("StatementListIndexTrackableShape not found", inner);
    assertTrue("StatementListIndexTrackableShape should implement TrackableShape",
        TrackableShape.class.isAssignableFrom(inner));
  }

  @Test
  public void statementListIndexTrackableShape_hasGetBlockStatementMethod() throws Exception {
    Class<?> inner = findInnerClass("StatementListIndexTrackableShape");
    assertNotNull("StatementListIndexTrackableShape not found", inner);
    assertNotNull(inner.getMethod("getBlockStatement"));
  }

  @Test
  public void statementListIndexTrackableShape_hasGetIndexMethod() throws Exception {
    Class<?> inner = findInnerClass("StatementListIndexTrackableShape");
    assertNotNull("StatementListIndexTrackableShape not found", inner);
    Method m = inner.getMethod("getIndex");
    assertEquals(int.class, m.getReturnType());
  }

  @Test
  public void statementListIndexTrackableShape_hasIsInViewMethod() throws Exception {
    Class<?> inner = findInnerClass("StatementListIndexTrackableShape");
    assertNotNull("StatementListIndexTrackableShape not found", inner);
    Method m = inner.getMethod("isInView");
    assertEquals(boolean.class, m.getReturnType());
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
