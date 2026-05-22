package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.AmbientLight;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrAmbientLight} — creation, hierarchy, and property
 * delegation to base GlrLight class.
 */
public class GlrAmbientLightCreationTest {

  // ── Creation via AdapterFactory ───────────────────────────────────

  @Test
  public void adapterFactory_createsGlrAmbientLight() {
    AmbientLight sg = new AmbientLight();
    GlrLight<?> adapter = (GlrLight<?>) AdapterFactory.getAdapterFor(sg);
    assertNotNull(adapter);
    assertTrue(adapter instanceof GlrAmbientLight);
  }

  @Test
  public void ownerIsSet() {
    AmbientLight sg = new AmbientLight();
    GlrAmbientLight adapter = (GlrAmbientLight) AdapterFactory.getAdapterFor(sg);
    assertSame(sg, adapter.getOwner());
  }

  // ── propertyChanged — color ───────────────────────────────────────

  @Test
  public void propertyChanged_color_syncsFromBase() throws Exception {
    AmbientLight sg = new AmbientLight();
    GlrAmbientLight adapter = (GlrAmbientLight) AdapterFactory.getAdapterFor(sg);
    adapter.propertyChanged(sg.color);
    Field f = GlrLight.class.getDeclaredField("color");
    f.setAccessible(true);
    float[] color = (float[]) f.get(adapter);
    assertEquals(1.0f, color[0], 0.01f); // WHITE
    assertEquals(1.0f, color[1], 0.01f);
    assertEquals(1.0f, color[2], 0.01f);
    assertEquals(1.0f, color[3], 0.01f);
  }

  // ── propertyChanged — brightness ──────────────────────────────────

  @Test
  public void propertyChanged_brightness_syncsFromBase() throws Exception {
    AmbientLight sg = new AmbientLight();
    GlrAmbientLight adapter = (GlrAmbientLight) AdapterFactory.getAdapterFor(sg);
    adapter.propertyChanged(sg.brightness);
    Field f = GlrLight.class.getDeclaredField("brightness");
    f.setAccessible(true);
    assertEquals(1.0f, f.getFloat(adapter), 0.01f);
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void extendsGlrLight() {
    assertTrue(GlrLight.class.isAssignableFrom(GlrAmbientLight.class));
  }

  @Test
  public void doesNotExtendGlrPointLight() {
    assertFalse(GlrPointLight.class.isAssignableFrom(GlrAmbientLight.class));
  }

  @Test
  public void extendsGlrAffector() {
    assertTrue(GlrAffector.class.isAssignableFrom(GlrAmbientLight.class));
  }

  // ── No additional fields ──────────────────────────────────────────

  @Test
  public void noAdditionalDeclaredFields() {
    Field[] fields = GlrAmbientLight.class.getDeclaredFields();
    assertEquals("GlrAmbientLight should declare no additional fields", 0, fields.length);
  }

  // ── No additional methods ─────────────────────────────────────────

  @Test
  public void noAdditionalDeclaredMethods() {
    long nonSyntheticMethodCount = java.util.Arrays.stream(GlrAmbientLight.class.getDeclaredMethods())
        .filter(method -> !method.isSynthetic())
        .count();
    assertEquals("GlrAmbientLight should declare no additional non-synthetic methods", 0, nonSyntheticMethodCount);
  }

  // ── Multiple instances ────────────────────────────────────────────

  @Test
  public void multipleInstances_independentState() throws Exception {
    AmbientLight sg1 = new AmbientLight();
    AmbientLight sg2 = new AmbientLight();
    GlrAmbientLight a1 = (GlrAmbientLight) AdapterFactory.getAdapterFor(sg1);
    GlrAmbientLight a2 = (GlrAmbientLight) AdapterFactory.getAdapterFor(sg2);
    assertNotSame(a1, a2);
    assertNotSame(a1.getOwner(), a2.getOwner());
  }
}
