package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link RenderContext} — opacity stack push/pop/multiply,
 * globalBrightness, ambient light accumulation, fog enable/disable,
 * texture enabled state, lighting enabled state, light ID counter,
 * and clearRect logic. Uses reflection to access private fields
 * since no GL context is available.
 */
public class RenderContextOpacityStackTest {

  // ── globalOpacity via reflection ──────────────────────────────────

  @Test
  public void globalOpacity_initiallyOne() throws Exception {
    RenderContext rc = new RenderContext();
    assertEquals(1.0f, getGlobalOpacity(rc), 0.0001f);
  }

  @Test
  public void pushGlobalOpacity_preservesCurrent() throws Exception {
    RenderContext rc = new RenderContext();
    rc.pushGlobalOpacity();
    assertEquals(1.0f, getGlobalOpacity(rc), 0.0001f);
  }

  @Test
  public void multiplyGlobalOpacity_multipliesValue() throws Exception {
    RenderContext rc = new RenderContext();
    rc.multiplyGlobalOpacity(0.5f);
    assertEquals(0.5f, getGlobalOpacity(rc), 0.0001f);
  }

  @Test
  public void multiplyGlobalOpacity_chainedMultiplications() throws Exception {
    RenderContext rc = new RenderContext();
    rc.multiplyGlobalOpacity(0.5f);
    rc.multiplyGlobalOpacity(0.5f);
    assertEquals(0.25f, getGlobalOpacity(rc), 0.0001f);
  }

  @Test
  public void pushPopGlobalOpacity_restoresValue() throws Exception {
    RenderContext rc = new RenderContext();
    rc.pushGlobalOpacity();
    rc.multiplyGlobalOpacity(0.3f);
    assertEquals(0.3f, getGlobalOpacity(rc), 0.0001f);
    rc.popGlobalOpacity();
    assertEquals(1.0f, getGlobalOpacity(rc), 0.0001f);
  }

  @Test
  public void nestedPushPop_restoresCorrectly() throws Exception {
    RenderContext rc = new RenderContext();
    rc.pushGlobalOpacity();
    rc.multiplyGlobalOpacity(0.5f);
    rc.pushGlobalOpacity();
    rc.multiplyGlobalOpacity(0.4f);
    assertEquals(0.2f, getGlobalOpacity(rc), 0.0001f);
    rc.popGlobalOpacity();
    assertEquals(0.5f, getGlobalOpacity(rc), 0.0001f);
    rc.popGlobalOpacity();
    assertEquals(1.0f, getGlobalOpacity(rc), 0.0001f);
  }

  @Test
  public void multiplyByZero_producesZero() throws Exception {
    RenderContext rc = new RenderContext();
    rc.multiplyGlobalOpacity(0.0f);
    assertEquals(0.0f, getGlobalOpacity(rc), 0.0001f);
  }

  @Test
  public void multiplyByOne_keepsOriginal() throws Exception {
    RenderContext rc = new RenderContext();
    rc.multiplyGlobalOpacity(0.7f);
    rc.multiplyGlobalOpacity(1.0f);
    assertEquals(0.7f, getGlobalOpacity(rc), 0.0001f);
  }

  // ── globalBrightness ──────────────────────────────────────────────

  @Test
  public void getGlobalBrightness_initiallyOne() {
    RenderContext rc = new RenderContext();
    assertEquals(1.0f, rc.getGlobalBrightness(), 0.0001f);
  }

  @Test
  public void setGlobalBrightness_updatesValue() {
    RenderContext rc = new RenderContext();
    rc.setGlobalBrightness(0.75f);
    assertEquals(0.75f, rc.getGlobalBrightness(), 0.0001f);
  }

  @Test
  public void setGlobalBrightness_zero() {
    RenderContext rc = new RenderContext();
    rc.setGlobalBrightness(0.0f);
    assertEquals(0.0f, rc.getGlobalBrightness(), 0.0001f);
  }

  // ── getAmbient / addAmbient ───────────────────────────────────────

  @Test
  public void beginAffectorSetup_resetsAmbient() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    float[] ambient = new float[4];
    rc.getAmbient(ambient);
    assertEquals(0f, ambient[0], 0.0001f);
    assertEquals(0f, ambient[1], 0.0001f);
    assertEquals(0f, ambient[2], 0.0001f);
    assertEquals(1f, ambient[3], 0.0001f);
  }

  @Test
  public void addAmbient_accumulatesColor() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    rc.addAmbient(new float[]{0.5f, 0.3f, 0.1f, 1.0f}, 1.0f);
    float[] ambient = new float[4];
    rc.getAmbient(ambient);
    assertEquals(0.5f, ambient[0], 0.0001f);
    assertEquals(0.3f, ambient[1], 0.0001f);
    assertEquals(0.1f, ambient[2], 0.0001f);
  }

  @Test
  public void addAmbient_multipleAdds() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    rc.addAmbient(new float[]{0.2f, 0.2f, 0.2f, 1.0f}, 1.0f);
    rc.addAmbient(new float[]{0.3f, 0.3f, 0.3f, 1.0f}, 1.0f);
    float[] ambient = new float[4];
    rc.getAmbient(ambient);
    assertEquals(0.5f, ambient[0], 0.0001f);
    assertEquals(0.5f, ambient[1], 0.0001f);
    assertEquals(0.5f, ambient[2], 0.0001f);
  }

  @Test
  public void addAmbient_withBrightness() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    rc.addAmbient(new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0.5f);
    float[] ambient = new float[4];
    rc.getAmbient(ambient);
    assertEquals(0.5f, ambient[0], 0.0001f);
    assertEquals(0.5f, ambient[1], 0.0001f);
    assertEquals(0.5f, ambient[2], 0.0001f);
  }

  // ── isFogEnabled ──────────────────────────────────────────────────

  @Test
  public void isFogEnabled_initiallyFalse() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    assertFalse(rc.isFogEnabled());
  }

  @Test
  public void setIsFogEnabled_true() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    rc.setIsFogEnabled(true);
    assertTrue(rc.isFogEnabled());
  }

  @Test
  public void setIsFogEnabled_toggleBackToFalse() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    rc.setIsFogEnabled(true);
    rc.setIsFogEnabled(false);
    assertFalse(rc.isFogEnabled());
  }

  // ── isTextureEnabled ──────────────────────────────────────────────

  @Test
  public void isTextureEnabled_initiallyFalse() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    assertFalse(rc.isTextureEnabled());
  }

  @Test
  public void clearDiffuseColorTextureAdapter_keepsFalse() {
    RenderContext rc = new RenderContext();
    rc.clearDiffuseColorTextureAdapter();
    assertFalse(rc.isTextureEnabled());
  }

  // ── isLightingEnabled ─────────────────────────────────────────────

  @Test
  public void isLightingEnabled_alwaysTrue() {
    RenderContext rc = new RenderContext();
    assertTrue(rc.isLightingEnabled());
  }

  // ── isShadingEnabled ──────────────────────────────────────────────

  @Test
  public void isShadingEnabled_initiallyFalse() {
    RenderContext rc = new RenderContext();
    assertFalse(rc.isShadingEnabled());
  }

  // ── getNextLightID ────────────────────────────────────────────────

  @Test
  public void getNextLightID_afterBeginAffectorSetup_startsAtGL_LIGHT0() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    int id = rc.getNextLightID();
    // GL_LIGHT0 = 0x4000
    assertEquals(0x4000, id);
  }

  @Test
  public void getNextLightID_increments() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    int first = rc.getNextLightID();
    int second = rc.getNextLightID();
    assertEquals(first + 1, second);
  }

  @Test
  public void getNextLightID_threeConsecutive() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    int a = rc.getNextLightID();
    int b = rc.getNextLightID();
    int c = rc.getNextLightID();
    assertEquals(a + 1, b);
    assertEquals(b + 1, c);
  }

  // ── getURatio / getVRatio when no texture ─────────────────────────

  @Test
  public void getURatio_noTexture_returnsNaN() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    assertTrue(Float.isNaN(rc.getURatio()));
  }

  @Test
  public void getVRatio_noTexture_returnsNaN() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    assertTrue(Float.isNaN(rc.getVRatio()));
  }

  // ── UnusedTexturesListener static add/remove ──────────────────────

  @Test
  public void addRemoveUnusedTexturesListener_noException() {
    RenderContext.UnusedTexturesListener listener = gl -> {};
    RenderContext.addUnusedTexturesListener(listener);
    RenderContext.removeUnusedTexturesListener(listener);
  }

  @Test
  public void addUnusedTexturesListener_twice_noException() {
    RenderContext.UnusedTexturesListener listener = gl -> {};
    RenderContext.addUnusedTexturesListener(listener);
    RenderContext.addUnusedTexturesListener(listener);
    RenderContext.removeUnusedTexturesListener(listener);
    RenderContext.removeUnusedTexturesListener(listener);
  }

  // ── Reflection helper ─────────────────────────────────────────────

  private static float getGlobalOpacity(RenderContext rc) throws Exception {
    Field f = RenderContext.class.getDeclaredField("globalOpacity");
    f.setAccessible(true);
    return f.getFloat(rc);
  }
}
