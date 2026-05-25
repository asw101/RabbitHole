package edu.cmu.cs.dennisc.render.gl;


import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link TextureBinding} — map initialization, forget behavior
 * with null GL context, and the ForgettableBinding interface.
 */
public class TextureBindingStateTest {


  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_noException() {
    TextureBinding binding = new TextureBinding();
    assertNotNull(binding);
  }

  // ── map initially empty ───────────────────────────────────────────

  @Test
  public void map_initiallyEmpty() throws Exception {
    TextureBinding binding = new TextureBinding();
    Field f = TextureBinding.class.getDeclaredField("map");
    f.setAccessible(true);
    Map<?, ?> map = (Map<?, ?>) f.get(binding);
    assertTrue(map.isEmpty());
  }

  // ── forget with no data ───────────────────────────────────────────

  @Test
  public void forget_emptyMap_noException() {
    TextureBinding binding = new TextureBinding();
    edu.cmu.cs.dennisc.render.gl.imp.RenderContext rc =
        new edu.cmu.cs.dennisc.render.gl.imp.RenderContext();
    binding.forget(rc);
  }

  @Test
  public void forget_twice_noException() {
    TextureBinding binding = new TextureBinding();
    edu.cmu.cs.dennisc.render.gl.imp.RenderContext rc =
        new edu.cmu.cs.dennisc.render.gl.imp.RenderContext();
    binding.forget(rc);
    binding.forget(rc);
  }

  // ── implements ForgettableBinding ─────────────────────────────────

  @Test
  public void implementsForgettableBinding() {
    TextureBinding binding = new TextureBinding();
    assertTrue(binding instanceof ForgettableBinding);
  }

  // ── Data inner class structure ────────────────────────────────────

  @Test
  public void dataClass_exists() throws Exception {
    Class<?>[] innerClasses = TextureBinding.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> c : innerClasses) {
      if (c.getSimpleName().equals("Data")) {
        found = true;
        break;
      }
    }
    assertTrue("Data inner class should exist", found);
  }

  @Test
  public void dataClass_hasTextureField() throws Exception {
    Class<?> dataClass = getDataClass();
    Field f = dataClass.getDeclaredField("texture");
    assertNotNull(f);
  }

  @Test
  public void dataClass_hasTextureDataField() throws Exception {
    Class<?> dataClass = getDataClass();
    Field f = dataClass.getDeclaredField("textureData");
    assertNotNull(f);
  }

  @Test
  public void dataClass_hasGlField() throws Exception {
    Class<?> dataClass = getDataClass();
    Field f = dataClass.getDeclaredField("gl");
    assertNotNull(f);
  }

  // ── getData creates entry ─────────────────────────────────────────

  @Test
  public void getData_createsEntryForNewRC() throws Exception {
    TextureBinding binding = new TextureBinding();
    edu.cmu.cs.dennisc.render.gl.imp.RenderContext rc =
        new edu.cmu.cs.dennisc.render.gl.imp.RenderContext();

    java.lang.reflect.Method m = TextureBinding.class.getDeclaredMethod(
        "getData", edu.cmu.cs.dennisc.render.gl.imp.RenderContext.class);
    m.setAccessible(true);
    Object data = m.invoke(binding, rc);
    assertNotNull(data);

    Field f = TextureBinding.class.getDeclaredField("map");
    f.setAccessible(true);
    Map<?, ?> map = (Map<?, ?>) f.get(binding);
    assertEquals(1, map.size());
  }

  @Test
  public void getData_returnsSameForSameRC() throws Exception {
    TextureBinding binding = new TextureBinding();
    edu.cmu.cs.dennisc.render.gl.imp.RenderContext rc =
        new edu.cmu.cs.dennisc.render.gl.imp.RenderContext();

    java.lang.reflect.Method m = TextureBinding.class.getDeclaredMethod(
        "getData", edu.cmu.cs.dennisc.render.gl.imp.RenderContext.class);
    m.setAccessible(true);
    Object data1 = m.invoke(binding, rc);
    Object data2 = m.invoke(binding, rc);
    assertSame(data1, data2);
  }

  @Test
  public void getData_differentRCs_differentData() throws Exception {
    TextureBinding binding = new TextureBinding();
    edu.cmu.cs.dennisc.render.gl.imp.RenderContext rc1 =
        new edu.cmu.cs.dennisc.render.gl.imp.RenderContext();
    edu.cmu.cs.dennisc.render.gl.imp.RenderContext rc2 =
        new edu.cmu.cs.dennisc.render.gl.imp.RenderContext();

    java.lang.reflect.Method m = TextureBinding.class.getDeclaredMethod(
        "getData", edu.cmu.cs.dennisc.render.gl.imp.RenderContext.class);
    m.setAccessible(true);
    Object data1 = m.invoke(binding, rc1);
    Object data2 = m.invoke(binding, rc2);
    assertNotSame(data1, data2);
  }

  // ── forget removes entry ──────────────────────────────────────────

  @Test
  public void forget_removesEntry() throws Exception {
    TextureBinding binding = new TextureBinding();
    edu.cmu.cs.dennisc.render.gl.imp.RenderContext rc =
        new edu.cmu.cs.dennisc.render.gl.imp.RenderContext();

    // Create an entry first
    java.lang.reflect.Method m = TextureBinding.class.getDeclaredMethod(
        "getData", edu.cmu.cs.dennisc.render.gl.imp.RenderContext.class);
    m.setAccessible(true);
    m.invoke(binding, rc);

    Field f = TextureBinding.class.getDeclaredField("map");
    f.setAccessible(true);
    Map<?, ?> map = (Map<?, ?>) f.get(binding);
    assertEquals(1, map.size());

    binding.forget(rc);
    assertEquals(0, map.size());
  }

  // ── Data initial state ────────────────────────────────────────────

  @Test
  public void data_initialTexture_isNull() throws Exception {
    TextureBinding binding = new TextureBinding();
    edu.cmu.cs.dennisc.render.gl.imp.RenderContext rc =
        new edu.cmu.cs.dennisc.render.gl.imp.RenderContext();

    java.lang.reflect.Method m = TextureBinding.class.getDeclaredMethod(
        "getData", edu.cmu.cs.dennisc.render.gl.imp.RenderContext.class);
    m.setAccessible(true);
    Object data = m.invoke(binding, rc);

    Class<?> dataClass = getDataClass();
    Field f = dataClass.getDeclaredField("texture");
    f.setAccessible(true);
    assertNull(f.get(data));
  }

  @Test
  public void data_initialGl_isNull() throws Exception {
    TextureBinding binding = new TextureBinding();
    edu.cmu.cs.dennisc.render.gl.imp.RenderContext rc =
        new edu.cmu.cs.dennisc.render.gl.imp.RenderContext();

    java.lang.reflect.Method m = TextureBinding.class.getDeclaredMethod(
        "getData", edu.cmu.cs.dennisc.render.gl.imp.RenderContext.class);
    m.setAccessible(true);
    Object data = m.invoke(binding, rc);

    Class<?> dataClass = getDataClass();
    Field f = dataClass.getDeclaredField("gl");
    f.setAccessible(true);
    assertNull(f.get(data));
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private static Class<?> getDataClass() throws Exception {
    for (Class<?> c : TextureBinding.class.getDeclaredClasses()) {
      if (c.getSimpleName().equals("Data")) {
        return c;
      }
    }
    throw new ClassNotFoundException("TextureBinding.Data");
  }
}
