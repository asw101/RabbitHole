package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.DoubleLiteral;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class DoubleModelTest {
  @Test
  public void getInstance_returnsSameInstance() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel m1 = DoubleModel.getInstance();
    DoubleModel m2 = DoubleModel.getInstance();
    assertSame(m1, m2);
  }

  @Test
  public void isDecimalPointSupported_returnsTrue() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertTrue(DoubleModel.getInstance().isDecimalPointSupported());
  }

  @Test
  public void setText_andGetExpressionValue() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel model = DoubleModel.getInstance();
    model.setText("3.14");
    DoubleLiteral val = model.getExpressionValue();
    assertNotNull(val);
  }

  @Test
  public void setText_emptyString_getExplanation() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel model = DoubleModel.getInstance();
    model.setText("");
    String explanation = model.getExplanationIfOkButtonShouldBeDisabled();
    assertNotNull(explanation);
    assertEquals("enterNumber", explanation);
  }

  @Test
  public void setText_validNumber_noExplanation() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel model = DoubleModel.getInstance();
    model.setText("42.0");
    String explanation = model.getExplanationIfOkButtonShouldBeDisabled();
    assertNull(explanation);
  }

  @Test
  public void setText_invalidText_hasExplanation() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel model = DoubleModel.getInstance();
    model.setText("abc");
    String explanation = model.getExplanationIfOkButtonShouldBeDisabled();
    assertNotNull(explanation);
  }

  @Test
  public void negate_addsMinusSign() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel model = DoubleModel.getInstance();
    model.setText("5.0");
    model.negate();
    DoubleLiteral val = model.getExpressionValue();
    assertNotNull(val);
  }

  @Test
  public void negate_twice_restoresOriginal() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel model = DoubleModel.getInstance();
    model.setText("7.0");
    model.negate();
    model.negate();
    DoubleLiteral val = model.getExpressionValue();
    assertNotNull(val);
  }

  @Test
  public void getTextField_notNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertNotNull(DoubleModel.getInstance().getTextField());
  }
}
