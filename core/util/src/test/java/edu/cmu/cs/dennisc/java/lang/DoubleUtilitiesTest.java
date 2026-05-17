package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import static org.junit.Assert.*;

public class DoubleUtilitiesTest {

  @Test
  public void toDouble_fromInteger() {
    assertEquals(42.0, DoubleUtilities.toDouble(42), 0.0);
  }

  @Test
  public void toDouble_fromFloat() {
    assertEquals(3.14, DoubleUtilities.toDouble(3.14f), 0.01);
  }

  @Test
  public void toDouble_fromLong() {
    assertEquals(100000.0, DoubleUtilities.toDouble(100000L), 0.0);
  }

  @Test
  public void divide_normalDivision() {
    assertEquals(2.5, DoubleUtilities.divide(5, 2), 0.0);
  }

  @Test
  public void divide_byZero_returnsInfinity() {
    double result = DoubleUtilities.divide(1, 0);
    assertTrue(Double.isInfinite(result));
  }

  @Test
  public void divide_zeroByZero_returnsNaN() {
    double result = DoubleUtilities.divide(0, 0);
    assertTrue(Double.isNaN(result));
  }

  @Test
  public void round_toTwoDecimalPlaces() {
    assertEquals(3.14, DoubleUtilities.round(3.14159, 2), 0.0);
  }

  @Test
  public void round_toZeroDecimalPlaces() {
    assertEquals(3.0, DoubleUtilities.round(3.14, 0), 0.0);
  }

  @Test
  public void round_NaN_returnsNaN() {
    assertTrue(Double.isNaN(DoubleUtilities.round(Double.NaN, 2)));
  }

  @Test
  public void round_infinity_returnsInfinity() {
    assertTrue(Double.isInfinite(DoubleUtilities.round(Double.POSITIVE_INFINITY, 2)));
  }

  @Test
  public void parseDoubleInCurrentDefaultLocale_validNumber() {
    double result = DoubleUtilities.parseDoubleInCurrentDefaultLocale("123");
    assertFalse(Double.isNaN(result));
  }

  @Test
  public void parseDoubleInCurrentDefaultLocale_invalidString_returnsNaN() {
    double result = DoubleUtilities.parseDoubleInCurrentDefaultLocale("abc");
    assertTrue(Double.isNaN(result));
  }

  @Test
  public void formatInCurrentDefaultLocale_formatsNumber() {
    String result = DoubleUtilities.formatInCurrentDefaultLocale(1234.5);
    assertNotNull(result);
    assertFalse(result.isEmpty());
  }
}
