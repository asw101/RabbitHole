package org.lgna.project.ast;

import org.junit.Test;
import org.lgna.project.code.CodeOrganizer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RelationalInfixExpressionOperatorTest {
  @Test
  public void operatorsCompareNumbersByValueAndObjectsByIdentity() {
    assertTrue(RelationalInfixExpression.Operator.LESS.operate(2, 2.5d));
    assertTrue(RelationalInfixExpression.Operator.GREATER_EQUALS.operate((byte) 4, (short) 4));
    assertTrue(RelationalInfixExpression.Operator.EQUALS.operate(3, 3L));

    Object shared = new Object();
    assertTrue(RelationalInfixExpression.Operator.EQUALS.operate(shared, shared));
    assertFalse(RelationalInfixExpression.Operator.EQUALS.operate(new String("x"), new String("x")));
    assertTrue(RelationalInfixExpression.Operator.NOT_EQUALS.operate(new String("x"), new String("x")));
  }

  @Test
  public void orderingNonNumbersThrowsRuntimeException() {
    try {
      RelationalInfixExpression.Operator.LESS.operate("alpha", "beta");
      fail("Expected less-than comparison to reject non-numbers");
    } catch (RuntimeException expected) {
    }
  }

  @Test
  public void constructorCarriesBooleanTypeAndProcessorDispatch() {
    RelationalInfixExpression expression = new RelationalInfixExpression(
        new IntegerLiteral(1),
        RelationalInfixExpression.Operator.LESS,
        new IntegerLiteral(2),
        Integer.class,
        Integer.class
    );
    TrackingProcessor processor = new TrackingProcessor();

    expression.process(processor);

    assertSame(JavaType.BOOLEAN_OBJECT_TYPE, expression.getType());
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
