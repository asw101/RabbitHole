package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link AbstractDialogComposite} — base dialog composite.
 * Reflection-only since headless instantiation requires full dialog infrastructure.
 */
public class AbstractDialogCompositeCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractDialogComposite.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(AbstractDialogComposite.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractComposite() {
    assertTrue(AbstractComposite.class.isAssignableFrom(AbstractDialogComposite.class));
  }

  @Test
  public void class_hasTypeParameters() {
    assertTrue(AbstractDialogComposite.class.getTypeParameters().length > 0);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = AbstractDialogComposite.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  @Test
  public void constructor_isPublic() {
    for (Constructor<?> c : AbstractDialogComposite.class.getDeclaredConstructors()) {
      assertTrue(Modifier.isPublic(c.getModifiers()));
    }
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_getDefaultTitleText_exists() {
    assertMethodExists("getDefaultTitleText");
  }

  @Test
  public void method_getDialogTitle_exists() {
    assertMethodExists("getDialogTitle");
  }

  @Test
  public void method_initializeIfNecessary_inherited() throws Exception {
    assertNotNull(AbstractDialogComposite.class.getMethod("initializeIfNecessary"));
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(AbstractDialogComposite.class.getDeclaredMethods().length >= 3);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(AbstractDialogComposite.class.getModifiers()));
  }

  private void assertMethodExists(String name) {
    boolean found = false;
    for (Method m : AbstractDialogComposite.class.getDeclaredMethods()) {
      if (m.getName().equals(name)) {
        found = true;
        break;
      }
    }
    assertTrue("Expected method: " + name, found);
  }
}
