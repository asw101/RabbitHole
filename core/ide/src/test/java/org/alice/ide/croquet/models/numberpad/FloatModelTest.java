package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.FloatLiteral;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class FloatModelTest {
  @Before
  public void requireGraphicsEnvironment() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  }

  @Test
  public void getInstance_returnsSameInstance() {
    assertSame(FloatModel.getInstance(), FloatModel.getInstance());
  }

  @Test
  public void isDecimalPointSupported_returnsTrue() {
    assertTrue(FloatModel.getInstance().isDecimalPointSupported());
  }

  @Test
  public void setText_andGetValue() {
    FloatModel model = FloatModel.getInstance();
    model.setText("1.5");
    assertNotNull(model.getExpressionValue());
  }

  @Test
  public void setText_emptyString_explanation() {
    FloatModel model = FloatModel.getInstance();
    model.setText("");
    assertEquals("enterNumber", model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void setText_validNumber_noExplanation() {
    FloatModel model = FloatModel.getInstance();
    model.setText("2.5");
    assertNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void getTextField_notNull() {
    assertNotNull(FloatModel.getInstance().getTextField());
  }
}
