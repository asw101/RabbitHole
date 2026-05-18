package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

/**
 * Tests for {@link PlusMinusOperation} — toggles sign on NumberModel text.
 * Covers singleton caching, perform behavior via model mutation.
 */
public class PlusMinusOperationTest {

  private IntegerModel model;

  @Before
  public void setUp() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    model = IntegerModel.getInstance();
    model.setText("");
  }

  // ---- singleton caching ----

  @Test
  public void getInstance_returnsSameForSameModel() {
    PlusMinusOperation op1 = PlusMinusOperation.getInstance(model);
    PlusMinusOperation op2 = PlusMinusOperation.getInstance(model);
    assertSame(op1, op2);
  }

  @Test
  public void getInstance_differentModels_differentInstances() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel doubleModel = DoubleModel.getInstance();
    PlusMinusOperation op1 = PlusMinusOperation.getInstance(model);
    PlusMinusOperation op2 = PlusMinusOperation.getInstance(doubleModel);
    assertNotSame(op1, op2);
  }

  // ---- behavior via model ----

  @Test
  public void negate_positiveNumber_prependsMinus() {
    model.setText("42");
    model.negate();
    assertTrue(model.getTextField().getText().startsWith("-"));
  }

  @Test
  public void negate_negativeNumber_removesMinus() {
    model.setText("-42");
    model.negate();
    assertFalse(model.getTextField().getText().startsWith("-"));
  }

  @Test
  public void negate_twice_restoresOriginal() {
    model.setText("100");
    String original = model.getTextField().getText();
    model.negate();
    model.negate();
    assertEquals(original, model.getTextField().getText());
  }

  @Test
  public void negate_emptyText_prependsMinus() {
    model.setText("");
    model.negate();
    assertEquals("-", model.getTextField().getText());
  }

  @Test
  public void negate_zero_prependsMinus() {
    model.setText("0");
    model.negate();
    assertTrue(model.getTextField().getText().startsWith("-"));
  }

  @Test
  public void negate_singleDigit() {
    model.setText("7");
    model.negate();
    assertEquals("-7", model.getTextField().getText());
  }

  @Test
  public void negate_multiDigit() {
    model.setText("12345");
    model.negate();
    assertEquals("-12345", model.getTextField().getText());
  }

  // ---- integration with validation ----

  @Test
  public void negate_validNumber_remainsValid() {
    model.setText("42");
    model.negate();
    assertNull("Negated number should still be valid",
        model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void negate_producesNegativeExpression() {
    model.setText("10");
    model.negate();
    assertNotNull(model.getExpressionValue());
  }
}
