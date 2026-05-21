package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.croquet.*;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.*;

import javax.swing.undo.CannotUndoException;

import static org.junit.Assert.*;

public class DeclareNonGalleryFieldEditTest {

  private static final class TestableDeclareNonGalleryFieldEdit extends DeclareNonGalleryFieldEdit {
    private TestableDeclareNonGalleryFieldEdit(UserActivity userActivity, UserType<?> declaringType, UserField field) {
      super(userActivity, declaringType, field);
    }

    private void performDoOrRedo(boolean isDo) {
      this.doOrRedoInternal(isDo);
    }

    private void performUndo() {
      this.undoInternal();
    }

    private UserType<?> getDeclaringTypeForTest() {
      return this.getDeclaringType();
    }

    private UserField getFieldForTest() {
      return this.getField();
    }

    private String describeForTest() {
      StringBuilder sb = new StringBuilder();
      this.appendDescription(sb, DescriptionStyle.TERSE);
      return sb.toString();
    }
  }

  @Test
  public void construct_withNullUserActivity_succeeds() {
    TestableDeclareNonGalleryFieldEdit edit = new TestableDeclareNonGalleryFieldEdit(null, createType("Hero"), createField("power"));
    assertNotNull(edit);
  }

  @Test
  public void getDeclaringType_afterConstruction_returnsProvidedType() {
    NamedUserType type = createType("PropContainer");
    TestableDeclareNonGalleryFieldEdit edit = new TestableDeclareNonGalleryFieldEdit(null, type, createField("prop"));
    assertSame(type, edit.getDeclaringTypeForTest());
  }

  @Test
  public void getField_afterConstruction_returnsProvidedField() {
    UserField field = createField("health");
    TestableDeclareNonGalleryFieldEdit edit = new TestableDeclareNonGalleryFieldEdit(null, createType("Creature"), field);
    assertSame(field, edit.getFieldForTest());
  }

  @Test
  public void doOrRedoInternal_onEmptyType_addsField() {
    NamedUserType type = createType("Inventory");
    UserField field = createField("coins");
    TestableDeclareNonGalleryFieldEdit edit = new TestableDeclareNonGalleryFieldEdit(null, type, field);

    edit.performDoOrRedo(true);

    assertEquals(1, type.fields.size());
    assertSame(field, type.fields.get(0));
  }

  @Test
  public void doOrRedoInternal_withExistingFields_appendsField() {
    NamedUserType type = createType("Inventory");
    UserField first = createField("first");
    UserField second = createField("second");
    UserField added = createField("added");
    type.fields.add(first);
    type.fields.add(second);

    TestableDeclareNonGalleryFieldEdit edit = new TestableDeclareNonGalleryFieldEdit(null, type, added);
    edit.performDoOrRedo(true);

    assertEquals(3, type.fields.size());
    assertSame(first, type.fields.get(0));
    assertSame(second, type.fields.get(1));
    assertSame(added, type.fields.get(2));
  }

  @Test
  public void undoInternal_afterDo_removesField() {
    NamedUserType type = createType("UndoHolder");
    UserField field = createField("temporary");
    TestableDeclareNonGalleryFieldEdit edit = new TestableDeclareNonGalleryFieldEdit(null, type, field);

    edit.performDoOrRedo(true);
    assertTrue(type.fields.contains(field));

    edit.performUndo();

    assertFalse(type.fields.contains(field));
    assertEquals(0, type.fields.size());
  }

  @Test
  public void doUndoRedo_afterUndo_reinsertsFieldAtStoredIndex() {
    NamedUserType type = createType("RoundTripHolder");
    UserField before = createField("before");
    UserField inserted = createField("inserted");
    UserField tail = createField("tail");
    type.fields.add(before);

    TestableDeclareNonGalleryFieldEdit edit = new TestableDeclareNonGalleryFieldEdit(null, type, inserted);
    edit.performDoOrRedo(true);
    edit.performUndo();
    type.fields.add(tail);

    edit.performDoOrRedo(false);

    assertEquals(3, type.fields.size());
    assertSame(before, type.fields.get(0));
    assertSame(inserted, type.fields.get(1));
    assertSame(tail, type.fields.get(2));
  }

  @Test(expected = CannotUndoException.class)
  public void undoInternal_withoutMatchingField_throwsCannotUndoException() {
    NamedUserType type = createType("MissingFieldHolder");
    TestableDeclareNonGalleryFieldEdit edit = new TestableDeclareNonGalleryFieldEdit(null, type, createField("ghost"));
    edit.performUndo();
  }

  @Test
  public void appendDescription_withField_containsDeclarePrefixAndFieldName() {
    TestableDeclareNonGalleryFieldEdit edit = new TestableDeclareNonGalleryFieldEdit(null, createType("DescribeType"), createField("direction"));
    String description = edit.describeForTest();
    assertTrue(description.startsWith("declare:"));
    assertTrue(description.contains("direction"));
  }

  @Test
  public void multipleEdits_withDifferentTypes_modifyTheirOwnFieldLists() {
    NamedUserType firstType = createType("FirstType");
    NamedUserType secondType = createType("SecondType");
    UserField firstField = createField("alpha");
    UserField secondField = createField("beta");

    new TestableDeclareNonGalleryFieldEdit(null, firstType, firstField).performDoOrRedo(true);
    new TestableDeclareNonGalleryFieldEdit(null, secondType, secondField).performDoOrRedo(true);

    assertEquals(1, firstType.fields.size());
    assertSame(firstField, firstType.fields.get(0));
    assertEquals(1, secondType.fields.size());
    assertSame(secondField, secondType.fields.get(0));
  }

  private NamedUserType createType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(Object.class));
    return type;
  }

  private UserField createField(String name) {
    return new UserField(name, JavaType.getInstance(Integer.class), new IntegerLiteral(1));
  }
}
