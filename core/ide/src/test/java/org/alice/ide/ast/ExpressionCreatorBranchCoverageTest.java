package org.alice.ide.ast;

import edu.cmu.cs.dennisc.java.lang.DoubleUtilities;
import org.junit.Test;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaField;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.TypeExpression;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class ExpressionCreatorBranchCoverageTest {
  private enum SampleEnum {
    ALPHA,
    BETA
  }

  public static final class PublicFieldOwner {
    public static final String LABEL = "label";
    public static final Double SCALE = 2.5;
    public int count = 4;
    private static final String SECRET = "secret";
  }

  private static class RecordingExpressionCreator extends ExpressionCreator {
    private Object lastCustomValue;
    private final Expression customExpression;

    private RecordingExpressionCreator(Expression customExpression) {
      this.customExpression = customExpression;
    }

    @Override
    protected Expression createCustomExpression(Object value) {
      this.lastCustomValue = value;
      return this.customExpression;
    }

    private Expression createDoubleValue(Double value) {
      return this.createDoubleExpression(value);
    }

    private Expression createDoubleValue(Double value, int decimalPlaces) {
      return this.createDoubleExpression(value, decimalPlaces);
    }

    private Expression createIntegerValue(Integer value) {
      return this.createIntegerExpression(value);
    }

    private FieldAccess createFieldAccess(Field field) {
      return this.createPublicStaticFieldAccess(field);
    }
  }

  private static class ThrowingExpressionCreator extends ExpressionCreator {
    @Override
    protected Expression createCustomExpression(Object value) throws CannotCreateExpressionException {
      throw new CannotCreateExpressionException(value);
    }
  }

  private DoubleLiteral assertDoubleLiteral(Expression expression) {
    assertTrue(expression instanceof DoubleLiteral);
    return (DoubleLiteral) expression;
  }

  private IntegerLiteral assertIntegerLiteral(Expression expression) {
    assertTrue(expression instanceof IntegerLiteral);
    return (IntegerLiteral) expression;
  }

  private StringLiteral assertStringLiteral(Expression expression) {
    assertTrue(expression instanceof StringLiteral);
    return (StringLiteral) expression;
  }

  private FieldAccess assertFieldAccess(Expression expression) {
    assertTrue(expression instanceof FieldAccess);
    return (FieldAccess) expression;
  }

  @Test
  public void milliDecimalPlacesConstantIsThree() {
    assertEquals(3, ExpressionCreator.MILLI_DECIMAL_PLACES);
  }

  @Test
  public void microDecimalPlacesConstantIsSix() {
    assertEquals(6, ExpressionCreator.MICRO_DECIMAL_PLACES);
  }

  @Test
  public void defaultDecimalPlacesMatchesMicroDecimalPlaces() {
    assertEquals(ExpressionCreator.MICRO_DECIMAL_PLACES, ExpressionCreator.DEFAULT_DECIMAL_PLACES);
  }

  @Test
  public void createDoubleExpressionForZeroUsesDoubleLiteral() {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    DoubleLiteral literal = assertDoubleLiteral(creator.createDoubleValue(0.0));
    assertEquals(0.0, literal.value.getValue(), 0.0);
  }

  @Test
  public void createDoubleExpressionForNegativeOneUsesDoubleLiteral() {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    DoubleLiteral literal = assertDoubleLiteral(creator.createDoubleValue(-1.0));
    assertEquals(-1.0, literal.value.getValue(), 0.0);
  }

  @Test
  public void createDoubleExpressionForMinValueRoundsUsingDefaultPrecision() {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    DoubleLiteral literal = assertDoubleLiteral(creator.createDoubleValue(Double.MIN_VALUE));
    assertEquals(DoubleUtilities.round(Double.MIN_VALUE, ExpressionCreator.DEFAULT_DECIMAL_PLACES), literal.value.getValue(), 0.0);
  }

  @Test
  public void createDoubleExpressionWithExplicitPrecisionRoundsValue() {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    DoubleLiteral literal = assertDoubleLiteral(creator.createDoubleValue(12.34567, ExpressionCreator.MILLI_DECIMAL_PLACES));
    assertEquals(DoubleUtilities.round(12.34567, ExpressionCreator.MILLI_DECIMAL_PLACES), literal.value.getValue(), 0.0);
  }

  @Test
  public void createIntegerExpressionForPositiveValueUsesIntegerLiteral() {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    IntegerLiteral literal = assertIntegerLiteral(creator.createIntegerValue(42));
    assertEquals(Integer.valueOf(42), literal.value.getValue());
  }

  @Test
  public void createIntegerExpressionForMinValueUsesIntegerLiteral() {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    IntegerLiteral literal = assertIntegerLiteral(creator.createIntegerValue(Integer.MIN_VALUE));
    assertEquals(Integer.valueOf(Integer.MIN_VALUE), literal.value.getValue());
  }

  @Test
  public void createStringExpressionForTextUsesStringLiteral() {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    StringLiteral literal = assertStringLiteral(creator.createStringExpression("hello"));
    assertEquals("hello", literal.value.getValue());
  }

  @Test
  public void createStringExpressionForSpecialCharactersPreservesText() {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    String value = "line1\nline2\t\u2603";
    StringLiteral literal = assertStringLiteral(creator.createStringExpression(value));
    assertEquals(value, literal.value.getValue());
  }

  @Test
  public void createStringExpressionForNullReturnsNullLiteral() {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    assertTrue(creator.createStringExpression(null) instanceof NullLiteral);
  }

  @Test
  public void createEnumExpressionForSampleEnumReturnsFieldAccess() {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    FieldAccess fieldAccess = assertFieldAccess(creator.createEnumExpression(SampleEnum.ALPHA));
    assertSame(JavaField.getInstance(SampleEnum.class, "ALPHA"), fieldAccess.field.getValue());
    assertTrue(fieldAccess.expression.getValue() instanceof TypeExpression);
    assertSame(org.lgna.project.ast.JavaType.getInstance(SampleEnum.class), ((TypeExpression) fieldAccess.expression.getValue()).value.getValue());
  }

  @Test
  public void createEnumExpressionForNullReturnsNullLiteral() {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    assertTrue(creator.createEnumExpression(null) instanceof NullLiteral);
  }

  @Test
  public void createExpressionDispatchesDoubleToDoubleLiteral() throws ExpressionCreator.CannotCreateExpressionException {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    assertTrue(creator.createExpression(3.5) instanceof DoubleLiteral);
  }

  @Test
  public void createExpressionDispatchesStringToStringLiteral() throws ExpressionCreator.CannotCreateExpressionException {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    assertTrue(creator.createExpression("dispatch") instanceof StringLiteral);
  }

  @Test
  public void createExpressionDispatchesNullToNullLiteral() throws ExpressionCreator.CannotCreateExpressionException {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    assertTrue(creator.createExpression(null) instanceof NullLiteral);
  }

  @Test
  public void createExpressionDelegatesUnsupportedObjectToCreateCustomExpression() throws ExpressionCreator.CannotCreateExpressionException {
    EmptyExpression customExpression = new EmptyExpression(Object.class);
    RecordingExpressionCreator creator = new RecordingExpressionCreator(customExpression);
    Object value = new Object();

    Expression expression = creator.createExpression(value);

    assertSame(customExpression, expression);
    assertSame(value, creator.lastCustomValue);
  }

  @Test
  public void createExpressionCanPropagateCannotCreateExpressionException() {
    ThrowingExpressionCreator creator = new ThrowingExpressionCreator();
    Object value = new Object();

    try {
      creator.createExpression(value);
      fail();
    } catch (ExpressionCreator.CannotCreateExpressionException e) {
      assertSame(value, e.getValue());
    }
  }

  @Test
  public void createPublicStaticFieldAccessForPublicStaticFieldCreatesFieldAccess() throws Exception {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    Field reflectedField = PublicFieldOwner.class.getField("LABEL");

    FieldAccess fieldAccess = creator.createFieldAccess(reflectedField);

    assertSame(JavaField.getInstance(reflectedField), fieldAccess.field.getValue());
    assertTrue(fieldAccess.expression.getValue() instanceof TypeExpression);
    assertSame(org.lgna.project.ast.JavaType.getInstance(PublicFieldOwner.class), ((TypeExpression) fieldAccess.expression.getValue()).value.getValue());
  }

  @Test
  public void createPublicStaticFieldAccessUsesFieldTypeAsAccessType() throws Exception {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    Field reflectedField = PublicFieldOwner.class.getField("SCALE");

    FieldAccess fieldAccess = creator.createFieldAccess(reflectedField);

    assertSame(org.lgna.project.ast.JavaType.DOUBLE_OBJECT_TYPE, fieldAccess.getType());
  }

  @Test
  public void createPublicStaticFieldAccessRejectsPublicNonStaticField() throws Exception {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    Field reflectedField = PublicFieldOwner.class.getField("count");

    try {
      creator.createFieldAccess(reflectedField);
      fail();
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("count"));
    }
  }

  @Test
  public void createPublicStaticFieldAccessRejectsPrivateStaticField() throws Exception {
    RecordingExpressionCreator creator = new RecordingExpressionCreator(new NullLiteral());
    Field reflectedField = PublicFieldOwner.class.getDeclaredField("SECRET");

    try {
      creator.createFieldAccess(reflectedField);
      fail();
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("SECRET"));
    }
  }
}
