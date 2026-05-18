package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class DoubleModelTest {

  @Test
  public void getInstance_returnsNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertNotNull(DoubleModel.getInstance());
  }

  @Test
  public void getInstance_returnsSameInstance() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertSame(DoubleModel.getInstance(), DoubleModel.getInstance());
  }

  @Test
  public void isDecimalPointSupported_returnsTrue() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertTrue(DoubleModel.getInstance().isDecimalPointSupported());
  }

  @Test
  public void setText_thenGetExpressionValue_returnsDoubleLiteral() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel model = DoubleModel.getInstance();
    model.setText("3.14");
    assertNotNull(model.getExpressionValue());
  }

  @Test
  public void emptyText_explanationNotNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel model = DoubleModel.getInstance();
    model.setText("");
    String explanation = model.getExplanationIfOkButtonShouldBeDisabled();
    assertNotNull(explanation);
  }

  @Test
  public void validText_explanationIsNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel model = DoubleModel.getInstance();
    model.setText("42.0");
    assertNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void invalidText_explanationNotNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel model = DoubleModel.getInstance();
    model.setText("abc");
    assertNotNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void negate_togglesSign() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel model = DoubleModel.getInstance();
    model.setText("5.0");
    model.negate();
    String text = model.getTextField().getText();
    assertTrue(text.startsWith("-"));
  }

  @Test
  public void negate_twice_restoresOriginal() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel model = DoubleModel.getInstance();
    model.setText("5.0");
    model.negate();
    model.negate();
    String text = model.getTextField().getText();
    assertFalse(text.startsWith("-"));
  }

  @Test
  public void delete_removesLastCharacter() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel model = DoubleModel.getInstance();
    model.setText("123");
    model.getTextField().setCaretPosition(3);
    model.delete();
    assertEquals("12", model.getTextField().getText());
  }
}
