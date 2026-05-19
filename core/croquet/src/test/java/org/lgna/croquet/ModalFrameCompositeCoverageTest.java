package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link ModalFrameComposite} — modal frame dialog composite.
 * Reflection-only since headless instantiation requires full UI infrastructure.
 */
public class ModalFrameCompositeCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(ModalFrameComposite.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(ModalFrameComposite.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractComposite() {
    assertTrue(AbstractComposite.class.isAssignableFrom(ModalFrameComposite.class));
  }

  @Test
  public void class_hasTypeParameters() {
    assertTrue(ModalFrameComposite.class.getTypeParameters().length > 0);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = ModalFrameComposite.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_initializeIfNecessary_inherited() throws Exception {
    assertNotNull(ModalFrameComposite.class.getMethod("initializeIfNecessary"));
  }

  @Test
  public void method_localize_declared() throws Exception {
    assertNotNull(ModalFrameComposite.class.getDeclaredMethod("localize"));
  }

  @Test
  public void method_getModalFrameTitle_exists() {
    assertMethodExists("getModalFrameTitle");
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(ModalFrameComposite.class.getDeclaredMethods().length >= 2);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(ModalFrameComposite.class.getModifiers()));
  }

  private void assertMethodExists(String name) {
    boolean found = false;
    for (Method m : ModalFrameComposite.class.getDeclaredMethods()) {
      if (m.getName().equals(name)) {
        found = true;
        break;
      }
    }
    assertTrue("Expected method: " + name, found);
  }
}
