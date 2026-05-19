package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link CascadeRoot} — root of the cascade cascade item tree.
 * Reflection-only since headless instantiation requires full UI infrastructure.
 */
public class CascadeRootCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(CascadeRoot.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(CascadeRoot.class.getModifiers()));
  }

  @Test
  public void class_hasTypeParameters() {
    assertTrue(CascadeRoot.class.getTypeParameters().length > 0);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = CascadeRoot.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_getPopupPrepModel_exists() {
    assertMethodExists("getPopupPrepModel");
  }

  @Test
  public void method_getCompletionModel_exists() {
    assertMethodExists("getCompletionModel");
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(CascadeRoot.class.getDeclaredMethods().length >= 2);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(CascadeRoot.class.getModifiers()));
  }

  // ── Inner classes ─────────────────────────────────────────────────

  @Test
  public void innerClasses_checked() {
    assertNotNull(CascadeRoot.class.getDeclaredClasses());
  }

  private void assertMethodExists(String name) {
    boolean found = false;
    for (Method m : CascadeRoot.class.getDeclaredMethods()) {
      if (m.getName().equals(name)) {
        found = true;
        break;
      }
    }
    assertTrue("Expected method: " + name, found);
  }
}
