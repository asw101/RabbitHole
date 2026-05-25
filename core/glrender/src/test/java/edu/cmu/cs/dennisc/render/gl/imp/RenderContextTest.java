package edu.cmu.cs.dennisc.render.gl.imp;


import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Consolidated tests for {@link RenderContext} — opacity stack,
 * brightness, ambient light, fog, texture/lighting/shading state,
 * light ID allocation, texture ratio, and structural contracts.
 * Uses reflection for private fields since no GL context is available.
 */
public class RenderContextTest {


  // ═══════════════════════════════════════════════════════════════════
  // Construction & structure
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void constructor_noException() {
    RenderContext rc = new RenderContext();
    assertNotNull(rc);
  }

  @Test
  public void constructor_glInitiallyNull() {
    RenderContext rc = new RenderContext();
    assertNull(rc.gl);
  }

  @Test
  public void extendsContext() {
    assertTrue(Context.class.isAssignableFrom(RenderContext.class));
  }

  @Test
  public void setGL_null_doesNotThrow() {
    RenderContext rc = new RenderContext();
    rc.setGL(null);
    assertNull(rc.gl);
  }

  @Test
  public void glu_isNotNull() {
    RenderContext rc = new RenderContext();
    assertNotNull(rc.glu);
  }

  @Test
  public void twoInstances_haveIndependentState() {
    RenderContext rc1 = new RenderContext();
    RenderContext rc2 = new RenderContext();
    assertNotSame(rc1, rc2);
  }

  @Test
  public void globalOpacityStack_fieldExists() throws Exception {
    Field f = RenderContext.class.getDeclaredField("globalOpacityStack");
    f.setAccessible(true);
    assertNotNull(f.get(new RenderContext()));
  }

  @Test
  public void resourceCache_fieldExists() throws Exception {
    Field f = RenderContext.class.getDeclaredField("resourceCache");
    f.setAccessible(true);
    assertNotNull(f.get(new RenderContext()));
  }

  @Test
  public void ambient_bufferInitialized() throws Exception {
    Field f = RenderContext.class.getDeclaredField("ambient");
    f.setAccessible(true);
    float[] ambient = (float[]) f.get(new RenderContext());
    assertNotNull(ambient);
    assertEquals(4, ambient.length);
  }

  @Test
  public void colorScratch_bufferInitialized() throws Exception {
    Field f = RenderContext.class.getDeclaredField("colorScratch");
    f.setAccessible(true);
    float[] scratch = (float[]) f.get(new RenderContext());
    assertNotNull(scratch);
    assertEquals(4, scratch.length);
  }

  @Test
  public void unusedTexturesListener_innerInterfaceExists() {
    boolean found = false;
    for (Class<?> inner : RenderContext.class.getDeclaredClasses()) {
      if (inner.getSimpleName().equals("UnusedTexturesListener")) {
        found = true;
        assertTrue(inner.isInterface());
        break;
      }
    }
    assertTrue("UnusedTexturesListener inner interface should exist", found);
  }

  @Test
  public void captureBuffers_methodExists() throws Exception {
    assertNotNull(RenderContext.class.getDeclaredMethod("captureBuffers",
        java.awt.image.BufferedImage.class, java.nio.FloatBuffer.class, boolean[].class));
  }

  @Test
  public void renderLetterboxingIfNecessary_methodExists() throws Exception {
    assertNotNull(RenderContext.class.getDeclaredMethod("renderLetterboxingIfNecessary",
        int.class, int.class));
  }

  // ═══════════════════════════════════════════════════════════════════
  // Global opacity stack
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void globalOpacity_initiallyOne() throws Exception {
    assertEquals(1.0f, getGlobalOpacity(new RenderContext()), 0.0001f);
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

  @Test
  public void multiplyByNegative_allowed() throws Exception {
    RenderContext rc = new RenderContext();
    rc.multiplyGlobalOpacity(-1.0f);
    assertEquals(-1.0f, getGlobalOpacity(rc), 0.0001f);
  }

  @Test
  public void opacityStack_pushPopEmpty() throws Exception {
    RenderContext rc = new RenderContext();
    rc.pushGlobalOpacity();
    rc.popGlobalOpacity();
    assertEquals(1.0f, getGlobalOpacity(rc), 0.0001f);
  }

  @Test
  public void opacityStack_deepNesting() throws Exception {
    RenderContext rc = new RenderContext();
    for (int i = 0; i < 10; i++) {
      rc.pushGlobalOpacity();
      rc.multiplyGlobalOpacity(0.9f);
    }
    for (int i = 9; i >= 0; i--) {
      rc.popGlobalOpacity();
    }
    assertEquals(1.0f, getGlobalOpacity(rc), 0.0001f);
  }

  @Test
  public void opacityStack_nestedMultiply_accumulatesCorrectly() throws Exception {
    RenderContext rc = new RenderContext();
    rc.pushGlobalOpacity();
    rc.multiplyGlobalOpacity(0.5f);
    assertEquals(0.5f, getGlobalOpacity(rc), 0.0001f);
    rc.pushGlobalOpacity();
    rc.multiplyGlobalOpacity(0.3f);
    assertEquals(0.15f, getGlobalOpacity(rc), 0.0001f);
    rc.popGlobalOpacity();
    assertEquals(0.5f, getGlobalOpacity(rc), 0.0001f);
    rc.popGlobalOpacity();
    assertEquals(1.0f, getGlobalOpacity(rc), 0.0001f);
  }

  // ═══════════════════════════════════════════════════════════════════
  // Global brightness
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void getGlobalBrightness_initiallyOne() {
    assertEquals(1.0f, new RenderContext().getGlobalBrightness(), 0.0001f);
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

  // ═══════════════════════════════════════════════════════════════════
  // Ambient light accumulation
  // ═══════════════════════════════════════════════════════════════════

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
  public void beginAffectorSetup_resetsAmbientAfterAccumulation() {
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

  // ═══════════════════════════════════════════════════════════════════
  // Fog state
  // ═══════════════════════════════════════════════════════════════════

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

  @Test
  public void beginAffectorSetup_resetsFogEnabled() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    rc.setIsFogEnabled(true);
    rc.beginAffectorSetup();
    assertFalse(rc.isFogEnabled());
  }

  // ═══════════════════════════════════════════════════════════════════
  // Texture, lighting, shading state
  // ═══════════════════════════════════════════════════════════════════

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

  @Test
  public void isLightingEnabled_alwaysTrue() {
    assertTrue(new RenderContext().isLightingEnabled());
  }

  @Test
  public void isShadingEnabled_initiallyFalse() {
    assertFalse(new RenderContext().isShadingEnabled());
  }

  // ═══════════════════════════════════════════════════════════════════
  // Light ID allocation
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void getNextLightID_afterBeginAffectorSetup_startsAtGL_LIGHT0() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    assertEquals(0x4000, rc.getNextLightID());
  }

  @Test
  public void getNextLightID_increments() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    int first = rc.getNextLightID();
    assertEquals(first + 1, rc.getNextLightID());
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

  @Test
  public void getNextLightID_fiveConsecutive_sequential() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    int base = rc.getNextLightID();
    for (int i = 1; i < 5; i++) {
      assertEquals(base + i, rc.getNextLightID());
    }
  }

  @Test
  public void beginAffectorSetup_resetsNextLightID() {
    RenderContext rc = new RenderContext();
    rc.beginAffectorSetup();
    int id1 = rc.getNextLightID();
    rc.beginAffectorSetup();
    int id2 = rc.getNextLightID();
    assertEquals(id1, id2);
  }

  // ═══════════════════════════════════════════════════════════════════
  // Texture ratio (no texture → NaN)
  // ═══════════════════════════════════════════════════════════════════

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

  // ═══════════════════════════════════════════════════════════════════
  // UnusedTexturesListener
  // ═══════════════════════════════════════════════════════════════════

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

  // ═══════════════════════════════════════════════════════════════════
  // Reflection helper — Field cached to avoid repeated lookup
  // ═══════════════════════════════════════════════════════════════════

  private static final Field GLOBAL_OPACITY_FIELD;
  static {
    try {
      GLOBAL_OPACITY_FIELD = RenderContext.class.getDeclaredField("globalOpacity");
      GLOBAL_OPACITY_FIELD.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private static float getGlobalOpacity(RenderContext rc) throws Exception {
    return GLOBAL_OPACITY_FIELD.getFloat(rc);
  }
}
