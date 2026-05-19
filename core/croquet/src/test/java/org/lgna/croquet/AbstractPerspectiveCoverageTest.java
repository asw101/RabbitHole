package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link AbstractPerspective} — perspective management.
 * Reflection-only since headless instantiation requires Application context.
 */
public class AbstractPerspectiveCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractPerspective.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(AbstractPerspective.class.getModifiers()));
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = AbstractPerspective.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(AbstractPerspective.class.getDeclaredMethods().length >= 2);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(AbstractPerspective.class.getModifiers()));
  }
}
