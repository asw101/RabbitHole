package edu.cmu.cs.dennisc.render.gl;

import com.jogamp.opengl.DefaultGLCapabilitiesChooser;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLCapabilitiesChooser;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import org.junit.Assume;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlDrawableUtils} comparison logic and structural contracts.
 * Exercises the areEquivalentIgnoringMultisample algorithm via reflection,
 * and verifies the capabilities chooser fallback path.
 */
public class GlDrawableUtilsComparisonTest {


  // ── getPerhapsMultisampledGlCapabilitiesChooser ─────────────────────

  @Test
  public void getPerhapsMultisampledChooser_returnsNonNull() {
    GLCapabilitiesChooser chooser = GlDrawableUtils.getPerhapsMultisampledGlCapabilitiesChooser();
    assertNotNull(chooser);
  }

  @Test
  public void getPerhapsMultisampledChooser_defaultIsDefaultChooser() {
    GLCapabilitiesChooser chooser = GlDrawableUtils.getPerhapsMultisampledGlCapabilitiesChooser();
    assertTrue("With MAXIMUM_MULTISAMPLE_COUNT=0, should return DefaultGLCapabilitiesChooser",
        chooser instanceof DefaultGLCapabilitiesChooser);
  }

  @Test
  public void getPerhapsMultisampledChooser_stableReference() {
    GLCapabilitiesChooser a = GlDrawableUtils.getPerhapsMultisampledGlCapabilitiesChooser();
    GLCapabilitiesChooser b = GlDrawableUtils.getPerhapsMultisampledGlCapabilitiesChooser();
    assertSame("Should return same instance", a, b);
  }

  // ── getGlContextToShare ────────────────────────────────────────────

  @Test
  public void getGlContextToShare_nullInput_returnsNull() {
    assertNull(GlDrawableUtils.getGlContextToShare(null));
  }

  // ── areEquivalentIgnoringMultisample via reflection ─────────────────

  @Test
  public void areEquivalentIgnoringMultisample_methodExists() throws Exception {
    Method method = GlDrawableUtils.class.getDeclaredMethod(
        "areEquivalentIgnoringMultisample",
        GLCapabilitiesImmutable.class,
        GLCapabilitiesImmutable.class);
    assertNotNull(method);
    assertTrue("Should be private", java.lang.reflect.Modifier.isPrivate(method.getModifiers()));
    assertTrue("Should be static", java.lang.reflect.Modifier.isStatic(method.getModifiers()));
  }

  @Test
  public void areEquivalentIgnoringMultisample_identicalCaps_returnsTrue() throws Exception {
    GLCapabilities[] pair = createCapabilitiesPair();
    Assume.assumeTrue("No display available", pair != null);
    Method method = getEquivMethod();
    assertTrue("Identical caps should be equivalent", (boolean) method.invoke(null, pair[0], pair[1]));
  }

  @Test
  public void areEquivalentIgnoringMultisample_differentDepthBits_returnsFalse() throws Exception {
    GLCapabilities[] pair = createCapabilitiesPair();
    Assume.assumeTrue("No display available", pair != null);
    pair[0].setDepthBits(16);
    pair[1].setDepthBits(32);
    Method method = getEquivMethod();
    assertFalse("Different depth bits should not be equivalent", (boolean) method.invoke(null, pair[0], pair[1]));
  }

  @Test
  public void areEquivalentIgnoringMultisample_differentStencilBits_returnsFalse() throws Exception {
    GLCapabilities[] pair = createCapabilitiesPair();
    Assume.assumeTrue("No display available", pair != null);
    pair[0].setStencilBits(0);
    pair[1].setStencilBits(8);
    Method method = getEquivMethod();
    assertFalse("Different stencil bits should not be equivalent", (boolean) method.invoke(null, pair[0], pair[1]));
  }

  @Test
  public void areEquivalentIgnoringMultisample_differentDoubleBuffered_returnsFalse() throws Exception {
    GLCapabilities[] pair = createCapabilitiesPair();
    Assume.assumeTrue("No display available", pair != null);
    pair[0].setDoubleBuffered(true);
    pair[1].setDoubleBuffered(false);
    Method method = getEquivMethod();
    assertFalse("Different doubleBuffered should not be equivalent", (boolean) method.invoke(null, pair[0], pair[1]));
  }

  @Test
  public void areEquivalentIgnoringMultisample_differentSampleCount_returnsTrue() throws Exception {
    GLCapabilities[] pair = createCapabilitiesPair();
    Assume.assumeTrue("No display available", pair != null);
    pair[0].setSampleBuffers(true);
    pair[0].setNumSamples(4);
    pair[1].setSampleBuffers(false);
    pair[1].setNumSamples(0);
    Method method = getEquivMethod();
    assertTrue("Different sample count should still be equivalent (multisample ignored)", (boolean) method.invoke(null, pair[0], pair[1]));
  }

  // ── Constructor is private ─────────────────────────────────────────

  @Test
  public void constructor_isPrivate() throws Exception {
    var ctor = GlDrawableUtils.class.getDeclaredConstructor();
    assertTrue("Constructor should be private",
        java.lang.reflect.Modifier.isPrivate(ctor.getModifiers()));
  }

  @Test(expected = java.lang.reflect.InvocationTargetException.class)
  public void constructor_throwsAssertionError() throws Exception {
    var ctor = GlDrawableUtils.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    ctor.newInstance();
  }

  // ── Helpers ────────────────────────────────────────────────────────

  private static GLCapabilities[] createCapabilitiesPair() {
    try {
      return new GLCapabilities[]{new GLCapabilities(null), new GLCapabilities(null)};
    } catch (Throwable e) {
      // Native GL libs or display unavailable (headless CI) — skip tests
      return null;
    }
  }

  private static Method getEquivMethod() throws Exception {
    Method m = GlDrawableUtils.class.getDeclaredMethod(
        "areEquivalentIgnoringMultisample",
        GLCapabilitiesImmutable.class,
        GLCapabilitiesImmutable.class);
    m.setAccessible(true);
    return m;
  }
}
