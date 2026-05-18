package edu.cmu.cs.dennisc.java.awt;

import edu.cmu.cs.dennisc.math.GoldenRatio;
import org.junit.Test;

import java.awt.Dimension;

import static org.junit.Assert.*;

public class DimensionUtilitiesTest {

  @Test
  public void constrainToMinimumWidth_increasesWidthWhenBelowMinimum() {
    Dimension dimension = new Dimension(10, 20);

    Dimension result = DimensionUtilities.constrainToMinimumWidth(dimension, 15);

    assertSame(dimension, result);
    assertEquals(new Dimension(15, 20), result);
  }

  @Test
  public void constrainToMinimumWidth_keepsWidthWhenAlreadyLargeEnough() {
    Dimension dimension = new Dimension(20, 30);

    DimensionUtilities.constrainToMinimumWidth(dimension, 15);

    assertEquals(new Dimension(20, 30), dimension);
  }

  @Test
  public void constrainToMinimumWidth_nullDimensionCreatesNewDimension() {
    Dimension result = DimensionUtilities.constrainToMinimumWidth(null, 12);

    assertEquals(new Dimension(12, 0), result);
  }

  @Test
  public void constrainToMinimumHeight_increasesHeightWhenBelowMinimum() {
    Dimension dimension = new Dimension(10, 20);

    Dimension result = DimensionUtilities.constrainToMinimumHeight(dimension, 25);

    assertSame(dimension, result);
    assertEquals(new Dimension(10, 25), result);
  }

  @Test
  public void constrainToMinimumHeight_nullDimensionCreatesNewDimension() {
    Dimension result = DimensionUtilities.constrainToMinimumHeight(null, 14);

    assertEquals(new Dimension(0, 14), result);
  }

  @Test
  public void constrainToMinimumSize_updatesBothDimensions() {
    Dimension dimension = new Dimension(10, 20);

    Dimension result = DimensionUtilities.constrainToMinimumSize(dimension, 15, 25);

    assertSame(dimension, result);
    assertEquals(new Dimension(15, 25), result);
  }

  @Test
  public void constrainToMinimumSize_nullDimensionCreatesNewDimension() {
    Dimension result = DimensionUtilities.constrainToMinimumSize(null, 15, 25);

    assertEquals(new Dimension(15, 25), result);
  }

  @Test
  public void constrainToMaximumWidth_decreasesWidthWhenAboveMaximum() {
    Dimension dimension = new Dimension(20, 30);

    Dimension result = DimensionUtilities.constrainToMaximumWidth(dimension, 15);

    assertSame(dimension, result);
    assertEquals(new Dimension(15, 30), result);
  }

  @Test
  public void constrainToMaximumHeight_decreasesHeightWhenAboveMaximum() {
    Dimension dimension = new Dimension(20, 30);

    Dimension result = DimensionUtilities.constrainToMaximumHeight(dimension, 25);

    assertSame(dimension, result);
    assertEquals(new Dimension(20, 25), result);
  }

  @Test
  public void constrainToMaximumSize_updatesBothDimensions() {
    Dimension dimension = new Dimension(20, 30);

    Dimension result = DimensionUtilities.constrainToMaximumSize(dimension, 15, 25);

    assertSame(dimension, result);
    assertEquals(new Dimension(15, 25), result);
  }

  @Test
  public void constrainToWidth_setsExactWidth() {
    Dimension dimension = new Dimension(20, 30);

    Dimension result = DimensionUtilities.constrainToWidth(dimension, 9);

    assertSame(dimension, result);
    assertEquals(new Dimension(9, 30), result);
  }

  @Test
  public void constrainToHeight_setsExactHeight() {
    Dimension dimension = new Dimension(20, 30);

    Dimension result = DimensionUtilities.constrainToHeight(dimension, 7);

    assertSame(dimension, result);
    assertEquals(new Dimension(20, 7), result);
  }

  @Test
  public void calculateBestFittingSize_whenWidthBasedCandidateIsTooTall_returnsHeightBasedSize() {
    Dimension result = DimensionUtilities.calculateBestFittingSize(new Dimension(100, 50), 1.0);

    assertEquals(new Dimension(50, 50), result);
  }

  @Test
  public void calculateBestFittingSize_whenHeightBasedCandidateIsTooWide_returnsWidthBasedSize() {
    Dimension result = DimensionUtilities.calculateBestFittingSize(new Dimension(50, 100), 1.0);

    assertEquals(new Dimension(50, 50), result);
  }

  @Test
  public void calculateBestFittingSize_whenBothCandidatesFit_returnsLargerArea() {
    Dimension result = DimensionUtilities.calculateBestFittingSize(new Dimension(100, 101), 0.995);

    assertEquals(new Dimension(100, 101), result);
  }

  @Test
  public void createWiderGoldenRatioSizeFromWidth_usesShorterSideLengthForHeight() {
    Dimension result = DimensionUtilities.createWiderGoldenRatioSizeFromWidth(161);

    assertEquals(new Dimension(161, GoldenRatio.getShorterSideLength(161)), result);
  }

  @Test
  public void createWiderGoldenRatioSizeFromHeight_usesLongerSideLengthForWidth() {
    Dimension result = DimensionUtilities.createWiderGoldenRatioSizeFromHeight(99);

    assertEquals(new Dimension(GoldenRatio.getLongerSideLength(99), 99), result);
  }

  @Test
  public void createTallerGoldenRatioSizeFromWidth_usesLongerSideLengthForHeight() {
    Dimension result = DimensionUtilities.createTallerGoldenRatioSizeFromWidth(77);

    assertEquals(new Dimension(77, GoldenRatio.getLongerSideLength(77)), result);
  }

  @Test
  public void createTallerGoldenRatioSizeFromHeight_usesShorterSideLengthForWidth() {
    Dimension result = DimensionUtilities.createTallerGoldenRatioSizeFromHeight(88);

    assertEquals(new Dimension(GoldenRatio.getShorterSideLength(88), 88), result);
  }
}
