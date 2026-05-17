package org.alice.ide.croquet.models.cascade.arithmetic;

import org.junit.Test;
import org.lgna.project.ast.ArithmeticInfixExpression;

import static org.junit.Assert.*;

/**
 * Tests for {@link ArithmeticUtilities} — operator array constants.
 */
public class ArithmeticUtilitiesTest {

  // ---- PRIME_TIME_DOUBLE ----

  @Test
  public void primeTimeDoubleOperators_hasFourElements() {
    assertEquals(4, ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS.length);
  }

  @Test
  public void primeTimeDoubleOperators_containsPlus() {
    assertEquals(ArithmeticInfixExpression.Operator.PLUS,
        ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS[0]);
  }

  @Test
  public void primeTimeDoubleOperators_containsMinus() {
    assertEquals(ArithmeticInfixExpression.Operator.MINUS,
        ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS[1]);
  }

  @Test
  public void primeTimeDoubleOperators_containsTimes() {
    assertEquals(ArithmeticInfixExpression.Operator.TIMES,
        ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS[2]);
  }

  @Test
  public void primeTimeDoubleOperators_containsRealDivide() {
    assertEquals(ArithmeticInfixExpression.Operator.REAL_DIVIDE,
        ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS[3]);
  }

  // ---- TUCKED_AWAY_DOUBLE ----

  @Test
  public void tuckedAwayDoubleOperators_hasThreeElements() {
    assertEquals(3, ArithmeticUtilities.TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS.length);
  }

  @Test
  public void tuckedAwayDoubleOperators_containsRealRemainder() {
    assertEquals(ArithmeticInfixExpression.Operator.REAL_REMAINDER,
        ArithmeticUtilities.TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS[0]);
  }

  @Test
  public void tuckedAwayDoubleOperators_containsIntegerDivide() {
    assertEquals(ArithmeticInfixExpression.Operator.INTEGER_DIVIDE,
        ArithmeticUtilities.TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS[1]);
  }

  @Test
  public void tuckedAwayDoubleOperators_containsIntegerRemainder() {
    assertEquals(ArithmeticInfixExpression.Operator.INTEGER_REMAINDER,
        ArithmeticUtilities.TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS[2]);
  }

  // ---- PRIME_TIME_INTEGER ----

  @Test
  public void primeTimeIntegerOperators_hasThreeElements() {
    assertEquals(3, ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS.length);
  }

  @Test
  public void primeTimeIntegerOperators_containsExpectedOperators() {
    assertEquals(ArithmeticInfixExpression.Operator.PLUS,
        ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS[0]);
    assertEquals(ArithmeticInfixExpression.Operator.MINUS,
        ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS[1]);
    assertEquals(ArithmeticInfixExpression.Operator.TIMES,
        ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS[2]);
  }

  // ---- TUCKED_AWAY_INTEGER ----

  @Test
  public void tuckedAwayIntegerOperators_hasTwoElements() {
    assertEquals(2, ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS.length);
  }

  @Test
  public void tuckedAwayIntegerOperators_containsExpectedOperators() {
    assertEquals(ArithmeticInfixExpression.Operator.INTEGER_DIVIDE,
        ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS[0]);
    assertEquals(ArithmeticInfixExpression.Operator.INTEGER_REMAINDER,
        ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS[1]);
  }

  // ---- no double/integer overlap in PRIME_TIME ----

  @Test
  public void primeTimeDouble_doesNotContainIntegerDivide() {
    for (ArithmeticInfixExpression.Operator op : ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS) {
      assertNotEquals(ArithmeticInfixExpression.Operator.INTEGER_DIVIDE, op);
    }
  }

  @Test
  public void primeTimeInteger_doesNotContainRealDivide() {
    for (ArithmeticInfixExpression.Operator op : ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS) {
      assertNotEquals(ArithmeticInfixExpression.Operator.REAL_DIVIDE, op);
    }
  }
}
