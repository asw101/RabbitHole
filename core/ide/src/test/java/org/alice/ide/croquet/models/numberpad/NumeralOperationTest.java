package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class NumeralOperationTest {
  private static void assumeNotHeadless() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  }

  @Test
  public void sameModelAndNumeralReuseInstance() {
    assumeNotHeadless();
    assertSame(NumeralOperation.getInstance(IntegerModel.getInstance(), (short) 7),
        NumeralOperation.getInstance(IntegerModel.getInstance(), (short) 7));
  }

  @Test
  public void differentNumeralsUseDifferentInstances() {
    assumeNotHeadless();
    assertNotSame(NumeralOperation.getInstance(IntegerModel.getInstance(), (short) 1),
        NumeralOperation.getInstance(IntegerModel.getInstance(), (short) 2));
  }

  @Test
  public void differentModelsUseDifferentInstances() {
    assumeNotHeadless();
    assertNotSame(NumeralOperation.getInstance(IntegerModel.getInstance(), (short) 5),
        NumeralOperation.getInstance(DoubleModel.getInstance(), (short) 5));
  }

  @Test
  public void localizedNameMatchesNumeral() {
    assumeNotHeadless();
    assertEquals("9", NumeralOperation.getInstance(IntegerModel.getInstance(), (short) 9).getImp().getName());
  }
}
