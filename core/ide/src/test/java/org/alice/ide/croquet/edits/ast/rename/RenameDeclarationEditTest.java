package org.alice.ide.croquet.edits.ast.rename;

import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link RenameDeclarationEdit} — edit that renames an AbstractDeclaration.
 * Covers construction, doOrRedo/undo round-trip, and the name property contract.
 */
public class RenameDeclarationEditTest {

  private UserMethod method;

  @Before
  public void setUp() {
    method = new UserMethod();
    method.name.setValue("oldName");
    method.returnType.setValue(JavaType.VOID_TYPE);
  }

  // ---- construction ----

  @Test
  public void construct_withNullUserActivity_succeeds() {
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "oldName", "newName");
    assertNotNull(edit);
  }

  @Test
  public void construct_withSameOldAndNewName_succeeds() {
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "oldName", "oldName");
    assertNotNull(edit);
  }

  @Test
  public void construct_withEmptyNames_succeeds() {
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "", "");
    assertNotNull(edit);
  }

  // ---- doOrRedoInternal ----

  @Test
  public void doOrRedoInternal_setsNextName() {
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "oldName", "newName");
    edit.doOrRedoInternal(true);
    assertEquals("newName", method.getName());
  }

  @Test
  public void doOrRedoInternal_isDo_false_setsNextName() {
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "oldName", "newName");
    edit.doOrRedoInternal(false);
    assertEquals("newName", method.getName());
  }

  // ---- undoInternal ----

  @Test
  public void undoInternal_restoresPrevName() {
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "oldName", "newName");
    edit.doOrRedoInternal(true);
    assertEquals("newName", method.getName());

    edit.undoInternal();
    assertEquals("oldName", method.getName());
  }

  // ---- round trip ----

  @Test
  public void doAndUndo_roundTrip() {
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "oldName", "newName");
    assertEquals("oldName", method.getName());

    edit.doOrRedoInternal(true);
    assertEquals("newName", method.getName());

    edit.undoInternal();
    assertEquals("oldName", method.getName());
  }

  @Test
  public void doUndoRedo_cycle() {
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "oldName", "newName");

    edit.doOrRedoInternal(true);
    assertEquals("newName", method.getName());

    edit.undoInternal();
    assertEquals("oldName", method.getName());

    edit.doOrRedoInternal(false);
    assertEquals("newName", method.getName());
  }

  @Test
  public void multipleUndoRedo_cycles() {
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "oldName", "newName");

    for (int i = 0; i < 5; i++) {
      edit.doOrRedoInternal(i == 0);
      assertEquals("Cycle " + i + " do", "newName", method.getName());

      edit.undoInternal();
      assertEquals("Cycle " + i + " undo", "oldName", method.getName());
    }
  }

  // ---- rename on different declaration types ----

  @Test
  public void rename_userField() {
    UserField field = new UserField();
    field.name.setValue("oldField");
    field.valueType.setValue(JavaType.getInstance(Object.class));

    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, field, "oldField", "newField");
    edit.doOrRedoInternal(true);
    assertEquals("newField", field.getName());

    edit.undoInternal();
    assertEquals("oldField", field.getName());
  }

  @Test
  public void rename_userParameter() {
    UserParameter param = new UserParameter();
    param.name.setValue("oldParam");
    param.valueType.setValue(JavaType.getInstance(String.class));

    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, param, "oldParam", "newParam");
    edit.doOrRedoInternal(true);
    assertEquals("newParam", param.getName());

    edit.undoInternal();
    assertEquals("oldParam", param.getName());
  }

  @Test
  public void rename_userLocal() {
    UserLocal local = new UserLocal();
    local.name.setValue("oldLocal");
    local.valueType.setValue(JavaType.getInstance(int.class));

    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, local, "oldLocal", "newLocal");
    edit.doOrRedoInternal(true);
    assertEquals("newLocal", local.getName());

    edit.undoInternal();
    assertEquals("oldLocal", local.getName());
  }

  // ---- sameOldAndNew ----

  @Test
  public void rename_sameValue_noChange() {
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "oldName", "oldName");
    edit.doOrRedoInternal(true);
    assertEquals("oldName", method.getName());
    edit.undoInternal();
    assertEquals("oldName", method.getName());
  }

  // ---- special characters in name ----

  @Test
  public void rename_withSpaces() {
    method.name.setValue("old name");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "old name", "new name");
    edit.doOrRedoInternal(true);
    assertEquals("new name", method.getName());
  }

  @Test
  public void rename_withUnicode() {
    method.name.setValue("méthode");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "méthode", "方法");
    edit.doOrRedoInternal(true);
    assertEquals("方法", method.getName());

    edit.undoInternal();
    assertEquals("méthode", method.getName());
  }
}
