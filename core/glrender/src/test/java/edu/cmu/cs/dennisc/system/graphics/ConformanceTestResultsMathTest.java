package edu.cmu.cs.dennisc.system.graphics;

import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for z-value conversion math used in {@link ConformanceTestResults.PickDetails}.
 * These are pure math operations that don't require a GL context.
 * Tests the convertZValueToLong and convertZValueToFloat algorithms
 * replicated here from the private methods.
 */
public class ConformanceTestResultsMathTest {

  // ── convertZValueToLong (int → unsigned long via mask) ────────────

  @Test
  public void convertZValueToLong_zero() {
    assertEquals(0L, convertZValueToLong(0));
  }

  @Test
  public void convertZValueToLong_one() {
    assertEquals(1L, convertZValueToLong(1));
  }

  @Test
  public void convertZValueToLong_maxPositiveInt() {
    assertEquals(0x7FFFFFFFL, convertZValueToLong(Integer.MAX_VALUE));
  }

  @Test
  public void convertZValueToLong_negativeOne() {
    assertEquals(PickContext.MAX_UNSIGNED_INTEGER, convertZValueToLong(-1));
  }

  @Test
  public void convertZValueToLong_minInt() {
    assertEquals(0x80000000L, convertZValueToLong(Integer.MIN_VALUE));
  }

  @Test
  public void convertZValueToLong_alwaysNonNegative() {
    for (int v : new int[]{-1, -100, Integer.MIN_VALUE, 0, 1, Integer.MAX_VALUE}) {
      long result = convertZValueToLong(v);
      assertTrue("Result should be non-negative for input " + v, result >= 0);
    }
  }

  @Test
  public void convertZValueToLong_alwaysWithinRange() {
    for (int v : new int[]{-1, 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE}) {
      long result = convertZValueToLong(v);
      assertTrue("Result should be <= MAX_UNSIGNED_INTEGER", result <= PickContext.MAX_UNSIGNED_INTEGER);
    }
  }

  // ── convertZValueToFloat (long → normalized 0..1) ─────────────────

  @Test
  public void convertZValueToFloat_zero() {
    assertEquals(0.0f, convertZValueToFloat(0L), 0.0001f);
  }

  @Test
  public void convertZValueToFloat_maxUnsigned() {
    assertEquals(1.0f, convertZValueToFloat(PickContext.MAX_UNSIGNED_INTEGER), 0.0001f);
  }

  @Test
  public void convertZValueToFloat_halfMax() {
    long half = PickContext.MAX_UNSIGNED_INTEGER / 2;
    float result = convertZValueToFloat(half);
    assertEquals(0.5f, result, 0.001f);
  }

  @Test
  public void convertZValueToFloat_quarterMax() {
    long quarter = PickContext.MAX_UNSIGNED_INTEGER / 4;
    float result = convertZValueToFloat(quarter);
    assertEquals(0.25f, result, 0.001f);
  }

  @Test
  public void convertZValueToFloat_alwaysInRange() {
    long[] values = {0, 1, 100, 1000, PickContext.MAX_UNSIGNED_INTEGER / 4,
        PickContext.MAX_UNSIGNED_INTEGER / 2, PickContext.MAX_UNSIGNED_INTEGER};
    for (long v : values) {
      float result = convertZValueToFloat(v);
      assertTrue("Float should be >= 0", result >= 0.0f);
      assertTrue("Float should be <= 1", result <= 1.0f);
    }
  }

  // ── Round-trip: int → long → float ────────────────────────────────

  @Test
  public void roundTrip_zero() {
    int zAsInt = 0;
    long zAsLong = convertZValueToLong(zAsInt);
    float zFloat = convertZValueToFloat(zAsLong);
    assertEquals(0.0f, zFloat, 0.0001f);
  }

  @Test
  public void roundTrip_negativeOne_producesOne() {
    int zAsInt = -1;
    long zAsLong = convertZValueToLong(zAsInt);
    float zFloat = convertZValueToFloat(zAsLong);
    assertEquals(1.0f, zFloat, 0.0001f);
  }

  @Test
  public void roundTrip_positiveInt_producesExpected() {
    int zAsInt = 0x3FFFFFFF; // ~1/4 of range
    long zAsLong = convertZValueToLong(zAsInt);
    float zFloat = convertZValueToFloat(zAsLong);
    assertEquals(0.25f, zFloat, 0.01f);
  }

  // ── FISHY_PICK_VALUE ──────────────────────────────────────────────

  @Test
  public void fishyPickValue_computation() {
    long fishy = (PickContext.MAX_UNSIGNED_INTEGER / 2) + 1;
    assertEquals(0x80000000L, fishy);
  }

  @Test
  public void fishyPickValue_toFloat() {
    long fishy = (PickContext.MAX_UNSIGNED_INTEGER / 2) + 1;
    float result = convertZValueToFloat(fishy);
    assertTrue(result > 0.49f && result < 0.51f);
  }

  @Test
  public void fishyPickValue_isNotMaxUnsigned() {
    long fishy = (PickContext.MAX_UNSIGNED_INTEGER / 2) + 1;
    assertNotEquals(PickContext.MAX_UNSIGNED_INTEGER, fishy);
  }

  @Test
  public void fishyPickValue_isNotZero() {
    long fishy = (PickContext.MAX_UNSIGNED_INTEGER / 2) + 1;
    assertNotEquals(0L, fishy);
  }

  // ── ConformanceTestResults.SINGLETON ──────────────────────────────

  @Test
  public void singleton_isNotNull() {
    assertNotNull(ConformanceTestResults.SINGLETON);
  }

  @Test
  public void singleton_sharedDetails_initiallyNull() {
    assertNull(ConformanceTestResults.SINGLETON.getSharedDetails());
  }

  @Test
  public void singleton_synchronousPickDetails_initiallyNull() {
    assertNull(ConformanceTestResults.SINGLETON.getSynchronousPickDetails());
  }

  @Test
  public void singleton_asynchronousPickDetails_initiallyNull() {
    assertNull(ConformanceTestResults.SINGLETON.getAsynchronousPickDetails());
  }

  // ── Helper methods replicating private logic ──────────────────────

  private static long convertZValueToLong(int zValue) {
    long rv = zValue;
    rv &= PickContext.MAX_UNSIGNED_INTEGER;
    return rv;
  }

  private static float convertZValueToFloat(long zValue) {
    float zFront = (float) zValue;
    zFront /= (float) PickContext.MAX_UNSIGNED_INTEGER;
    return zFront;
  }
}
