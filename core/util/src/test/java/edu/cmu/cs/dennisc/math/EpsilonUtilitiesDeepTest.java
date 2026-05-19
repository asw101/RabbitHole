package edu.cmu.cs.dennisc.math;

import org.junit.Test;

import static org.junit.Assert.*;

public class EpsilonUtilitiesDeepTest {

  @Test
  public void reasonableEpsilon_isCorrectValue() {
    assertEquals(0.001, EpsilonUtilities.REASONABLE_EPSILON, 1e-10);
  }

  @Test
  public void isWithinEpsilon_doubleOverload_close() {
    assertTrue(EpsilonUtilities.isWithinEpsilon(5.0, 5.0005, 0.001));
  }

  @Test
  public void isWithinEpsilon_doubleOverload_far() {
    assertFalse(EpsilonUtilities.isWithinEpsilon(5.0, 5.002, 0.001));
  }

  @Test
  public void isWithinEpsilon_doubleOverload_equal() {
    assertTrue(EpsilonUtilities.isWithinEpsilon(1.0, 1.0, 0.001));
  }

  @Test
  public void isWithinEpsilon_doubleBoundaryIsExclusive() {
    // At exactly epsilon distance, abs(1.0 - 1.001) == 0.001 which is NOT < 0.001
    assertFalse(EpsilonUtilities.isWithinEpsilon(1.0, 1.002, 0.001));
  }

  @Test
  public void isWithinEpsilon_floatOverload_close() {
    assertTrue(EpsilonUtilities.isWithinEpsilon(5.0f, 5.0005f, 0.001f));
  }

  @Test
  public void isWithinEpsilon_floatOverload_far() {
    assertFalse(EpsilonUtilities.isWithinEpsilon(5.0f, 5.002f, 0.001f));
  }

  @Test
  public void isWithinEpsilon_floatBoundaryIsExclusive() {
    assertFalse(EpsilonUtilities.isWithinEpsilon(1.0f, 1.001f, 0.001f));
  }

  @Test
  public void isWithinReasonableEpsilon_double_close() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilon(1.0, 1.0005));
  }

  @Test
  public void isWithinReasonableEpsilon_double_far() {
    assertFalse(EpsilonUtilities.isWithinReasonableEpsilon(1.0, 1.01));
  }

  @Test
  public void isWithinReasonableEpsilon_double_negative() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilon(-1.0, -1.0005));
  }

  @Test
  public void isWithinReasonableEpsilon_double_negativeFar() {
    assertFalse(EpsilonUtilities.isWithinReasonableEpsilon(-1.0, -1.01));
  }

  @Test
  public void isWithinReasonableEpsilon_float_close() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilon(1.0f, 1.0005f));
  }

  @Test
  public void isWithinReasonableEpsilon_float_far() {
    assertFalse(EpsilonUtilities.isWithinReasonableEpsilon(1.0f, 1.01f));
  }

  @Test
  public void isWithinEpsilonOf1InSquaredSpace_double_exactly1() {
    assertTrue(EpsilonUtilities.isWithinEpsilonOf1InSquaredSpace(1.0, 0.001));
  }

  @Test
  public void isWithinEpsilonOf1InSquaredSpace_double_far() {
    assertFalse(EpsilonUtilities.isWithinEpsilonOf1InSquaredSpace(0.5, 0.001));
  }

  @Test
  public void isWithinEpsilonOf1InSquaredSpace_double_large() {
    assertFalse(EpsilonUtilities.isWithinEpsilonOf1InSquaredSpace(4.0, 0.001));
  }

  @Test
  public void isWithinEpsilonOf0InSquaredSpace_double_zero() {
    assertTrue(EpsilonUtilities.isWithinEpsilonOf0InSquaredSpace(0.0, 0.001));
  }

  @Test
  public void isWithinEpsilonOf0InSquaredSpace_double_verySmall() {
    assertTrue(EpsilonUtilities.isWithinEpsilonOf0InSquaredSpace(0.0000001, 0.001));
  }

  @Test
  public void isWithinEpsilonOf0InSquaredSpace_double_large() {
    assertFalse(EpsilonUtilities.isWithinEpsilonOf0InSquaredSpace(1.0, 0.001));
  }

  @Test
  public void isWithinEpsilonOf0InSquaredSpace_doubleBoundaryIsExclusive() {
    assertFalse(EpsilonUtilities.isWithinEpsilonOf0InSquaredSpace(0.001 * 0.001, 0.001));
  }

  @Test
  public void isWithinEpsilonOf1InSquaredSpace_float_exactly1() {
    assertTrue(EpsilonUtilities.isWithinEpsilonOf1InSquaredSpace(1.0f, 0.001f));
  }

  @Test
  public void isWithinEpsilonOf1InSquaredSpace_float_far() {
    assertFalse(EpsilonUtilities.isWithinEpsilonOf1InSquaredSpace(0.5f, 0.001f));
  }

  @Test
  public void isWithinEpsilonOf0InSquaredSpace_float_zero() {
    assertTrue(EpsilonUtilities.isWithinEpsilonOf0InSquaredSpace(0.0f, 0.001f));
  }

  @Test
  public void isWithinEpsilonOf0InSquaredSpace_float_large() {
    assertFalse(EpsilonUtilities.isWithinEpsilonOf0InSquaredSpace(1.0f, 0.001f));
  }

  @Test
  public void isWithinEpsilonOf0InSquaredSpace_floatBoundaryIsExclusive() {
    assertFalse(EpsilonUtilities.isWithinEpsilonOf0InSquaredSpace(0.001f * 0.001f, 0.001f));
  }

  @Test
  public void isWithinReasonableEpsilonOf0InSquaredSpace_double_zero() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilonOf0InSquaredSpace(0.0));
  }

  @Test
  public void isWithinReasonableEpsilonOf0InSquaredSpace_double_large() {
    assertFalse(EpsilonUtilities.isWithinReasonableEpsilonOf0InSquaredSpace(1.0));
  }

  @Test
  public void isWithinReasonableEpsilonOf0InSquaredSpace_float_zero() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilonOf0InSquaredSpace(0.0f));
  }

  @Test
  public void isWithinReasonableEpsilonOf0InSquaredSpace_float_large() {
    assertFalse(EpsilonUtilities.isWithinReasonableEpsilonOf0InSquaredSpace(1.0f));
  }

  @Test
  public void constants_derivedCorrectly() {
    assertEquals((1 - 0.001) * (1 - 0.001), EpsilonUtilities.MINIMUM_FOR_WITHIN_REASONABLE_EPSILON_OF_1_IN_SQUARED_SPACE, 1e-10);
    assertEquals((1 + 0.001) * (1 + 0.001), EpsilonUtilities.MAXIMUM_FOR_WITHIN_REASONABLE_EPSILON_OF_1_IN_SQUARED_SPACE, 1e-10);
    assertEquals(0.001 * 0.001, EpsilonUtilities.MAXIMUM_FOR_WITHIN_REASONABLE_EPSILON_OF_0_IN_SQUARED_SPACE, 1e-10);
  }

  @Test
  public void floatConstants_derivedCorrectly() {
    assertEquals((float) EpsilonUtilities.REASONABLE_EPSILON, EpsilonUtilities.REASONABLE_EPSILON_FLOAT, 1e-6f);
  }

  @Test
  public void isWithinEpsilon_zero_zero() {
    assertTrue(EpsilonUtilities.isWithinEpsilon(0.0, 0.0, 0.001));
  }

  @Test
  public void isWithinEpsilon_largeNumbers() {
    assertTrue(EpsilonUtilities.isWithinEpsilon(1000000.0, 1000000.0005, 0.001));
  }
}
