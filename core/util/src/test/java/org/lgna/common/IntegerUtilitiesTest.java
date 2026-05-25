package org.lgna.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntegerUtilitiesTest {
  @Test
  public void doubleHelpersHandlePositiveAndNegativeValues() {
    assertEquals(Integer.valueOf(2), IntegerUtilities.toFlooredInteger(2.9));
    assertEquals(Integer.valueOf(-3), IntegerUtilities.toFlooredInteger(-2.1));
    assertEquals(Integer.valueOf(3), IntegerUtilities.toRoundedInteger(2.5));
    assertEquals(Integer.valueOf(-2), IntegerUtilities.toRoundedInteger(-2.4));
    assertEquals(Integer.valueOf(3), IntegerUtilities.toCeilingedInteger(2.1));
    assertEquals(Integer.valueOf(-2), IntegerUtilities.toCeilingedInteger(-2.9));
  }

  @Test
  public void floatHelpersMirrorDoubleBehavior() {
    assertEquals(Integer.valueOf(1), IntegerUtilities.toFlooredInteger(1.9f));
    assertEquals(Integer.valueOf(-2), IntegerUtilities.toFlooredInteger(-1.2f));
    assertEquals(Integer.valueOf(2), IntegerUtilities.toRoundedInteger(1.6f));
    assertEquals(Integer.valueOf(-1), IntegerUtilities.toRoundedInteger(-1.4f));
    assertEquals(Integer.valueOf(2), IntegerUtilities.toCeilingedInteger(1.1f));
    assertEquals(Integer.valueOf(-1), IntegerUtilities.toCeilingedInteger(-1.9f));
  }
}
