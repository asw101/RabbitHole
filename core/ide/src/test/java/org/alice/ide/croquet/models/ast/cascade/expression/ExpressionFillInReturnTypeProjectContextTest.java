package org.alice.ide.croquet.models.ast.cascade.expression;

import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;
import org.lgna.project.ast.ArrayAccess;
import org.lgna.project.ast.ArrayLength;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionProperty;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalAccess;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ParameterAccess;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserParameter;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ExpressionFillInReturnTypeProjectContextTest extends ProjectContextTestCase {
  @Test
  public void fieldAndLocalAccessOperationsPreserveDeclaredValueTypes() throws Exception {
    UserLocal scoreLabel = new UserLocal("scoreLabel", String.class, false);

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
  public void parameterAndLocalArrayAccessCascadesProduceComponentTypedExpressions() throws Exception {
    UserParameter names = new UserParameter("names", String[].class);
    UserLocal counts = new UserLocal("counts", Integer[].class, false);

    ArrayAccess parameterAccess = invokeArrayAccess(
        ParameterArrayAccessCascade.getInstance(names, expressionProperty()),
        new IntegerLiteral(1));
    ArrayAccess localAccess = invokeArrayAccess(
        LocalArrayAccessCascade.getInstance(counts, expressionProperty()),
        new IntegerLiteral(2));

    assertTrue(parameterAccess.array.getValue() instanceof ParameterAccess);
    assertSame(names, ((ParameterAccess) parameterAccess.array.getValue()).parameter.getValue());
    assertEquals(JavaType.getInstance(String.class), parameterAccess.getType());

    assertTrue(localAccess.array.getValue() instanceof LocalAccess);
    assertSame(counts, ((LocalAccess) localAccess.array.getValue()).local.getValue());
    assertEquals(JavaType.getInstance(Integer.class), localAccess.getType());
  }

  @Test
  public void fieldAndParameterArrayLengthOperationsReturnIntegerExpressions() throws Exception {
    UserField inventory = new UserField("inventory", String[].class, new NullLiteral());
    fixture.sceneType.fields.add(inventory);
    UserParameter numbers = new UserParameter("numbers", Integer[].class);

    Expression fieldLengthExpression = invokeCreateExpression(FieldArrayLengthOperation.getInstance(inventory, expressionProperty()));
    Expression parameterLengthExpression = invokeCreateExpression(ParameterArrayLengthOperation.getInstance(numbers, expressionProperty()));

    assertTrue(fieldLengthExpression instanceof ArrayLength);
    assertTrue(((ArrayLength) fieldLengthExpression).array.getValue() instanceof FieldAccess);
    assertSame(inventory, ((FieldAccess) ((ArrayLength) fieldLengthExpression).array.getValue()).field.getValue());
    assertSame(JavaType.INTEGER_OBJECT_TYPE, fieldLengthExpression.getType());

    assertTrue(parameterLengthExpression instanceof ArrayLength);
    assertTrue(((ArrayLength) parameterLengthExpression).array.getValue() instanceof ParameterAccess);
    assertSame(numbers, ((ParameterAccess) ((ArrayLength) parameterLengthExpression).array.getValue()).parameter.getValue());
    assertSame(JavaType.INTEGER_OBJECT_TYPE, parameterLengthExpression.getType());
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
}
