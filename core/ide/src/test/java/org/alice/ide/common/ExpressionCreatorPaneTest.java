package org.alice.ide.common;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Reflection-only characterization tests for {@link ExpressionCreatorPane}.
 *
 * <p>ExpressionCreatorPane is abstract and depends on AbstractExpressionDragModel
 * (a Croquet model class) which requires significant GUI context. These tests
 * verify the class contract, method signatures, and hierarchy without instantiation.
 */
public class ExpressionCreatorPaneTest {

  // ── class hierarchy ────────────────────────────────────────────────

  @Test
  public void expressionCreatorPane_isAbstract() {
    assertTrue("ExpressionCreatorPane should be abstract",
        Modifier.isAbstract(ExpressionCreatorPane.class.getModifiers()));
  }

  @Test
  public void expressionCreatorPane_isPublic() {
    assertTrue("ExpressionCreatorPane should be public",
        Modifier.isPublic(ExpressionCreatorPane.class.getModifiers()));
  }

  @Test
  public void expressionCreatorPane_extendsExpressionLikeSubstance() {
    assertEquals("Should extend ExpressionLikeSubstance",
        ExpressionLikeSubstance.class,
        ExpressionCreatorPane.class.getSuperclass());
  }

  // ── isClickAndClackAppropriate ─────────────────────────────────────

  @Test
  public void isClickAndClackAppropriate_isDeclared() throws Exception {
    Method m = ExpressionCreatorPane.class.getDeclaredMethod("isClickAndClackAppropriate");
    assertNotNull(m);
  }

  @Test
  public void isClickAndClackAppropriate_isProtected() throws Exception {
    Method m = ExpressionCreatorPane.class.getDeclaredMethod("isClickAndClackAppropriate");
    assertTrue("Should be protected", Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void isClickAndClackAppropriate_returnsBoolean() throws Exception {
    Method m = ExpressionCreatorPane.class.getDeclaredMethod("isClickAndClackAppropriate");
    assertEquals(boolean.class, m.getReturnType());
  }

  // ── getExpressionType ──────────────────────────────────────────────

  @Test
  public void getExpressionType_isDeclared() throws Exception {
    Method m = ExpressionCreatorPane.class.getMethod("getExpressionType");
    assertNotNull(m);
  }

  @Test
  public void getExpressionType_isFinal() throws Exception {
    Method m = ExpressionCreatorPane.class.getMethod("getExpressionType");
    assertTrue("getExpressionType should be final",
        Modifier.isFinal(m.getModifiers()));
  }

  @Test
  public void getExpressionType_isPublic() throws Exception {
    Method m = ExpressionCreatorPane.class.getMethod("getExpressionType");
    assertTrue("getExpressionType should be public",
        Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void getExpressionType_returnsAbstractType() throws Exception {
    Method m = ExpressionCreatorPane.class.getMethod("getExpressionType");
    assertEquals("Return type should be AbstractType",
        AbstractType.class, m.getReturnType());
  }

  // ── handleMouseQuoteEnteredUnquote ─────────────────────────────────

  @Test
  public void handleMouseEnteredUnquote_isDeclared() throws Exception {
    Method m = ExpressionCreatorPane.class.getDeclaredMethod("handleMouseQuoteEnteredUnquote");
    assertNotNull(m);
  }

  @Test
  public void handleMouseEnteredUnquote_isProtected() throws Exception {
    Method m = ExpressionCreatorPane.class.getDeclaredMethod("handleMouseQuoteEnteredUnquote");
    assertTrue("Should be protected", Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void handleMouseEnteredUnquote_returnsVoid() throws Exception {
    Method m = ExpressionCreatorPane.class.getDeclaredMethod("handleMouseQuoteEnteredUnquote");
    assertEquals(void.class, m.getReturnType());
  }

  // ── handleMouseQuoteExitedUnquote ──────────────────────────────────

  @Test
  public void handleMouseExitedUnquote_isDeclared() throws Exception {
    Method m = ExpressionCreatorPane.class.getDeclaredMethod("handleMouseQuoteExitedUnquote");
    assertNotNull(m);
  }

  @Test
  public void handleMouseExitedUnquote_isProtected() throws Exception {
    Method m = ExpressionCreatorPane.class.getDeclaredMethod("handleMouseQuoteExitedUnquote");
    assertTrue("Should be protected", Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void handleMouseExitedUnquote_returnsVoid() throws Exception {
    Method m = ExpressionCreatorPane.class.getDeclaredMethod("handleMouseQuoteExitedUnquote");
    assertEquals(void.class, m.getReturnType());
  }

  // ── constructor ────────────────────────────────────────────────────

  @Test
  public void constructor_takesAbstractExpressionDragModel() throws Exception {
    java.lang.reflect.Constructor<?>[] ctors = ExpressionCreatorPane.class.getDeclaredConstructors();
    assertEquals("Should have exactly one constructor", 1, ctors.length);
    Class<?>[] paramTypes = ctors[0].getParameterTypes();
    assertEquals(1, paramTypes.length);
    assertEquals("org.alice.ide.ast.draganddrop.expression.AbstractExpressionDragModel",
        paramTypes[0].getName());
  }

  @Test
  public void constructor_isPublic() throws Exception {
    java.lang.reflect.Constructor<?>[] ctors = ExpressionCreatorPane.class.getDeclaredConstructors();
    assertTrue("Constructor should be public",
        Modifier.isPublic(ctors[0].getModifiers()));
  }

  // ── declared methods count ─────────────────────────────────────────

  @Test
  public void declaredMethods_countAtLeastFour() {
    Method[] methods = ExpressionCreatorPane.class.getDeclaredMethods();
    assertTrue("Should declare at least 4 methods (isClickAndClack, getExpressionType, mouseEntered, mouseExited)",
        methods.length >= 4);
  }
}
