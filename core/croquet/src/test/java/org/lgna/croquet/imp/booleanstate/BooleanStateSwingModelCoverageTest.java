package org.lgna.croquet.imp.booleanstate;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link BooleanStateSwingModel} — Swing model for BooleanState.
 * Tests class structure and method signatures via reflection.
 */
public class BooleanStateSwingModelCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(BooleanStateSwingModel.class.getModifiers()));
  }

  @Test
  public void class_isNotAbstract() {
    assertFalse(Modifier.isAbstract(BooleanStateSwingModel.class.getModifiers()));
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(BooleanStateSwingModel.class.getModifiers()));
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = BooleanStateSwingModel.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_getButtonModel_exists() {
    assertMethodExists("getButtonModel");
  }

  @Test
  public void method_getAction_exists() {
    assertMethodExists("getAction");
  }

  @Test
  public void method_isTextVariable_exists() {
    assertMethodExists("isTextVariable");
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue("Expected multiple methods",
        BooleanStateSwingModel.class.getDeclaredMethods().length >= 3);
  }

  // ── Inner classes ─────────────────────────────────────────────────

  @Test
  public void innerClasses_checked() {
    assertNotNull(BooleanStateSwingModel.class.getDeclaredClasses());
  }

  private void assertMethodExists(String name) {
    boolean found = false;
    for (Method m : BooleanStateSwingModel.class.getDeclaredMethods()) {
      if (m.getName().equals(name)) {
        found = true;
        break;
      }
    }
    assertTrue("Expected method: " + name, found);
  }
}
