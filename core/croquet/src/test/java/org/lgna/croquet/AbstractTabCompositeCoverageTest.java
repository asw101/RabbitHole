package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link AbstractTabComposite} — base for tabbed composites.
 * Reflection-only since headless instantiation requires full UI infrastructure.
 */
public class AbstractTabCompositeCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractTabComposite.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(AbstractTabComposite.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractComposite() {
    assertTrue(AbstractComposite.class.isAssignableFrom(AbstractTabComposite.class));
  }

  @Test
  public void class_hasTypeParameters() {
    assertTrue(AbstractTabComposite.class.getTypeParameters().length > 0);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = AbstractTabComposite.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_initializeIfNecessary_inherited() throws Exception {
    assertNotNull(AbstractTabComposite.class.getMethod("initializeIfNecessary"));
  }

  @Test
  public void method_localize_declared() {
    assertMethodExists("localize");
  }

  @Test
  public void method_isPotentiallyCloseable_exists() {
    assertMethodExists("isPotentiallyCloseable");
  }

  @Test
  public void method_customizeTitleComponentAppearance_exists() {
    assertMethodExists("customizeTitleComponentAppearance");
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(AbstractTabComposite.class.getDeclaredMethods().length >= 3);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(AbstractTabComposite.class.getModifiers()));
  }

  private void assertMethodExists(String name) {
    boolean found = false;
    for (Method m : AbstractTabComposite.class.getDeclaredMethods()) {
      if (m.getName().equals(name)) {
        found = true;
        break;
      }
    }
    assertTrue("Expected method: " + name, found);
  }
}
