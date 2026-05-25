package edu.cmu.cs.dennisc.render.gl;


import com.jogamp.opengl.DefaultGLCapabilitiesChooser;
import com.jogamp.opengl.GLCapabilitiesChooser;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * External service integration tests for {@link GlDrawableUtils}.
 * Verifies the structural contracts and pure-logic fallback paths
 * for JOGL drawable utilities without requiring a live GL context.
 */
public class GlDrawableUtilsExternalServiceTest {


  // ── Structural: utility class pattern ─────────────────────────────

  @Test
  public void class_hasPrivateConstructor() throws Exception {
    Constructor<?> ctor = GlDrawableUtils.class.getDeclaredConstructor();
    assertTrue("Utility class must have private constructor",
        Modifier.isPrivate(ctor.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(GlDrawableUtils.class.getModifiers()));
  }

  // ── linuxDrawableSizes field ──────────────────────────────────────

  @Test
  public void linuxDrawableSizes_fieldExists() throws Exception {
    Field f = GlDrawableUtils.class.getDeclaredField("linuxDrawableSizes");
    assertNotNull(f);
    assertTrue("Should be static", Modifier.isStatic(f.getModifiers()));
    assertTrue("Should be final", Modifier.isFinal(f.getModifiers()));
  }

  // ── glDefaultCapabilitiesChooser field ────────────────────────────

  @Test
  public void defaultChooser_fieldExists() throws Exception {
    Field f = GlDrawableUtils.class.getDeclaredField("glDefaultCapabilitiesChooser");
    f.setAccessible(true);
    Object chooser = f.get(null);
    assertNotNull("Default chooser should be initialized statically", chooser);
    assertTrue(chooser instanceof DefaultGLCapabilitiesChooser);
  }

  // ── glMultisampleCapabilitiesChooser field ────────────────────────

  @Test
  public void multisampleChooser_isNull_whenMaxCountIsZero() throws Exception {
    Field f = GlDrawableUtils.class.getDeclaredField("glMultisampleCapabilitiesChooser");
    f.setAccessible(true);
    assertNull("With max count 0, multisample chooser should be null", f.get(null));
  }

  // ── getPerhapsMultisampledGlCapabilitiesChooser ────────────────────

  @Test
  public void getPerhapsMultisampledChooser_neverReturnsNull() {
    assertNotNull(GlDrawableUtils.getPerhapsMultisampledGlCapabilitiesChooser());
  }

  @Test
  public void getPerhapsMultisampledChooser_fallsBackToDefault() {
    GLCapabilitiesChooser chooser = GlDrawableUtils.getPerhapsMultisampledGlCapabilitiesChooser();
    assertTrue("Should fall back to DefaultGLCapabilitiesChooser",
        chooser instanceof DefaultGLCapabilitiesChooser);
  }

  @Test
  public void getPerhapsMultisampledChooser_returnsSameInstanceEachTime() {
    GLCapabilitiesChooser a = GlDrawableUtils.getPerhapsMultisampledGlCapabilitiesChooser();
    GLCapabilitiesChooser b = GlDrawableUtils.getPerhapsMultisampledGlCapabilitiesChooser();
    assertSame(a, b);
  }

  // ── getGlContextToShare ───────────────────────────────────────────

  @Test
  public void getGlContextToShare_null_returnsNull() {
    assertNull(GlDrawableUtils.getGlContextToShare(null));
  }

  // ── createGlCapabilities method ───────────────────────────────────

  @Test
  public void createGlCapabilities_methodExists() throws Exception {
    Method m = GlDrawableUtils.class.getDeclaredMethod(
        "createGlCapabilities",
        edu.cmu.cs.dennisc.render.RenderCapabilities.class);
    assertNotNull(m);
    assertTrue("Should be static", Modifier.isStatic(m.getModifiers()));
  }

  // ── GlMultisampledCapabilitiesChooser inner class ─────────────────

  @Test
  public void multisampledChooserClass_exists() {
    boolean found = false;
    for (Class<?> inner : GlDrawableUtils.class.getDeclaredClasses()) {
      if (inner.getSimpleName().equals("GlMultisampledCapabilitiesChooser")) {
        found = true;
        break;
      }
    }
    assertTrue("GlMultisampledCapabilitiesChooser inner class should exist", found);
  }

  @Test
  public void multisampledChooserClass_isPrivate() throws Exception {
    for (Class<?> inner : GlDrawableUtils.class.getDeclaredClasses()) {
      if (inner.getSimpleName().equals("GlMultisampledCapabilitiesChooser")) {
        assertTrue("Should be private", Modifier.isPrivate(inner.getModifiers()));
        return;
      }
    }
    fail("Class not found");
  }

  @Test
  public void multisampledChooserClass_extendsDefaultChooser() throws Exception {
    for (Class<?> inner : GlDrawableUtils.class.getDeclaredClasses()) {
      if (inner.getSimpleName().equals("GlMultisampledCapabilitiesChooser")) {
        assertEquals(DefaultGLCapabilitiesChooser.class, inner.getSuperclass());
        return;
      }
    }
    fail("Class not found");
  }

  @Test
  public void multisampledChooserClass_hasMaximumMultisampleCountField() throws Exception {
    for (Class<?> inner : GlDrawableUtils.class.getDeclaredClasses()) {
      if (inner.getSimpleName().equals("GlMultisampledCapabilitiesChooser")) {
        Field f = inner.getDeclaredField("maximumMultisampleCount");
        assertNotNull(f);
        return;
      }
    }
    fail("Class not found");
  }

  // ── getGlDrawableWidth / getGlDrawableHeight methods ──────────────

  @Test
  public void getGlDrawableWidth_methodSignature() throws Exception {
    Method m = GlDrawableUtils.class.getDeclaredMethod(
        "getGlDrawableWidth", com.jogamp.opengl.GLDrawable.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(int.class, m.getReturnType());
  }

  @Test
  public void getGlDrawableHeight_methodSignature() throws Exception {
    Method m = GlDrawableUtils.class.getDeclaredMethod(
        "getGlDrawableHeight", com.jogamp.opengl.GLDrawable.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(int.class, m.getReturnType());
  }

  @Test
  public void getGLJPanelWidth_methodSignature() throws Exception {
    Method m = GlDrawableUtils.class.getDeclaredMethod(
        "getGLJPanelWidth", com.jogamp.opengl.GLDrawable.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(int.class, m.getReturnType());
  }

  @Test
  public void getGLJPanelHeight_methodSignature() throws Exception {
    Method m = GlDrawableUtils.class.getDeclaredMethod(
        "getGLJPanelHeight", com.jogamp.opengl.GLDrawable.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(int.class, m.getReturnType());
  }

  // ── areEquivalentIgnoringMultisample ──────────────────────────────

  @Test
  public void areEquivalent_methodSignature() throws Exception {
    Method m = GlDrawableUtils.class.getDeclaredMethod(
        "areEquivalentIgnoringMultisample",
        GLCapabilitiesImmutable.class,
        GLCapabilitiesImmutable.class);
    assertTrue(Modifier.isPrivate(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  // ── Public factory methods ────────────────────────────────────────

  @Test
  public void createGLCanvas_methodSignature() throws Exception {
    Method m = GlDrawableUtils.class.getDeclaredMethod(
        "createGLCanvas",
        edu.cmu.cs.dennisc.render.RenderCapabilities.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void canCreateGlPixelBuffer_methodSignature() throws Exception {
    Method m = GlDrawableUtils.class.getDeclaredMethod("canCreateGlPixelBuffer");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void createOffscreenAutoDrawable_methodSignature() throws Exception {
    Method m = GlDrawableUtils.class.getDeclaredMethod(
        "createOffscreenAutoDrawable",
        com.jogamp.opengl.GLCapabilities.class,
        com.jogamp.opengl.GLCapabilitiesChooser.class,
        int.class, int.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
}
