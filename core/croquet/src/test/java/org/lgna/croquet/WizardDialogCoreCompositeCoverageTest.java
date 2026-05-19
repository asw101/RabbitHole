package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link WizardDialogCoreComposite} — wizard dialog infrastructure.
 * Reflection-only since headless instantiation requires full UI setup.
 */
public class WizardDialogCoreCompositeCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(WizardDialogCoreComposite.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(WizardDialogCoreComposite.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractComposite() {
    assertTrue(AbstractComposite.class.isAssignableFrom(WizardDialogCoreComposite.class));
  }

  @Test
  public void class_hasNoTypeParameters() {
    assertEquals(0, WizardDialogCoreComposite.class.getTypeParameters().length);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = WizardDialogCoreComposite.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_getWizardPageIterator_exists() {
    assertMethodExists("getWizardPageIterator");
  }

  @Test
  public void method_getIndex_exists() {
    assertMethodExists("getIndex");
  }

  @Test
  public void method_initializeIfNecessary_inherited() throws Exception {
    assertNotNull(WizardDialogCoreComposite.class.getMethod("initializeIfNecessary"));
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(WizardDialogCoreComposite.class.getDeclaredMethods().length >= 5);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(WizardDialogCoreComposite.class.getModifiers()));
  }

  private void assertMethodExists(String name) {
    boolean found = false;
    for (Method m : WizardDialogCoreComposite.class.getDeclaredMethods()) {
      if (m.getName().equals(name)) {
        found = true;
        break;
      }
    }
    assertTrue("Expected method: " + name, found);
  }
}
