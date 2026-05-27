package org.lgna.project.ast;

import org.junit.Test;
import org.lgna.project.code.CodeOrganizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class ShiftInfixExpressionOperatorTest {
  @Test
  public void operatorsCoverSignedUnsignedAndLongShifts() {
    assertEquals(12, ((Number) ShiftInfixExpression.Operator.LEFT_SHIFT.operate(3, 2)).intValue());
    assertEquals(-4, ((Number) ShiftInfixExpression.Operator.RIGHT_SHIFT_SIGNED.operate(-8, 1)).intValue());
    assertEquals(2147483644, ((Number) ShiftInfixExpression.Operator.RIGHT_SHIFT_UNSIGNED.operate(-8, 1)).intValue());
    assertEquals(4L, ((Number) ShiftInfixExpression.Operator.RIGHT_SHIFT_UNSIGNED.operate(8L, 1L)).longValue());
  }

  @Test
  public void constructorCarriesTypeAndInvalidOperandsThrow() {
    ShiftInfixExpression expression = new ShiftInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(8),
        ShiftInfixExpression.Operator.LEFT_SHIFT,
        new IntegerLiteral(1)
    );
    TrackingProcessor processor = new TrackingProcessor();

    expression.process(processor);

    assertSame(JavaType.getInstance(Integer.class), expression.getType());
    assertEquals(10, expression.getLevelOfPrecedence());
    assertSame(expression, processor.processed);

    try {
      ShiftInfixExpression.Operator.LEFT_SHIFT.operate("a", "b");
      fail("Expected shift operator to reject non-integral operands");
    } catch (RuntimeException expected) {
    }
  }

  private static final class TrackingProcessor implements AstProcessor {
    private InfixExpression<?> processed;

    @Override
    public CodeOrganizer getNewCodeOrganizerForTypeName(String typeName) {
      return null;
    }

    @Override
    public void processInfixExpression(InfixExpression infixExpression) {
      this.processed = infixExpression;
    }
  }
}
