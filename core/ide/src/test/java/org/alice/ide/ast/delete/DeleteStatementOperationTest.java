package org.alice.ide.ast.delete;

import static org.alice.ide.testing.JacocoReflectionSupport.declaredMethods;
import static org.alice.ide.testing.JacocoReflectionSupport.declaredFields;

import org.junit.Test;
import org.lgna.croquet.ActionOperation;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.Statement;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class DeleteStatementOperationTest {

  private Comment createStatement(String text) {
    return new Comment(text);
  }

  private Object readStatement(DeleteStatementOperation operation) throws Exception {
    Field field = DeleteStatementOperation.class.getDeclaredField("statement");
    field.setAccessible(true);
    return field.get(operation);
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(DeleteStatementOperation.class.getModifiers()));
  }

  @Test
  public void classIsConcrete() {
    assertFalse(Modifier.isAbstract(DeleteStatementOperation.class.getModifiers()));
  }

  @Test
  public void classIsNotFinal() {
    assertFalse(Modifier.isFinal(DeleteStatementOperation.class.getModifiers()));
  }

  @Test
  public void classIsNotInterfaceOrEnum() {
    assertFalse(DeleteStatementOperation.class.isInterface());
    assertFalse(DeleteStatementOperation.class.isEnum());
  }

  @Test
  public void extendsActionOperation() {
    assertTrue(ActionOperation.class.isAssignableFrom(DeleteStatementOperation.class));
  }

  @Test
  public void simpleNameMatchesSource() {
    assertEquals("DeleteStatementOperation", DeleteStatementOperation.class.getSimpleName());
  }

  @Test
  public void packageNameMatchesSource() {
    assertEquals("org.alice.ide.ast.delete", DeleteStatementOperation.class.getPackage().getName());
  }

  @Test
  public void hasSingleConstructor() {
    assertEquals(1, DeleteStatementOperation.class.getDeclaredConstructors().length);
  }

  @Test
  public void constructorAcceptsStatement() throws Exception {
    assertNotNull(DeleteStatementOperation.class.getDeclaredConstructor(Statement.class));
  }

  @Test
  public void constructorIsPublic() throws Exception {
    Constructor<?> constructor = DeleteStatementOperation.class.getDeclaredConstructor(Statement.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void constructorHasSingleParameter() throws Exception {
    assertEquals(1, DeleteStatementOperation.class.getDeclaredConstructor(Statement.class).getParameterCount());
  }

  @Test
  public void statementFieldExists() throws Exception {
    assertNotNull(DeleteStatementOperation.class.getDeclaredField("statement"));
  }

  @Test
  public void statementFieldIsPrivateFinal() throws Exception {
    Field field = DeleteStatementOperation.class.getDeclaredField("statement");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void statementFieldUsesStatementType() throws Exception {
    Field field = DeleteStatementOperation.class.getDeclaredField("statement");
    assertEquals(Statement.class, field.getType());
  }

  @Test
  public void performMethodExists() throws Exception {
    assertNotNull(DeleteStatementOperation.class.getDeclaredMethod("perform", org.lgna.croquet.history.UserActivity.class));
  }

  @Test
  public void performMethodIsProtectedVoid() throws Exception {
    Method method = DeleteStatementOperation.class.getDeclaredMethod("perform", org.lgna.croquet.history.UserActivity.class);
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void performMethodHasSingleParameter() throws Exception {
    Method method = DeleteStatementOperation.class.getDeclaredMethod("perform", org.lgna.croquet.history.UserActivity.class);
    assertEquals(1, method.getParameterCount());
  }

  @Test
  public void instanceCanBeConstructedWithComment() {
    assertNotNull(new DeleteStatementOperation(createStatement("delete me")));
  }

  @Test
  public void constructorStoresProvidedStatement() throws Exception {
    Comment statement = createStatement("target");
    DeleteStatementOperation operation = new DeleteStatementOperation(statement);
    assertSame(statement, readStatement(operation));
  }

  @Test
  public void constructorAllowsNullStatement() throws Exception {
    assertNull(readStatement(new DeleteStatementOperation(null)));
  }

  @Test
  public void differentStatementsProduceDifferentOperations() {
    assertNotSame(new DeleteStatementOperation(createStatement("one")), new DeleteStatementOperation(createStatement("two")));
  }

  @Test
  public void classIsAssignableFromOwnInstance() {
    assertTrue(DeleteStatementOperation.class.isInstance(new DeleteStatementOperation(createStatement("sample"))));
  }

  @Test
  public void classHasOneDeclaredField() {
    assertEquals(1, declaredFields(DeleteStatementOperation.class).length);
  }

  @Test
  public void classHasOneDeclaredMethod() {
    assertEquals(1, declaredMethods(DeleteStatementOperation.class).length);
  }

  @Test
  public void classHasOneDeclaredConstructor() {
    assertEquals(1, DeleteStatementOperation.class.getDeclaredConstructors().length);
  }

  @Test
  public void operationSuperclassIsActionOperation() {
    assertEquals(ActionOperation.class, DeleteStatementOperation.class.getSuperclass());
  }

  @Test
  public void performMethodNameMatchesSource() throws Exception {
    assertEquals("perform", DeleteStatementOperation.class.getDeclaredMethod("perform", org.lgna.croquet.history.UserActivity.class).getName());
  }

  @Test
  public void statementFieldNameMatchesSource() throws Exception {
    assertEquals("statement", DeleteStatementOperation.class.getDeclaredField("statement").getName());
  }


  @Test
  public void operationInstancesUseExactConcreteClass() {
    assertSame(DeleteStatementOperation.class, new DeleteStatementOperation(createStatement("exact")).getClass());
  }

  @Test
  public void constructorParameterTypeNameMatchesStatement() throws Exception {
    assertEquals("org.lgna.project.ast.Statement", DeleteStatementOperation.class.getDeclaredConstructor(Statement.class).getParameterTypes()[0].getName());
  }

  @Test
  public void statementFieldCanStoreCommentSubtype() throws Exception {
    Comment comment = createStatement("subtype");
    DeleteStatementOperation operation = new DeleteStatementOperation(comment);
    assertTrue(readStatement(operation) instanceof Comment);
  }

  @Test
  public void getSuperclassChainEndsAtActionOperation() {
    assertEquals(ActionOperation.class, DeleteStatementOperation.class.getSuperclass());
  }

}
