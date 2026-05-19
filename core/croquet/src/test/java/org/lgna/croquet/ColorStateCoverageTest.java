package org.lgna.croquet;

import org.junit.Test;
import org.lgna.croquet.color.ColorState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Reflection-only coverage tests for {@link ColorState}. Cannot instantiate
 * headlessly because the constructor creates a ColorChooserDialogCoreComposite
 * that accesses Application.INHERIT_GROUP.
 */
public class ColorStateCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_extendsItemState() {
    assertTrue(ItemState.class.isAssignableFrom(ColorState.class));
  }

  @Test
  public void class_extendsState() {
    assertTrue(State.class.isAssignableFrom(ColorState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(ColorState.class.getModifiers()));
  }

  // ── Constructor presence ──────────────────────────────────────────

  @Test
  public void constructor_exists_groupUuidColor() throws Exception {
    Constructor<?> ctor = ColorState.class.getDeclaredConstructor(
        Group.class, java.util.UUID.class, java.awt.Color.class);
    assertNotNull(ctor);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_getValue_exists() throws Exception {
    Method m = ColorState.class.getMethod("getValue");
    assertNotNull(m);
  }

  @Test
  public void method_getItemCodec_exists() throws Exception {
    Method m = ColorState.class.getMethod("getItemCodec");
    assertNotNull(m);
  }

  @Test
  public void method_appendRepresentation_exists() throws Exception {
    Method m = ColorState.class.getMethod("appendRepresentation", StringBuilder.class, Object.class);
    assertNotNull(m);
  }

  // ── Inner class SwingModel ────────────────────────────────────────

  @Test
  public void innerClass_SwingModel_exists() {
    Class<?>[] inners = ColorState.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> inner : inners) {
      if (inner.getSimpleName().equals("SwingModel")) {
        found = true;
        break;
      }
    }
    assertTrue("Expected inner class SwingModel", found);
  }
}
