package org.lgna.project.ast;

import org.junit.Test;
import org.lgna.project.code.CodeOrganizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ArithmeticInfixExpressionOperatorTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void operatorsApplyCurrentNumericPromotionRules() {
    assertEquals(5.5, ArithmeticInfixExpression.Operator.PLUS.operate(2, 3.5).doubleValue(), EPSILON);
    assertEquals(6L, ArithmeticInfixExpression.Operator.TIMES.operate(2L, 3).longValue());
    assertEquals(3.5, ArithmeticInfixExpression.Operator.REAL_DIVIDE.operate(7, 2).doubleValue(), EPSILON);
    assertEquals(3, ArithmeticInfixExpression.Operator.INTEGER_DIVIDE.operate(7.9, 2.1).intValue());
    assertEquals(3.0, ArithmeticInfixExpression.Operator.REAL_REMAINDER.operate((short) 7, (short) 4).doubleValue(), EPSILON);
    assertEquals(3L, ArithmeticInfixExpression.Operator.INTEGER_REMAINDER.operate(7L, 4).longValue());
  }

  @Test
  public void constructorCarriesTypePrecedenceAndProcessorDispatch() {
    ArithmeticInfixExpression expression = new ArithmeticInfixExpression(
        new IntegerLiteral(4),
        ArithmeticInfixExpression.Operator.TIMES,
        new IntegerLiteral(5),
        Integer.TYPE
    );
    TrackingProcessor processor = new TrackingProcessor();

    expression.process(processor);

    assertSame(JavaType.getInstance(Integer.TYPE), expression.getType());
    assertEquals(12, expression.getLevelOfPrecedence());
    assertSame(expression, processor.processed);
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
