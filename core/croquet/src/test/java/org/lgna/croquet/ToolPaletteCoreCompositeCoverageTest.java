package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link ToolPaletteCoreComposite} — tool palette composite.
 * Reflection-only since headless instantiation requires full UI infrastructure.
 */
public class ToolPaletteCoreCompositeCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(ToolPaletteCoreComposite.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(ToolPaletteCoreComposite.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractComposite() {
    assertTrue(AbstractComposite.class.isAssignableFrom(ToolPaletteCoreComposite.class));
  }

  @Test
  public void class_hasTypeParameters() {
    assertTrue(ToolPaletteCoreComposite.class.getTypeParameters().length > 0);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = ToolPaletteCoreComposite.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_initializeIfNecessary_inherited() throws Exception {
    assertNotNull(ToolPaletteCoreComposite.class.getMethod("initializeIfNecessary"));
  }

  @Test
  public void method_modifyTextIfNecessary_exists() {
    assertMethodExists("modifyTextIfNecessary");
  }

  @Test
  public void method_getOuterComposite_exists() {
    assertMethodExists("getOuterComposite");
  }

  @Test
  public void innerClass_outerComposite_exists() {
    boolean found = false;
    for (Class<?> inner : ToolPaletteCoreComposite.class.getDeclaredClasses()) {
      if ("OuterComposite".equals(inner.getSimpleName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected OuterComposite", found);
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(ToolPaletteCoreComposite.class.getDeclaredMethods().length >= 2);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(ToolPaletteCoreComposite.class.getModifiers()));
  }

  private void assertMethodExists(String name) {
    boolean found = false;
    for (Method m : ToolPaletteCoreComposite.class.getDeclaredMethods()) {
      if (m.getName().equals(name)) {
        found = true;
        break;
      }
    }
    assertTrue("Expected method: " + name, found);
  }
}
