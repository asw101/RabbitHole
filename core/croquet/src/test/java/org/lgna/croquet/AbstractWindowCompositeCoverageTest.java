package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link AbstractWindowComposite} — window composite base.
 * Reflection-only since headless instantiation requires full UI infrastructure.
 */
public class AbstractWindowCompositeCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractWindowComposite.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(AbstractWindowComposite.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractComposite() {
    assertTrue(AbstractComposite.class.isAssignableFrom(AbstractWindowComposite.class));
  }

  @Test
  public void class_hasTypeParameters() {
    assertTrue(AbstractWindowComposite.class.getTypeParameters().length > 0);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = AbstractWindowComposite.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_initializeIfNecessary_inherited() throws Exception {
    assertNotNull(AbstractWindowComposite.class.getMethod("initializeIfNecessary"));
  }

  @Test
  public void method_getDesiredWindowLocation_exists() throws Exception {
    assertNotNull(AbstractWindowComposite.class.getDeclaredMethod("getDesiredWindowLocation"));
  }

  // ── Declared methods ──────────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(AbstractWindowComposite.class.getDeclaredMethods().length >= 2);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(AbstractWindowComposite.class.getModifiers()));
  }
}
