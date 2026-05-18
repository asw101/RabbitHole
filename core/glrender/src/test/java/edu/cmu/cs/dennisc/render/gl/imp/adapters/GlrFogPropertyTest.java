package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.ExponentialFog;
import edu.cmu.cs.dennisc.scenegraph.ExponentialSquaredFog;
import edu.cmu.cs.dennisc.scenegraph.LinearFog;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for fog adapter classes — propertyChanged coverage for all fog types:
 * {@link GlrLinearFog}, {@link GlrExponentialFog}, {@link GlrExponentialSquaredFog},
 * and base {@link GlrFog} color property.
 */
public class GlrFogPropertyTest {

  // ═══════════════════════════════════════════════════════════════════
  // GlrLinearFog
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void linearFog_adapterCreation() {
    LinearFog sg = new LinearFog();
    GlrLinearFog adapter = (GlrLinearFog) AdapterFactory.getAdapterFor(sg);
    assertNotNull(adapter);
    assertSame(sg, adapter.getOwner());
  }

  @Test
  public void linearFog_propertyChanged_nearDistance() throws Exception {
    LinearFog sg = new LinearFog();
    GlrLinearFog adapter = (GlrLinearFog) AdapterFactory.getAdapterFor(sg);
    sg.nearDistance.setValue(10.0);
    adapter.propertyChanged(sg.nearDistance);
    Field f = GlrLinearFog.class.getDeclaredField("near");
    f.setAccessible(true);
    assertEquals(10.0f, f.getFloat(adapter), 0.001f);
  }

  @Test
  public void linearFog_propertyChanged_farDistance() throws Exception {
    LinearFog sg = new LinearFog();
    GlrLinearFog adapter = (GlrLinearFog) AdapterFactory.getAdapterFor(sg);
    sg.farDistance.setValue(500.0);
    adapter.propertyChanged(sg.farDistance);
    Field f = GlrLinearFog.class.getDeclaredField("far");
    f.setAccessible(true);
    assertEquals(500.0f, f.getFloat(adapter), 0.001f);
  }

  @Test
  public void linearFog_propertyChanged_color_delegatesToBase() throws Exception {
    LinearFog sg = new LinearFog();
    GlrLinearFog adapter = (GlrLinearFog) AdapterFactory.getAdapterFor(sg);
    adapter.propertyChanged(sg.color);
    Field f = GlrFog.class.getDeclaredField("color");
    f.setAccessible(true);
    float[] color = (float[]) f.get(adapter);
    assertEquals(1.0f, color[0], 0.01f); // WHITE
  }

  @Test
  public void linearFog_initialNear_matchesDefault() throws Exception {
    LinearFog sg = new LinearFog();
    GlrLinearFog adapter = (GlrLinearFog) AdapterFactory.getAdapterFor(sg);
    Field f = GlrLinearFog.class.getDeclaredField("near");
    f.setAccessible(true);
    // AdapterFactory syncs; LinearFog default nearDistance=1.0
    assertEquals(1.0f, f.getFloat(adapter), 0.001f);
  }

  @Test
  public void linearFog_initialFar_matchesDefault() throws Exception {
    LinearFog sg = new LinearFog();
    GlrLinearFog adapter = (GlrLinearFog) AdapterFactory.getAdapterFor(sg);
    Field f = GlrLinearFog.class.getDeclaredField("far");
    f.setAccessible(true);
    // AdapterFactory syncs; LinearFog default farDistance=256.0
    assertEquals(256.0f, f.getFloat(adapter), 0.001f);
  }

  // ═══════════════════════════════════════════════════════════════════
  // GlrExponentialFog
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void exponentialFog_adapterCreation() {
    ExponentialFog sg = new ExponentialFog();
    GlrExponentialFog adapter = (GlrExponentialFog) AdapterFactory.getAdapterFor(sg);
    assertNotNull(adapter);
    assertSame(sg, adapter.getOwner());
  }

  @Test
  public void exponentialFog_propertyChanged_density() throws Exception {
    ExponentialFog sg = new ExponentialFog();
    GlrExponentialFog adapter = (GlrExponentialFog) AdapterFactory.getAdapterFor(sg);
    sg.density.setValue(0.05);
    adapter.propertyChanged(sg.density);
    Field f = GlrExponentialFog.class.getDeclaredField("density");
    f.setAccessible(true);
    assertEquals(0.05f, f.getFloat(adapter), 0.001f);
  }

  @Test
  public void exponentialFog_propertyChanged_color_delegatesToBase() throws Exception {
    ExponentialFog sg = new ExponentialFog();
    GlrExponentialFog adapter = (GlrExponentialFog) AdapterFactory.getAdapterFor(sg);
    adapter.propertyChanged(sg.color);
    Field f = GlrFog.class.getDeclaredField("color");
    f.setAccessible(true);
    float[] color = (float[]) f.get(adapter);
    assertEquals(1.0f, color[0], 0.01f);
  }

  @Test
  public void exponentialFog_initialDensity_matchesDefault() throws Exception {
    ExponentialFog sg = new ExponentialFog();
    GlrExponentialFog adapter = (GlrExponentialFog) AdapterFactory.getAdapterFor(sg);
    Field f = GlrExponentialFog.class.getDeclaredField("density");
    f.setAccessible(true);
    // AdapterFactory syncs; ExponentialFog default density=1.0
    assertEquals(1.0f, f.getFloat(adapter), 0.001f);
  }

  // ═══════════════════════════════════════════════════════════════════
  // GlrExponentialSquaredFog
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void exponentialSquaredFog_adapterCreation() {
    ExponentialSquaredFog sg = new ExponentialSquaredFog();
    GlrExponentialSquaredFog adapter = (GlrExponentialSquaredFog) AdapterFactory.getAdapterFor(sg);
    assertNotNull(adapter);
    assertSame(sg, adapter.getOwner());
  }

  @Test
  public void exponentialSquaredFog_propertyChanged_density() throws Exception {
    ExponentialSquaredFog sg = new ExponentialSquaredFog();
    GlrExponentialSquaredFog adapter = (GlrExponentialSquaredFog) AdapterFactory.getAdapterFor(sg);
    sg.density.setValue(0.02);
    adapter.propertyChanged(sg.density);
    Field f = GlrExponentialSquaredFog.class.getDeclaredField("density");
    f.setAccessible(true);
    assertEquals(0.02f, f.getFloat(adapter), 0.001f);
  }

  @Test
  public void exponentialSquaredFog_propertyChanged_color_delegatesToBase() throws Exception {
    ExponentialSquaredFog sg = new ExponentialSquaredFog();
    GlrExponentialSquaredFog adapter = (GlrExponentialSquaredFog) AdapterFactory.getAdapterFor(sg);
    adapter.propertyChanged(sg.color);
    Field f = GlrFog.class.getDeclaredField("color");
    f.setAccessible(true);
    float[] color = (float[]) f.get(adapter);
    assertEquals(1.0f, color[0], 0.01f);
  }

  @Test
  public void exponentialSquaredFog_initialDensity_matchesDefault() throws Exception {
    ExponentialSquaredFog sg = new ExponentialSquaredFog();
    GlrExponentialSquaredFog adapter = (GlrExponentialSquaredFog) AdapterFactory.getAdapterFor(sg);
    Field f = GlrExponentialSquaredFog.class.getDeclaredField("density");
    f.setAccessible(true);
    // AdapterFactory syncs; ExponentialSquaredFog default density=1.0
    assertEquals(1.0f, f.getFloat(adapter), 0.001f);
  }

  // ═══════════════════════════════════════════════════════════════════
  // Base GlrFog — color property
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void baseFog_colorField_syncedByAdapterFactory() throws Exception {
    LinearFog sg = new LinearFog();
    GlrLinearFog adapter = (GlrLinearFog) AdapterFactory.getAdapterFor(sg);
    Field f = GlrFog.class.getDeclaredField("color");
    f.setAccessible(true);
    float[] color = (float[]) f.get(adapter);
    assertEquals(4, color.length);
    // AdapterFactory syncs; Fog default color is WHITE
    assertEquals(1.0f, color[0], 0.001f);
  }

  @Test
  public void baseFog_colorField_length4() throws Exception {
    ExponentialFog sg = new ExponentialFog();
    GlrExponentialFog adapter = (GlrExponentialFog) AdapterFactory.getAdapterFor(sg);
    Field f = GlrFog.class.getDeclaredField("color");
    f.setAccessible(true);
    float[] color = (float[]) f.get(adapter);
    assertEquals(4, color.length);
  }

  // ═══════════════════════════════════════════════════════════════════
  // Hierarchy
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void linearFog_extendsGlrFog() {
    assertTrue(GlrFog.class.isAssignableFrom(GlrLinearFog.class));
  }

  @Test
  public void exponentialFog_extendsGlrFog() {
    assertTrue(GlrFog.class.isAssignableFrom(GlrExponentialFog.class));
  }

  @Test
  public void exponentialSquaredFog_extendsGlrFog() {
    assertTrue(GlrFog.class.isAssignableFrom(GlrExponentialSquaredFog.class));
  }

  @Test
  public void baseFog_extendsGlrAffector() {
    assertTrue(GlrAffector.class.isAssignableFrom(GlrFog.class));
  }
}
