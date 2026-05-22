package org.alice.ide.croquet.edits.ast.rename;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link RenameDeclarationEdit} — edit that renames any AbstractDeclaration.
 * Supports full doOrRedo/undo round-trip headless since it only sets name property.
 */
public class RenameDeclarationEditTest {

  // ---- construction ----

  @Test
  public void construct_withNullUserActivity_succeeds() {
    UserMethod method = new UserMethod();
    method.name.setValue("oldName");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "oldName", "newName");
    assertNotNull(edit);
  }

  @Test
  public void construct_withField_succeeds() {
    UserField field = new UserField();
    field.name.setValue("oldField");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, field, "oldField", "newField");
    assertNotNull(edit);
  }

  // ---- doOrRedoInternal ----

  @Test
  public void doOrRedo_appliesNewName() {
    UserMethod method = new UserMethod();
    method.name.setValue("oldName");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "oldName", "newName");
    edit.doOrRedoInternal(true);
    assertEquals("newName", method.getName());
  }

  @Test
  public void doOrRedo_isDo_false_appliesNewName() {
    UserMethod method = new UserMethod();
    method.name.setValue("oldName");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "oldName", "newName");
    edit.doOrRedoInternal(false);
    assertEquals("newName", method.getName());
  }

  // ---- undoInternal ----

  @Test
  public void undo_restoresOldName() {
    UserMethod method = new UserMethod();
    method.name.setValue("oldName");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "oldName", "newName");
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    assertEquals("oldName", method.getName());
  }

  // ---- round trip ----

  @Test
  public void doAndUndo_roundTrip_restoresOriginal() {
    UserMethod method = new UserMethod();
    method.name.setValue("originalName");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "originalName", "renamedName");

    edit.doOrRedoInternal(true);
    assertEquals("renamedName", method.getName());

    edit.undoInternal();
    assertEquals("originalName", method.getName());
  }

  @Test
  public void multipleUndoRedo_cycles() {
    UserMethod method = new UserMethod();
    method.name.setValue("start");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "start", "end");

    for (int i = 0; i < 5; i++) {
      edit.doOrRedoInternal(i == 0);
      assertEquals("After do/redo " + i, "end", method.getName());

      edit.undoInternal();
      assertEquals("After undo " + i, "start", method.getName());
    }
  }

  // ---- field rename ----

  @Test
  public void renameField_doAndUndo() {
    UserField field = new UserField();
    field.name.setValue("x");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, field, "x", "y");

    edit.doOrRedoInternal(true);
    assertEquals("y", field.getName());

    edit.undoInternal();
    assertEquals("x", field.getName());
  }

  // ---- same name ----

  @Test
  public void rename_sameOldAndNew_noChange() {
    UserMethod method = new UserMethod();
    method.name.setValue("sameName");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "sameName", "sameName");
    edit.doOrRedoInternal(true);
    assertEquals("sameName", method.getName());
  }

  // ---- empty names ----

  @Test
  public void rename_toEmptyString() {
    UserMethod method = new UserMethod();
    method.name.setValue("hasName");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "hasName", "");
    edit.doOrRedoInternal(true);
    assertEquals("", method.getName());
  }

  @Test
  public void rename_fromEmptyString() {
    UserMethod method = new UserMethod();
    method.name.setValue("");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, method, "", "newName");
    edit.doOrRedoInternal(true);
    assertEquals("newName", method.getName());
  }

  // ---- parameter ----

  @Test
  public void renameParameter() {
    UserParameter param = new UserParameter();
    param.name.setValue("oldParam");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, param, "oldParam", "newParam");
    edit.doOrRedoInternal(true);
    assertEquals("newParam", param.getName());
  }

  @Test
  public void renameParameter_undoRestores() {
    UserParameter param = new UserParameter();
    param.name.setValue("param1");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, param, "param1", "param2");
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    assertEquals("param1", param.getName());
  }
}
