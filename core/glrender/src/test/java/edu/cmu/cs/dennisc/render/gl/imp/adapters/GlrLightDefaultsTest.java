package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import edu.cmu.cs.dennisc.scenegraph.AmbientLight;
import edu.cmu.cs.dennisc.scenegraph.DirectionalLight;
import edu.cmu.cs.dennisc.scenegraph.PointLight;
import edu.cmu.cs.dennisc.scenegraph.SpotLight;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrLight} base class — default values for position, direction,
 * attenuation, spot parameters. Exercises via concrete subclasses.
 */
public class GlrLightDefaultsTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  // ── Base light default methods ────────────────────────────────────

  @Test
  public void defaultPosition_isForwardDirection() throws Exception {
    AmbientLight sg = new AmbientLight();
    GlrAmbientLight adapter = (GlrAmbientLight) AdapterFactory.getAdapterFor(sg);
    float[] rv = new float[4];
    Method m = GlrLight.class.getDeclaredMethod("getPosition", float[].class);
    m.setAccessible(true);
    m.invoke(adapter, (Object) rv);
    assertEquals(0, rv[0], 0.001f);
    assertEquals(0, rv[1], 0.001f);
    assertEquals(1, rv[2], 0.001f);
    assertEquals(0, rv[3], 0.001f);
  }

  @Test
  public void defaultSpotDirection_isNegativeZ() throws Exception {
    AmbientLight sg = new AmbientLight();
    GlrAmbientLight adapter = (GlrAmbientLight) AdapterFactory.getAdapterFor(sg);
    float[] rv = new float[3];
    Method m = GlrLight.class.getDeclaredMethod("getSpotDirection", float[].class);
    m.setAccessible(true);
    m.invoke(adapter, (Object) rv);
    assertEquals(0, rv[0], 0.001f);
    assertEquals(0, rv[1], 0.001f);
    assertEquals(-1, rv[2], 0.001f);
  }

  @Test
  public void defaultSpotExponent_isZero() throws Exception {
    AmbientLight sg = new AmbientLight();
    GlrAmbientLight adapter = (GlrAmbientLight) AdapterFactory.getAdapterFor(sg);
    Method m = GlrLight.class.getDeclaredMethod("getSpotExponent");
    m.setAccessible(true);
    assertEquals(0f, (float) m.invoke(adapter), 0.001f);
  }

  @Test
  public void defaultSpotCutoff_is180() throws Exception {
    AmbientLight sg = new AmbientLight();
    GlrAmbientLight adapter = (GlrAmbientLight) AdapterFactory.getAdapterFor(sg);
    Method m = GlrLight.class.getDeclaredMethod("getSpotCutoff");
    m.setAccessible(true);
    assertEquals(180f, (float) m.invoke(adapter), 0.001f);
  }

  @Test
  public void defaultConstantAttenuation_isOne() throws Exception {
    AmbientLight sg = new AmbientLight();
    GlrAmbientLight adapter = (GlrAmbientLight) AdapterFactory.getAdapterFor(sg);
    Method m = GlrLight.class.getDeclaredMethod("getConstantAttenuation");
    m.setAccessible(true);
    assertEquals(1f, (float) m.invoke(adapter), 0.001f);
  }

  @Test
  public void defaultLinearAttenuation_isZero() throws Exception {
    AmbientLight sg = new AmbientLight();
    GlrAmbientLight adapter = (GlrAmbientLight) AdapterFactory.getAdapterFor(sg);
    Method m = GlrLight.class.getDeclaredMethod("getLinearAttenuation");
    m.setAccessible(true);
    assertEquals(0f, (float) m.invoke(adapter), 0.001f);
  }

  @Test
  public void defaultQuadraticAttenuation_isZero() throws Exception {
    AmbientLight sg = new AmbientLight();
    GlrAmbientLight adapter = (GlrAmbientLight) AdapterFactory.getAdapterFor(sg);
    Method m = GlrLight.class.getDeclaredMethod("getQuadraticAttenuation");
    m.setAccessible(true);
    assertEquals(0f, (float) m.invoke(adapter), 0.001f);
  }

  // ── Static fields ─────────────────────────────────────────────────

  @Test
  public void staticPositionBuffer_exists() throws Exception {
    Field f = GlrLight.class.getDeclaredField("s_positionBuffer");
    f.setAccessible(true);
    assertNotNull(f.get(null));
    assertTrue(f.get(null) instanceof FloatBuffer);
  }

  @Test
  public void staticSpotDirectionBuffer_exists() throws Exception {
    Field f = GlrLight.class.getDeclaredField("s_spotDirectionBuffer");
    f.setAccessible(true);
    assertNotNull(f.get(null));
    assertTrue(f.get(null) instanceof FloatBuffer);
  }

  @Test
  public void staticPosition_hasLength4() throws Exception {
    Field f = GlrLight.class.getDeclaredField("s_position");
    f.setAccessible(true);
    float[] arr = (float[]) f.get(null);
    assertEquals(4, arr.length);
  }

  @Test
  public void staticSpotDirection_hasLength3() throws Exception {
    Field f = GlrLight.class.getDeclaredField("s_spotDirection");
    f.setAccessible(true);
    float[] arr = (float[]) f.get(null);
    assertEquals(3, arr.length);
  }

  // ── Color field initialization ────────────────────────────────────

  @Test
  public void colorField_syncedByAdapterFactory() throws Exception {
    AmbientLight sg = new AmbientLight();
    GlrAmbientLight adapter = (GlrAmbientLight) AdapterFactory.getAdapterFor(sg);
    Field f = GlrLight.class.getDeclaredField("color");
    f.setAccessible(true);
    float[] color = (float[]) f.get(adapter);
    assertEquals(4, color.length);
    // AdapterFactory syncs all properties; default color is WHITE
    assertEquals(1.0f, color[0], 0.01f);
  }

  @Test
  public void brightnessField_syncedByAdapterFactory() throws Exception {
    AmbientLight sg = new AmbientLight();
    GlrAmbientLight adapter = (GlrAmbientLight) AdapterFactory.getAdapterFor(sg);
    Field f = GlrLight.class.getDeclaredField("brightness");
    f.setAccessible(true);
    // AdapterFactory syncs all properties; default brightness is 1.0
    assertEquals(1.0f, f.getFloat(adapter), 0.01f);
  }

  // ── propertyChanged for color and brightness ──────────────────────

  @Test
  public void propertyChanged_color_syncsArray() throws Exception {
    PointLight sg = new PointLight();
    GlrPointLight<?> adapter = (GlrPointLight<?>) AdapterFactory.getAdapterFor(sg);
    adapter.propertyChanged(sg.color);
    Field f = GlrLight.class.getDeclaredField("color");
    f.setAccessible(true);
    float[] color = (float[]) f.get(adapter);
    assertFalse("Color should no longer be NaN after sync", Float.isNaN(color[0]));
    assertEquals(1.0f, color[0], 0.01f); // WHITE
  }

  @Test
  public void propertyChanged_brightness_syncsFloat() throws Exception {
    PointLight sg = new PointLight();
    GlrPointLight<?> adapter = (GlrPointLight<?>) AdapterFactory.getAdapterFor(sg);
    adapter.propertyChanged(sg.brightness);
    Field f = GlrLight.class.getDeclaredField("brightness");
    f.setAccessible(true);
    assertEquals(1.0f, f.getFloat(adapter), 0.01f);
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void glrLight_extendsGlrAffector() {
    assertTrue(GlrAffector.class.isAssignableFrom(GlrLight.class));
  }

  @Test
  public void allLightSubclasses_extendGlrLight() {
    assertTrue(GlrLight.class.isAssignableFrom(GlrAmbientLight.class));
    assertTrue(GlrLight.class.isAssignableFrom(GlrPointLight.class));
    assertTrue(GlrLight.class.isAssignableFrom(GlrSpotLight.class));
    assertTrue(GlrLight.class.isAssignableFrom(GlrDirectionalLight.class));
  }
}
