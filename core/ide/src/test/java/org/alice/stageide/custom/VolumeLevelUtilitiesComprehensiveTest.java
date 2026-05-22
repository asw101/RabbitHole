package org.alice.stageide.custom;

import org.junit.Test;
import org.lgna.croquet.BoundedIntegerState;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class VolumeLevelUtilitiesComprehensiveTest {

  private static Object detailField(String name) throws Exception {
    BoundedIntegerState.Details details = VolumeLevelUtilities.createDetails();
    Field field = BoundedIntegerState.Details.class.getDeclaredField(name);
    field.setAccessible(true);
    return field.get(details);
  }

  @Test
  public void createDetailsReturnsNonNullObject() {
    assertNotNull(VolumeLevelUtilities.createDetails());
  }

  @Test
  public void createDetailsReturnsBoundedIntegerDetailsSubtype() {
    assertTrue(VolumeLevelUtilities.createDetails() instanceof BoundedIntegerState.Details);
  }

  @Test
  public void createDetailsInitialValueIsOneHundred() throws Exception {
    assertEquals(100, detailField("initialValue"));
  }

  @Test
  public void createDetailsMinimumIsZero() throws Exception {
    assertEquals(0, detailField("minimum"));
  }

  @Test
  public void createDetailsMaximumIsTwoHundred() throws Exception {
    assertEquals(200, detailField("maximum"));
  }

  @Test
  public void createDetailsUsesDefaultStepSizeOfOne() throws Exception {
    assertEquals(1, detailField("stepSize"));
  }

  @Test
  public void createDetailsReturnsFreshInstances() {
    assertNotSame(VolumeLevelUtilities.createDetails(), VolumeLevelUtilities.createDetails());
  }

  @Test
  public void toDoubleConvertsZero() {
    assertEquals(0.0, VolumeLevelUtilities.toDouble(0), 0.0);
  }

  @Test
  public void toDoubleConvertsSinglePercent() {
    assertEquals(0.01, VolumeLevelUtilities.toDouble(1), 0.0000001);
  }

  @Test
  public void toDoubleConvertsFiftyPercent() {
    assertEquals(0.5, VolumeLevelUtilities.toDouble(50), 0.0);
  }

  @Test
  public void toDoubleConvertsOneHundredPercent() {
    assertEquals(1.0, VolumeLevelUtilities.toDouble(100), 0.0);
  }

  @Test
  public void toDoubleConvertsOneHundredFiftyPercent() {
    assertEquals(1.5, VolumeLevelUtilities.toDouble(150), 0.0);
  }

  @Test
  public void toDoubleConvertsTenPercent() {
    assertEquals(0.1, VolumeLevelUtilities.toDouble(10), 0.0);
  }

  @Test
  public void toDoubleConvertsTwoHundredPercent() {
    assertEquals(2.0, VolumeLevelUtilities.toDouble(200), 0.0);
  }

  @Test
  public void toDoubleConvertsNegativeValue() {
    assertEquals(-0.25, VolumeLevelUtilities.toDouble(-25), 0.0);
  }

  @Test
  public void toIntConvertsZero() {
    assertEquals(0, VolumeLevelUtilities.toInt(0.0));
  }

  @Test
  public void toIntConvertsOnePercent() {
    assertEquals(1, VolumeLevelUtilities.toInt(0.01));
  }

  @Test
  public void toIntConvertsHalfVolume() {
    assertEquals(50, VolumeLevelUtilities.toInt(0.5));
  }

  @Test
  public void toIntConvertsFullVolume() {
    assertEquals(100, VolumeLevelUtilities.toInt(1.0));
  }

  @Test
  public void toIntConvertsOneAndAHalf() {
    assertEquals(150, VolumeLevelUtilities.toInt(1.5));
  }

  @Test
  public void toIntConvertsDoubleVolume() {
    assertEquals(200, VolumeLevelUtilities.toInt(2.0));
  }

  @Test
  public void toIntTruncatesAfterScaling() {
    assertEquals(123, VolumeLevelUtilities.toInt(1.239));
  }

  @Test
  public void toIntConvertsNegativeValue() {
    assertEquals(-75, VolumeLevelUtilities.toInt(-0.75));
  }

  @Test
  public void toIntHandlesNaN() {
    assertEquals(0, VolumeLevelUtilities.toInt(Double.NaN));
  }

  @Test
  public void toIntHandlesPositiveInfinity() {
    assertEquals(Integer.MAX_VALUE, VolumeLevelUtilities.toInt(Double.POSITIVE_INFINITY));
  }

  @Test
  public void toIntHandlesNegativeInfinity() {
    assertEquals(Integer.MIN_VALUE, VolumeLevelUtilities.toInt(Double.NEGATIVE_INFINITY));
  }

  @Test
  public void roundTripPreservesRepresentativeValues() {
    int[] values = {0, 1, 50, 75, 100, 150, 200};
    for (int value : values) {
      assertEquals(value, VolumeLevelUtilities.toInt(VolumeLevelUtilities.toDouble(value)));
    }
  }
}
