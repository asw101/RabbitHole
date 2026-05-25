package edu.cmu.cs.dennisc.render.gl.imp;


import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlResourceCache} — display list and texture binding map
 * management, deferred-deletion queues, and listener management.
 * Uses reflection since the class is package-private.
 */
public class GlResourceCacheStateTest {


  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_noException() throws Exception {
    Object cache = createCache();
    assertNotNull(cache);
  }

  // ── displayListMap initially empty ────────────────────────────────

  @Test
  public void displayListMap_initiallyEmpty() throws Exception {
    Object cache = createCache();
    Map<?, ?> map = getMap(cache, "displayListMap");
    assertTrue(map.isEmpty());
  }

  // ── textureBindingMap initially empty ──────────────────────────────

  @Test
  public void textureBindingMap_initiallyEmpty() throws Exception {
    Object cache = createCache();
    Map<?, ?> map = getMap(cache, "textureBindingMap");
    assertTrue(map.isEmpty());
  }

  // ── toBeForgottenDisplayLists initially empty ─────────────────────

  @Test
  public void toBeForgottenDisplayLists_initiallyEmpty() throws Exception {
    Object cache = createCache();
    List<?> list = getList(cache, "toBeForgottenDisplayLists");
    assertTrue(list.isEmpty());
  }

  // ── toBeForgottenTextures initially empty ─────────────────────────

  @Test
  public void toBeForgottenTextures_initiallyEmpty() throws Exception {
    Object cache = createCache();
    List<?> list = getList(cache, "toBeForgottenTextures");
    assertTrue(list.isEmpty());
  }

  // ── getDisplayListID for unknown adapter returns null ──────────────

  @Test
  public void getDisplayListID_unknownAdapter_returnsNull() throws Exception {
    Object cache = createCache();
    java.lang.reflect.Method m = GlResourceCache.class.getDeclaredMethod(
        "getDisplayListID", edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrGeometry.class);
    m.setAccessible(true);
    Object result = m.invoke(cache, (Object) null);
    assertNull(result);
  }

  // ── Static listener list ──────────────────────────────────────────

  @Test
  public void unusedTexturesListeners_isNotNull() throws Exception {
    Field f = GlResourceCache.class.getDeclaredField("unusedTexturesListeners");
    f.setAccessible(true);
    assertNotNull(f.get(null));
  }

  @Test
  public void addUnusedTexturesListener_noException() {
    RenderContext.UnusedTexturesListener listener = gl -> {};
    RenderContext.addUnusedTexturesListener(listener);
    RenderContext.removeUnusedTexturesListener(listener);
  }

  @Test
  public void removeUnusedTexturesListener_noException() {
    RenderContext.UnusedTexturesListener listener = gl -> {};
    RenderContext.removeUnusedTexturesListener(listener);
  }

  // ── actuallyForgetDisplayListsIfNecessary with empty list ─────────

  @Test
  public void actuallyForgetDisplayLists_emptyList_noException() throws Exception {
    Object cache = createCache();
    java.lang.reflect.Method m = GlResourceCache.class.getDeclaredMethod(
        "actuallyForgetDisplayListsIfNecessary", RenderContext.class);
    m.setAccessible(true);
    // Will not call gl.glDeleteLists since list is empty
    m.invoke(cache, new RenderContext());
  }

  // ── actuallyForgetTexturesIfNecessary with empty list ──────────────

  @Test
  public void actuallyForgetTextures_emptyList_noException() throws Exception {
    Object cache = createCache();
    java.lang.reflect.Method m = GlResourceCache.class.getDeclaredMethod(
        "actuallyForgetTexturesIfNecessary", RenderContext.class);
    m.setAccessible(true);
    m.invoke(cache, new RenderContext());
  }

  // ── forgetAllCachedItems with empty maps ──────────────────────────

  @Test
  public void forgetAllCachedItems_emptyMaps_noException() throws Exception {
    Object cache = createCache();
    java.lang.reflect.Method m = GlResourceCache.class.getDeclaredMethod(
        "forgetAllCachedItems", RenderContext.class);
    m.setAccessible(true);
    m.invoke(cache, new RenderContext());
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private static Object createCache() throws Exception {
    var ctor = GlResourceCache.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    return ctor.newInstance();
  }

  @SuppressWarnings("unchecked")
  private static Map<?, ?> getMap(Object cache, String fieldName) throws Exception {
    Field f = GlResourceCache.class.getDeclaredField(fieldName);
    f.setAccessible(true);
    return (Map<?, ?>) f.get(cache);
  }

  @SuppressWarnings("unchecked")
  private static List<?> getList(Object cache, String fieldName) throws Exception {
    Field f = GlResourceCache.class.getDeclaredField(fieldName);
    f.setAccessible(true);
    return (List<?>) f.get(cache);
  }
}
