package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.FloatLiteral;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class FloatModelTest {
  private static FloatModel model() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    FloatModel model = FloatModel.getInstance();
    model.setText("");
    return model;
  }

  @Test
  public void singletonReturnsSameInstance() {
    assertSame(FloatModel.getInstance(), FloatModel.getInstance());
  }

  @Test
  public void decimalPointIsSupported() {
    assertTrue(FloatModel.getInstance().isDecimalPointSupported());
  }

  @Test
  public void validFloatProducesFloatLiteral() {
    FloatModel model = model();
    model.setText("12.5");

    assertTrue(model.getExpressionValue() instanceof FloatLiteral);
  }

  @Test
  public void nanProducesNoExpression() {
    FloatModel model = model();
    model.setText("NaN");

    assertNull(model.getExpressionValue());
    assertEquals("isNotValid", model.getExplanationIfOkButtonShouldBeDisabled());
  }
}
