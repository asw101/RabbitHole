package org.alice.ide.croquet.edits.ast;

import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import org.junit.Test;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class RevertFieldEditTest {
  private static Unsafe getUnsafe() throws Exception {
    Field field = Unsafe.class.getDeclaredField("theUnsafe");
    field.setAccessible(true);
    return (Unsafe) field.get(null);
  }

  private static RevertFieldEdit createEditWithoutConstructor(UserField userField) throws Exception {
    RevertFieldEdit edit = (RevertFieldEdit) getUnsafe().allocateInstance(RevertFieldEdit.class);
    Field fieldField = RevertFieldEdit.class.getDeclaredField("field");
    fieldField.setAccessible(true);
    fieldField.set(edit, userField);
    Field redoField = RevertFieldEdit.class.getDeclaredField("redoStateCode");
    redoField.setAccessible(true);
    redoField.set(edit, null);
    return edit;
  }

  private static UserField unmanagedField() {
    UserField field = new UserField("sample", JavaType.getInstance(Object.class), new NullLiteral());
    field.managementLevel.setValue(ManagementLevel.NONE);
    return field;
  }

  @Test
  public void classIsNotFinal() {
    assertFalse(Modifier.isFinal(RevertFieldEdit.class.getModifiers()));
  }

  @Test
  public void extendsAbstractEdit() throws Exception {
    RevertFieldEdit edit = createEditWithoutConstructor(unmanagedField());
    assertTrue(edit instanceof AbstractEdit);
  }

  @Test
  public void directSuperclassIsAbstractEdit() {
    assertSame(AbstractEdit.class, RevertFieldEdit.class.getSuperclass());
  }

  @Test
  public void userActivityConstructor_exists() throws Exception {
    Constructor<RevertFieldEdit> constructor = RevertFieldEdit.class.getConstructor(
        org.lgna.croquet.history.UserActivity.class,
        UserField.class);
    assertNotNull(constructor);
  }

  @Test
  public void binaryDecoderConstructor_exists() throws Exception {
    Constructor<RevertFieldEdit> constructor = RevertFieldEdit.class.getConstructor(
        edu.cmu.cs.dennisc.codec.BinaryDecoder.class,
        Object.class);
    assertNotNull(constructor);
  }

  @Test
  public void unsafeAllocatedInstance_isNotNull() throws Exception {
    assertNotNull(createEditWithoutConstructor(unmanagedField()));
  }

  @Test
  public void getField_returnsInjectedField() throws Exception {
    UserField field = unmanagedField();
    RevertFieldEdit edit = createEditWithoutConstructor(field);
    assertSame(field, edit.getField());
  }

  @Test
  public void injectedField_remainsUnmanaged() throws Exception {
    RevertFieldEdit edit = createEditWithoutConstructor(unmanagedField());
    assertEquals(ManagementLevel.NONE, edit.getField().managementLevel.getValue());
  }

  @Test
  public void encodeMethodExists() throws Exception {
    Method method = RevertFieldEdit.class.getMethod("encode", BinaryEncoder.class);
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void doOrRedoInternal_withUnmanagedField_doesNotThrow() throws Exception {
    RevertFieldEdit edit = createEditWithoutConstructor(unmanagedField());
    edit.doOrRedoInternal(true);
  }

  @Test
  public void undoInternal_withUnmanagedField_doesNotThrow() throws Exception {
    RevertFieldEdit edit = createEditWithoutConstructor(unmanagedField());
    edit.undoInternal();
  }

  @Test
  public void getTerseDescription_containsDeclarePrefix() throws Exception {
    RevertFieldEdit edit = createEditWithoutConstructor(unmanagedField());
    assertTrue(edit.getTerseDescription().startsWith("declare:"));
  }

  @Test
  public void getDetailedDescription_containsClassName() throws Exception {
    RevertFieldEdit edit = createEditWithoutConstructor(unmanagedField());
    assertTrue(edit.getDetailedDescription().contains(RevertFieldEdit.class.getName()));
  }

  @Test
  public void getRedoPresentation_containsRedoPrefix() throws Exception {
    RevertFieldEdit edit = createEditWithoutConstructor(unmanagedField());
    assertTrue(edit.getRedoPresentation().startsWith("Redo:"));
  }

  @Test
  public void getUndoPresentation_containsUndoPrefix() throws Exception {
    RevertFieldEdit edit = createEditWithoutConstructor(unmanagedField());
    assertTrue(edit.getUndoPresentation().startsWith("Undo:"));
  }

  @Test
  public void fieldField_isDeclaredFinal() throws Exception {
    Field field = RevertFieldEdit.class.getDeclaredField("field");
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void redoStateCodeField_exists() throws Exception {
    Field field = RevertFieldEdit.class.getDeclaredField("redoStateCode");
    assertNotNull(field);
  }

  @Test
  public void protectedGetterMethod_exists() throws Exception {
    Method method = RevertFieldEdit.class.getDeclaredMethod("getField");
    assertEquals(UserField.class, method.getReturnType());
  }
}
