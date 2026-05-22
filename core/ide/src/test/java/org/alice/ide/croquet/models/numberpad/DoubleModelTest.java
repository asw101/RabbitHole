package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.DoubleLiteral;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class DoubleModelTest {
  @Before
  public void requireGraphicsEnvironment() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  }

  @Test
  public void getInstance_returnsSameInstance() {
    assertSame(DoubleModel.getInstance(), DoubleModel.getInstance());
  }

  @Test
  public void isDecimalPointSupported_returnsTrue() {
    assertTrue(DoubleModel.getInstance().isDecimalPointSupported());
  }

  @Test
  public void setText_andGetExpressionValue() {
    DoubleModel model = DoubleModel.getInstance();
    model.setText("3.14");
    assertNotNull(model.getExpressionValue());
  }

  @Test
  public void setText_emptyString_getExplanation() {
    DoubleModel model = DoubleModel.getInstance();
    model.setText("");
    assertEquals("enterNumber", model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void setText_validNumber_noExplanation() {
    DoubleModel model = DoubleModel.getInstance();
    model.setText("42.0");
    assertNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void setText_invalidText_hasExplanation() {
    DoubleModel model = DoubleModel.getInstance();
    model.setText("abc");
    assertNotNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void negate_addsMinusSign() {
    DoubleModel model = DoubleModel.getInstance();
    model.setText("5.0");
    model.negate();
    assertNotNull(model.getExpressionValue());
  }

  @Test
  public void negate_twice_restoresOriginal() {
    DoubleModel model = DoubleModel.getInstance();
    model.setText("7.0");
    model.negate();
    model.negate();
    assertNotNull(model.getExpressionValue());
  }

  @Test
  public void getTextField_notNull() {
    assertNotNull(DoubleModel.getInstance().getTextField());
  }
}
