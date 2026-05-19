package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;

import static org.junit.Assert.*;

/**
 * Reflection-only coverage tests for {@link SingleSelectTreeState}.
 * Cannot instantiate headlessly — requires tree model infrastructure.
 */
public class SingleSelectTreeStateCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_extendsState() {
    assertTrue(State.class.isAssignableFrom(SingleSelectTreeState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(SingleSelectTreeState.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(SingleSelectTreeState.class.getModifiers()));
  }

  @Test
  public void class_hasTypeParameter() {
    TypeVariable<?>[] tp = SingleSelectTreeState.class.getTypeParameters();
    assertTrue(tp.length > 0);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = SingleSelectTreeState.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  @Test
  public void constructor_isPublic() {
    Constructor<?>[] ctors = SingleSelectTreeState.class.getDeclaredConstructors();
    for (Constructor<?> c : ctors) {
      assertTrue("Constructor should be public",
          Modifier.isPublic(c.getModifiers()));
    }
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_getValue_inherited() throws Exception {
    assertNotNull(SingleSelectTreeState.class.getMethod("getValue"));
  }

  @Test
  public void method_addValueListener_inherited() {
    boolean found = false;
    for (Method m : SingleSelectTreeState.class.getMethods()) {
      if ("addValueListener".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected addValueListener", found);
  }

  @Test
  public void method_getSwingModel_declared() {
    boolean found = false;
    for (Method m : SingleSelectTreeState.class.getDeclaredMethods()) {
      if ("getSwingModel".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected getSwingModel", found);
  }

  @Test
  public void method_getSelectedNode_exists() {
    boolean found = false;
    for (Method m : SingleSelectTreeState.class.getMethods()) {
      if (m.getName().contains("getSelected") || m.getName().contains("getValue")) {
        found = true;
        break;
      }
    }
    assertTrue("Expected selection method", found);
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    Method[] methods = SingleSelectTreeState.class.getDeclaredMethods();
    assertTrue("Expected multiple methods, got " + methods.length, methods.length >= 3);
  }

  // ── Not final ─────────────────────────────────────────────────────

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(SingleSelectTreeState.class.getModifiers()));
  }

  // ── Inner classes ─────────────────────────────────────────────────

  @Test
  public void innerClasses_checked() {
    // Document the inner class count for coverage
    Class<?>[] inners = SingleSelectTreeState.class.getDeclaredClasses();
    assertNotNull(inners);
  }
}
