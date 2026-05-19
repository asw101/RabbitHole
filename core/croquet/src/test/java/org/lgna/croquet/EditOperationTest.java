package org.lgna.croquet;

import org.junit.Test;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.history.UserActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.Assert.*;

public class EditOperationTest {

  // ── Class hierarchy ────────────────────────────────────────────────

  @Test
  public void class_extendsActionOperation() {
    assertEquals(ActionOperation.class, EditOperation.class.getSuperclass());
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(EditOperation.class.getModifiers()));
  }

  @Test
  public void class_isNotAbstract() {
    assertFalse(Modifier.isAbstract(EditOperation.class.getModifiers()));
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(EditOperation.class.getModifiers()));
  }

  // ── Constructors ───────────────────────────────────────────────────

  @Test
  public void constructor_editOnly_exists() throws Exception {
    Constructor<EditOperation> ctor = EditOperation.class.getConstructor(Edit.class);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
    assertEquals(1, ctor.getParameterCount());
  }

  @Test
  public void constructor_groupAndEdit_exists() throws Exception {
    Constructor<EditOperation> ctor = EditOperation.class.getConstructor(Group.class, Edit.class);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
    assertEquals(2, ctor.getParameterCount());
  }

  @Test
  public void constructor_count_isExactlyTwo() {
    Constructor<?>[] ctors = EditOperation.class.getDeclaredConstructors();
    assertEquals(2, ctors.length);
  }

  // ── Fields ─────────────────────────────────────────────────────────

  @Test
  public void edit_field_exists() throws Exception {
    Field editField = EditOperation.class.getDeclaredField("edit");
    assertTrue(Modifier.isPrivate(editField.getModifiers()));
    assertTrue(Modifier.isFinal(editField.getModifiers()));
    assertEquals(Edit.class, editField.getType());
  }

  // ── perform method ─────────────────────────────────────────────────

  @Test
  public void perform_method_exists() throws Exception {
    Method perform = EditOperation.class.getDeclaredMethod("perform", UserActivity.class);
    assertTrue(Modifier.isProtected(perform.getModifiers()));
    assertFalse(Modifier.isAbstract(perform.getModifiers()));
    assertEquals(void.class, perform.getReturnType());
  }

  @Test
  public void perform_overridesActionOperationPerform() throws Exception {
    Method childPerform = EditOperation.class.getDeclaredMethod("perform", UserActivity.class);
    Method parentPerform = ActionOperation.class.getDeclaredMethod("perform", UserActivity.class);
    assertEquals(childPerform.getName(), parentPerform.getName());
    assertArrayEquals(childPerform.getParameterTypes(), parentPerform.getParameterTypes());
  }

  // ── Interface conformance ──────────────────────────────────────────

  @Test
  public void implements_CompletionModel_viaInheritance() {
    assertTrue(CompletionModel.class.isAssignableFrom(EditOperation.class));
  }

  @Test
  public void implements_Triggerable_viaInheritance() {
    assertTrue(Triggerable.class.isAssignableFrom(EditOperation.class));
  }

  @Test
  public void implements_Element_viaInheritance() {
    assertTrue(Element.class.isAssignableFrom(EditOperation.class));
  }

  // ── Method signatures from parent chain ────────────────────────────

  @Test
  public void performInActivity_inherited_fromOperation() throws Exception {
    Method performInActivity = ActionOperation.class.getDeclaredMethod("performInActivity",
        UserActivity.class);
    assertNotNull(performInActivity);
  }

  @Test
  public void getImp_available_fromOperation() throws Exception {
    Method getImp = Operation.class.getDeclaredMethod("getImp");
    assertTrue(Modifier.isPublic(getImp.getModifiers()));
  }

  // ── Declared method count ──────────────────────────────────────────

  @Test
  public void declaredMethods_count() {
    Method[] methods = EditOperation.class.getDeclaredMethods();
    int nonSynthetic = 0;
    for (Method m : methods) {
      if (!m.isSynthetic() && !m.isBridge()) {
        nonSynthetic++;
      }
    }
    assertEquals("EditOperation should declare exactly 1 method (perform)", 1, nonSynthetic);
  }
}
