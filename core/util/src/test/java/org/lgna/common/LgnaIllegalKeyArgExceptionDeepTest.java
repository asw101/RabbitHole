package org.lgna.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class LgnaIllegalKeyArgExceptionDeepTest {

  @Test
  public void constructor_storesKeyName() {
    assertEquals("detail", new LgnaIllegalKeyArgumentException("detail", "myKey", "myVal").getMessage());
  }

  @Test
  public void checkArgumentIsNotNull_validReturnsValue() {
    assertEquals(Integer.valueOf(42), LgnaIllegalKeyArgumentException.checkArgumentIsNotNull(42, "param"));
  }

  @Test(expected = LgnaIllegalKeyArgumentException.class)
  public void checkArgumentIsNotNull_nullThrows() {
    LgnaIllegalKeyArgumentException.checkArgumentIsNotNull(null, "param");
  }

  @Test
  public void checkArgumentIsNumber_validReturnsValue() {
    assertEquals(3.14, LgnaIllegalKeyArgumentException.checkArgumentIsNumber(3.14, "pi").doubleValue(), 1e-10);
  }

  @Test(expected = LgnaIllegalKeyArgumentException.class)
  public void checkArgumentIsNumber_nullThrows() {
    LgnaIllegalKeyArgumentException.checkArgumentIsNumber(null, "value");
  }

  @Test(expected = LgnaIllegalKeyArgumentException.class)
  public void checkArgumentIsNumber_nanThrows() {
    LgnaIllegalKeyArgumentException.checkArgumentIsNumber(Double.NaN, "value");
  }

  @Test
  public void checkArgumentIsPositive_validReturnsValue() {
    assertEquals(1.0, LgnaIllegalKeyArgumentException.checkArgumentIsPositive(1.0, "size").doubleValue(), 1e-10);
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
  public void checkArgumentIsPositiveOrZero_validZero() {
    assertEquals(0.0, LgnaIllegalKeyArgumentException.checkArgumentIsPositiveOrZero(0.0, "count").doubleValue(), 1e-10);
  }

  @Test
  public void checkArgumentIsPositiveOrZero_positive() {
    assertEquals(5.0, LgnaIllegalKeyArgumentException.checkArgumentIsPositiveOrZero(5.0, "count").doubleValue(), 1e-10);
  }

  @Test(expected = LgnaIllegalKeyArgumentException.class)
  public void checkArgumentIsPositiveOrZero_nullThrows() {
    LgnaIllegalKeyArgumentException.checkArgumentIsPositiveOrZero(null, "count");
  }

  @Test(expected = LgnaIllegalKeyArgumentException.class)
  public void checkArgumentIsPositiveOrZero_negativeThrows() {
    LgnaIllegalKeyArgumentException.checkArgumentIsPositiveOrZero(-0.1, "count");
  }

  @Test
  public void checkArgumentIsBetween0and1_midValue() {
    assertEquals(0.5, LgnaIllegalKeyArgumentException.checkArgumentIsBetween0and1(0.5, "opacity").doubleValue(), 1e-10);
  }

  @Test
  public void checkArgumentIsBetween0and1_zero() {
    assertEquals(0.0, LgnaIllegalKeyArgumentException.checkArgumentIsBetween0and1(0.0, "opacity").doubleValue(), 1e-10);
  }

  @Test
  public void checkArgumentIsBetween0and1_one() {
    assertEquals(1.0, LgnaIllegalKeyArgumentException.checkArgumentIsBetween0and1(1.0, "opacity").doubleValue(), 1e-10);
  }

  @Test(expected = LgnaIllegalKeyArgumentException.class)
  public void checkArgumentIsBetween0and1_nanThrows() {
    LgnaIllegalKeyArgumentException.checkArgumentIsBetween0and1(Double.NaN, "opacity");
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
  public void extendsLgnaRuntimeException() {
    assertTrue(new LgnaIllegalKeyArgumentException("test", "key", "val") instanceof LgnaRuntimeException);
  }

  @Test
  public void getFormattedString_containsKeyName() {
    String formatted = new LgnaIllegalKeyArgumentException("detail msg", "myKeyName", "badValue").getFormattedString();

    assertTrue(formatted.contains("myKeyName"));
    assertTrue(formatted.contains("detail msg"));
    assertTrue(formatted.contains("badValue"));
  }

  @Test
  public void getFormattedString_mentionsValueLabel() {
    String formatted = new LgnaIllegalKeyArgumentException("detail msg", "myKeyName", "badValue").getFormattedString();

    assertTrue(formatted.contains("value:"));
  }
}
