package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import edu.cmu.cs.dennisc.scenegraph.DirectionalLight;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrDirectionalLight} — getPosition override (direction with w=0),
 * propertyChanged delegation, and structural checks.
 */
public class GlrDirectionalLightPropertyTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  private GlrDirectionalLight adapter;
  private DirectionalLight sg;

  @Before
  public void setUp() {
    sg = new DirectionalLight();
    adapter = (GlrDirectionalLight) AdapterFactory.getAdapterFor(sg);
  }

  // ── propertyChanged — color ───────────────────────────────────────

  @Test
  public void propertyChanged_color_syncsArray() throws Exception {
    adapter.propertyChanged(sg.color);
    Field f = GlrLight.class.getDeclaredField("color");
    f.setAccessible(true);
    float[] color = (float[]) f.get(adapter);
    assertFalse("Color should be synced", Float.isNaN(color[0]));
    assertEquals(1.0f, color[0], 0.01f); // WHITE default
  }

  // ── propertyChanged — brightness ──────────────────────────────────

  @Test
  public void propertyChanged_brightness_syncsFloat() throws Exception {
    adapter.propertyChanged(sg.brightness);
    Field f = GlrLight.class.getDeclaredField("brightness");
    f.setAccessible(true);
    assertEquals(1.0f, f.getFloat(adapter), 0.01f);
  }

  // ── Structure ─────────────────────────────────────────────────────

  @Test
  public void extendsGlrLight_notGlrPointLight() {
    assertTrue(GlrLight.class.isAssignableFrom(GlrDirectionalLight.class));
    assertFalse(GlrPointLight.class.isAssignableFrom(GlrDirectionalLight.class));
  }

  @Test
  public void ownerIsSet() {
    assertSame(sg, adapter.getOwner());
  }

  // ── Default overridden values ─────────────────────────────────────

  @Test
  public void defaultSpotCutoff_is180() throws Exception {
    Method m = GlrLight.class.getDeclaredMethod("getSpotCutoff");
    m.setAccessible(true);
    assertEquals(180f, (float) m.invoke(adapter), 0.001f);
  }

  @Test
  public void defaultConstantAttenuation_isOne() throws Exception {
    Method m = GlrLight.class.getDeclaredMethod("getConstantAttenuation");
    m.setAccessible(true);
    assertEquals(1f, (float) m.invoke(adapter), 0.001f);
  }

  @Test
  public void defaultLinearAttenuation_isZero() throws Exception {
    Method m = GlrLight.class.getDeclaredMethod("getLinearAttenuation");
    m.setAccessible(true);
    assertEquals(0f, (float) m.invoke(adapter), 0.001f);
  }

  @Test
  public void defaultQuadraticAttenuation_isZero() throws Exception {
    Method m = GlrLight.class.getDeclaredMethod("getQuadraticAttenuation");
    m.setAccessible(true);
    assertEquals(0f, (float) m.invoke(adapter), 0.001f);
  }
}
