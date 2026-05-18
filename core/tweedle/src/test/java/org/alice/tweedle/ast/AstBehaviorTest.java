package org.alice.tweedle.ast;

import org.alice.tweedle.*;
import org.alice.tweedle.run.Frame;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class AstBehaviorTest {

  private static final class SpyExpression extends TweedleExpression {
    private final TweedleValue value;
    private boolean evaluated;

    private SpyExpression(TweedleValue value) {
      super(value.getType());
      this.value = value;
    }

    @Override
    public TweedleValue evaluate(Frame frame) {
      evaluated = true;
      return value;
    }
  }

  private static final class MemberProbe extends MemberAccessExpression {
    private MemberProbe(TweedleExpression target) {
      super(target);
    }

    @Override
    public TweedleValue evaluate(Frame frame) {
      return evaluateTarget(frame);
    }
  }

  @Test
  public void arithmeticExpressionsEvaluateAcrossNumericBranches() {
    TweedleExpression one = TweedleTypes.WHOLE_NUMBER.createValue(1);
    TweedleExpression two = TweedleTypes.WHOLE_NUMBER.createValue(2);
    TweedleExpression half = TweedleTypes.DECIMAL_NUMBER.createValue(0.5);

    assertEquals(Integer.valueOf(3), ((TweedlePrimitiveValue<Integer>) new AdditionExpression(one, two).evaluate(null)).getPrimitiveValue());
    assertEquals(Integer.valueOf(-1), ((TweedlePrimitiveValue<Integer>) new SubtractionExpression(one, two).evaluate(null)).getPrimitiveValue());
    assertEquals(Integer.valueOf(2), ((TweedlePrimitiveValue<Integer>) new MultiplicationExpression(one, two).evaluate(null)).getPrimitiveValue());
    assertEquals(Integer.valueOf(2), ((TweedlePrimitiveValue<Integer>) new DivisionExpression(TweedleTypes.WHOLE_NUMBER.createValue(5), two).evaluate(null)).getPrimitiveValue());
    assertEquals(Integer.valueOf(1), ((TweedlePrimitiveValue<Integer>) new ModuloExpression(TweedleTypes.WHOLE_NUMBER.createValue(5), two).evaluate(null)).getPrimitiveValue());
    assertEquals(Double.valueOf(2.5), ((TweedlePrimitiveValue<Double>) new AdditionExpression(two, half).evaluate(null)).getPrimitiveValue());
    assertSame(TweedleTypes.WHOLE_NUMBER, new AdditionExpression(one, two).getType());
    assertSame(TweedleTypes.DECIMAL_NUMBER, new AdditionExpression(two, half).getType());
  }

  @Test
  public void comparisonAndLogicalExpressionsEvaluateToBooleans() {
    TweedleExpression trueValue = TweedleTypes.TRUE;
    TweedleExpression falseValue = TweedleTypes.FALSE;

    assertEquals(Boolean.TRUE, ((TweedlePrimitiveValue<Boolean>) new LessThanExpression(TweedleTypes.WHOLE_NUMBER.createValue(1), TweedleTypes.WHOLE_NUMBER.createValue(2)).evaluate(null)).getPrimitiveValue());
    assertEquals(Boolean.TRUE, ((TweedlePrimitiveValue<Boolean>) new LessThanOrEqualExpression(TweedleTypes.WHOLE_NUMBER.createValue(2), TweedleTypes.WHOLE_NUMBER.createValue(2)).evaluate(null)).getPrimitiveValue());
    assertEquals(Boolean.TRUE, ((TweedlePrimitiveValue<Boolean>) new GreaterThanExpression(TweedleTypes.WHOLE_NUMBER.createValue(3), TweedleTypes.WHOLE_NUMBER.createValue(2)).evaluate(null)).getPrimitiveValue());
    assertEquals(Boolean.TRUE, ((TweedlePrimitiveValue<Boolean>) new GreaterThanOrEqualExpression(TweedleTypes.WHOLE_NUMBER.createValue(2), TweedleTypes.WHOLE_NUMBER.createValue(2)).evaluate(null)).getPrimitiveValue());
    assertEquals(Boolean.TRUE, ((TweedlePrimitiveValue<Boolean>) new EqualToExpression(TweedleTypes.WHOLE_NUMBER.createValue(3), TweedleTypes.WHOLE_NUMBER.createValue(3)).evaluate(null)).getPrimitiveValue());
    assertEquals(Boolean.TRUE, ((TweedlePrimitiveValue<Boolean>) new NotEqualToExpression(TweedleTypes.WHOLE_NUMBER.createValue(3), TweedleTypes.WHOLE_NUMBER.createValue(4)).evaluate(null)).getPrimitiveValue());
    assertEquals(Boolean.FALSE, ((TweedlePrimitiveValue<Boolean>) new LogicalAndExpression<>(trueValue, falseValue).evaluate(null)).getPrimitiveValue());
    assertEquals(Boolean.TRUE, ((TweedlePrimitiveValue<Boolean>) new LogicalOrExpression<>(trueValue, falseValue).evaluate(null)).getPrimitiveValue());
    assertEquals(Boolean.TRUE, ((TweedlePrimitiveValue<Boolean>) new LogicalNotExpression(falseValue).evaluate(null)).getPrimitiveValue());
  }

  @Test
  public void stringAndNegativeExpressionsPreserveCurrentBehavior() {
    StringConcatenationExpression concat = new StringConcatenationExpression(TweedleTypes.TEXT_STRING.createValue("hello"), TweedleTypes.WHOLE_NUMBER.createValue(5));
    NegativeExpression negativeWhole = new NegativeExpression(TweedleTypes.WHOLE_NUMBER.createValue(4));
    NegativeExpression negativeDecimal = new NegativeExpression(TweedleTypes.DECIMAL_NUMBER.createValue(4.5));
    NegativeExpression unsupported = new NegativeExpression(TweedleTypes.NUMBER.createValue(Long.valueOf(7L)));

    assertEquals("hello5", ((TweedlePrimitiveValue<String>) concat.evaluate(null)).getPrimitiveValue());
    assertEquals(Integer.valueOf(-4), ((TweedlePrimitiveValue<Integer>) negativeWhole.evaluate(null)).getPrimitiveValue());
    assertEquals(Double.valueOf(-4.5), ((TweedlePrimitiveValue<Double>) negativeDecimal.evaluate(null)).getPrimitiveValue());
    assertThrows(RuntimeException.class, () -> unsupported.evaluate(null));
  }

  @Test
  public void assignmentAndMemberAccessExpressionsExposeTargetsAndArguments() {
    SpyExpression target = new SpyExpression(TweedleTypes.TEXT_STRING.createValue("target"));
    SpyExpression value = new SpyExpression(TweedleTypes.WHOLE_NUMBER.createValue(7));
    AssignmentExpression assignment = new AssignmentExpression(target, value);
    FieldAccess fieldAccess = new FieldAccess(target, "name");
    MethodCallExpression methodCall = new MethodCallExpression(target, "move", Collections.singletonMap("amount", value), false);
    MemberProbe memberProbe = new MemberProbe(target);

    assertSame(target, assignment.getAssigneeExp());
    assertSame(value, assignment.getValueExp());
    assertEquals(Integer.valueOf(7), ((TweedlePrimitiveValue<Integer>) assignment.evaluate(null)).getPrimitiveValue());
    assertTrue(value.evaluated);

    assertEquals("name", fieldAccess.getFieldName());
    assertNull(fieldAccess.evaluate(null));
    assertTrue(target.evaluated);

    target.evaluated = false;
    assertEquals("move", methodCall.getMethodName());
    assertSame(value, methodCall.getArg("amount"));
    assertEquals(1, methodCall.getArguments().size());
    assertFalse(methodCall.hasExplicitTarget());
    assertThrows(UnsupportedOperationException.class, () -> methodCall.getArguments().put("other", value));
    assertNull(methodCall.evaluate(null));
    assertTrue(target.evaluated);

    assertSame(target, memberProbe.getTarget());
    assertSame(target.value, memberProbe.evaluate(null));
  }

  @Test
  public void simpleReferenceExpressionsRemainUnevaluated() {
    IdentifierReference identifier = new IdentifierReference("count");
    ThisExpression thisExpression = new ThisExpression();
    SuperExpression superExpression = new SuperExpression();
    ArrayIndexExpression arrayIndex = new ArrayIndexExpression(TweedleTypes.WHOLE_NUMBER, TweedleNull.NULL, TweedleTypes.WHOLE_NUMBER.createValue(0));
    Instantiation typeInstantiation = new Instantiation(new TweedleTypeReference("Scene"), Collections.emptyMap());
    Instantiation superInstantiation = new Instantiation(new SuperExpression(), Collections.emptyMap());
    LambdaExpression lambdaExpression = new LambdaExpression(List.of(new TweedleRequiredParameter(TweedleTypes.WHOLE_NUMBER, "value")), List.of(new ReturnStatement()));
    LambdaEvaluation noArgEvaluation = new LambdaEvaluation(identifier);
    LambdaEvaluation argEvaluation = new LambdaEvaluation(identifier, List.of(TweedleTypes.WHOLE_NUMBER.createValue(1)));

    assertEquals("count", identifier.getName());
    assertNull(identifier.evaluate(null));
    assertNull(thisExpression.evaluate(null));
    assertNull(superExpression.evaluate(null));
    assertEquals("super", superExpression.getName());
    superExpression.invoke(new Frame(TweedleNull.NULL), null, null, new TweedleValue[0]);
    assertNull(arrayIndex.evaluate(null));
    assertEquals("newScene()", typeInstantiation.toString());
    assertNull(typeInstantiation.evaluate(null));
    assertNull(superInstantiation.evaluate(null));
    assertNull(lambdaExpression.evaluate(null));
    assertEquals(1, lambdaExpression.getParameters().size());
    assertEquals(1, lambdaExpression.getStatements().size());
    assertNull(noArgEvaluation.evaluate(null));
    assertNull(argEvaluation.evaluate(null));
  }

  @Test
  public void arrayInitializerAndStatementContainersExposeState() {
    TweedleArrayInitializer sizedArray = new TweedleArrayInitializer(new TweedleArrayType(TweedleTypes.WHOLE_NUMBER), TweedleTypes.WHOLE_NUMBER.createValue(3));
    TweedleArrayInitializer initializedArray = new TweedleArrayInitializer(TweedleTypes.WHOLE_NUMBER, List.of(TweedleTypes.WHOLE_NUMBER.createValue(1), TweedleTypes.WHOLE_NUMBER.createValue(2)));
    TweedleArray evaluated = (TweedleArray) initializedArray.evaluate(null);

    assertFalse(sizedArray.hasElementInitializers());
    assertTrue(sizedArray.getElements().isEmpty());
    assertEquals(Integer.valueOf(3), ((TweedlePrimitiveValue<Integer>) sizedArray.getInitializeSize()).getPrimitiveValue());
    assertTrue(initializedArray.hasElementInitializers());
    assertEquals(2, initializedArray.getElements().size());
    assertEquals(2, evaluated.length());
    assertTrue(initializedArray.toString().contains("WholeNumber[]"));

    TweedleLocalVariable local = new TweedleLocalVariable(TweedleTypes.TEXT_STRING, "name", TweedleTypes.TEXT_STRING.createValue("Ada"));
    LocalVariableDeclaration declaration = new LocalVariableDeclaration(true, local);
    ExpressionStatement expressionStatement = new ExpressionStatement(TweedleTypes.WHOLE_NUMBER.createValue(1));
    ReturnStatement defaultReturn = new ReturnStatement();
    ReturnStatement valueReturn = new ReturnStatement(TweedleTypes.TEXT_STRING.createValue("done"));
    ConditionalStatement conditional = new ConditionalStatement(TweedleTypes.TRUE, List.of(expressionStatement), List.of(defaultReturn));
    CountUpLoop countUpLoop = new CountUpLoop("i", TweedleTypes.WHOLE_NUMBER.createValue(3), List.of(expressionStatement));
    ForEachLoop forEachLoop = new ForEachLoop(local, initializedArray, List.of(expressionStatement));
    ForEachTogether forEachTogether = new ForEachTogether(local, initializedArray, List.of(expressionStatement));
    WhileLoop whileLoop = new WhileLoop(TweedleTypes.TRUE, List.of(expressionStatement));
    DoInOrder doInOrder = new DoInOrder(List.of(expressionStatement));
    DoTogether doTogether = new DoTogether(List.of(expressionStatement));

    assertTrue(declaration.isConstant());
    assertSame(local, declaration.getDeclaration());
    assertSame(TweedleTypes.TEXT_STRING, local.getType());
    assertEquals("name", local.getName());
    assertNotNull(local.getInitializer());
    assertSame(expressionStatement.getExpression(), expressionStatement.getExpression());
    assertSame(TweedleNull.NULL, defaultReturn.getExpression());
    assertSame(TweedleVoidType.VOID, defaultReturn.getType());
    assertEquals("done", ((TweedlePrimitiveValue<String>) valueReturn.getExpression()).getPrimitiveValue());
    assertSame(TweedleTypes.TRUE, conditional.getCondition());
    assertEquals(1, conditional.getThenBlock().size());
    assertEquals(1, conditional.getElseBlock().size());
    assertEquals("i", countUpLoop.getLoopVariable().getName());
    assertEquals(1, countUpLoop.getStatements().size());
    assertSame(local, forEachLoop.getLoopVar());
    assertSame(initializedArray, forEachLoop.getLoopValues());
    assertSame(local, forEachTogether.getLoopVar());
    assertSame(initializedArray, forEachTogether.getLoopValues());
    assertSame(TweedleTypes.TRUE, whileLoop.getRunCondition());
    assertEquals(1, whileLoop.getStatements().size());
    assertEquals(1, doInOrder.getStatements().size());
    assertEquals(1, doTogether.getStatements().size());
  }
}
