package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.SpotLight;
import org.alice.math.immutable.AngleInRadians;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrSpotLight} — propertyChanged coverage for outerBeamAngle,
 * innerBeamAngle (no-op), falloff (ignored), and getSpotCutoff override.
 */
public class GlrSpotLightPropertyTest {

  private GlrSpotLight adapter;
  private SpotLight sg;

  @Before
  public void setUp() {
    sg = new SpotLight();
    adapter = (GlrSpotLight) AdapterFactory.getAdapterFor(sg);
  }

  // ── propertyChanged — outerBeamAngle ──────────────────────────────

  @Test
  public void propertyChanged_outerBeamAngle_syncsField() throws Exception {
    sg.outerBeamAngle.setValue(new AngleInRadians(Math.PI / 4));
    adapter.propertyChanged(sg.outerBeamAngle);
    Field f = GlrSpotLight.class.getDeclaredField("outerBeamInDegrees");
    f.setAccessible(true);
    assertEquals(45.0f, f.getFloat(adapter), 0.1f);
  }

  @Test
  public void propertyChanged_outerBeamAngle_reflectedInGetSpotCutoff() throws Exception {
    sg.outerBeamAngle.setValue(new AngleInRadians(Math.PI / 6));
    adapter.propertyChanged(sg.outerBeamAngle);
    Method m = GlrSpotLight.class.getDeclaredMethod("getSpotCutoff");
    m.setAccessible(true);
    assertEquals(30.0f, (float) m.invoke(adapter), 0.1f);
  }

  @Test
  public void propertyChanged_outerBeamAngle_90degrees() throws Exception {
    sg.outerBeamAngle.setValue(new AngleInRadians(Math.PI / 2));
    adapter.propertyChanged(sg.outerBeamAngle);
    Field f = GlrSpotLight.class.getDeclaredField("outerBeamInDegrees");
    f.setAccessible(true);
    assertEquals(90.0f, f.getFloat(adapter), 0.1f);
  }

  // ── propertyChanged — innerBeamAngle (no-op) ─────────────────────

  @Test
  public void propertyChanged_innerBeamAngle_noOp() {
    // innerBeamAngle branch is empty — should not throw
    adapter.propertyChanged(sg.innerBeamAngle);
  }

  // ── propertyChanged — falloff (ignored) ───────────────────────────

  @Test
  public void propertyChanged_falloff_ignored() {
    // falloff is explicitly checked and ignored — should not throw
    adapter.propertyChanged(sg.falloff);
  }

  // ── propertyChanged — delegates attenuation to GlrPointLight ─────

  @Test
  public void propertyChanged_constantAttenuation_delegates() throws Exception {
    sg.constantAttenuation.setValue(5.0);
    adapter.propertyChanged(sg.constantAttenuation);
    Field f = GlrPointLight.class.getDeclaredField("constant");
    f.setAccessible(true);
    assertEquals(5.0f, f.getFloat(adapter), 0.001f);
  }

  @Test
  public void propertyChanged_linearAttenuation_delegates() throws Exception {
    sg.linearAttenuation.setValue(0.3);
    adapter.propertyChanged(sg.linearAttenuation);
    Field f = GlrPointLight.class.getDeclaredField("linear");
    f.setAccessible(true);
    assertEquals(0.3f, f.getFloat(adapter), 0.001f);
  }

  @Test
  public void propertyChanged_color_delegates() throws Exception {
    adapter.propertyChanged(sg.color);
    Field f = GlrLight.class.getDeclaredField("color");
    f.setAccessible(true);
    float[] color = (float[]) f.get(adapter);
    assertFalse(Float.isNaN(color[0]));
  }

  @Test
  public void propertyChanged_brightness_delegates() throws Exception {
    adapter.propertyChanged(sg.brightness);
    Field f = GlrLight.class.getDeclaredField("brightness");
    f.setAccessible(true);
    assertFalse(Float.isNaN(f.getFloat(adapter)));
  }

  // ── Structure ─────────────────────────────────────────────────────

  @Test
  public void extendsGlrPointLight() {
    assertTrue(GlrPointLight.class.isAssignableFrom(GlrSpotLight.class));
  }

  @Test
  public void ownerIsSet() {
    assertSame(sg, adapter.getOwner());
  }

  // ── Initial state ─────────────────────────────────────────────────

  @Test
  public void initialOuterBeamInDegrees_matchesOwnerDefault() throws Exception {
    Field f = GlrSpotLight.class.getDeclaredField("outerBeamInDegrees");
    f.setAccessible(true);
    float expected = (float) sg.outerBeamAngle.getValue().getAsDegrees();
    assertEquals(expected, f.getFloat(adapter), 0.1f);
  }
}
