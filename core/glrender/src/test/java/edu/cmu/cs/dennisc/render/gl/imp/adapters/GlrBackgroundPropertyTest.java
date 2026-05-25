package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import edu.cmu.cs.dennisc.scenegraph.Background;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrBackground} — propertyChanged color sync,
 * initial state, and structural checks.
 */
public class GlrBackgroundPropertyTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  // ── Creation ──────────────────────────────────────────────────────

  @Test
  public void adapterCreation() {
    Background sg = new Background();
    GlrBackground adapter = (GlrBackground) AdapterFactory.getAdapterFor(sg);
    assertNotNull(adapter);
    assertSame(sg, adapter.getOwner());
  }

  // ── propertyChanged — color ───────────────────────────────────────

  @Test
  public void propertyChanged_color_syncsArray() throws Exception {
    Background sg = new Background();
    GlrBackground adapter = (GlrBackground) AdapterFactory.getAdapterFor(sg);
    adapter.propertyChanged(sg.color);
    float[] color = getColor(adapter);
    assertEquals(1.0f, color[0], 0.01f); // WHITE
    assertEquals(1.0f, color[1], 0.01f);
    assertEquals(1.0f, color[2], 0.01f);
    assertEquals(1.0f, color[3], 0.01f);
  }

  @Test
  public void propertyChanged_color_multipleUpdates() throws Exception {
    Background sg = new Background();
    GlrBackground adapter = (GlrBackground) AdapterFactory.getAdapterFor(sg);
    adapter.propertyChanged(sg.color);
    float[] c1 = getColor(adapter).clone();
    adapter.propertyChanged(sg.color);
    float[] c2 = getColor(adapter);
    assertEquals(c1[0], c2[0], 0.001f);
    assertEquals(c1[1], c2[1], 0.001f);
  }

  // ── Initial state ─────────────────────────────────────────────────

  @Test
  public void colorField_syncedToWhiteByAdapterFactory() throws Exception {
    Background sg = new Background();
    GlrBackground adapter = (GlrBackground) AdapterFactory.getAdapterFor(sg);
    float[] color = getColor(adapter);
    assertEquals(4, color.length);
    // AdapterFactory syncs all properties; default color is WHITE
    assertEquals(1.0f, color[0], 0.001f);
  }

  @Test
  public void colorField_length4() throws Exception {
    Background sg = new Background();
    GlrBackground adapter = (GlrBackground) AdapterFactory.getAdapterFor(sg);
    float[] color = getColor(adapter);
    assertEquals(4, color.length);
  }

  // ── Structure ─────────────────────────────────────────────────────

  @Test
  public void extendsGlrElement() {
    assertTrue(GlrElement.class.isAssignableFrom(GlrBackground.class));
  }

  @Test
  public void doesNotExtendGlrAffector() {
    assertFalse(GlrAffector.class.isAssignableFrom(GlrBackground.class));
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private float[] getColor(GlrBackground adapter) throws Exception {
    Field f = GlrBackground.class.getDeclaredField("color");
    f.setAccessible(true);
    return (float[]) f.get(adapter);
  }
}
