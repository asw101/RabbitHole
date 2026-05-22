package org.alice.ide.ast;

import org.lgna.project.ast.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Deep coverage tests for {@link IncompleteAstUtilities} — exercising methods
 * not covered by IncompleteAstUtilitiesTest or IncompleteAstUtilitiesExtendedTest.
 */
public class IncompleteAstUtilitiesBranchCoverageTest {

  // ---- arithmetic infix with left operand and AbstractType ----

  @Test
  public void createArithmeticInfix_withLeftExprAndTypes_hasCorrectOperator() {
    Expression left = new IntegerLiteral(5);
    ArithmeticInfixExpression expr = IncompleteAstUtilities
        .createIncompleteArithmeticInfixExpression(
            left, ArithmeticInfixExpression.Operator.REAL_REMAINDER,
            JavaType.getInstance(Double.class), JavaType.getInstance(Double.class));
    assertNotNull(expr);
    assertEquals(ArithmeticInfixExpression.Operator.REAL_REMAINDER, expr.operator.getValue());
    assertSame(left, expr.leftOperand.getValue());
    assertTrue(expr.rightOperand.getValue() instanceof EmptyExpression);
  }

  // ---- conditional infix with explicit left operand ----

  @Test
  public void createConditionalInfix_withLeftOperand_orOperator_preservesLeft() {
    Expression left = new BooleanLiteral(false);
    ConditionalInfixExpression expr = IncompleteAstUtilities
        .createIncompleteConditionalInfixExpression(left, ConditionalInfixExpression.Operator.OR);
    assertSame(left, expr.leftOperand.getValue());
    assertEquals(ConditionalInfixExpression.Operator.OR, expr.operator.getValue());
    assertTrue(expr.rightOperand.getValue() instanceof EmptyExpression);
  }

  // ---- relational infix with AbstractType overload ----

  @Test
  public void createRelationalInfix_withAbstractTypes_greaterEquals() {
    RelationalInfixExpression expr = IncompleteAstUtilities
        .createIncompleteRelationalInfixExpression(
            JavaType.getInstance(Integer.class),
            RelationalInfixExpression.Operator.GREATER_EQUALS,
            JavaType.getInstance(Integer.class));
    assertNotNull(expr);
    assertEquals(RelationalInfixExpression.Operator.GREATER_EQUALS, expr.operator.getValue());
  }

  // ---- logical complement details ----

  @Test
  public void logicalComplement_operandIsBooleanEmpty() {
    LogicalComplement lc = IncompleteAstUtilities.createIncompleteLogicalComplement();
    Expression operand = lc.operand.getValue();
    assertTrue(operand instanceof EmptyExpression);
  }

  // ---- count loop has non-null body ----

  @Test
  public void countLoop_hasBody() {
    CountLoop loop = IncompleteAstUtilities.createIncompleteCountLoop();
    assertNotNull(loop);
    assertNotNull(loop.body.getValue());
  }

  // ---- while loop has non-null body ----

  @Test
  public void whileLoop_hasBody() {
    WhileLoop loop = IncompleteAstUtilities.createIncompleteWhileLoop();
    assertNotNull(loop);
    assertNotNull(loop.body.getValue());
  }

  // ---- conditional statement structure ----

  @Test
  public void conditionalStatement_hasBooleanExpressionBodyPair() {
    ConditionalStatement stmt = IncompleteAstUtilities.createIncompleteConditionalStatement();
    assertNotNull(stmt);
    assertFalse("Should have at least one condition-body pair",
        stmt.booleanExpressionBodyPairs.getValue().isEmpty());
  }

  // ---- forEachInArrayLoop has body ----

  @Test
  public void forEachInArrayLoop_hasBody() {
    ForEachInArrayLoop loop = IncompleteAstUtilities.createIncompleteForEachInArrayLoop();
    assertNotNull(loop);
    assertNotNull(loop.body.getValue());
  }

  // ---- eachInArrayTogether has body ----

  @Test
  public void eachInArrayTogether_hasBody() {
    EachInArrayTogether stmt = IncompleteAstUtilities.createIncompleteEachInArrayTogether();
    assertNotNull(stmt);
    assertNotNull(stmt.body.getValue());
  }

  // ---- local declaration details ----

  @Test
  public void localDeclaration_localHasPlaceholderName() {
    LocalDeclarationStatement stmt = IncompleteAstUtilities.createIncompleteLocalDeclarationStatement();
    UserLocal local = stmt.local.getValue();
    assertNotNull(local);
    assertEquals("???", local.getName());
  }

  @Test
  public void localDeclaration_initializerIsEmpty() {
    LocalDeclarationStatement stmt = IncompleteAstUtilities.createIncompleteLocalDeclarationStatement();
    Expression init = stmt.initializer.getValue();
    assertTrue(init instanceof EmptyExpression);
  }

  // ---- method invocation with a known Java method ----

  @Test
  public void createIncompleteMethodInvocation_withExpressionAndMethod() {
    JavaMethod method = JavaMethod.getInstance(String.class, "substring", Integer.TYPE);
    Expression expr = new StringLiteral("hello");
    MethodInvocation mi = IncompleteAstUtilities.createIncompleteMethodInvocation(expr, method);
    assertNotNull(mi);
    assertSame(expr, mi.expression.getValue());
    assertSame(method, mi.method.getValue());
    assertFalse("Should have arguments for required params",
        mi.requiredArguments.getValue().isEmpty());
  }

  @Test
  public void createIncompleteMethodInvocationStatement_returnsExpressionStatement() {
    JavaMethod method = JavaMethod.getInstance(String.class, "charAt", Integer.TYPE);
    ExpressionStatement stmt = IncompleteAstUtilities.createIncompleteMethodInvocationStatement(method);
    assertNotNull(stmt);
    assertTrue(stmt.expression.getValue() instanceof MethodInvocation);
  }

  // ---- field access ----

  @Test
  public void createIncompleteFieldAccess_returnsFieldAccess() {
    JavaField field = JavaField.getInstance(System.class, "out");
    FieldAccess fa = IncompleteAstUtilities.createIncompleteFieldAccess(field);
    assertNotNull(fa);
    assertSame(field, fa.field.getValue());
  }

  // ---- assignment expression with expression and field ----

  @Test
  public void createIncompleteAssignmentExpression_withExprAndField_returnsAssignment() {
    UserField userField = new UserField();
    userField.name.setValue("count");
    userField.valueType.setValue(JavaType.getInstance(Integer.class));
    userField.initializer.setValue(new NullLiteral());

    NamedUserType type = new NamedUserType();
    type.name.setValue("Host");
    type.superType.setValue(JavaType.getInstance(Object.class));
    type.fields.add(userField);

    Expression thisExpr = new ThisExpression();
    AssignmentExpression assignment = IncompleteAstUtilities.createIncompleteAssignmentExpression(thisExpr, userField);
    assertNotNull(assignment);
    assertEquals(AssignmentExpression.Operator.ASSIGN, assignment.operator.getValue());
  }

  // ---- assignment expression statement with expression and field ----

  @Test
  public void createIncompleteAssignmentExpressionStatement_withExprAndField() {
    UserField userField = new UserField();
    userField.name.setValue("value");
    userField.valueType.setValue(JavaType.getInstance(Double.class));
    userField.initializer.setValue(new NullLiteral());

    NamedUserType type = new NamedUserType();
    type.name.setValue("Container");
    type.superType.setValue(JavaType.getInstance(Object.class));
    type.fields.add(userField);

    ExpressionStatement stmt = IncompleteAstUtilities.createIncompleteAssignmentExpressionStatement(
        new ThisExpression(), userField);
    assertNotNull(stmt);
    assertTrue(stmt.expression.getValue() instanceof AssignmentExpression);
  }

  // ---- return statement with void type ----

  @Test
  public void createIncompleteReturnStatement_voidType_returnsNonNull() {
    ReturnStatement stmt = IncompleteAstUtilities.createIncompleteReturnStatement(JavaType.VOID_TYPE);
    assertNotNull(stmt);
  }

  // ---- string concatenation with literal left ----

  @Test
  public void stringConcatenation_withLeft_rightIsEmpty() {
    StringLiteral left = new StringLiteral("prefix");
    StringConcatenation sc = IncompleteAstUtilities.createIncompleteStringConcatenation(left);
    assertNotNull(sc);
    assertSame(left, sc.leftOperand.getValue());
    assertTrue(sc.rightOperand.getValue() instanceof EmptyExpression);
  }

  // ---- string concatenation no-arg ----

  @Test
  public void stringConcatenation_noArgs_bothOperandsAreEmpty() {
    StringConcatenation sc = IncompleteAstUtilities.createIncompleteStringConcatenation();
    assertNotNull(sc);
    assertTrue(sc.leftOperand.getValue() instanceof EmptyExpression);
    assertTrue(sc.rightOperand.getValue() instanceof EmptyExpression);
  }
}
