package edu.cmu.cs.dennisc.math;

import org.junit.Test;

import java.awt.Dimension;

import static org.junit.Assert.*;

public class GoldenRatioDeepTest {

  @Test
  public void phi_value() {
    assertEquals(1.6180339887, GoldenRatio.PHI, 1e-10);
  }

  @Test
  public void phi_satisfiesGoldenProperty() {
    double phiSquared = GoldenRatio.PHI * GoldenRatio.PHI;
    assertEquals(GoldenRatio.PHI + 1, phiSquared, 0.01);
  }

  @Test
  public void getShorterSideLength_100() {
    int shorter = GoldenRatio.getShorterSideLength(100);
    assertEquals((int) (100 / GoldenRatio.PHI), shorter);
  }

  @Test
  public void getLongerSideLength_100() {
    int longer = GoldenRatio.getLongerSideLength(100);
    assertEquals((int) (100 * GoldenRatio.PHI), longer);
  }

  @Test
  public void getShorterSideLength_zero() {
    assertEquals(0, GoldenRatio.getShorterSideLength(0));
  }

  @Test
  public void getLongerSideLength_zero() {
    assertEquals(0, GoldenRatio.getLongerSideLength(0));
  }

  @Test
  public void createWiderSizeFromWidth() {
    Dimension d = GoldenRatio.createWiderSizeFromWidth(200);
    assertEquals(200, d.width);
    assertEquals(GoldenRatio.getShorterSideLength(200), d.height);
    assertTrue(d.width > d.height);
  }

  @Test
  public void createWiderSizeFromHeight() {
    Dimension d = GoldenRatio.createWiderSizeFromHeight(100);
    assertEquals(100, d.height);
    assertEquals(GoldenRatio.getLongerSideLength(100), d.width);
    assertTrue(d.width > d.height);
  }

  @Test
  public void createTallerSizeFromWidth() {
    Dimension d = GoldenRatio.createTallerSizeFromWidth(100);
    assertEquals(100, d.width);
    assertEquals(GoldenRatio.getLongerSideLength(100), d.height);
    assertTrue(d.height > d.width);
  }

  @Test
  public void createTallerSizeFromHeight() {
    Dimension d = GoldenRatio.createTallerSizeFromHeight(200);
    assertEquals(200, d.height);
    assertEquals(GoldenRatio.getShorterSideLength(200), d.width);
    assertTrue(d.height > d.width);
  }

  @Test
  public void inverse_relationship() {
    int original = 500;
    int shorter = GoldenRatio.getShorterSideLength(original);
    int longer = GoldenRatio.getLongerSideLength(shorter);
    assertTrue(Math.abs(original - longer) <= 2);
  }

  @Test
  public void widerSize_widthGreaterThanHeight() {
    for (int w = 50; w <= 500; w += 50) {
      Dimension d = GoldenRatio.createWiderSizeFromWidth(w);
      assertTrue("Width " + w + " should be wider", d.width >= d.height);
    }
  }

  @Test
  public void tallerSize_heightGreaterThanWidth() {
    for (int h = 50; h <= 500; h += 50) {
      Dimension d = GoldenRatio.createTallerSizeFromHeight(h);
      assertTrue("Height " + h + " should be taller", d.height >= d.width);
    }
  }
}
