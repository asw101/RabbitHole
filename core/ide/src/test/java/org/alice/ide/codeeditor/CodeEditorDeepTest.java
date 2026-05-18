package org.alice.ide.codeeditor;

import org.lgna.croquet.DropSite;
import org.lgna.croquet.views.TrackableShape;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Tests for {@link CodeEditor} public API contracts — class hierarchy,
 * inner class interfaces, key public methods, and constructor shape.
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

}
