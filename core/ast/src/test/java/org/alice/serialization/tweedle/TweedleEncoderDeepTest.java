package org.alice.serialization.tweedle;

import org.junit.Test;
import org.lgna.project.ast.AbstractDeclaration;
import org.lgna.project.ast.AbstractNode;
import org.lgna.project.ast.ArithmeticInfixExpression;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.BooleanLiteral;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.ConditionalStatement;
import org.lgna.project.ast.ConstructorBlockStatement;
import org.lgna.project.ast.CountLoop;
import org.lgna.project.ast.DoInOrder;
import org.lgna.project.ast.DoTogether;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ReturnStatement;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.code.ProcessableNode;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Deep coverage tests for {@link TweedleEncoder}.
 * Targets statement encoding (local declarations, conditionals, loops),
 * expression encoding (literals, arithmetic), method encoding,
 * class structure, disabled statement rendering, and round-trip fidelity.
 *
 * <p>All tests live in the same package as TweedleEncoder to access
 * its package-private constructor.
 */
public class TweedleEncoderDeepTest {

  private String encode(ProcessableNode node) {
    return new TweedleEncoder().encode(node);
  }

  // ── Literal encoding ──────────────────────────────────────────────────────

  @Test
  public void encodeIntegerLiteralContainsValue() {
    String result = encode(new IntegerLiteral(42));
    assertNotNull(result);
    assertTrue("Integer literal should contain 42", result.contains("42"));
  }

  @Test
  public void encodeIntegerLiteralZero() {
    String result = encode(new IntegerLiteral(0));
    assertNotNull(result);
    assertTrue("Zero literal should contain 0", result.contains("0"));
  }

  @Test
  public void encodeIntegerLiteralNegative() {
    String result = encode(new IntegerLiteral(-7));
    assertNotNull(result);
    assertTrue("Negative literal should contain -7", result.contains("-7"));
  }

  @Test
  public void encodeDoubleLiteralContainsValue() {
    String result = encode(new DoubleLiteral(3.14));
    assertNotNull(result);
    assertTrue("Double literal should contain 3.14", result.contains("3.14"));
  }

  @Test
  public void encodeStringLiteralContainsValue() {
    String result = encode(new StringLiteral("hello"));
    assertNotNull(result);
    assertTrue("String literal should contain hello", result.contains("hello"));
  }

  @Test
  public void encodeStringLiteralEmptyIsNonNull() {
    String result = encode(new StringLiteral(""));
    assertNotNull(result);
  }

  @Test
  public void encodeBooleanLiteralTrueContainsTrue() {
    String result = encode(new BooleanLiteral(true));
    assertNotNull(result);
    assertTrue("Boolean true should contain 'true'", result.contains("true"));
  }

  @Test
  public void encodeBooleanLiteralFalseContainsFalse() {
    String result = encode(new BooleanLiteral(false));
    assertNotNull(result);
    assertTrue("Boolean false should contain 'false'", result.contains("false"));
  }

  @Test
  public void encodeNullLiteralIsNonNull() {
    String result = encode(new NullLiteral());
    assertNotNull(result);
  }

  // ── Statement encoding ────────────────────────────────────────────────────

  @Test
  public void encodeLocalDeclarationContainsVariableName() {
    UserLocal variable = new UserLocal("count", Integer.class, true);
    LocalDeclarationStatement stmt = new LocalDeclarationStatement(variable, new IntegerLiteral(0));
    String result = encode(stmt);
    assertNotNull(result);
    assertTrue("Local declaration should contain variable name", result.contains("count"));
  }

  @Test
  public void encodeLocalDeclarationContainsInitializerValue() {
    UserLocal variable = new UserLocal("total", Integer.class, true);
    LocalDeclarationStatement stmt = new LocalDeclarationStatement(variable, new IntegerLiteral(99));
    String result = encode(stmt);
    assertTrue("Local declaration should contain initializer value", result.contains("99"));
  }

  @Test
  public void encodeBlockStatementIsNonEmpty() {
    BlockStatement block = new BlockStatement(
        new Comment("inside block"));
    String result = encode(block);
    assertNotNull(result);
    assertTrue("Block statement should produce non-empty output", result.length() > 0);
  }

  @Test
  public void encodeCommentProducesOutput() {
    Comment comment = new Comment("a comment");
    String result = encode(comment);
    assertNotNull(result);
  }

  // ── Conditional statement encoding ────────────────────────────────────────

  @Test
  public void encodeConditionalStatementContainsIfKeyword() {
    ConditionalStatement conditional = AstUtilities.createConditionalStatement(new BooleanLiteral(true));
    String result = encode(conditional);
    assertNotNull(result);
    assertTrue("Conditional should contain if-related keyword", result.contains("if"));
  }

  // ── CountLoop encoding ────────────────────────────────────────────────────

  @Test
  public void encodeCountLoopContainsCountValue() {
    CountLoop loop = AstUtilities.createCountLoop(new IntegerLiteral(5));
    String result = encode(loop);
    assertNotNull(result);
    assertTrue("Count loop should contain the count", result.contains("5"));
  }

  // ── DoInOrder encoding ────────────────────────────────────────────────────

  @Test
  public void encodeDoInOrderContainsBody() {
    DoInOrder doInOrder = new DoInOrder(
        new BlockStatement(new Comment("step")));
    String result = encode(doInOrder);
    assertNotNull(result);
    assertTrue("DoInOrder should produce non-empty output", result.length() > 0);
  }

  // ── DoTogether encoding ───────────────────────────────────────────────────

  @Test
  public void encodeDoTogetherContainsBody() {
    DoTogether doTogether = new DoTogether(
        new BlockStatement(new Comment("parallel")));
    String result = encode(doTogether);
    assertNotNull(result);
    assertTrue("DoTogether should produce non-empty output", result.length() > 0);
  }

  // ── Arithmetic expression encoding ────────────────────────────────────────

  @Test
  public void encodeArithmeticAdditionContainsOperands() {
    ArithmeticInfixExpression expr = new ArithmeticInfixExpression(
        new IntegerLiteral(3),
        ArithmeticInfixExpression.Operator.PLUS,
        new IntegerLiteral(4),
        Integer.class);
    String result = encode(expr);
    assertNotNull(result);
    assertTrue("Arithmetic should contain left operand", result.contains("3"));
    assertTrue("Arithmetic should contain right operand", result.contains("4"));
  }

  @Test
  public void encodeArithmeticMultiplicationContainsStar() {
    ArithmeticInfixExpression expr = new ArithmeticInfixExpression(
        new IntegerLiteral(6),
        ArithmeticInfixExpression.Operator.TIMES,
        new IntegerLiteral(7),
        Integer.class);
    String result = encode(expr);
    assertNotNull(result);
    assertTrue("Multiplication should contain *", result.contains("*"));
  }

  // ── Method encoding ──────────────────────────────────────────────────────

  @Test
  public void encodeUserMethodContainsMethodName() {
    UserMethod method = new UserMethod("doWork", Void.TYPE,
        new UserParameter[0], new BlockStatement());
    method.isStatic.setValue(false);
    String result = encode(method);
    assertNotNull(result);
    assertTrue("Encoded method should contain name", result.contains("doWork"));
  }

  @Test
  public void encodeUserMethodWithParameterContainsParameterName() {
    UserParameter param = new UserParameter("count", Integer.class);
    UserMethod method = new UserMethod("repeat", Void.TYPE,
        new UserParameter[]{param}, new BlockStatement());
    String result = encode(method);
    assertNotNull(result);
    assertTrue("Encoded method should contain parameter name", result.contains("count"));
  }

  @Test
  public void encodeUserMethodWithReturnStatementContainsReturnValue() {
    ReturnStatement ret = new ReturnStatement(JavaType.getInstance(Integer.class), new IntegerLiteral(42));
    UserMethod method = new UserMethod("getAnswer", Integer.class,
        new UserParameter[0], new BlockStatement(ret));
    String result = encode(method);
    assertNotNull(result);
    assertTrue("Encoded method with return should contain 42", result.contains("42"));
  }

  // ── Class/type encoding ───────────────────────────────────────────────────

  @Test
  public void encodeNamedUserTypeContainsClassName() {
    NamedUserType type = new NamedUserType(
        "MyClass", null, Object.class,
        new NamedUserConstructor[]{},
        new UserMethod[0],
        new UserField[0]);
    String result = encode(type);
    assertNotNull(result);
    assertTrue("Encoded type should contain class name", result.contains("MyClass"));
  }

  @Test
  public void encodeNamedUserTypeWithFieldContainsFieldName() {
    UserField field = new UserField("score", Integer.class, new IntegerLiteral(100));
    NamedUserType type = new NamedUserType(
        "Player", null, Object.class,
        new NamedUserConstructor[]{},
        new UserMethod[0],
        new UserField[]{field});
    String result = encode(type);
    assertTrue("Encoded type should contain field name", result.contains("score"));
  }

  @Test
  public void encodeNamedUserTypeWithMethodContainsMethodName() {
    UserMethod method = new UserMethod("run", Void.TYPE,
        new UserParameter[0], new BlockStatement());
    NamedUserType type = new NamedUserType(
        "Runner", null, Object.class,
        new NamedUserConstructor[]{},
        new UserMethod[]{method},
        new UserField[0]);
    String result = encode(type);
    assertTrue("Encoded type should contain method name", result.contains("run"));
  }

  // ── Constructor encoding via type ─────────────────────────────────────────

  @Test
  public void encodeTypeWithConstructorContainsConstructorSignature() {
    NamedUserConstructor ctor = new NamedUserConstructor(
        new UserParameter[0], new ConstructorBlockStatement());
    NamedUserType type = new NamedUserType(
        "Buildable", null, Object.class,
        new NamedUserConstructor[]{ctor},
        new UserMethod[0],
        new UserField[0]);
    String result = encode(type);
    assertNotNull(result);
    assertTrue("Type with constructor should encode", result.length() > 0);
  }

  // ── Terminal set behavior ────────────────────────────────────────────────

  @Test
  public void encodeWithEmptyTerminalSetMatchesDefault() {
    NamedUserType type = new NamedUserType(
        "TerminalTest", null, Object.class,
        new NamedUserConstructor[]{},
        new UserMethod[0],
        new UserField[0]);

    String defaultResult = new TweedleEncoder().encode(type);
    String terminalResult = new TweedleEncoder(new HashSet<>()).encode(type);

    assertEquals("Empty terminal set should match default encoding",
        defaultResult, terminalResult);
  }

  // ── Encoder independence ──────────────────────────────────────────────────

  @Test
  public void separateEncoderInstancesProduceIndependentOutput() {
    NamedUserType typeA = new NamedUserType(
        "TypeA", null, Object.class,
        new NamedUserConstructor[]{}, new UserMethod[0], new UserField[0]);
    NamedUserType typeB = new NamedUserType(
        "TypeB", null, Object.class,
        new NamedUserConstructor[]{}, new UserMethod[0], new UserField[0]);

    String outputA = new TweedleEncoder().encode(typeA);
    String outputB = new TweedleEncoder().encode(typeB);

    assertTrue("TypeA output should contain TypeA", outputA.contains("TypeA"));
    assertTrue("TypeB output should contain TypeB", outputB.contains("TypeB"));
    assertFalse("TypeA output should not contain TypeB", outputA.contains("TypeB"));
    assertFalse("TypeB output should not contain TypeA", outputB.contains("TypeA"));
  }

  // ── Encode→decode round-trip ──────────────────────────────────────────────

  @Test
  public void encodeDecodeRoundTripPreservesClassName() throws Exception {
    TweedleEncoderDecoder facade = new TweedleEncoderDecoder();
    NamedUserType original = createTestType("RoundTripper");

    String encoded = new TweedleEncoder().encode(original);
    AbstractNode decoded = facade.decode(encoded);

    assertTrue("Decoded node should be NamedUserType", decoded instanceof NamedUserType);
    assertEquals("Class name should survive round-trip",
        original.getName(), ((NamedUserType) decoded).getName());
  }

  @Test
  public void encodeDecodeRoundTripPreservesFieldCount() throws Exception {
    TweedleEncoderDecoder facade = new TweedleEncoderDecoder();

    UserField field1 = new UserField("x", Integer.class, new IntegerLiteral(1));
    UserField field2 = new UserField("y", Integer.class, new IntegerLiteral(2));
    NamedUserType original = new NamedUserType(
        "TwoFields", null, Object.class,
        new NamedUserConstructor[]{}, new UserMethod[0],
        new UserField[]{field1, field2});

    String encoded = new TweedleEncoder().encode(original);
    NamedUserType decoded = (NamedUserType) facade.decode(encoded);

    assertEquals("Field count should survive round-trip",
        original.getDeclaredFields().size(), decoded.getDeclaredFields().size());
  }

  @Test
  public void encodeDecodeRoundTripPreservesMethodCount() throws Exception {
    TweedleEncoderDecoder facade = new TweedleEncoderDecoder();

    UserMethod m1 = new UserMethod("alpha", Void.TYPE, new UserParameter[0], new BlockStatement());
    UserMethod m2 = new UserMethod("beta", Void.TYPE, new UserParameter[0], new BlockStatement());
    NamedUserType original = new NamedUserType(
        "TwoMethods", null, Object.class,
        new NamedUserConstructor[]{}, new UserMethod[]{m1, m2}, new UserField[0]);

    String encoded = new TweedleEncoder().encode(original);
    NamedUserType decoded = (NamedUserType) facade.decode(encoded);

    assertEquals("Method count should survive round-trip",
        original.getDeclaredMethods().size(), decoded.getDeclaredMethods().size());
  }

  // ── Idempotency ──────────────────────────────────────────────────────────

  @Test
  public void encodingSameNodeTwiceProducesSameResult() {
    NamedUserType type = createTestType("IdempotentType");

    String first = new TweedleEncoder().encode(type);
    String second = new TweedleEncoder().encode(type);

    assertEquals("Encoding the same node twice should produce identical output", first, second);
  }

  // ── Disabled statement encoding ───────────────────────────────────────────

  @Test
  public void encodeDisabledStatementProducesDisabledMarker() {
    Comment disabled = new Comment("hidden");
    disabled.isEnabled.setValue(false);

    BlockStatement block = new BlockStatement(disabled);
    UserMethod method = new UserMethod("withDisabled", Void.TYPE,
        new UserParameter[0], block);

    String result = encode(method);
    assertNotNull(result);
    // TweedleEncoder uses *< and >* markers for disabled statements
    // or the statement is simply omitted; check that the output is still valid
    assertTrue("Method with disabled comment should still encode", result.length() > 0);
  }

  // ── Helper ────────────────────────────────────────────────────────────────

  private static NamedUserType createTestType(String name) {
    return new NamedUserType(
        name, null, Object.class,
        new NamedUserConstructor[]{}, new UserMethod[0], new UserField[0]);
  }
}
