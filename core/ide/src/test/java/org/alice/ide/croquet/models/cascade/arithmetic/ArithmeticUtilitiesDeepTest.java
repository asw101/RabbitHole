package org.alice.ide.croquet.models.cascade.arithmetic;

import org.lgna.project.ast.ArithmeticInfixExpression;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link ArithmeticUtilities} — operator arrays,
 * completeness, and no-overlap.
 */
public class ArithmeticUtilitiesDeepTest {

  @Test
  public void primeTimeDoubleOperators_containsExpectedOperators() {
    ArithmeticInfixExpression.Operator[] ops = ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS;
    assertEquals(4, ops.length);
    List<ArithmeticInfixExpression.Operator> list = Arrays.asList(ops);
    assertTrue(list.contains(ArithmeticInfixExpression.Operator.PLUS));
    assertTrue(list.contains(ArithmeticInfixExpression.Operator.MINUS));
    assertTrue(list.contains(ArithmeticInfixExpression.Operator.TIMES));
    assertTrue(list.contains(ArithmeticInfixExpression.Operator.REAL_DIVIDE));
  }

  @Test
  public void tuckedAwayDoubleOperators_containsExpectedOperators() {
    ArithmeticInfixExpression.Operator[] ops = ArithmeticUtilities.TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS;
    assertEquals(3, ops.length);
    List<ArithmeticInfixExpression.Operator> list = Arrays.asList(ops);
    assertTrue(list.contains(ArithmeticInfixExpression.Operator.REAL_REMAINDER));
    assertTrue(list.contains(ArithmeticInfixExpression.Operator.INTEGER_DIVIDE));
    assertTrue(list.contains(ArithmeticInfixExpression.Operator.INTEGER_REMAINDER));
  }

  @Test
  public void primeTimeIntegerOperators_containsExpectedOperators() {
    ArithmeticInfixExpression.Operator[] ops = ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS;
    assertEquals(3, ops.length);
    List<ArithmeticInfixExpression.Operator> list = Arrays.asList(ops);
    assertTrue(list.contains(ArithmeticInfixExpression.Operator.PLUS));
    assertTrue(list.contains(ArithmeticInfixExpression.Operator.MINUS));
    assertTrue(list.contains(ArithmeticInfixExpression.Operator.TIMES));
  }

  @Test
  public void tuckedAwayIntegerOperators_containsExpectedOperators() {
    ArithmeticInfixExpression.Operator[] ops = ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS;
    assertEquals(2, ops.length);
    List<ArithmeticInfixExpression.Operator> list = Arrays.asList(ops);
    assertTrue(list.contains(ArithmeticInfixExpression.Operator.INTEGER_DIVIDE));
    assertTrue(list.contains(ArithmeticInfixExpression.Operator.INTEGER_REMAINDER));
  }

  @Test
  public void primeTimeAndTuckedAwayDouble_noOverlap() {
    List<ArithmeticInfixExpression.Operator> prime = Arrays.asList(ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS);
    List<ArithmeticInfixExpression.Operator> tucked = Arrays.asList(ArithmeticUtilities.TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS);
    for (ArithmeticInfixExpression.Operator op : prime) {
      assertFalse("Overlap in double operators: " + op, tucked.contains(op));
    }
  }

  @Test
  public void primeTimeAndTuckedAwayInteger_noOverlap() {
    List<ArithmeticInfixExpression.Operator> prime = Arrays.asList(ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS);
    List<ArithmeticInfixExpression.Operator> tucked = Arrays.asList(ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS);
    for (ArithmeticInfixExpression.Operator op : prime) {
      assertFalse("Overlap in integer operators: " + op, tucked.contains(op));
    }
  }

  @Test
  public void primeTimeDoubleOperators_orderIsCorrect() {
    ArithmeticInfixExpression.Operator[] ops = ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS;
    assertSame(ArithmeticInfixExpression.Operator.PLUS, ops[0]);
    assertSame(ArithmeticInfixExpression.Operator.MINUS, ops[1]);
    assertSame(ArithmeticInfixExpression.Operator.TIMES, ops[2]);
    assertSame(ArithmeticInfixExpression.Operator.REAL_DIVIDE, ops[3]);
  }

  @Test
  public void primeTimeIntegerOperators_doesNotContainDivide() {
    List<ArithmeticInfixExpression.Operator> list = Arrays.asList(ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS);
    assertFalse("Integer prime time should not contain REAL_DIVIDE",
        list.contains(ArithmeticInfixExpression.Operator.REAL_DIVIDE));
    assertFalse("Integer prime time should not contain INTEGER_DIVIDE",
        list.contains(ArithmeticInfixExpression.Operator.INTEGER_DIVIDE));
  }

  @Test
  public void allOperatorArrays_haveNoNulls() {
    for (ArithmeticInfixExpression.Operator op : ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS) {
      assertNotNull(op);
    }
    for (ArithmeticInfixExpression.Operator op : ArithmeticUtilities.TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS) {
      assertNotNull(op);
    }
    for (ArithmeticInfixExpression.Operator op : ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS) {
      assertNotNull(op);
    }
    for (ArithmeticInfixExpression.Operator op : ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS) {
      assertNotNull(op);
    }
  }

  @Test
  public void primeTimeDoubleOperators_noDuplicates() {
    ArithmeticInfixExpression.Operator[] ops = ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS;
    long distinct = Arrays.stream(ops).distinct().count();
    assertEquals(ops.length, distinct);
  }

  @Test
  public void primeTimeIntegerOperators_noDuplicates() {
    ArithmeticInfixExpression.Operator[] ops = ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS;
    long distinct = Arrays.stream(ops).distinct().count();
    assertEquals(ops.length, distinct);
  }
}
