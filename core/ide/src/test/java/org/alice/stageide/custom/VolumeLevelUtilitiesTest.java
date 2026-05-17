package org.alice.stageide.custom;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link VolumeLevelUtilities} — volume int/double conversion
 * with BigDecimal precision.
 */
public class VolumeLevelUtilitiesTest {

  // ---- toDouble ----

  @Test
  public void toDouble_zeroIsZero() {
    assertEquals(0.0, VolumeLevelUtilities.toDouble(0), 0.0);
  }

  @Test
  public void toDouble_100IsOne() {
    assertEquals(1.0, VolumeLevelUtilities.toDouble(100), 0.0);
  }

  @Test
  public void toDouble_200IsTwo() {
    assertEquals(2.0, VolumeLevelUtilities.toDouble(200), 0.0);
  }

  @Test
  public void toDouble_50IsHalf() {
    assertEquals(0.5, VolumeLevelUtilities.toDouble(50), 0.0);
  }

  @Test
  public void toDouble_150Is1Point5() {
    assertEquals(1.5, VolumeLevelUtilities.toDouble(150), 0.0);
  }

  @Test
  public void toDouble_1Is0Point01() {
    assertEquals(0.01, VolumeLevelUtilities.toDouble(1), 1e-10);
  }

  // ---- toInt ----

  @Test
  public void toInt_zeroIsZero() {
    assertEquals(0, VolumeLevelUtilities.toInt(0.0));
  }

  @Test
  public void toInt_oneIs100() {
    assertEquals(100, VolumeLevelUtilities.toInt(1.0));
  }

  @Test
  public void toInt_twoIs200() {
    assertEquals(200, VolumeLevelUtilities.toInt(2.0));
  }

  @Test
  public void toInt_halfIs50() {
    assertEquals(50, VolumeLevelUtilities.toInt(0.5));
  }

  @Test
  public void toInt_1Point5Is150() {
    assertEquals(150, VolumeLevelUtilities.toInt(1.5));
  }

  // ---- toInt special values ----

  @Test
  public void toInt_NaN_isZero() {
    assertEquals(0, VolumeLevelUtilities.toInt(Double.NaN));
  }

  @Test
  public void toInt_positiveInfinity_isMaxInt() {
    assertEquals(Integer.MAX_VALUE, VolumeLevelUtilities.toInt(Double.POSITIVE_INFINITY));
  }

  @Test
  public void toInt_negativeInfinity_isMinInt() {
    assertEquals(Integer.MIN_VALUE, VolumeLevelUtilities.toInt(Double.NEGATIVE_INFINITY));
  }

  // ---- round-trip ----

  @Test
  public void roundTrip_0() {
    assertEquals(0, VolumeLevelUtilities.toInt(VolumeLevelUtilities.toDouble(0)));
  }

  @Test
  public void roundTrip_100() {
    assertEquals(100, VolumeLevelUtilities.toInt(VolumeLevelUtilities.toDouble(100)));
  }

  @Test
  public void roundTrip_200() {
    assertEquals(200, VolumeLevelUtilities.toInt(VolumeLevelUtilities.toDouble(200)));
  }

  @Test
  public void roundTrip_75() {
    assertEquals(75, VolumeLevelUtilities.toInt(VolumeLevelUtilities.toDouble(75)));
  }

  // ---- createDetails ----

  @Test
  public void createDetails_returnsNonNull() {
    assertNotNull(VolumeLevelUtilities.createDetails());
  }
}
