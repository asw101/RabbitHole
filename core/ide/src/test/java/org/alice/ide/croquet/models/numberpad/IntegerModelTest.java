package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.IntegerLiteral;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class IntegerModelTest {
  @Test
  public void getInstance_returnsSameInstance() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    IntegerModel m1 = IntegerModel.getInstance();
    IntegerModel m2 = IntegerModel.getInstance();
    assertSame(m1, m2);
  }

  @Test
  public void isDecimalPointSupported_returnsFalse() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertFalse(IntegerModel.getInstance().isDecimalPointSupported());
  }

  @Test
  public void setText_andGetValue() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    IntegerModel model = IntegerModel.getInstance();
    model.setText("42");
    Expression val = model.getExpressionValue();
    assertNotNull(val);
    assertTrue(val instanceof IntegerLiteral);
  }

  @Test
  public void setText_emptyString_explanationIsEnterNumber() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    IntegerModel model = IntegerModel.getInstance();
    model.setText("");
    assertEquals("enterNumber", model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void setText_validNumber_noExplanation() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    IntegerModel model = IntegerModel.getInstance();
    model.setText("100");
    assertNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void setText_largeValue_returnsMaxValue() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    IntegerModel model = IntegerModel.getInstance();
    model.setText(Long.toString((long) Integer.MAX_VALUE + 1));
    Expression val = model.getExpressionValue();
    assertNotNull(val);
  }

  @Test
  public void setText_negativeValue_works() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    IntegerModel model = IntegerModel.getInstance();
    model.setText("-10");
    Expression val = model.getExpressionValue();
    assertNotNull(val);
    assertTrue(val instanceof IntegerLiteral);
  }

  @Test
  public void negate_works() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    IntegerModel model = IntegerModel.getInstance();
    model.setText("5");
    model.negate();
    Expression val = model.getExpressionValue();
    assertNotNull(val);
  }

  @Test
  public void delete_removesLastChar() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    IntegerModel model = IntegerModel.getInstance();
    model.setText("123");
    model.getTextField().setCaretPosition(3);
    model.delete();
    Expression val = model.getExpressionValue();
    assertNotNull(val);
  }
}
