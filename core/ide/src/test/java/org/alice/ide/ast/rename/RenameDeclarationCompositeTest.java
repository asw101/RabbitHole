package org.alice.ide.ast.rename;

import org.alice.ide.croquet.edits.ast.rename.RenameDeclarationEdit;
import org.alice.ide.name.NameValidator;
import org.junit.Test;
import org.lgna.croquet.edits.Edit;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.Assert.*;

public class RenameDeclarationCompositeTest {

  private static class DummyNameValidator extends NameValidator {
    @Override
    public boolean isNameValid(String name) {
      return name != null && !name.trim().isEmpty();
    }

    @Override
    public boolean isNameAvailable(String name) {
      return true;
    }
  }

  private static class TestRenameDeclarationComposite extends RenameDeclarationComposite<UserField> {
    private TestRenameDeclarationComposite(UserField declaration) {
      super(UUID.fromString("00000000-0000-0000-0000-000000000123"), new DummyNameValidator(), declaration);
    }
  }

  private UserField createField(String name) {
    return new UserField(name, JavaType.STRING_TYPE, new NullLiteral());
  }

  private RenameDeclarationEdit createEdit(TestRenameDeclarationComposite composite, String nextName) throws Exception {
    composite.getNameState().setValueTransactionlessly(nextName);
    Method method = RenameDeclarationComposite.class.getDeclaredMethod("createEdit", org.lgna.croquet.history.UserActivity.class);
    method.setAccessible(true);
    return (RenameDeclarationEdit) method.invoke(composite, new Object[] { null });
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(RenameDeclarationComposite.class.getModifiers()));
  }

  @Test
  public void classIsAbstract() {
    assertTrue(Modifier.isAbstract(RenameDeclarationComposite.class.getModifiers()));
  }

  @Test
  public void extendsRenameComposite() {
    assertTrue(RenameComposite.class.isAssignableFrom(RenameDeclarationComposite.class));
  }

  @Test
  public void simpleNameMatchesSource() {
    assertEquals("RenameDeclarationComposite", RenameDeclarationComposite.class.getSimpleName());
  }

  @Test
  public void packageNameMatchesSource() {
    assertEquals("org.alice.ide.ast.rename", RenameDeclarationComposite.class.getPackage().getName());
  }

  @Test
  public void declarationFieldExists() throws Exception {
    assertNotNull(RenameDeclarationComposite.class.getDeclaredField("declaration"));
  }

  @Test
  public void declarationFieldIsPrivateFinal() throws Exception {
    Field field = RenameDeclarationComposite.class.getDeclaredField("declaration");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void declarationFieldUsesAbstractDeclarationType() throws Exception {
    Field field = RenameDeclarationComposite.class.getDeclaredField("declaration");
    assertEquals(org.lgna.project.ast.AbstractDeclaration.class, field.getType());
  }

  @Test
  public void constructorAcceptsUuidValidatorAndDeclaration() throws Exception {
    Constructor<?> constructor = RenameDeclarationComposite.class.getDeclaredConstructor(UUID.class, NameValidator.class, org.lgna.project.ast.AbstractDeclaration.class);
    assertNotNull(constructor);
  }

  @Test
  public void constructorIsPublic() throws Exception {
    Constructor<?> constructor = RenameDeclarationComposite.class.getDeclaredConstructor(UUID.class, NameValidator.class, org.lgna.project.ast.AbstractDeclaration.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void getInitialValueMethodExists() throws Exception {
    Method method = RenameDeclarationComposite.class.getDeclaredMethod("getInitialValue");
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(String.class, method.getReturnType());
  }

  @Test
  public void createEditMethodExists() throws Exception {
    Method method = RenameDeclarationComposite.class.getDeclaredMethod("createEdit", org.lgna.croquet.history.UserActivity.class);
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(Edit.class, method.getReturnType());
  }

  @Test
  public void createViewMethodExists() throws Exception {
    Method method = RenameDeclarationComposite.class.getDeclaredMethod("createView");
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(org.alice.ide.ast.rename.components.RenamePanel.class, method.getReturnType());
  }

  @Test
  public void getViewBackgroundColorMethodIsPrivate() throws Exception {
    Method method = RenameDeclarationComposite.class.getDeclaredMethod("getViewBackgroundColor");
    assertTrue(Modifier.isPrivate(method.getModifiers()));
  }

  @Test
  public void concreteSubclassCanBeConstructed() {
    assertNotNull(new TestRenameDeclarationComposite(createField("fieldName")));
  }

  @Test
  public void inheritedNameStateIsAvailable() {
    assertNotNull(new TestRenameDeclarationComposite(createField("distance")).getNameState());
  }

  @Test
  public void declarationFieldStoresProvidedDeclaration() throws Exception {
    UserField field = createField("score");
    TestRenameDeclarationComposite composite = new TestRenameDeclarationComposite(field);
    Field declaration = RenameDeclarationComposite.class.getDeclaredField("declaration");
    declaration.setAccessible(true);
    assertSame(field, declaration.get(composite));
  }

  @Test
  public void getInitialValueUsesDeclarationName() throws Exception {
    TestRenameDeclarationComposite composite = new TestRenameDeclarationComposite(createField("coins"));
    Method method = RenameDeclarationComposite.class.getDeclaredMethod("getInitialValue");
    method.setAccessible(true);
    assertEquals("coins", method.invoke(composite));
  }

  @Test
  public void createEditReturnsRenameDeclarationEdit() throws Exception {
    RenameDeclarationEdit edit = createEdit(new TestRenameDeclarationComposite(createField("speed")), "velocity");
    assertNotNull(edit);
  }

  @Test
  public void createEditCapturesPreviousAndNextValues() throws Exception {
    RenameDeclarationEdit edit = createEdit(new TestRenameDeclarationComposite(createField("size")), "width");
    Field prevValue = RenameDeclarationEdit.class.getDeclaredField("prevValue");
    Field nextValue = RenameDeclarationEdit.class.getDeclaredField("nextValue");
    prevValue.setAccessible(true);
    nextValue.setAccessible(true);
    assertEquals("size", prevValue.get(edit));
    assertEquals("width", nextValue.get(edit));
  }

  @Test
  public void createEditCapturesDeclarationReference() throws Exception {
    UserField field = createField("health");
    RenameDeclarationEdit edit = createEdit(new TestRenameDeclarationComposite(field), "hp");
    Field declaration = RenameDeclarationEdit.class.getDeclaredField("declaration");
    declaration.setAccessible(true);
    assertSame(field, declaration.get(edit));
  }

  @Test
  public void subclassIsAssignableToRenameComposite() {
    assertTrue(RenameComposite.class.isAssignableFrom(TestRenameDeclarationComposite.class));
  }

  @Test
  public void declaredMethodCountMatchesSource() {
    assertEquals(5, RenameDeclarationComposite.class.getDeclaredMethods().length);
  }


  @Test
  public void genericSuperclassReferencesRenameComposite() {
    assertTrue(RenameDeclarationComposite.class.getGenericSuperclass().getTypeName().contains("RenameComposite"));
  }

}
