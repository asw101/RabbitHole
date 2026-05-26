package org.alice.ide.croquet.models.ast.cascade.expression;

import org.alice.ide.croquet.models.cascade.ParameterBlank;
import org.junit.Test;
import org.lgna.croquet.CascadeBlank;
import org.lgna.project.ast.ArrayAccess;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionProperty;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ParameterAccess;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ExpressionCascadeComputationTest {
  @Test
  public void thisOperationCachesByExpressionProperty() {
    ExpressionProperty property = expressionProperty();

    ThisOperation cached = ThisOperation.getInstance(property);
    assertSame(cached, ThisOperation.getInstance(property));
    assertNotSame(cached, ThisOperation.getInstance(expressionProperty()));
  }

  @Test
  public void thisOperationCreatesAThisExpression() throws Exception {
    Expression expression = invokeCreateExpression(ThisOperation.getInstance(expressionProperty()));

    assertTrue(expression instanceof ThisExpression);
  }

  @Test
  public void parameterAccessOperationCreatesAnAccessForTheRequestedParameter() throws Exception {
    UserParameter parameter = new UserParameter("count", Integer.class);

    Expression expression = invokeCreateExpression(ParameterAccessOperation.getInstance(parameter, expressionProperty()));

    assertTrue(expression instanceof ParameterAccess);
    assertSame(parameter, ((ParameterAccess) expression).parameter.getValue());
    assertEquals(JavaType.getInstance(Integer.class), expression.getType());
  }

  @Test
  public void functionInvocationCascadeComputesOneBlankPerRequiredParameter() throws Exception {
    UserMethod method = new UserMethod();
    UserParameter count = new UserParameter("count", Integer.class);
    UserParameter label = new UserParameter("label", String.class);
    method.requiredParameters.add(count);
    method.requiredParameters.add(label);

    FunctionInvocationCascade cascade = FunctionInvocationCascade.getInstance(method, expressionProperty());
    List<? extends CascadeBlank<Expression>> blanks = invokeGetBlanks(cascade);

    assertEquals(2, blanks.size());
    assertSame(ParameterBlank.getInstance(count), blanks.get(0));
    assertSame(ParameterBlank.getInstance(label), blanks.get(1));
    assertSame(cascade, FunctionInvocationCascade.getInstance(method, cascade.getExpressionProperty()));
    assertNotSame(cascade, FunctionInvocationCascade.getInstance(method, expressionProperty()));
  }

  @Test
  public void fieldArrayAccessAndLocalArrayLengthCacheBySubjectAndExpressionProperty() {
    org.lgna.project.ast.UserField field = new org.lgna.project.ast.UserField("items", String[].class, null);
    org.lgna.project.ast.UserLocal local = new org.lgna.project.ast.UserLocal("counts", Integer[].class, false);
    ExpressionProperty property = expressionProperty();

    FieldArrayAccessCascade fieldCascade = FieldArrayAccessCascade.getInstance(field, property);
    assertSame(fieldCascade, FieldArrayAccessCascade.getInstance(field, property));
    assertNotSame(fieldCascade, FieldArrayAccessCascade.getInstance(field, expressionProperty()));

    LocalArrayLengthOperation localOperation = LocalArrayLengthOperation.getInstance(local, property);
    assertSame(localOperation, LocalArrayLengthOperation.getInstance(local, property));
    assertNotSame(localOperation, LocalArrayLengthOperation.getInstance(local, expressionProperty()));
  }

  @Test
  public void arrayAccessCascadeBuildsArrayAccessFromAccessExpressionAndIndex() {
    UserParameter parameter = new UserParameter("items", String[].class);
    ParameterAccess accessExpression = new ParameterAccess(parameter);
    IntegerLiteral index = new IntegerLiteral(3);
    TestArrayAccessCascade cascade = new TestArrayAccessCascade(
        expressionProperty(),
        JavaType.getInstance(String[].class),
        accessExpression);

    ArrayAccess access = cascade.compute(index);

    assertSame(JavaType.getInstance(String[].class), access.arrayType.getValue());
    assertSame(accessExpression, access.array.getValue());
    assertSame(index, access.index.getValue());
    assertEquals(JavaType.getInstance(String.class), access.getType());
  }

  private static ExpressionProperty expressionProperty() {
    return new ExpressionStatement().expression;
  }

  @SuppressWarnings("unchecked")
  private static List<? extends CascadeBlank<Expression>> invokeGetBlanks(Object cascade) throws Exception {
    Method method = org.lgna.croquet.ImmutableCascade.class.getDeclaredMethod("getBlanks");
    method.setAccessible(true);
    return (List<? extends CascadeBlank<Expression>>) method.invoke(cascade);
  }

  private static Expression invokeCreateExpression(Object operation) throws Exception {
    Method method = operation.getClass().getDeclaredMethod("createExpression");
    method.setAccessible(true);
    return (Expression) method.invoke(operation);
  }

  private static final class TestArrayAccessCascade extends ArrayAccessCascade {
    private final org.lgna.project.ast.AbstractType<?, ?, ?> arrayType;
    private final Expression accessExpression;

    private TestArrayAccessCascade(
        ExpressionProperty expressionProperty,
        org.lgna.project.ast.AbstractType<?, ?, ?> arrayType,
        Expression accessExpression) {
      super(UUID.fromString("99e26f56-ef12-4444-b608-7a53b9f1f4e6"), expressionProperty);
      this.arrayType = arrayType;
      this.accessExpression = accessExpression;
    }

    @Override
    protected org.lgna.project.ast.AbstractType<?, ?, ?> getArrayType() {
      return this.arrayType;
    }

    @Override
    protected Expression createAccessExpression() {
      return this.accessExpression;
    }

    private ArrayAccess compute(Expression indexExpression) {
      return (ArrayAccess) this.createExpression(new Expression[] {indexExpression});
    }
  }
}
