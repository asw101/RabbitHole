package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link PickContext} constants and z-value conversion math
 * used in {@link edu.cmu.cs.dennisc.system.graphics.ConformanceTestResults}.
 * These test the pure math without requiring GL context.
 */
public class PickContextConstantsTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  // ── MAX_UNSIGNED_INTEGER ──────────────────────────────────────────

  @Test
  public void maxUnsignedInteger_isCorrect() {
    assertEquals(0xFFFFFFFFL, PickContext.MAX_UNSIGNED_INTEGER);
  }

  @Test
  public void maxUnsignedInteger_is4294967295() {
    assertEquals(4294967295L, PickContext.MAX_UNSIGNED_INTEGER);
  }

  @Test
  public void maxUnsignedInteger_fitsInLong() {
    assertTrue(PickContext.MAX_UNSIGNED_INTEGER > 0);
    assertTrue(PickContext.MAX_UNSIGNED_INTEGER < Long.MAX_VALUE);
  }

  // ── Z-value conversion: int → long (masking) ─────────────────────

  @Test
  public void zValueConversion_positiveInt_noChange() {
    int zAsInt = 0x7FFFFFFF;
    long zAsLong = zAsInt & PickContext.MAX_UNSIGNED_INTEGER;
    assertEquals(0x7FFFFFFFL, zAsLong);
  }

  @Test
  public void zValueConversion_negativeInt_maskedToUnsigned() {
    int zAsInt = -1; // 0xFFFFFFFF in two's complement
    long zAsLong = zAsInt & PickContext.MAX_UNSIGNED_INTEGER;
    assertEquals(PickContext.MAX_UNSIGNED_INTEGER, zAsLong);
  }

  @Test
  public void zValueConversion_zero_staysZero() {
    int zAsInt = 0;
    long zAsLong = zAsInt & PickContext.MAX_UNSIGNED_INTEGER;
    assertEquals(0L, zAsLong);
  }

  @Test
  public void zValueConversion_one_staysOne() {
    int zAsInt = 1;
    long zAsLong = zAsInt & PickContext.MAX_UNSIGNED_INTEGER;
    assertEquals(1L, zAsLong);
  }

  @Test
  public void zValueConversion_minInt_maskedCorrectly() {
    int zAsInt = Integer.MIN_VALUE; // 0x80000000
    long zAsLong = zAsInt & PickContext.MAX_UNSIGNED_INTEGER;
    assertEquals(0x80000000L, zAsLong);
  }

  // ── Z-value to float conversion ───────────────────────────────────

  @Test
  public void zValueToFloat_maxUnsigned_producesOne() {
    long zAsLong = PickContext.MAX_UNSIGNED_INTEGER;
    float zFloat = (float) zAsLong / (float) PickContext.MAX_UNSIGNED_INTEGER;
    assertEquals(1.0f, zFloat, 0.0001f);
  }

  @Test
  public void zValueToFloat_zero_producesZero() {
    long zAsLong = 0L;
    float zFloat = (float) zAsLong / (float) PickContext.MAX_UNSIGNED_INTEGER;
    assertEquals(0.0f, zFloat, 0.0001f);
  }

  @Test
  public void zValueToFloat_halfMax_producesHalf() {
    long zAsLong = PickContext.MAX_UNSIGNED_INTEGER / 2;
    float zFloat = (float) zAsLong / (float) PickContext.MAX_UNSIGNED_INTEGER;
    assertEquals(0.5f, zFloat, 0.001f);
  }

  @Test
  public void zValueToFloat_quarterMax_producesQuarter() {
    long zAsLong = PickContext.MAX_UNSIGNED_INTEGER / 4;
    float zFloat = (float) zAsLong / (float) PickContext.MAX_UNSIGNED_INTEGER;
    assertEquals(0.25f, zFloat, 0.001f);
  }

  @Test
  public void zValueToFloat_inRange_0_1() {
    for (int i = 0; i <= 100; i++) {
      long zAsLong = (PickContext.MAX_UNSIGNED_INTEGER * i) / 100;
      float zFloat = (float) zAsLong / (float) PickContext.MAX_UNSIGNED_INTEGER;
      assertTrue("Z value should be >= 0", zFloat >= 0.0f);
      assertTrue("Z value should be <= 1", zFloat <= 1.0f);
    }
  }

  // ── FISHY_PICK_VALUE ──────────────────────────────────────────────

  @Test
  public void fishyPickValue_isHalfMaxPlusOne() {
    long fishy = (PickContext.MAX_UNSIGNED_INTEGER / 2) + 1;
    assertEquals(0x80000000L, fishy);
  }

  @Test
  public void fishyPickValue_toFloat_isAboutHalf() {
    long fishy = (PickContext.MAX_UNSIGNED_INTEGER / 2) + 1;
    float zFloat = (float) fishy / (float) PickContext.MAX_UNSIGNED_INTEGER;
    assertEquals(0.5f, zFloat, 0.001f);
  }

  // ── PickContext construction ───────────────────────────────────────

  @Test
  public void constructor_synchronous_noException() {
    PickContext pc = new PickContext(true);
    assertNotNull(pc);
  }

  @Test
  public void constructor_asynchronous_noException() {
    PickContext pc = new PickContext(false);
    assertNotNull(pc);
  }

  // ── isTextureEnabled / isLightingEnabled ──────────────────────────

  @Test
  public void isTextureEnabled_alwaysFalse() {
    PickContext pc = new PickContext(true);
    assertFalse(pc.isTextureEnabled());
  }

  @Test
  public void isLightingEnabled_alwaysFalse() {
    PickContext pc = new PickContext(true);
    assertFalse(pc.isLightingEnabled());
  }

  // ── scaled count (inherited from Context) ─────────────────────────

  @Test
  public void isScaled_initiallyFalse() {
    PickContext pc = new PickContext(true);
    assertFalse(pc.isScaled());
  }

  @Test
  public void incrementScaledCount_setsScaledTrue() {
    PickContext pc = new PickContext(true);
    pc.incrementScaledCount();
    assertTrue(pc.isScaled());
  }

  @Test
  public void decrementScaledCount_setsScaledFalse() {
    PickContext pc = new PickContext(true);
    pc.incrementScaledCount();
    pc.decrementScaledCount();
    assertFalse(pc.isScaled());
  }

  @Test
  public void multipleIncrements_decrementsToZero() {
    PickContext pc = new PickContext(true);
    pc.incrementScaledCount();
    pc.incrementScaledCount();
    pc.incrementScaledCount();
    assertTrue(pc.isScaled());
    pc.decrementScaledCount();
    assertTrue(pc.isScaled());
    pc.decrementScaledCount();
    assertTrue(pc.isScaled());
    pc.decrementScaledCount();
    assertFalse(pc.isScaled());
  }

  @Test
  public void pushAndPopScaledCount() {
    PickContext pc = new PickContext(true);
    pc.incrementScaledCount();
    assertTrue(pc.isScaled());
    pc.pushScaledCountAndSetToZero();
    assertFalse(pc.isScaled());
    pc.popAndRestoreScaledCount();
    assertTrue(pc.isScaled());
  }

  @Test
  public void nestedPushPopScaledCount() {
    PickContext pc = new PickContext(true);
    pc.incrementScaledCount();
    pc.incrementScaledCount();
    pc.pushScaledCountAndSetToZero();
    assertFalse(pc.isScaled());
    pc.incrementScaledCount();
    assertTrue(pc.isScaled());
    pc.pushScaledCountAndSetToZero();
    assertFalse(pc.isScaled());
    pc.popAndRestoreScaledCount();
    assertTrue(pc.isScaled());
    pc.popAndRestoreScaledCount();
    assertTrue(pc.isScaled());
  }

  // ── getPickNameForVisualAdapter ───────────────────────────────────

  @Test
  public void getPickNameForVisualAdapter_sequentialNames() {
    PickContext pc = new PickContext(true);
    int name0 = pc.getPickNameForVisualAdapter(null);
    int name1 = pc.getPickNameForVisualAdapter(null);
    int name2 = pc.getPickNameForVisualAdapter(null);
    assertEquals(0, name0);
    assertEquals(1, name1);
    assertEquals(2, name2);
  }

  @Test
  public void getPickVisualAdapterForName_null_returnsNull() {
    PickContext pc = new PickContext(true);
    pc.getPickNameForVisualAdapter(null);
    assertNull(pc.getPickVisualAdapterForName(0));
  }

  @Test
  public void getPickVisualAdapterForName_unknownName_returnsNull() {
    PickContext pc = new PickContext(true);
    assertNull(pc.getPickVisualAdapterForName(999));
  }
}
