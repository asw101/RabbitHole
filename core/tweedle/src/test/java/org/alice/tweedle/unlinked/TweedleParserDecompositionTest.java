package org.alice.tweedle.unlinked;

import org.alice.tweedle.*;
import org.alice.tweedle.ast.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Characterization tests for the TweedleUnlinkedParser decomposition.
 * These tests verify the structural contracts of the extraction:
 * - ExpressionVisitor, StatementVisitor, BinaryConstructor are top-level package-private types
 * - Parser utility methods are accessible at package scope
 * - Extracted visitors produce identical results when wired to a parser instance
 *
 * These tests would FAIL if the extraction were reverted to inner classes.
 */
public class TweedleParserDecompositionTest {

  private TweedleUnlinkedParser parser;

  @Before
  public void setUp() {
    parser = new TweedleUnlinkedParser();
  }

  // ── Structural: ExpressionVisitor ──────────────────────────

  @Test
  public void expressionVisitorShouldBeTopLevelClass() {
    assertFalse("ExpressionVisitor must not be an inner class",
        ExpressionVisitor.class.isMemberClass());
  }

  @Test
  public void expressionVisitorShouldBePackagePrivate() {
    int modifiers = ExpressionVisitor.class.getModifiers();
    assertFalse("ExpressionVisitor must not be public", Modifier.isPublic(modifiers));
    assertFalse("ExpressionVisitor must not be protected", Modifier.isProtected(modifiers));
    assertFalse("ExpressionVisitor must not be private", Modifier.isPrivate(modifiers));
  }

  @Test
  public void expressionVisitorShouldAcceptParserInConstructor() {
    ExpressionVisitor visitor = new ExpressionVisitor(parser);
    assertNotNull("ExpressionVisitor should be constructable with a parser", visitor);
  }

  @Test
  public void expressionVisitorShouldAcceptParserAndExpectedType() {
    ExpressionVisitor visitor = new ExpressionVisitor(parser, TweedleTypes.WHOLE_NUMBER);
    assertNotNull("ExpressionVisitor should accept parser + expectedType", visitor);
  }

  @Test
  public void expressionVisitorShouldAcceptParserExpectedTypeAndAllowPrimitiveNull() {
    ExpressionVisitor visitor = new ExpressionVisitor(parser, TweedleTypes.WHOLE_NUMBER, true);
    assertNotNull("ExpressionVisitor should accept parser + expectedType + allowPrimitiveNull", visitor);
  }

  // ── Structural: StatementVisitor ───────────────────────────

  @Test
  public void statementVisitorShouldBeTopLevelClass() {
    assertFalse("StatementVisitor must not be an inner class",
        StatementVisitor.class.isMemberClass());
  }

  @Test
  public void statementVisitorShouldBePackagePrivate() {
    int modifiers = StatementVisitor.class.getModifiers();
    assertFalse("StatementVisitor must not be public", Modifier.isPublic(modifiers));
    assertFalse("StatementVisitor must not be protected", Modifier.isProtected(modifiers));
    assertFalse("StatementVisitor must not be private", Modifier.isPrivate(modifiers));
  }

  @Test
  public void statementVisitorShouldAcceptParserInConstructor() {
    StatementVisitor visitor = new StatementVisitor(parser);
    assertNotNull("StatementVisitor should be constructable with a parser", visitor);
  }

  // ── Structural: BinaryConstructor ──────────────────────────

  @Test
  public void binaryConstructorShouldBeTopLevelInterface() {
    assertTrue("BinaryConstructor must be an interface", BinaryConstructor.class.isInterface());
    assertFalse("BinaryConstructor must not be an inner class",
        BinaryConstructor.class.isMemberClass());
  }

  @Test
  public void binaryConstructorShouldBePackagePrivate() {
    int modifiers = BinaryConstructor.class.getModifiers();
    assertFalse("BinaryConstructor must not be public", Modifier.isPublic(modifiers));
  }

  @Test
  public void binaryConstructorShouldBeFunctionalInterface() {
    assertNotNull("BinaryConstructor must have @FunctionalInterface annotation",
        BinaryConstructor.class.getAnnotation(FunctionalInterface.class));
  }

  @Test
  public void binaryConstructorShouldBeUsableAsLambda() {
    TweedleExpression lhs = TweedleTypes.WHOLE_NUMBER.createValue(3);
    TweedleExpression rhs = TweedleTypes.WHOLE_NUMBER.createValue(4);
    BinaryConstructor ctor = AdditionExpression::new;
    BinaryExpression result = ctor.newBinExp(lhs, rhs);
    assertNotNull("BinaryConstructor lambda should produce a BinaryExpression", result);
    assertTrue("Result should be an AdditionExpression", result instanceof AdditionExpression);
  }

  // ── Utility: getPrimitiveType ──────────────────────────────

  @Test
  public void getPrimitiveTypeShouldReturnWholeNumber() {
    assertEquals(TweedleTypes.WHOLE_NUMBER, parser.getPrimitiveType("WholeNumber"));
  }

  @Test
  public void getPrimitiveTypeShouldReturnDecimalNumber() {
    assertEquals(TweedleTypes.DECIMAL_NUMBER, parser.getPrimitiveType("DecimalNumber"));
  }

  @Test
  public void getPrimitiveTypeShouldReturnBoolean() {
    assertEquals(TweedleTypes.BOOLEAN, parser.getPrimitiveType("Boolean"));
  }

  @Test
  public void getPrimitiveTypeShouldReturnTextString() {
    assertEquals(TweedleTypes.TEXT_STRING, parser.getPrimitiveType("TextString"));
  }

  @Test
  public void getPrimitiveTypeShouldReturnNumber() {
    assertEquals(TweedleTypes.NUMBER, parser.getPrimitiveType("Number"));
  }

  @Test
  public void getPrimitiveTypeShouldReturnNullForUnknown() {
    assertNull("Unknown type name should return null", parser.getPrimitiveType("Foo"));
  }

  @Test
  public void getPrimitiveTypeShouldReturnNullForEmpty() {
    assertNull("Empty type name should return null", parser.getPrimitiveType(""));
  }

  // ── Utility: getTypeReference ──────────────────────────────

  @Test
  public void getTypeReferenceShouldReturnReferenceWithCorrectName() {
    TweedleTypeReference ref = parser.getTypeReference("SModel");
    assertNotNull("getTypeReference should return a non-null reference", ref);
    assertEquals("SModel", ref.getName());
  }

  @Test
  public void getTypeReferenceShouldCreateDistinctInstances() {
    TweedleTypeReference ref1 = parser.getTypeReference("Foo");
    TweedleTypeReference ref2 = parser.getTypeReference("Foo");
    assertNotSame("Each call should create a new instance", ref1, ref2);
  }

  // ── Utility: visitLabeledArguments ─────────────────────────

  @Test
  public void visitLabeledArgumentsShouldReturnEmptyMapForNull() {
    Map<String, TweedleExpression> result = parser.visitLabeledArguments(null);
    assertNotNull("Result should not be null", result);
    assertTrue("Result should be empty for null input", result.isEmpty());
  }

  // ── Utility: visitUnlabeledArguments ───────────────────────

  @Test
  public void visitUnlabeledArgumentsShouldReturnEmptyListForNull() {
    ExpressionVisitor visitor = new ExpressionVisitor(parser);
    List<TweedleExpression> result = parser.visitUnlabeledArguments(null, visitor);
    assertNotNull("Result should not be null", result);
    assertTrue("Result should be empty for null listContext", result.isEmpty());
  }

  // ── Parser line count contract ─────────────────────────────

  @Test
  public void parserClassShouldNotContainExpressionVisitorAsInnerClass() {
    for (Class<?> inner : TweedleUnlinkedParser.class.getDeclaredClasses()) {
      assertNotEquals("ExpressionVisitor must not be an inner class of parser",
          "ExpressionVisitor", inner.getSimpleName());
    }
  }

  @Test
  public void parserClassShouldNotContainStatementVisitorAsInnerClass() {
    for (Class<?> inner : TweedleUnlinkedParser.class.getDeclaredClasses()) {
      assertNotEquals("StatementVisitor must not be an inner class of parser",
          "StatementVisitor", inner.getSimpleName());
    }
  }

  @Test
  public void parserClassShouldNotContainBinaryConstructorAsInnerClass() {
    for (Class<?> inner : TweedleUnlinkedParser.class.getDeclaredClasses()) {
      assertNotEquals("BinaryConstructor must not be an inner class of parser",
          "BinaryConstructor", inner.getSimpleName());
    }
  }

  // ── Integration: parsing through extracted visitors ────────

  @Test
  public void expressionVisitorShouldParseIntegerLiteral() {
    TweedleExpression result = parser.parseExpression("42");
    assertNotNull(result);
    assertTrue(result instanceof TweedlePrimitiveValue);
    assertEquals(42, ((TweedlePrimitiveValue<?>) result).getPrimitiveValue());
  }

  @Test
  public void expressionVisitorShouldParseBinaryOperation() {
    TweedleExpression result = parser.parseExpression("3 + 4");
    assertTrue("Should produce AdditionExpression", result instanceof AdditionExpression);
    assertEquals(7, ((TweedlePrimitiveValue<?>) ((AdditionExpression) result).evaluate(null)).getPrimitiveValue());
  }

  @Test
  public void expressionVisitorShouldParseStringLiteral() {
    TweedleExpression result = parser.parseExpression("\"hello\"");
    assertTrue(result instanceof TweedlePrimitiveValue);
    assertEquals("hello", ((TweedlePrimitiveValue<?>) result).getPrimitiveValue());
  }

  @Test
  public void expressionVisitorShouldParseNullLiteral() {
    TweedleExpression result = parser.parseExpression("null");
    assertSame(TweedleNull.NULL, result);
  }

  @Test
  public void expressionVisitorShouldParseBooleanLiteral() {
    TweedleExpression result = parser.parseExpression("true");
    assertTrue(result instanceof TweedlePrimitiveValue);
    assertEquals(true, ((TweedlePrimitiveValue<?>) result).getPrimitiveValue());
  }

  @Test
  public void expressionVisitorShouldParseThisExpression() {
    TweedleExpression result = parser.parseExpression("this");
    assertTrue(result instanceof ThisExpression);
  }

  @Test
  public void expressionVisitorShouldParseFieldAccess() {
    TweedleExpression result = parser.parseExpression("this.myField");
    assertTrue(result instanceof FieldAccess);
    assertEquals("myField", ((FieldAccess) result).getFieldName());
  }

  @Test
  public void expressionVisitorShouldParseMethodCall() {
    TweedleExpression result = parser.parseExpression("this.myMethod()");
    assertTrue(result instanceof MethodCallExpression);
    assertEquals("myMethod", ((MethodCallExpression) result).getMethodName());
  }

  @Test
  public void expressionVisitorShouldParseNewObjectCreation() {
    TweedleExpression result = parser.parseExpression("new Foo()");
    assertTrue(result instanceof Instantiation);
  }

  @Test
  public void expressionVisitorShouldParsePrimitiveArrayInitializer() {
    TweedleExpression result = parser.parseExpression("new WholeNumber[] {1, 2, 3}");
    assertTrue(result instanceof TweedleArrayInitializer);
  }

  @Test
  public void expressionVisitorShouldParseAssignment() {
    TweedleExpression result = parser.parseExpression("x <- 5");
    assertTrue(result instanceof AssignmentExpression);
  }

  @Test
  public void expressionVisitorShouldParseNegation() {
    TweedleExpression result = parser.parseExpression("-3");
    assertTrue(result instanceof TweedlePrimitiveValue);
    assertEquals(-3, ((TweedlePrimitiveValue<?>) result).getPrimitiveValue());
  }

  @Test
  public void expressionVisitorShouldParseLogicalNot() {
    TweedleExpression result = parser.parseExpression("!true");
    assertTrue("!true should produce a LogicalNotExpression", result instanceof LogicalNotExpression);
    assertEquals(TweedleTypes.BOOLEAN, result.getType());
  }

  @Test
  public void expressionVisitorShouldParseConcatenation() {
    TweedleExpression result = parser.parseExpression("\"a\" .. \"b\"");
    assertTrue(result instanceof StringConcatenationExpression);
  }

  // ── Integration: StatementVisitor ──────────────────────────

  @Test
  public void statementVisitorShouldParseReturnStatement() {
    TweedleStatement result = parser.parseStatement("return 5");
    assertTrue(result instanceof ReturnStatement);
  }

  @Test
  public void statementVisitorShouldParseReturnVoid() {
    TweedleStatement result = parser.parseStatement("return");
    assertTrue(result instanceof ReturnStatement);
  }

  @Test
  public void statementVisitorShouldParseLocalVariableDeclaration() {
    TweedleStatement result = parser.parseStatement("WholeNumber x <- 5");
    assertTrue(result instanceof LocalVariableDeclaration);
  }

  @Test
  public void statementVisitorShouldParseConstantDeclaration() {
    TweedleStatement result = parser.parseStatement("constant WholeNumber x <- 5");
    assertTrue(result instanceof LocalVariableDeclaration);
  }

  @Test
  public void statementVisitorShouldParseIfStatement() {
    TweedleStatement result = parser.parseStatement("if(true) { }");
    assertTrue(result instanceof ConditionalStatement);
  }

  @Test
  public void statementVisitorShouldParseIfElseStatement() {
    TweedleStatement result = parser.parseStatement("if(true) { } else { }");
    assertTrue(result instanceof ConditionalStatement);
    ConditionalStatement cond = (ConditionalStatement) result;
    assertNotNull(cond.getElseBlock());
  }

  @Test
  public void statementVisitorShouldParseCountUpLoop() {
    TweedleStatement result = parser.parseStatement("countUpTo( idx < 3 ) {}");
    assertTrue(result instanceof CountUpLoop);
  }

  @Test
  public void statementVisitorShouldParseForEachLoop() {
    TweedleStatement result = parser.parseStatement("forEach(WholeNumber item in items) {}");
    assertTrue(result instanceof ForEachLoop);
  }

  @Test
  public void statementVisitorShouldParseWhileLoop() {
    TweedleStatement result = parser.parseStatement("while(true) {}");
    assertTrue(result instanceof WhileLoop);
  }

  @Test
  public void statementVisitorShouldParseDoInOrder() {
    TweedleStatement result = parser.parseStatement("doInOrder {}");
    assertTrue(result instanceof DoInOrder);
  }

  @Test
  public void statementVisitorShouldParseDoTogether() {
    TweedleStatement result = parser.parseStatement("doTogether {}");
    assertTrue(result instanceof DoTogether);
  }

  @Test
  public void statementVisitorShouldParseExpressionStatement() {
    TweedleStatement result = parser.parseStatement("this.myMethod()");
    assertTrue(result instanceof ExpressionStatement);
  }

  // ── Integration: full type parsing with extracted visitors ─

  @Test
  public void parserShouldParseClassWithMethodUsingExtractedVisitors() {
    TweedleType result = parser.parseType(
        "class Foo {\n  void bar() {\n    return\n  }\n}");
    assertTrue(result instanceof TweedleClass);
    TweedleClass cls = (TweedleClass) result;
    assertEquals("Foo", cls.getName());
    assertEquals(1, cls.getMethods().size());
    assertEquals("bar", cls.getMethods().getFirst().getName());
  }

  @Test
  public void parserShouldParseClassWithFieldUsingExtractedVisitors() {
    TweedleType result = parser.parseType(
        "class Foo {\n  WholeNumber count <- 0\n}");
    assertTrue(result instanceof TweedleClass);
    TweedleClass cls = (TweedleClass) result;
    assertEquals(1, cls.getProperties().size());
    assertEquals("count", cls.getProperties().getFirst().getName());
  }

  @Test
  public void parserShouldParseClassWithConstructorUsingExtractedVisitors() {
    TweedleType result = parser.parseType(
        "class Foo {\n  Foo() {\n  }\n}");
    assertTrue(result instanceof TweedleClass);
    TweedleClass cls = (TweedleClass) result;
    assertEquals(1, cls.getConstructors().size());
  }

  @Test
  public void parserShouldParseEnumUsingExtractedVisitors() {
    TweedleType result = parser.parseType(
        "enum Color { RED, GREEN, BLUE }");
    assertTrue(result instanceof TweedleEnum);
    TweedleEnum enm = (TweedleEnum) result;
    assertEquals("Color", enm.getName());
  }

  @Test
  public void parserShouldParseSubclassUsingExtractedVisitors() {
    TweedleType result = parser.parseType(
        "class SScene extends SThing {}");
    assertTrue(result instanceof TweedleClass);
  }

  // ── Edge cases ─────────────────────────────────────────────

  @Test
  public void binaryConstructorShouldWorkWithAllOperatorTypes() {
    TweedleExpression lhs = TweedleTypes.WHOLE_NUMBER.createValue(10);
    TweedleExpression rhs = TweedleTypes.WHOLE_NUMBER.createValue(3);

    BinaryConstructor sub = SubtractionExpression::new;
    BinaryConstructor mul = MultiplicationExpression::new;
    BinaryConstructor div = DivisionExpression::new;
    BinaryConstructor mod = ModuloExpression::new;

    assertTrue(sub.newBinExp(lhs, rhs) instanceof SubtractionExpression);
    assertTrue(mul.newBinExp(lhs, rhs) instanceof MultiplicationExpression);
    assertTrue(div.newBinExp(lhs, rhs) instanceof DivisionExpression);
    assertTrue(mod.newBinExp(lhs, rhs) instanceof ModuloExpression);
  }

  @Test
  public void binaryConstructorShouldWorkWithComparisonOperators() {
    TweedleExpression lhs = TweedleTypes.WHOLE_NUMBER.createValue(3);
    TweedleExpression rhs = TweedleTypes.WHOLE_NUMBER.createValue(4);

    BinaryConstructor lt = LessThanExpression::new;
    BinaryConstructor gt = GreaterThanExpression::new;
    BinaryConstructor le = LessThanOrEqualExpression::new;
    BinaryConstructor ge = GreaterThanOrEqualExpression::new;
    BinaryConstructor eq = EqualToExpression::new;
    BinaryConstructor ne = NotEqualToExpression::new;

    assertTrue(lt.newBinExp(lhs, rhs) instanceof LessThanExpression);
    assertTrue(gt.newBinExp(lhs, rhs) instanceof GreaterThanExpression);
    assertTrue(le.newBinExp(lhs, rhs) instanceof LessThanOrEqualExpression);
    assertTrue(ge.newBinExp(lhs, rhs) instanceof GreaterThanOrEqualExpression);
    assertTrue(eq.newBinExp(lhs, rhs) instanceof EqualToExpression);
    assertTrue(ne.newBinExp(lhs, rhs) instanceof NotEqualToExpression);
  }

  @Test
  public void binaryConstructorShouldWorkWithLogicalOperators() {
    TweedleExpression lhs = TweedleTypes.BOOLEAN.createValue(true);
    TweedleExpression rhs = TweedleTypes.BOOLEAN.createValue(false);

    BinaryConstructor and = LogicalAndExpression::new;
    BinaryConstructor or = LogicalOrExpression::new;

    assertTrue(and.newBinExp(lhs, rhs) instanceof LogicalAndExpression);
    assertTrue(or.newBinExp(lhs, rhs) instanceof LogicalOrExpression);
  }

  @Test
  public void expressionVisitorShouldParseMethodCallWithArguments() {
    TweedleExpression result = parser.parseExpression("this.move(direction: north, amount: 1)");
    assertTrue(result instanceof MethodCallExpression);
    MethodCallExpression call = (MethodCallExpression) result;
    assertEquals("move", call.getMethodName());
  }

  @Test
  public void expressionVisitorShouldParseCompoundExpression() {
    TweedleExpression result = parser.parseExpression("(1 + 2) * 3");
    assertNotNull(result);
    assertTrue(result instanceof MultiplicationExpression);
  }

  @Test
  public void expressionVisitorShouldParseDecimalLiteral() {
    TweedleExpression result = parser.parseExpression("3.14");
    assertTrue(result instanceof TweedlePrimitiveValue);
    assertEquals(3.14, ((TweedlePrimitiveValue<?>) result).getPrimitiveValue());
  }

  @Test
  public void expressionVisitorShouldParseObjectArrayInitializer() {
    TweedleExpression result = parser.parseExpression("new SModel[] {this.a, this.b}");
    assertTrue(result instanceof TweedleArrayInitializer);
  }
}
