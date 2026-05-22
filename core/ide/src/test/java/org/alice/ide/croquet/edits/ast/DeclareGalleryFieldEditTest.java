package org.alice.ide.croquet.edits.ast;

import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.UserField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class DeclareGalleryFieldEditTest {
  private NamedUserType declaringType;
  private UserField field;
  private Statement[] doStatements;
  private Statement[] undoStatements;

  @Before
  public void setUp() {
    declaringType = new NamedUserType();
    declaringType.name.setValue("ExampleType");
    field = new UserField("galleryField", JavaType.getInstance(Object.class), new NullLiteral());
    doStatements = new Statement[]{new ExpressionStatement(new IntegerLiteral(1))};
    undoStatements = new Statement[]{new ExpressionStatement(new IntegerLiteral(2))};
  }

  @Test
  public void construct_withNullUserActivityAndSceneEditor_succeeds() {
    DeclareGalleryFieldEdit edit = new DeclareGalleryFieldEdit(null, null, declaringType, field, doStatements, undoStatements);
    assertNotNull(edit);
  }

  @Test
  public void getDeclaringType_returnsConstructionValue() {
    DeclareGalleryFieldEdit edit = new DeclareGalleryFieldEdit(null, null, declaringType, field, doStatements, undoStatements);
    assertSame(declaringType, edit.getDeclaringType());
  }

  @Test
  public void getField_returnsConstructionValue() {
    DeclareGalleryFieldEdit edit = new DeclareGalleryFieldEdit(null, null, declaringType, field, doStatements, undoStatements);
    assertSame(field, edit.getField());
  }

  @Test
  public void storesSceneEditorReference() throws Exception {
    DeclareGalleryFieldEdit edit = new DeclareGalleryFieldEdit(null, null, declaringType, field, doStatements, undoStatements);
    Field privateField = DeclareGalleryFieldEdit.class.getDeclaredField("sceneEditor");
    privateField.setAccessible(true);
    assertNull(privateField.get(edit));
  }

  @Test
  public void storesDoStatementsArrayReference() throws Exception {
    DeclareGalleryFieldEdit edit = new DeclareGalleryFieldEdit(null, null, declaringType, field, doStatements, undoStatements);
    Field privateField = DeclareGalleryFieldEdit.class.getDeclaredField("doStatements");
    privateField.setAccessible(true);
    assertSame(doStatements, privateField.get(edit));
  }

  @Test
  public void storesUndoStatementsArrayReference() throws Exception {
    DeclareGalleryFieldEdit edit = new DeclareGalleryFieldEdit(null, null, declaringType, field, doStatements, undoStatements);
    Field privateField = DeclareGalleryFieldEdit.class.getDeclaredField("undoStatements");
    privateField.setAccessible(true);
    assertSame(undoStatements, privateField.get(edit));
  }

  @Test
  public void extendsDeclareFieldEdit() {
    DeclareGalleryFieldEdit edit = new DeclareGalleryFieldEdit(null, null, declaringType, field, doStatements, undoStatements);
    assertTrue(edit instanceof DeclareFieldEdit);
  }

  @Test
  public void extendsAbstractEdit() {
    DeclareGalleryFieldEdit edit = new DeclareGalleryFieldEdit(null, null, declaringType, field, doStatements, undoStatements);
    assertTrue(edit instanceof AbstractEdit);
  }

  @Test
  public void classIsNotFinal() {
    assertFalse(Modifier.isFinal(DeclareGalleryFieldEdit.class.getModifiers()));
  }

  @Test
  public void getTerseDescription_containsDeclarePrefix() {
    DeclareGalleryFieldEdit edit = new DeclareGalleryFieldEdit(null, null, declaringType, field, doStatements, undoStatements);
    assertTrue(edit.getTerseDescription().startsWith("declare:"));
  }

  @Test
  public void getDetailedDescription_containsClassName() {
    DeclareGalleryFieldEdit edit = new DeclareGalleryFieldEdit(null, null, declaringType, field, doStatements, undoStatements);
    assertTrue(edit.getDetailedDescription().contains(DeclareGalleryFieldEdit.class.getName()));
  }

  @Test
  public void getRedoPresentation_containsRedoPrefix() {
    DeclareGalleryFieldEdit edit = new DeclareGalleryFieldEdit(null, null, declaringType, field, doStatements, undoStatements);
    assertTrue(edit.getRedoPresentation().startsWith("Redo:"));
  }

  @Test
  public void getUndoPresentation_containsUndoPrefix() {
    DeclareGalleryFieldEdit edit = new DeclareGalleryFieldEdit(null, null, declaringType, field, doStatements, undoStatements);
    assertTrue(edit.getUndoPresentation().startsWith("Undo:"));
  }

  @Test
  public void encodeMethodExists() throws Exception {
    Method method = DeclareGalleryFieldEdit.class.getMethod("encode", BinaryEncoder.class);
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void constructorSignature_matchesSource() throws Exception {
    Constructor<DeclareGalleryFieldEdit> constructor = DeclareGalleryFieldEdit.class.getConstructor(
        org.lgna.croquet.history.UserActivity.class,
        org.alice.ide.sceneeditor.AbstractSceneEditor.class,
        org.lgna.project.ast.UserType.class,
        UserField.class,
        Statement[].class,
        Statement[].class);
    assertNotNull(constructor);
  }

  @Test
  public void binaryDecoderConstructor_exists() throws Exception {
    Constructor<DeclareGalleryFieldEdit> constructor = DeclareGalleryFieldEdit.class.getConstructor(
        edu.cmu.cs.dennisc.codec.BinaryDecoder.class,
        Object.class);
    assertNotNull(constructor);
  }

  @Test
  public void directSuperclassIsDeclareFieldEdit() {
    assertSame(DeclareFieldEdit.class, DeclareGalleryFieldEdit.class.getSuperclass());
  }

  @Test
  public void construction_doesNotMutateDeclaringTypeFields() {
    new DeclareGalleryFieldEdit(null, null, declaringType, field, doStatements, undoStatements);
    assertTrue(declaringType.fields.isEmpty());
  }
}
