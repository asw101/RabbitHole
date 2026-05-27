package edu.cmu.cs.dennisc.math;

import org.junit.Test;

import java.awt.Dimension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GoldenRatioTest {
  @Test
  public void sideLengthHelpersStayConsistentWithinIntegerRounding() {
    int shorter = GoldenRatio.getShorterSideLength(1618);
    int longer = GoldenRatio.getLongerSideLength(1000);

    assertTrue(Math.abs(shorter - 1000) <= 1);
    assertEquals(1618, longer);
  }

  @Test
  public void widerAndTallerFactoriesUseExpectedDimensions() {
    Dimension wider = GoldenRatio.createWiderSizeFromWidth(1440);
    Dimension taller = GoldenRatio.createTallerSizeFromHeight(720);

    assertEquals(1440, wider.width);
    assertEquals(GoldenRatio.getShorterSideLength(1440), wider.height);
    assertEquals(GoldenRatio.getShorterSideLength(720), taller.width);
    assertEquals(720, taller.height);
  }
}
