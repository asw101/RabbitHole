package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;


import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * TDD contract tests for issue #655: Extract GL resource lifecycle
 * management from RenderContext.java into GlResourceCache.
 *
 * Written BEFORE implementation — all tests FAIL initially.
 * They pass once extraction is complete.
 *
 * Contract groups:
 *   1.  GlResourceCache — exists as package-private class
 *   2.  GlResourceCache — owns the four mutable collection fields
 *   3.  GlResourceCache — static listener management methods
 *   4.  GlResourceCache — display list instance methods
 *   5.  GlResourceCache — texture binding instance methods
 *   6.  GlResourceCache — bulk operation methods
 *   7.  GlResourceCache — private internal methods
 *   8.  RenderContext — retains UnusedTexturesListener interface
 *   9.  RenderContext — has resourceCache delegate field
 *  10.  RenderContext — forwarding wrappers preserve public API
 *  11.  RenderContext — fields moved out (no longer on RenderContext)
 *  12.  RenderContext — line count under 500
 *  13.  Bug fix — removeUnusedTexturesListener uses .remove()
 *  14.  GlResourceCache source file exists
 */
public class GlResourceCacheExtractionContractTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }


  private static final String PKG = "edu.cmu.cs.dennisc.render.gl.imp";
  private static final String ADAPTERS_PKG = "edu.cmu.cs.dennisc.render.gl.imp.adapters";
  private static final String SRC_DIR =
      "core/glrender/src/main/java/edu/cmu/cs/dennisc/render/gl/imp/";

  private static Class<?> cacheClass;
  private static Class<?> renderContextClass;
  private static Class<?> glrGeometryClass;
  private static Class<?> glrTextureClass;

  @BeforeClass
  public static void resolveClasses() {
    cacheClass = tryLoad(PKG + ".GlResourceCache");
    renderContextClass = tryLoad(PKG + ".RenderContext");
    glrGeometryClass = tryLoad(ADAPTERS_PKG + ".GlrGeometry");
    glrTextureClass = tryLoad(ADAPTERS_PKG + ".GlrTexture");
  }

  private static Class<?> tryLoad(String fqcn) {
    try {
      return Class.forName(fqcn);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  private static boolean isPackagePrivate(int mods) {
    return !Modifier.isPublic(mods)
        && !Modifier.isProtected(mods)
        && !Modifier.isPrivate(mods);
  }

  // ══════════════════════════════════════════════════════════════════
  // 1. GlResourceCache — exists as package-private class
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void glResourceCache_classExists() {
    assertNotNull("GlResourceCache must exist as a class in " + PKG,
        cacheClass);
  }

  @Test
  public void glResourceCache_isPackagePrivate() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertTrue("GlResourceCache must be package-private",
        isPackagePrivate(cacheClass.getModifiers()));
  }

  @Test
  public void glResourceCache_isNotAbstract() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertFalse("GlResourceCache must not be abstract",
        Modifier.isAbstract(cacheClass.getModifiers()));
  }

  @Test
  public void glResourceCache_hasNoArgConstructor() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    try {
      cacheClass.getDeclaredConstructor();
    } catch (NoSuchMethodException e) {
      fail("GlResourceCache must have a no-arg constructor");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 2. GlResourceCache — owns the four mutable collection fields
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void glResourceCache_hasDisplayListMap() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertFieldDeclared(cacheClass, "displayListMap", Map.class);
  }

  @Test
  public void glResourceCache_hasTextureBindingMap() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertFieldDeclared(cacheClass, "textureBindingMap", Map.class);
  }

  @Test
  public void glResourceCache_hasToBeForgottenDisplayLists() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertFieldDeclared(cacheClass, "toBeForgottenDisplayLists", List.class);
  }

  @Test
  public void glResourceCache_hasToBeForgottenTextures() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertFieldDeclared(cacheClass, "toBeForgottenTextures", List.class);
  }

  @Test
  public void glResourceCache_displayListMap_isFinal() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertFieldIsFinal(cacheClass, "displayListMap");
  }

  @Test
  public void glResourceCache_textureBindingMap_isFinal() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertFieldIsFinal(cacheClass, "textureBindingMap");
  }

  @Test
  public void glResourceCache_toBeForgottenDisplayLists_isFinal() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertFieldIsFinal(cacheClass, "toBeForgottenDisplayLists");
  }

  @Test
  public void glResourceCache_toBeForgottenTextures_isFinal() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertFieldIsFinal(cacheClass, "toBeForgottenTextures");
  }

  // ══════════════════════════════════════════════════════════════════
  // 3. GlResourceCache — static listener management methods
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void glResourceCache_hasStaticUnusedTexturesListeners() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertFieldDeclared(cacheClass, "unusedTexturesListeners", List.class);
    assertFieldIsStatic(cacheClass, "unusedTexturesListeners");
  }

  @Test
  public void glResourceCache_addUnusedTexturesListener_isStatic() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertNotNull("RenderContext must exist", renderContextClass);
    Class<?> listenerType = findInnerClass(renderContextClass, "UnusedTexturesListener");
    assertNotNull("UnusedTexturesListener must exist in RenderContext", listenerType);
    try {
      Method m = cacheClass.getDeclaredMethod("addUnusedTexturesListener", listenerType);
      assertTrue("addUnusedTexturesListener must be static",
          Modifier.isStatic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("GlResourceCache must have addUnusedTexturesListener(" +
          "RenderContext.UnusedTexturesListener)");
    }
  }

  @Test
  public void glResourceCache_removeUnusedTexturesListener_isStatic() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertNotNull("RenderContext must exist", renderContextClass);
    Class<?> listenerType = findInnerClass(renderContextClass, "UnusedTexturesListener");
    assertNotNull("UnusedTexturesListener must exist in RenderContext", listenerType);
    try {
      Method m = cacheClass.getDeclaredMethod("removeUnusedTexturesListener", listenerType);
      assertTrue("removeUnusedTexturesListener must be static",
          Modifier.isStatic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("GlResourceCache must have removeUnusedTexturesListener(" +
          "RenderContext.UnusedTexturesListener)");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 4. GlResourceCache — display list instance methods
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void glResourceCache_hasGetDisplayListID() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertNotNull("GlrGeometry must exist", glrGeometryClass);
    assertMethodExists(cacheClass, "getDisplayListID",
        Integer.class, glrGeometryClass);
  }

  @Test
  public void glResourceCache_hasGenerateDisplayListID() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertNotNull("GlrGeometry must exist", glrGeometryClass);
    assertNotNull("RenderContext must exist", renderContextClass);
    assertMethodExists(cacheClass, "generateDisplayListID",
        Integer.class, glrGeometryClass, renderContextClass);
  }

  @Test
  public void glResourceCache_hasForgetGeometryAdapter_3arg() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertNotNull("GlrGeometry must exist", glrGeometryClass);
    assertNotNull("RenderContext must exist", renderContextClass);
    assertMethodExists(cacheClass, "forgetGeometryAdapter",
        void.class, glrGeometryClass, boolean.class, renderContextClass);
  }

  @Test
  public void glResourceCache_hasForgetGeometryAdapter_2arg() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertNotNull("GlrGeometry must exist", glrGeometryClass);
    assertNotNull("RenderContext must exist", renderContextClass);
    assertMethodExists(cacheClass, "forgetGeometryAdapter",
        void.class, glrGeometryClass, renderContextClass);
  }

  @Test
  public void glResourceCache_hasActuallyForgetDisplayListsIfNecessary() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertNotNull("RenderContext must exist", renderContextClass);
    assertMethodExists(cacheClass, "actuallyForgetDisplayListsIfNecessary",
        void.class, renderContextClass);
  }

  // ══════════════════════════════════════════════════════════════════
  // 5. GlResourceCache — texture binding instance methods
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void glResourceCache_hasForgetTextureAdapter_3arg() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertNotNull("GlrTexture must exist", glrTextureClass);
    assertNotNull("RenderContext must exist", renderContextClass);
    assertMethodExists(cacheClass, "forgetTextureAdapter",
        void.class, glrTextureClass, boolean.class, renderContextClass);
  }

  @Test
  public void glResourceCache_hasForgetTextureAdapter_2arg() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertNotNull("GlrTexture must exist", glrTextureClass);
    assertNotNull("RenderContext must exist", renderContextClass);
    assertMethodExists(cacheClass, "forgetTextureAdapter",
        void.class, glrTextureClass, renderContextClass);
  }

  @Test
  public void glResourceCache_hasActuallyForgetTexturesIfNecessary() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertNotNull("RenderContext must exist", renderContextClass);
    assertMethodExists(cacheClass, "actuallyForgetTexturesIfNecessary",
        void.class, renderContextClass);
  }

  // ══════════════════════════════════════════════════════════════════
  // 6. GlResourceCache — bulk operation methods
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void glResourceCache_hasForgetAllCachedItems() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertNotNull("RenderContext must exist", renderContextClass);
    assertMethodExists(cacheClass, "forgetAllCachedItems",
        void.class, renderContextClass);
  }

  @Test
  public void glResourceCache_hasClearUnusedTextures_static() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    try {
      Class<?> glClass = Class.forName("com.jogamp.opengl.GL");
      Method m = cacheClass.getDeclaredMethod("clearUnusedTextures", glClass);
      assertTrue("clearUnusedTextures must be static",
          Modifier.isStatic(m.getModifiers()));
    } catch (ClassNotFoundException e) {
      fail("com.jogamp.opengl.GL must be on classpath");
    } catch (NoSuchMethodException e) {
      fail("GlResourceCache must have static clearUnusedTextures(GL)");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 7. GlResourceCache — private internal methods
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void glResourceCache_hasForgetAllGeometryAdapters_private() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertNotNull("RenderContext must exist", renderContextClass);
    assertPrivateMethodExists(cacheClass, "forgetAllGeometryAdapters",
        renderContextClass);
  }

  @Test
  public void glResourceCache_hasForgetAllTextureAdapters_private() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertNotNull("RenderContext must exist", renderContextClass);
    assertPrivateMethodExists(cacheClass, "forgetAllTextureAdapters",
        renderContextClass);
  }

  @Test
  public void glResourceCache_hasForgetTextureBindingID_private() {
    assertNotNull("GlResourceCache must exist", cacheClass);
    assertNotNull("GlrTexture must exist", glrTextureClass);
    assertNotNull("RenderContext must exist", renderContextClass);
    try {
      Class<?> forgettableBindingClass =
          Class.forName("edu.cmu.cs.dennisc.render.gl.ForgettableBinding");
      Method m = cacheClass.getDeclaredMethod("forgetTextureBindingID",
          glrTextureClass, forgettableBindingClass, boolean.class,
          renderContextClass);
      assertTrue("forgetTextureBindingID must be private",
          Modifier.isPrivate(m.getModifiers()));
    } catch (ClassNotFoundException e) {
      fail("ForgettableBinding must be on classpath");
    } catch (NoSuchMethodException e) {
      fail("GlResourceCache must have private forgetTextureBindingID(...)");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 8. RenderContext — retains UnusedTexturesListener interface
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderContext_retainsUnusedTexturesListener() {
    assertNotNull("RenderContext must exist", renderContextClass);
    Class<?> listenerType = findInnerClass(renderContextClass, "UnusedTexturesListener");
    assertNotNull("UnusedTexturesListener must remain in RenderContext",
        listenerType);
    assertTrue("UnusedTexturesListener must be public",
        Modifier.isPublic(listenerType.getModifiers()));
    assertTrue("UnusedTexturesListener must be an interface",
        listenerType.isInterface());
  }

  @Test
  public void renderContext_unusedTexturesListener_hasCallback() {
    assertNotNull("RenderContext must exist", renderContextClass);
    Class<?> listenerType = findInnerClass(renderContextClass, "UnusedTexturesListener");
    assertNotNull("UnusedTexturesListener must exist", listenerType);
    try {
      Class<?> glClass = Class.forName("com.jogamp.opengl.GL");
      listenerType.getMethod("unusedTexturesCleared", glClass);
    } catch (ClassNotFoundException e) {
      fail("com.jogamp.opengl.GL must be on classpath");
    } catch (NoSuchMethodException e) {
      fail("UnusedTexturesListener must have unusedTexturesCleared(GL)");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 9. RenderContext — has resourceCache delegate field
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderContext_hasResourceCacheField() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertNotNull("GlResourceCache must exist", cacheClass);
    try {
      Field f = renderContextClass.getDeclaredField("resourceCache");
      assertEquals("resourceCache must be of type GlResourceCache",
          cacheClass, f.getType());
      assertTrue("resourceCache must be final",
          Modifier.isFinal(f.getModifiers()));
      assertTrue("resourceCache must be private",
          Modifier.isPrivate(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("RenderContext must have a 'resourceCache' field of type GlResourceCache");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 10. RenderContext — forwarding wrappers preserve public API
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderContext_retainsGetDisplayListID() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertNotNull("GlrGeometry must exist", glrGeometryClass);
    assertPublicMethodExists(renderContextClass, "getDisplayListID",
        Integer.class, glrGeometryClass);
  }

  @Test
  public void renderContext_retainsGenerateDisplayListID() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertNotNull("GlrGeometry must exist", glrGeometryClass);
    assertPublicMethodExists(renderContextClass, "generateDisplayListID",
        Integer.class, glrGeometryClass);
  }

  @Test
  public void renderContext_retainsForgetGeometryAdapter_2arg() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertNotNull("GlrGeometry must exist", glrGeometryClass);
    assertPublicMethodExists(renderContextClass, "forgetGeometryAdapter",
        void.class, glrGeometryClass, boolean.class);
  }

  @Test
  public void renderContext_retainsForgetGeometryAdapter_1arg() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertNotNull("GlrGeometry must exist", glrGeometryClass);
    assertPublicMethodExists(renderContextClass, "forgetGeometryAdapter",
        void.class, glrGeometryClass);
  }

  @Test
  public void renderContext_retainsForgetTextureAdapter_2arg() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertNotNull("GlrTexture must exist", glrTextureClass);
    assertPublicMethodExists(renderContextClass, "forgetTextureAdapter",
        void.class, glrTextureClass, boolean.class);
  }

  @Test
  public void renderContext_retainsForgetTextureAdapter_1arg() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertNotNull("GlrTexture must exist", glrTextureClass);
    assertPublicMethodExists(renderContextClass, "forgetTextureAdapter",
        void.class, glrTextureClass);
  }

  @Test
  public void renderContext_retainsActuallyForgetTexturesIfNecessary() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertPublicMethodExists(renderContextClass,
        "actuallyForgetTexturesIfNecessary", void.class);
  }

  @Test
  public void renderContext_retainsActuallyForgetDisplayListsIfNecessary() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertPublicMethodExists(renderContextClass,
        "actuallyForgetDisplayListsIfNecessary", void.class);
  }

  @Test
  public void renderContext_retainsForgetAllCachedItems() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertPublicMethodExists(renderContextClass,
        "forgetAllCachedItems", void.class);
  }

  @Test
  public void renderContext_retainsClearUnusedTextures() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertPublicMethodExists(renderContextClass,
        "clearUnusedTextures", void.class);
  }

  @Test
  public void renderContext_retainsAddUnusedTexturesListener_static() {
    assertNotNull("RenderContext must exist", renderContextClass);
    Class<?> listenerType = findInnerClass(renderContextClass, "UnusedTexturesListener");
    assertNotNull("UnusedTexturesListener must exist", listenerType);
    try {
      Method m = renderContextClass.getMethod("addUnusedTexturesListener",
          listenerType);
      assertTrue("addUnusedTexturesListener must be static on RenderContext",
          Modifier.isStatic(m.getModifiers()));
      assertTrue("addUnusedTexturesListener must be public on RenderContext",
          Modifier.isPublic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("RenderContext must retain public static addUnusedTexturesListener");
    }
  }

  @Test
  public void renderContext_retainsRemoveUnusedTexturesListener_static() {
    assertNotNull("RenderContext must exist", renderContextClass);
    Class<?> listenerType = findInnerClass(renderContextClass, "UnusedTexturesListener");
    assertNotNull("UnusedTexturesListener must exist", listenerType);
    try {
      Method m = renderContextClass.getMethod("removeUnusedTexturesListener",
          listenerType);
      assertTrue("removeUnusedTexturesListener must be static on RenderContext",
          Modifier.isStatic(m.getModifiers()));
      assertTrue("removeUnusedTexturesListener must be public on RenderContext",
          Modifier.isPublic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("RenderContext must retain public static removeUnusedTexturesListener");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 11. RenderContext — fields moved out (no longer on RenderContext)
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderContext_noDisplayListMap() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertFieldAbsent(renderContextClass, "displayListMap",
        "displayListMap should be moved to GlResourceCache");
  }

  @Test
  public void renderContext_noTextureBindingMap() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertFieldAbsent(renderContextClass, "textureBindingMap",
        "textureBindingMap should be moved to GlResourceCache");
  }

  @Test
  public void renderContext_noToBeForgottenDisplayLists() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertFieldAbsent(renderContextClass, "toBeForgottenDisplayLists",
        "toBeForgottenDisplayLists should be moved to GlResourceCache");
  }

  @Test
  public void renderContext_noToBeForgottenTextures() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertFieldAbsent(renderContextClass, "toBeForgottenTextures",
        "toBeForgottenTextures should be moved to GlResourceCache");
  }

  @Test
  public void renderContext_noUnusedTexturesListeners() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertFieldAbsent(renderContextClass, "unusedTexturesListeners",
        "unusedTexturesListeners should be moved to GlResourceCache");
  }

  @Test
  public void renderContext_noForgetAllGeometryAdapters() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertMethodAbsent(renderContextClass, "forgetAllGeometryAdapters",
        "forgetAllGeometryAdapters should be moved to GlResourceCache");
  }

  @Test
  public void renderContext_noForgetAllTextureAdapters() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertMethodAbsent(renderContextClass, "forgetAllTextureAdapters",
        "forgetAllTextureAdapters should be moved to GlResourceCache");
  }

  @Test
  public void renderContext_noForgetTextureBindingID() {
    assertNotNull("RenderContext must exist", renderContextClass);
    assertMethodAbsent(renderContextClass, "forgetTextureBindingID",
        "forgetTextureBindingID should be moved to GlResourceCache");
  }

  // ══════════════════════════════════════════════════════════════════
  // 12. RenderContext — line count under 500
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderContext_lineCount_under500() throws Exception {
    Path sourceFile = findSourceFile(SRC_DIR + "RenderContext.java");
    assertNotNull("Must find RenderContext.java", sourceFile);
    long lineCount = Files.lines(sourceFile).count();
    assertTrue(
        "RenderContext.java must be under 500 lines (actual: "
            + lineCount + ")",
        lineCount < 500);
  }

  // ══════════════════════════════════════════════════════════════════
  // 13. Bug fix — removeUnusedTexturesListener uses .remove()
  //     Verified structurally: if the listener list is on
  //     GlResourceCache and tests 3 pass, the .add() bug is gone.
  //     This test verifies through the public API forwarding wrapper.
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderContext_removeListener_doesNotDoubleAdd() {
    // Structural verification: if GlResourceCache exists and has
    // removeUnusedTexturesListener, the method source must call .remove()
    // not .add(). We verify this indirectly through source file content.
    assertNotNull("GlResourceCache must exist", cacheClass);
    Path sourceFile = findSourceFile(SRC_DIR + "GlResourceCache.java");
    assertNotNull("Must find GlResourceCache.java", sourceFile);
    try {
      String content = new String(Files.readAllBytes(sourceFile));
      // The removeUnusedTexturesListener method must call .remove(), not .add()
      assertTrue(
          "GlResourceCache.removeUnusedTexturesListener must call .remove() on the list",
          content.contains("unusedTexturesListeners.remove(listener)"));
      // Ensure it does NOT contain the bug pattern (add in remove method)
      assertFalse(
          "removeUnusedTexturesListener must NOT call .add() (the original bug)",
          containsBugPattern(content));
    } catch (Exception e) {
      fail("Could not read GlResourceCache.java: " + e.getMessage());
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 14. GlResourceCache source file exists
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void glResourceCache_sourceFileExists() {
    Path sourceFile = findSourceFile(SRC_DIR + "GlResourceCache.java");
    assertNotNull("GlResourceCache.java source file must exist at " +
        SRC_DIR + "GlResourceCache.java", sourceFile);
  }

  @Test
  public void glResourceCache_lineCount_reasonable() throws Exception {
    Path sourceFile = findSourceFile(SRC_DIR + "GlResourceCache.java");
    assertNotNull("Must find GlResourceCache.java", sourceFile);
    long lineCount = Files.lines(sourceFile).count();
    assertTrue(
        "GlResourceCache.java should be between 80 and 200 lines (actual: "
            + lineCount + ")",
        lineCount >= 80 && lineCount <= 200);
  }

  // ══════════════════════════════════════════════════════════════════
  // Helper: check for the bug pattern where removeUnused... calls .add()
  // ══════════════════════════════════════════════════════════════════

  /**
   * Detects the original bug where removeUnusedTexturesListener
   * calls unusedTexturesListeners.add(listener) instead of .remove().
   * Scans a narrow window around the method signature.
   */
  private boolean containsBugPattern(String content) {
    String[] lines = content.split("\n");
    for (int i = 0; i < lines.length; i++) {
      if (lines[i].contains("removeUnusedTexturesListener")) {
        // Check the next 3 lines for .add(listener)
        for (int j = i; j < Math.min(i + 4, lines.length); j++) {
          if (lines[j].contains(".add(listener)")) {
            return true;
          }
        }
      }
    }
    return false;
  }

  // ══════════════════════════════════════════════════════════════════
  // Shared assertion helpers
  // ══════════════════════════════════════════════════════════════════

  private void assertFieldDeclared(Class<?> clazz, String fieldName,
      Class<?> expectedType) {
    try {
      Field f = clazz.getDeclaredField(fieldName);
      assertTrue(fieldName + " must be assignable to " + expectedType.getSimpleName(),
          expectedType.isAssignableFrom(f.getType()));
    } catch (NoSuchFieldException e) {
      fail(clazz.getSimpleName() + " must have field '" + fieldName + "'");
    }
  }

  private void assertFieldIsFinal(Class<?> clazz, String fieldName) {
    try {
      Field f = clazz.getDeclaredField(fieldName);
      assertTrue("'" + fieldName + "' must be final",
          Modifier.isFinal(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail(clazz.getSimpleName() + " must have field '" + fieldName + "'");
    }
  }

  private void assertFieldIsStatic(Class<?> clazz, String fieldName) {
    try {
      Field f = clazz.getDeclaredField(fieldName);
      assertTrue("'" + fieldName + "' must be static",
          Modifier.isStatic(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail(clazz.getSimpleName() + " must have field '" + fieldName + "'");
    }
  }

  private void assertFieldAbsent(Class<?> clazz, String fieldName,
      String reason) {
    try {
      clazz.getDeclaredField(fieldName);
      fail("Field '" + fieldName + "' should not exist in "
          + clazz.getSimpleName() + ": " + reason);
    } catch (NoSuchFieldException e) {
      // expected
    }
  }

  private void assertMethodExists(Class<?> clazz, String name,
      Class<?> returnType, Class<?>... params) {
    try {
      Method m = clazz.getDeclaredMethod(name, params);
      assertEquals(name + " return type", returnType, m.getReturnType());
    } catch (NoSuchMethodException e) {
      fail(clazz.getSimpleName() + " must have " + name + "(...)");
    }
  }

  private void assertPublicMethodExists(Class<?> clazz, String name,
      Class<?> returnType, Class<?>... params) {
    try {
      Method m = clazz.getDeclaredMethod(name, params);
      assertEquals(name + " return type", returnType, m.getReturnType());
      assertTrue(name + " must be public",
          Modifier.isPublic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail(clazz.getSimpleName() + " must have public " + name + "(...)");
    }
  }

  private void assertPrivateMethodExists(Class<?> clazz, String name,
      Class<?>... params) {
    try {
      Method m = clazz.getDeclaredMethod(name, params);
      assertTrue(name + " must be private",
          Modifier.isPrivate(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail(clazz.getSimpleName() + " must have private " + name + "(...)");
    }
  }

  private void assertMethodAbsent(Class<?> clazz, String name,
      String reason) {
    for (Method m : clazz.getDeclaredMethods()) {
      if (m.getName().equals(name)) {
        fail("Method '" + name + "' should not exist in "
            + clazz.getSimpleName() + ": " + reason);
      }
    }
  }

  private Class<?> findInnerClass(Class<?> clazz, String simpleName) {
    for (Class<?> inner : clazz.getDeclaredClasses()) {
      if (simpleName.equals(inner.getSimpleName())) {
        return inner;
      }
    }
    return null;
  }

  private Path findSourceFile(String relativePath) {
    Path cwd = Paths.get(System.getProperty("user.dir"));
    for (Path p = cwd; p != null; p = p.getParent()) {
      Path candidate = p.resolve(relativePath);
      if (Files.exists(candidate)) {
        return candidate;
      }
      if (p.equals(p.getRoot())) {
        break;
      }
    }
    return null;
  }
}
