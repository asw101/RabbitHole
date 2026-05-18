package org.lgna.project.ast;

import org.junit.Test;
import org.lgna.project.code.CodeOrganizer;

import static org.junit.Assert.*;

/**
 * Extended coverage tests for JavaCodeGenerator builder, code generation edge cases,
 * and additional SourceCodeGenerator paths.
 */
public class JavaCodeGeneratorExtendedTest {

  private JavaCodeGenerator createDefaultGenerator() {
    return new JavaCodeGenerator.Builder()
        .isLambdaSupported(true)
        .isPublicStaticFinalFieldGetterDesired(false)
        .build();
  }

  private static String generate(NamedUserType type) {
    JavaCodeGenerator generator = new JavaCodeGenerator.Builder()
        .isLambdaSupported(true)
        .addDefaultCodeOrganizerDefinition(CodeOrganizer.defaultCodeOrganizer)
        .build();
    type.process(generator);
    return generator.getText();
  }

  // ── Builder pattern ──────────────────────────────────────

  @Test
  public void builderDefaults() {
    JavaCodeGenerator gen = new JavaCodeGenerator.Builder().build();
    assertNotNull(gen);
    assertFalse(gen.isLambdaSupported());
    assertFalse(gen.isPublicStaticFinalFieldGetterDesired());
  }

  @Test
  public void builderWithLambdaSupport() {
    JavaCodeGenerator gen = new JavaCodeGenerator.Builder()
        .isLambdaSupported(true)
        .build();
    assertTrue(gen.isLambdaSupported());
  }

  @Test
  public void builderWithFieldGetter() {
    JavaCodeGenerator gen = new JavaCodeGenerator.Builder()
        .isPublicStaticFinalFieldGetterDesired(true)
        .build();
    assertTrue(gen.isPublicStaticFinalFieldGetterDesired());
  }

  @Test
  public void builderWithImportOnDemand() {
    JavaCodeGenerator gen = new JavaCodeGenerator.Builder()
        .addImportOnDemandPackage(java.util.List.class.getPackage())
        .build();
    assertNotNull(gen);
  }

  @Test
  public void builderWithCodeOrganizerDefinition() {
    JavaCodeGenerator gen = new JavaCodeGenerator.Builder()
        .addCodeOrganizerDefinition("Scene", CodeOrganizer.sceneClassCodeOrganizer)
        .addDefaultCodeOrganizerDefinition(CodeOrganizer.defaultCodeOrganizer)
        .build();
    assertNotNull(gen);
  }

  // ── Code generation for various AST constructs ────────

  @Test
  public void generateCodeForSimpleType() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("TestClass", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createProcedure("doSomething");
    type.methods.add(method);
    String code = generate(type);
    assertNotNull(code);
    assertTrue(code.contains("TestClass"));
    assertTrue(code.contains("doSomething"));
  }

  @Test
  public void generateCodeWithField() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("WithField", JavaType.OBJECT_TYPE);
    UserField field = new UserField("myField", JavaType.getInstance(String.class), new NullLiteral());
    type.fields.add(field);
    String code = generate(type);
    assertNotNull(code);
    assertTrue(code.contains("myField"));
  }

  @Test
  public void generateCodeWithConditional() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("CondType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createProcedure("condMethod");
    ConditionalStatement cond = AstUtilities.createConditionalStatement(new BooleanLiteral(true));
    method.body.getValue().statements.add(cond);
    type.methods.add(method);
    String code = generate(type);
    assertTrue(code.contains("if"));
  }

  @Test
  public void generateCodeWithCountLoop() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("LoopType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createProcedure("loopMethod");
    CountLoop loop = AstUtilities.createCountLoop(new IntegerLiteral(3));
    method.body.getValue().statements.add(loop);
    type.methods.add(method);
    String code = generate(type);
    assertNotNull(code);
  }

  @Test
  public void generateCodeWithWhileLoop() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("WhileType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createProcedure("whileMethod");
    WhileLoop loop = AstUtilities.createWhileLoop(new BooleanLiteral(true));
    method.body.getValue().statements.add(loop);
    type.methods.add(method);
    String code = generate(type);
    assertNotNull(code);
    assertTrue(code.contains("while"));
  }

  @Test
  public void generateCodeWithDoInOrder() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("OrderType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createProcedure("orderMethod");
    DoInOrder dio = AstUtilities.createDoInOrder();
    method.body.getValue().statements.add(dio);
    type.methods.add(method);
    String code = generate(type);
    assertNotNull(code);
  }

  @Test
  public void generateCodeWithDoTogether() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("TogetherType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createProcedure("togetherMethod");
    DoTogether dt = AstUtilities.createDoTogether();
    method.body.getValue().statements.add(dt);
    type.methods.add(method);
    String code = generate(type);
    assertNotNull(code);
  }

  @Test
  public void generateCodeWithComment() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("CommentType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createProcedure("commentMethod");
    Comment comment = new Comment();
    comment.text.setValue("This is a comment");
    method.body.getValue().statements.add(comment);
    type.methods.add(method);
    String code = generate(type);
    assertNotNull(code);
    assertTrue(code.contains("This is a comment"));
  }

  @Test
  public void generateCodeWithReturnStatement() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("ReturnType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createFunction("getValue", String.class);
    ReturnStatement ret = AstUtilities.createReturnStatement(String.class, new StringLiteral("hello"));
    method.body.getValue().statements.add(ret);
    type.methods.add(method);
    String code = generate(type);
    assertNotNull(code);
    assertTrue(code.contains("return"));
  }

  @Test
  public void generateCodeWithLocalDeclaration() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("LocalType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createProcedure("localMethod");
    UserLocal local = new UserLocal("temp", JavaType.getInstance(int.class), false);
    LocalDeclarationStatement lds = new LocalDeclarationStatement(local, new IntegerLiteral(42));
    method.body.getValue().statements.add(lds);
    type.methods.add(method);
    String code = generate(type);
    assertNotNull(code);
  }

  @Test
  public void generateCodeWithMethodInvocation() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("InvokeType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createProcedure("invokeMethod");
    JavaMethod stringLength = JavaMethod.getInstance(String.class, "length");
    ExpressionStatement stmt = AstUtilities.createMethodInvocationStatement(
        new StringLiteral("test"), stringLength);
    method.body.getValue().statements.add(stmt);
    type.methods.add(method);
    String code = generate(type);
    assertNotNull(code);
  }

  @Test
  public void generateCodeWithStringConcatenation() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("ConcatType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createFunction("concat", String.class);
    StringConcatenation concat = AstUtilities.createStringConcatenation(
        new StringLiteral("a"), new StringLiteral("b"));
    ReturnStatement ret = new ReturnStatement(JavaType.getInstance(String.class), concat);
    method.body.getValue().statements.add(ret);
    type.methods.add(method);
    String code = generate(type);
    assertTrue(code.contains("+"));
  }

  @Test
  public void generateCodeWithArithmetic() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("ArithType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createFunction("add", int.class);
    ArithmeticInfixExpression arith = new ArithmeticInfixExpression(
        new IntegerLiteral(1), ArithmeticInfixExpression.Operator.PLUS,
        new IntegerLiteral(2), int.class);
    ReturnStatement ret = new ReturnStatement(JavaType.getInstance(int.class), arith);
    method.body.getValue().statements.add(ret);
    type.methods.add(method);
    String code = generate(type);
    assertNotNull(code);
  }

  @Test
  public void generateCodeWithFieldAccess() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("FldType", JavaType.OBJECT_TYPE);
    UserField field = new UserField("data", JavaType.getInstance(String.class), new NullLiteral());
    type.fields.add(field);
    UserMethod method = AstUtilities.createFunction("getData", String.class);
    FieldAccess fa = new FieldAccess(new ThisExpression(), field);
    ReturnStatement ret = new ReturnStatement(JavaType.getInstance(String.class), fa);
    method.body.getValue().statements.add(ret);
    type.methods.add(method);
    String code = generate(type);
    assertTrue(code.contains("data"));
  }

  @Test
  public void generateCodeWithInstanceCreation() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("NewType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createFunction("create", Object.class);
    InstanceCreation ic = AstUtilities.createInstanceCreation(StringBuilder.class);
    ReturnStatement ret = new ReturnStatement(JavaType.OBJECT_TYPE, ic);
    method.body.getValue().statements.add(ret);
    type.methods.add(method);
    String code = generate(type);
    assertTrue(code.contains("new"));
  }

  @Test
  public void generateCodeWithLogicalComplement() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("LogicType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createFunction("negate", boolean.class);
    LogicalComplement lc = new LogicalComplement(new BooleanLiteral(true));
    ReturnStatement ret = new ReturnStatement(JavaType.getInstance(boolean.class), lc);
    method.body.getValue().statements.add(ret);
    type.methods.add(method);
    String code = generate(type);
    assertTrue(code.contains("!"));
  }

  @Test
  public void generateCodeWithDisabledStatement() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("DisabledType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createProcedure("disabledMethod");
    Comment comment = new Comment();
    comment.text.setValue("disabled comment");
    comment.isEnabled.setValue(false);
    method.body.getValue().statements.add(comment);
    type.methods.add(method);
    String code = generate(type);
    assertNotNull(code);
  }

  @Test
  public void generateCodeWithLambdaCreation() {
    // Lambda requires statement context; just verify creation
    LambdaExpression lambda = AstUtilities.createLambdaExpression(Runnable.class);
    assertNotNull(lambda);
    assertNotNull(lambda.value.getValue());
  }

  @Test
  public void generateCodeWithAssignment() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("AssignType", JavaType.OBJECT_TYPE);
    UserField field = new UserField("value", JavaType.getInstance(String.class), new NullLiteral());
    type.fields.add(field);
    UserMethod method = AstUtilities.createProcedure("setVal");
    ExpressionStatement stmt = AstUtilities.createFieldAssignmentStatement(field, new StringLiteral("new"));
    method.body.getValue().statements.add(stmt);
    type.methods.add(method);
    String code = generate(type);
    assertTrue(code.contains("="));
  }

  @Test
  public void generateCodeWithNull() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("NullType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createFunction("getNull", Object.class);
    ReturnStatement ret = new ReturnStatement(JavaType.OBJECT_TYPE, new NullLiteral());
    method.body.getValue().statements.add(ret);
    type.methods.add(method);
    String code = generate(type);
    assertTrue(code.contains("null"));
  }

  @Test
  public void generateCodeWithThis() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("ThisType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createFunction("getSelf", Object.class);
    ReturnStatement ret = new ReturnStatement(JavaType.OBJECT_TYPE, new ThisExpression());
    method.body.getValue().statements.add(ret);
    type.methods.add(method);
    String code = generate(type);
    assertTrue(code.contains("this"));
  }

  @Test
  public void generateCodeWithArrayCreation() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("ArrayType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createFunction("getArray", Object.class);
    ArrayInstanceCreation aic = AstUtilities.createArrayInstanceCreation(
        String[].class, new StringLiteral("a"));
    ReturnStatement ret = new ReturnStatement(JavaType.OBJECT_TYPE, aic);
    method.body.getValue().statements.add(ret);
    type.methods.add(method);
    String code = generate(type);
    assertNotNull(code);
  }

  @Test
  public void generateCodeWithRelationalExpression() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("RelType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createFunction("compare", boolean.class);
    RelationalInfixExpression rie = new RelationalInfixExpression(
        new IntegerLiteral(1), RelationalInfixExpression.Operator.LESS,
        new IntegerLiteral(2), JavaType.getInstance(int.class), JavaType.getInstance(int.class));
    ReturnStatement ret = new ReturnStatement(JavaType.getInstance(boolean.class), rie);
    method.body.getValue().statements.add(ret);
    type.methods.add(method);
    String code = generate(type);
    assertTrue(code.contains("<"));
  }

  @Test
  public void generateCodeWithConditionalExpression() {
    JavaCodeGenerator gen = createDefaultGenerator();
    NamedUserType type = AstUtilities.createType("CondExprType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createFunction("logicAnd", boolean.class);
    ConditionalInfixExpression cie = new ConditionalInfixExpression(
        new BooleanLiteral(true), ConditionalInfixExpression.Operator.AND,
        new BooleanLiteral(false));
    ReturnStatement ret = new ReturnStatement(JavaType.getInstance(boolean.class), cie);
    method.body.getValue().statements.add(ret);
    type.methods.add(method);
    String code = generate(type);
    assertTrue(code.contains("&&"));
  }
}
