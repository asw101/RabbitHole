package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Reflection-only coverage tests for {@link CustomItemState}. Cannot instantiate
 * headlessly because it is abstract and has cascade infrastructure dependencies.
 */
public class CustomItemStateCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_extendsItemState() {
    assertTrue(ItemState.class.isAssignableFrom(CustomItemState.class));
  }

  @Test
  public void class_extendsState() {
    assertTrue(State.class.isAssignableFrom(CustomItemState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(CustomItemState.class.getModifiers()));
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructor_exists_groupUuidValueCodec() throws Exception {
    Constructor<?> ctor = CustomItemState.class.getDeclaredConstructor(
        Group.class, java.util.UUID.class, Object.class, ItemCodec.class);
    assertNotNull(ctor);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  // ── InternalRoot inner class ──────────────────────────────────────

  @Test
  public void innerClass_exists() {
    Class<?>[] inners = CustomItemState.class.getDeclaredClasses();
    assertTrue("Expected at least one inner class", inners.length > 0);
  }

  // ── Method presence ───────────────────────────────────────────────

  @Test
  public void method_getValue_inherited() throws Exception {
    assertNotNull(CustomItemState.class.getMethod("getValue"));
  }

  @Test
  public void method_getItemCodec_inherited() throws Exception {
    assertNotNull(CustomItemState.class.getMethod("getItemCodec"));
  }

  @Test
  public void method_appendRepresentation_inherited() throws Exception {
    assertNotNull(CustomItemState.class.getMethod("appendRepresentation", StringBuilder.class, Object.class));
  }
}
