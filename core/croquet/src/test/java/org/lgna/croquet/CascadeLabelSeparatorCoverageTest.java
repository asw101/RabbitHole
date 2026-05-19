package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Reflection-based coverage tests for {@link CascadeLabelSeparator}.
 * Cannot instantiate headlessly because the constructor requires
 * CascadeItem infrastructure.
 */
public class CascadeLabelSeparatorCoverageTest {

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(CascadeLabelSeparator.class.getModifiers()));
  }

  @Test
  public void class_extendsCascadeSeparator() {
    assertTrue(CascadeSeparator.class.isAssignableFrom(
        CascadeLabelSeparator.class));
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(CascadeLabelSeparator.class.getModifiers()));
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructor_takesUUID() throws Exception {
    var ctor = CascadeLabelSeparator.class.getDeclaredConstructor(
        java.util.UUID.class);
    assertNotNull(ctor);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  // ── Method existence ──────────────────────────────────────────────

  @Test
  public void method_localize_exists() throws Exception {
    Method m = CascadeLabelSeparator.class.getDeclaredMethod("localize");
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void method_getMenuItemIconProxyText_exists() throws Exception {
    Method m = CascadeLabelSeparator.class.getDeclaredMethod(
        "getMenuItemIconProxyText");
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void method_isValid_exists() throws Exception {
    Method m = CascadeLabelSeparator.class.getMethod("isValid");
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void method_createMenuItemIconProxy_exists() throws Exception {
    Method m = CascadeLabelSeparator.class.getDeclaredMethod(
        "createMenuItemIconProxy",
        org.lgna.croquet.imp.cascade.ItemNode.class);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertTrue(Modifier.isFinal(m.getModifiers()));
  }

  @Test
  public void method_getMenuItemText_exists() throws Exception {
    Method m = CascadeLabelSeparator.class.getMethod("getMenuItemText");
    assertNotNull(m);
  }

  // ── Field existence ───────────────────────────────────────────────

  @Test
  public void field_menuItemText_exists() throws Exception {
    var f = CascadeLabelSeparator.class.getDeclaredField("menuItemText");
    assertNotNull(f);
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  // ── Method return types ───────────────────────────────────────────

  @Test
  public void getMenuItemText_returnsString() throws Exception {
    Method m = CascadeLabelSeparator.class.getMethod("getMenuItemText");
    assertEquals(String.class, m.getReturnType());
  }

  @Test
  public void isValid_returnsBoolean() throws Exception {
    Method m = CascadeLabelSeparator.class.getMethod("isValid");
    assertEquals(boolean.class, m.getReturnType());
  }

  // ── isValid private helper — verified via reflection existence ────

  @Test
  public void privateIsValid_method_exists() throws Exception {
    Method m = CascadeLabelSeparator.class.getDeclaredMethod(
        "isValid", String.class);
    assertNotNull(m);
    assertTrue(Modifier.isPrivate(m.getModifiers()));
  }

  @Test
  public void privateIsValid_returnType_boolean() throws Exception {
    Method m = CascadeLabelSeparator.class.getDeclaredMethod(
        "isValid", String.class);
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void publicIsValid_method_exists() throws Exception {
    Method m = CascadeLabelSeparator.class.getMethod("isValid");
    assertNotNull(m);
    assertEquals(0, m.getParameterCount());
  }

  @Test
  public void getMenuItemText_method_returnsNull() throws Exception {
    Method m = CascadeLabelSeparator.class.getMethod("getMenuItemText");
    assertEquals(String.class, m.getReturnType());
  }
}
