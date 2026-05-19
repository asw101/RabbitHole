package org.alice.ide.x.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Reflection-based structural tests for the two croquet classes:
 * - SceneEditorUpdatingArgumentCascade (synchronized cache with static Map)
 * - SceneEditorUpdatingExpressionPropertyEdit (UserField-aware edit)
 *
 * All tests are headless-safe (pure reflection, no GUI instantiation)
 * and never invoke getInstance() or constructors.
 */
public class AstI18nFactoryCroquetTest {

  private static final String CASCADE_FQN =
      "org.alice.ide.x.croquet.SceneEditorUpdatingArgumentCascade";
  private static final String EDIT_FQN =
      "org.alice.ide.x.croquet.edits.SceneEditorUpdatingExpressionPropertyEdit";

  // ========================================================================
  // SceneEditorUpdatingArgumentCascade
  // ========================================================================

  @Test
  public void cascade_isLoadable() throws ClassNotFoundException {
    Class.forName(CASCADE_FQN);
  }

  @Test
  public void cascade_isConcrete() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CASCADE_FQN);
    assertFalse(Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void cascade_isPublic() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CASCADE_FQN);
    assertTrue(Modifier.isPublic(cls.getModifiers()));
  }

  @Test
  public void cascade_extendsAbstractArgumentCascade() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CASCADE_FQN);
    assertEquals("AbstractArgumentCascade", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void cascade_hasPrivateConstructor() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CASCADE_FQN);
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
  }

  @Test
  public void cascade_constructorTakesSimpleArgument() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CASCADE_FQN);
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors[0].getParameterCount());
    assertEquals("SimpleArgument", ctors[0].getParameterTypes()[0].getSimpleName());
  }

  @Test
  public void cascade_hasStaticGetInstance() throws ClassNotFoundException, NoSuchMethodException {
    Class<?> cls = Class.forName(CASCADE_FQN);
    Method m = cls.getMethod("getInstance", org.lgna.project.ast.SimpleArgument.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void cascade_getInstanceIsSynchronized() throws ClassNotFoundException, NoSuchMethodException {
    Class<?> cls = Class.forName(CASCADE_FQN);
    Method m = cls.getMethod("getInstance", org.lgna.project.ast.SimpleArgument.class);
    assertTrue(Modifier.isSynchronized(m.getModifiers()));
  }

  @Test
  public void cascade_getInstanceReturnsSelf() throws ClassNotFoundException, NoSuchMethodException {
    Class<?> cls = Class.forName(CASCADE_FQN);
    Method m = cls.getMethod("getInstance", org.lgna.project.ast.SimpleArgument.class);
    assertEquals(cls, m.getReturnType());
  }

  @Test
  public void cascade_hasStaticMapField() throws ClassNotFoundException, NoSuchFieldException {
    Class<?> cls = Class.forName(CASCADE_FQN);
    Field f = cls.getDeclaredField("map");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Map.class.isAssignableFrom(f.getType()));
  }

  @Test
  public void cascade_hasCreateExpressionPropertyEdit() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CASCADE_FQN);
    for (Method m : cls.getDeclaredMethods()) {
      if (m.getName().equals("createExpressionPropertyEdit")) {
        assertTrue(Modifier.isProtected(m.getModifiers()));
        assertFalse(Modifier.isAbstract(m.getModifiers()));
        return;
      }
    }
    fail("createExpressionPropertyEdit not found");
  }

  @Test
  public void cascade_superclassIsNotNull() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CASCADE_FQN);
    // UUID is inherited from CascadeRoot or higher in the hierarchy;
    // verify the hierarchy is inspectable
    assertNotNull(cls.getSuperclass());
  }

  @Test
  public void cascade_noInnerClasses() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CASCADE_FQN);
    assertEquals(0, cls.getDeclaredClasses().length);
  }

  @Test
  public void cascade_packageIsCorrect() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CASCADE_FQN);
    assertEquals("org.alice.ide.x.croquet", cls.getPackage().getName());
  }

  // ========================================================================
  // SceneEditorUpdatingExpressionPropertyEdit
  // ========================================================================

  @Test
  public void edit_isLoadable() throws ClassNotFoundException {
    Class.forName(EDIT_FQN);
  }

  @Test
  public void edit_isConcrete() throws ClassNotFoundException {
    Class<?> cls = Class.forName(EDIT_FQN);
    assertFalse(Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void edit_isPublic() throws ClassNotFoundException {
    Class<?> cls = Class.forName(EDIT_FQN);
    assertTrue(Modifier.isPublic(cls.getModifiers()));
  }

  @Test
  public void edit_extendsExpressionPropertyEdit() throws ClassNotFoundException {
    Class<?> cls = Class.forName(EDIT_FQN);
    assertEquals("ExpressionPropertyEdit", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void edit_hasTwoConstructors() throws ClassNotFoundException {
    Class<?> cls = Class.forName(EDIT_FQN);
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(2, ctors.length);
  }

  @Test
  public void edit_hasFiveParamConstructor() throws ClassNotFoundException {
    Class<?> cls = Class.forName(EDIT_FQN);
    boolean found = false;
    for (Constructor<?> c : cls.getDeclaredConstructors()) {
      if (c.getParameterCount() == 5) {
        found = true;
        assertTrue(Modifier.isPublic(c.getModifiers()));
        Class<?>[] params = c.getParameterTypes();
        assertEquals("UserActivity", params[0].getSimpleName());
        assertEquals("ExpressionProperty", params[1].getSimpleName());
        assertEquals("Expression", params[2].getSimpleName());
        assertEquals("Expression", params[3].getSimpleName());
        assertEquals("UserField", params[4].getSimpleName());
      }
    }
    assertTrue("5-param constructor not found", found);
  }

  @Test
  public void edit_hasBinaryDecoderConstructor() throws ClassNotFoundException {
    Class<?> cls = Class.forName(EDIT_FQN);
    boolean found = false;
    for (Constructor<?> c : cls.getDeclaredConstructors()) {
      if (c.getParameterCount() == 2) {
        found = true;
        assertTrue(Modifier.isPublic(c.getModifiers()));
        Class<?>[] params = c.getParameterTypes();
        assertEquals("BinaryDecoder", params[0].getSimpleName());
      }
    }
    assertTrue("BinaryDecoder constructor not found", found);
  }

  @Test
  public void edit_hasUserFieldField() throws ClassNotFoundException, NoSuchFieldException {
    Class<?> cls = Class.forName(EDIT_FQN);
    Field f = cls.getDeclaredField("field");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals("UserField", f.getType().getSimpleName());
  }

  @Test
  public void edit_hasSetValue() throws ClassNotFoundException {
    Class<?> cls = Class.forName(EDIT_FQN);
    for (Method m : cls.getDeclaredMethods()) {
      if (m.getName().equals("setValue")) {
        assertTrue(Modifier.isProtected(m.getModifiers()));
        assertEquals(1, m.getParameterCount());
        assertEquals("Expression", m.getParameterTypes()[0].getSimpleName());
        return;
      }
    }
    fail("setValue method not found");
  }

  @Test
  public void edit_noInnerClasses() throws ClassNotFoundException {
    Class<?> cls = Class.forName(EDIT_FQN);
    assertEquals(0, cls.getDeclaredClasses().length);
  }

  @Test
  public void edit_packageIsCorrect() throws ClassNotFoundException {
    Class<?> cls = Class.forName(EDIT_FQN);
    assertEquals("org.alice.ide.x.croquet.edits", cls.getPackage().getName());
  }

  @Test
  public void edit_superclassIsInCroquetEditsPackage() throws ClassNotFoundException {
    Class<?> cls = Class.forName(EDIT_FQN);
    Class<?> superCls = cls.getSuperclass();
    assertNotNull(superCls);
    assertTrue(superCls.getName().contains("croquet.edits"));
  }

  @Test
  public void edit_inheritanceChain() throws ClassNotFoundException {
    Class<?> cls = Class.forName(EDIT_FQN);
    // SceneEditorUpdatingExpressionPropertyEdit -> ExpressionPropertyEdit
    assertEquals("ExpressionPropertyEdit", cls.getSuperclass().getSimpleName());
    // ExpressionPropertyEdit -> some Edit base class
    assertNotNull(cls.getSuperclass().getSuperclass());
  }

  // ========================================================================
  // Cross-cutting: both classes exist in expected packages
  // ========================================================================

  @Test
  public void bothClasses_areDifferentPackages() throws ClassNotFoundException {
    Class<?> cascade = Class.forName(CASCADE_FQN);
    Class<?> edit = Class.forName(EDIT_FQN);
    assertNotEquals(cascade.getPackage().getName(), edit.getPackage().getName());
  }

  @Test
  public void cascade_methodCount() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CASCADE_FQN);
    Method[] declaredMethods = cls.getDeclaredMethods();
    // getInstance + createExpressionPropertyEdit = at least 2
    assertTrue("Should have at least 2 declared methods",
        declaredMethods.length >= 2);
  }

  @Test
  public void edit_methodCount() throws ClassNotFoundException {
    Class<?> cls = Class.forName(EDIT_FQN);
    Method[] declaredMethods = cls.getDeclaredMethods();
    // setValue = at least 1
    assertTrue("Should have at least 1 declared method",
        declaredMethods.length >= 1);
  }
}
