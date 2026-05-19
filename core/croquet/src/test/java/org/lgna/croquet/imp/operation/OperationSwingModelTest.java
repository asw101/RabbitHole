package org.lgna.croquet.imp.operation;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.Operation;

import javax.swing.Action;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class OperationSwingModelTest {

  // ── Class structure ────────────────────────────────────────────────

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(OperationSwingModel.class.getModifiers()));
  }

  @Test
  public void class_isNotAbstract() {
    assertFalse(Modifier.isAbstract(OperationSwingModel.class.getModifiers()));
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(OperationSwingModel.class.getModifiers()));
  }

  @Test
  public void class_directlyExtendsObject() {
    assertEquals(Object.class, OperationSwingModel.class.getSuperclass());
  }

  // ── Constructor ────────────────────────────────────────────────────

  @Test
  public void constructor_takesOperation() throws Exception {
    Constructor<OperationSwingModel> ctor =
        OperationSwingModel.class.getDeclaredConstructor(Operation.class);
    assertNotNull(ctor);
  }

  @Test
  public void constructor_isPackagePrivate() throws Exception {
    Constructor<OperationSwingModel> ctor =
        OperationSwingModel.class.getDeclaredConstructor(Operation.class);
    assertFalse(Modifier.isPublic(ctor.getModifiers()));
    assertFalse(Modifier.isProtected(ctor.getModifiers()));
    assertFalse(Modifier.isPrivate(ctor.getModifiers()));
  }

  // ── Fields ─────────────────────────────────────────────────────────

  @Test
  public void operation_field_exists() throws Exception {
    Field f = OperationSwingModel.class.getDeclaredField("operation");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals(Operation.class, f.getType());
  }

  @Test
  public void action_field_exists() throws Exception {
    Field f = OperationSwingModel.class.getDeclaredField("action");
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals(Action.class, f.getType());
  }

  @Test
  public void fieldCount_isTwo() {
    Field[] fields = OperationSwingModel.class.getDeclaredFields();
    assertEquals(2, fields.length);
  }

  // ── Public methods ─────────────────────────────────────────────────

  @Test
  public void getAction_method_exists() throws Exception {
    Method m = OperationSwingModel.class.getDeclaredMethod("getAction");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(Action.class, m.getReturnType());
  }

  @Test
  public void declaredMethods_count() {
    Method[] methods = OperationSwingModel.class.getDeclaredMethods();
    int count = 0;
    for (Method m : methods) {
      if (!m.isSynthetic() && !m.isBridge()) {
        count++;
      }
    }
    assertEquals("OperationSwingModel should have 1 declared method (getAction)", 1, count);
  }
}
