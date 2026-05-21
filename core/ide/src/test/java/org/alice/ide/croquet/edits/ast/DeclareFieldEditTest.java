package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.croquet.*;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class DeclareFieldEditTest {

  private static final class TestDeclareFieldEdit extends DeclareFieldEdit {
    private boolean didDoOrRedo;
    private boolean didUndo;
    private boolean lastIsDo;

    private TestDeclareFieldEdit(UserActivity userActivity, UserType<?> declaringType, UserField field) {
      super(userActivity, declaringType, field);
    }

    @Override
    protected void doOrRedoInternal(boolean isDo) {
      this.didDoOrRedo = true;
      this.lastIsDo = isDo;
    }

    @Override
    protected void undoInternal() {
      this.didUndo = true;
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
    TestDeclareFieldEdit edit = new TestDeclareFieldEdit(null, createType("Hero"), createField("score"));
    assertNotNull(edit);
  }

  @Test
  public void getDeclaringType_withNamedUserType_returnsConstructionValue() {
    NamedUserType type = createType("CameraHolder");
    TestDeclareFieldEdit edit = new TestDeclareFieldEdit(null, type, createField("camera"));
    assertSame(type, edit.getDeclaringTypeForTest());
  }

  @Test
  public void getField_withUserField_returnsConstructionValue() {
    UserField field = createField("count");
    TestDeclareFieldEdit edit = new TestDeclareFieldEdit(null, createType("Counter"), field);
    assertSame(field, edit.getFieldForTest());
  }

  @Test
  public void construct_withNullDeclaringType_preservesNull() {
    TestDeclareFieldEdit edit = new TestDeclareFieldEdit(null, null, createField("value"));
    assertNull(edit.getDeclaringTypeForTest());
  }

  @Test
  public void construct_withNullField_preservesNull() {
    TestDeclareFieldEdit edit = new TestDeclareFieldEdit(null, createType("NullFieldType"), null);
    assertNull(edit.getFieldForTest());
  }

  @Test
  public void doOrRedoInternal_withIsDoTrue_recordsTrue() {
    TestDeclareFieldEdit edit = new TestDeclareFieldEdit(null, createType("DoType"), createField("value"));
    edit.doOrRedoInternal(true);
    assertTrue(edit.didDoOrRedo);
    assertTrue(edit.lastIsDo);
  }

  @Test
  public void doOrRedoInternal_withIsDoFalse_recordsFalse() {
    TestDeclareFieldEdit edit = new TestDeclareFieldEdit(null, createType("RedoType"), createField("value"));
    edit.doOrRedoInternal(false);
    assertTrue(edit.didDoOrRedo);
    assertFalse(edit.lastIsDo);
  }

  @Test
  public void undoInternal_afterConstruction_setsUndoFlag() {
    TestDeclareFieldEdit edit = new TestDeclareFieldEdit(null, createType("UndoType"), createField("value"));
    edit.undoInternal();
    assertTrue(edit.didUndo);
  }

  @Test
  public void appendDescription_withField_containsDeclarePrefixAndFieldName() {
    TestDeclareFieldEdit edit = new TestDeclareFieldEdit(null, createType("DescribeType"), createField("lives"));
    String description = edit.describeForTest();
    assertTrue(description.startsWith("declare:"));
    assertTrue(description.contains("lives"));
  }

  @Test
  public void extendsAbstractEdit_withConcreteSubclass_returnsTrue() {
    TestDeclareFieldEdit edit = new TestDeclareFieldEdit(null, createType("AbstractTypeHolder"), createField("amount"));
    assertTrue(edit instanceof AbstractEdit);
  }

  @Test
  public void multipleInstances_withDifferentConstructionValues_remainIndependent() {
    NamedUserType alpha = createType("Alpha");
    NamedUserType beta = createType("Beta");
    UserField firstField = createField("first");
    UserField secondField = createField("second");

    TestDeclareFieldEdit first = new TestDeclareFieldEdit(null, alpha, firstField);
    TestDeclareFieldEdit second = new TestDeclareFieldEdit(null, beta, secondField);

    assertSame(alpha, first.getDeclaringTypeForTest());
    assertSame(firstField, first.getFieldForTest());
    assertSame(beta, second.getDeclaringTypeForTest());
    assertSame(secondField, second.getFieldForTest());
    assertNotSame(first.getFieldForTest(), second.getFieldForTest());
  }

  private NamedUserType createType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(Object.class));
    return type;
  }

  private UserField createField(String name) {
    return new UserField(name, JavaType.getInstance(String.class), new StringLiteral(name + "Value"));
  }
}
