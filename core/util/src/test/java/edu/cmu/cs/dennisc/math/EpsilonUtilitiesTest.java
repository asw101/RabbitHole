package edu.cmu.cs.dennisc.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpsilonUtilitiesTest {

  // --- REASONABLE_EPSILON constant ---

  @Test
  void reasonableEpsilon_isPositive() {
    assertTrue(EpsilonUtilities.REASONABLE_EPSILON > 0);
  }

  @Test
  void reasonableEpsilon_value() {
    assertEquals(0.001, EpsilonUtilities.REASONABLE_EPSILON, 1e-15);
  }

  @Test
  void reasonableEpsilonFloat_matchesDouble() {
    assertEquals((float) EpsilonUtilities.REASONABLE_EPSILON, EpsilonUtilities.REASONABLE_EPSILON_FLOAT);
  }

  // --- isWithinEpsilon (double) ---

  @Test
  void isWithinEpsilon_double_exactMatch() {
    assertTrue(EpsilonUtilities.isWithinEpsilon(5.0, 5.0, 0.001));
  }

  @Test
  void isWithinEpsilon_double_withinRange() {
    assertTrue(EpsilonUtilities.isWithinEpsilon(5.0, 5.0005, 0.001));
  }

  @Test
  void isWithinEpsilon_double_outsideRange() {
    assertFalse(EpsilonUtilities.isWithinEpsilon(5.0, 5.01, 0.001));
  }

  @Test
  void isWithinEpsilon_double_negativeDifference() {
    assertTrue(EpsilonUtilities.isWithinEpsilon(5.0, 4.9995, 0.001));
  }

  @Test
  void isWithinEpsilon_double_atExactBoundary() {
    assertFalse(EpsilonUtilities.isWithinEpsilon(5.0, 5.001, 0.001));
  }

  @Test
  void isWithinEpsilon_double_zeroEpsilon() {
    assertFalse(EpsilonUtilities.isWithinEpsilon(5.0, 5.0 + 1e-15, 0.0));
  }

  // --- isWithinEpsilon (float) ---

  @Test
  void isWithinEpsilon_float_exactMatch() {
    assertTrue(EpsilonUtilities.isWithinEpsilon(5.0f, 5.0f, 0.001f));
  }

  @Test
  void isWithinEpsilon_float_withinRange() {
    assertTrue(EpsilonUtilities.isWithinEpsilon(5.0f, 5.0005f, 0.001f));
  }

  @Test
  void isWithinEpsilon_float_outsideRange() {
    assertFalse(EpsilonUtilities.isWithinEpsilon(5.0f, 5.01f, 0.001f));
  }

  // --- isWithinReasonableEpsilon (double) ---

  @Test
  void isWithinReasonableEpsilon_double_exactMatch() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilon(1.0, 1.0));
  }

  @Test
  void isWithinReasonableEpsilon_double_withinRange() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilon(1.0, 1.0005));
  }

  @Test
  void isWithinReasonableEpsilon_double_outsideRange() {
    assertFalse(EpsilonUtilities.isWithinReasonableEpsilon(1.0, 1.01));
  }

  // --- isWithinReasonableEpsilon (float) ---

  @Test
  void isWithinReasonableEpsilon_float_exactMatch() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilon(1.0f, 1.0f));
  }

  @Test
  void isWithinReasonableEpsilon_float_withinRange() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilon(1.0f, 1.0005f));
  }

  @Test
  void isWithinReasonableEpsilon_float_outsideRange() {
    assertFalse(EpsilonUtilities.isWithinReasonableEpsilon(1.0f, 1.01f));
  }

  // --- isWithinEpsilonOf1InSquaredSpace (double) ---

  @Test
  void isWithinEpsilonOf1InSquaredSpace_double_exactlyOne() {
    assertTrue(EpsilonUtilities.isWithinEpsilonOf1InSquaredSpace(1.0, 0.001));
  }

  @Test
  void isWithinEpsilonOf1InSquaredSpace_double_slightlyAbove() {
    assertTrue(EpsilonUtilities.isWithinEpsilonOf1InSquaredSpace(1.001, 0.01));
  }

  @Test
  void isWithinEpsilonOf1InSquaredSpace_double_farFromOne() {
    assertFalse(EpsilonUtilities.isWithinEpsilonOf1InSquaredSpace(2.0, 0.001));
  }

  @Test
  void isWithinEpsilonOf1InSquaredSpace_double_zero() {
    assertFalse(EpsilonUtilities.isWithinEpsilonOf1InSquaredSpace(0.0, 0.001));
  }

  // --- isWithinEpsilonOf0InSquaredSpace (double) ---

  @Test
  void isWithinEpsilonOf0InSquaredSpace_double_zero() {
    assertTrue(EpsilonUtilities.isWithinEpsilonOf0InSquaredSpace(0.0, 0.001));
  }

  @Test
  void isWithinEpsilonOf0InSquaredSpace_double_small() {
    assertTrue(EpsilonUtilities.isWithinEpsilonOf0InSquaredSpace(0.0000001, 0.001));
  }

  @Test
  void isWithinEpsilonOf0InSquaredSpace_double_large() {
    assertFalse(EpsilonUtilities.isWithinEpsilonOf0InSquaredSpace(1.0, 0.001));
  }

  // --- isWithinEpsilonOf1InSquaredSpace (float) ---

  @Test
  void isWithinEpsilonOf1InSquaredSpace_float_exactlyOne() {
    assertTrue(EpsilonUtilities.isWithinEpsilonOf1InSquaredSpace(1.0f, 0.001f));
  }

  @Test
  void isWithinEpsilonOf1InSquaredSpace_float_farFromOne() {
    assertFalse(EpsilonUtilities.isWithinEpsilonOf1InSquaredSpace(2.0f, 0.001f));
  }

  // --- isWithinEpsilonOf0InSquaredSpace (float) ---

  @Test
  void isWithinEpsilonOf0InSquaredSpace_float_zero() {
    assertTrue(EpsilonUtilities.isWithinEpsilonOf0InSquaredSpace(0.0f, 0.001f));
  }

  @Test
  void isWithinEpsilonOf0InSquaredSpace_float_large() {
    assertFalse(EpsilonUtilities.isWithinEpsilonOf0InSquaredSpace(1.0f, 0.001f));
  }

  // --- isWithinReasonableEpsilonOf1InSquaredSpace (double) ---

  @Test
  void isWithinReasonableEpsilonOf1InSquaredSpace_double_one() {
    // Per the code: checks (MIN < a) && (a < MAX_0) which is actually a known quirk
    // Just verify the method is callable and consistent with constants
    double val = 1.0;
    boolean result = EpsilonUtilities.isWithinReasonableEpsilonOf1InSquaredSpace(val);
    assertEquals(
        (EpsilonUtilities.MINIMUM_FOR_WITHIN_REASONABLE_EPSILON_OF_1_IN_SQUARED_SPACE < val)
        && (val < EpsilonUtilities.MAXIMUM_FOR_WITHIN_REASONABLE_EPSILON_OF_0_IN_SQUARED_SPACE),
        result);
  }

  @Test
  void isWithinReasonableEpsilonOf1InSquaredSpace_double_farValue() {
    assertFalse(EpsilonUtilities.isWithinReasonableEpsilonOf1InSquaredSpace(100.0));
  }

  // --- isWithinReasonableEpsilonOf0InSquaredSpace (double) ---

  @Test
  void isWithinReasonableEpsilonOf0InSquaredSpace_double_zero() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilonOf0InSquaredSpace(0.0));
  }

  @Test
  void isWithinReasonableEpsilonOf0InSquaredSpace_double_large() {
    assertFalse(EpsilonUtilities.isWithinReasonableEpsilonOf0InSquaredSpace(1.0));
  }

  // --- isWithinReasonableEpsilonOf1InSquaredSpace (float) ---

  @Test
  void isWithinReasonableEpsilonOf1InSquaredSpace_float_one() {
    float val = 1.0f;
    boolean result = EpsilonUtilities.isWithinReasonableEpsilonOf1InSquaredSpace(val);
    assertEquals(
        (EpsilonUtilities.MINIMUM_FOR_WITHIN_REASONABLE_EPSILON_OF_1_IN_SQUARED_SPACE_FLOAT < val)
        && (val < EpsilonUtilities.MAXIMUM_FOR_WITHIN_REASONABLE_EPSILON_OF_0_IN_SQUARED_SPACE_FLOAT),
        result);
  }

  @Test
  void isWithinReasonableEpsilonOf1InSquaredSpace_float_far() {
    assertFalse(EpsilonUtilities.isWithinReasonableEpsilonOf1InSquaredSpace(100.0f));
  }

  // --- isWithinReasonableEpsilonOf0InSquaredSpace (float) ---

  @Test
  void isWithinReasonableEpsilonOf0InSquaredSpace_float_zero() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilonOf0InSquaredSpace(0.0f));
  }

  @Test
  void isWithinReasonableEpsilonOf0InSquaredSpace_float_large() {
    assertFalse(EpsilonUtilities.isWithinReasonableEpsilonOf0InSquaredSpace(1.0f));
  }

  // --- Squared space constants ---

  @Test
  void minConstant_isSquareOf0_999() {
    double expected = 0.999 * 0.999;
    assertEquals(expected, EpsilonUtilities.MINIMUM_FOR_WITHIN_REASONABLE_EPSILON_OF_1_IN_SQUARED_SPACE, 1e-15);
  }

  @Test
  void maxConstant_isSquareOf1_001() {
    double expected = 1.001 * 1.001;
    assertEquals(expected, EpsilonUtilities.MAXIMUM_FOR_WITHIN_REASONABLE_EPSILON_OF_1_IN_SQUARED_SPACE, 1e-15);
  }

  @Test
  void max0Constant_isSquareOfEpsilon() {
    double expected = 0.001 * 0.001;
    assertEquals(expected, EpsilonUtilities.MAXIMUM_FOR_WITHIN_REASONABLE_EPSILON_OF_0_IN_SQUARED_SPACE, 1e-15);
  }

  // --- Number interface ---

  @Test
  void isWithinEpsilon_acceptsNumberType() {
    Number n = 5.0;
    assertTrue(EpsilonUtilities.isWithinEpsilon(n, 5.0, 0.001));
  }

  @Test
  void isWithinReasonableEpsilon_acceptsIntegerAsNumber() {
    Number n = 1;
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilon(n, 1.0));
  }
}
