package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;

import static org.junit.Assert.*;

/**
 * Tests for {@link BoundedNumberState} — abstract number state with min/max/step.
 * Covers class structure, AtomicChange inner class, and method signatures.
 */
public class BoundedNumberStateDeepTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_extendsState() {
    assertTrue(State.class.isAssignableFrom(BoundedNumberState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(BoundedNumberState.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(BoundedNumberState.class.getModifiers()));
  }

  @Test
  public void class_hasTypeParameter() {
    TypeVariable<?>[] tp = BoundedNumberState.class.getTypeParameters();
    assertEquals(1, tp.length);
  }

  @Test
  public void class_typeParameterBounded() {
    TypeVariable<?>[] tp = BoundedNumberState.class.getTypeParameters();
    assertTrue(tp[0].getBounds().length > 0);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructor_exists() {
    Constructor<?>[] ctors = BoundedNumberState.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  @Test
  public void constructor_isPublic() {
    Constructor<?>[] ctors = BoundedNumberState.class.getDeclaredConstructors();
    for (Constructor<?> c : ctors) {
      if (c.getParameterCount() >= 3) {
        assertTrue(Modifier.isPublic(c.getModifiers()));
      }
    }
  }

  // ── Method signatures ─────────────────────────────────────────────

  @Test
  public void method_getMinimum_exists() throws Exception {
    assertNotNull(BoundedNumberState.class.getMethod("getMinimum"));
  }

  @Test
  public void method_setMinimum_exists() throws Exception {
    assertNotNull(BoundedNumberState.class.getMethod("setMinimum", Number.class));
  }

  @Test
  public void method_getMaximum_exists() throws Exception {
    assertNotNull(BoundedNumberState.class.getMethod("getMaximum"));
  }

  @Test
  public void method_setMaximum_exists() throws Exception {
    assertNotNull(BoundedNumberState.class.getMethod("setMaximum", Number.class));
  }

  @Test
  public void method_getSwingModel_exists() throws Exception {
    assertNotNull(BoundedNumberState.class.getMethod("getSwingModel"));
  }

  @Test
  public void method_createSlider_exists() throws Exception {
    Method[] methods = BoundedNumberState.class.getDeclaredMethods();
    boolean found = false;
    for (Method m : methods) {
      if ("createSlider".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected createSlider method", found);
  }

  @Test
  public void method_createSpinner_exists() throws Exception {
    Method[] methods = BoundedNumberState.class.getDeclaredMethods();
    boolean found = false;
    for (Method m : methods) {
      if ("createSpinner".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected createSpinner method", found);
  }

  @Test
  public void method_setAll_exists() throws Exception {
    Method[] methods = BoundedNumberState.class.getDeclaredMethods();
    boolean found = false;
    for (Method m : methods) {
      if ("setAll".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected setAll method", found);
  }

  // ── AtomicChange inner class ──────────────────────────────────────

  @Test
  public void atomicChange_innerClassExists() {
    Class<?>[] inners = BoundedNumberState.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> c : inners) {
      if ("AtomicChange".equals(c.getSimpleName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected AtomicChange inner class", found);
  }

  @Test
  public void atomicChange_isPublic() {
    for (Class<?> c : BoundedNumberState.class.getDeclaredClasses()) {
      if ("AtomicChange".equals(c.getSimpleName())) {
        assertTrue(Modifier.isPublic(c.getModifiers()));
      }
    }
  }

  @Test
  public void atomicChange_isStatic() {
    for (Class<?> c : BoundedNumberState.class.getDeclaredClasses()) {
      if ("AtomicChange".equals(c.getSimpleName())) {
        assertTrue(Modifier.isStatic(c.getModifiers()));
      }
    }
  }

  @Test
  public void atomicChange_hasTypeParameter() {
    for (Class<?> c : BoundedNumberState.class.getDeclaredClasses()) {
      if ("AtomicChange".equals(c.getSimpleName())) {
        assertTrue(c.getTypeParameters().length > 0);
      }
    }
  }

  @Test
  public void atomicChange_hasMethods() {
    for (Class<?> c : BoundedNumberState.class.getDeclaredClasses()) {
      if ("AtomicChange".equals(c.getSimpleName())) {
        Method[] methods = c.getDeclaredMethods();
        assertTrue("Expected methods in AtomicChange", methods.length > 0);
      }
    }
  }

  // ── SwingModel inner interface ────────────────────────────────────

  @Test
  public void swingModel_innerInterfaceExists() {
    Class<?>[] inners = BoundedNumberState.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> c : inners) {
      if ("SwingModel".equals(c.getSimpleName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected SwingModel inner interface", found);
  }

  @Test
  public void swingModel_isInterface() {
    for (Class<?> c : BoundedNumberState.class.getDeclaredClasses()) {
      if ("SwingModel".equals(c.getSimpleName())) {
        assertTrue(c.isInterface());
      }
    }
  }

  // ── Protected methods ─────────────────────────────────────────────

  @Test
  public void method_appendRepresentation_exists() throws Exception {
    Method m = BoundedNumberState.class.getDeclaredMethod(
        "appendRepresentation", StringBuilder.class, Object.class);
    assertNotNull(m);
  }

  @Test
  public void method_localize_exists() throws Exception {
    Method m = BoundedNumberState.class.getDeclaredMethod("localize");
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void method_setSwingValue_exists() {
    Method[] methods = BoundedNumberState.class.getDeclaredMethods();
    boolean found = false;
    for (Method m : methods) {
      if ("setSwingValue".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected setSwingValue method", found);
  }

  // ── Inherited from State ──────────────────────────────────────────

  @Test
  public void method_getValue_inherited() throws Exception {
    assertNotNull(BoundedNumberState.class.getMethod("getValue"));
  }

  @Test
  public void method_addValueListener_inherited() {
    boolean found = false;
    for (Method m : BoundedNumberState.class.getMethods()) {
      if ("addValueListener".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected addValueListener", found);
  }

  @Test
  public void method_addAndInvokeValueListener_inherited() {
    boolean found = false;
    for (Method m : BoundedNumberState.class.getMethods()) {
      if ("addAndInvokeValueListener".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected addAndInvokeValueListener", found);
  }

  @Test
  public void method_removeValueListener_inherited() {
    boolean found = false;
    for (Method m : BoundedNumberState.class.getMethods()) {
      if ("removeValueListener".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected removeValueListener", found);
  }

  // ── Inner class count ─────────────────────────────────────────────

  @Test
  public void innerClassCount_atLeastTwo() {
    assertTrue("Expected at least 2 inner classes (AtomicChange, SwingModel)",
        BoundedNumberState.class.getDeclaredClasses().length >= 2);
  }
}
