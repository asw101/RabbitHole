package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.FloatLiteral;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class FloatModelTest {
  @Test
  public void getInstance_returnsSameInstance() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    FloatModel m1 = FloatModel.getInstance();
    FloatModel m2 = FloatModel.getInstance();
    assertSame(m1, m2);
  }

  @Test
  public void isDecimalPointSupported_returnsTrue() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertTrue(FloatModel.getInstance().isDecimalPointSupported());
  }

  @Test
  public void setText_andGetValue() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    FloatModel model = FloatModel.getInstance();
    model.setText("1.5");
    FloatLiteral val = model.getExpressionValue();
    assertNotNull(val);
  }

  @Test
  public void setText_emptyString_explanation() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    FloatModel model = FloatModel.getInstance();
    model.setText("");
    assertEquals("enterNumber", model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void setText_validNumber_noExplanation() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    FloatModel model = FloatModel.getInstance();
    model.setText("2.5");
    assertNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void getTextField_notNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertNotNull(FloatModel.getInstance().getTextField());
  }
}
