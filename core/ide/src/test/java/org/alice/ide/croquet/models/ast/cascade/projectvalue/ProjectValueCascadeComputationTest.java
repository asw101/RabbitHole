package org.alice.ide.croquet.models.ast.cascade.projectvalue;

import org.alice.ide.croquet.edits.ast.ExpressionPropertyEdit;
import org.alice.ide.croquet.models.ast.cascade.ProjectExpressionPropertyCascade;
import org.alice.ide.croquet.models.ast.cascade.ProjectExpressionPropertyOperation;
import org.alice.ide.croquet.models.cascade.ExpressionBlank;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.ArithmeticInfixExpression;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionProperty;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ProjectValueCascadeComputationTest {
  @Test
  public void createEditUsesPreviousPropertyValueAndComputedProjectExpression() throws Exception {
    ExpressionProperty expressionProperty = new ExpressionStatement().expression;
    IntegerLiteral previous = new IntegerLiteral(1);
    IntegerLiteral left = new IntegerLiteral(2);
    IntegerLiteral right = new IntegerLiteral(3);
    expressionProperty.setValue(previous);
    TestProjectValueCascade cascade = new TestProjectValueCascade(expressionProperty);

    ExpressionPropertyEdit edit = cascade.createEditForTest(left, right);
    Expression next = getExpressionField(edit, "nextExpression");

    assertSame(expressionProperty, getField(edit, "expressionProperty"));
    assertSame(previous, getExpressionField(edit, "prevExpression"));
    assertTrue(next instanceof ArithmeticInfixExpression);
    assertSame(left, ((ArithmeticInfixExpression) next).leftOperand.getValue());
    assertSame(right, ((ArithmeticInfixExpression) next).rightOperand.getValue());
    assertSame(JavaType.getInstance(Integer.class), next.getType());
  }

  @Test
  public void projectExpressionPropertyEditCanDoAndUndoComputedValue() throws Exception {
    ExpressionProperty expressionProperty = new ExpressionStatement().expression;
    IntegerLiteral previous = new IntegerLiteral(5);
    IntegerLiteral left = new IntegerLiteral(8);
    IntegerLiteral right = new IntegerLiteral(13);
    expressionProperty.setValue(previous);
    TestProjectValueCascade cascade = new TestProjectValueCascade(expressionProperty);
    ExpressionPropertyEdit edit = cascade.createEditForTest(left, right);
    Expression next = getExpressionField(edit, "nextExpression");

    invokeDo(edit);
    assertSame(next, expressionProperty.getValue());

    invokeUndo(edit);
    assertSame(previous, expressionProperty.getValue());
  }

  @Test
  public void projectExpressionPropertyOperationCommitsCreatedExpressionToProperty() {
    ExpressionProperty expressionProperty = new ExpressionStatement().expression;
    IntegerLiteral previous = new IntegerLiteral(21);
    IntegerLiteral next = new IntegerLiteral(34);
    expressionProperty.setValue(previous);

    new TestProjectValueOperation(expressionProperty, next).performForTest();

    assertSame(next, expressionProperty.getValue());
  }

  private static Object getField(Object instance, String name) throws Exception {
    Field field = instance.getClass().getDeclaredField(name);
    field.setAccessible(true);
    return field.get(instance);
  }

  private static Expression getExpressionField(ExpressionPropertyEdit edit, String name) throws Exception {
    return (Expression) getField(edit, name);
  }

  private static void invokeDo(ExpressionPropertyEdit edit) throws Exception {
    Method method = ExpressionPropertyEdit.class.getDeclaredMethod("doOrRedoInternal", boolean.class);
    method.setAccessible(true);
    method.invoke(edit, true);
  }

  private static void invokeUndo(ExpressionPropertyEdit edit) throws Exception {
    Method method = ExpressionPropertyEdit.class.getDeclaredMethod("undoInternal");
    method.setAccessible(true);
    method.invoke(edit);
  }

  private static final class TestProjectValueCascade extends ProjectExpressionPropertyCascade {
    private TestProjectValueCascade(ExpressionProperty expressionProperty) {
      super(
          UUID.fromString("03d35014-c235-420f-bf57-261b18dd7907"),
          expressionProperty,
          ExpressionBlank.createBlanks(Integer.class, Integer.class));
    }

    @Override
    protected Expression createExpression(Expression[] expressions) {
      return new ArithmeticInfixExpression(
          expressions[0],
          ArithmeticInfixExpression.Operator.PLUS,
          expressions[1],
          Integer.class);
    }

    private ExpressionPropertyEdit createEditForTest(Expression... expressions) {
      return this.createEdit(new UserActivity(), expressions);
    }
  }

  private static final class TestProjectValueOperation extends ProjectExpressionPropertyOperation {
    private final Expression nextExpression;

    private TestProjectValueOperation(ExpressionProperty expressionProperty, Expression nextExpression) {
      super(UUID.fromString("62450fc9-998e-4d80-be2d-d30a4925114f"), expressionProperty);
      this.nextExpression = nextExpression;
    }

    @Override
    protected Expression createExpression() {
      return this.nextExpression;
    }

    private void performForTest() {
      this.perform(new UserActivity());
    }
  }
}
