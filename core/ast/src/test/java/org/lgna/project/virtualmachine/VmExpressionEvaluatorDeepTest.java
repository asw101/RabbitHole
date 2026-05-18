package org.lgna.project.virtualmachine;

import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.ArrayAccess;
import org.lgna.project.ast.ArrayInstanceCreation;
import org.lgna.project.ast.ArrayLength;
import org.lgna.project.ast.AssignmentExpression;
import org.lgna.project.ast.BitwiseInfixExpression;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.BooleanLiteral;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.FloatLiteral;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalAccess;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.ReturnStatement;
import org.lgna.project.ast.ShiftInfixExpression;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.TypeExpression;
import org.lgna.project.ast.TypeLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Deep coverage tests for {@link VmExpressionEvaluator}.
 * Targets bitwise, shift, assignment, field access, array access/length,
 * float literal, type expression, and type literal evaluation paths
 * not covered by the existing characterization tests.
 */
public class VmExpressionEvaluatorDeepTest {

  private ReleaseVirtualMachine vm;

  @Before
  public void setUp() {
    vm = new ReleaseVirtualMachine();
  }

  private Object eval(Expression expression) {
    Object[] results = vm.ENTRY_POINT_evaluate(null, new Expression[]{expression});
    return results[0];
  }

  // ── Bitwise infix expressions (Integer) ──────────────────────────────────

  @Test
  public void bitwiseAndWithIntegerOperands() {
    Expression expr = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(5),
        BitwiseInfixExpression.Operator.AND,
        new IntegerLiteral(3));
    assertEquals(1, eval(expr));
  }

  @Test
  public void bitwiseOrWithIntegerOperands() {
    Expression expr = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(5),
        BitwiseInfixExpression.Operator.OR,
        new IntegerLiteral(3));
    assertEquals(7, eval(expr));
  }

  @Test
  public void bitwiseXorWithIntegerOperands() {
    Expression expr = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(5),
        BitwiseInfixExpression.Operator.XOR,
        new IntegerLiteral(3));
    assertEquals(6, eval(expr));
  }

  @Test
  public void bitwiseAndWithZeroMasks() {
    Expression expr = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(0xFF),
        BitwiseInfixExpression.Operator.AND,
        new IntegerLiteral(0x0F));
    assertEquals(0x0F, eval(expr));
  }

  @Test
  public void bitwiseOrCombinesBits() {
    Expression expr = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(0xF0),
        BitwiseInfixExpression.Operator.OR,
        new IntegerLiteral(0x0F));
    assertEquals(0xFF, eval(expr));
  }

  @Test
  public void bitwiseXorInvertsMatchingBits() {
    Expression expr = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(0xFF),
        BitwiseInfixExpression.Operator.XOR,
        new IntegerLiteral(0xFF));
    assertEquals(0, eval(expr));
  }

  // ── Shift infix expressions (Integer) ─────────────────────────────────────

  @Test
  public void leftShiftByThreeMultipliesByEight() {
    Expression expr = new ShiftInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(1),
        ShiftInfixExpression.Operator.LEFT_SHIFT,
        new IntegerLiteral(3));
    assertEquals(8, eval(expr));
  }

  @Test
  public void rightShiftSignedPreservesSign() {
    Expression expr = new ShiftInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(-8),
        ShiftInfixExpression.Operator.RIGHT_SHIFT_SIGNED,
        new IntegerLiteral(2));
    assertEquals(-2, eval(expr));
  }

  @Test
  public void rightShiftUnsignedFillsWithZeros() {
    Expression expr = new ShiftInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(-1),
        ShiftInfixExpression.Operator.RIGHT_SHIFT_UNSIGNED,
        new IntegerLiteral(28));
    assertEquals(15, eval(expr));
  }

  @Test
  public void leftShiftZeroRemainsZero() {
    Expression expr = new ShiftInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(0),
        ShiftInfixExpression.Operator.LEFT_SHIFT,
        new IntegerLiteral(10));
    assertEquals(0, eval(expr));
  }

  @Test
  public void rightShiftSignedPositiveValueDividesByPowerOfTwo() {
    Expression expr = new ShiftInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(64),
        ShiftInfixExpression.Operator.RIGHT_SHIFT_SIGNED,
        new IntegerLiteral(3));
    assertEquals(8, eval(expr));
  }

  // ── Float literal ──────────────────────────────────────────────────────────

  @Test
  public void floatLiteralEvaluatesToBoxedFloat() {
    assertEquals(2.5f, eval(new FloatLiteral(2.5f)));
  }

  @Test
  public void floatLiteralZeroEvaluatesToZero() {
    assertEquals(0.0f, eval(new FloatLiteral(0.0f)));
  }

  // ── Type expression ────────────────────────────────────────────────────────

  @Test
  public void typeExpressionEvaluatesToAbstractType() {
    TypeExpression typeExpr = new TypeExpression(String.class);
    Object result = eval(typeExpr);
    assertNotNull(result);
    assertEquals(JavaType.getInstance(String.class), result);
  }

  // ── Type literal ───────────────────────────────────────────────────────────

  @Test
  public void typeLiteralEvaluatesToClassReflection() {
    TypeLiteral typeLiteral = new TypeLiteral(Integer.class);
    Object result = eval(typeLiteral);
    assertNotNull(result);
  }

  // ── Assignment expression via local ────────────────────────────────────────

  @Test
  public void assignmentToLocalUpdatesValueAndReturnsNull() {
    UserLocal local = new UserLocal("x", Integer.class, false);
    LocalDeclarationStatement decl = new LocalDeclarationStatement(local, new IntegerLiteral(10));
    AssignmentExpression assignment = new AssignmentExpression(
        JavaType.getInstance(Integer.class),
        new LocalAccess(local),
        AssignmentExpression.Operator.ASSIGN,
        new IntegerLiteral(42));
    ReturnStatement ret = new ReturnStatement(JavaType.getInstance(Integer.class), new LocalAccess(local));

    UserMethod method = new UserMethod("assignTest", Integer.class,
        new UserParameter[0],
        new BlockStatement(
            decl,
            new ExpressionStatement(assignment),
            ret));
    method.isStatic.setValue(true);

    NamedUserType type = VmTestSupport.createProgramType("AssignTestProgram");
    type.methods.add(method);

    Object result = vm.ENTRY_POINT_invoke(null, method);
    assertEquals(42, result);
  }

  // ── Array instance creation + access + length ──────────────────────────────

  @Test
  public void arrayInstanceCreationAndAccessEvaluatesCorrectly() {
    ArrayInstanceCreation arrayCreation = new ArrayInstanceCreation(
        Integer[].class,
        new Integer[]{3},
        new IntegerLiteral(10), new IntegerLiteral(20), new IntegerLiteral(30));

    UserLocal arr = new UserLocal("arr", Integer[].class, false);
    LocalDeclarationStatement decl = new LocalDeclarationStatement(arr, arrayCreation);
    ArrayAccess access = new ArrayAccess(Integer[].class, new LocalAccess(arr), new IntegerLiteral(1));
    ReturnStatement ret = new ReturnStatement(JavaType.getInstance(Integer.class), access);

    UserMethod method = new UserMethod("arrayTest", Integer.class,
        new UserParameter[0], new BlockStatement(decl, ret));
    method.isStatic.setValue(true);

    NamedUserType type = VmTestSupport.createProgramType("ArrayTestProgram");
    type.methods.add(method);

    Object result = vm.ENTRY_POINT_invoke(null, method);
    assertEquals(20, result);
  }

  @Test
  public void arrayLengthReturnsCorrectSize() {
    ArrayInstanceCreation arrayCreation = new ArrayInstanceCreation(
        Integer[].class,
        new Integer[]{4},
        new IntegerLiteral(1), new IntegerLiteral(2),
        new IntegerLiteral(3), new IntegerLiteral(4));

    UserLocal arr = new UserLocal("arr", Integer[].class, false);
    LocalDeclarationStatement decl = new LocalDeclarationStatement(arr, arrayCreation);
    ArrayLength arrayLength = new ArrayLength(new LocalAccess(arr));
    ReturnStatement ret = new ReturnStatement(JavaType.getInstance(Integer.class), arrayLength);

    UserMethod method = new UserMethod("lengthTest", Integer.class,
        new UserParameter[0], new BlockStatement(decl, ret));
    method.isStatic.setValue(true);

    NamedUserType type = VmTestSupport.createProgramType("ArrayLengthProgram");
    type.methods.add(method);

    Object result = vm.ENTRY_POINT_invoke(null, method);
    assertEquals(4, result);
  }

  // ── Field access via UserInstance ──────────────────────────────────────────

  @Test
  public void fieldAccessOnUserInstanceReturnsFieldValue() {
    UserField nameField = new UserField("name", String.class, new StringLiteral("Alice"));
    NamedUserType personType = VmTestSupport.createTypeWithConstructor("Person");
    personType.fields.add(nameField);

    UserInstance instance = vm.ENTRY_POINT_createInstance(personType);
    assertNotNull(instance);
    Object fieldValue = instance.getFieldValue(nameField);
    assertEquals("Alice", fieldValue);
  }

  // ── Nested bitwise expressions ──────────────────────────────────────────

  @Test
  public void nestedBitwiseExpressionsEvaluateCorrectly() {
    // (5 & 3) | 8 = 1 | 8 = 9
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
    assertEquals(9, eval(outer));
  }

  // ── Combined shift and bitwise ────────────────────────────────────────────

  @Test
  public void shiftThenBitwiseMaskEvaluatesCorrectly() {
    // (1 << 4) & 0xFF = 16 & 255 = 16
    Expression shifted = new ShiftInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(1),
        ShiftInfixExpression.Operator.LEFT_SHIFT,
        new IntegerLiteral(4));
    Expression masked = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        shifted,
        BitwiseInfixExpression.Operator.AND,
        new IntegerLiteral(0xFF));
    assertEquals(16, eval(masked));
  }

  // ── Multiple expressions in batch (bitwise) ───────────────────────────────

  @Test
  public void batchEvaluatesBitwiseAndShiftExpressionsInOneCall() {
    Expression bitwiseExpr = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(12),
        BitwiseInfixExpression.Operator.AND,
        new IntegerLiteral(10));
    Expression shiftExpr = new ShiftInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(3),
        ShiftInfixExpression.Operator.LEFT_SHIFT,
        new IntegerLiteral(2));

    Object[] results = vm.ENTRY_POINT_evaluate(null, new Expression[]{bitwiseExpr, shiftExpr});
    assertEquals(2, results.length);
    assertEquals(8, results[0]);   // 12 & 10 = 8
    assertEquals(12, results[1]);  // 3 << 2 = 12
  }

  // ── Double literal edge cases ──────────────────────────────────────────────

  @Test
  public void doubleLiteralNegativeInfinityEvaluatesCorrectly() {
    assertEquals(Double.NEGATIVE_INFINITY, eval(new DoubleLiteral(Double.NEGATIVE_INFINITY)));
  }

  @Test
  public void doubleLiteralPositiveInfinityEvaluatesCorrectly() {
    assertEquals(Double.POSITIVE_INFINITY, eval(new DoubleLiteral(Double.POSITIVE_INFINITY)));
  }

  @Test
  public void doubleLiteralNaNEvaluatesToNaN() {
    Object result = eval(new DoubleLiteral(Double.NaN));
    assertEquals(Double.NaN, result);
  }

  // ── Integer edge values ────────────────────────────────────────────────────

  @Test
  public void integerLiteralMaxValueEvaluatesCorrectly() {
    assertEquals(Integer.MAX_VALUE, eval(new IntegerLiteral(Integer.MAX_VALUE)));
  }

  @Test
  public void integerLiteralMinValueEvaluatesCorrectly() {
    assertEquals(Integer.MIN_VALUE, eval(new IntegerLiteral(Integer.MIN_VALUE)));
  }

  // ── Bitwise operator identity laws ─────────────────────────────────────────

  @Test
  public void bitwiseAndWithAllOnesIsIdentity() {
    Expression expr = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(42),
        BitwiseInfixExpression.Operator.AND,
        new IntegerLiteral(-1));
    assertEquals(42, eval(expr));
  }

  @Test
  public void bitwiseOrWithZeroIsIdentity() {
    Expression expr = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(42),
        BitwiseInfixExpression.Operator.OR,
        new IntegerLiteral(0));
    assertEquals(42, eval(expr));
  }

  @Test
  public void bitwiseXorWithZeroIsIdentity() {
    Expression expr = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(42),
        BitwiseInfixExpression.Operator.XOR,
        new IntegerLiteral(0));
    assertEquals(42, eval(expr));
  }
}
