package org.alice.ide.ast;

import org.lgna.project.ast.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Extended tests for {@link IncompleteAstUtilities} — covering additional factory
 * methods for deeper line coverage of the utility class and AST node construction.
 */
public class IncompleteAstUtilitiesExtendedTest {

  // ---- assignment expressions ----

  @Test
  public void createIncompleteAssignmentExpressionStatement_noArgs_returnsNonNull() {
    ExpressionStatement stmt = IncompleteAstUtilities.createIncompleteAssignmentExpressionStatement();
    assertNotNull(stmt);
    assertTrue(stmt.expression.getValue() instanceof AssignmentExpression);
  }

  // ---- local assignment ----

  @Test
  public void createIncompleteLocalAssignment_returnsAssignment() {
    UserLocal local = new UserLocal("temp", JavaType.getInstance(Double.class), false);
    AssignmentExpression expr = IncompleteAstUtilities.createIncompleteLocalAssignment(local);
    assertNotNull(expr);
  }

  @Test
  public void createIncompleteLocalAssignmentStatement_returnsStatement() {
    UserLocal local = new UserLocal("temp", JavaType.getInstance(Integer.class), false);
    ExpressionStatement stmt = IncompleteAstUtilities.createIncompleteLocalAssignmentStatement(local);
    assertNotNull(stmt);
  }

  // ---- local array assignment ----

  @Test
  public void createIncompleteLocalArrayAssignment_returnsAssignment() {
    UserLocal local = new UserLocal("arr", JavaType.getInstance(Double[].class), false);
    AssignmentExpression expr = IncompleteAstUtilities.createIncompleteLocalArrayAssignment(local);
    assertNotNull(expr);
  }

  @Test
  public void createIncompleteLocalArrayAssignmentStatement_returnsStatement() {
    UserLocal local = new UserLocal("arr", JavaType.getInstance(Integer[].class), false);
    ExpressionStatement stmt = IncompleteAstUtilities.createIncompleteLocalArrayAssignmentStatement(local);
    assertNotNull(stmt);
  }

  // ---- parameter array assignment ----

  @Test
  public void createIncompleteParameterArrayAssignment_returnsAssignment() {
    UserParameter param = new UserParameter();
    param.name.setValue("items");
    param.valueType.setValue(JavaType.getInstance(String[].class));
    AssignmentExpression expr = IncompleteAstUtilities.createIncompleteParameterArrayAssignment(param);
    assertNotNull(expr);
  }

  @Test
  public void createIncompleteParameterArrayAssignmentStatement_returnsStatement() {
    UserParameter param = new UserParameter();
    param.name.setValue("items");
    param.valueType.setValue(JavaType.getInstance(Object[].class));
    ExpressionStatement stmt = IncompleteAstUtilities.createIncompleteParameterArrayAssignmentStatement(param);
    assertNotNull(stmt);
  }

  // ---- instance creation ----

  @Test
  public void createIncompleteInstanceCreation_zeroParamConstructor() {
    JavaConstructor constructor = JavaConstructor.getInstance(Object.class);
    InstanceCreation ic = IncompleteAstUtilities.createIncompleteInstanceCreation(constructor);
    assertNotNull(ic);
  }

  @Test
  public void createIncompleteInstanceCreation_multiParamConstructor() {
    JavaConstructor constructor = JavaConstructor.getInstance(String.class, byte[].class);
    InstanceCreation ic = IncompleteAstUtilities.createIncompleteInstanceCreation(constructor);
    assertNotNull(ic);
    // should have arguments for each parameter
    assertFalse(ic.requiredArguments.getValue().isEmpty());
  }

  // ---- arithmetic expressions: all operator types ----

  @Test
  public void createArithmeticInfix_allOperators() {
    for (ArithmeticInfixExpression.Operator op : ArithmeticInfixExpression.Operator.values()) {
      ArithmeticInfixExpression expr = IncompleteAstUtilities
          .createIncompleteArithmeticInfixExpression(
              Double.class, op, Double.class, Double.class);
      assertNotNull(op.name() + " should create expression", expr);
      assertEquals(op, expr.operator.getValue());
    }
  }

  // ---- relational expressions: all operator types ----

  @Test
  public void createRelationalInfix_allOperators() {
    for (RelationalInfixExpression.Operator op : RelationalInfixExpression.Operator.values()) {
      RelationalInfixExpression expr = IncompleteAstUtilities
          .createIncompleteRelationalInfixExpression(
              Double.class, op, Double.class);
      assertNotNull(op.name() + " should create expression", expr);
      assertEquals(op, expr.operator.getValue());
    }
  }

  // ---- conditional expressions: both operators ----

  @Test
  public void createConditionalInfix_andOperator() {
    ConditionalInfixExpression expr = IncompleteAstUtilities
        .createIncompleteConditionalInfixExpression(ConditionalInfixExpression.Operator.AND);
    assertEquals(ConditionalInfixExpression.Operator.AND, expr.operator.getValue());
  }

  @Test
  public void createConditionalInfix_orOperator() {
    ConditionalInfixExpression expr = IncompleteAstUtilities
        .createIncompleteConditionalInfixExpression(ConditionalInfixExpression.Operator.OR);
    assertEquals(ConditionalInfixExpression.Operator.OR, expr.operator.getValue());
  }

  // ---- incomplete return with Integer type ----

  @Test
  public void createIncompleteReturnStatement_integerType() {
    ReturnStatement stmt = IncompleteAstUtilities
        .createIncompleteReturnStatement(JavaType.getInstance(Integer.class));
    assertNotNull(stmt);
  }

  // ---- incomplete return with String type ----

  @Test
  public void createIncompleteReturnStatement_stringType() {
    ReturnStatement stmt = IncompleteAstUtilities
        .createIncompleteReturnStatement(JavaType.getInstance(String.class));
    assertNotNull(stmt);
  }

  // ---- static method invocation ----

  @Test
  public void createIncompleteStaticMethodInvocation_returnsMethodInvocation() {
    JavaMethod method = JavaMethod.getInstance(Math.class, "abs", Double.TYPE);
    MethodInvocation mi = IncompleteAstUtilities.createIncompleteStaticMethodInvocation(method);
    assertNotNull(mi);
  }

  @Test
  public void createIncompleteStaticMethodInvocationStatement_returnsStatement() {
    JavaMethod method = JavaMethod.getInstance(Math.class, "abs", Double.TYPE);
    ExpressionStatement stmt = IncompleteAstUtilities.createIncompleteStaticMethodInvocationStatement(method);
    assertNotNull(stmt);
  }
}
