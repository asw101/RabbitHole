package edu.cmu.cs.dennisc.math;

import org.junit.Test;

import static org.junit.Assert.*;

public class SineCosineCacheDeepTest {

  @Test
  public void constructor_createsArraysOfCorrectLength() {
    SineCosineCache cache = new SineCosineCache(10);

    assertEquals(10, cache.cosines.length);
    assertEquals(10, cache.sines.length);
    assertEquals(10, cache.angles.length);
  }

  @Test
  public void dTheta_defaultsToZero() {
    assertEquals(0.0, new SineCosineCache(10).dTheta, 1e-10);
  }

  @Test
  public void cosines0_isOne() {
    assertEquals(1.0, new SineCosineCache(10).cosines[0], 1e-10);
  }

  @Test
  public void sines0_isZero() {
    assertEquals(0.0, new SineCosineCache(10).sines[0], 1e-10);
  }

  @Test
  public void angles0_isZero() {
    assertEquals(0.0, new SineCosineCache(10).angles[0], 1e-10);
  }

  @Test
  public void constructor_lengthOne_hasSingleZeroAngle() {
    SineCosineCache cache = new SineCosineCache(1);

    assertEquals(1, cache.angles.length);
    assertEquals(0.0, cache.angles[0], 1e-10);
    assertEquals(1.0, cache.cosines[0], 1e-10);
    assertEquals(0.0, cache.sines[0], 1e-10);
  }

  @Test
  public void getSine_quadrant0() {
    SineCosineCache cache = new SineCosineCache(10);

    assertEquals(0.0, cache.getSine(0, 0), 1e-10);
    assertTrue(cache.getSine(0, 5) > 0);
  }

  @Test
  public void getCosine_quadrant0() {
    assertEquals(1.0, new SineCosineCache(10).getCosine(0, 0), 1e-10);
  }

  @Test
  public void getSine_quadrant1_reflection() {
    SineCosineCache cache = new SineCosineCache(10);

    assertEquals(cache.sines[cache.sines.length - 1 - 3], cache.getSine(1, 3), 1e-10);
  }

  @Test
  public void getCosine_quadrant1_negative() {
    SineCosineCache cache = new SineCosineCache(10);

    assertEquals(-cache.cosines[cache.cosines.length - 1], cache.getCosine(1, 0), 1e-10);
  }

  @Test
  public void getSine_quadrant2_negative() {
    SineCosineCache cache = new SineCosineCache(10);

    assertEquals(-cache.sines[3], cache.getSine(2, 3), 1e-10);
  }

  @Test
  public void getCosine_quadrant2_negative() {
    SineCosineCache cache = new SineCosineCache(10);

    assertEquals(-cache.cosines[3], cache.getCosine(2, 3), 1e-10);
  }

  @Test
  public void getSine_quadrant3_negativeReflection() {
    SineCosineCache cache = new SineCosineCache(10);

    assertEquals(-cache.sines[cache.sines.length - 1 - 3], cache.getSine(3, 3), 1e-10);
  }

  @Test
  public void getCosine_quadrant3_positiveReflection() {
    SineCosineCache cache = new SineCosineCache(10);

    assertEquals(cache.cosines[cache.cosines.length - 1 - 3], cache.getCosine(3, 3), 1e-10);
  }

  @Test
  public void getAngle_quadrant0() {
    SineCosineCache cache = new SineCosineCache(10);

    assertEquals(cache.angles[0], cache.getAngle(0, 0), 1e-10);
    assertEquals(cache.angles[5], cache.getAngle(0, 5), 1e-10);
  }

  @Test
  public void getAngle_quadrant1() {
    SineCosineCache cache = new SineCosineCache(10);

    assertEquals(Math.PI / 2.0 + cache.angles[0], cache.getAngle(1, 0), 1e-10);
  }

  @Test
  public void getAngle_quadrant2() {
    SineCosineCache cache = new SineCosineCache(10);

    assertEquals(Math.PI + cache.angles[0], cache.getAngle(2, 0), 1e-10);
  }

  @Test
  public void getAngle_quadrant3() {
    SineCosineCache cache = new SineCosineCache(10);

    assertEquals(1.5 * Math.PI + cache.angles[0], cache.getAngle(3, 0), 1e-10);
  }

  @Test
  public void getAngle_sameIndexIncreasesAcrossQuadrants() {
    SineCosineCache cache = new SineCosineCache(10);

    double q0 = cache.getAngle(0, 2);
    double q1 = cache.getAngle(1, 2);
    double q2 = cache.getAngle(2, 2);
    double q3 = cache.getAngle(3, 2);

    assertTrue(q0 < q1);
    assertTrue(q1 < q2);
    assertTrue(q2 < q3);
  }

  @Test
  public void getCosine_quadrant0_lastIndexMatchesCachedValue() {
    SineCosineCache cache = new SineCosineCache(10);

    assertEquals(cache.cosines[cache.cosines.length - 1], cache.getCosine(0, cache.cosines.length - 1), 1e-10);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getSine_invalidQuadrant_throws() {
    new SineCosineCache(10).getSine(5, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getCosine_invalidQuadrant_throws() {
    new SineCosineCache(10).getCosine(5, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getSine_negativeQuadrant_throws() {
    new SineCosineCache(10).getSine(-1, 0);
  }

  @Test
  public void anglesAreMonotonicallyIncreasing() {
    SineCosineCache cache = new SineCosineCache(20);

    for (int i = 1; i < 20; i++) {
      assertTrue(cache.angles[i] > cache.angles[i - 1]);
    }
  }

  @Test
  public void sinesAreMonotonicallyIncreasing() {
    SineCosineCache cache = new SineCosineCache(20);

    for (int i = 1; i < 20; i++) {
      assertTrue(cache.sines[i] >= cache.sines[i - 1]);
    }
  }

  @Test
  public void cosinesAreMonotonicallyDecreasing() {
    SineCosineCache cache = new SineCosineCache(20);

    for (int i = 1; i < 20; i++) {
      assertTrue(cache.cosines[i] <= cache.cosines[i - 1]);
    }
  }
}
