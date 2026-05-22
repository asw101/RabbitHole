package org.alice.ide.croquet.models.ast;

import org.alice.ide.croquet.models.ast.cascade.statement.ProcedureInvocationInsertCascade;
import org.alice.ide.croquet.models.ast.cascade.statement.StatementInsertCascade;
import org.alice.ide.croquet.models.ast.cascade.statement.StatementInsertOperation;
import org.junit.Test;
import org.lgna.croquet.CompletionModel;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.history.UserActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class InsertStatementCompletionModelComprehensiveTest {

  @Test
  public void insertStatementCompletionModel_isPublic() {
    assertTrue(Modifier.isPublic(InsertStatementCompletionModel.class.getModifiers()));
  }

  @Test
  public void insertStatementCompletionModel_isInterface() {
    assertTrue(Modifier.isInterface(InsertStatementCompletionModel.class.getModifiers()));
  }

  @Test
  public void insertStatementCompletionModel_extendsCompletionModel() {
    assertTrue(CompletionModel.class.isAssignableFrom(InsertStatementCompletionModel.class));
  }

  @Test
  public void insertStatementCompletionModel_declaresNoMethods() {
    assertEquals(0, InsertStatementCompletionModel.class.getDeclaredMethods().length);
  }

  @Test
  public void insertStatementCompletionModel_declaresNoFields() {
    assertEquals(0, InsertStatementCompletionModel.class.getDeclaredFields().length);
  }

  @Test
  public void insertStatementCompletionModel_declaresNoTypeParameters() {
    assertEquals(0, InsertStatementCompletionModel.class.getTypeParameters().length);
  }

  @Test
  public void insertStatementCompletionModel_isNotAnnotationOrEnum() {
    assertFalse(InsertStatementCompletionModel.class.isAnnotation());
    assertFalse(InsertStatementCompletionModel.class.isEnum());
  }

  @Test
  public void statementInsertCascade_implementsInterface() {
    assertTrue(InsertStatementCompletionModel.class.isAssignableFrom(StatementInsertCascade.class));
  }

  @Test
  public void statementInsertOperation_implementsInterface() {
    assertTrue(InsertStatementCompletionModel.class.isAssignableFrom(StatementInsertOperation.class));
  }

  @Test
  public void procedureInvocationInsertCascade_implementsInterfaceThroughHierarchy() {
    assertTrue(InsertStatementCompletionModel.class.isAssignableFrom(ProcedureInvocationInsertCascade.class));
  }

  @Test
  public void statementInsertCascade_isAbstract() {
    assertTrue(Modifier.isAbstract(StatementInsertCascade.class.getModifiers()));
  }

  @Test
  public void statementInsertOperation_isAbstract() {
    assertTrue(Modifier.isAbstract(StatementInsertOperation.class.getModifiers()));
  }

  @Test
  public void statementInsertCascade_constructorMatchesSourceShape() {
    Constructor<?> constructor = StatementInsertCascade.class.getDeclaredConstructors()[0];
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertEquals(4, constructor.getParameterTypes().length);
  }

  @Test
  public void statementInsertOperation_constructorMatchesSourceShape() {
    Constructor<?> constructor = StatementInsertOperation.class.getDeclaredConstructors()[0];
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertEquals(2, constructor.getParameterTypes().length);
  }

  @Test
  public void statementInsertCascade_declaresEpicHackAccessor() throws Exception {
    Method method = StatementInsertCascade.class.getMethod("EPIC_HACK_isActive");
    assertEquals(boolean.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
  }

  @Test
  public void statementInsertCascade_declaresBlockStatementAccessor() throws Exception {
    Method method = StatementInsertCascade.class.getMethod("getBlockStatementIndexPair");
    assertTrue(Modifier.isPublic(method.getModifiers()));
  }

  @Test
  public void statementInsertCascade_declaresIsEnvelopingAccessor() throws Exception {
    Method method = StatementInsertCascade.class.getMethod("isEnveloping");
    assertEquals(boolean.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
  }

  @Test
  public void statementInsertCascade_declaresProtectedAbstractCreateStatement() throws Exception {
    Method method = StatementInsertCascade.class.getDeclaredMethod("createStatement", org.lgna.project.ast.Expression[].class);
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertTrue(Modifier.isAbstract(method.getModifiers()));
  }

  @Test
  public void statementInsertCascade_declaresProtectedCreateEdit() throws Exception {
    Method method = StatementInsertCascade.class.getDeclaredMethod(
        "createEdit", UserActivity.class, org.lgna.project.ast.Expression[].class);
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(org.alice.ide.croquet.edits.ast.InsertStatementEdit.class, method.getReturnType());
  }

  @Test
  public void statementInsertOperation_declaresBlockStatementAccessor() throws Exception {
    Method method = StatementInsertOperation.class.getMethod("getBlockStatementIndexPair");
    assertTrue(Modifier.isPublic(method.getModifiers()));
  }

  @Test
  public void statementInsertOperation_declaresProtectedAbstractCreateEdit() throws Exception {
    Method method = StatementInsertOperation.class.getDeclaredMethod("createEdit", UserActivity.class);
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertTrue(Modifier.isAbstract(method.getModifiers()));
    assertEquals(Edit.class, method.getReturnType());
  }

  @Test
  public void statementInsertOperation_declaresProtectedPerform() throws Exception {
    Method method = StatementInsertOperation.class.getDeclaredMethod("perform", UserActivity.class);
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void interfaceRemainsTopLevel() {
    assertNull(InsertStatementCompletionModel.class.getEnclosingClass());
  }

  @Test
  public void statementInsertTypesRemainClassesNotInterfaces() {
    assertFalse(StatementInsertCascade.class.isInterface());
    assertFalse(StatementInsertOperation.class.isInterface());
  }

  @Test
  public void statementInsertOperationAndCascadeAreDistinctAbstractions() {
    assertNotEquals(StatementInsertCascade.class, StatementInsertOperation.class);
  }

  @Test
  public void statementInsertCascade_extendsExpressionsCascade() {
    assertTrue(org.alice.ide.croquet.models.ast.cascade.ExpressionsCascade.class.isAssignableFrom(StatementInsertCascade.class));
  }

  @Test
  public void statementInsertOperation_extendsActionOperation() {
    assertTrue(org.lgna.croquet.ActionOperation.class.isAssignableFrom(StatementInsertOperation.class));
  }

  @Test
  public void procedureInvocationInsertCascade_isConcrete() {
    assertFalse(Modifier.isAbstract(ProcedureInvocationInsertCascade.class.getModifiers()));
  }

  @Test
  public void epicHackAccessor_acceptsNoParameters() throws Exception {
    assertEquals(0, StatementInsertCascade.class.getMethod("EPIC_HACK_isActive").getParameterTypes().length);
  }

  @Test
  public void insertStatementCompletionModel_simpleNameMatchesSource() {
    assertEquals("InsertStatementCompletionModel", InsertStatementCompletionModel.class.getSimpleName());
  }

  @Test
  public void insertStatementCompletionModel_packageMatchesSource() {
    assertEquals("org.alice.ide.croquet.models.ast", InsertStatementCompletionModel.class.getPackage().getName());
  }


  @Test
  public void statementInsertOperation_simpleNameMatchesSource() {
    assertEquals("StatementInsertOperation", StatementInsertOperation.class.getSimpleName());
  }

}
