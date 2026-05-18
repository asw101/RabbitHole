package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class FloatModelTest {

  @Test
  public void getInstance_returnsNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertNotNull(FloatModel.getInstance());
  }

  @Test
  public void getInstance_returnsSameInstance() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertSame(FloatModel.getInstance(), FloatModel.getInstance());
  }

  @Test
  public void isDecimalPointSupported_returnsTrue() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertTrue(FloatModel.getInstance().isDecimalPointSupported());
  }

  @Test
  public void setText_thenGetExpressionValue_returnsFloatLiteral() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    FloatModel model = FloatModel.getInstance();
    model.setText("2.5");
    assertNotNull(model.getExpressionValue());
  }

  @Test
  public void emptyText_explanationNotNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    FloatModel model = FloatModel.getInstance();
    model.setText("");
    assertNotNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void validText_explanationIsNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    FloatModel model = FloatModel.getInstance();
    model.setText("1.0");
    assertNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }
}
