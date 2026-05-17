package edu.cmu.cs.dennisc.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SineCosineCacheTest {

  static final double EPSILON = 1e-10;
  static final int LENGTH = 10;
  final SineCosineCache cache = new SineCosineCache(LENGTH);

  // --- Constructor ---

  @Test
  void constructor_initializesArraysWithCorrectLength() {
    assertEquals(LENGTH, cache.cosines.length);
    assertEquals(LENGTH, cache.sines.length);
    assertEquals(LENGTH, cache.angles.length);
  }

  @Test
  void constructor_firstAngleIsZero() {
    assertEquals(0.0, cache.angles[0], EPSILON);
  }

  @Test
  void constructor_firstCosineIsOne() {
    assertEquals(1.0, cache.cosines[0], EPSILON);
  }

  @Test
  void constructor_firstSineIsZero() {
    assertEquals(0.0, cache.sines[0], EPSILON);
  }

  @Test
  void constructor_anglesIncrease() {
    for (int i = 1; i < LENGTH; i++) {
      assertTrue(cache.angles[i] > cache.angles[i - 1]);
    }
  }

  @Test
  void constructor_anglesSpanQuarterCircle() {
    double expectedStep = (Math.PI / 2.0) / LENGTH;
    double lastExpectedAngle = expectedStep * (LENGTH - 1);
    assertEquals(lastExpectedAngle, cache.angles[LENGTH - 1], EPSILON);
  }

  // --- getSine quadrant 0 ---

  @Test
  void getSine_quadrant0_returnsDirectValue() {
    for (int i = 0; i < LENGTH; i++) {
      assertEquals(cache.sines[i], cache.getSine(0, i), EPSILON);
    }
  }

  // --- getSine quadrant 1 ---

  @Test
  void getSine_quadrant1_returnsMirroredValue() {
    int max = LENGTH - 1;
    for (int i = 0; i < LENGTH; i++) {
      assertEquals(cache.sines[max - i], cache.getSine(1, i), EPSILON);
    }
  }

  // --- getSine quadrant 2 ---

  @Test
  void getSine_quadrant2_returnsNegatedValue() {
    for (int i = 0; i < LENGTH; i++) {
      assertEquals(-cache.sines[i], cache.getSine(2, i), EPSILON);
    }
  }

  // --- getSine quadrant 3 ---

  @Test
  void getSine_quadrant3_returnsNegatedMirroredValue() {
    int max = LENGTH - 1;
    for (int i = 0; i < LENGTH; i++) {
      assertEquals(-cache.sines[max - i], cache.getSine(3, i), EPSILON);
    }
  }

  // --- getCosine quadrant 0 ---

  @Test
  void getCosine_quadrant0_returnsDirectValue() {
    for (int i = 0; i < LENGTH; i++) {
      assertEquals(cache.cosines[i], cache.getCosine(0, i), EPSILON);
    }
  }

  // --- getCosine quadrant 1 ---

  @Test
  void getCosine_quadrant1_returnsNegatedMirroredValue() {
    int max = LENGTH - 1;
    for (int i = 0; i < LENGTH; i++) {
      assertEquals(-cache.cosines[max - i], cache.getCosine(1, i), EPSILON);
    }
  }

  // --- getCosine quadrant 2 ---

  @Test
  void getCosine_quadrant2_returnsNegatedValue() {
    for (int i = 0; i < LENGTH; i++) {
      assertEquals(-cache.cosines[i], cache.getCosine(2, i), EPSILON);
    }
  }

  // --- getCosine quadrant 3 ---

  @Test
  void getCosine_quadrant3_returnsMirroredValue() {
    int max = LENGTH - 1;
    for (int i = 0; i < LENGTH; i++) {
      assertEquals(cache.cosines[max - i], cache.getCosine(3, i), EPSILON);
    }
  }

  // --- getSine invalid quadrant ---

  @Test
  void getSine_invalidQuadrant_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> cache.getSine(4, 0));
    assertThrows(IllegalArgumentException.class, () -> cache.getSine(-1, 0));
  }

  // --- getCosine invalid quadrant ---

  @Test
  void getCosine_invalidQuadrant_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> cache.getCosine(4, 0));
    assertThrows(IllegalArgumentException.class, () -> cache.getCosine(-1, 0));
  }

  // --- getAngle ---

  @Test
  void getAngle_quadrant0_matchesStoredAngle() {
    for (int i = 0; i < LENGTH; i++) {
      assertEquals(cache.angles[i], cache.getAngle(0, i), EPSILON);
    }
  }

  @Test
  void getAngle_quadrant1_offsetByHalfPi() {
    for (int i = 0; i < LENGTH; i++) {
      assertEquals((Math.PI * 0.5) + cache.angles[i], cache.getAngle(1, i), EPSILON);
    }
  }

  @Test
  void getAngle_quadrant2_offsetByPi() {
    for (int i = 0; i < LENGTH; i++) {
      assertEquals(Math.PI + cache.angles[i], cache.getAngle(2, i), EPSILON);
    }
  }

  @Test
  void getAngle_quadrant3_offsetBy3HalfPi() {
    for (int i = 0; i < LENGTH; i++) {
      assertEquals((Math.PI * 1.5) + cache.angles[i], cache.getAngle(3, i), EPSILON);
    }
  }

  // --- Pythagorean identity ---

  @Test
  void sinSquaredPlusCosSquared_equalsOne() {
    for (int i = 0; i < LENGTH; i++) {
      double sinVal = cache.sines[i];
      double cosVal = cache.cosines[i];
      assertEquals(1.0, sinVal * sinVal + cosVal * cosVal, EPSILON);
    }
  }

  // --- Cache with length 1 ---

  @Test
  void constructor_lengthOne_singleElement() {
    SineCosineCache small = new SineCosineCache(1);
    assertEquals(1, small.cosines.length);
    assertEquals(0.0, small.angles[0], EPSILON);
    assertEquals(1.0, small.cosines[0], EPSILON);
    assertEquals(0.0, small.sines[0], EPSILON);
  }
}
