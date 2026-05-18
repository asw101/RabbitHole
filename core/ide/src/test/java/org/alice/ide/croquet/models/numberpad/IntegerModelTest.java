package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

/**
 * Tests for {@link IntegerModel}, {@link DoubleModel}, and {@link FloatModel}.
 * Headless-guarded since NumberModel creates a JTextField.
 */
public class IntegerModelTest {

  @Test
  public void getInstance_returnsNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertNotNull(IntegerModel.getInstance());
  }

  @Test
  public void getInstance_returnsSameInstance() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertSame(IntegerModel.getInstance(), IntegerModel.getInstance());
  }

  @Test
  public void isDecimalPointSupported_returnsFalse() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertFalse(IntegerModel.getInstance().isDecimalPointSupported());
  }

  @Test
  public void setText_thenGetExpressionValue_returnsExpression() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    IntegerModel model = IntegerModel.getInstance();
    model.setText("42");
    assertNotNull(model.getExpressionValue());
  }

  @Test
  public void emptyText_explanationNotNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    IntegerModel model = IntegerModel.getInstance();
    model.setText("");
    assertNotNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void validText_explanationIsNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    IntegerModel model = IntegerModel.getInstance();
    model.setText("100");
    assertNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void negate_prependsMinus() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    IntegerModel model = IntegerModel.getInstance();
    model.setText("7");
    model.negate();
    assertTrue(model.getTextField().getText().startsWith("-"));
  }

  @Test
  public void delete_removesCharacter() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    IntegerModel model = IntegerModel.getInstance();
    model.setText("99");
    model.getTextField().setCaretPosition(2);
    model.delete();
    assertEquals("9", model.getTextField().getText());
  }

  // ---- DoubleModel ----

  @Test
  public void doubleModel_getInstance_returnsNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertNotNull(DoubleModel.getInstance());
  }

  @Test
  public void doubleModel_getInstance_returnsSameInstance() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertSame(DoubleModel.getInstance(), DoubleModel.getInstance());
  }

  @Test
  public void doubleModel_supportsDecimalPoint() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertTrue(DoubleModel.getInstance().isDecimalPointSupported());
  }

  // ---- FloatModel ----

  @Test
  public void floatModel_getInstance_returnsNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertNotNull(FloatModel.getInstance());
  }

  @Test
  public void floatModel_getInstance_returnsSameInstance() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertSame(FloatModel.getInstance(), FloatModel.getInstance());
  }

  @Test
  public void floatModel_supportsDecimalPoint() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertTrue(FloatModel.getInstance().isDecimalPointSupported());
  }
}
