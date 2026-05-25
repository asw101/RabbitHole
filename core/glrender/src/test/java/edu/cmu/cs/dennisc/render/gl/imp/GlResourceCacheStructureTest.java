package edu.cmu.cs.dennisc.render.gl.imp;


import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlResourceCache} extracted-class structure — verifies
 * the cache class exists, has the expected fields, and its static listener
 * management methods work without a GL context.
 */
public class GlResourceCacheStructureTest {


  // ── Class structure ───────────────────────────────────────────────

  @Test
  public void glResourceCache_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.render.gl.imp.GlResourceCache");
    assertNotNull(cls);
  }

  @Test
  public void glResourceCache_isPackagePrivate() throws ClassNotFoundException {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.render.gl.imp.GlResourceCache");
    int mods = cls.getModifiers();
    assertFalse("Should not be public", java.lang.reflect.Modifier.isPublic(mods));
    assertFalse("Should not be protected", java.lang.reflect.Modifier.isProtected(mods));
    assertFalse("Should not be private", java.lang.reflect.Modifier.isPrivate(mods));
  }

  @Test
  public void glResourceCache_hasDisplayListMapField() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.render.gl.imp.GlResourceCache");
    Field f = cls.getDeclaredField("displayListMap");
    assertNotNull(f);
  }

  @Test
  public void glResourceCache_hasTextureBindingMapField() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.render.gl.imp.GlResourceCache");
    Field f = cls.getDeclaredField("textureBindingMap");
    assertNotNull(f);
  }

  @Test
  public void glResourceCache_hasToBeForgottenDisplayListsField() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.render.gl.imp.GlResourceCache");
    Field f = cls.getDeclaredField("toBeForgottenDisplayLists");
    assertNotNull(f);
  }

  @Test
  public void glResourceCache_hasToBeForgottenTexturesField() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.render.gl.imp.GlResourceCache");
    Field f = cls.getDeclaredField("toBeForgottenTextures");
    assertNotNull(f);
  }

  // ── Static listener management (via RenderContext API) ────────────

  @Test
  public void addRemoveUnusedTexturesListener_noException() {
    RenderContext.UnusedTexturesListener listener = gl -> {};
    RenderContext.addUnusedTexturesListener(listener);
    RenderContext.removeUnusedTexturesListener(listener);
  }

  @Test
  public void addUnusedTexturesListener_multiple_noException() {
    RenderContext.UnusedTexturesListener l1 = gl -> {};
    RenderContext.UnusedTexturesListener l2 = gl -> {};
    RenderContext.addUnusedTexturesListener(l1);
    RenderContext.addUnusedTexturesListener(l2);
    RenderContext.removeUnusedTexturesListener(l1);
    RenderContext.removeUnusedTexturesListener(l2);
  }

  @Test
  public void removeNonExistentListener_noException() {
    RenderContext.removeUnusedTexturesListener(gl -> {});
  }

  // ── GlResourceCache instance methods via reflection ───────────────

  @Test
  public void getDisplayListID_null_returnsNull() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.render.gl.imp.GlResourceCache");
    Object cache = cls.getDeclaredConstructor().newInstance();
    Method m = cls.getDeclaredMethod("getDisplayListID",
        Class.forName("edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrGeometry"));
    m.setAccessible(true);
    Object result = m.invoke(cache, (Object) null);
    assertNull(result);
  }

  // ── RenderContext owns a GlResourceCache ──────────────────────────

  @Test
  public void renderContext_hasResourceCacheField() throws Exception {
    Field f = RenderContext.class.getDeclaredField("resourceCache");
    assertNotNull(f);
    f.setAccessible(true);
    RenderContext rc = new RenderContext();
    assertNotNull(f.get(rc));
  }
}
