package org.lgna.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class LgnaIllegalKeyArgumentExceptionTest {

  @Test
  public void checkArgumentIsNotNull_validReturnsValue() {
    String result = LgnaIllegalKeyArgumentException.checkArgumentIsNotNull("hello", "name");
    assertEquals("hello", result);
  }

  @Test(expected = LgnaIllegalKeyArgumentException.class)
  public void checkArgumentIsNotNull_nullThrows() {
    LgnaIllegalKeyArgumentException.checkArgumentIsNotNull(null, "name");
  }

  @Test
  public void checkArgumentIsNumber_validReturnsValue() {
    Number result = LgnaIllegalKeyArgumentException.checkArgumentIsNumber(42.0, "count");
    assertEquals(42.0, result.doubleValue(), 1e-10);
  }

  @Test(expected = LgnaIllegalKeyArgumentException.class)
  public void checkArgumentIsNumber_nanThrows() {
    LgnaIllegalKeyArgumentException.checkArgumentIsNumber(Double.NaN, "value");
  }

  @Test(expected = LgnaIllegalKeyArgumentException.class)
  public void checkArgumentIsNumber_nullThrows() {
    LgnaIllegalKeyArgumentException.checkArgumentIsNumber(null, "value");
  }

  @Test
  public void checkArgumentIsPositive_validReturnsValue() {
    Number result = LgnaIllegalKeyArgumentException.checkArgumentIsPositive(5.0, "size");
    assertEquals(5.0, result.doubleValue(), 1e-10);
  }

  @Test(expected = LgnaIllegalKeyArgumentException.class)
  public void checkArgumentIsPositive_zeroThrows() {
    LgnaIllegalKeyArgumentException.checkArgumentIsPositive(0.0, "size");
  }

  @Test(expected = LgnaIllegalKeyArgumentException.class)
  public void checkArgumentIsPositive_negativeThrows() {
    LgnaIllegalKeyArgumentException.checkArgumentIsPositive(-1.0, "size");
  }

  @Test
  public void checkArgumentIsPositiveOrZero_zeroValid() {
    Number result = LgnaIllegalKeyArgumentException.checkArgumentIsPositiveOrZero(0.0, "age");
    assertEquals(0.0, result.doubleValue(), 1e-10);
  }

  @Test
  public void checkArgumentIsPositiveOrZero_positiveValid() {
    Number result = LgnaIllegalKeyArgumentException.checkArgumentIsPositiveOrZero(5.0, "age");
    assertEquals(5.0, result.doubleValue(), 1e-10);
  }

  @Test(expected = LgnaIllegalKeyArgumentException.class)
  public void checkArgumentIsPositiveOrZero_negativeThrows() {
    LgnaIllegalKeyArgumentException.checkArgumentIsPositiveOrZero(-0.1, "age");
  }

  @Test
  public void checkArgumentIsBetween0and1_midValue() {
    Number result = LgnaIllegalKeyArgumentException.checkArgumentIsBetween0and1(0.5, "opacity");
    assertEquals(0.5, result.doubleValue(), 1e-10);
  }

  @Test
  public void checkArgumentIsBetween0and1_zero() {
    Number result = LgnaIllegalKeyArgumentException.checkArgumentIsBetween0and1(0.0, "opacity");
    assertEquals(0.0, result.doubleValue(), 1e-10);
  }

  @Test
  public void checkArgumentIsBetween0and1_one() {
    Number result = LgnaIllegalKeyArgumentException.checkArgumentIsBetween0and1(1.0, "opacity");
    assertEquals(1.0, result.doubleValue(), 1e-10);
  }

  @Test(expected = LgnaIllegalKeyArgumentException.class)
  public void checkArgumentIsBetween0and1_negativeThrows() {
    LgnaIllegalKeyArgumentException.checkArgumentIsBetween0and1(-0.1, "opacity");
  }

  @Test(expected = LgnaIllegalKeyArgumentException.class)
  public void checkArgumentIsBetween0and1_aboveOneThrows() {
    LgnaIllegalKeyArgumentException.checkArgumentIsBetween0and1(1.1, "opacity");
  }

  @Test
  public void constructor_storesValues() {
    LgnaIllegalKeyArgumentException ex = new LgnaIllegalKeyArgumentException("detail", "keyName", "badVal");
    assertEquals("detail", ex.getMessage());
  }

  @Test
  public void appendFormattedString_containsKeyAndValue() {
    LgnaIllegalKeyArgumentException ex = new LgnaIllegalKeyArgumentException("must be positive", "radius", -5);
    StringBuilder sb = new StringBuilder();
    ex.appendFormattedString(sb);
    String result = sb.toString();
    assertTrue(result.contains("radius"));
    assertTrue(result.contains("must be positive"));
    assertTrue(result.contains("-5"));
  }
}
