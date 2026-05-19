package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link CardOwnerComposite} — composite that manages card panels.
 * Uses reflection only since headless instantiation is not possible.
 */
public class CardOwnerCompositeCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(CardOwnerComposite.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(CardOwnerComposite.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractComposite() {
    assertTrue(AbstractComposite.class.isAssignableFrom(CardOwnerComposite.class));
  }

  @Test
  public void class_hasNoTypeParameters() {
    assertEquals(0, CardOwnerComposite.class.getTypeParameters().length);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = CardOwnerComposite.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_showCard_exists() {
    assertMethodExists("showCard");
  }

  @Test
  public void method_getCards_exists() {
    assertMethodExists("getCards");
  }

  @Test
  public void method_initializeIfNecessary_inherited() throws Exception {
    assertNotNull(CardOwnerComposite.class.getMethod("initializeIfNecessary"));
  }

  @Test
  public void method_getShowingCard_exists() {
    assertMethodExists("getShowingCard");
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(CardOwnerComposite.class.getDeclaredMethods().length >= 3);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(CardOwnerComposite.class.getModifiers()));
  }

  private void assertMethodExists(String name) {
    boolean found = false;
    for (Method m : CardOwnerComposite.class.getDeclaredMethods()) {
      if (m.getName().equals(name)) {
        found = true;
        break;
      }
    }
    assertTrue("Expected method: " + name, found);
  }
}
