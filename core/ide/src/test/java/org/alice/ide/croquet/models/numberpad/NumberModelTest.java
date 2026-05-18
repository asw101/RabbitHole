package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class NumberModelTest {

  private IntegerModel model;

  @Before
  public void setUp() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    model = IntegerModel.getInstance();
    model.setText("");
  }

  @Test
  public void setTextAndGetContent() {
    model.setText("123");
    assertEquals("enterNumber should be null for valid number",
        null, model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void negatePositiveNumber() {
    model.setText("123");
    model.negate();
    // After negate, the text should start with minus
    assertNull("Negated valid number should parse fine",
        model.getExplanationIfOkButtonShouldBeDisabled());
    assertNotNull("Should still produce an expression", model.getExpressionValue());
  }

  @Test
  public void negateTwiceReturnsOriginal() {
    model.setText("456");
    model.negate();
    model.negate();
    assertNull("Double negate should yield valid number",
        model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void deleteFromEnd() {
    model.setText("789");
    // Position caret at end
    model.getTextField().setCaretPosition(3);
    model.delete();
    // After deleting one char from "789", we get "78"
    assertNull("Should still be a valid number after delete",
        model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void replaceSelectionWithNumeral() {
    model.setText("");
    model.replaceSelection((short) 5);
    assertNull("After inserting digit, should be valid",
        model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void emptyTextDisablesOk() {
    model.setText("");
    assertEquals("enterNumber", model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void validNumberEnablesOk() {
    model.setText("42");
    assertNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void integerModelDoesNotSupportDecimalPoint() {
    assertFalse(model.isDecimalPointSupported());
  }

  @Test
  public void doubleModelSupportsDecimalPoint() {
    DoubleModel doubleModel = DoubleModel.getInstance();
    assertTrue(doubleModel.isDecimalPointSupported());
  }
}
