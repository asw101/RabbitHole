package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import static org.junit.Assert.*;

public class MathUtilitiesTest {

  @Test
  public void minShort_singleValue() {
    assertEquals((short) 5, MathUtilities.minShort((short) 5));
  }

  @Test
  public void minShort_multipleValues() {
    assertEquals((short) 1, MathUtilities.minShort((short) 3, (short) 1, (short) 7));
  }

  @Test
  public void maxShort_singleValue() {
    assertEquals((short) 5, MathUtilities.maxShort((short) 5));
  }

  @Test
  public void maxShort_multipleValues() {
    assertEquals((short) 7, MathUtilities.maxShort((short) 3, (short) 1, (short) 7));
  }

  @Test
  public void minInt_singleValue() {
    assertEquals(5, MathUtilities.minInt(5));
  }

  @Test
  public void minInt_multipleValues() {
    assertEquals(-10, MathUtilities.minInt(3, -10, 7, 100));
  }

  @Test
  public void maxInt_singleValue() {
    assertEquals(5, MathUtilities.maxInt(5));
  }

  @Test
  public void maxInt_multipleValues() {
    assertEquals(100, MathUtilities.maxInt(3, -10, 7, 100));
  }

  @Test
  public void minLong_singleValue() {
    assertEquals(5L, MathUtilities.minLong(5L));
  }

  @Test
  public void minLong_multipleValues() {
    assertEquals(-100L, MathUtilities.minLong(3L, -100L, 7L));
  }

  @Test
  public void maxLong_singleValue() {
    assertEquals(5L, MathUtilities.maxLong(5L));
  }

  @Test
  public void maxLong_multipleValues() {
    assertEquals(1000L, MathUtilities.maxLong(3L, 1000L, 7L));
  }

  @Test
  public void minFloat_singleValue() {
    assertEquals(5.0f, MathUtilities.minFloat(5.0f), 1e-6f);
  }

  @Test
  public void minFloat_multipleValues() {
    assertEquals(-1.5f, MathUtilities.minFloat(3.0f, -1.5f, 7.0f), 1e-6f);
  }

  @Test
  public void maxFloat_singleValue() {
    assertEquals(5.0f, MathUtilities.maxFloat(5.0f), 1e-6f);
  }

  @Test
  public void maxFloat_multipleValues() {
    assertEquals(7.0f, MathUtilities.maxFloat(3.0f, -1.5f, 7.0f), 1e-6f);
  }

  @Test
  public void minDouble_singleValue() {
    assertEquals(5.0, MathUtilities.minDouble(5.0), 1e-10);
  }

  @Test
  public void minDouble_multipleValues() {
    assertEquals(-99.5, MathUtilities.minDouble(3.0, -99.5, 7.0, 100.0), 1e-10);
  }

  @Test
  public void maxDouble_singleValue() {
    assertEquals(5.0, MathUtilities.maxDouble(5.0), 1e-10);
  }

  @Test
  public void maxDouble_multipleValues() {
    assertEquals(100.0, MathUtilities.maxDouble(3.0, -99.5, 7.0, 100.0), 1e-10);
  }
}
