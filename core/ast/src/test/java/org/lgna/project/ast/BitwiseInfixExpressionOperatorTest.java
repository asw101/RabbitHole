package org.lgna.project.ast;

import org.junit.Test;
import org.lgna.project.code.CodeOrganizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BitwiseInfixExpressionOperatorTest {
  @Test
  public void operatorsPreserveCurrentBitwisePromotionBehavior() {
    assertEquals(2, ((Number) BitwiseInfixExpression.Operator.AND.operate(6, 3)).intValue());
    assertEquals(5L, ((Number) BitwiseInfixExpression.Operator.OR.operate(4L, 1L)).longValue());

    Object xor = BitwiseInfixExpression.Operator.XOR.operate((short) 7, (short) 3);
    assertTrue(xor instanceof Integer);
    assertEquals(4, ((Number) xor).intValue());
  }

  @Test
  public void constructorCarriesTypeAndInvalidOperandsThrow() {
    BitwiseInfixExpression expression = new BitwiseInfixExpression(
        JavaType.getInstance(Integer.class),
        new IntegerLiteral(6),
        BitwiseInfixExpression.Operator.AND,
        new IntegerLiteral(3)
    );
    TrackingProcessor processor = new TrackingProcessor();

    expression.process(processor);

    assertSame(JavaType.getInstance(Integer.class), expression.getType());
    assertEquals(7, expression.getLevelOfPrecedence());
    assertSame(expression, processor.processed);

    try {
      BitwiseInfixExpression.Operator.AND.operate("a", "b");
      fail("Expected bitwise AND to reject non-integral operands");
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
