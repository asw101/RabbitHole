package org.alice.stageide.custom;

import org.junit.Test;

import static org.junit.Assert.*;

public class AudioSourceCustomExpressionCreatorTest {

  @Test
  public void toDouble_zero_returnsZero() {
    assertEquals(0.0, VolumeLevelUtilities.toDouble(0), 0.0001);
  }

  @Test
  public void toDouble_100_returnsOne() {
    assertEquals(1.0, VolumeLevelUtilities.toDouble(100), 0.0001);
  }

  @Test
  public void toDouble_200_returnsTwo() {
    assertEquals(2.0, VolumeLevelUtilities.toDouble(200), 0.0001);
  }

  @Test
  public void toDouble_50_returnsPointFive() {
    assertEquals(0.5, VolumeLevelUtilities.toDouble(50), 0.0001);
  }

  @Test
  public void toDouble_1_returnsPointZeroOne() {
    assertEquals(0.01, VolumeLevelUtilities.toDouble(1), 0.0001);
  }

  @Test
  public void toInt_zero_returnsZero() {
    assertEquals(0, VolumeLevelUtilities.toInt(0.0));
  }

  @Test
  public void toInt_one_returns100() {
    assertEquals(100, VolumeLevelUtilities.toInt(1.0));
  }

  @Test
  public void toInt_two_returns200() {
    assertEquals(200, VolumeLevelUtilities.toInt(2.0));
  }

  @Test
  public void toInt_pointFive_returns50() {
    assertEquals(50, VolumeLevelUtilities.toInt(0.5));
  }

  @Test
  public void toInt_NaN_returnsZero() {
    assertEquals(0, VolumeLevelUtilities.toInt(Double.NaN));
  }

  @Test
  public void toInt_positiveInfinity_returnsMaxInt() {
    assertEquals(Integer.MAX_VALUE, VolumeLevelUtilities.toInt(Double.POSITIVE_INFINITY));
  }

  @Test
  public void toInt_negativeInfinity_returnsMinInt() {
    assertEquals(Integer.MIN_VALUE, VolumeLevelUtilities.toInt(Double.NEGATIVE_INFINITY));
  }

  @Test
  public void roundTrip_100() {
    int original = 100;
    double asDouble = VolumeLevelUtilities.toDouble(original);
    int back = VolumeLevelUtilities.toInt(asDouble);
    assertEquals(original, back);
  }

  @Test
  public void roundTrip_0() {
    int original = 0;
    double asDouble = VolumeLevelUtilities.toDouble(original);
    int back = VolumeLevelUtilities.toInt(asDouble);
    assertEquals(original, back);
  }

  @Test
  public void roundTrip_200() {
    int original = 200;
    double asDouble = VolumeLevelUtilities.toDouble(original);
    int back = VolumeLevelUtilities.toInt(asDouble);
    assertEquals(original, back);
  }

  @Test
  public void roundTrip_75() {
    int original = 75;
    double asDouble = VolumeLevelUtilities.toDouble(original);
    int back = VolumeLevelUtilities.toInt(asDouble);
    assertEquals(original, back);
  }

  @Test
  public void createDetails_returnsNonNull() {
    assertNotNull(VolumeLevelUtilities.createDetails());
  }
}
