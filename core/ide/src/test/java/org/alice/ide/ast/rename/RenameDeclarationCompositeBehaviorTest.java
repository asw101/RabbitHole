package org.alice.ide.ast.rename;

import org.alice.ide.croquet.edits.ast.rename.RenameDeclarationEdit;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class RenameDeclarationCompositeBehaviorTest {

  @Test
  public void methodRenameUndoUsesTheCurrentDeclarationName() throws Exception {
    NamedUserType owner = createOwnerType();
    UserMethod method = new UserMethod();
    method.name.setValue("originalName");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.body.setValue(new BlockStatement());
    owner.methods.add(method);

    RenameMethodComposite composite = RenameMethodComposite.getInstance(method);
    method.name.setValue("middleName");

    RenameDeclarationEdit edit = createEdit(composite, "finalName");
    doOrRedo(edit, true);
    assertEquals("finalName", method.getName());

    undo(edit);
    assertEquals("middleName", method.getName());
  }

  @Test
  public void cachedFieldCompositeSupportsSuccessiveRenameRounds() throws Exception {
    NamedUserType owner = createOwnerType();
    UserField field = new UserField("score", JavaType.STRING_TYPE, new NullLiteral());
    owner.fields.add(field);

    RenameFieldComposite composite = RenameFieldComposite.getInstance(field);
    RenameDeclarationEdit firstEdit = createEdit(composite, "points");
    doOrRedo(firstEdit, true);
    assertEquals("points", field.getName());

    RenameFieldComposite cachedComposite = RenameFieldComposite.getInstance(field);
    assertSame(composite, cachedComposite);

    RenameDeclarationEdit secondEdit = createEdit(cachedComposite, "totalPoints");
    doOrRedo(secondEdit, true);
    assertEquals("totalPoints", field.getName());

    undo(secondEdit);
    assertEquals("points", field.getName());

    undo(firstEdit);
    assertEquals("score", field.getName());
  }

  private static NamedUserType createOwnerType() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("Scene");
    type.superType.setValue(JavaType.OBJECT_TYPE);
    return type;
  }

  private static RenameDeclarationEdit createEdit(RenameDeclarationComposite<?> composite, String nextName) throws Exception {
    composite.getNameState().setValueTransactionlessly(nextName);
    Method method = RenameDeclarationComposite.class.getDeclaredMethod("createEdit", org.lgna.croquet.history.UserActivity.class);
    method.setAccessible(true);
    return (RenameDeclarationEdit) method.invoke(composite, new Object[] { null });
  }

  private static void doOrRedo(RenameDeclarationEdit edit, boolean isDo) throws Exception {
    Method method = RenameDeclarationEdit.class.getDeclaredMethod("doOrRedoInternal", boolean.class);
    method.setAccessible(true);
    method.invoke(edit, isDo);
  }

  private static void undo(RenameDeclarationEdit edit) throws Exception {
    Method method = RenameDeclarationEdit.class.getDeclaredMethod("undoInternal");
    method.setAccessible(true);
    method.invoke(edit);
  }
}
