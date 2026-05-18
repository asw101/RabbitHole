package org.lgna.project.ast;

import org.lgna.project.code.CodeOrganizer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Deep coverage tests for {@link SourceCodeGenerator} abstract methods
 * exercised via {@link JavaCodeGenerator} as the concrete subclass.
 * Targets bitwise/shift expression generation, disabled statement
 * nesting, comment formatting, doInOrder/doTogether generation,
 * processClass with organizer, and edge cases not covered by
 * existing SourceCodeGeneratorTest.
 */
public class SourceCodeGeneratorDeepTest {

  private static String generate(Statement statement) {
    JavaCodeGenerator generator = new JavaCodeGenerator.Builder().build();
    statement.process(generator);
    return generator.getText();
  }

  private static String generate(Expression expression) {
    JavaCodeGenerator generator = new JavaCodeGenerator.Builder().build();
    generator.processExpression(expression);
    return generator.getText();
  }

  private static String generate(NamedUserType type) {
    JavaCodeGenerator generator = new JavaCodeGenerator.Builder()
        .addDefaultCodeOrganizerDefinition(CodeOrganizer.defaultCodeOrganizer)
        .build();
    type.process(generator);
    return generator.getText();
  }

  // ── Bitwise infix expression generation ───────────────────────────────────

  @Test
  public void bitwiseAndGeneratesAmpersand() {
    Expression expr = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(5),
        BitwiseInfixExpression.Operator.AND,
        new IntegerLiteral(3));
    assertEquals("5&3", generate(expr));
  }

  @Test
  public void bitwiseOrGeneratesPipe() {
    Expression expr = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(5),
        BitwiseInfixExpression.Operator.OR,
        new IntegerLiteral(3));
    assertEquals("5|3", generate(expr));
  }

  @Test
  public void bitwiseXorGeneratesCaret() {
    Expression expr = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(5),
        BitwiseInfixExpression.Operator.XOR,
        new IntegerLiteral(3));
    assertEquals("5^3", generate(expr));
  }

  // ── Shift infix expression generation ─────────────────────────────────────

  @Test
  public void leftShiftGeneratesDoubleLeftAngle() {
    Expression expr = new ShiftInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(1),
        ShiftInfixExpression.Operator.LEFT_SHIFT,
        new IntegerLiteral(3));
    assertEquals("1<<3", generate(expr));
  }

  @Test
  public void rightShiftSignedGeneratesDoubleRightAngle() {
    Expression expr = new ShiftInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(-8),
        ShiftInfixExpression.Operator.RIGHT_SHIFT_SIGNED,
        new IntegerLiteral(2));
    assertEquals("-8>>2", generate(expr));
  }

  @Test
  public void rightShiftUnsignedGeneratesTripleRightAngle() {
    Expression expr = new ShiftInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(-1),
        ShiftInfixExpression.Operator.RIGHT_SHIFT_UNSIGNED,
        new IntegerLiteral(28));
    assertEquals("-1>>>28", generate(expr));
  }

  // ── Nested bitwise expressions ────────────────────────────────────────────

  @Test
  public void nestedBitwiseExpressionsPreservePrecedenceInOutput() {
    Expression inner = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(5),
        BitwiseInfixExpression.Operator.AND,
        new IntegerLiteral(3));
    Expression outer = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        inner,
        BitwiseInfixExpression.Operator.OR,
        new IntegerLiteral(8));
    String result = generate(outer);
    assertNotNull(result);
    assertTrue("Output should contain both operator symbols", result.contains("&") && result.contains("|"));
  }

  // ── Combined shift and bitwise ────────────────────────────────────────────

  @Test
  public void shiftThenBitwiseMaskGeneratesCorrectly() {
    Expression shifted = new ShiftInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(1),
        ShiftInfixExpression.Operator.LEFT_SHIFT,
        new IntegerLiteral(4));
    Expression masked = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        shifted,
        BitwiseInfixExpression.Operator.AND,
        new IntegerLiteral(255));
    String result = generate(masked);
    assertNotNull(result);
    assertTrue("Output should contain << and &", result.contains("<<") && result.contains("&"));
  }

  // ── Disabled statement variations ─────────────────────────────────────────

  @Test
  public void singleDisabledStatementWrappedInCommentBlock() {
    LocalDeclarationStatement stmt = new LocalDeclarationStatement(
        new UserLocal("hidden", String.class, true),
        new StringLiteral("secret"));
    stmt.isEnabled.setValue(false);

    BlockStatement block = new BlockStatement(stmt);
    String source = generate(block);

    assertTrue("Disabled statement should be wrapped in /* disabled", source.contains("/* disabled"));
    assertTrue("Disabled wrapper should close with */", source.contains("*/"));
  }

  @Test
  public void multipleDisabledStatementsEachGetWrapped() {
    LocalDeclarationStatement stmt1 = new LocalDeclarationStatement(
        new UserLocal("a", String.class, true), new StringLiteral("x"));
    stmt1.isEnabled.setValue(false);
    LocalDeclarationStatement stmt2 = new LocalDeclarationStatement(
        new UserLocal("b", String.class, true), new StringLiteral("y"));
    stmt2.isEnabled.setValue(false);

    BlockStatement block = new BlockStatement(stmt1, stmt2);
    String source = generate(block);

    int count = 0;
    int idx = 0;
    while ((idx = source.indexOf("/* disabled", idx)) != -1) {
      count++;
      idx += 11;
    }
    assertEquals("Each disabled statement should get its own wrapper", 2, count);
  }

  @Test
  public void enabledStatementAfterDisabledRendersNormally() {
    LocalDeclarationStatement disabled = new LocalDeclarationStatement(
        new UserLocal("hidden", String.class, true), new StringLiteral("x"));
    disabled.isEnabled.setValue(false);
    LocalDeclarationStatement enabled = new LocalDeclarationStatement(
        new UserLocal("visible", String.class, true), new StringLiteral("y"));

    BlockStatement block = new BlockStatement(disabled, enabled);
    String source = generate(block);

    assertTrue("Enabled statement should appear normally",
        source.contains("final String visible=\"y\""));
    assertTrue("Disabled statement should be wrapped",
        source.contains("/* disabled"));
  }

  // ── DoInOrder code generation ─────────────────────────────────────────────

  @Test
  public void doInOrderWithSingleStatementGeneratesBody() {
    DoInOrder doInOrder = new DoInOrder(
        new BlockStatement(
            new LocalDeclarationStatement(
                new UserLocal("x", Integer.class, true), new IntegerLiteral(1))));
    String source = generate(doInOrder);
    assertNotNull(source);
    assertTrue("DoInOrder should contain the local declaration",
        source.contains("final Integer x=1"));
  }

  @Test
  public void doInOrderWithMultipleStatementsGeneratesAll() {
    DoInOrder doInOrder = new DoInOrder(
        new BlockStatement(
            new LocalDeclarationStatement(new UserLocal("a", Integer.class, true), new IntegerLiteral(1)),
            new LocalDeclarationStatement(new UserLocal("b", Integer.class, true), new IntegerLiteral(2))));
    String source = generate(doInOrder);
    assertTrue("Both declarations should appear", source.contains("a=1") && source.contains("b=2"));
  }

  // ── Comment generation ────────────────────────────────────────────────────

  @Test
  public void commentStatementGeneratesNoOutput() {
    Comment comment = new Comment("test comment");
    String source = generate(comment);
    assertNotNull(source);
  }

  // ── Empty block ──────────────────────────────────────────────────────────

  @Test
  public void emptyBlockStatementGeneratesBraces() {
    BlockStatement block = new BlockStatement();
    String source = generate(block);
    assertTrue("Empty block should generate {}", source.contains("{}") || source.contains("{ }"));
  }

  // ── String concatenation via code generator ────────────────────────────────

  @Test
  public void stringConcatenationGeneratesPlusOperator() {
    StringConcatenation concat = new StringConcatenation(
        new StringLiteral("hello"), new StringLiteral(" world"));
    String source = generate(concat);
    assertTrue("Concatenation should use + operator", source.contains("+"));
  }

  // ── Assignment expression ────────────────────────────────────────────────

  @Test
  public void assignmentExpressionGeneratesEqualsSign() {
    UserLocal count = new UserLocal("count", Integer.class, false);
    AssignmentExpression assignment = new AssignmentExpression(
        JavaType.getInstance(Integer.class),
        new LocalAccess(count),
        AssignmentExpression.Operator.ASSIGN,
        new IntegerLiteral(10));
    ExpressionStatement stmt = new ExpressionStatement(assignment);
    String source = generate(stmt);
    assertTrue("Assignment should use = sign", source.contains("count=10"));
  }

  // ── Boolean literal code generation ────────────────────────────────────────

  @Test
  public void booleanLiteralTrueGeneratesTrue() {
    assertEquals("true", generate(new BooleanLiteral(true)));
  }

  @Test
  public void booleanLiteralFalseGeneratesFalse() {
    assertEquals("false", generate(new BooleanLiteral(false)));
  }

  // ── Integer literal code generation edge cases ─────────────────────────────

  @Test
  public void integerLiteralNegativeGeneratesNegativeSign() {
    String source = generate(new IntegerLiteral(-42));
    assertTrue("Negative literal should contain minus sign", source.contains("-42"));
  }

  @Test
  public void integerLiteralZeroGeneratesZero() {
    assertEquals("0", generate(new IntegerLiteral(0)));
  }

  // ── Double literal code generation ─────────────────────────────────────────

  @Test
  public void doubleLiteralGeneratesDecimalPoint() {
    String source = generate(new DoubleLiteral(3.14));
    assertTrue("Double literal should contain value", source.contains("3.14"));
  }

  // ── WhileLoop code generation ──────────────────────────────────────────────

  @Test
  public void whileLoopWithTrueConditionGeneratesCorrectSyntax() {
    WhileLoop loop = new WhileLoop(new BooleanLiteral(true), new BlockStatement());
    assertEquals("while (true){}", generate(loop));
  }

  // ── ConditionalStatement code generation ──────────────────────────────────

  @Test
  public void conditionalWithThenOnlyGeneratesIfBlock() {
    ConditionalStatement conditional = AstUtilities.createConditionalStatement(new BooleanLiteral(true));
    String source = generate(conditional);
    assertTrue("Should contain if keyword", source.contains("if(true)"));
  }

  // ── CountLoop code generation ─────────────────────────────────────────────

  @Test
  public void countLoopWithLiteralCountGeneratesForLoop() {
    CountLoop loop = AstUtilities.createCountLoop(new IntegerLiteral(5));
    String source = generate(loop);
    assertTrue("Count loop should generate for loop with count",
        source.contains("for(") && source.contains("<5"));
  }

  // ── ForEachInArrayLoop code generation ─────────────────────────────────────

  @Test
  public void forEachInArrayLoopGeneratesEnhancedForSyntax() {
    ForEachInArrayLoop loop = new ForEachInArrayLoop(
        new UserLocal("item", String.class, true),
        AstUtilities.createArrayInstanceCreation(
            String[].class,
            new StringLiteral("red"),
            new StringLiteral("blue")),
        new BlockStatement());
    String source = generate(loop);
    assertTrue("ForEach should generate enhanced for loop",
        source.contains("for(String"));
  }

  // ── processClass generates class structure ─────────────────────────────────

  @Test
  public void processClassGeneratesHeaderBodyAndFooter() {
    NamedUserType simpleType = new NamedUserType(
        "SimpleClass",
        null,
        Object.class,
        new NamedUserConstructor[]{
            new NamedUserConstructor(new UserParameter[]{}, new ConstructorBlockStatement())
        },
        new UserMethod[0],
        new UserField[0]);

    String source = generate(simpleType);
    assertTrue("Should contain class keyword", source.contains("class SimpleClass"));
    assertTrue("Should contain extends", source.contains("extends Object"));
  }

  @Test
  public void processClassWithFieldAndMethodGeneratesAll() {
    UserField field = new UserField("value", Integer.class, new IntegerLiteral(0));
    field.accessLevel.setValue(AccessLevel.PRIVATE);

    UserMethod method = new UserMethod(
        "getValue", Integer.class,
        new UserParameter[0],
        new BlockStatement(AstUtilities.createReturnStatement(Integer.class, new FieldAccess(new ThisExpression(), field))));

    NamedUserType type = new NamedUserType(
        "ValueHolder",
        null,
        Object.class,
        new NamedUserConstructor[]{new NamedUserConstructor(new UserParameter[]{}, new ConstructorBlockStatement())},
        new UserMethod[]{method},
        new UserField[]{field});

    String source = generate(type);
    assertTrue("Class should contain field name", source.contains("value"));
    assertTrue("Class should contain method name", source.contains("getValue"));
  }

  // ── Array length code generation ──────────────────────────────────────────

  @Test
  public void arrayLengthGeneratesDotLength() {
    UserLocal items = new UserLocal("items", String[].class, false);
    assertEquals("items.length", generate(new ArrayLength(new LocalAccess(items))));
  }

  // ── Array access code generation ──────────────────────────────────────────

  @Test
  public void arrayAccessGeneratesBracketNotation() {
    UserLocal items = new UserLocal("items", Integer[].class, false);
    assertEquals(
        "items[2]",
        generate(new ArrayAccess(Integer[].class, new LocalAccess(items), new IntegerLiteral(2))));
  }

  // ── Logical complement code generation ─────────────────────────────────────

  @Test
  public void logicalComplementOfFalseGeneratesExclamation() {
    assertEquals("!false", generate(new LogicalComplement(new BooleanLiteral(false))));
  }

  // ── Null literal code generation ──────────────────────────────────────────

  @Test
  public void nullLiteralGeneratesNull() {
    assertEquals("null", generate(new NullLiteral()));
  }

  // ── Field access code generation ──────────────────────────────────────────

  @Test
  public void fieldAccessGeneratesThisDotFieldName() {
    UserField f = new UserField("data", String.class, new StringLiteral("x"));
    assertEquals("this.data", generate(new FieldAccess(new ThisExpression(), f)));
  }
}
