package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class PlusMinusOperationTest {
  private static void assumeNotHeadless() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  }

  @Test
  public void sameModelReusesInstance() {
    assumeNotHeadless();
    assertSame(PlusMinusOperation.getInstance(IntegerModel.getInstance()),
        PlusMinusOperation.getInstance(IntegerModel.getInstance()));
  }

  @Test
  public void differentModelsUseDifferentInstances() {
    assumeNotHeadless();
    assertNotSame(PlusMinusOperation.getInstance(IntegerModel.getInstance()),
        PlusMinusOperation.getInstance(DoubleModel.getInstance()));
  }

  @Test
  public void localizedNameUsesPlusMinusSymbol() {
    assumeNotHeadless();
    assertEquals("±", PlusMinusOperation.getInstance(IntegerModel.getInstance()).getImp().getName());
  }
}
