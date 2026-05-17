package org.alice.ide.ast;

import org.lgna.project.ast.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link IncompleteAstUtilities} — static factory methods
 * producing incomplete AST nodes with placeholder expressions.
 */
public class IncompleteAstUtilitiesTest {

  // ---- arithmetic infix expressions ----

  @Test
  public void createIncompleteArithmeticInfixExpression_withClasses() {
    ArithmeticInfixExpression expr = IncompleteAstUtilities
        .createIncompleteArithmeticInfixExpression(
            Double.class, ArithmeticInfixExpression.Operator.PLUS,
            Double.class, Double.class);
    assertNotNull(expr);
    assertEquals(ArithmeticInfixExpression.Operator.PLUS, expr.operator.getValue());
  }

  @Test
  public void createIncompleteArithmeticInfixExpression_withTypes() {
    AbstractType<?, ?, ?> doubleType = JavaType.getInstance(Double.class);
    ArithmeticInfixExpression expr = IncompleteAstUtilities
        .createIncompleteArithmeticInfixExpression(
            doubleType, ArithmeticInfixExpression.Operator.TIMES,
            doubleType, doubleType);
    assertNotNull(expr);
    assertEquals(ArithmeticInfixExpression.Operator.TIMES, expr.operator.getValue());
  }

  @Test
  public void createIncompleteArithmeticInfixExpression_withLeftOperand() {
    Expression left = new DoubleLiteral(3.14);
    ArithmeticInfixExpression expr = IncompleteAstUtilities
        .createIncompleteArithmeticInfixExpression(
            left, ArithmeticInfixExpression.Operator.MINUS,
            Double.class, Double.class);
    assertNotNull(expr);
    assertSame(left, expr.leftOperand.getValue());
  }

  @Test
  public void createIncompleteArithmeticInfixExpression_rightOperandIsEmpty() {
    ArithmeticInfixExpression expr = IncompleteAstUtilities
        .createIncompleteArithmeticInfixExpression(
            Double.class, ArithmeticInfixExpression.Operator.REAL_DIVIDE,
            Double.class, Double.class);
    assertTrue(expr.rightOperand.getValue() instanceof EmptyExpression);
  }

  // ---- conditional infix expressions ----

  @Test
  public void createIncompleteConditionalInfixExpression_noLeftOperand() {
    ConditionalInfixExpression expr = IncompleteAstUtilities
        .createIncompleteConditionalInfixExpression(
            ConditionalInfixExpression.Operator.AND);
    assertNotNull(expr);
    assertEquals(ConditionalInfixExpression.Operator.AND, expr.operator.getValue());
  }

  @Test
  public void createIncompleteConditionalInfixExpression_withLeftOperand() {
    Expression left = new BooleanLiteral(true);
    ConditionalInfixExpression expr = IncompleteAstUtilities
        .createIncompleteConditionalInfixExpression(
            left, ConditionalInfixExpression.Operator.OR);
    assertSame(left, expr.leftOperand.getValue());
  }

  // ---- relational infix expressions ----

  @Test
  public void createIncompleteRelationalInfixExpression_withClasses() {
    RelationalInfixExpression expr = IncompleteAstUtilities
        .createIncompleteRelationalInfixExpression(
            Double.class, RelationalInfixExpression.Operator.LESS,
            Double.class);
    assertNotNull(expr);
    assertEquals(RelationalInfixExpression.Operator.LESS, expr.operator.getValue());
  }

  // ---- logical complement ----

  @Test
  public void createIncompleteLogicalComplement_returnsNonNull() {
    LogicalComplement lc = IncompleteAstUtilities.createIncompleteLogicalComplement();
    assertNotNull(lc);
    assertTrue(lc.operand.getValue() instanceof EmptyExpression);
  }

  // ---- loops and control flow ----

  @Test
  public void createIncompleteCountLoop_returnsNonNull() {
    CountLoop loop = IncompleteAstUtilities.createIncompleteCountLoop();
    assertNotNull(loop);
  }

  @Test
  public void createIncompleteWhileLoop_returnsNonNull() {
    WhileLoop loop = IncompleteAstUtilities.createIncompleteWhileLoop();
    assertNotNull(loop);
  }

  @Test
  public void createIncompleteConditionalStatement_returnsNonNull() {
    ConditionalStatement stmt = IncompleteAstUtilities.createIncompleteConditionalStatement();
    assertNotNull(stmt);
  }

  @Test
  public void createIncompleteForEachInArrayLoop_returnsNonNull() {
    ForEachInArrayLoop loop = IncompleteAstUtilities.createIncompleteForEachInArrayLoop();
    assertNotNull(loop);
  }

  @Test
  public void createIncompleteEachInArrayTogether_returnsNonNull() {
    EachInArrayTogether stmt = IncompleteAstUtilities.createIncompleteEachInArrayTogether();
    assertNotNull(stmt);
  }

  // ---- local declaration ----

  @Test
  public void createIncompleteLocalDeclarationStatement_returnsNonNull() {
    LocalDeclarationStatement stmt = IncompleteAstUtilities.createIncompleteLocalDeclarationStatement();
    assertNotNull(stmt);
  }

  // ---- return statement ----

  @Test
  public void createIncompleteReturnStatement_returnsNonNull() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(Double.class);
    ReturnStatement stmt = IncompleteAstUtilities.createIncompleteReturnStatement(type);
    assertNotNull(stmt);
  }

  // ---- string concatenation ----

  @Test
  public void createIncompleteStringConcatenation_noArgs() {
    StringConcatenation sc = IncompleteAstUtilities.createIncompleteStringConcatenation();
    assertNotNull(sc);
  }

  @Test
  public void createIncompleteStringConcatenation_withLeftOperand() {
    Expression left = new StringLiteral("hello");
    StringConcatenation sc = IncompleteAstUtilities.createIncompleteStringConcatenation(left);
    assertNotNull(sc);
  }
}
