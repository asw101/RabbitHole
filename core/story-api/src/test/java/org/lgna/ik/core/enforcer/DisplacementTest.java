package org.lgna.ik.core.enforcer;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Characterization tests for Displacement — a pure value type wrapping
 * a double[] representing position or orientation change.
 * Displacement is the cleanest extraction (already public static in the original).
 */
public class DisplacementTest {

  private static final double DELTA = 1e-12;

  @Test
  public void constructorStoresArray() {
    double[] data = {1.0, 2.0, 3.0};
    Displacement d = new Displacement(data);
    assertEquals(3, d.size());
    assertEquals(1.0, d.storage[0], DELTA);
    assertEquals(2.0, d.storage[1], DELTA);
    assertEquals(3.0, d.storage[2], DELTA);
  }

  @Test
  public void sizeReturnsStorageLength() {
    Displacement d = new Displacement(new double[]{0, 0, 0, 0, 0});
    assertEquals(5, d.size());
  }

  @Test
  public void createThisMinusOtherSubtractsElementwise() {
    Displacement a = new Displacement(new double[]{10.0, 20.0, 30.0});
    Displacement b = new Displacement(new double[]{1.0, 2.0, 3.0});
    Displacement result = a.createThisMinusOther(b);

    assertArrayEquals(new double[]{9.0, 18.0, 27.0}, result.storage, DELTA);
  }

  @Test
  public void createThisMinusOtherPreservesOriginals() {
    Displacement a = new Displacement(new double[]{5.0, 6.0});
    Displacement b = new Displacement(new double[]{1.0, 2.0});
    a.createThisMinusOther(b);

    assertEquals(5.0, a.storage[0], DELTA);
    assertEquals(1.0, b.storage[0], DELTA);
  }

  @Test
  public void createThisMinusOtherWithZeroProducesCopy() {
    Displacement a = new Displacement(new double[]{3.0, 4.0, 5.0});
    Displacement zero = new Displacement(new double[]{0.0, 0.0, 0.0});
    Displacement result = a.createThisMinusOther(zero);

    assertArrayEquals(a.storage, result.storage, DELTA);
  }

  @Test(expected = RuntimeException.class)
  public void createThisMinusOtherThrowsOnLengthMismatch() {
    Displacement a = new Displacement(new double[]{1.0, 2.0});
    Displacement b = new Displacement(new double[]{1.0, 2.0, 3.0});
    a.createThisMinusOther(b);
  }

  @Test
  public void emptyDisplacementHasSizeZero() {
    Displacement d = new Displacement(new double[]{});
    assertEquals(0, d.size());
  }

  @Test
  public void createThisMinusOtherWithNegativesWorks() {
    Displacement a = new Displacement(new double[]{-1.0, -2.0});
    Displacement b = new Displacement(new double[]{-3.0, 1.0});
    Displacement result = a.createThisMinusOther(b);

    assertArrayEquals(new double[]{2.0, -3.0}, result.storage, DELTA);
  }
}
