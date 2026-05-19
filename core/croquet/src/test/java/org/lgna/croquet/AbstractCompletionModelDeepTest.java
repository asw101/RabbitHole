package org.lgna.croquet;

import org.junit.Test;
import org.lgna.croquet.imp.operation.OperationImp;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link AbstractCompletionModel} — reflection coverage of
 * inner SidekickLabel, methods, and hierarchy. Cannot instantiate directly.
 */
public class AbstractCompletionModelDeepTest {

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractCompletionModel.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractModel() {
    assertTrue(AbstractModel.class.isAssignableFrom(AbstractCompletionModel.class));
  }

  @Test
  public void class_implementsCompletionModel() {
    assertTrue(CompletionModel.class.isAssignableFrom(AbstractCompletionModel.class));
  }

  // ── Inner class SidekickLabel ─────────────────────────────────────

  @Test
  public void innerClass_SidekickLabel_exists() {
    Class<?>[] inners = AbstractCompletionModel.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> inner : inners) {
      if (inner.getSimpleName().equals("SidekickLabel")) {
        found = true;
        break;
      }
    }
    assertTrue("Expected inner class SidekickLabel", found);
  }

  @Test
  public void innerClass_SidekickLabel_extendsPlainStringValue() {
    Class<?>[] inners = AbstractCompletionModel.class.getDeclaredClasses();
    for (Class<?> inner : inners) {
      if (inner.getSimpleName().equals("SidekickLabel")) {
        assertTrue(PlainStringValue.class.isAssignableFrom(inner));
      }
    }
  }

  // ── Method existence ──────────────────────────────────────────────

  @Test
  public void method_getSidekickLabel_exists() throws Exception {
    Method m = AbstractCompletionModel.class.getMethod("getSidekickLabel");
    assertNotNull(m);
  }

  @Test
  public void method_hasSidekickLabel_exists() throws Exception {
    Method m = AbstractCompletionModel.class.getMethod("hasSidekickLabel");
    assertNotNull(m);
  }

  @Test
  public void method_isEnabled_inherited() throws Exception {
    Method m = AbstractCompletionModel.class.getMethod("isEnabled");
    assertNotNull(m);
  }

  @Test
  public void method_setEnabled_inherited() throws Exception {
    Method m = AbstractCompletionModel.class.getMethod("setEnabled", boolean.class);
    assertNotNull(m);
  }

  @Test
  public void method_relocalize_inherited() throws Exception {
    Method m = AbstractCompletionModel.class.getMethod("relocalize");
    assertNotNull(m);
  }

  @Test
  public void method_initializeIfNecessary_inherited() throws Exception {
    Method m = AbstractCompletionModel.class.getMethod("initializeIfNecessary");
    assertNotNull(m);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructor_takesGroupAndUUID() throws Exception {
    var ctor = AbstractCompletionModel.class.getDeclaredConstructor(
        Group.class, java.util.UUID.class);
    assertNotNull(ctor);
    // Constructor is package-private
    assertFalse(java.lang.reflect.Modifier.isPublic(ctor.getModifiers()));
  }
}
