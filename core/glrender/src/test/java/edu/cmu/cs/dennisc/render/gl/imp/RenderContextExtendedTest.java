package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests for {@link RenderContext} — extended coverage of beginAffectorSetup,
 * ambient light accumulation with brightness, fog state, shading state,
 * light ID allocation, and clearRect logic. All tests avoid GL calls.
 */
public class RenderContextExtendedTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  // ── beginAffectorSetup resets ─────────────────────────────────────

  @Test
  public void beginAffectorSetup_resetsNextLightID() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    int id1 = rc.getNextLightID();
    rc.beginAffectorSetup();
    int id2 = rc.getNextLightID();
    assertEquals(id1, id2);
  }

  @Test
  public void beginAffectorSetup_resetsFogEnabled() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    rc.setIsFogEnabled(true);
    rc.beginAffectorSetup();
    assertFalse(rc.isFogEnabled());
  }

  @Test
  public void beginAffectorSetup_resetsAmbientToZero() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    rc.addAmbient(new float[]{1, 1, 1, 1}, 1);
    rc.beginAffectorSetup();
    float[] ambient = new float[4];
    rc.getAmbient(ambient);
    assertEquals(0f, ambient[0], 0.0001f);
    assertEquals(0f, ambient[1], 0.0001f);
    assertEquals(0f, ambient[2], 0.0001f);
    assertEquals(1f, ambient[3], 0.0001f);
  }

  // ── addAmbient with brightness factors ────────────────────────────

  @Test
  public void addAmbient_zeroBrightness_noEffect() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    rc.addAmbient(new float[]{1, 1, 1, 1}, 0.0f);
    float[] ambient = new float[4];
    rc.getAmbient(ambient);
    assertEquals(0f, ambient[0], 0.0001f);
  }

  @Test
  public void addAmbient_halfBrightness() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    rc.addAmbient(new float[]{1, 0.8f, 0.6f, 1}, 0.5f);
    float[] ambient = new float[4];
    rc.getAmbient(ambient);
    assertEquals(0.5f, ambient[0], 0.0001f);
    assertEquals(0.4f, ambient[1], 0.0001f);
    assertEquals(0.3f, ambient[2], 0.0001f);
  }

  @Test
  public void addAmbient_threeAdds_accumulate() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    rc.addAmbient(new float[]{0.1f, 0.2f, 0.3f, 1}, 1.0f);
    rc.addAmbient(new float[]{0.1f, 0.2f, 0.3f, 1}, 1.0f);
    rc.addAmbient(new float[]{0.1f, 0.2f, 0.3f, 1}, 1.0f);
    float[] ambient = new float[4];
    rc.getAmbient(ambient);
    assertEquals(0.3f, ambient[0], 0.0001f);
    assertEquals(0.6f, ambient[1], 0.0001f);
    assertEquals(0.9f, ambient[2], 0.0001f);
  }

  // ── globalBrightness edge cases ───────────────────────────────────

  @Test
  public void setGlobalBrightness_largeValue() {
    RenderContext rc = new RenderContext();
    rc.setGlobalBrightness(10.0f);
    assertEquals(10.0f, rc.getGlobalBrightness(), 0.0001f);
  }

  @Test
  public void setGlobalBrightness_negative() {
    RenderContext rc = new RenderContext();
    rc.setGlobalBrightness(-0.5f);
    assertEquals(-0.5f, rc.getGlobalBrightness(), 0.0001f);
  }

  @Test
  public void setGlobalBrightness_multipleTimes_lastWins() {
    RenderContext rc = new RenderContext();
    rc.setGlobalBrightness(0.1f);
    rc.setGlobalBrightness(0.5f);
    rc.setGlobalBrightness(0.9f);
    assertEquals(0.9f, rc.getGlobalBrightness(), 0.0001f);
  }

  // ── fog enabled / disabled ────────────────────────────────────────

  @Test
  public void fog_toggleMultipleTimes() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    assertFalse(rc.isFogEnabled());
    rc.setIsFogEnabled(true);
    assertTrue(rc.isFogEnabled());
    rc.setIsFogEnabled(false);
    assertFalse(rc.isFogEnabled());
    rc.setIsFogEnabled(true);
    assertTrue(rc.isFogEnabled());
  }

  // ── isShadingEnabled ──────────────────────────────────────────────

  @Test
  public void isShadingEnabled_defaultFalse() {
    RenderContext rc = new RenderContext();
    assertFalse(rc.isShadingEnabled());
  }

  // ── isTextureEnabled after clear ──────────────────────────────────

  @Test
  public void isTextureEnabled_afterBeginAffectorSetup_false() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    assertFalse(rc.isTextureEnabled());
  }

  @Test
  public void clearDiffuseColorTextureAdapter_isTextureEnabledFalse() {
    RenderContext rc = new RenderContext();
    rc.clearDiffuseColorTextureAdapter();
    assertFalse(rc.isTextureEnabled());
  }

  // ── getNextLightID: many allocations ──────────────────────────────

  @Test
  public void getNextLightID_fiveConsecutive_sequential() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    int base = rc.getNextLightID();
    for (int i = 1; i < 5; i++) {
      assertEquals(base + i, rc.getNextLightID());
    }
  }

  // ── getURatio/getVRatio after clear ───────────────────────────────

  @Test
  public void getURatio_afterClear_returnsNaN() {
    RenderContext rc = new RenderContext();
    rc.clearDiffuseColorTextureAdapter();
    assertTrue(Float.isNaN(rc.getURatio()));
  }

  @Test
  public void getVRatio_afterClear_returnsNaN() {
    RenderContext rc = new RenderContext();
    rc.clearDiffuseColorTextureAdapter();
    assertTrue(Float.isNaN(rc.getVRatio()));
  }

  // ── Opacity stack: deep nesting ───────────────────────────────────

  @Test
  public void opacityStack_deepNesting() throws Exception {
    RenderContext rc = new RenderContext();
    float[] expected = new float[10];
    for (int i = 0; i < 10; i++) {
      rc.pushGlobalOpacity();
      rc.multiplyGlobalOpacity(0.9f);
      expected[i] = getOpacity(rc);
    }
    for (int i = 9; i >= 0; i--) {
      rc.popGlobalOpacity();
    }
    assertEquals(1.0f, getOpacity(rc), 0.0001f);
  }

  @Test
  public void opacityStack_nestedMultiply_accumulatesCorrectly() throws Exception {
    RenderContext rc = new RenderContext();
    rc.pushGlobalOpacity();
    rc.multiplyGlobalOpacity(0.5f);
    assertEquals(0.5f, getOpacity(rc), 0.0001f);
    rc.pushGlobalOpacity();
    rc.multiplyGlobalOpacity(0.3f);
    assertEquals(0.15f, getOpacity(rc), 0.0001f);
    rc.popGlobalOpacity();
    assertEquals(0.5f, getOpacity(rc), 0.0001f);
    rc.popGlobalOpacity();
    assertEquals(1.0f, getOpacity(rc), 0.0001f);
  }

  @Test
  public void opacityStack_multiplyByNegative() throws Exception {
    RenderContext rc = new RenderContext();
    rc.multiplyGlobalOpacity(-1.0f);
    assertEquals(-1.0f, getOpacity(rc), 0.0001f);
  }

  @Test
  public void opacityStack_pushPopEmpty() throws Exception {
    RenderContext rc = new RenderContext();
    rc.pushGlobalOpacity();
    rc.popGlobalOpacity();
    assertEquals(1.0f, getOpacity(rc), 0.0001f);
  }

  // ── isLightingEnabled always true for RenderContext ────────────────

  @Test
  public void isLightingEnabled_alwaysTrue() {
    RenderContext rc = new RenderContext();
    assertTrue(rc.isLightingEnabled());
  }

  // ── glu accessor ──────────────────────────────────────────────────

  @Test
  public void glu_isNotNull() {
    RenderContext rc = new RenderContext();
    assertNotNull(rc.glu);
  }

  // ── Reflection helper ─────────────────────────────────────────────

  private static float getOpacity(RenderContext rc) throws Exception {
    Field f = RenderContext.class.getDeclaredField("globalOpacity");
    f.setAccessible(true);
    return f.getFloat(rc);
  }
}
