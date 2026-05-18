package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrLight} — default parameter values (position, spot direction,
 * spot exponent, spot cutoff, attenuation), color/brightness field defaults.
 * Uses a minimal concrete subclass since GlrLight is abstract.
 */
public class GlrLightDefaultsTest {

  // ── getPosition defaults ──────────────────────────────────────────

  @Test
  public void getPosition_defaultZ_isOne() {
    TestableGlrLight light = new TestableGlrLight();
    float[] rv = new float[4];
    light.getPosition(rv);
    assertEquals(0.0f, rv[0], 0.0001f);
    assertEquals(0.0f, rv[1], 0.0001f);
    assertEquals(1.0f, rv[2], 0.0001f);
    assertEquals(0.0f, rv[3], 0.0001f);
  }

  @Test
  public void getPosition_returnsSameArray() {
    TestableGlrLight light = new TestableGlrLight();
    float[] rv = new float[4];
    float[] result = light.getPosition(rv);
    assertSame(rv, result);
  }

  // ── getSpotDirection defaults ─────────────────────────────────────

  @Test
  public void getSpotDirection_default() {
    TestableGlrLight light = new TestableGlrLight();
    float[] rv = new float[3];
    light.getSpotDirection(rv);
    assertEquals(0.0f, rv[0], 0.0001f);
    assertEquals(0.0f, rv[1], 0.0001f);
    assertEquals(-1.0f, rv[2], 0.0001f);
  }

  @Test
  public void getSpotDirection_returnsSameArray() {
    TestableGlrLight light = new TestableGlrLight();
    float[] rv = new float[3];
    float[] result = light.getSpotDirection(rv);
    assertSame(rv, result);
  }

  // ── getSpotExponent ───────────────────────────────────────────────

  @Test
  public void getSpotExponent_defaultZero() {
    TestableGlrLight light = new TestableGlrLight();
    assertEquals(0.0f, light.getSpotExponent(), 0.0001f);
  }

  // ── getSpotCutoff ─────────────────────────────────────────────────

  @Test
  public void getSpotCutoff_default180() {
    TestableGlrLight light = new TestableGlrLight();
    assertEquals(180.0f, light.getSpotCutoff(), 0.0001f);
  }

  // ── getConstantAttenuation ────────────────────────────────────────

  @Test
  public void getConstantAttenuation_defaultOne() {
    TestableGlrLight light = new TestableGlrLight();
    assertEquals(1.0f, light.getConstantAttenuation(), 0.0001f);
  }

  // ── getLinearAttenuation ──────────────────────────────────────────

  @Test
  public void getLinearAttenuation_defaultZero() {
    TestableGlrLight light = new TestableGlrLight();
    assertEquals(0.0f, light.getLinearAttenuation(), 0.0001f);
  }

  // ── getQuadraticAttenuation ───────────────────────────────────────

  @Test
  public void getQuadraticAttenuation_defaultZero() {
    TestableGlrLight light = new TestableGlrLight();
    assertEquals(0.0f, light.getQuadraticAttenuation(), 0.0001f);
  }

  // ── color field defaults ──────────────────────────────────────────

  @Test
  public void color_initiallyNaN() throws Exception {
    TestableGlrLight light = new TestableGlrLight();
    float[] color = getFloatArray(light, "color");
    assertEquals(4, color.length);
    for (float v : color) {
      assertTrue("color should be NaN", Float.isNaN(v));
    }
  }

  // ── brightness field default ──────────────────────────────────────

  @Test
  public void brightness_initiallyNaN() throws Exception {
    TestableGlrLight light = new TestableGlrLight();
    Field f = GlrLight.class.getDeclaredField("brightness");
    f.setAccessible(true);
    assertTrue(Float.isNaN(f.getFloat(light)));
  }

  // ── static fields ─────────────────────────────────────────────────

  @Test
  public void s_position_hasLength4() throws Exception {
    float[] pos = (float[]) getStaticField("s_position");
    assertEquals(4, pos.length);
  }

  @Test
  public void s_spotDirection_hasLength3() throws Exception {
    float[] dir = (float[]) getStaticField("s_spotDirection");
    assertEquals(3, dir.length);
  }

  @Test
  public void s_positionBuffer_isNotNull() throws Exception {
    assertNotNull(getStaticField("s_positionBuffer"));
  }

  @Test
  public void s_spotDirectionBuffer_isNotNull() throws Exception {
    assertNotNull(getStaticField("s_spotDirectionBuffer"));
  }

  @Test
  public void s_positionBuffer_isFloatBuffer() throws Exception {
    assertTrue(getStaticField("s_positionBuffer") instanceof FloatBuffer);
  }

  @Test
  public void s_spotDirectionBuffer_isFloatBuffer() throws Exception {
    assertTrue(getStaticField("s_spotDirectionBuffer") instanceof FloatBuffer);
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private static float[] getFloatArray(Object obj, String name) throws Exception {
    Field f = findField(obj.getClass(), name);
    f.setAccessible(true);
    return (float[]) f.get(obj);
  }

  private static Object getStaticField(String name) throws Exception {
    Field f = GlrLight.class.getDeclaredField(name);
    f.setAccessible(true);
    return f.get(null);
  }

  private static Field findField(Class<?> clazz, String name) throws NoSuchFieldException {
    while (clazz != null) {
      try {
        return clazz.getDeclaredField(name);
      } catch (NoSuchFieldException e) {
        clazz = clazz.getSuperclass();
      }
    }
    throw new NoSuchFieldException(name);
  }

  /**
   * Minimal concrete subclass for testing abstract GlrLight.
   * No-ops setupAffectors to avoid needing a RenderContext.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private static class TestableGlrLight extends GlrLight {
    // Expose protected methods for testing
    @Override
    public float[] getPosition(float[] rv) {
      return super.getPosition(rv);
    }

    @Override
    public float[] getSpotDirection(float[] rv) {
      return super.getSpotDirection(rv);
    }

    @Override
    public float getSpotExponent() {
      return super.getSpotExponent();
    }

    @Override
    public float getSpotCutoff() {
      return super.getSpotCutoff();
    }

    @Override
    public float getConstantAttenuation() {
      return super.getConstantAttenuation();
    }

    @Override
    public float getLinearAttenuation() {
      return super.getLinearAttenuation();
    }

    @Override
    public float getQuadraticAttenuation() {
      return super.getQuadraticAttenuation();
    }
  }
}
