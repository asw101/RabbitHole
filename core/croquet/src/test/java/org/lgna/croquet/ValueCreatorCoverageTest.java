package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link ValueCreator} — creates values via cascade UI.
 * Reflection-only since headless instantiation requires cascade infrastructure.
 */
public class ValueCreatorCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(ValueCreator.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(ValueCreator.class.getModifiers()));
  }

  @Test
  public void class_hasTypeParameters() {
    assertTrue(ValueCreator.class.getTypeParameters().length > 0);
  }

  @Test
  public void class_extendsAbstractCompletionModel() {
    assertTrue(AbstractCompletionModel.class.isAssignableFrom(ValueCreator.class));
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = ValueCreator.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_fire_inherited() throws Exception {
    assertNotNull(ValueCreator.class.getMethod("fire",
        org.lgna.croquet.history.UserActivity.class));
  }

  @Test
  public void method_isEnabled_inherited() throws Exception {
    assertNotNull(ValueCreator.class.getMethod("isEnabled"));
  }

  @Test
  public void method_setEnabled_inherited() throws Exception {
    assertNotNull(ValueCreator.class.getMethod("setEnabled", boolean.class));
  }

  @Test
  public void method_getFillIn_exists() {
    assertMethodExists("getFillIn");
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(ValueCreator.class.getDeclaredMethods().length >= 3);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(ValueCreator.class.getModifiers()));
  }

  private void assertMethodExists(String name) {
    boolean found = false;
    for (Method m : ValueCreator.class.getDeclaredMethods()) {
      if (m.getName().equals(name)) {
        found = true;
        break;
      }
    }
    assertTrue("Expected method: " + name, found);
  }
}
