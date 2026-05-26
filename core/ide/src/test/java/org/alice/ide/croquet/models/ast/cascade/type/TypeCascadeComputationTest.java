package org.alice.ide.croquet.models.ast.cascade.type;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.alice.ide.croquet.models.ast.cascade.statement.AssignmentInsertCascade;
import org.alice.ide.croquet.models.ast.cascade.statement.FieldArrayAtIndexAssignmentInsertCascade;
import org.alice.ide.croquet.models.ast.cascade.statement.LocalArrayAtIndexAssignmentInsertCascade;
import org.alice.ide.croquet.models.ast.cascade.statement.ReturnStatementInsertCascade;
import org.alice.ide.croquet.models.cascade.ExpressionBlank;
import org.junit.Test;
import org.lgna.croquet.CascadeBlank;
import org.lgna.project.ast.ArrayAccess;
import org.lgna.project.ast.AssignmentExpression;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalAccess;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ReturnStatement;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TypeCascadeComputationTest {
  @Test
  public void returnStatementCascadeUsesEnclosingFunctionTypeForBlankAndStatement() throws Exception {
    BlockStatement body = new BlockStatement();
    new UserMethod("score", JavaType.INTEGER_OBJECT_TYPE, new UserParameter[0], body);
    BlockStatementIndexPair pair = new BlockStatementIndexPair(body, 0);
    ReturnStatementInsertCascade cascade = ReturnStatementInsertCascade.getInstance(pair);
    IntegerLiteral value = new IntegerLiteral(42);

    List<? extends CascadeBlank<Expression>> blanks = invokeGetBlanks(cascade);
    ReturnStatement statement = (ReturnStatement) invokeCreateStatement(cascade, value);

    assertEquals(1, blanks.size());
    assertSame(JavaType.INTEGER_OBJECT_TYPE, ((ExpressionBlank) blanks.get(0)).getValueType());
    assertSame(JavaType.INTEGER_OBJECT_TYPE, statement.expressionType.getValue());
    assertSame(value, statement.expression.getValue());
  }

  @Test
  public void localArrayAssignmentCascadeUsesComponentTypeForAssignmentExpression() throws Exception {
    UserLocal local = new UserLocal("labels", String[].class, false);
    LocalArrayAtIndexAssignmentInsertCascade cascade = new LocalArrayAtIndexAssignmentInsertCascade(
        new BlockStatementIndexPair(new BlockStatement(), 0),
        local);
    IntegerLiteral index = new IntegerLiteral(1);
    StringLiteral value = new StringLiteral("alice");

    AssignmentExpression expression = invokeCreateAssignmentExpression(cascade, index, value);
    ArrayAccess leftHandSide = (ArrayAccess) expression.leftHandSide.getValue();

    assertSame(JavaType.getInstance(String.class), expression.expressionType.getValue());
    assertTrue(leftHandSide.array.getValue() instanceof LocalAccess);
    assertSame(local, ((LocalAccess) leftHandSide.array.getValue()).local.getValue());
    assertSame(JavaType.getInstance(String[].class), leftHandSide.arrayType.getValue());
    assertSame(JavaType.getInstance(String.class), leftHandSide.getType());
    assertSame(value, expression.rightHandSide.getValue());
  }

  @Test
  public void fieldArrayAssignmentCascadeCachesByFieldAndUsesComponentType() throws Exception {
    BlockStatementIndexPair pair = new BlockStatementIndexPair(new BlockStatement(), 0);
    UserField field = new UserField("values", String[].class, new NullLiteral());
    FieldArrayAtIndexAssignmentInsertCascade first = FieldArrayAtIndexAssignmentInsertCascade.getInstance(pair, field);
    FieldArrayAtIndexAssignmentInsertCascade second = FieldArrayAtIndexAssignmentInsertCascade.getInstance(pair, field);
    IntegerLiteral index = new IntegerLiteral(2);
    StringLiteral value = new StringLiteral("rabbit");

    AssignmentExpression expression = invokeCreateAssignmentExpression(first, index, value);
    ArrayAccess leftHandSide = (ArrayAccess) expression.leftHandSide.getValue();

    assertSame(first, second);
    assertSame(JavaType.getInstance(String.class), expression.expressionType.getValue());
    assertTrue(leftHandSide.array.getValue() instanceof FieldAccess);
    assertSame(field, ((FieldAccess) leftHandSide.array.getValue()).field.getValue());
    assertSame(JavaType.getInstance(String[].class), leftHandSide.arrayType.getValue());
    assertSame(JavaType.getInstance(String.class), leftHandSide.getType());
    assertSame(value, expression.rightHandSide.getValue());
  }

  @SuppressWarnings("unchecked")
  private static List<? extends CascadeBlank<Expression>> invokeGetBlanks(Object cascade) throws Exception {
    Method method = org.lgna.croquet.ImmutableCascade.class.getDeclaredMethod("getBlanks");
    method.setAccessible(true);
    return (List<? extends CascadeBlank<Expression>>) method.invoke(cascade);
  }

  private static Object invokeCreateStatement(ReturnStatementInsertCascade cascade, Expression expression) throws Exception {
    Method method = ReturnStatementInsertCascade.class.getDeclaredMethod("createStatement", Expression[].class);
    method.setAccessible(true);
    return method.invoke(cascade, new Object[] {new Expression[] {expression}});
  }

  private static AssignmentExpression invokeCreateAssignmentExpression(AssignmentInsertCascade cascade, Expression... expressions) throws Exception {
    Method method = AssignmentInsertCascade.class.getDeclaredMethod("createExpression", Expression[].class);
    method.setAccessible(true);
    return (AssignmentExpression) method.invoke(cascade, new Object[] {expressions});
  }
}
