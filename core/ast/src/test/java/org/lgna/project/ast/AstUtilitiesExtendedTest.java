package org.lgna.project.ast;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class AstUtilitiesExtendedTest {

  @Test
  public void createMethodCreatesNamedUserMethodWithEmptyBody() {
    UserMethod method = AstUtilities.createMethod("answer", JavaType.OBJECT_TYPE);

    assertNotNull(method);
    assertEquals("answer", method.getName());
    assertSame(JavaType.OBJECT_TYPE, method.returnType.getValue());
    assertEquals(0, method.requiredParameters.size());
    assertNotNull(method.body.getValue());
    assertEquals(0, method.body.getValue().statements.size());
  }

  @Test
  public void createFunctionWithTypeDelegatesToMethodStructure() {
    AbstractType<?, ?, ?> returnType = JavaType.getInstance(String.class);

    UserMethod method = AstUtilities.createFunction("format", returnType);

    assertEquals("format", method.getName());
    assertSame(returnType, method.returnType.getValue());
    assertNotNull(method.body.getValue());
  }

  @Test
  public void createFunctionWithClassUsesJavaType() {
    UserMethod method = AstUtilities.createFunction("count", Integer.class);

    assertEquals("count", method.getName());
    assertEquals(JavaType.getInstance(Integer.class), method.returnType.getValue());
  }

  @Test
  public void createProcedureUsesVoidReturnType() {
    UserMethod method = AstUtilities.createProcedure("run");

    assertEquals("run", method.getName());
    assertSame(JavaType.VOID_TYPE, method.returnType.getValue());
    assertEquals(0, method.requiredParameters.size());
  }

  @Test
  public void createTypeCreatesNamedUserTypeWithConstructor() {
    AbstractType<?, ?, ?> superType = JavaType.getInstance(Object.class);

    NamedUserType type = AstUtilities.createType("SampleType", superType);

    assertNotNull(type);
    assertEquals("SampleType", type.getName());
    assertSame(superType, type.superType.getValue());
    assertEquals(1, type.constructors.size());
    assertTrue(type.constructors.get(0).body.getValue() instanceof ConstructorBlockStatement);
  }

  @Test
  public void createDoInOrderWrapsEmptyBlockStatement() {
    DoInOrder statement = AstUtilities.createDoInOrder();

    assertNotNull(statement.body.getValue());
    assertEquals(0, statement.body.getValue().statements.size());
  }

  @Test
  public void createDoTogetherWrapsEmptyBlockStatement() {
    DoTogether statement = AstUtilities.createDoTogether();

    assertNotNull(statement.body.getValue());
    assertEquals(0, statement.body.getValue().statements.size());
  }

  @Test
  public void createCommentCreatesEmptyTextComment() {
    Comment comment = AstUtilities.createComment();

    assertNotNull(comment);
    assertEquals("", comment.text.getValue());
  }

  @Test
  public void createLocalDeclarationStatementStoresLocalAndInitializer() {
    UserLocal local = mutableStringLocal();
    Expression initializer = stringLiteral();

    LocalDeclarationStatement statement = AstUtilities.createLocalDeclarationStatement(local, initializer);

    assertSame(local, statement.local.getValue());
    assertSame(initializer, statement.initializer.getValue());
  }

  @Test
  public void createCountLoopUsesIntegerLocalsAndBody() {
    Expression count = intLiteral();

    CountLoop loop = AstUtilities.createCountLoop(count);

    assertSame(count, loop.count.getValue());
    assertNotNull(loop.body.getValue());
    assertEquals(0, loop.body.getValue().statements.size());
    assertEquals(JavaType.INTEGER_OBJECT_TYPE, loop.variable.getValue().getValueType());
    assertFalse(loop.variable.getValue().isFinal.getValue());
    assertEquals(JavaType.INTEGER_OBJECT_TYPE, loop.constant.getValue().getValueType());
    assertTrue(loop.constant.getValue().isFinal.getValue());
  }

  @Test
  public void createWhileLoopStoresConditional() {
    Expression conditional = booleanLiteral();

    WhileLoop loop = AstUtilities.createWhileLoop(conditional);

    assertSame(conditional, loop.conditional.getValue());
    assertNotNull(loop.body.getValue());
    assertEquals(0, loop.body.getValue().statements.size());
  }

  @Test
  public void createConditionalStatementCreatesSinglePairAndElseBody() {
    Expression conditional = booleanLiteral();

    ConditionalStatement statement = AstUtilities.createConditionalStatement(conditional);

    assertEquals(1, statement.booleanExpressionBodyPairs.size());
    assertSame(conditional, statement.booleanExpressionBodyPairs.get(0).expression.getValue());
    assertNotNull(statement.booleanExpressionBodyPairs.get(0).body.getValue());
    assertNotNull(statement.elseBody.getValue());
    assertEquals(0, statement.elseBody.getValue().statements.size());
  }

  @Test
  public void isKeywordExpressionReturnsFalseForNull() {
    assertFalse(AstUtilities.isKeywordExpression(null));
  }

  @Test
  public void isKeywordExpressionReturnsFalseForNonKeyedParent() {
    UserParameter parameter = new UserParameter("value", String.class);
    Expression expression = stringLiteral();
    new SimpleArgument(parameter, expression);

    assertFalse(AstUtilities.isKeywordExpression(expression));
  }

  @Test
  public void isKeywordExpressionReturnsTrueForJavaKeyedArgumentParent() {
    UserParameter parameter = new UserParameter("values", String[].class);
    JavaMethod keyMethod = JavaMethod.getInstance(String.class, "valueOf", Object.class);
    JavaKeyedArgument argument = new JavaKeyedArgument(parameter, keyMethod, nullLiteral());

    assertTrue(AstUtilities.isKeywordExpression(argument.expression.getValue()));
  }

  @Test
  public void createTypeExpressionFromTypeWrapsValue() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);

    TypeExpression expression = AstUtilities.createTypeExpression(type);

    assertSame(type, expression.value.getValue());
    assertEquals(JavaType.getInstance(Class.class), expression.getType());
  }

  @Test
  public void createTypeExpressionFromClassConvertsToJavaType() {
    TypeExpression expression = AstUtilities.createTypeExpression(Integer.class);

    assertEquals(JavaType.getInstance(Integer.class), expression.value.getValue());
  }

  @Test
  public void createStaticMethodInvocationUsesTypeExpressionReceiver() {
    JavaMethod method = JavaMethod.getInstance(String.class, "valueOf", Object.class);
    Expression argument = nullLiteral();

    MethodInvocation invocation = AstUtilities.createStaticMethodInvocation(method, argument);

    assertSame(method, invocation.method.getValue());
    assertTrue(invocation.expression.getValue() instanceof TypeExpression);
    assertEquals(method.getDeclaringType(), ((TypeExpression) invocation.expression.getValue()).value.getValue());
    assertEquals(1, invocation.requiredArguments.size());
    assertSame(argument, invocation.requiredArguments.get(0).expression.getValue());
  }

  @Test
  public void createMethodInvocationUsesInstanceAndArguments() {
    Expression instance = stringLiteral();
    JavaMethod method = JavaMethod.getInstance(String.class, "concat", String.class);
    Expression argument = new StringLiteral(" world");

    MethodInvocation invocation = AstUtilities.createMethodInvocation(instance, method, argument);

    assertSame(instance, invocation.expression.getValue());
    assertSame(method, invocation.method.getValue());
    assertEquals(1, invocation.requiredArguments.size());
    assertSame(argument, invocation.requiredArguments.get(0).expression.getValue());
  }

  @Test
  public void createMethodInvocationStatementWrapsInvocation() {
    Expression instance = stringLiteral();
    JavaMethod method = JavaMethod.getInstance(String.class, "concat", String.class);
    Expression argument = new StringLiteral("!");

    ExpressionStatement statement = AstUtilities.createMethodInvocationStatement(instance, method, argument);

    assertTrue(statement.expression.getValue() instanceof MethodInvocation);
    MethodInvocation invocation = (MethodInvocation) statement.expression.getValue();
    assertSame(instance, invocation.expression.getValue());
    assertSame(method, invocation.method.getValue());
    assertSame(argument, invocation.requiredArguments.get(0).expression.getValue());
  }

  @Test
  public void createStaticFieldAccessFromAbstractFieldUsesTypeExpression() {
    JavaField field = JavaField.getInstance(Integer.class, "MAX_VALUE");

    FieldAccess access = AstUtilities.createStaticFieldAccess(field);

    assertSame(field, access.field.getValue());
    assertTrue(access.expression.getValue() instanceof TypeExpression);
    assertEquals(field.getDeclaringType(), ((TypeExpression) access.expression.getValue()).value.getValue());
  }

  @Test
  public void createStaticFieldAccessFromReflectionFieldWrapsJavaField() throws Exception {
    Field reflectionField = Integer.class.getDeclaredField("MAX_VALUE");

    FieldAccess access = AstUtilities.createStaticFieldAccess(reflectionField);

    assertEquals("MAX_VALUE", access.field.getValue().getName());
    assertEquals(JavaType.getInstance(Integer.class), access.field.getValue().getDeclaringType());
    assertTrue(access.expression.getValue() instanceof TypeExpression);
  }

  @Test
  public void createStaticFieldAccessFromClassAndNameFindsField() {
    FieldAccess access = AstUtilities.createStaticFieldAccess(Boolean.class, "TRUE");

    assertEquals("TRUE", access.field.getValue().getName());
    assertEquals(JavaType.getInstance(Boolean.class), access.field.getValue().getDeclaringType());
  }

  @Test
  public void createInstanceCreationFromConstructorUsesArguments() {
    JavaConstructor constructor = JavaConstructor.getInstance(StringBuilder.class, String.class);
    Expression argument = stringLiteral();

    InstanceCreation creation = AstUtilities.createInstanceCreation(constructor, argument);

    assertSame(constructor, creation.constructor.getValue());
    assertEquals(1, creation.requiredArguments.size());
    assertSame(argument, creation.requiredArguments.get(0).expression.getValue());
    assertEquals(JavaType.getInstance(StringBuilder.class), creation.getType());
  }

  @Test
  public void createInstanceCreationFromTypeUsesDeclaredConstructor() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(ArrayList.class);

    InstanceCreation creation = AstUtilities.createInstanceCreation(type);

    assertEquals(type, creation.getType());
    assertEquals(0, creation.requiredArguments.size());
  }

  @Test
  public void createInstanceCreationFromClassAndParametersUsesResolvedConstructor() {
    Expression argument = new StringLiteral("seed");

    InstanceCreation creation = AstUtilities.createInstanceCreation(StringBuilder.class, new Class<?>[]{String.class}, argument);

    assertEquals(JavaType.getInstance(StringBuilder.class), creation.getType());
    assertEquals(1, creation.requiredArguments.size());
    assertSame(argument, creation.requiredArguments.get(0).expression.getValue());
  }

  @Test
  public void createInstanceCreationFromClassUsesNoArgDeclaredConstructor() {
    InstanceCreation creation = AstUtilities.createInstanceCreation(ArrayList.class);

    assertEquals(JavaType.getInstance(ArrayList.class), creation.getType());
    assertEquals(0, creation.requiredArguments.size());
  }

  @Test
  public void createArrayInstanceCreationFromTypeUsesLengthAndExpressions() {
    Expression first = stringLiteral();
    Expression second = new StringLiteral("bye");

    ArrayInstanceCreation creation = AstUtilities.createArrayInstanceCreation(JavaType.getInstance(String[].class), first, second);

    assertEquals(JavaType.getInstance(String[].class), creation.arrayType.getValue());
    assertEquals(Integer.valueOf(2), creation.lengths.get(0));
    assertEquals(2, creation.expressions.size());
    assertSame(first, creation.expressions.get(0));
    assertSame(second, creation.expressions.get(1));
  }

  @Test
  public void createArrayInstanceCreationFromClassConvertsArrayType() {
    Expression first = intLiteral();
    Expression second = new IntegerLiteral(7);

    ArrayInstanceCreation creation = AstUtilities.createArrayInstanceCreation(Integer[].class, first, second);

    assertEquals(JavaType.getInstance(Integer[].class), creation.arrayType.getValue());
    assertEquals(Integer.valueOf(2), creation.lengths.get(0));
    assertEquals(2, creation.expressions.size());
  }

  @Test
  public void createReturnStatementFromTypeStoresExpression() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    Expression expression = stringLiteral();

    ReturnStatement statement = AstUtilities.createReturnStatement(type, expression);

    assertSame(type, statement.expressionType.getValue());
    assertSame(expression, statement.expression.getValue());
  }

  @Test
  public void createReturnStatementFromClassConvertsType() {
    Expression expression = intLiteral();

    ReturnStatement statement = AstUtilities.createReturnStatement(Integer.class, expression);

    assertEquals(JavaType.getInstance(Integer.class), statement.expressionType.getValue());
    assertSame(expression, statement.expression.getValue());
  }

  @Test
  public void createFieldAssignmentUsesProvidedExpressionAndValue() {
    Expression receiver = new ThisExpression();
    UserField field = mutableStringField();
    Expression value = stringLiteral();

    AssignmentExpression assignment = AstUtilities.createFieldAssignment(receiver, field, value);

    assertSame(AssignmentExpression.Operator.ASSIGN, assignment.operator.getValue());
    assertSame(field.getValueType(), assignment.expressionType.getValue());
    assertTrue(assignment.leftHandSide.getValue() instanceof FieldAccess);
    FieldAccess access = (FieldAccess) assignment.leftHandSide.getValue();
    assertSame(receiver, access.expression.getValue());
    assertSame(field, access.field.getValue());
    assertSame(value, assignment.rightHandSide.getValue());
  }

  @Test
  public void createFieldAssignmentStatementWrapsAssignment() {
    Expression receiver = new ThisExpression();
    UserField field = mutableStringField();
    Expression value = stringLiteral();

    ExpressionStatement statement = AstUtilities.createFieldAssignmentStatement(receiver, field, value);

    assertTrue(statement.expression.getValue() instanceof AssignmentExpression);
    AssignmentExpression assignment = (AssignmentExpression) statement.expression.getValue();
    assertSame(value, assignment.rightHandSide.getValue());
  }

  @Test
  public void createFieldAssignmentWithoutExpressionUsesThis() {
    UserField field = mutableStringField();
    Expression value = stringLiteral();

    AssignmentExpression assignment = AstUtilities.createFieldAssignment(field, value);

    assertTrue(assignment.leftHandSide.getValue() instanceof FieldAccess);
    FieldAccess access = (FieldAccess) assignment.leftHandSide.getValue();
    assertTrue(access.expression.getValue() instanceof ThisExpression);
    assertSame(field, access.field.getValue());
    assertSame(value, assignment.rightHandSide.getValue());
  }

  @Test
  public void createFieldAssignmentStatementWithoutExpressionUsesThis() {
    UserField field = mutableStringField();

    ExpressionStatement statement = AstUtilities.createFieldAssignmentStatement(field, stringLiteral());

    assertTrue(statement.expression.getValue() instanceof AssignmentExpression);
    AssignmentExpression assignment = (AssignmentExpression) statement.expression.getValue();
    FieldAccess access = (FieldAccess) assignment.leftHandSide.getValue();
    assertTrue(access.expression.getValue() instanceof ThisExpression);
  }

  @Test
  public void createLocalAssignmentUsesLocalAccess() {
    UserLocal local = mutableStringLocal();
    Expression value = stringLiteral();

    AssignmentExpression assignment = AstUtilities.createLocalAssignment(local, value);

    assertSame(AssignmentExpression.Operator.ASSIGN, assignment.operator.getValue());
    assertSame(local.getValueType(), assignment.expressionType.getValue());
    assertTrue(assignment.leftHandSide.getValue() instanceof LocalAccess);
    assertSame(local, ((LocalAccess) assignment.leftHandSide.getValue()).local.getValue());
    assertSame(value, assignment.rightHandSide.getValue());
  }

  @Test
  public void createLocalAssignmentStatementWrapsLocalAssignment() {
    UserLocal local = mutableStringLocal();

    ExpressionStatement statement = AstUtilities.createLocalAssignmentStatement(local, stringLiteral());

    assertTrue(statement.expression.getValue() instanceof AssignmentExpression);
    AssignmentExpression assignment = (AssignmentExpression) statement.expression.getValue();
    assertTrue(assignment.leftHandSide.getValue() instanceof LocalAccess);
    assertSame(local, ((LocalAccess) assignment.leftHandSide.getValue()).local.getValue());
  }

  @Test
  public void createStringConcatenationStoresOperandsAndStringType() {
    Expression left = stringLiteral();
    Expression right = intLiteral();

    StringConcatenation concatenation = AstUtilities.createStringConcatenation(left, right);

    assertSame(left, concatenation.leftOperand.getValue());
    assertSame(right, concatenation.rightOperand.getValue());
    assertSame(JavaType.STRING_TYPE, concatenation.getType());
  }

  private static IntegerLiteral intLiteral() {
    return new IntegerLiteral(42);
  }

  private static DoubleLiteral doubleLiteral() {
    return new DoubleLiteral(3.14);
  }

  private static StringLiteral stringLiteral() {
    return new StringLiteral("hello");
  }

  private static BooleanLiteral booleanLiteral() {
    return new BooleanLiteral(true);
  }

  private static NullLiteral nullLiteral() {
    return new NullLiteral();
  }

  private static UserField mutableStringField() {
    return new UserField("myField", JavaType.getInstance(String.class), nullLiteral());
  }

  private static UserLocal mutableStringLocal() {
    return new UserLocal("myLocal", JavaType.getInstance(String.class), false);
  }
}
