package org.lgna.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class LgnaIllegalArgumentExceptionTest {

  @Test
  public void checkArgumentIsNotNull_validReturnsValue() {
    String result = LgnaIllegalArgumentException.checkArgumentIsNotNull("hello", 0);
    assertEquals("hello", result);
  }

  @Test(expected = LgnaIllegalArgumentException.class)
  public void checkArgumentIsNotNull_nullThrows() {
    LgnaIllegalArgumentException.checkArgumentIsNotNull(null, 0);
  }

  @Test
  public void checkArgumentIsNumber_validReturnsValue() {
    Number result = LgnaIllegalArgumentException.checkArgumentIsNumber(42.0, 0);
    assertEquals(42.0, result.doubleValue(), 1e-10);
  }

  @Test(expected = LgnaIllegalArgumentException.class)
  public void checkArgumentIsNumber_nanThrows() {
    LgnaIllegalArgumentException.checkArgumentIsNumber(Double.NaN, 0);
  }

  @Test(expected = LgnaIllegalArgumentException.class)
  public void checkArgumentIsNumber_nullThrows() {
    LgnaIllegalArgumentException.checkArgumentIsNumber(null, 0);
  }

  @Test
  public void checkArgumentIsPositive_validReturnsValue() {
    Number result = LgnaIllegalArgumentException.checkArgumentIsPositive(5.0, 0);
    assertEquals(5.0, result.doubleValue(), 1e-10);
  }

  @Test(expected = LgnaIllegalArgumentException.class)
  public void checkArgumentIsPositive_zeroThrows() {
    LgnaIllegalArgumentException.checkArgumentIsPositive(0.0, 0);
  }

  @Test(expected = LgnaIllegalArgumentException.class)
  public void checkArgumentIsPositive_negativeThrows() {
    LgnaIllegalArgumentException.checkArgumentIsPositive(-1.0, 0);
  }

  @Test
  public void checkArgumentIsPositiveOrZero_validReturnsValue() {
    Number result = LgnaIllegalArgumentException.checkArgumentIsPositiveOrZero(0.0, 0);
    assertEquals(0.0, result.doubleValue(), 1e-10);
  }

  @Test
  public void checkArgumentIsPositiveOrZero_positiveReturnsValue() {
    Number result = LgnaIllegalArgumentException.checkArgumentIsPositiveOrZero(5.0, 1);
    assertEquals(5.0, result.doubleValue(), 1e-10);
  }

  @Test(expected = LgnaIllegalArgumentException.class)
  public void checkArgumentIsPositiveOrZero_negativeThrows() {
    LgnaIllegalArgumentException.checkArgumentIsPositiveOrZero(-0.1, 0);
  }

  @Test
  public void checkArgumentIsBetween0and1_validReturnsValue() {
    Number result = LgnaIllegalArgumentException.checkArgumentIsBetween0and1(0.5, 0);
    assertEquals(0.5, result.doubleValue(), 1e-10);
  }

  @Test
  public void checkArgumentIsBetween0and1_zero() {
    Number result = LgnaIllegalArgumentException.checkArgumentIsBetween0and1(0.0, 0);
    assertEquals(0.0, result.doubleValue(), 1e-10);
  }

  @Test
  public void checkArgumentIsBetween0and1_one() {
    Number result = LgnaIllegalArgumentException.checkArgumentIsBetween0and1(1.0, 0);
    assertEquals(1.0, result.doubleValue(), 1e-10);
  }

  @Test(expected = LgnaIllegalArgumentException.class)
  public void checkArgumentIsBetween0and1_negativeThrows() {
    LgnaIllegalArgumentException.checkArgumentIsBetween0and1(-0.1, 0);
  }

  @Test(expected = LgnaIllegalArgumentException.class)
  public void checkArgumentIsBetween0and1_aboveOneThrows() {
    LgnaIllegalArgumentException.checkArgumentIsBetween0and1(1.1, 0);
  }

  @Test
  public void constructor_storesIndexAndValue() {
    LgnaIllegalArgumentException ex = new LgnaIllegalArgumentException("test msg", 3, "badVal");
    assertEquals(3, ex.getIndex());
    assertEquals("badVal", ex.getValue());
    assertEquals("test msg", ex.getMessage());
  }

  @Test
  public void appendFormattedString_containsClassName() {
    LgnaIllegalArgumentException ex = new LgnaIllegalArgumentException("test", 0, null);
    StringBuilder sb = new StringBuilder();
    ex.appendFormattedString(sb);
    String result = sb.toString();
    assertTrue(result.contains("LgnaIllegalArgumentException"));
    assertTrue(result.contains("test"));
    assertTrue(result.contains("<html>"));
  }
}
