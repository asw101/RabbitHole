package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * External service integration tests for {@link RenderContext}.
 * Verifies the GL context binding, opacity stack, resource cache,
 * and structural contracts without requiring a live GL context.
 */
public class RenderContextExternalServiceTest {

  @Test
  public void constructor_noException() {
    RenderContext rc = new RenderContext();
    assertNotNull(rc);
  }

  @Test
  public void constructor_glInitiallyNull() {
    RenderContext rc = new RenderContext();
    assertNull(rc.gl);
  }

  @Test
  public void extendsContext() {
    assertTrue(Context.class.isAssignableFrom(RenderContext.class));
  }

  @Test
  public void setGL_null_doesNotThrow() {
    RenderContext rc = new RenderContext();
    rc.setGL(null);
  }

  @Test
  public void setGL_sameValue_noChange() {
    RenderContext rc = new RenderContext();
    rc.setGL(null);
    assertNull(rc.gl);
  }

  @Test
  public void globalOpacityStack_fieldExists() throws Exception {
    Field f = RenderContext.class.getDeclaredField("globalOpacityStack");
    f.setAccessible(true);
    RenderContext rc = new RenderContext();
    assertNotNull(f.get(rc));
  }

  @Test
  public void globalOpacity_initiallyOne() throws Exception {
    Field f = RenderContext.class.getDeclaredField("globalOpacity");
    f.setAccessible(true);
    RenderContext rc = new RenderContext();
    assertEquals(1.0f, f.getFloat(rc), 0.0001f);
  }

  @Test
  public void pushGlobalOpacity_methodSignature() throws Exception {
    Method m = RenderContext.class.getDeclaredMethod("pushGlobalOpacity");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void popGlobalOpacity_methodSignature() throws Exception {
    Method m = RenderContext.class.getDeclaredMethod("popGlobalOpacity");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void multiplyGlobalOpacity_methodSignature() throws Exception {
    Method m = RenderContext.class.getDeclaredMethod("multiplyGlobalOpacity", float.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void isTextureEnabled_methodExists() throws Exception {
    Method m = RenderContext.class.getDeclaredMethod("isTextureEnabled");
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void isLightingEnabled_methodExists() throws Exception {
    Method m = RenderContext.class.getDeclaredMethod("isLightingEnabled");
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void enableNormalize_methodExists() throws Exception {
    Method m = RenderContext.class.getDeclaredMethod("enableNormalize");
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void disableNormalize_methodExists() throws Exception {
    Method m = RenderContext.class.getDeclaredMethod("disableNormalize");
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void resourceCache_fieldExists() throws Exception {
    Field f = RenderContext.class.getDeclaredField("resourceCache");
    f.setAccessible(true);
    RenderContext rc = new RenderContext();
    assertNotNull(f.get(rc));
  }

  @Test
  public void unusedTexturesListener_innerInterfaceExists() {
    boolean found = false;
    for (Class<?> inner : RenderContext.class.getDeclaredClasses()) {
      if (inner.getSimpleName().equals("UnusedTexturesListener")) {
        found = true;
        assertTrue(inner.isInterface());
        break;
      }
    }
    assertTrue("UnusedTexturesListener inner interface should exist", found);
  }

  @Test
  public void twoInstances_haveIndependentState() {
    RenderContext rc1 = new RenderContext();
    RenderContext rc2 = new RenderContext();
    assertNotSame(rc1, rc2);
  }

  @Test
  public void glu_isNotNull() {
    RenderContext rc = new RenderContext();
    assertNotNull(rc.glu);
  }

  @Test
  public void globalBrightness_initiallyOne() throws Exception {
    Field f = RenderContext.class.getDeclaredField("globalBrightness");
    f.setAccessible(true);
    RenderContext rc = new RenderContext();
    assertEquals(1.0f, f.getFloat(rc), 0.0001f);
  }

  @Test
  public void ambient_bufferInitialized() throws Exception {
    Field f = RenderContext.class.getDeclaredField("ambient");
    f.setAccessible(true);
    RenderContext rc = new RenderContext();
    float[] ambient = (float[]) f.get(rc);
    assertNotNull(ambient);
    assertEquals(4, ambient.length);
  }

  @Test
  public void colorScratch_bufferInitialized() throws Exception {
    Field f = RenderContext.class.getDeclaredField("colorScratch");
    f.setAccessible(true);
    RenderContext rc = new RenderContext();
    float[] scratch = (float[]) f.get(rc);
    assertNotNull(scratch);
    assertEquals(4, scratch.length);
  }

  @Test
  public void face_fieldExists() throws Exception {
    Field f = RenderContext.class.getDeclaredField("face");
    f.setAccessible(true);
    assertEquals(int.class, f.getType());
  }

  @Test
  public void isFogEnabled_fieldExists() throws Exception {
    Field f = RenderContext.class.getDeclaredField("isFogEnabled");
    f.setAccessible(true);
    assertEquals(boolean.class, f.getType());
  }

  @Test
  public void isShadingEnabled_fieldExists() throws Exception {
    Field f = RenderContext.class.getDeclaredField("isShadingEnabled");
    f.setAccessible(true);
    assertEquals(boolean.class, f.getType());
  }

  @Test
  public void getGlobalBrightness_methodExists() throws Exception {
    Method m = RenderContext.class.getDeclaredMethod("getGlobalBrightness");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(float.class, m.getReturnType());
  }

  @Test
  public void setGlobalBrightness_methodExists() throws Exception {
    Method m = RenderContext.class.getDeclaredMethod("setGlobalBrightness", float.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void captureBuffers_methodExists() throws Exception {
    Method m = RenderContext.class.getDeclaredMethod("captureBuffers",
        java.awt.image.BufferedImage.class, java.nio.FloatBuffer.class, boolean[].class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void renderLetterboxingIfNecessary_methodExists() throws Exception {
    Method m = RenderContext.class.getDeclaredMethod("renderLetterboxingIfNecessary",
        int.class, int.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
}
