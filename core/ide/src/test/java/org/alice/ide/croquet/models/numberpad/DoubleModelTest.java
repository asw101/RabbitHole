package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.DoubleLiteral;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class DoubleModelTest {
  private static DoubleModel model() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel model = DoubleModel.getInstance();
    model.setText("");
    return model;
  }

  @Test
  public void singletonReturnsSameInstance() {
    assertSame(DoubleModel.getInstance(), DoubleModel.getInstance());
  }

  @Test
  public void decimalPointIsSupported() {
    assertTrue(DoubleModel.getInstance().isDecimalPointSupported());
  }

  @Test
  public void validDoubleProducesDoubleLiteral() {
    DoubleModel model = model();
    model.setText("3.14159");

    assertTrue(model.getExpressionValue() instanceof DoubleLiteral);
  }

  @Test
  public void invalidTextProducesNoExpression() {
    DoubleModel model = model();
    model.setText("not-a-number");

    assertNull(model.getExpressionValue());
    assertEquals("isNotValid", model.getExplanationIfOkButtonShouldBeDisabled());
  }
}
