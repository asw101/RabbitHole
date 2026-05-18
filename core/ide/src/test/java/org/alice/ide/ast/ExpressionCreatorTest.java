package org.alice.ide.ast;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExpressionCreatorTest {
  @Test
  public void milliDecimalPlaces_is3() {
    assertEquals(3, ExpressionCreator.MILLI_DECIMAL_PLACES);
  }

  @Test
  public void microDecimalPlaces_is6() {
    assertEquals(6, ExpressionCreator.MICRO_DECIMAL_PLACES);
  }

  @Test
  public void defaultDecimalPlaces_isMicro() {
    assertEquals(ExpressionCreator.MICRO_DECIMAL_PLACES, ExpressionCreator.DEFAULT_DECIMAL_PLACES);
  }

  @Test
  public void cannotCreateExpressionException_storesValue() {
    Object value = "test";
    ExpressionCreator.CannotCreateExpressionException ex = new ExpressionCreator.CannotCreateExpressionException(value);
    assertSame(value, ex.getValue());
  }

  @Test
  public void cannotCreateExpressionException_nullValue() {
    ExpressionCreator.CannotCreateExpressionException ex = new ExpressionCreator.CannotCreateExpressionException(null);
    assertNull(ex.getValue());
  }
}
