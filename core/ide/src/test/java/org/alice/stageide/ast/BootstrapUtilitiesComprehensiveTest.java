package org.alice.stageide.ast;

import org.alice.stageide.StageIDE;
import org.junit.Test;
import org.lgna.project.ast.AccessLevel;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.SuperConstructorInvocationStatement;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.TypeExpression;
import org.lgna.project.ast.UserMethod;
import org.lgna.story.SProgram;
import org.lgna.story.SScene;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class BootstrapUtilitiesComprehensiveTest {
  @Test
  public void createTypeWithAbstractTypeSetsNameAndSuperType() throws Exception {
    NamedUserType type = invokeCreateType("Program", JavaType.getInstance(SProgram.class));

    assertEquals("Program", type.getName());
    assertSame(JavaType.getInstance(SProgram.class), type.getSuperType());
  }

  @Test
  public void createTypeWithAbstractTypeCreatesSingleConstructor() throws Exception {
    NamedUserType type = invokeCreateType("Program", JavaType.getInstance(SProgram.class));

    assertEquals(1, type.constructors.size());
    assertTrue(type.constructors.get(0).body.getValue().constructorInvocationStatement.getValue() instanceof SuperConstructorInvocationStatement);
  }

  @Test
  public void createTypeInitializesSuperConstructorInvocation() throws Exception {
    NamedUserType type = invokeCreateType("Program", JavaType.getInstance(SProgram.class));
    NamedUserConstructor constructor = type.constructors.get(0);
    SuperConstructorInvocationStatement statement = (SuperConstructorInvocationStatement) constructor.body.getValue().constructorInvocationStatement.getValue();

    assertSame(type.getSuperType().getDeclaredConstructor(), statement.constructor.getValue());
  }

  @Test
  public void createTypeWithClassUsesJavaTypeForSuperClass() throws Exception {
    NamedUserType type = invokeCreateType("Scene", SScene.class);

    assertSame(JavaType.getInstance(SScene.class), type.getSuperType());
  }

  @Test
  public void createProcedureSetsAccessNameReturnTypeAndEmptyBody() throws Exception {
    UserMethod method = invokeCreateProcedure(AccessLevel.PRIVATE, "performCustomSetup");

    assertEquals(AccessLevel.PRIVATE, method.getAccessLevel());
    assertEquals("performCustomSetup", method.getName());
    assertSame(JavaType.VOID_TYPE, method.getReturnType());
    assertTrue(method.body.getValue().statements.isEmpty());
  }

  @Test
  public void createProcedureLeavesStaticAndSignatureLockDisabled() throws Exception {
    UserMethod method = invokeCreateProcedure(AccessLevel.PUBLIC, "callMe");

    assertFalse(method.isStatic());
    assertFalse(method.isSignatureLocked());
  }

  @Test
  public void createStoryDeclarationCreatesFinalStoryLocal() throws Exception {
    LocalDeclarationStatement declaration = invokeCreateStoryDeclaration(JavaType.getInstance(SProgram.class));

    assertEquals("story", declaration.local.getValue().getName());
    assertTrue(declaration.local.getValue().isFinal.getValue());
  }

  @Test
  public void createStoryDeclarationUsesInstanceCreationOfDeclaredType() throws Exception {
    LocalDeclarationStatement declaration = invokeCreateStoryDeclaration(JavaType.getInstance(SProgram.class));

    assertTrue(declaration.initializer.getValue() instanceof InstanceCreation);
    assertSame(JavaType.getInstance(SProgram.class), declaration.initializer.getValue().getType());
  }

  @Test
  public void createStoryDeclarationUsesDeclaredConstructor() throws Exception {
    LocalDeclarationStatement declaration = invokeCreateStoryDeclaration(JavaType.getInstance(SProgram.class));
    InstanceCreation creation = (InstanceCreation) declaration.initializer.getValue();

    assertSame(JavaType.getInstance(SProgram.class).getDeclaredConstructor(), creation.constructor.getValue());
  }

  @Test
  public void createFieldAccessUsesTypeExpressionForEnum() {
    FieldAccess fieldAccess = BootstrapUtilities.createFieldAccess(Thread.State.BLOCKED);

    assertTrue(fieldAccess.expression.getValue() instanceof TypeExpression);
    assertEquals("BLOCKED", fieldAccess.field.getValue().getName());
  }

  @Test
  public void createMethodInvocationStatementPreservesReceiver() {
    JavaMethod method = JavaMethod.getInstance(String.class, "trim");
    ThisExpression receiver = new ThisExpression();

    ExpressionStatement statement = BootstrapUtilities.createMethodInvocationStatement(receiver, method);

    assertSame(receiver, ((MethodInvocation) statement.expression.getValue()).expression.getValue());
  }

  @Test
  public void createMethodInvocationStatementPreservesArgumentOrder() {
    JavaMethod method = JavaMethod.getInstance(String.class, "substring", int.class, int.class);
    Expression first = new org.lgna.project.ast.IntegerLiteral(1);
    Expression second = new org.lgna.project.ast.IntegerLiteral(3);

    MethodInvocation invocation = (MethodInvocation) BootstrapUtilities.createMethodInvocationStatement(new org.lgna.project.ast.StringLiteral("hello"), method, first, second).expression.getValue();

    assertSame(first, invocation.requiredArguments.get(0).expression.getValue());
    assertSame(second, invocation.requiredArguments.get(1).expression.getValue());
  }

  @Test
  public void createMethodInvocationStatementWithoutArgumentsLeavesRequiredArgumentsEmpty() {
    JavaMethod method = JavaMethod.getInstance(String.class, "trim");

    MethodInvocation invocation = (MethodInvocation) BootstrapUtilities.createMethodInvocationStatement(new org.lgna.project.ast.StringLiteral("hello"), method).expression.getValue();

    assertTrue(invocation.requiredArguments.isEmpty());
  }

  @Test
  public void addCommentIfNecessaryAddsKnownGeneratedComment() throws Exception {
    NamedUserType sceneType = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    UserMethod method = invokeCreateProcedure(AccessLevel.PRIVATE, StageIDE.PERFORM_GENERATED_SET_UP_METHOD_NAME);
    sceneType.methods.add(method);

    invokeAddCommentIfNecessary(method);

    assertTrue(method.body.getValue().statements.get(0) instanceof Comment);
    assertTrue(((Comment) method.body.getValue().statements.get(0)).text.getValue().contains("DO NOT EDIT"));
  }

  @Test
  public void addCommentIfNecessarySkipsUnknownMethods() throws Exception {
    NamedUserType sceneType = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    UserMethod method = invokeCreateProcedure(AccessLevel.PRIVATE, "customMethod");
    sceneType.methods.add(method);

    invokeAddCommentIfNecessary(method);

    assertTrue(method.body.getValue().statements.isEmpty());
  }

  private static NamedUserType invokeCreateType(String name, Object superTypeOrClass) throws Exception {
    Method method = superTypeOrClass instanceof Class<?> ? BootstrapUtilities.class.getDeclaredMethod("createType", String.class, Class.class) : BootstrapUtilities.class.getDeclaredMethod("createType", String.class, org.lgna.project.ast.AbstractType.class);
    method.setAccessible(true);
    return (NamedUserType) method.invoke(null, name, superTypeOrClass);
  }

  private static UserMethod invokeCreateProcedure(AccessLevel accessLevel, String name) throws Exception {
    Method method = BootstrapUtilities.class.getDeclaredMethod("createProcedure", AccessLevel.class, String.class);
    method.setAccessible(true);
    return (UserMethod) method.invoke(null, accessLevel, name);
  }

  private static LocalDeclarationStatement invokeCreateStoryDeclaration(org.lgna.project.ast.AbstractType<?, ?, ?> type) throws Exception {
    Method method = BootstrapUtilities.class.getDeclaredMethod("createStoryDeclaration", org.lgna.project.ast.AbstractType.class);
    method.setAccessible(true);
    return (LocalDeclarationStatement) method.invoke(null, type);
  }

  private static void invokeAddCommentIfNecessary(UserMethod methodValue) throws Exception {
    Method method = BootstrapUtilities.class.getDeclaredMethod("addCommentIfNecessaryToMethod", UserMethod.class);
    method.setAccessible(true);
    method.invoke(null, methodValue);
  }
}
