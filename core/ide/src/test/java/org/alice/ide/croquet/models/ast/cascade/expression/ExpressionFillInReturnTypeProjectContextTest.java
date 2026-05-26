package org.alice.ide.croquet.models.ast.cascade.expression;

import org.alice.ide.instancefactory.InstanceFactory;
import org.alice.ide.instancefactory.ThisFieldAccessFactory;
import org.alice.ide.instancefactory.ThisInstanceFactory;
import org.alice.ide.testing.ProjectContextTestCase;
import org.alice.ide.testing.TestIdeBootstrap;
import org.junit.Test;
import org.lgna.project.ast.ArrayAccess;
import org.lgna.project.ast.ArrayLength;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionProperty;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalAccess;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ParameterAccess;
import org.lgna.project.ast.ReturnStatement;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ExpressionFillInReturnTypeProjectContextTest extends ProjectContextTestCase {
  @Test
  public void fieldAndLocalAccessOperationsPreserveDeclaredValueTypes() throws Exception {
    UserLocal scoreLabel = new UserLocal("scoreLabel", String.class, false);
    configureContext(fixture.sceneProcedure, ThisInstanceFactory.getInstance());

    Expression fieldExpression = invokeCreateExpression(FieldAccessOperation.getInstance(fixture.actorField, expressionProperty()));
    Expression localExpression = invokeCreateExpression(LocalAccessOperation.getInstance(scoreLabel, expressionProperty()));

    assertTrue(fieldExpression instanceof FieldAccess);
    assertSame(fixture.actorField, ((FieldAccess) fieldExpression).field.getValue());
    assertSame(fixture.actorType, fieldExpression.getType());

    assertTrue(localExpression instanceof LocalAccess);
    assertSame(scoreLabel, ((LocalAccess) localExpression).local.getValue());
    assertEquals(JavaType.getInstance(String.class), localExpression.getType());
  }

  @Test
  public void parameterLocalAndFieldArrayAccessCascadesProduceComponentTypedExpressions() throws Exception {
    UserParameter names = new UserParameter("names", String[].class);
    UserLocal counts = new UserLocal("counts", Integer[].class, false);
    UserField inventory = new UserField("inventory", String[].class, new NullLiteral());
    fixture.actorType.fields.add(inventory);
    configureContext(fixture.sceneProcedure, ThisFieldAccessFactory.getInstance(fixture.actorField));

    ArrayAccess parameterAccess = invokeArrayAccess(
        ParameterArrayAccessCascade.getInstance(names, expressionProperty()),
        new IntegerLiteral(1));
    ArrayAccess localAccess = invokeArrayAccess(
        LocalArrayAccessCascade.getInstance(counts, expressionProperty()),
        new IntegerLiteral(2));
    ArrayAccess fieldAccess = invokeArrayAccess(
        FieldArrayAccessCascade.getInstance(inventory, expressionProperty()),
        new IntegerLiteral(3));

    assertTrue(parameterAccess.array.getValue() instanceof ParameterAccess);
    assertSame(names, ((ParameterAccess) parameterAccess.array.getValue()).parameter.getValue());
    assertEquals(JavaType.getInstance(String.class), parameterAccess.getType());

    assertTrue(localAccess.array.getValue() instanceof LocalAccess);
    assertSame(counts, ((LocalAccess) localAccess.array.getValue()).local.getValue());
    assertEquals(JavaType.getInstance(Integer.class), localAccess.getType());

    assertTrue(fieldAccess.array.getValue() instanceof FieldAccess);
    assertSame(inventory, ((FieldAccess) fieldAccess.array.getValue()).field.getValue());
    assertEquals(JavaType.getInstance(String.class), fieldAccess.getType());
    assertTrue(((FieldAccess) fieldAccess.array.getValue()).expression.getValue() instanceof FieldAccess);
    FieldAccess receiver = (FieldAccess) fieldAccess.array.getValue();
    assertSame(fixture.actorField, ((FieldAccess) receiver.expression.getValue()).field.getValue());
  }

  @Test
  public void fieldParameterAndLocalArrayLengthOperationsReturnIntegerExpressions() throws Exception {
    UserField inventory = new UserField("inventory", String[].class, new NullLiteral());
    fixture.sceneType.fields.add(inventory);
    UserParameter numbers = new UserParameter("numbers", Integer[].class);
    UserLocal counts = new UserLocal("counts", Integer[].class, false);
    configureContext(fixture.sceneProcedure, ThisInstanceFactory.getInstance());

    Expression fieldLengthExpression = invokeCreateExpression(FieldArrayLengthOperation.getInstance(inventory, expressionProperty()));
    Expression parameterLengthExpression = invokeCreateExpression(ParameterArrayLengthOperation.getInstance(numbers, expressionProperty()));
    Expression localLengthExpression = invokeCreateExpression(LocalArrayLengthOperation.getInstance(counts, expressionProperty()));

    assertTrue(fieldLengthExpression instanceof ArrayLength);
    assertTrue(((ArrayLength) fieldLengthExpression).array.getValue() instanceof FieldAccess);
    assertSame(inventory, ((FieldAccess) ((ArrayLength) fieldLengthExpression).array.getValue()).field.getValue());
    assertSame(JavaType.INTEGER_OBJECT_TYPE, fieldLengthExpression.getType());

    assertTrue(parameterLengthExpression instanceof ArrayLength);
    assertTrue(((ArrayLength) parameterLengthExpression).array.getValue() instanceof ParameterAccess);
    assertSame(numbers, ((ParameterAccess) ((ArrayLength) parameterLengthExpression).array.getValue()).parameter.getValue());
    assertSame(JavaType.INTEGER_OBJECT_TYPE, parameterLengthExpression.getType());

    assertTrue(localLengthExpression instanceof ArrayLength);
    assertTrue(((ArrayLength) localLengthExpression).array.getValue() instanceof LocalAccess);
    assertSame(counts, ((LocalAccess) ((ArrayLength) localLengthExpression).array.getValue()).local.getValue());
    assertSame(JavaType.INTEGER_OBJECT_TYPE, localLengthExpression.getType());
  }

  @Test
  public void fieldAccessOperationUsesCurrentInstanceFactoryExpression() throws Exception {
    UserField nickname = new UserField("nickname", String.class, new NullLiteral());
    fixture.actorType.fields.add(nickname);
    configureContext(fixture.sceneProcedure, ThisFieldAccessFactory.getInstance(fixture.actorField));

    FieldAccess expression = (FieldAccess) invokeCreateExpression(FieldAccessOperation.getInstance(nickname, expressionProperty()));

    assertSame(nickname, expression.field.getValue());
    assertSame(JavaType.getInstance(String.class), expression.getType());
    assertTrue(expression.expression.getValue() instanceof FieldAccess);
    FieldAccess receiver = (FieldAccess) expression.expression.getValue();
    assertSame(fixture.actorField, receiver.field.getValue());
    assertTrue(receiver.expression.getValue() instanceof ThisExpression);
  }

  @Test
  public void functionInvocationCascadeUsesCurrentInstanceFactoryExpression() throws Exception {
    UserParameter suffix = new UserParameter("suffix", String.class);
    UserMethod describe = new UserMethod(
        "describe",
        JavaType.STRING_TYPE,
        new UserParameter[] {suffix},
        new BlockStatement(new ReturnStatement(JavaType.STRING_TYPE, new StringLiteral("hero"))));
    fixture.actorType.methods.add(describe);
    configureContext(fixture.sceneProcedure, ThisFieldAccessFactory.getInstance(fixture.actorField));

    StringLiteral argument = new StringLiteral("!");
    MethodInvocation invocation = invokeMethodInvocation(
        FunctionInvocationCascade.getInstance(describe, expressionProperty()),
        argument);

    assertSame(describe, invocation.method.getValue());
    assertSame(JavaType.STRING_TYPE, invocation.getType());
    assertTrue(invocation.expression.getValue() instanceof FieldAccess);
    FieldAccess receiver = (FieldAccess) invocation.expression.getValue();
    assertSame(fixture.actorField, receiver.field.getValue());
    assertTrue(receiver.expression.getValue() instanceof ThisExpression);
    assertSame(argument, invocation.requiredArguments.get(0).expression.getValue());
  }

  private static ExpressionProperty expressionProperty() {
    return new ExpressionStatement().expression;
  }

  private static Expression invokeCreateExpression(Object operation) throws Exception {
    Class<?> type = operation.getClass();
    while (type != null) {
      try {
        Method method = type.getDeclaredMethod("createExpression");
        method.setAccessible(true);
        return (Expression) method.invoke(operation);
      } catch (NoSuchMethodException missingOnThisType) {
        type = type.getSuperclass();
      }
    }
    throw new NoSuchMethodException("createExpression");
  }

  private static ArrayAccess invokeArrayAccess(ArrayAccessCascade cascade, Expression indexExpression) throws Exception {
    Method method = ArrayAccessCascade.class.getDeclaredMethod("createExpression", Expression[].class);
    method.setAccessible(true);
    return (ArrayAccess) method.invoke(cascade, new Object[] {new Expression[] {indexExpression}});
  }

  private static MethodInvocation invokeMethodInvocation(FunctionInvocationCascade cascade, Expression... arguments) throws Exception {
    Method method = FunctionInvocationCascade.class.getDeclaredMethod("createExpression", Expression[].class);
    method.setAccessible(true);
    return (MethodInvocation) method.invoke(cascade, new Object[] {arguments});
  }

  private static void configureContext(UserMethod focusedCode, InstanceFactory instanceFactory) {
    TestIdeBootstrap.runOnEdt(() -> {
      TestIdeBootstrap.getDocumentFrame().setFocusedCode(focusedCode);
      TestIdeBootstrap.getDocumentFrame().getInstanceFactoryState().setValueTransactionlessly(instanceFactory);
    });
  }
}
