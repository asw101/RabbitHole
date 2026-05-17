package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import static org.junit.Assert.*;

public class IntegerUtilitiesTest {

  @Test
  public void toFlooredInteger_positiveDouble() {
    assertEquals(Integer.valueOf(2), IntegerUtilities.toFlooredInteger(2.7));
  }

  @Test
  public void toFlooredInteger_negativeDouble() {
    assertEquals(Integer.valueOf(-3), IntegerUtilities.toFlooredInteger(-2.3));
  }

  @Test
  public void toRoundedInteger_roundsUp() {
    assertEquals(Integer.valueOf(3), IntegerUtilities.toRoundedInteger(2.5));
  }

  @Test
  public void toRoundedInteger_roundsDown() {
    assertEquals(Integer.valueOf(2), IntegerUtilities.toRoundedInteger(2.4));
  }

  @Test
  public void toCeilingedInteger_positiveDouble() {
    assertEquals(Integer.valueOf(3), IntegerUtilities.toCeilingedInteger(2.1));
  }

  @Test
  public void toCeilingedInteger_negativeDouble() {
    assertEquals(Integer.valueOf(-2), IntegerUtilities.toCeilingedInteger(-2.7));
  }

  @Test
  public void toFlooredInteger_float() {
    assertEquals(Integer.valueOf(2), IntegerUtilities.toFlooredInteger(2.9f));
  }

  @Test
  public void toRoundedInteger_float() {
    assertEquals(Integer.valueOf(3), IntegerUtilities.toRoundedInteger(2.6f));
  }

  @Test
  public void toCeilingedInteger_float() {
    assertEquals(Integer.valueOf(4), IntegerUtilities.toCeilingedInteger(3.1f));
  }

  @Test
  public void toFlooredInteger_wholeDouble() {
    assertEquals(Integer.valueOf(5), IntegerUtilities.toFlooredInteger(5.0));
  }
}
