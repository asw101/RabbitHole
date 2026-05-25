package edu.cmu.cs.dennisc.render.joglrenderer;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Color;
import java.awt.font.GlyphVector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TDD contract tests for issues #514 and #524: Extract inner classes
 * from NonCachingTextRenderer into separate top-level files.
 *
 * Written BEFORE implementation — all tests FAIL initially.
 * They pass once extraction is complete.
 *
 * Contract groups:
 *   1. TextRendererGlyph – extracted from Glyph inner class (#514)
 *   2. TextRendererGlyphProducer – extracted from GlyphProducer inner class (#514)
 *   3. TextRendererQuadRenderer – extracted from Pipelined_QuadRenderer inner class (#514)
 *   4. Inner classes removed from NonCachingTextRenderer
 *   5. Remaining inner classes preserved (none after #524)
 *   6. NonCachingTextRenderer members widened from private → package-private
 *   7. Field type changes for mGlyphProducer / mPipelinedQuadRenderer
 *   8. NonCachingTextRenderer line count under 500
 *   9. CharSequenceIterator – extracted top-level class (#524)
 *  10. TextData – extracted top-level class (#524)
 *  11. Manager – extracted top-level class (#524)
 *  12. DefaultRenderDelegate – extracted top-level class (#524)
 *  13. CharacterCache – extracted top-level class (#524)
 *  14. DebugListener – extracted top-level class (#524)
 *  15. Additional widened fields for Manager back-references (#524)
 *  16. TextRendererPipeline – extracted delegate for rendering pipeline (#537)
 *  17. Pipeline integration on NonCachingTextRenderer (#537)
 *  18. Fields widened for pipeline delegate access (#537)
 *  19. TextRendererProperties – extracted delegate for properties/dispose/bounds (#543)
 *  20. Properties integration on NonCachingTextRenderer (#543)
 *  21. Fields widened for properties delegate access (#543)
 */
public class InnerClassExtractionContractTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  private static final String PKG = "edu.cmu.cs.dennisc.render.joglrenderer";

  private static Class<?> glyphClass;
  private static Class<?> glyphProducerClass;
  private static Class<?> quadRendererClass;
  private static Class<?> charSeqIterClass;
  private static Class<?> textDataClass;
  private static Class<?> managerClass;
  private static Class<?> defaultRenderDelegateClass;
  private static Class<?> characterCacheClass;
  private static Class<?> debugListenerClass;
  private static Class<?> pipelineClass;
  private static Class<?> propertiesClass;

  @BeforeClass
  public static void resolveExtractedClasses() {
    glyphClass = tryLoad(PKG + ".TextRendererGlyph");
    glyphProducerClass = tryLoad(PKG + ".TextRendererGlyphProducer");
    quadRendererClass = tryLoad(PKG + ".TextRendererQuadRenderer");
    charSeqIterClass = tryLoad(PKG + ".CharSequenceIterator");
    textDataClass = tryLoad(PKG + ".TextData");
    managerClass = tryLoad(PKG + ".Manager");
    defaultRenderDelegateClass = tryLoad(PKG + ".DefaultRenderDelegate");
    characterCacheClass = tryLoad(PKG + ".CharacterCache");
    debugListenerClass = tryLoad(PKG + ".DebugListener");
    pipelineClass = tryLoad(PKG + ".TextRendererPipeline");
    propertiesClass = tryLoad(PKG + ".TextRendererProperties");
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

  // ── 1. TextRendererGlyph ──────────────────────────────────────────

  @Test
  public void textRendererGlyph_classExists() {
    assertNotNull("TextRendererGlyph must exist as a top-level class", glyphClass);
  }

  @Test
  public void textRendererGlyph_isPackagePrivate() {
    assertNotNull("class must exist", glyphClass);
    assertTrue("must be package-private",
        isPackagePrivate(glyphClass.getModifiers()));
  }

  @Test
  public void textRendererGlyph_hasSuppressWarningsCheckStyle() throws Exception {
    assertNotNull("class must exist", glyphClass);
    assertSourceContainsSuppressWarnings("TextRendererGlyph.java");
  }

  @Test
  public void textRendererGlyph_hasTextRendererField() {
    assertNotNull("class must exist", glyphClass);
    assertFieldExists(glyphClass, "textRenderer", NonCachingTextRenderer.class);
  }

  @Test
  public void textRendererGlyph_hasProducerFieldOfExtractedType() {
    assertNotNull("TextRendererGlyph must exist", glyphClass);
    assertNotNull("TextRendererGlyphProducer must exist", glyphProducerClass);
    assertFieldExists(glyphClass, "producer", glyphProducerClass);
  }

  @Test
  public void textRendererGlyph_hasUnicodeConstructor() {
    assertNotNull("TextRendererGlyph must exist", glyphClass);
    assertNotNull("TextRendererGlyphProducer must exist", glyphProducerClass);
    assertConstructorExists(glyphClass,
        "constructor(int, int, float, GlyphVector, TextRendererGlyphProducer, NonCachingTextRenderer)",
        int.class, int.class, float.class, GlyphVector.class,
        glyphProducerClass, NonCachingTextRenderer.class);
  }

  @Test
  public void textRendererGlyph_hasStringConstructor() {
    assertNotNull("TextRendererGlyph must exist", glyphClass);
    assertConstructorExists(glyphClass,
        "constructor(String, boolean, NonCachingTextRenderer)",
        String.class, boolean.class, NonCachingTextRenderer.class);
  }

  @Test
  public void textRendererGlyph_hasPublicGetUnicodeID() {
    assertNotNull("class must exist", glyphClass);
    assertPublicMethod(glyphClass, "getUnicodeID", int.class);
  }

  @Test
  public void textRendererGlyph_hasPublicGetGlyphCode() {
    assertNotNull("class must exist", glyphClass);
    assertPublicMethod(glyphClass, "getGlyphCode", int.class);
  }

  @Test
  public void textRendererGlyph_hasPublicGetAdvance() {
    assertNotNull("class must exist", glyphClass);
    assertPublicMethod(glyphClass, "getAdvance", float.class);
  }

  @Test
  public void textRendererGlyph_hasPublicDraw3D() {
    assertNotNull("class must exist", glyphClass);
    assertPublicMethodWithParams(glyphClass, "draw3D", float.class,
        float.class, float.class, float.class, float.class);
  }

  @Test
  public void textRendererGlyph_hasPublicClear() {
    assertNotNull("class must exist", glyphClass);
    assertPublicMethod(glyphClass, "clear", void.class);
  }

  // ── 2. TextRendererGlyphProducer ──────────────────────────────────

  @Test
  public void textRendererGlyphProducer_classExists() {
    assertNotNull("TextRendererGlyphProducer must exist as a top-level class",
        glyphProducerClass);
  }

  @Test
  public void textRendererGlyphProducer_isPackagePrivate() {
    assertNotNull("class must exist", glyphProducerClass);
    assertTrue("must be package-private",
        isPackagePrivate(glyphProducerClass.getModifiers()));
  }

  @Test
  public void textRendererGlyphProducer_hasSuppressWarningsCheckStyle() throws Exception {
    assertNotNull("class must exist", glyphProducerClass);
    assertSourceContainsSuppressWarnings("TextRendererGlyphProducer.java");
  }

  @Test
  public void textRendererGlyphProducer_hasTextRendererField() {
    assertNotNull("class must exist", glyphProducerClass);
    assertFieldExists(glyphProducerClass, "textRenderer", NonCachingTextRenderer.class);
  }

  @Test
  public void textRendererGlyphProducer_hasConstructor() {
    assertNotNull("class must exist", glyphProducerClass);
    assertConstructorExists(glyphProducerClass,
        "constructor(int, NonCachingTextRenderer)",
        int.class, NonCachingTextRenderer.class);
  }

  @Test
  public void textRendererGlyphProducer_undefinedConstant_isNegativeTwo() {
    assertNotNull("class must exist", glyphProducerClass);
    try {
      Field f = glyphProducerClass.getDeclaredField("undefined");
      f.setAccessible(true);
      assertTrue("undefined must be static", Modifier.isStatic(f.getModifiers()));
      assertEquals("undefined must be -2", -2, f.getInt(null));
    } catch (NoSuchFieldException | IllegalAccessException e) {
      fail("TextRendererGlyphProducer must have static int undefined = -2");
    }
  }

  @Test
  public void textRendererGlyphProducer_glyphCacheComponentType() {
    assertNotNull("TextRendererGlyphProducer must exist", glyphProducerClass);
    assertNotNull("TextRendererGlyph must exist", glyphClass);
    try {
      Field f = glyphProducerClass.getDeclaredField("glyphCache");
      assertTrue("glyphCache must be an array", f.getType().isArray());
      assertEquals("glyphCache element type must be TextRendererGlyph",
          glyphClass, f.getType().getComponentType());
    } catch (NoSuchFieldException e) {
      fail("glyphCache field must exist in TextRendererGlyphProducer");
    }
  }

  @Test
  public void textRendererGlyphProducer_iterFieldType() {
    assertNotNull("class must exist", glyphProducerClass);
    assertNotNull("CharSequenceIterator must exist as top-level", charSeqIterClass);
    try {
      Field f = glyphProducerClass.getDeclaredField("iter");
      assertEquals("iter must be CharSequenceIterator (top-level)",
          charSeqIterClass, f.getType());
    } catch (NoSuchFieldException e) {
      fail("iter field of type CharSequenceIterator must exist: " + e);
    }
  }

  @Test
  public void textRendererGlyphProducer_hasPublicGetGlyphs() {
    assertNotNull("class must exist", glyphProducerClass);
    assertPublicMethodWithParams(glyphProducerClass, "getGlyphs",
        java.util.List.class, CharSequence.class);
  }

  @Test
  public void textRendererGlyphProducer_hasPublicRegister() {
    assertNotNull("TextRendererGlyphProducer must exist", glyphProducerClass);
    assertNotNull("TextRendererGlyph must exist", glyphClass);
    try {
      Method m = glyphProducerClass.getDeclaredMethod("register", glyphClass);
      assertTrue("register must be public", Modifier.isPublic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("register(TextRendererGlyph) must exist");
    }
  }

  @Test
  public void textRendererGlyphProducer_hasPublicClearCacheEntry() {
    assertNotNull("class must exist", glyphProducerClass);
    assertPublicMethodWithParams(glyphProducerClass, "clearCacheEntry",
        void.class, int.class);
  }

  @Test
  public void textRendererGlyphProducer_hasPublicClearAllCacheEntries() {
    assertNotNull("class must exist", glyphProducerClass);
    assertPublicMethod(glyphProducerClass, "clearAllCacheEntries", void.class);
  }

  @Test
  public void textRendererGlyphProducer_hasPublicGetGlyphPixelWidth() {
    assertNotNull("class must exist", glyphProducerClass);
    assertPublicMethodWithParams(glyphProducerClass, "getGlyphPixelWidth",
        float.class, char.class);
  }

  // ── 3. TextRendererQuadRenderer ───────────────────────────────────

  @Test
  public void textRendererQuadRenderer_classExists() {
    assertNotNull("TextRendererQuadRenderer must exist as a top-level class",
        quadRendererClass);
  }

  @Test
  public void textRendererQuadRenderer_isPackagePrivate() {
    assertNotNull("class must exist", quadRendererClass);
    assertTrue("must be package-private",
        isPackagePrivate(quadRendererClass.getModifiers()));
  }

  @Test
  public void textRendererQuadRenderer_hasSuppressWarningsCheckStyle() throws Exception {
    assertNotNull("class must exist", quadRendererClass);
    assertSourceContainsSuppressWarnings("TextRendererQuadRenderer.java");
  }

  @Test
  public void textRendererQuadRenderer_hasTextRendererField() {
    assertNotNull("class must exist", quadRendererClass);
    assertFieldExists(quadRendererClass, "textRenderer", NonCachingTextRenderer.class);
  }

  @Test
  public void textRendererQuadRenderer_hasConstructor() {
    assertNotNull("class must exist", quadRendererClass);
    assertConstructorExists(quadRendererClass,
        "constructor(NonCachingTextRenderer)",
        NonCachingTextRenderer.class);
  }

  @Test
  public void textRendererQuadRenderer_hasPublicGlTexCoord2f() {
    assertNotNull("class must exist", quadRendererClass);
    assertPublicMethodWithParams(quadRendererClass, "glTexCoord2f",
        void.class, float.class, float.class);
  }

  @Test
  public void textRendererQuadRenderer_hasPublicGlVertex3f() {
    assertNotNull("class must exist", quadRendererClass);
    assertPublicMethodWithParams(quadRendererClass, "glVertex3f",
        void.class, float.class, float.class, float.class);
  }

  @Test
  public void textRendererQuadRenderer_drawIsPackagePrivate() {
    assertNotNull("class must exist", quadRendererClass);
    try {
      Method m = quadRendererClass.getDeclaredMethod("draw");
      assertTrue("draw() must be package-private (called from flushGlyphPipeline)",
          isPackagePrivate(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("draw() must exist in TextRendererQuadRenderer");
    }
  }

  @Test
  public void textRendererQuadRenderer_hasPublicDispose() {
    assertNotNull("class must exist", quadRendererClass);
    assertPublicMethod(quadRendererClass, "dispose", void.class);
  }

  // ── 4. Inner classes removed from NonCachingTextRenderer ──────────

  @Test
  public void innerClass_Glyph_removed() {
    assertInnerClassAbsent("Glyph");
  }

  @Test
  public void innerClass_GlyphProducer_removed() {
    assertInnerClassAbsent("GlyphProducer");
  }

  @Test
  public void innerClass_Pipelined_QuadRenderer_removed() {
    assertInnerClassAbsent("Pipelined_QuadRenderer");
  }

  // ── 5. Remaining inner classes — all extracted after #524 ──────────

  @Test
  public void innerClass_CharSequenceIterator_removed() {
    assertInnerClassAbsent("CharSequenceIterator");
  }

  @Test
  public void innerClass_TextData_removed() {
    assertInnerClassAbsent("TextData");
  }

  @Test
  public void innerClass_DefaultRenderDelegate_removed() {
    assertInnerClassAbsent("DefaultRenderDelegate");
  }

  @Test
  public void innerClass_CharacterCache_removed() {
    assertInnerClassAbsent("CharacterCache");
  }

  @Test
  public void innerClass_Manager_removed() {
    assertInnerClassAbsent("Manager");
  }

  @Test
  public void innerClass_DebugListener_removed() {
    assertInnerClassAbsent("DebugListener");
  }

  // ── 6. NonCachingTextRenderer members widened ─────────────────────

  @Test
  public void field_font_isPackagePrivateFinal() {
    assertFieldWidened("font");
    assertFieldFinal("font");
  }

  @Test
  public void field_packer_isPackagePrivate() {
    assertFieldWidened("packer");
  }

  @Test
  public void field_renderDelegate_isPackagePrivateFinal() {
    assertFieldWidened("renderDelegate");
    assertFieldFinal("renderDelegate");
  }

  @Test
  public void field_singleUnicode_isPackagePrivateFinal() {
    assertFieldWidened("singleUnicode");
    assertFieldFinal("singleUnicode");
  }

  @Test
  public void field_DISABLE_GLYPH_CACHE_isPackagePrivateStatic() {
    assertFieldWidened("DISABLE_GLYPH_CACHE");
    assertFieldStatic("DISABLE_GLYPH_CACHE");
  }

  @Test
  public void field_DRAW_BBOXES_isPackagePrivateStatic() {
    assertFieldWidened("DRAW_BBOXES");
    assertFieldStatic("DRAW_BBOXES");
  }

  @Test
  public void field_isExtensionAvailable_GL_VERSION_1_5_isPackagePrivate() {
    assertFieldWidened("isExtensionAvailable_GL_VERSION_1_5");
  }

  @Test
  public void method_getBackingStore_isPackagePrivate() {
    assertMethodWidened("getBackingStore");
  }

  @Test
  public void method_getGraphics2D_isPackagePrivate() {
    assertMethodWidened("getGraphics2D");
  }

  @Test
  public void method_getFontRenderContext_remainsPublic() {
    // getFontRenderContext is already public — extraction should not change it
    try {
      Method m = NonCachingTextRenderer.class.getDeclaredMethod("getFontRenderContext");
      assertTrue("getFontRenderContext should remain public",
          Modifier.isPublic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("getFontRenderContext must exist");
    }
  }

  @Test
  public void method_normalize_isPackagePrivate() {
    assertMethodWidened("normalize");
  }

  @Test
  public void method_preNormalize_isStaticPackagePrivate() {
    try {
      Method m = NonCachingTextRenderer.class.getDeclaredMethod(
          "preNormalize", java.awt.geom.Rectangle2D.class);
      assertTrue("preNormalize must be static",
          Modifier.isStatic(m.getModifiers()));
      assertTrue("preNormalize must be package-private",
          isPackagePrivate(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("preNormalize(Rectangle2D) must exist");
    }
  }

  @Test
  public void method_draw3D_ROBUST_isPackagePrivate() {
    assertMethodWidened("draw3D_ROBUST");
  }

  @Test
  public void method_is15Available_isPackagePrivate() {
    assertMethodWidened("is15Available");
  }

  // ── 7. Field type changes ─────────────────────────────────────────

  @Test
  public void field_mGlyphProducer_typeIsTextRendererGlyphProducer() {
    assertNotNull("TextRendererGlyphProducer must exist", glyphProducerClass);
    try {
      Field f = NonCachingTextRenderer.class.getDeclaredField("mGlyphProducer");
      assertEquals("mGlyphProducer type must be TextRendererGlyphProducer",
          glyphProducerClass, f.getType());
    } catch (NoSuchFieldException e) {
      fail("mGlyphProducer field must exist in NonCachingTextRenderer");
    }
  }

  @Test
  public void field_mPipelinedQuadRenderer_typeIsTextRendererQuadRenderer() {
    assertNotNull("TextRendererQuadRenderer must exist", quadRendererClass);
    try {
      Field f = NonCachingTextRenderer.class.getDeclaredField("mPipelinedQuadRenderer");
      assertEquals("mPipelinedQuadRenderer type must be TextRendererQuadRenderer",
          quadRendererClass, f.getType());
    } catch (NoSuchFieldException e) {
      fail("mPipelinedQuadRenderer field must exist in NonCachingTextRenderer");
    }
  }

  // ── 8. Line count ─────────────────────────────────────────────────

  @Test
  public void nonCachingTextRenderer_lineCount_under500() throws Exception {
    Path sourceFile = findSourceFile(
        "core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/"
            + "NonCachingTextRenderer.java");
    assertNotNull("Must find NonCachingTextRenderer.java", sourceFile);
    long lineCount = Files.lines(sourceFile).count();
    // After extracting 9 inner classes (#514, #524), rendering pipeline
    // methods (#537), and property/dispose/bounds methods (#543), the file
    // drops from ~1800 to under 500 lines.
    assertTrue(
        "NonCachingTextRenderer.java must be under 500 lines (actual: "
            + lineCount + ")",
        lineCount < 500);
  }

  // ── 9. CharSequenceIterator — extracted top-level class ───────────

  @Test
  public void charSequenceIterator_classExists() {
    assertNotNull("CharSequenceIterator must exist as a top-level class",
        charSeqIterClass);
  }

  @Test
  public void charSequenceIterator_isPackagePrivate() {
    assertNotNull("class must exist", charSeqIterClass);
    assertTrue("must be package-private",
        isPackagePrivate(charSeqIterClass.getModifiers()));
  }

  @Test
  public void charSequenceIterator_hasSuppressWarningsCheckStyle() throws Exception {
    assertNotNull("class must exist", charSeqIterClass);
    assertSourceContainsSuppressWarnings("CharSequenceIterator.java");
  }

  @Test
  public void charSequenceIterator_implementsCharacterIterator() {
    assertNotNull("class must exist", charSeqIterClass);
    assertTrue("must implement CharacterIterator",
        java.text.CharacterIterator.class.isAssignableFrom(charSeqIterClass));
  }

  @Test
  public void charSequenceIterator_hasNoArgConstructor() {
    assertNotNull("class must exist", charSeqIterClass);
    assertConstructorExists(charSeqIterClass, "no-arg constructor");
  }

  @Test
  public void charSequenceIterator_hasCharSequenceConstructor() {
    assertNotNull("class must exist", charSeqIterClass);
    assertConstructorExists(charSeqIterClass,
        "constructor(CharSequence)", CharSequence.class);
  }

  // ── 10. TextData — extracted top-level class ──────────────────────

  @Test
  public void textData_classExists() {
    assertNotNull("TextData must exist as a top-level class", textDataClass);
  }

  @Test
  public void textData_isPackagePrivate() {
    assertNotNull("class must exist", textDataClass);
    assertTrue("must be package-private",
        isPackagePrivate(textDataClass.getModifiers()));
  }

  @Test
  public void textData_hasSuppressWarningsCheckStyle() throws Exception {
    assertNotNull("class must exist", textDataClass);
    assertSourceContainsSuppressWarnings("TextData.java");
  }

  @Test
  public void textData_hasConstructor() {
    assertNotNull("class must exist", textDataClass);
    assertConstructorExists(textDataClass,
        "constructor(String, Point, Rectangle2D, int)",
        String.class, java.awt.Point.class,
        java.awt.geom.Rectangle2D.class, int.class);
  }

  @Test
  public void textData_hasUnicodeIDField() {
    assertNotNull("class must exist", textDataClass);
    assertFieldExists(textDataClass, "unicodeID", int.class);
  }

  // ── 11. Manager — extracted top-level class ───────────────────────

  @Test
  public void manager_classExists() {
    assertNotNull("Manager must exist as a top-level class", managerClass);
  }

  @Test
  public void manager_isPackagePrivate() {
    assertNotNull("class must exist", managerClass);
    assertTrue("must be package-private",
        isPackagePrivate(managerClass.getModifiers()));
  }

  @Test
  public void manager_hasSuppressWarningsCheckStyle() throws Exception {
    assertNotNull("class must exist", managerClass);
    assertSourceContainsSuppressWarnings("Manager.java");
  }

  @Test
  public void manager_implementsBackingStoreManager() {
    assertNotNull("class must exist", managerClass);
    assertTrue("must implement BackingStoreManager",
        com.jogamp.opengl.util.packrect.BackingStoreManager.class
            .isAssignableFrom(managerClass));
  }

  @Test
  public void manager_hasTextRendererField() {
    assertNotNull("class must exist", managerClass);
    assertFieldExists(managerClass, "textRenderer",
        NonCachingTextRenderer.class);
  }

  @Test
  public void manager_hasConstructorWithTextRenderer() {
    assertNotNull("class must exist", managerClass);
    assertConstructorExists(managerClass,
        "constructor(NonCachingTextRenderer)",
        NonCachingTextRenderer.class);
  }

  // ── 12. DefaultRenderDelegate — extracted top-level class ─────────

  @Test
  public void defaultRenderDelegate_classExists() {
    assertNotNull("DefaultRenderDelegate must exist as a top-level class",
        defaultRenderDelegateClass);
  }

  @Test
  public void defaultRenderDelegate_isPublic() {
    assertNotNull("class must exist", defaultRenderDelegateClass);
    assertTrue("must be public for API compatibility",
        Modifier.isPublic(defaultRenderDelegateClass.getModifiers()));
  }

  @Test
  public void defaultRenderDelegate_hasSuppressWarningsCheckStyle() throws Exception {
    assertNotNull("class must exist", defaultRenderDelegateClass);
    assertSourceContainsSuppressWarnings("DefaultRenderDelegate.java");
  }

  @Test
  public void defaultRenderDelegate_implementsRenderDelegate() {
    assertNotNull("class must exist", defaultRenderDelegateClass);
    assertTrue("must implement TextRenderer.RenderDelegate",
        com.jogamp.opengl.util.awt.TextRenderer.RenderDelegate.class
            .isAssignableFrom(defaultRenderDelegateClass));
  }

  @Test
  public void defaultRenderDelegate_hasNoArgConstructor() {
    assertNotNull("class must exist", defaultRenderDelegateClass);
    assertConstructorExists(defaultRenderDelegateClass, "no-arg constructor");
  }

  // ── 13. CharacterCache — extracted top-level class ────────────────

  @Test
  public void characterCache_classExists() {
    assertNotNull("CharacterCache must exist as a top-level class",
        characterCacheClass);
  }

  @Test
  public void characterCache_isPackagePrivate() {
    assertNotNull("class must exist", characterCacheClass);
    assertTrue("must be package-private",
        isPackagePrivate(characterCacheClass.getModifiers()));
  }

  @Test
  public void characterCache_hasSuppressWarningsCheckStyle() throws Exception {
    assertNotNull("class must exist", characterCacheClass);
    assertSourceContainsSuppressWarnings("CharacterCache.java");
  }

  @Test
  public void characterCache_hasStaticValueOfMethod() {
    assertNotNull("class must exist", characterCacheClass);
    try {
      Method m = characterCacheClass.getDeclaredMethod("valueOf", char.class);
      assertTrue("valueOf must be static",
          Modifier.isStatic(m.getModifiers()));
      assertEquals("valueOf return type", Character.class, m.getReturnType());
    } catch (NoSuchMethodException e) {
      fail("CharacterCache must have static valueOf(char) method");
    }
  }

  // ── 14. DebugListener — extracted top-level class ─────────────────

  @Test
  public void debugListener_classExists() {
    assertNotNull("DebugListener must exist as a top-level class",
        debugListenerClass);
  }

  @Test
  public void debugListener_isPackagePrivate() {
    assertNotNull("class must exist", debugListenerClass);
    assertTrue("must be package-private",
        isPackagePrivate(debugListenerClass.getModifiers()));
  }

  @Test
  public void debugListener_hasSuppressWarningsCheckStyle() throws Exception {
    assertNotNull("class must exist", debugListenerClass);
    assertSourceContainsSuppressWarnings("DebugListener.java");
  }

  @Test
  public void debugListener_implementsGLEventListener() {
    assertNotNull("class must exist", debugListenerClass);
    assertTrue("must implement GLEventListener",
        com.jogamp.opengl.GLEventListener.class
            .isAssignableFrom(debugListenerClass));
  }

  @Test
  public void debugListener_hasTextRendererField() {
    assertNotNull("class must exist", debugListenerClass);
    assertFieldExists(debugListenerClass, "textRenderer",
        NonCachingTextRenderer.class);
  }

  @Test
  public void debugListener_hasConstructorWithTextRendererGlFrame() {
    assertNotNull("class must exist", debugListenerClass);
    assertConstructorExists(debugListenerClass,
        "constructor(NonCachingTextRenderer, GL, Frame)",
        NonCachingTextRenderer.class,
        com.jogamp.opengl.GL.class, java.awt.Frame.class);
  }

  // ── 15. Additional widened fields for Manager (#524) ──────────────
  // Note: color cache fields (haveCachedColor, cachedR/G/B/A, cachedColor,
  // needToResetColor) and smoothing were widened in #524 for Manager access.
  // In #543 they moved to TextRendererProperties — see sections 19 and 20.

  @Test
  public void field_mipmap_isPackagePrivate() {
    assertFieldWidened("mipmap");
  }

  @Test
  public void field_inBeginEndPair_isPackagePrivate() {
    assertFieldWidened("inBeginEndPair");
  }

  @Test
  public void field_isOrthoMode_isPackagePrivate() {
    assertFieldWidened("isOrthoMode");
  }

  @Test
  public void field_beginRenderingWidth_isPackagePrivate() {
    assertFieldWidened("beginRenderingWidth");
  }

  @Test
  public void field_beginRenderingHeight_isPackagePrivate() {
    assertFieldWidened("beginRenderingHeight");
  }

  @Test
  public void field_beginRenderingDepthTestDisabled_isPackagePrivate() {
    assertFieldWidened("beginRenderingDepthTestDisabled");
  }

  @Test
  public void field_stringLocations_isPackagePrivate() {
    assertFieldWidened("stringLocations");
  }

  @Test
  public void field_mGlyphProducer_isPackagePrivate() {
    assertFieldWidened("mGlyphProducer");
  }

  @Test
  public void method_clearUnusedEntries_isPackagePrivate() {
    assertMethodWidened("clearUnusedEntries");
  }

  @Test
  public void method_flushGlyphPipeline_isPackagePrivate() {
    assertMethodWidened("flushGlyphPipeline");
  }

  // ── 16. TextRendererPipeline — extracted delegate class (#537) ────

  @Test
  public void textRendererPipeline_classExists() {
    assertNotNull("TextRendererPipeline must exist as a top-level class",
        pipelineClass);
  }

  @Test
  public void textRendererPipeline_isPackagePrivate() {
    assertNotNull("class must exist", pipelineClass);
    assertTrue("must be package-private",
        isPackagePrivate(pipelineClass.getModifiers()));
  }

  @Test
  public void textRendererPipeline_hasSuppressWarningsCheckStyle() throws Exception {
    assertNotNull("class must exist", pipelineClass);
    assertSourceContainsSuppressWarnings("TextRendererPipeline.java");
  }

  @Test
  public void textRendererPipeline_sourceFileExists() throws Exception {
    Path sourceFile = findSourceFile(
        "core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/"
            + "TextRendererPipeline.java");
    assertNotNull("TextRendererPipeline.java source file must exist", sourceFile);
  }

  @Test
  public void textRendererPipeline_hasRendererConstructor() {
    assertNotNull("class must exist", pipelineClass);
    assertConstructorExists(pipelineClass,
        "constructor(NonCachingTextRenderer)",
        NonCachingTextRenderer.class);
  }

  @Test
  public void textRendererPipeline_hasBeginRenderingMethod() {
    assertNotNull("class must exist", pipelineClass);
    assertPackagePrivateMethodWithParams(pipelineClass, "beginRendering",
        boolean.class, int.class, int.class, boolean.class);
  }

  @Test
  public void textRendererPipeline_hasEndRenderingMethod() {
    assertNotNull("class must exist", pipelineClass);
    assertPackagePrivateMethodWithParams(pipelineClass, "endRendering",
        boolean.class);
  }

  @Test
  public void textRendererPipeline_hasInternalDraw3DMethod() {
    assertNotNull("class must exist", pipelineClass);
    assertPackagePrivateMethodWithParams(pipelineClass, "internal_draw3D",
        CharSequence.class, float.class, float.class, float.class, float.class);
  }

  @Test
  public void textRendererPipeline_hasFlushGlyphPipelineMethod() {
    assertNotNull("class must exist", pipelineClass);
    try {
      Method m = pipelineClass.getDeclaredMethod("flushGlyphPipeline");
      assertTrue("flushGlyphPipeline must be package-private",
          isPackagePrivate(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("TextRendererPipeline must have flushGlyphPipeline()");
    }
  }

  @Test
  public void textRendererPipeline_hasDraw3D_ROBUSTMethod() {
    assertNotNull("class must exist", pipelineClass);
    assertPackagePrivateMethodWithParams(pipelineClass, "draw3D_ROBUST",
        CharSequence.class, float.class, float.class, float.class, float.class);
  }

  @Test
  public void textRendererPipeline_hasDebugMethod() {
    assertNotNull("class must exist", pipelineClass);
    try {
      Class<?> glClass = Class.forName("com.jogamp.opengl.GL");
      Method m = pipelineClass.getDeclaredMethod("debug", glClass);
      assertTrue("debug must be package-private",
          isPackagePrivate(m.getModifiers()));
    } catch (ClassNotFoundException e) {
      fail("com.jogamp.opengl.GL must be on classpath");
    } catch (NoSuchMethodException e) {
      fail("TextRendererPipeline must have debug(GL)");
    }
  }

  @Test
  public void textRendererPipeline_beginRendering_returnsVoid() {
    assertNotNull("class must exist", pipelineClass);
    try {
      Method m = pipelineClass.getDeclaredMethod("beginRendering",
          boolean.class, int.class, int.class, boolean.class);
      assertEquals("beginRendering must return void", void.class, m.getReturnType());
    } catch (NoSuchMethodException e) {
      fail("beginRendering(boolean,int,int,boolean) must exist");
    }
  }

  @Test
  public void textRendererPipeline_endRendering_returnsVoid() {
    assertNotNull("class must exist", pipelineClass);
    try {
      Method m = pipelineClass.getDeclaredMethod("endRendering", boolean.class);
      assertEquals("endRendering must return void", void.class, m.getReturnType());
    } catch (NoSuchMethodException e) {
      fail("endRendering(boolean) must exist");
    }
  }

  @Test
  public void textRendererPipeline_hasRendererField() {
    assertNotNull("class must exist", pipelineClass);
    assertFieldExists(pipelineClass, "renderer", NonCachingTextRenderer.class);
  }

  @Test
  public void textRendererPipeline_rendererFieldIsFinal() {
    assertNotNull("class must exist", pipelineClass);
    try {
      Field f = pipelineClass.getDeclaredField("renderer");
      assertTrue("renderer field must be final",
          Modifier.isFinal(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("renderer field must exist in TextRendererPipeline");
    }
  }

  // ── 17. Pipeline integration on NonCachingTextRenderer (#537) ─────

  @Test
  public void field_pipeline_existsOnRenderer() {
    assertNotNull("TextRendererPipeline must exist", pipelineClass);
    assertFieldExists(NonCachingTextRenderer.class, "pipeline", pipelineClass);
  }

  @Test
  public void field_pipeline_isPackagePrivate() {
    try {
      Field f = NonCachingTextRenderer.class.getDeclaredField("pipeline");
      assertTrue("pipeline must be package-private",
          isPackagePrivate(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("Field 'pipeline' must exist in NonCachingTextRenderer");
    }
  }

  @Test
  public void field_pipeline_isFinal() {
    try {
      Field f = NonCachingTextRenderer.class.getDeclaredField("pipeline");
      assertTrue("pipeline must be final",
          Modifier.isFinal(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("Field 'pipeline' must exist in NonCachingTextRenderer");
    }
  }

  // ── 18. Fields widened for pipeline delegate access (#537) ────────

  @Test
  public void field_haveMaxSize_isPackagePrivate() {
    assertFieldWidened("haveMaxSize");
  }

  @Test
  public void field_numRenderCycles_isPackagePrivate() {
    assertFieldWidened("numRenderCycles");
  }

  @Test
  public void field_dbgFrame_isPackagePrivate() {
    assertFieldWidened("dbgFrame");
  }

  @Test
  public void field_debugged_isPackagePrivate() {
    assertFieldWidened("debugged");
  }

  @Test
  public void field_CYCLES_PER_FLUSH_isPackagePrivateStatic() {
    assertFieldWidened("CYCLES_PER_FLUSH");
    assertFieldStatic("CYCLES_PER_FLUSH");
  }

  // ── 19. TextRendererProperties — extracted delegate (#543) ────────

  @Test
  public void textRendererProperties_classExists() {
    assertNotNull("TextRendererProperties must exist as a top-level class",
        propertiesClass);
  }

  @Test
  public void textRendererProperties_isPackagePrivate() {
    assertNotNull("class must exist", propertiesClass);
    assertTrue("must be package-private",
        isPackagePrivate(propertiesClass.getModifiers()));
  }

  @Test
  public void textRendererProperties_hasSuppressWarningsCheckStyle() throws Exception {
    assertNotNull("class must exist", propertiesClass);
    assertSourceContainsSuppressWarnings("TextRendererProperties.java");
  }

  @Test
  public void textRendererProperties_sourceFileExists() throws Exception {
    Path sourceFile = findSourceFile(
        "core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/"
            + "TextRendererProperties.java");
    assertNotNull("TextRendererProperties.java source file must exist", sourceFile);
  }

  @Test
  public void textRendererProperties_hasRendererField() {
    assertNotNull("class must exist", propertiesClass);
    assertFieldExists(propertiesClass, "renderer", NonCachingTextRenderer.class);
  }

  @Test
  public void textRendererProperties_rendererFieldIsFinal() {
    assertNotNull("class must exist", propertiesClass);
    try {
      Field f = propertiesClass.getDeclaredField("renderer");
      assertTrue("renderer field must be final",
          Modifier.isFinal(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("renderer field must exist in TextRendererProperties");
    }
  }

  @Test
  public void textRendererProperties_hasRendererConstructor() {
    assertNotNull("class must exist", propertiesClass);
    assertConstructorExists(propertiesClass,
        "constructor(NonCachingTextRenderer)",
        NonCachingTextRenderer.class);
  }

  @Test
  public void textRendererProperties_hasHaveCachedColorField() {
    assertNotNull("class must exist", propertiesClass);
    assertFieldExists(propertiesClass, "haveCachedColor", boolean.class);
  }

  @Test
  public void textRendererProperties_hasCachedRField() {
    assertNotNull("class must exist", propertiesClass);
    assertFieldExists(propertiesClass, "cachedR", float.class);
  }

  @Test
  public void textRendererProperties_hasCachedGField() {
    assertNotNull("class must exist", propertiesClass);
    assertFieldExists(propertiesClass, "cachedG", float.class);
  }

  @Test
  public void textRendererProperties_hasCachedBField() {
    assertNotNull("class must exist", propertiesClass);
    assertFieldExists(propertiesClass, "cachedB", float.class);
  }

  @Test
  public void textRendererProperties_hasCachedAField() {
    assertNotNull("class must exist", propertiesClass);
    assertFieldExists(propertiesClass, "cachedA", float.class);
  }

  @Test
  public void textRendererProperties_hasCachedColorField() {
    assertNotNull("class must exist", propertiesClass);
    assertFieldExists(propertiesClass, "cachedColor", Color.class);
  }

  @Test
  public void textRendererProperties_hasNeedToResetColorField() {
    assertNotNull("class must exist", propertiesClass);
    assertFieldExists(propertiesClass, "needToResetColor", boolean.class);
  }

  @Test
  public void textRendererProperties_hasSmoothingField() {
    assertNotNull("class must exist", propertiesClass);
    assertFieldExists(propertiesClass, "smoothing", boolean.class);
  }

  @Test
  public void textRendererProperties_colorFieldsArePackagePrivate() {
    assertNotNull("class must exist", propertiesClass);
    for (String fieldName : new String[]{"haveCachedColor", "cachedR", "cachedG",
        "cachedB", "cachedA", "cachedColor", "needToResetColor"}) {
      try {
        Field f = propertiesClass.getDeclaredField(fieldName);
        assertTrue(fieldName + " must be package-private",
            isPackagePrivate(f.getModifiers()));
      } catch (NoSuchFieldException e) {
        fail("Field '" + fieldName + "' must exist in TextRendererProperties");
      }
    }
  }

  @Test
  public void textRendererProperties_smoothingFieldIsPackagePrivate() {
    assertNotNull("class must exist", propertiesClass);
    try {
      Field f = propertiesClass.getDeclaredField("smoothing");
      assertTrue("smoothing must be package-private",
          isPackagePrivate(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("smoothing field must exist in TextRendererProperties");
    }
  }

  @Test
  public void textRendererProperties_hasDisposeMethod() {
    assertNotNull("class must exist", propertiesClass);
    try {
      Method m = propertiesClass.getDeclaredMethod("dispose");
      assertTrue("dispose must be public",
          Modifier.isPublic(m.getModifiers()));
      assertEquals("dispose must return void", void.class, m.getReturnType());
    } catch (NoSuchMethodException e) {
      fail("TextRendererProperties must have dispose()");
    }
  }

  @Test
  public void textRendererProperties_hasSetColorWithColor() {
    assertNotNull("class must exist", propertiesClass);
    try {
      Method m = propertiesClass.getDeclaredMethod("setColor", Color.class);
      assertTrue("setColor(Color) must be public",
          Modifier.isPublic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("TextRendererProperties must have setColor(Color)");
    }
  }

  @Test
  public void textRendererProperties_hasSetColorWithFloats() {
    assertNotNull("class must exist", propertiesClass);
    try {
      Method m = propertiesClass.getDeclaredMethod("setColor",
          float.class, float.class, float.class, float.class);
      assertTrue("setColor(float,float,float,float) must be public",
          Modifier.isPublic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("TextRendererProperties must have setColor(float,float,float,float)");
    }
  }

  @Test
  public void textRendererProperties_hasSetSmoothing() {
    assertNotNull("class must exist", propertiesClass);
    try {
      Method m = propertiesClass.getDeclaredMethod("setSmoothing", boolean.class);
      assertTrue("setSmoothing must be public",
          Modifier.isPublic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("TextRendererProperties must have setSmoothing(boolean)");
    }
  }

  @Test
  public void textRendererProperties_hasGetSmoothing() {
    assertNotNull("class must exist", propertiesClass);
    assertPublicMethod(propertiesClass, "getSmoothing", boolean.class);
  }

  @Test
  public void textRendererProperties_hasSetUseVertexArrays() {
    assertNotNull("class must exist", propertiesClass);
    try {
      Method m = propertiesClass.getDeclaredMethod("setUseVertexArrays", boolean.class);
      assertTrue("setUseVertexArrays must be public",
          Modifier.isPublic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("TextRendererProperties must have setUseVertexArrays(boolean)");
    }
  }

  @Test
  public void textRendererProperties_hasGetMyUseVertexArrays() {
    assertNotNull("class must exist", propertiesClass);
    assertPublicMethod(propertiesClass, "getMyUseVertexArrays", boolean.class);
  }

  @Test
  public void textRendererProperties_hasGetBoundsString() {
    assertNotNull("class must exist", propertiesClass);
    assertPublicMethodWithParams(propertiesClass, "getBounds",
        java.awt.geom.Rectangle2D.class, String.class);
  }

  @Test
  public void textRendererProperties_hasGetBoundsCharSequence() {
    assertNotNull("class must exist", propertiesClass);
    assertPublicMethodWithParams(propertiesClass, "getBounds",
        java.awt.geom.Rectangle2D.class, CharSequence.class);
  }

  @Test
  public void textRendererProperties_hasGetCharWidth() {
    assertNotNull("class must exist", propertiesClass);
    assertPublicMethodWithParams(propertiesClass, "getCharWidth",
        float.class, char.class);
  }

  // ── 20. Properties integration on NonCachingTextRenderer (#543) ───

  @Test
  public void field_properties_existsOnRenderer() {
    assertNotNull("TextRendererProperties must exist", propertiesClass);
    assertFieldExists(NonCachingTextRenderer.class, "properties", propertiesClass);
  }

  @Test
  public void field_properties_isPackagePrivate() {
    try {
      Field f = NonCachingTextRenderer.class.getDeclaredField("properties");
      assertTrue("properties must be package-private",
          isPackagePrivate(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("Field 'properties' must exist in NonCachingTextRenderer");
    }
  }

  @Test
  public void field_properties_isFinal() {
    try {
      Field f = NonCachingTextRenderer.class.getDeclaredField("properties");
      assertTrue("properties must be final",
          Modifier.isFinal(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("Field 'properties' must exist in NonCachingTextRenderer");
    }
  }

  @Test
  public void field_haveCachedColor_extractedToProperties() {
    assertFieldAbsent("haveCachedColor");
  }

  @Test
  public void field_cachedR_extractedToProperties() {
    assertFieldAbsent("cachedR");
  }

  @Test
  public void field_cachedG_extractedToProperties() {
    assertFieldAbsent("cachedG");
  }

  @Test
  public void field_cachedB_extractedToProperties() {
    assertFieldAbsent("cachedB");
  }

  @Test
  public void field_cachedA_extractedToProperties() {
    assertFieldAbsent("cachedA");
  }

  @Test
  public void field_cachedColor_extractedToProperties() {
    assertFieldAbsent("cachedColor");
  }

  @Test
  public void field_needToResetColor_extractedToProperties() {
    assertFieldAbsent("needToResetColor");
  }

  @Test
  public void field_smoothing_extractedToProperties() {
    assertFieldAbsent("smoothing");
  }

  @Test
  public void field_useVertexArrays_extractedToProperties() {
    assertFieldAbsent("useVertexArrays");
  }

  // ── 21. Fields widened for properties delegate access (#543) ──────

  @Test
  public void field_cachedBackingStore_isPackagePrivate() {
    assertFieldWidened("cachedBackingStore");
  }

  @Test
  public void field_cachedGraphics_isPackagePrivate() {
    assertFieldWidened("cachedGraphics");
  }

  @Test
  public void field_cachedFontRenderContext_isPackagePrivate() {
    assertFieldWidened("cachedFontRenderContext");
  }

  // ── Assertion helpers ─────────────────────────────────────────────

  private void assertSourceContainsSuppressWarnings(String fileName) throws Exception {
    Path sourceFile = findSourceFile(
        "core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/"
            + fileName);
    assertNotNull("Must find " + fileName, sourceFile);
    String source = new String(Files.readAllBytes(sourceFile));
    assertTrue(fileName + " must have @SuppressWarnings(\"CheckStyle\")",
        source.contains("@SuppressWarnings(\"CheckStyle\")"));
  }

  private void assertFieldExists(Class<?> clazz, String name, Class<?> expectedType) {
    try {
      Field f = clazz.getDeclaredField(name);
      assertEquals(clazz.getSimpleName() + "." + name + " type",
          expectedType, f.getType());
    } catch (NoSuchFieldException e) {
      fail(clazz.getSimpleName() + " must have field '" + name + "'");
    }
  }

  private void assertConstructorExists(Class<?> clazz, String desc, Class<?>... params) {
    try {
      clazz.getDeclaredConstructor(params);
    } catch (NoSuchMethodException e) {
      fail(clazz.getSimpleName() + " must have " + desc);
    }
  }

  private void assertPublicMethod(Class<?> clazz, String name, Class<?> returnType) {
    try {
      Method m = clazz.getDeclaredMethod(name);
      assertTrue(name + " must be public", Modifier.isPublic(m.getModifiers()));
      assertEquals(name + " return type", returnType, m.getReturnType());
    } catch (NoSuchMethodException e) {
      fail(clazz.getSimpleName() + " must have " + name + "()");
    }
  }

  private void assertPublicMethodWithParams(Class<?> clazz, String name,
      Class<?> returnType, Class<?>... params) {
    try {
      Method m = clazz.getDeclaredMethod(name, params);
      assertTrue(name + " must be public", Modifier.isPublic(m.getModifiers()));
      assertEquals(name + " return type", returnType, m.getReturnType());
    } catch (NoSuchMethodException e) {
      fail(clazz.getSimpleName() + " must have " + name + "(...)");
    }
  }

  private void assertInnerClassAbsent(String simpleName) {
    for (Class<?> inner : NonCachingTextRenderer.class.getDeclaredClasses()) {
      if (simpleName.equals(inner.getSimpleName())) {
        fail("Inner class '" + simpleName
            + "' must be extracted to a top-level class");
      }
    }
  }

  private void assertFieldWidened(String fieldName) {
    try {
      Field f = NonCachingTextRenderer.class.getDeclaredField(fieldName);
      assertTrue("'" + fieldName + "' must be package-private (not private)",
          isPackagePrivate(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("Field '" + fieldName + "' must exist in NonCachingTextRenderer");
    }
  }

  private void assertFieldFinal(String fieldName) {
    try {
      Field f = NonCachingTextRenderer.class.getDeclaredField(fieldName);
      assertTrue("'" + fieldName + "' must be final",
          Modifier.isFinal(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("Field '" + fieldName + "' must exist");
    }
  }

  private void assertFieldStatic(String fieldName) {
    try {
      Field f = NonCachingTextRenderer.class.getDeclaredField(fieldName);
      assertTrue("'" + fieldName + "' must be static",
          Modifier.isStatic(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("Field '" + fieldName + "' must exist");
    }
  }

  private void assertMethodWidened(String methodName) {
    for (Method m : NonCachingTextRenderer.class.getDeclaredMethods()) {
      if (m.getName().equals(methodName)) {
        assertTrue("'" + methodName + "' must be package-private (not private)",
            isPackagePrivate(m.getModifiers()));
        return;
      }
    }
    fail("Method '" + methodName + "' must exist in NonCachingTextRenderer");
  }

  private void assertPackagePrivateMethodWithParams(Class<?> clazz, String name,
      Class<?>... params) {
    try {
      Method m = clazz.getDeclaredMethod(name, params);
      assertTrue(clazz.getSimpleName() + "." + name + " must be package-private",
          isPackagePrivate(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail(clazz.getSimpleName() + " must have method " + name + "(...)");
    }
  }

  private Path findSourceFile(String relativePath) {
    Path cwd = Paths.get(System.getProperty("user.dir"));
    for (Path p = cwd; p != null; p = p.getParent()) {
      Path candidate = p.resolve(relativePath);
      if (Files.exists(candidate)) {
        return candidate;
      }
      // Stop at root or after reasonable depth
      if (p.equals(p.getRoot())) {
        break;
      }
    }
    return null;
  }

  private void assertFieldAbsent(String fieldName) {
    try {
      NonCachingTextRenderer.class.getDeclaredField(fieldName);
      fail("Field '" + fieldName
          + "' should have been extracted from NonCachingTextRenderer"
          + " to TextRendererProperties");
    } catch (NoSuchFieldException e) {
      // expected — field was successfully extracted
    }
  }
}
