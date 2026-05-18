package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Test;

import java.awt.GraphicsEnvironment;
import java.text.DecimalFormatSymbols;

import static org.junit.Assert.*;

public class DecimalPointOperationTest {
  private static void assumeNotHeadless() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  }

  @Test
  public void sameModelReusesInstance() {
    assumeNotHeadless();
    assertSame(DecimalPointOperation.getInstance(DoubleModel.getInstance()),
        DecimalPointOperation.getInstance(DoubleModel.getInstance()));
  }

  @Test
  public void differentModelsUseDifferentInstances() {
    assumeNotHeadless();
    assertNotSame(DecimalPointOperation.getInstance(FloatModel.getInstance()),
        DecimalPointOperation.getInstance(DoubleModel.getInstance()));
  }

  @Test
  public void localizedNameUsesCurrentDecimalSeparator() {
    assumeNotHeadless();
    String expected = String.valueOf(new DecimalFormatSymbols().getDecimalSeparator());

    assertEquals(expected, DecimalPointOperation.getInstance(DoubleModel.getInstance()).getImp().getName());
  }

  @Test
  public void numberModelUsesOperationNameWhenInsertingDecimalPoint() {
    DoubleModel model = DoubleModel.getInstance();
    assumeNotHeadless();
    model.setText("12");
    model.getTextField().setCaretPosition(2);
    model.replaceSelectionWithDecimalPoint();

    assertEquals("12" + DecimalPointOperation.getInstance(model).getImp().getName(), model.getTextField().getText());
  }
}
