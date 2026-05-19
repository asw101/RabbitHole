package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;

import static org.junit.Assert.*;

public class BoundedNumberStateAtomicChangeTest {

  private BoundedNumberState.AtomicChange<Integer> change;

  @Before
  public void setUp() {
    change = new BoundedNumberState.AtomicChange<>();
  }

  // ── Class structure ────────────────────────────────────────────────

  @Test
  public void class_isStaticInner() {
    assertTrue(Modifier.isStatic(BoundedNumberState.AtomicChange.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(BoundedNumberState.AtomicChange.class.getModifiers()));
  }

  @Test
  public void class_isNotAbstract() {
    assertFalse(Modifier.isAbstract(BoundedNumberState.AtomicChange.class.getModifiers()));
  }

  @Test
  public void class_hasTypeParameter_N_extends_Number() {
    TypeVariable<?>[] params = BoundedNumberState.AtomicChange.class.getTypeParameters();
    assertEquals(1, params.length);
    assertEquals("N", params[0].getName());
    assertEquals(Number.class, params[0].getBounds()[0]);
  }

  // ── Field structure ────────────────────────────────────────────────

  @Test
  public void field_minimum_exists() throws Exception {
    Field f = BoundedNumberState.AtomicChange.class.getDeclaredField("minimum");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  @Test
  public void field_maximum_exists() throws Exception {
    Field f = BoundedNumberState.AtomicChange.class.getDeclaredField("maximum");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  @Test
  public void field_stepSize_exists() throws Exception {
    Field f = BoundedNumberState.AtomicChange.class.getDeclaredField("stepSize");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  @Test
  public void field_extent_exists() throws Exception {
    Field f = BoundedNumberState.AtomicChange.class.getDeclaredField("extent");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  @Test
  public void field_value_exists() throws Exception {
    Field f = BoundedNumberState.AtomicChange.class.getDeclaredField("value");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  @Test
  public void field_isAdjusting_exists() throws Exception {
    Field f = BoundedNumberState.AtomicChange.class.getDeclaredField("isAdjusting");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  @Test
  public void fieldCount_is_six() {
    Field[] fields = BoundedNumberState.AtomicChange.class.getDeclaredFields();
    assertEquals(6, fields.length);
  }

  // ── Fluent builder ─────────────────────────────────────────────────

  @Test
  public void minimum_returnsSelf() {
    BoundedNumberState.AtomicChange<Integer> result = change.minimum(0);
    assertSame(change, result);
  }

  @Test
  public void maximum_returnsSelf() {
    BoundedNumberState.AtomicChange<Integer> result = change.maximum(100);
    assertSame(change, result);
  }

  @Test
  public void stepSize_returnsSelf() {
    BoundedNumberState.AtomicChange<Integer> result = change.stepSize(5);
    assertSame(change, result);
  }

  @Test
  public void extent_returnsSelf() {
    BoundedNumberState.AtomicChange<Integer> result = change.extent(10);
    assertSame(change, result);
  }

  @Test
  public void value_returnsSelf() {
    BoundedNumberState.AtomicChange<Integer> result = change.value(42);
    assertSame(change, result);
  }

  @Test
  public void isAdjusting_returnsSelf() {
    BoundedNumberState.AtomicChange<Integer> result = change.isAdjusting(true);
    assertSame(change, result);
  }

  // ── Fluent chaining ────────────────────────────────────────────────

  @Test
  public void fluentChain_allSetters() {
    BoundedNumberState.AtomicChange<Integer> result = change
        .minimum(0)
        .maximum(100)
        .stepSize(1)
        .extent(0)
        .value(50)
        .isAdjusting(false);

    assertSame(change, result);
  }

  @Test
  public void fluentChain_partial_minMaxValue() {
    BoundedNumberState.AtomicChange<Integer> result = change
        .minimum(10)
        .maximum(90)
        .value(50);

    assertSame(change, result);
  }

  @Test
  public void fluentChain_onlyValue() {
    BoundedNumberState.AtomicChange<Integer> result = change.value(7);
    assertSame(change, result);
  }

  // ── Field values via reflection ────────────────────────────────────

  @Test
  public void minimum_storesValue() throws Exception {
    change.minimum(5);
    assertEquals(Integer.valueOf(5), getField("minimum"));
  }

  @Test
  public void maximum_storesValue() throws Exception {
    change.maximum(200);
    assertEquals(Integer.valueOf(200), getField("maximum"));
  }

  @Test
  public void stepSize_storesValue() throws Exception {
    change.stepSize(3);
    assertEquals(Integer.valueOf(3), getField("stepSize"));
  }

  @Test
  public void extent_storesValue() throws Exception {
    change.extent(15);
    assertEquals(Integer.valueOf(15), getField("extent"));
  }

  @Test
  public void value_storesValue() throws Exception {
    change.value(42);
    assertEquals(Integer.valueOf(42), getField("value"));
  }

  @Test
  public void isAdjusting_storesTrue() throws Exception {
    change.isAdjusting(true);
    assertEquals(Boolean.TRUE, getField("isAdjusting"));
  }

  @Test
  public void isAdjusting_storesFalse() throws Exception {
    change.isAdjusting(false);
    assertEquals(Boolean.FALSE, getField("isAdjusting"));
  }

  @Test
  public void fields_initiallyNull_except_isAdjusting() throws Exception {
    assertNull(getField("minimum"));
    assertNull(getField("maximum"));
    assertNull(getField("stepSize"));
    assertNull(getField("extent"));
    assertNull(getField("value"));
    assertEquals(Boolean.FALSE, getField("isAdjusting"));
  }

  // ── Double-typed AtomicChange ──────────────────────────────────────

  @Test
  public void doubleTyped_fluent_works() throws Exception {
    BoundedNumberState.AtomicChange<Double> dc = new BoundedNumberState.AtomicChange<>();
    BoundedNumberState.AtomicChange<Double> result = dc
        .minimum(0.0)
        .maximum(1.0)
        .stepSize(0.01)
        .value(0.5);

    assertSame(dc, result);
    Field f = BoundedNumberState.AtomicChange.class.getDeclaredField("value");
    f.setAccessible(true);
    assertEquals(Double.valueOf(0.5), f.get(dc));
  }

  // ── updateSwingModel method ────────────────────────────────────────

  @Test
  public void updateSwingModel_method_exists() throws Exception {
    Method m = BoundedNumberState.AtomicChange.class.getDeclaredMethod(
        "updateSwingModel", BoundedNumberState.SwingModel.class);
    assertFalse(Modifier.isPublic(m.getModifiers()));
  }

  // ── Method count ───────────────────────────────────────────────────

  @Test
  public void declaredMethods_count_matches() {
    Method[] methods = BoundedNumberState.AtomicChange.class.getDeclaredMethods();
    int count = 0;
    for (Method m : methods) {
      if (!m.isSynthetic() && !m.isBridge()) {
        count++;
      }
    }
    assertEquals("AtomicChange should have 7 methods", 7, count);
  }

  // ── BoundedNumberState class-level tests ───────────────────────────

  @Test
  public void boundedNumberState_isAbstract() {
    assertTrue(Modifier.isAbstract(BoundedNumberState.class.getModifiers()));
  }

  @Test
  public void boundedNumberState_extendsState() {
    assertEquals(State.class, BoundedNumberState.class.getSuperclass());
  }

  @Test
  public void boundedNumberState_hasTypeParam_N_extends_Number() {
    TypeVariable<?>[] params = BoundedNumberState.class.getTypeParameters();
    assertEquals(1, params.length);
    assertEquals("N", params[0].getName());
    assertEquals(Number.class, params[0].getBounds()[0]);
  }

  @Test
  public void swingModel_interface_exists() {
    Class<?>[] inner = BoundedNumberState.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> c : inner) {
      if ("SwingModel".equals(c.getSimpleName())) {
        found = true;
        assertTrue(c.isInterface());
        break;
      }
    }
    assertTrue("SwingModel interface should exist", found);
  }

  @Test
  public void atomicChange_class_exists() {
    Class<?>[] inner = BoundedNumberState.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> c : inner) {
      if ("AtomicChange".equals(c.getSimpleName())) {
        found = true;
        assertFalse(c.isInterface());
        break;
      }
    }
    assertTrue("AtomicChange class should exist", found);
  }

  @Test
  public void innerClassCount_isTwo() {
    Class<?>[] inner = BoundedNumberState.class.getDeclaredClasses();
    assertEquals(2, inner.length);
  }

  // ── Abstract methods ───────────────────────────────────────────────

  @Test
  public void getMinimum_isAbstract() throws Exception {
    Method m = BoundedNumberState.class.getDeclaredMethod("getMinimum");
    assertTrue(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void setMinimum_isAbstract() throws Exception {
    Method m = BoundedNumberState.class.getDeclaredMethod("setMinimum", Number.class);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void getMaximum_isAbstract() throws Exception {
    Method m = BoundedNumberState.class.getDeclaredMethod("getMaximum");
    assertTrue(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void setMaximum_isAbstract() throws Exception {
    Method m = BoundedNumberState.class.getDeclaredMethod("setMaximum", Number.class);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
  }

  private Object getField(String name) throws Exception {
    Field f = BoundedNumberState.AtomicChange.class.getDeclaredField(name);
    f.setAccessible(true);
    return f.get(change);
  }
}
