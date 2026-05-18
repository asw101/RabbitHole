package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.IntegerLiteral;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class IntegerModelTest {
  @Before
  public void requireGraphicsEnvironment() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  }

  @Test
  public void getInstance_returnsSameInstance() {
    assertSame(IntegerModel.getInstance(), IntegerModel.getInstance());
  }

  @Test
  public void isDecimalPointSupported_returnsFalse() {
    assertFalse(IntegerModel.getInstance().isDecimalPointSupported());
  }

  @Test
  public void setText_andGetValue() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("42");
    Expression val = model.getExpressionValue();
    assertNotNull(val);
    assertTrue(val instanceof IntegerLiteral);
  }

  @Test
  public void setText_emptyString_explanationIsEnterNumber() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("");
    assertEquals("enterNumber", model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void setText_validNumber_noExplanation() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("100");
    assertNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void setText_largeValue_returnsMaxValue() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText(Long.toString((long) Integer.MAX_VALUE + 1));
    assertNotNull(model.getExpressionValue());
  }

  @Test
  public void setText_negativeValue_works() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("-10");
    Expression val = model.getExpressionValue();
    assertNotNull(val);
    assertTrue(val instanceof IntegerLiteral);
  }

  @Test
  public void negate_works() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("5");
    model.negate();
    assertNotNull(model.getExpressionValue());
  }

  @Test
  public void delete_removesLastChar() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("123");
    model.getTextField().setCaretPosition(3);
    model.delete();
    assertNotNull(model.getExpressionValue());
  }
}
