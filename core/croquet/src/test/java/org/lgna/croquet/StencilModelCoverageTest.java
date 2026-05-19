package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link StencilModel} — model for UI stencils/overlays.
 * Reflection-only since headless instantiation requires full UI infrastructure.
 */
public class StencilModelCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(StencilModel.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(StencilModel.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractModel() {
    assertTrue(AbstractModel.class.isAssignableFrom(StencilModel.class));
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = StencilModel.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_isEnabled_inherited() throws Exception {
    assertNotNull(StencilModel.class.getMethod("isEnabled"));
  }

  @Test
  public void method_setEnabled_inherited() throws Exception {
    assertNotNull(StencilModel.class.getMethod("setEnabled", boolean.class));
  }

  @Test
  public void method_getText_exists() {
    boolean found = false;
    for (Method m : StencilModel.class.getDeclaredMethods()) {
      if ("getText".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected getText", found);
  }

  // ── Declared methods ──────────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(StencilModel.class.getDeclaredMethods().length >= 1);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(StencilModel.class.getModifiers()));
  }
}
