package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link AbstractCascadeMenuModel} — base for cascade menu models.
 * Reflection-only since headless instantiation requires cascade infrastructure.
 */
public class AbstractCascadeMenuModelCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractCascadeMenuModel.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(AbstractCascadeMenuModel.class.getModifiers()));
  }

  @Test
  public void class_hasTypeParameters() {
    assertTrue(AbstractCascadeMenuModel.class.getTypeParameters().length > 0);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = AbstractCascadeMenuModel.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_isEnabled_inherited() throws Exception {
    assertNotNull(AbstractCascadeMenuModel.class.getMethod("isEnabled"));
  }

  @Test
  public void method_setEnabled_inherited() throws Exception {
    assertNotNull(AbstractCascadeMenuModel.class.getMethod("setEnabled", boolean.class));
  }

  @Test
  public void method_getBlanks_exists() throws Exception {
    assertNotNull(AbstractCascadeMenuModel.class.getMethod("getBlanks"));
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(AbstractCascadeMenuModel.class.getDeclaredMethods().length >= 2);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(AbstractCascadeMenuModel.class.getModifiers()));
  }
}
