package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link MutableSplitComposite} — mutable split pane composite.
 * Reflection-only since headless instantiation requires full UI infrastructure.
 */
public class MutableSplitCompositeCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(MutableSplitComposite.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(MutableSplitComposite.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractSplitComposite() {
    assertTrue(AbstractSplitComposite.class.isAssignableFrom(MutableSplitComposite.class));
  }

  @Test
  public void class_extendsAbstractComposite() {
    assertTrue(AbstractComposite.class.isAssignableFrom(MutableSplitComposite.class));
  }

  @Test
  public void class_hasNoTypeParameters() {
    assertEquals(0, MutableSplitComposite.class.getTypeParameters().length);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = MutableSplitComposite.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_initializeIfNecessary_inherited() throws Exception {
    assertNotNull(MutableSplitComposite.class.getMethod("initializeIfNecessary"));
  }

  @Test
  public void method_setLeadingComposite_exists() {
    boolean found = false;
    for (Method m : MutableSplitComposite.class.getDeclaredMethods()) {
      if ("setLeadingComposite".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected setLeadingComposite", found);
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(MutableSplitComposite.class.getDeclaredMethods().length >= 2);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(MutableSplitComposite.class.getModifiers()));
  }
}
