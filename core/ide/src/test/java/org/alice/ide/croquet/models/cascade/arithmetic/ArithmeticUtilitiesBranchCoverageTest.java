package org.alice.ide.croquet.models.cascade.arithmetic;

import org.junit.Test;
import org.lgna.project.ast.ArithmeticInfixExpression;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static org.junit.Assert.*;

public class ArithmeticUtilitiesBranchCoverageTest {

  @Test
  public void arithmeticUtilities_isPublicUtilityClass() {
    assertTrue(Modifier.isPublic(ArithmeticUtilities.class.getModifiers()));
    assertFalse(Modifier.isAbstract(ArithmeticUtilities.class.getModifiers()));
  }

  @Test
  public void arithmeticUtilities_hasSinglePrivateConstructor() {
    assertEquals(1, ArithmeticUtilities.class.getDeclaredConstructors().length);
    assertTrue(Modifier.isPrivate(ArithmeticUtilities.class.getDeclaredConstructors()[0].getModifiers()));
  }

  @Test
  public void arithmeticUtilities_privateConstructorThrowsAssertionError() throws Exception {
    Constructor<ArithmeticUtilities> constructor = ArithmeticUtilities.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      fail("Expected AssertionError");
    } catch (InvocationTargetException e) {
      assertTrue(e.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void primeTimeDoubleOperators_matchExpectedOrder() {
    assertArrayEquals(new ArithmeticInfixExpression.Operator[] {
            ArithmeticInfixExpression.Operator.PLUS,
            ArithmeticInfixExpression.Operator.MINUS,
            ArithmeticInfixExpression.Operator.TIMES,
            ArithmeticInfixExpression.Operator.REAL_DIVIDE
        },
        ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS);
  }

  @Test
  public void tuckedAwayDoubleOperators_matchExpectedOrder() {
    assertArrayEquals(new ArithmeticInfixExpression.Operator[] {
            ArithmeticInfixExpression.Operator.REAL_REMAINDER,
            ArithmeticInfixExpression.Operator.INTEGER_DIVIDE,
            ArithmeticInfixExpression.Operator.INTEGER_REMAINDER
        },
        ArithmeticUtilities.TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS);
  }

  @Test
  public void primeTimeIntegerOperators_matchExpectedOrder() {
    assertArrayEquals(new ArithmeticInfixExpression.Operator[] {
            ArithmeticInfixExpression.Operator.PLUS,
            ArithmeticInfixExpression.Operator.MINUS,
            ArithmeticInfixExpression.Operator.TIMES
        },
        ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS);
  }

  @Test
  public void tuckedAwayIntegerOperators_matchExpectedOrder() {
    assertArrayEquals(new ArithmeticInfixExpression.Operator[] {
            ArithmeticInfixExpression.Operator.INTEGER_DIVIDE,
            ArithmeticInfixExpression.Operator.INTEGER_REMAINDER
        },
        ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS);
  }

  @Test
  public void allOperatorArraysHaveExpectedLengths() {
    assertEquals(4, ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS.length);
    assertEquals(3, ArithmeticUtilities.TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS.length);
    assertEquals(3, ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS.length);
    assertEquals(2, ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS.length);
  }

  @Test
  public void allOperatorArraysContainNoNulls() {
    assertNoNulls(ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS);
    assertNoNulls(ArithmeticUtilities.TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS);
    assertNoNulls(ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS);
    assertNoNulls(ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS);
  }

  @Test
  public void primeTimeDoubleOperatorsContainNoDuplicates() {
    assertEquals(4, Arrays.stream(ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS).distinct().count());
  }

  @Test
  public void tuckedAwayDoubleOperatorsContainNoDuplicates() {
    assertEquals(3, Arrays.stream(ArithmeticUtilities.TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS).distinct().count());
  }

  @Test
  public void primeTimeIntegerOperatorsContainNoDuplicates() {
    assertEquals(3, Arrays.stream(ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS).distinct().count());
  }

  @Test
  public void tuckedAwayIntegerOperatorsContainNoDuplicates() {
    assertEquals(2, Arrays.stream(ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS).distinct().count());
  }

  @Test
  public void primeAndTuckedDoubleSetsDoNotOverlap() {
    assertDisjoint(
        ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS,
        ArithmeticUtilities.TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS);
  }

  @Test
  public void primeAndTuckedIntegerSetsDoNotOverlap() {
    assertDisjoint(
        ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS,
        ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS);
  }

  @Test
  public void combinedDoubleSetMatchesExpectedBranchCoverageSet() {
    EnumSet<ArithmeticInfixExpression.Operator> actual = EnumSet.noneOf(ArithmeticInfixExpression.Operator.class);
    actual.addAll(Arrays.asList(ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS));
    actual.addAll(Arrays.asList(ArithmeticUtilities.TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS));

    assertEquals(EnumSet.of(
            ArithmeticInfixExpression.Operator.PLUS,
            ArithmeticInfixExpression.Operator.MINUS,
            ArithmeticInfixExpression.Operator.TIMES,
            ArithmeticInfixExpression.Operator.REAL_DIVIDE,
            ArithmeticInfixExpression.Operator.REAL_REMAINDER,
            ArithmeticInfixExpression.Operator.INTEGER_DIVIDE,
            ArithmeticInfixExpression.Operator.INTEGER_REMAINDER),
        actual);
  }

  @Test
  public void combinedIntegerSetMatchesExpectedBranchCoverageSet() {
    EnumSet<ArithmeticInfixExpression.Operator> actual = EnumSet.noneOf(ArithmeticInfixExpression.Operator.class);
    actual.addAll(Arrays.asList(ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS));
    actual.addAll(Arrays.asList(ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS));

    assertEquals(EnumSet.of(
            ArithmeticInfixExpression.Operator.PLUS,
            ArithmeticInfixExpression.Operator.MINUS,
            ArithmeticInfixExpression.Operator.TIMES,
            ArithmeticInfixExpression.Operator.INTEGER_DIVIDE,
            ArithmeticInfixExpression.Operator.INTEGER_REMAINDER),
        actual);
  }

  @Test
  public void primeTimeDoubleOperatorsExcludeIntegerOnlyOperators() {
    List<ArithmeticInfixExpression.Operator> operators = Arrays.asList(ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS);
    assertFalse(operators.contains(ArithmeticInfixExpression.Operator.INTEGER_DIVIDE));
    assertFalse(operators.contains(ArithmeticInfixExpression.Operator.INTEGER_REMAINDER));
  }

  @Test
  public void primeTimeIntegerOperatorsExcludeRealOnlyOperators() {
    List<ArithmeticInfixExpression.Operator> operators = Arrays.asList(ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS);
    assertFalse(operators.contains(ArithmeticInfixExpression.Operator.REAL_DIVIDE));
    assertFalse(operators.contains(ArithmeticInfixExpression.Operator.REAL_REMAINDER));
  }

  @Test
  public void tuckedAwayGroupsExcludePrimeTimeBasics() {
    for (ArithmeticInfixExpression.Operator operator : ArithmeticUtilities.TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS) {
      assertNotEquals(ArithmeticInfixExpression.Operator.PLUS, operator);
      assertNotEquals(ArithmeticInfixExpression.Operator.MINUS, operator);
      assertNotEquals(ArithmeticInfixExpression.Operator.TIMES, operator);
    }
    for (ArithmeticInfixExpression.Operator operator : ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS) {
      assertNotEquals(ArithmeticInfixExpression.Operator.PLUS, operator);
      assertNotEquals(ArithmeticInfixExpression.Operator.MINUS, operator);
      assertNotEquals(ArithmeticInfixExpression.Operator.TIMES, operator);
    }
  }

  @Test
  public void operatorArraysAreDistinctFieldInstances() {
    assertNotSame(ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS,
        ArithmeticUtilities.TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS);
    assertNotSame(ArithmeticUtilities.PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS,
        ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS);
  }

  @Test
  public void operatorFieldsArePublicStaticFinal() throws Exception {
    assertPublicStaticFinalField("PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS");
    assertPublicStaticFinalField("TUCKED_AWAY_DOUBLE_ARITHMETIC_OPERATORS");
    assertPublicStaticFinalField("PRIME_TIME_INTEGER_ARITHMETIC_OPERATORS");
    assertPublicStaticFinalField("TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS");
  }

  @Test
  public void repeatedFieldReadsReturnSameArrayReference() {
    assertSame(ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS,
        ArithmeticUtilities.PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS);
    assertSame(ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS,
        ArithmeticUtilities.TUCKED_AWAY_INTEGER_ARITHMETIC_OPERATORS);
  }

  @Test
  public void operatorArraysUseOperatorEnumType() throws Exception {
    Field field = ArithmeticUtilities.class.getField("PRIME_TIME_DOUBLE_ARITHMETIC_OPERATORS");
    assertEquals(ArithmeticInfixExpression.Operator[].class, field.getType());
  }

  private static void assertNoNulls(ArithmeticInfixExpression.Operator[] operators) {
    for (ArithmeticInfixExpression.Operator operator : operators) {
      assertNotNull(operator);
    }
  }

  private static void assertDisjoint(ArithmeticInfixExpression.Operator[] left,
                                     ArithmeticInfixExpression.Operator[] right) {
    List<ArithmeticInfixExpression.Operator> rightList = Arrays.asList(right);
    for (ArithmeticInfixExpression.Operator operator : left) {
      assertFalse(rightList.contains(operator));
    }
  }

  private static void assertPublicStaticFinalField(String fieldName) throws Exception {
    Field field = ArithmeticUtilities.class.getField(fieldName);
    assertTrue(Modifier.isPublic(field.getModifiers()));
    assertTrue(Modifier.isStatic(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }
}
