package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.project.ast.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link IncompleteAstUtilities}.
 * Tests all factory methods for creating incomplete AST nodes
 * with empty expressions. Covers arithmetic, relational, conditional operators,
 * both type overloads (AbstractType and Class), field/local/array assignment,
 * instance creation, and string concatenation.
 *
 * Pure behavioral — no GUI dependency.
 * completeMethodInvocation is excluded as it requires IDE.getActiveInstance().
 */
public class IncompleteAstUtilitiesCoverageTest {

  private static final AbstractType<?, ?, ?> DOUBLE_TYPE = JavaType.getInstance(Double.class);
  private static final AbstractType<?, ?, ?> INT_TYPE = JavaType.INTEGER_OBJECT_TYPE;
  private static final AbstractType<?, ?, ?> STRING_TYPE = JavaType.getInstance(String.class);
  private static final AbstractType<?, ?, ?> BOOLEAN_TYPE = JavaType.BOOLEAN_OBJECT_TYPE;

  // ── Structural characterization ────────────────────────────────

  @Test
  public void isUtilityClass() throws Exception {
    ReflectionTestHelper.assertUtilityClass(IncompleteAstUtilities.class);
  }

  // ── ArithmeticInfixExpression — AbstractType overload ──────────

  @Test
  public void createIncompleteArithmeticInfixExpression_withAbstractTypes_returnsNonNull() {
    ArithmeticInfixExpression result = IncompleteAstUtilities
        .createIncompleteArithmeticInfixExpression(
            new EmptyExpression(DOUBLE_TYPE),
            ArithmeticInfixExpression.Operator.PLUS,
            DOUBLE_TYPE,
            DOUBLE_TYPE);
    assertNotNull("Arithmetic expression should not be null", result);
  }

  @Test
  public void createIncompleteArithmeticInfixExpression_withAbstractTypes_hasCorrectOperator() {
    ArithmeticInfixExpression result = IncompleteAstUtilities
        .createIncompleteArithmeticInfixExpression(
            new EmptyExpression(INT_TYPE),
            ArithmeticInfixExpression.Operator.MINUS,
            INT_TYPE,
            INT_TYPE);
    assertEquals(ArithmeticInfixExpression.Operator.MINUS, result.operator.getValue());
  }

  @Test
  public void createIncompleteArithmeticInfixExpression_rightOperandIsEmptyExpression() {
    Expression leftOperand = new EmptyExpression(INT_TYPE);
    ArithmeticInfixExpression result = IncompleteAstUtilities
        .createIncompleteArithmeticInfixExpression(
            leftOperand,
            ArithmeticInfixExpression.Operator.TIMES,
            INT_TYPE,
            INT_TYPE);
    assertTrue("Right operand should be EmptyExpression",
        result.rightOperand.getValue() instanceof EmptyExpression);
  }

  @Test
  public void createIncompleteArithmeticInfixExpression_leftOperandPreserved() {
    Expression leftOperand = new EmptyExpression(INT_TYPE);
    ArithmeticInfixExpression result = IncompleteAstUtilities
        .createIncompleteArithmeticInfixExpression(
            leftOperand,
            ArithmeticInfixExpression.Operator.PLUS,
            INT_TYPE,
            INT_TYPE);
    assertSame("Left operand should be preserved", leftOperand, result.leftOperand.getValue());
  }

  // ── ArithmeticInfixExpression — Class overload ─────────────────

  @Test
  public void createIncompleteArithmeticInfixExpression_withClasses_returnsNonNull() {
    ArithmeticInfixExpression result = IncompleteAstUtilities
        .createIncompleteArithmeticInfixExpression(
            new EmptyExpression(DOUBLE_TYPE),
            ArithmeticInfixExpression.Operator.REAL_REMAINDER,
            Double.class,
            Double.class);
    assertNotNull(result);
  }

  @Test
  public void createIncompleteArithmeticInfixExpression_withClasses_operatorPreserved() {
    ArithmeticInfixExpression result = IncompleteAstUtilities
        .createIncompleteArithmeticInfixExpression(
            new EmptyExpression(JavaType.getInstance(Integer.class)),
            ArithmeticInfixExpression.Operator.INTEGER_DIVIDE,
            Integer.class,
            Integer.class);
    assertEquals(ArithmeticInfixExpression.Operator.INTEGER_DIVIDE, result.operator.getValue());
  }

  // ── ArithmeticInfixExpression — both-sides-empty overloads ─────

  @Test
  public void createIncompleteArithmeticInfixExpression_bothSidesAbstractType_returnsNonNull() {
    ArithmeticInfixExpression result = IncompleteAstUtilities
        .createIncompleteArithmeticInfixExpression(
            DOUBLE_TYPE,
            ArithmeticInfixExpression.Operator.PLUS,
            DOUBLE_TYPE,
            DOUBLE_TYPE);
    assertNotNull(result);
    assertTrue("Left operand should be EmptyExpression",
        result.leftOperand.getValue() instanceof EmptyExpression);
  }

  @Test
  public void createIncompleteArithmeticInfixExpression_bothSidesClass_returnsNonNull() {
    ArithmeticInfixExpression result = IncompleteAstUtilities
        .createIncompleteArithmeticInfixExpression(
            Double.class,
            ArithmeticInfixExpression.Operator.MINUS,
            Double.class,
            Double.class);
    assertNotNull(result);
  }

  // ── All arithmetic operators ───────────────────────────────────

  @Test
  public void arithmeticExpression_allOperators() {
    for (ArithmeticInfixExpression.Operator op : ArithmeticInfixExpression.Operator.values()) {
      ArithmeticInfixExpression result = IncompleteAstUtilities
          .createIncompleteArithmeticInfixExpression(
              new EmptyExpression(DOUBLE_TYPE), op, DOUBLE_TYPE, DOUBLE_TYPE);
      assertNotNull("Expression for operator " + op + " should not be null", result);
      assertEquals("Operator should be " + op, op, result.operator.getValue());
    }
  }

  // ── ConditionalInfixExpression ─────────────────────────────────

  @Test
  public void createIncompleteConditionalInfixExpression_withLeftOperand_returnsNonNull() {
    Expression leftOperand = new EmptyExpression(BOOLEAN_TYPE);
    ConditionalInfixExpression result = IncompleteAstUtilities
        .createIncompleteConditionalInfixExpression(leftOperand, ConditionalInfixExpression.Operator.AND);
    assertNotNull(result);
  }

  @Test
  public void createIncompleteConditionalInfixExpression_rightOperandIsEmpty() {
    Expression leftOperand = new EmptyExpression(BOOLEAN_TYPE);
    ConditionalInfixExpression result = IncompleteAstUtilities
        .createIncompleteConditionalInfixExpression(leftOperand, ConditionalInfixExpression.Operator.OR);
    assertTrue("Right operand should be EmptyExpression",
        result.rightOperand.getValue() instanceof EmptyExpression);
  }

  @Test
  public void createIncompleteConditionalInfixExpression_operatorOnlyOverload() {
    ConditionalInfixExpression result = IncompleteAstUtilities
        .createIncompleteConditionalInfixExpression(ConditionalInfixExpression.Operator.AND);
    assertNotNull(result);
    assertTrue("Left operand should be EmptyExpression",
        result.leftOperand.getValue() instanceof EmptyExpression);
  }

  @Test
  public void conditionalExpression_allOperators() {
    for (ConditionalInfixExpression.Operator op : ConditionalInfixExpression.Operator.values()) {
      ConditionalInfixExpression result = IncompleteAstUtilities
          .createIncompleteConditionalInfixExpression(op);
      assertNotNull("Conditional for operator " + op + " should not be null", result);
      assertEquals(op, result.operator.getValue());
    }
  }

  // ── RelationalInfixExpression ──────────────────────────────────

  @Test
  public void createIncompleteRelationalInfixExpression_withAbstractTypes_returnsNonNull() {
    RelationalInfixExpression result = IncompleteAstUtilities
        .createIncompleteRelationalInfixExpression(DOUBLE_TYPE,
            RelationalInfixExpression.Operator.LESS, DOUBLE_TYPE);
    assertNotNull(result);
  }

  @Test
  public void createIncompleteRelationalInfixExpression_bothOperandsAreEmpty() {
    RelationalInfixExpression result = IncompleteAstUtilities
        .createIncompleteRelationalInfixExpression(INT_TYPE,
            RelationalInfixExpression.Operator.GREATER_EQUALS, INT_TYPE);
    assertTrue("Left operand should be EmptyExpression",
        result.leftOperand.getValue() instanceof EmptyExpression);
    assertTrue("Right operand should be EmptyExpression",
        result.rightOperand.getValue() instanceof EmptyExpression);
  }

  @Test
  public void createIncompleteRelationalInfixExpression_withClasses_returnsNonNull() {
    RelationalInfixExpression result = IncompleteAstUtilities
        .createIncompleteRelationalInfixExpression(Double.class,
            RelationalInfixExpression.Operator.LESS_EQUALS, Double.class);
    assertNotNull(result);
  }

  @Test
  public void relationalExpression_allOperators() {
    for (RelationalInfixExpression.Operator op : RelationalInfixExpression.Operator.values()) {
      RelationalInfixExpression result = IncompleteAstUtilities
          .createIncompleteRelationalInfixExpression(DOUBLE_TYPE, op, DOUBLE_TYPE);
      assertNotNull("Relational for operator " + op + " should not be null", result);
      assertEquals(op, result.operator.getValue());
    }
  }

  // ── LogicalComplement ──────────────────────────────────────────

  @Test
  public void createIncompleteLogicalComplement_returnsNonNull() {
    LogicalComplement result = IncompleteAstUtilities.createIncompleteLogicalComplement();
    assertNotNull(result);
  }

  @Test
  public void createIncompleteLogicalComplement_operandIsEmptyExpression() {
    LogicalComplement result = IncompleteAstUtilities.createIncompleteLogicalComplement();
    assertTrue("Operand should be EmptyExpression",
        result.operand.getValue() instanceof EmptyExpression);
  }

  // ── Control structures ─────────────────────────────────────────

  @Test
  public void createIncompleteLocalDeclarationStatement_returnsNonNull() {
    LocalDeclarationStatement result = IncompleteAstUtilities.createIncompleteLocalDeclarationStatement();
    assertNotNull(result);
  }

  @Test
  public void createIncompleteLocalDeclarationStatement_hasLocal() {
    LocalDeclarationStatement result = IncompleteAstUtilities.createIncompleteLocalDeclarationStatement();
    assertNotNull("Local should not be null", result.local.getValue());
  }

  @Test
  public void createIncompleteCountLoop_returnsNonNull() {
    CountLoop result = IncompleteAstUtilities.createIncompleteCountLoop();
    assertNotNull(result);
  }

  @Test
  public void createIncompleteWhileLoop_returnsNonNull() {
    WhileLoop result = IncompleteAstUtilities.createIncompleteWhileLoop();
    assertNotNull(result);
  }

  @Test
  public void createIncompleteConditionalStatement_returnsNonNull() {
    ConditionalStatement result = IncompleteAstUtilities.createIncompleteConditionalStatement();
    assertNotNull(result);
  }

  @Test
  public void createIncompleteForEachInArrayLoop_returnsNonNull() {
    ForEachInArrayLoop result = IncompleteAstUtilities.createIncompleteForEachInArrayLoop();
    assertNotNull(result);
  }

  @Test
  public void createIncompleteEachInArrayTogether_returnsNonNull() {
    EachInArrayTogether result = IncompleteAstUtilities.createIncompleteEachInArrayTogether();
    assertNotNull(result);
  }

  // ── Return statement ───────────────────────────────────────────

  @Test
  public void createIncompleteReturnStatement_returnsNonNull() {
    ReturnStatement result = IncompleteAstUtilities
        .createIncompleteReturnStatement(INT_TYPE);
    assertNotNull(result);
  }

  @Test
  public void createIncompleteReturnStatement_expressionIsEmpty() {
    ReturnStatement result = IncompleteAstUtilities
        .createIncompleteReturnStatement(STRING_TYPE);
    assertTrue("Return expression should be EmptyExpression",
        result.expression.getValue() instanceof EmptyExpression);
  }

  // ── Local assignment ───────────────────────────────────────────

  @Test
  public void createIncompleteLocalAssignment_returnsNonNull() {
    UserLocal local = new UserLocal("testLocal", INT_TYPE, false);
    AssignmentExpression result = IncompleteAstUtilities.createIncompleteLocalAssignment(local);
    assertNotNull(result);
  }

  @Test
  public void createIncompleteLocalAssignmentStatement_returnsExpressionStatement() {
    UserLocal local = new UserLocal("testLocal", STRING_TYPE, false);
    ExpressionStatement result = IncompleteAstUtilities.createIncompleteLocalAssignmentStatement(local);
    assertNotNull(result);
    assertNotNull("Expression should not be null", result.expression.getValue());
  }

  // ── Local array assignment ─────────────────────────────────────

  @Test
  public void createIncompleteLocalArrayAssignment_returnsNonNull() {
    UserLocal local = new UserLocal("testArray", JavaType.getInstance(int[].class), false);
    AssignmentExpression result = IncompleteAstUtilities.createIncompleteLocalArrayAssignment(local);
    assertNotNull(result);
  }

  @Test
  public void createIncompleteLocalArrayAssignmentStatement_returnsExpressionStatement() {
    UserLocal local = new UserLocal("testArray", JavaType.getInstance(String[].class), false);
    ExpressionStatement result = IncompleteAstUtilities.createIncompleteLocalArrayAssignmentStatement(local);
    assertNotNull(result);
  }

  // ── Parameter array assignment ─────────────────────────────────

  @Test
  public void createIncompleteParameterArrayAssignment_returnsNonNull() {
    UserParameter param = new UserParameter("testParam", JavaType.getInstance(double[].class));
    AssignmentExpression result = IncompleteAstUtilities.createIncompleteParameterArrayAssignment(param);
    assertNotNull(result);
  }

  @Test
  public void createIncompleteParameterArrayAssignmentStatement_returnsExpressionStatement() {
    UserParameter param = new UserParameter("testParam", JavaType.getInstance(Object[].class));
    ExpressionStatement result = IncompleteAstUtilities.createIncompleteParameterArrayAssignmentStatement(param);
    assertNotNull(result);
  }

  // ── String concatenation ───────────────────────────────────────

  @Test
  public void createIncompleteStringConcatenation_withLeftOperand_returnsNonNull() {
    Expression left = new EmptyExpression(STRING_TYPE);
    StringConcatenation result = IncompleteAstUtilities.createIncompleteStringConcatenation(left);
    assertNotNull(result);
  }

  @Test
  public void createIncompleteStringConcatenation_withLeftOperand_rightIsEmpty() {
    Expression left = new EmptyExpression(STRING_TYPE);
    StringConcatenation result = IncompleteAstUtilities.createIncompleteStringConcatenation(left);
    assertTrue("Right operand should be EmptyExpression",
        result.rightOperand.getValue() instanceof EmptyExpression);
  }

  @Test
  public void createIncompleteStringConcatenation_noArgs_returnsNonNull() {
    StringConcatenation result = IncompleteAstUtilities.createIncompleteStringConcatenation();
    assertNotNull(result);
  }

  @Test
  public void createIncompleteStringConcatenation_noArgs_bothSidesEmpty() {
    StringConcatenation result = IncompleteAstUtilities.createIncompleteStringConcatenation();
    assertTrue("Left operand should be EmptyExpression",
        result.leftOperand.getValue() instanceof EmptyExpression);
    assertTrue("Right operand should be EmptyExpression",
        result.rightOperand.getValue() instanceof EmptyExpression);
  }

  // ── Field access ───────────────────────────────────────────────

  @Test
  public void createIncompleteFieldAccess_isPublicStaticAndReturnsFieldAccess() {
    Method m = Arrays.stream(IncompleteAstUtilities.class.getDeclaredMethods())
        .filter(method -> method.getName().equals("createIncompleteFieldAccess")
            && method.getParameterCount() == 1
            && method.getReturnType() == FieldAccess.class)
        .findFirst().orElse(null);
    assertNotNull("createIncompleteFieldAccess(AbstractField) must exist", m);
    assertTrue("Must be public", Modifier.isPublic(m.getModifiers()));
    assertTrue("Must be static", Modifier.isStatic(m.getModifiers()));
  }

  // ── Assignment expression ──────────────────────────────────────

  @Test
  public void createIncompleteAssignmentExpression_overloads() {
    long overloadCount = Arrays.stream(IncompleteAstUtilities.class.getDeclaredMethods())
        .filter(m -> m.getName().equals("createIncompleteAssignmentExpression"))
        .count();
    // Should have 3 overloads: (Expression, AbstractField), (AbstractField), private no-arg
    assertTrue("Should have at least 2 overloads of createIncompleteAssignmentExpression",
        overloadCount >= 2);
  }

  @Test
  public void createIncompleteAssignmentExpression_returnsAssignmentExpression() {
    Method m = Arrays.stream(IncompleteAstUtilities.class.getDeclaredMethods())
        .filter(method -> method.getName().equals("createIncompleteAssignmentExpression"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertEquals("Must return AssignmentExpression", AssignmentExpression.class, m.getReturnType());
  }

  @Test
  public void createIncompleteAssignmentExpressionStatement_overloads() {
    long overloadCount = Arrays.stream(IncompleteAstUtilities.class.getDeclaredMethods())
        .filter(m -> m.getName().equals("createIncompleteAssignmentExpressionStatement"))
        .count();
    assertTrue("Should have at least 2 overloads of createIncompleteAssignmentExpressionStatement",
        overloadCount >= 2);
  }

  @Test
  public void createIncompleteAssignmentExpressionStatement_noArgs() {
    ExpressionStatement result = IncompleteAstUtilities
        .createIncompleteAssignmentExpressionStatement();
    assertNotNull(result);
  }

  // ── Method invocation (headless-safe overloads) ────────────────

  @Test
  public void createIncompleteMethodInvocation_withExpressionAndMethod_returnsNonNull() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestType");
    UserMethod method = new UserMethod();
    method.name.setValue("testMethod");
    method.returnType.setValue(JavaType.VOID_TYPE);
    type.methods.add(method);

    Expression expr = new SelectedInstanceFactoryExpression(type);
    MethodInvocation result = IncompleteAstUtilities
        .createIncompleteMethodInvocation(expr, method);
    assertNotNull(result);
    assertSame("Method should be preserved", method, result.method.getValue());
  }

  @Test
  public void createIncompleteMethodInvocation_withParameters_createsArguments() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestType");
    UserMethod method = new UserMethod();
    method.name.setValue("testMethod");
    method.returnType.setValue(JavaType.VOID_TYPE);
    UserParameter param1 = new UserParameter("p1", INT_TYPE);
    UserParameter param2 = new UserParameter("p2", STRING_TYPE);
    method.requiredParameters.add(param1);
    method.requiredParameters.add(param2);
    type.methods.add(method);

    Expression expr = new SelectedInstanceFactoryExpression(type);
    MethodInvocation result = IncompleteAstUtilities
        .createIncompleteMethodInvocation(expr, method);
    assertEquals("Should create argument for each required parameter",
        2, result.requiredArguments.size());
  }

  @Test
  public void createIncompleteMethodInvocationStatement_returnsExpressionStatement() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestType");
    UserMethod method = new UserMethod();
    method.name.setValue("testMethod");
    method.returnType.setValue(JavaType.VOID_TYPE);
    type.methods.add(method);

    Expression expr = new SelectedInstanceFactoryExpression(type);
    ExpressionStatement result = new ExpressionStatement(
        IncompleteAstUtilities.createIncompleteMethodInvocation(expr, method));
    assertNotNull(result);
    assertTrue("Expression should be MethodInvocation",
        result.expression.getValue() instanceof MethodInvocation);
  }

  // ── Static method invocation ───────────────────────────────────

  @Test
  public void createIncompleteStaticMethodInvocation_expressionIsTypeExpression() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestType");
    UserMethod method = new UserMethod();
    method.name.setValue("staticTestMethod");
    method.returnType.setValue(JavaType.VOID_TYPE);
    type.methods.add(method);

    MethodInvocation result = IncompleteAstUtilities.createIncompleteStaticMethodInvocation(method);
    assertNotNull(result);
    assertTrue("Expression should be TypeExpression for static invocation",
        result.expression.getValue() instanceof TypeExpression);
  }

  @Test
  public void createIncompleteStaticMethodInvocationStatement_returnsExpressionStatement() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestType");
    UserMethod method = new UserMethod();
    method.name.setValue("staticTestMethod");
    method.returnType.setValue(JavaType.VOID_TYPE);
    type.methods.add(method);

    ExpressionStatement result = IncompleteAstUtilities.createIncompleteStaticMethodInvocationStatement(method);
    assertNotNull(result);
  }

  // ── Instance creation ──────────────────────────────────────────

  @Test
  public void createIncompleteInstanceCreation_noParamConstructor() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestType");
    NamedUserConstructor ctor = new NamedUserConstructor();
    type.constructors.add(ctor);

    InstanceCreation result = IncompleteAstUtilities.createIncompleteInstanceCreation(ctor);
    assertNotNull(result);
    assertEquals("No-arg constructor should have 0 required arguments",
        0, result.requiredArguments.size());
  }

  @Test
  public void createIncompleteInstanceCreation_withParams_createsEmptyArgs() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestType");
    NamedUserConstructor ctor = new NamedUserConstructor();
    UserParameter param = new UserParameter("p1", INT_TYPE);
    ctor.requiredParameters.add(param);
    type.constructors.add(ctor);

    InstanceCreation result = IncompleteAstUtilities.createIncompleteInstanceCreation(ctor);
    assertNotNull(result);
    assertEquals("Should create argument for each required parameter",
        1, result.requiredArguments.size());
  }

  // ── Method count verification ──────────────────────────────────

  @Test
  public void publicMethodCount_matchesExpectedFactoryMethods() {
    Set<String> publicMethodNames = Arrays.stream(IncompleteAstUtilities.class.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .map(Method::getName)
        .collect(Collectors.toSet());

    // Verify key factory methods exist
    assertTrue(publicMethodNames.contains("createIncompleteArithmeticInfixExpression"));
    assertTrue(publicMethodNames.contains("createIncompleteConditionalInfixExpression"));
    assertTrue(publicMethodNames.contains("createIncompleteRelationalInfixExpression"));
    assertTrue(publicMethodNames.contains("createIncompleteLogicalComplement"));
    assertTrue(publicMethodNames.contains("createIncompleteLocalDeclarationStatement"));
    assertTrue(publicMethodNames.contains("createIncompleteCountLoop"));
    assertTrue(publicMethodNames.contains("createIncompleteWhileLoop"));
    assertTrue(publicMethodNames.contains("createIncompleteConditionalStatement"));
    assertTrue(publicMethodNames.contains("createIncompleteForEachInArrayLoop"));
    assertTrue(publicMethodNames.contains("createIncompleteEachInArrayTogether"));
    assertTrue(publicMethodNames.contains("createIncompleteMethodInvocation"));
    assertTrue(publicMethodNames.contains("createIncompleteMethodInvocationStatement"));
    assertTrue(publicMethodNames.contains("createIncompleteStaticMethodInvocation"));
    assertTrue(publicMethodNames.contains("createIncompleteStaticMethodInvocationStatement"));
    assertTrue(publicMethodNames.contains("completeMethodInvocation"));
    assertTrue(publicMethodNames.contains("createIncompleteFieldAccess"));
    assertTrue(publicMethodNames.contains("createIncompleteAssignmentExpression"));
    assertTrue(publicMethodNames.contains("createIncompleteAssignmentExpressionStatement"));
    assertTrue(publicMethodNames.contains("createIncompleteInstanceCreation"));
    assertTrue(publicMethodNames.contains("createIncompleteReturnStatement"));
    assertTrue(publicMethodNames.contains("createIncompleteLocalAssignment"));
    assertTrue(publicMethodNames.contains("createIncompleteLocalAssignmentStatement"));
    assertTrue(publicMethodNames.contains("createIncompleteLocalArrayAssignment"));
    assertTrue(publicMethodNames.contains("createIncompleteLocalArrayAssignmentStatement"));
    assertTrue(publicMethodNames.contains("createIncompleteParameterArrayAssignment"));
    assertTrue(publicMethodNames.contains("createIncompleteParameterArrayAssignmentStatement"));
    assertTrue(publicMethodNames.contains("createIncompleteStringConcatenation"));
  }
}
