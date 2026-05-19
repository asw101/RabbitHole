package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link FocusWindowComposite} — manages focus windows.
 * Reflection-only since headless instantiation requires full UI infrastructure.
 */
public class FocusWindowCompositeCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(FocusWindowComposite.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(FocusWindowComposite.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractComposite() {
    assertTrue(AbstractComposite.class.isAssignableFrom(FocusWindowComposite.class));
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = FocusWindowComposite.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_initializeIfNecessary_inherited() throws Exception {
    assertNotNull(FocusWindowComposite.class.getMethod("initializeIfNecessary"));
  }

  @Test
  public void method_getLaunchOperation_exists() {
    boolean found = false;
    for (Method m : FocusWindowComposite.class.getDeclaredMethods()) {
      if ("getLaunchOperation".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected getLaunchOperation", found);
  }

  // ── Declared methods ──────────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(FocusWindowComposite.class.getDeclaredMethods().length >= 2);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(FocusWindowComposite.class.getModifiers()));
  }
}
