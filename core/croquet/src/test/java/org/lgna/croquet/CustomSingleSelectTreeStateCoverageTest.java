package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link CustomSingleSelectTreeState} — customizable tree state.
 * Reflection-only since headless instantiation requires tree model infrastructure.
 */
public class CustomSingleSelectTreeStateCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_extendsSingleSelectTreeState() {
    assertTrue(SingleSelectTreeState.class.isAssignableFrom(CustomSingleSelectTreeState.class));
  }

  @Test
  public void class_extendsState() {
    assertTrue(State.class.isAssignableFrom(CustomSingleSelectTreeState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(CustomSingleSelectTreeState.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(CustomSingleSelectTreeState.class.getModifiers()));
  }

  @Test
  public void class_hasTypeParameters() {
    assertTrue(CustomSingleSelectTreeState.class.getTypeParameters().length > 0);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = CustomSingleSelectTreeState.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_getValue_inherited() throws Exception {
    assertNotNull(CustomSingleSelectTreeState.class.getMethod("getValue"));
  }

  @Test
  public void method_addValueListener_inherited() {
    boolean found = false;
    for (Method m : CustomSingleSelectTreeState.class.getMethods()) {
      if ("addValueListener".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected addValueListener", found);
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(CustomSingleSelectTreeState.class.getDeclaredMethods().length >= 2);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(CustomSingleSelectTreeState.class.getModifiers()));
  }
}
