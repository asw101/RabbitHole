package org.alice.ide.croquet.edits.ast.rename;

import org.junit.Test;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.*;

public class RenameDeclarationEditComprehensiveTest {
  private static UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(Object.class));
    return field;
  }

  @Test public void construct_notNull() {
    assertNotNull(new RenameDeclarationEdit(null, createField("before"), "before", "after"));
  }

  @Test public void isAbstractEdit() {
    assertTrue(new RenameDeclarationEdit(null, createField("before"), "before", "after") instanceof AbstractEdit);
  }

  @Test public void doOrRedoInternal_updatesDeclarationName() {
    UserField field = createField("before");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, field, "before", "after");
    edit.doOrRedoInternal(true);
    assertEquals("after", field.getName());
  }

  @Test public void redoInternal_updatesDeclarationName() {
    UserField field = createField("before");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, field, "before", "after");
    edit.doOrRedoInternal(false);
    assertEquals("after", field.getName());
  }

  @Test public void undoInternal_restoresPreviousName() {
    UserField field = createField("before");
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, field, "before", "after");
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    assertEquals("before", field.getName());
  }

  @Test public void declarationNameProperty_canBeSetDirectly() {
    UserField field = createField("before");
    field.getNamePropertyIfItExists().setValue("changed");
    assertEquals("changed", field.getName());
  }
}
