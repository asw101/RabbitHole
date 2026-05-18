package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class IncompleteAstUtilitiesTest {
  @Test
  public void createIncompleteArithmeticInfixExpression_withClasses() {
    ArithmeticInfixExpression expr = IncompleteAstUtilities.createIncompleteArithmeticInfixExpression(
        Double.class, ArithmeticInfixExpression.Operator.PLUS, Double.class, Double.class);
    assertNotNull(expr);
  }

  @Test
  public void createIncompleteArithmeticInfixExpression_withTypes() {
    AbstractType<?, ?, ?> doubleType = JavaType.getInstance(Double.class);
    ArithmeticInfixExpression expr = IncompleteAstUtilities.createIncompleteArithmeticInfixExpression(
        doubleType, ArithmeticInfixExpression.Operator.MINUS, doubleType, doubleType);
    assertNotNull(expr);
  }

  @Test
  public void createIncompleteArithmeticInfixExpression_withLeftOperand() {
    Expression leftOperand = new IntegerLiteral(42);
    ArithmeticInfixExpression expr = IncompleteAstUtilities.createIncompleteArithmeticInfixExpression(
        leftOperand, ArithmeticInfixExpression.Operator.TIMES, Integer.class, Integer.class);
    assertNotNull(expr);
  }

  @Test
  public void createIncompleteConditionalInfixExpression_withOperator() {
    ConditionalInfixExpression expr = IncompleteAstUtilities.createIncompleteConditionalInfixExpression(
        ConditionalInfixExpression.Operator.AND);
    assertNotNull(expr);
  }

  @Test
  public void createIncompleteConditionalInfixExpression_withLeftOperand() {
    Expression left = new BooleanLiteral(true);
    ConditionalInfixExpression expr = IncompleteAstUtilities.createIncompleteConditionalInfixExpression(
        left, ConditionalInfixExpression.Operator.OR);
    assertNotNull(expr);
  }

  @Test
  public void createIncompleteRelationalInfixExpression_withClasses() {
    RelationalInfixExpression expr = IncompleteAstUtilities.createIncompleteRelationalInfixExpression(
        Double.class, RelationalInfixExpression.Operator.LESS, Double.class);
    assertNotNull(expr);
  }

  @Test
  public void createIncompleteLogicalComplement() {
    LogicalComplement lc = IncompleteAstUtilities.createIncompleteLogicalComplement();
    assertNotNull(lc);
  }

  @Test
  public void createIncompleteLocalDeclarationStatement() {
    LocalDeclarationStatement stmt = IncompleteAstUtilities.createIncompleteLocalDeclarationStatement();
    assertNotNull(stmt);
  }

  @Test
  public void createIncompleteCountLoop() {
    CountLoop loop = IncompleteAstUtilities.createIncompleteCountLoop();
    assertNotNull(loop);
  }

  @Test
  public void createIncompleteWhileLoop() {
    WhileLoop loop = IncompleteAstUtilities.createIncompleteWhileLoop();
    assertNotNull(loop);
  }

  @Test
  public void createIncompleteConditionalStatement() {
    ConditionalStatement stmt = IncompleteAstUtilities.createIncompleteConditionalStatement();
    assertNotNull(stmt);
  }

  @Test
  public void createIncompleteForEachInArrayLoop() {
    ForEachInArrayLoop loop = IncompleteAstUtilities.createIncompleteForEachInArrayLoop();
    assertNotNull(loop);
  }

  @Test
  public void createIncompleteEachInArrayTogether() {
    EachInArrayTogether stmt = IncompleteAstUtilities.createIncompleteEachInArrayTogether();
    assertNotNull(stmt);
  }

  @Test
  public void createIncompleteReturnStatement() {
    ReturnStatement stmt = IncompleteAstUtilities.createIncompleteReturnStatement(JavaType.getInstance(String.class));
    assertNotNull(stmt);
  }

  @Test
  public void createIncompleteInstanceCreation() {
    JavaType stringType = JavaType.getInstance(String.class);
    AbstractConstructor ctor = stringType.getDeclaredConstructors().get(0);
    InstanceCreation ic = IncompleteAstUtilities.createIncompleteInstanceCreation(ctor);
    assertNotNull(ic);
  }

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

  @Test
  public void createIncompleteLocalAssignment() {
    UserLocal local = new UserLocal("x", JavaType.getInstance(Integer.class), false);
    AssignmentExpression ae = IncompleteAstUtilities.createIncompleteLocalAssignment(local);
    assertNotNull(ae);
  }

  @Test
  public void createIncompleteLocalAssignmentStatement() {
    UserLocal local = new UserLocal("y", JavaType.getInstance(String.class), false);
    ExpressionStatement stmt = IncompleteAstUtilities.createIncompleteLocalAssignmentStatement(local);
    assertNotNull(stmt);
  }

  @Test
  public void createIncompleteFieldAccess() {
    JavaField field = JavaField.getInstance(String.class, "CASE_INSENSITIVE_ORDER");
    FieldAccess fa = IncompleteAstUtilities.createIncompleteFieldAccess(field);
    assertNotNull(fa);
  }

  @Test
  public void createIncompleteAssignmentExpressionStatement_noArgs() {
    ExpressionStatement stmt = IncompleteAstUtilities.createIncompleteAssignmentExpressionStatement();
    assertNotNull(stmt);
  }
}
