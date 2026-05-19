package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link AdornedDialogCoreComposite} — adorned dialog composite.
 * Reflection-only since headless instantiation requires full UI infrastructure.
 */
public class AdornedDialogCoreCompositeCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(AdornedDialogCoreComposite.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(AdornedDialogCoreComposite.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractComposite() {
    assertTrue(AbstractComposite.class.isAssignableFrom(AdornedDialogCoreComposite.class));
  }

  @Test
  public void class_hasTypeParameters() {
    assertTrue(AdornedDialogCoreComposite.class.getTypeParameters().length > 0);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = AdornedDialogCoreComposite.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_initializeIfNecessary_inherited() throws Exception {
    assertNotNull(AdornedDialogCoreComposite.class.getMethod("initializeIfNecessary"));
  }

  @Test
  public void method_getDialogContentComposite_exists() {
    boolean found = false;
    for (Method m : AdornedDialogCoreComposite.class.getDeclaredMethods()) {
      if ("getDialogContentComposite".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected getDialogContentComposite", found);
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(AdornedDialogCoreComposite.class.getDeclaredMethods().length >= 3);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(AdornedDialogCoreComposite.class.getModifiers()));
  }

  // ── Implements OperationOwningComposite ────────────────────────────

  @Test
  public void class_doesNotImplementOperationOwningComposite() {
    assertFalse(OperationOwningComposite.class.isAssignableFrom(AdornedDialogCoreComposite.class));
  }

  private java.util.Set<Class<?>> getAllInterfaces(Class<?> clazz) {
    java.util.Set<Class<?>> result = new java.util.HashSet<>();
    while (clazz != null) {
      for (Class<?> i : clazz.getInterfaces()) {
        result.add(i);
      }
      clazz = clazz.getSuperclass();
    }
    return result;
  }
}
