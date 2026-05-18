package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.PointLight;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrPointLight} — propertyChanged coverage for attenuation
 * properties and overridden getConstant/Linear/QuadraticAttenuation methods.
 */
public class GlrPointLightPropertyTest {

  private GlrPointLight<?> adapter;
  private PointLight sg;

  @Before
  public void setUp() {
    sg = new PointLight();
    adapter = (GlrPointLight<?>) AdapterFactory.getAdapterFor(sg);
  }

  // ── propertyChanged — constant attenuation ────────────────────────

  @Test
  public void propertyChanged_constantAttenuation_syncsField() throws Exception {
    sg.constantAttenuation.setValue(2.5);
    adapter.propertyChanged(sg.constantAttenuation);
    assertEquals(2.5f, getFieldFloat("constant"), 0.001f);
  }

  @Test
  public void propertyChanged_constantAttenuation_reflectedInGetter() throws Exception {
    sg.constantAttenuation.setValue(3.0);
    adapter.propertyChanged(sg.constantAttenuation);
    Method m = GlrPointLight.class.getDeclaredMethod("getConstantAttenuation");
    m.setAccessible(true);
    assertEquals(3.0f, (float) m.invoke(adapter), 0.001f);
  }

  // ── propertyChanged — linear attenuation ──────────────────────────

  @Test
  public void propertyChanged_linearAttenuation_syncsField() throws Exception {
    sg.linearAttenuation.setValue(0.5);
    adapter.propertyChanged(sg.linearAttenuation);
    assertEquals(0.5f, getFieldFloat("linear"), 0.001f);
  }

  @Test
  public void propertyChanged_linearAttenuation_reflectedInGetter() throws Exception {
    sg.linearAttenuation.setValue(0.75);
    adapter.propertyChanged(sg.linearAttenuation);
    Method m = GlrPointLight.class.getDeclaredMethod("getLinearAttenuation");
    m.setAccessible(true);
    assertEquals(0.75f, (float) m.invoke(adapter), 0.001f);
  }

  // ── propertyChanged — quadratic attenuation ───────────────────────

  @Test
  public void propertyChanged_quadraticAttenuation_syncsField() throws Exception {
    sg.quadraticAttenuation.setValue(0.1);
    adapter.propertyChanged(sg.quadraticAttenuation);
    assertEquals(0.1f, getFieldFloat("quadratic"), 0.001f);
  }

  @Test
  public void propertyChanged_quadraticAttenuation_reflectedInGetter() throws Exception {
    sg.quadraticAttenuation.setValue(0.25);
    adapter.propertyChanged(sg.quadraticAttenuation);
    Method m = GlrPointLight.class.getDeclaredMethod("getQuadraticAttenuation");
    m.setAccessible(true);
    assertEquals(0.25f, (float) m.invoke(adapter), 0.001f);
  }

  // ── propertyChanged — delegates to super for color/brightness ─────

  @Test
  public void propertyChanged_color_delegatesToGlrLight() throws Exception {
    adapter.propertyChanged(sg.color);
    Field f = GlrLight.class.getDeclaredField("color");
    f.setAccessible(true);
    float[] color = (float[]) f.get(adapter);
    assertFalse("Should have synced color", Float.isNaN(color[0]));
  }

  @Test
  public void propertyChanged_brightness_delegatesToGlrLight() throws Exception {
    adapter.propertyChanged(sg.brightness);
    Field f = GlrLight.class.getDeclaredField("brightness");
    f.setAccessible(true);
    assertFalse("Should have synced brightness", Float.isNaN(f.getFloat(adapter)));
  }

  // ── Initial state ─────────────────────────────────────────────────

  @Test
  public void initialConstant_matchesOwnerDefault() throws Exception {
    // AdapterFactory syncs all properties; PointLight default constant=1.0
    assertEquals(1.0f, getFieldFloat("constant"), 0.001f);
  }

  @Test
  public void initialLinear_isZero() throws Exception {
    assertEquals(0.0f, getFieldFloat("linear"), 0.001f);
  }

  @Test
  public void initialQuadratic_isZero() throws Exception {
    assertEquals(0.0f, getFieldFloat("quadratic"), 0.001f);
  }

  // ── Structure ─────────────────────────────────────────────────────

  @Test
  public void extendsGlrLight() {
    assertTrue(GlrLight.class.isAssignableFrom(GlrPointLight.class));
  }

  @Test
  public void ownerIsSet() {
    assertSame(sg, adapter.getOwner());
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private float getFieldFloat(String name) throws Exception {
    Field f = GlrPointLight.class.getDeclaredField(name);
    f.setAccessible(true);
    return f.getFloat(adapter);
  }
}
