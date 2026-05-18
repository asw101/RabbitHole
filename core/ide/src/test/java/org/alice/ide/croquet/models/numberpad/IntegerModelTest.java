package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.IntegerLiteral;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class IntegerModelTest {
  private static IntegerModel model() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    IntegerModel model = IntegerModel.getInstance();
    model.setText("");
    return model;
  }

  @Test
  public void singletonReturnsSameInstance() {
    assertSame(IntegerModel.getInstance(), IntegerModel.getInstance());
  }

  @Test
  public void decimalPointIsNotSupported() {
    assertFalse(IntegerModel.getInstance().isDecimalPointSupported());
  }

  @Test
  public void maximumIntegerProducesIntegerLiteral() {
    IntegerModel model = model();
    model.setText(String.valueOf(Integer.MAX_VALUE));

    assertTrue(model.getExpressionValue() instanceof IntegerLiteral);
  }

  @Test
  public void valueAboveMaximumProducesFieldAccess() {
    IntegerModel model = model();
    model.setText(String.valueOf((long) Integer.MAX_VALUE + 1L));

    assertTrue(model.getExpressionValue() instanceof FieldAccess);
  }

  @Test
  public void valueBelowMinimumProducesFieldAccess() {
    IntegerModel model = model();
    model.setText(String.valueOf((long) Integer.MIN_VALUE - 1L));

    assertTrue(model.getExpressionValue() instanceof FieldAccess);
  }
}
