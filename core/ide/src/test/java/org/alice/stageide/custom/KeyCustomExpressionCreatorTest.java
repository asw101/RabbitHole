package org.alice.stageide.custom;

import org.alice.ide.custom.CustomExpressionCreatorComposite;
import org.junit.Test;
import org.lgna.project.ast.JavaType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Tests for {@link KeyCustomExpressionCreatorComposite} —
 * singleton, hierarchy, and API surface.
 */
public class KeyCustomExpressionCreatorTest {

  // ---- class structure --------------------------------------------------

  @Test
  public void classIsLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.custom.KeyCustomExpressionCreatorComposite");
  }

  @Test
  public void extendsCustomExpressionCreatorComposite() {
    assertTrue(CustomExpressionCreatorComposite.class
        .isAssignableFrom(KeyCustomExpressionCreatorComposite.class));
  }

  @Test
  public void classIsNotAbstract() {
    assertFalse(Modifier.isAbstract(
        KeyCustomExpressionCreatorComposite.class.getModifiers()));
  }

  // ---- public API methods -----------------------------------------------

  @Test
  public void getInstanceMethod_exists() throws NoSuchMethodException {
    Method m = KeyCustomExpressionCreatorComposite.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void getValueStateMethod_exists() throws NoSuchMethodException {
    Method m = KeyCustomExpressionCreatorComposite.class.getMethod("getValueState");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(KeyState.class, m.getReturnType());
  }

  @Test
  public void getPressAnyKeyLabelMethod_exists() throws NoSuchMethodException {
    Method m = KeyCustomExpressionCreatorComposite.class.getMethod("getPressAnyKeyLabel");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ---- constructor is private -------------------------------------------

  @Test
  public void constructor_isPrivate() throws Exception {
    java.lang.reflect.Constructor<?>[] ctors = KeyCustomExpressionCreatorComposite.class.getDeclaredConstructors();
    for (var ctor : ctors) {
      if (ctor.getParameterCount() == 0) {
        assertTrue("no-arg ctor must be private", Modifier.isPrivate(ctor.getModifiers()));
      }
    }
  }

  // ---- inherited API surface from CustomExpressionCreatorComposite ------

  @Test
  public void createViewMethod_isDeclared() throws NoSuchMethodException {
    Method m = KeyCustomExpressionCreatorComposite.class.getDeclaredMethod("createView");
    assertNotNull(m);
  }

  @Test
  public void createValueMethod_isDeclared() throws NoSuchMethodException {
    Method m = KeyCustomExpressionCreatorComposite.class.getDeclaredMethod("createValue");
    assertNotNull(m);
  }

  @Test
  public void getStatusPreRejectorCheckMethod_isDeclared() throws NoSuchMethodException {
    Method m = KeyCustomExpressionCreatorComposite.class.getDeclaredMethod("getStatusPreRejectorCheck");
    assertNotNull(m);
  }

  @Test
  public void initializeToPreviousExpressionMethod_isDeclared() throws NoSuchMethodException {
    Method m = KeyCustomExpressionCreatorComposite.class.getDeclaredMethod(
        "initializeToPreviousExpression", org.lgna.project.ast.Expression.class);
    assertNotNull(m);
  }

  // ---- Key type is a valid enum -----------------------------------------

  @Test
  public void keyType_resolvesViaJavaType() {
    JavaType keyType = JavaType.getInstance(org.lgna.story.Key.class);
    assertNotNull(keyType);
    assertTrue(keyType.isAssignableTo(Enum.class));
  }
}
