package edu.cmu.cs.dennisc.math;

import org.junit.Test;

import static org.junit.Assert.*;

public class SineCosineCacheExtendedTest {

  @Test
  public void constructor_10_createsArrays() {
    SineCosineCache cache = new SineCosineCache(10);
    assertEquals(10, cache.cosines.length);
    assertEquals(10, cache.sines.length);
    assertEquals(10, cache.angles.length);
  }

  @Test
  public void cosines_atZero_isOne() {
    SineCosineCache cache = new SineCosineCache(10);
    assertEquals(1.0, cache.cosines[0], 1e-10);
  }

  @Test
  public void sines_atZero_isZero() {
    SineCosineCache cache = new SineCosineCache(10);
    assertEquals(0.0, cache.sines[0], 1e-10);
  }

  @Test
  public void angles_atZero_isZero() {
    SineCosineCache cache = new SineCosineCache(10);
    assertEquals(0.0, cache.angles[0], 1e-10);
  }

  @Test
  public void getSine_quadrant0() {
    SineCosineCache cache = new SineCosineCache(10);
    assertEquals(cache.sines[0], cache.getSine(0, 0), 1e-10);
    assertEquals(cache.sines[5], cache.getSine(0, 5), 1e-10);
  }

  @Test
  public void getSine_quadrant1() {
    SineCosineCache cache = new SineCosineCache(10);
    double val = cache.getSine(1, 0);
    assertTrue(val >= 0);
  }

  @Test
  public void getSine_quadrant2() {
    SineCosineCache cache = new SineCosineCache(10);
    double val = cache.getSine(2, 0);
    assertEquals(0.0, val, 1e-10);
  }

  @Test
  public void getSine_quadrant3() {
    SineCosineCache cache = new SineCosineCache(10);
    double val = cache.getSine(3, 0);
    assertTrue(val <= 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getSine_invalidQuadrant_throws() {
    SineCosineCache cache = new SineCosineCache(10);
    cache.getSine(4, 0);
  }

  @Test
  public void getCosine_quadrant0() {
    SineCosineCache cache = new SineCosineCache(10);
    assertEquals(cache.cosines[0], cache.getCosine(0, 0), 1e-10);
  }

  @Test
  public void getCosine_quadrant1_negated() {
    SineCosineCache cache = new SineCosineCache(10);
    double val = cache.getCosine(1, 0);
    assertTrue(val <= 0);
  }

  @Test
  public void getCosine_quadrant2() {
    SineCosineCache cache = new SineCosineCache(10);
    double val = cache.getCosine(2, 0);
    assertEquals(-1.0, val, 1e-10);
  }

  @Test
  public void getCosine_quadrant3() {
    SineCosineCache cache = new SineCosineCache(10);
    double val = cache.getCosine(3, 0);
    assertTrue(val >= 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getCosine_invalidQuadrant_throws() {
    SineCosineCache cache = new SineCosineCache(10);
    cache.getCosine(-1, 0);
  }

  @Test
  public void getAngle_quadrant0_startsAtZero() {
    SineCosineCache cache = new SineCosineCache(10);
    assertEquals(0.0, cache.getAngle(0, 0), 1e-10);
  }

  @Test
  public void getAngle_quadrant1_startsAtPiOver2() {
    SineCosineCache cache = new SineCosineCache(10);
    assertEquals(Math.PI / 2, cache.getAngle(1, 0), 1e-10);
  }

  @Test
  public void getAngle_quadrant2_startsAtPi() {
    SineCosineCache cache = new SineCosineCache(10);
    assertEquals(Math.PI, cache.getAngle(2, 0), 1e-10);
  }

  @Test
  public void getAngle_quadrant3_startsAt3PiOver2() {
    SineCosineCache cache = new SineCosineCache(10);
    assertEquals(3 * Math.PI / 2, cache.getAngle(3, 0), 1e-10);
  }

  @Test
  public void sinSquaredPlusCosSquared_equalsOne() {
    SineCosineCache cache = new SineCosineCache(20);
    for (int i = 0; i < 20; i++) {
      double sinSq = cache.sines[i] * cache.sines[i];
      double cosSq = cache.cosines[i] * cache.cosines[i];
      assertEquals(1.0, sinSq + cosSq, 1e-10);
    }
  }

  @Test
  public void anglesAreIncreasing() {
    SineCosineCache cache = new SineCosineCache(20);
    for (int i = 1; i < 20; i++) {
      assertTrue(cache.angles[i] > cache.angles[i - 1]);
    }
  }
}
