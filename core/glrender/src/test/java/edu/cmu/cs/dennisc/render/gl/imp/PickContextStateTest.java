package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link PickContext} — pick name map management, synchronous/async
 * mode, texture and lighting enabled states, MAX_UNSIGNED_INTEGER constant,
 * and initialize behavior.
 */
import org.junit.Assume;
import java.awt.GraphicsEnvironment;
public class PickContextStateTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  // ── MAX_UNSIGNED_INTEGER constant ─────────────────────────────────

  @Test
  public void maxUnsignedInteger_isCorrect() {
    assertEquals(0xFFFFFFFFL, PickContext.MAX_UNSIGNED_INTEGER);
  }

  @Test
  public void maxUnsignedInteger_isPositive() {
    assertTrue(PickContext.MAX_UNSIGNED_INTEGER > 0);
  }

  @Test
  public void maxUnsignedInteger_fitsInLong() {
    assertTrue(PickContext.MAX_UNSIGNED_INTEGER < Long.MAX_VALUE);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructor_synchronous_noException() {
    PickContext ctx = new PickContext(true);
    assertNotNull(ctx);
  }

  @Test
  public void constructor_asynchronous_noException() {
    PickContext ctx = new PickContext(false);
    assertNotNull(ctx);
  }

  @Test
  public void isSynchronous_true_storedCorrectly() throws Exception {
    PickContext ctx = new PickContext(true);
    Field f = PickContext.class.getDeclaredField("isSynchronous");
    f.setAccessible(true);
    assertTrue(f.getBoolean(ctx));
  }

  @Test
  public void isSynchronous_false_storedCorrectly() throws Exception {
    PickContext ctx = new PickContext(false);
    Field f = PickContext.class.getDeclaredField("isSynchronous");
    f.setAccessible(true);
    assertFalse(f.getBoolean(ctx));
  }

  // ── isTextureEnabled ──────────────────────────────────────────────

  @Test
  public void isTextureEnabled_alwaysFalse() {
    PickContext ctx = new PickContext(true);
    assertFalse(ctx.isTextureEnabled());
  }

  @Test
  public void isTextureEnabled_asyncAlsoFalse() {
    PickContext ctx = new PickContext(false);
    assertFalse(ctx.isTextureEnabled());
  }

  // ── isLightingEnabled ─────────────────────────────────────────────

  @Test
  public void isLightingEnabled_alwaysFalse() {
    PickContext ctx = new PickContext(true);
    assertFalse(ctx.isLightingEnabled());
  }

  @Test
  public void isLightingEnabled_asyncAlsoFalse() {
    PickContext ctx = new PickContext(false);
    assertFalse(ctx.isLightingEnabled());
  }

  // ── getPickNameForVisualAdapter ───────────────────────────────────

  @Test
  public void getPickName_firstCall_returnsZero() {
    PickContext ctx = new PickContext(true);
    int name = ctx.getPickNameForVisualAdapter(null);
    assertEquals(0, name);
  }

  @Test
  public void getPickName_secondCall_returnsOne() {
    PickContext ctx = new PickContext(true);
    ctx.getPickNameForVisualAdapter(null);
    int name = ctx.getPickNameForVisualAdapter(null);
    assertEquals(1, name);
  }

  @Test
  public void getPickName_incrementsSequentially() {
    PickContext ctx = new PickContext(true);
    for (int i = 0; i < 10; i++) {
      assertEquals(i, ctx.getPickNameForVisualAdapter(null));
    }
  }

  // ── getPickVisualAdapterForName ───────────────────────────────────

  @Test
  public void getPickVisualAdapter_unknownName_returnsNull() {
    PickContext ctx = new PickContext(true);
    assertNull(ctx.getPickVisualAdapterForName(999));
  }

  @Test
  public void getPickVisualAdapter_registeredNull_returnsNull() {
    PickContext ctx = new PickContext(true);
    int name = ctx.getPickNameForVisualAdapter(null);
    assertNull(ctx.getPickVisualAdapterForName(name));
  }

  // ── pickNameMap via reflection ────────────────────────────────────

  @Test
  @SuppressWarnings("unchecked")
  public void pickNameMap_initiallyEmpty() throws Exception {
    PickContext ctx = new PickContext(true);
    Field f = PickContext.class.getDeclaredField("m_pickNameMap");
    f.setAccessible(true);
    Map<?, ?> map = (Map<?, ?>) f.get(ctx);
    assertTrue(map.isEmpty());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void pickNameMap_growsWithRegistrations() throws Exception {
    PickContext ctx = new PickContext(true);
    ctx.getPickNameForVisualAdapter(null);
    ctx.getPickNameForVisualAdapter(null);
    ctx.getPickNameForVisualAdapter(null);
    Field f = PickContext.class.getDeclaredField("m_pickNameMap");
    f.setAccessible(true);
    Map<?, ?> map = (Map<?, ?>) f.get(ctx);
    assertEquals(3, map.size());
  }

  // ── enableNormalize / disableNormalize are no-ops ─────────────────

  @Test
  public void enableNormalize_noException() {
    PickContext ctx = new PickContext(true);
    ctx.enableNormalize(); // should be no-op
  }

  @Test
  public void disableNormalize_noException() {
    PickContext ctx = new PickContext(true);
    ctx.disableNormalize(); // should be no-op
  }

  // ── initialize resets scaled count ────────────────────────────────

  @Test
  public void initialize_resetsScaledCount() {
    PickContext ctx = new PickContext(true);
    ctx.incrementScaledCount();
    assertTrue(ctx.isScaled());
    ctx.initialize();
    assertFalse(ctx.isScaled());
  }

  // ── gl field ──────────────────────────────────────────────────────

  @Test
  public void gl_initiallyNull() {
    PickContext ctx = new PickContext(true);
    assertNull(ctx.gl);
  }

  @Test
  public void setGL_null_noException() {
    PickContext ctx = new PickContext(true);
    ctx.setGL(null);
    assertNull(ctx.gl);
  }

  // ── glu accessor ─────────────────────────────────────────────────

  @Test
  public void glu_isNotNull() {
    PickContext ctx = new PickContext(true);
    assertNotNull(ctx.glu);
  }

  // ── getConformanceTestResultsPickDetails ──────────────────────────

  @Test
  public void conformancePickDetails_synchronous_initiallyNull() {
    PickContext ctx = new PickContext(true);
    assertNull(ctx.getConformanceTestResultsPickDetails());
  }

  @Test
  public void conformancePickDetails_asynchronous_initiallyNull() {
    PickContext ctx = new PickContext(false);
    assertNull(ctx.getConformanceTestResultsPickDetails());
  }
}
