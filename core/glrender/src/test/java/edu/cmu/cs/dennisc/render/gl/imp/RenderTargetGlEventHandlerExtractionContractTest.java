package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;


import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TDD contract tests for issue #671: Extract GL event handling from
 * RenderTargetImp.java into RenderTargetGlEventHandler.
 *
 * Written BEFORE implementation — all tests FAIL initially.
 * They pass once extraction is complete.
 *
 * Contract groups:
 *   1.  RenderTargetGlEventHandler — exists as package-private class
 *   2.  RenderTargetGlEventHandler — implements GLEventListener
 *   3.  RenderTargetGlEventHandler — constructor takes RenderTargetImp
 *   4.  RenderTargetGlEventHandler — has rtImp back-reference field
 *   5.  RenderTargetGlEventHandler — implements all 4 GL callbacks
 *   6.  RenderTargetImp — fields widened to package-private
 *   7.  RenderTargetImp — performRender() widened to package-private
 *   8.  RenderTargetImp — fireInitialized() widened to package-private
 *   9.  RenderTargetImp — fireResized() widened to package-private
 *  10.  RenderTargetImp — glEventHandler field replaces glEventListener
 *  11.  RenderTargetImp — dead code removed (line count under 500)
 *  12.  RenderTargetGlEventHandler source file exists
 *  13.  Dead code: paintOverlay block removed
 *  14.  Dead code: GL_EXT_abgr block removed
 *  15.  Dead code: displayChanged block removed
 *  16.  RenderTargetImp — anonymous GLEventListener inner class removed
 */
public class RenderTargetGlEventHandlerExtractionContractTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }


  private static final String PKG = "edu.cmu.cs.dennisc.render.gl.imp";
  private static final String SRC_DIR =
      "core/glrender/src/main/java/edu/cmu/cs/dennisc/render/gl/imp/";

  private static Class<?> handlerClass;
  private static Class<?> renderTargetImpClass;

  @BeforeClass
  public static void resolveClasses() {
    handlerClass = tryLoad(PKG + ".RenderTargetGlEventHandler");
    renderTargetImpClass = tryLoad(PKG + ".RenderTargetImp");
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
  // 1. RenderTargetGlEventHandler — exists as package-private class
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void handlerClass_exists() {
    assertNotNull("RenderTargetGlEventHandler must exist as a class in " + PKG,
        handlerClass);
  }

  @Test
  public void handlerClass_isPackagePrivate() {
    assertNotNull("Precondition: class must exist", handlerClass);
    assertTrue("RenderTargetGlEventHandler must be package-private",
        isPackagePrivate(handlerClass.getModifiers()));
  }

  @Test
  public void handlerClass_isNotAbstract() {
    assertNotNull("Precondition: class must exist", handlerClass);
    assertFalse("RenderTargetGlEventHandler must be concrete",
        Modifier.isAbstract(handlerClass.getModifiers()));
  }

  // ══════════════════════════════════════════════════════════════════
  // 2. RenderTargetGlEventHandler — implements GLEventListener
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void handlerClass_implementsGlEventListener() {
    assertNotNull("Precondition: class must exist", handlerClass);
    Class<?> glEventListenerIface = tryLoad("com.jogamp.opengl.GLEventListener");
    assertNotNull("GLEventListener interface must be on classpath", glEventListenerIface);
    assertTrue("RenderTargetGlEventHandler must implement GLEventListener",
        glEventListenerIface.isAssignableFrom(handlerClass));
  }

  // ══════════════════════════════════════════════════════════════════
  // 3. RenderTargetGlEventHandler — constructor takes RenderTargetImp
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void handlerClass_hasConstructorTakingRenderTargetImp() {
    assertNotNull("Precondition: handler class must exist", handlerClass);
    assertNotNull("Precondition: RenderTargetImp class must exist", renderTargetImpClass);
    try {
      Constructor<?> ctor = handlerClass.getDeclaredConstructor(renderTargetImpClass);
      assertTrue("Constructor should be package-private",
          isPackagePrivate(ctor.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("RenderTargetGlEventHandler must have constructor(RenderTargetImp)");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 4. RenderTargetGlEventHandler — has rtImp back-reference field
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void handlerClass_hasRtImpField() {
    assertNotNull("Precondition: handler class must exist", handlerClass);
    try {
      Field f = handlerClass.getDeclaredField("rtImp");
      assertTrue("rtImp field must be private",
          Modifier.isPrivate(f.getModifiers()));
      assertTrue("rtImp field must be final",
          Modifier.isFinal(f.getModifiers()));
      assertEquals("rtImp must be of type RenderTargetImp",
          renderTargetImpClass, f.getType());
    } catch (NoSuchFieldException e) {
      fail("RenderTargetGlEventHandler must have a 'rtImp' field");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 5. RenderTargetGlEventHandler — implements all 4 GL callbacks
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void handlerClass_hasInitMethod() {
    assertNotNull("Precondition: handler class must exist", handlerClass);
    Class<?> glAutoDrawable = tryLoad("com.jogamp.opengl.GLAutoDrawable");
    assertNotNull("GLAutoDrawable must be on classpath", glAutoDrawable);
    try {
      Method m = handlerClass.getDeclaredMethod("init", glAutoDrawable);
      assertTrue("init must be public", Modifier.isPublic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("RenderTargetGlEventHandler must implement init(GLAutoDrawable)");
    }
  }

  @Test
  public void handlerClass_hasDisplayMethod() {
    assertNotNull("Precondition: handler class must exist", handlerClass);
    Class<?> glAutoDrawable = tryLoad("com.jogamp.opengl.GLAutoDrawable");
    assertNotNull("GLAutoDrawable must be on classpath", glAutoDrawable);
    try {
      Method m = handlerClass.getDeclaredMethod("display", glAutoDrawable);
      assertTrue("display must be public", Modifier.isPublic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("RenderTargetGlEventHandler must implement display(GLAutoDrawable)");
    }
  }

  @Test
  public void handlerClass_hasReshapeMethod() {
    assertNotNull("Precondition: handler class must exist", handlerClass);
    Class<?> glAutoDrawable = tryLoad("com.jogamp.opengl.GLAutoDrawable");
    assertNotNull("GLAutoDrawable must be on classpath", glAutoDrawable);
    try {
      Method m = handlerClass.getDeclaredMethod("reshape",
          glAutoDrawable, int.class, int.class, int.class, int.class);
      assertTrue("reshape must be public", Modifier.isPublic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("RenderTargetGlEventHandler must implement reshape(GLAutoDrawable,int,int,int,int)");
    }
  }

  @Test
  public void handlerClass_hasDisposeMethod() {
    assertNotNull("Precondition: handler class must exist", handlerClass);
    Class<?> glAutoDrawable = tryLoad("com.jogamp.opengl.GLAutoDrawable");
    assertNotNull("GLAutoDrawable must be on classpath", glAutoDrawable);
    try {
      Method m = handlerClass.getDeclaredMethod("dispose", glAutoDrawable);
      assertTrue("dispose must be public", Modifier.isPublic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("RenderTargetGlEventHandler must implement dispose(GLAutoDrawable)");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 6. RenderTargetImp — fields widened to package-private
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderTargetImp_renderContextFieldIsPackagePrivate() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    assertFieldIsPackagePrivate("renderContext");
  }

  @Test
  public void renderTargetImp_drawableFieldIsPackagePrivate() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    assertFieldIsPackagePrivate("drawable");
  }

  @Test
  public void renderTargetImp_drawableWidthFieldIsPackagePrivate() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    assertFieldIsPackagePrivate("drawableWidth");
  }

  @Test
  public void renderTargetImp_drawableHeightFieldIsPackagePrivate() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    assertFieldIsPackagePrivate("drawableHeight");
  }

  @Test
  public void renderTargetImp_screenWidthFieldIsPackagePrivate() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    assertFieldIsPackagePrivate("screenWidth");
  }

  @Test
  public void renderTargetImp_screenHeightFieldIsPackagePrivate() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    assertFieldIsPackagePrivate("screenHeight");
  }

  private void assertFieldIsPackagePrivate(String fieldName) {
    try {
      Field f = renderTargetImpClass.getDeclaredField(fieldName);
      assertTrue("Field '" + fieldName + "' must be package-private (not private)",
          isPackagePrivate(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("RenderTargetImp must have field '" + fieldName + "'");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 7. RenderTargetImp — performRender() widened to package-private
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderTargetImp_performRenderIsPackagePrivate() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    try {
      Method m = renderTargetImpClass.getDeclaredMethod("performRender");
      assertTrue("performRender() must be package-private",
          isPackagePrivate(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("RenderTargetImp must have performRender() method");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 8. RenderTargetImp — fireInitialized() widened to package-private
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderTargetImp_fireInitializedIsPackagePrivate() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    Class<?> eventClass = tryLoad("edu.cmu.cs.dennisc.render.event.RenderTargetInitializeEvent");
    assertNotNull("RenderTargetInitializeEvent must be on classpath", eventClass);
    try {
      Method m = renderTargetImpClass.getDeclaredMethod("fireInitialized", eventClass);
      assertTrue("fireInitialized() must be package-private",
          isPackagePrivate(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("RenderTargetImp must have fireInitialized(RenderTargetInitializeEvent) method");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 9. RenderTargetImp — fireResized() widened to package-private
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderTargetImp_fireResizedIsPackagePrivate() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    Class<?> eventClass = tryLoad("edu.cmu.cs.dennisc.render.event.RenderTargetResizeEvent");
    assertNotNull("RenderTargetResizeEvent must be on classpath", eventClass);
    try {
      Method m = renderTargetImpClass.getDeclaredMethod("fireResized", eventClass);
      assertTrue("fireResized() must be package-private",
          isPackagePrivate(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("RenderTargetImp must have fireResized(RenderTargetResizeEvent) method");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 10. RenderTargetImp — glEventHandler field replaces glEventListener
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderTargetImp_hasGlEventHandlerField() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    assertNotNull("Precondition: handler class must exist", handlerClass);
    try {
      Field f = renderTargetImpClass.getDeclaredField("glEventHandler");
      assertEquals("glEventHandler must be of type RenderTargetGlEventHandler",
          handlerClass, f.getType());
      assertTrue("glEventHandler must be final",
          Modifier.isFinal(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("RenderTargetImp must have a 'glEventHandler' field");
    }
  }

  @Test
  public void renderTargetImp_noGlEventListenerField() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    try {
      renderTargetImpClass.getDeclaredField("glEventListener");
      fail("RenderTargetImp must NOT have the old 'glEventListener' field — it should be replaced by 'glEventHandler'");
    } catch (NoSuchFieldException e) {
      // expected — old field should be removed
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // 11. RenderTargetImp — dead code removed (line count under 500)
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderTargetImp_lineCountUnder500() throws Exception {
    Path srcPath = findSourceFile(SRC_DIR + "RenderTargetImp.java");
    assertNotNull("Source file must exist: " + SRC_DIR + "RenderTargetImp.java", srcPath);
    long lineCount = Files.lines(srcPath).count();
    assertTrue("RenderTargetImp.java must be under 500 lines (was " + lineCount + ")",
        lineCount < 500);
  }

  // ══════════════════════════════════════════════════════════════════
  // 12. RenderTargetGlEventHandler source file exists
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void handlerSourceFile_exists() {
    Path srcPath = findSourceFile(SRC_DIR + "RenderTargetGlEventHandler.java");
    assertNotNull("RenderTargetGlEventHandler.java source must exist at " + SRC_DIR,
        srcPath);
  }

  @Test
  public void handlerSourceFile_isReasonableSize() throws Exception {
    Path srcPath = findSourceFile(SRC_DIR + "RenderTargetGlEventHandler.java");
    if (srcPath == null) {
      fail("Source file does not exist yet: " + SRC_DIR + "RenderTargetGlEventHandler.java");
    }
    long lineCount = Files.lines(srcPath).count();
    assertTrue("RenderTargetGlEventHandler.java should be under 120 lines (was " + lineCount + ")",
        lineCount < 120);
    assertTrue("RenderTargetGlEventHandler.java should be at least 30 lines (was " + lineCount + ")",
        lineCount >= 30);
  }

  // ══════════════════════════════════════════════════════════════════
  // 13. Dead code: paintOverlay block removed
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderTargetImp_noPaintOverlayDeadCode() throws Exception {
    Path srcPath = findSourceFile(SRC_DIR + "RenderTargetImp.java");
    assertNotNull("Source file must exist", srcPath);
    String content = new String(Files.readAllBytes(srcPath));
    assertFalse("RenderTargetImp must not contain commented-out paintOverlay block",
        content.contains("paintOverlay"));
  }

  // ══════════════════════════════════════════════════════════════════
  // 14. Dead code: GL_EXT_abgr block removed
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderTargetImp_noGlExtAbgrDeadCode() throws Exception {
    Path srcPath = findSourceFile(SRC_DIR + "RenderTargetImp.java");
    assertNotNull("Source file must exist", srcPath);
    String content = new String(Files.readAllBytes(srcPath));
    assertFalse("RenderTargetImp must not contain commented-out GL_EXT_abgr check",
        content.contains("GL_EXT_abgr"));
  }

  // ══════════════════════════════════════════════════════════════════
  // 15. Dead code: displayChanged block removed
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderTargetImp_noDisplayChangedDeadCode() throws Exception {
    Path srcPath = findSourceFile(SRC_DIR + "RenderTargetImp.java");
    assertNotNull("Source file must exist", srcPath);
    String content = new String(Files.readAllBytes(srcPath));
    assertFalse("RenderTargetImp must not contain commented-out displayChanged block",
        content.contains("displayChanged"));
  }

  // ══════════════════════════════════════════════════════════════════
  // 16. RenderTargetImp — anonymous GLEventListener inner class removed
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderTargetImp_noAnonymousGlEventListenerInnerClass() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    Class<?> glEventListenerIface = tryLoad("com.jogamp.opengl.GLEventListener");
    assertNotNull("GLEventListener interface must be on classpath", glEventListenerIface);
    Class<?>[] innerClasses = renderTargetImpClass.getDeclaredClasses();
    for (Class<?> inner : innerClasses) {
      if (inner.isAnonymousClass() && glEventListenerIface.isAssignableFrom(inner)) {
        fail("RenderTargetImp should not contain an anonymous GLEventListener inner class — it should use RenderTargetGlEventHandler instead");
      }
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // Bonus: RenderTargetImp — handle*() private methods removed
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderTargetImp_noHandleInitMethod() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    Class<?> glAutoDrawable = tryLoad("com.jogamp.opengl.GLAutoDrawable");
    try {
      renderTargetImpClass.getDeclaredMethod("handleInit", glAutoDrawable);
      fail("RenderTargetImp should not have handleInit() — logic moved to handler");
    } catch (NoSuchMethodException e) {
      // expected
    }
  }

  @Test
  public void renderTargetImp_noHandleDisplayMethod() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    Class<?> glAutoDrawable = tryLoad("com.jogamp.opengl.GLAutoDrawable");
    try {
      renderTargetImpClass.getDeclaredMethod("handleDisplay", glAutoDrawable);
      fail("RenderTargetImp should not have handleDisplay() — logic moved to handler");
    } catch (NoSuchMethodException e) {
      // expected
    }
  }

  @Test
  public void renderTargetImp_noHandleReshapeMethod() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    Class<?> glAutoDrawable = tryLoad("com.jogamp.opengl.GLAutoDrawable");
    try {
      renderTargetImpClass.getDeclaredMethod("handleReshape",
          glAutoDrawable, int.class, int.class, int.class, int.class);
      fail("RenderTargetImp should not have handleReshape() — logic moved to handler");
    } catch (NoSuchMethodException e) {
      // expected
    }
  }

  @Test
  public void renderTargetImp_noHandleDisposeMethod() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    Class<?> glAutoDrawable = tryLoad("com.jogamp.opengl.GLAutoDrawable");
    try {
      renderTargetImpClass.getDeclaredMethod("handleDispose", glAutoDrawable);
      fail("RenderTargetImp should not have handleDispose() — logic moved to handler");
    } catch (NoSuchMethodException e) {
      // expected
    }
  }

  @Test
  public void renderTargetImp_noInitializeMethod() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    Class<?> glAutoDrawable = tryLoad("com.jogamp.opengl.GLAutoDrawable");
    try {
      renderTargetImpClass.getDeclaredMethod("initialize", glAutoDrawable);
      fail("RenderTargetImp should not have initialize(GLAutoDrawable) — logic moved to handler's init()");
    } catch (NoSuchMethodException e) {
      // expected
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // Bonus: Stable public API preserved in RenderTargetImp
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderTargetImp_publicApiPreserved_getRenderTarget() {
    assertMethodExists(renderTargetImpClass, "getRenderTarget", true);
  }

  @Test
  public void renderTargetImp_publicApiPreserved_addSgCamera() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    Class<?> glAutoDrawable = tryLoad("com.jogamp.opengl.GLAutoDrawable");
    Class<?> abstractCamera = tryLoad("edu.cmu.cs.dennisc.scenegraph.AbstractCamera");
    try {
      Method m = renderTargetImpClass.getDeclaredMethod("addSgCamera", abstractCamera, glAutoDrawable);
      assertTrue("addSgCamera must remain public", Modifier.isPublic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("RenderTargetImp must retain addSgCamera(AbstractCamera, GLAutoDrawable)");
    }
  }

  @Test
  public void renderTargetImp_publicApiPreserved_addRenderTargetListener() {
    Class<?> listenerClass = tryLoad("edu.cmu.cs.dennisc.render.event.RenderTargetListener");
    assertNotNull("RenderTargetListener must be on classpath", listenerClass);
    try {
      Method m = renderTargetImpClass.getDeclaredMethod("addRenderTargetListener", listenerClass);
      assertTrue("addRenderTargetListener must remain public", Modifier.isPublic(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("RenderTargetImp must retain addRenderTargetListener(RenderTargetListener)");
    }
  }

  @Test
  public void renderTargetImp_publicApiPreserved_createBufferedImageForUseAsColorBuffer() {
    assertMethodExists(renderTargetImpClass, "createBufferedImageForUseAsColorBuffer", true);
  }

  @Test
  public void renderTargetImp_publicApiPreserved_isListening() {
    assertMethodExists(renderTargetImpClass, "isListening", true);
  }

  private void assertMethodExists(Class<?> clazz, String methodName, boolean expectPublic) {
    assertNotNull("Precondition: class must exist", clazz);
    boolean foundAny = false;
    boolean foundPublic = false;
    for (Method m : clazz.getDeclaredMethods()) {
      if (m.getName().equals(methodName)) {
        foundAny = true;
        if (Modifier.isPublic(m.getModifiers())) {
          foundPublic = true;
        }
      }
    }
    assertTrue("Method " + methodName + "() must exist on " + clazz.getSimpleName(), foundAny);
    if (expectPublic) {
      assertTrue(methodName + "() must have at least one public overload",
          foundPublic);
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // Bonus: RenderTargetImp — isDisplayIgnoredDueToPreviousException stays private
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderTargetImp_exceptionFlagStaysPrivate() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    try {
      Field f = renderTargetImpClass.getDeclaredField("isDisplayIgnoredDueToPreviousException");
      assertTrue("isDisplayIgnoredDueToPreviousException must remain private",
          Modifier.isPrivate(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("RenderTargetImp must retain isDisplayIgnoredDueToPreviousException field");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // Bonus: fireCleared and fireRendered stay private (not accessed by handler)
  // ══════════════════════════════════════════════════════════════════

  @Test
  public void renderTargetImp_fireClearedStaysPrivate() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    Class<?> eventClass = tryLoad("edu.cmu.cs.dennisc.render.event.RenderTargetRenderEvent");
    assertNotNull("RenderTargetRenderEvent must be on classpath", eventClass);
    try {
      Method m = renderTargetImpClass.getDeclaredMethod("fireCleared", eventClass);
      assertTrue("fireCleared() must remain private — not needed by handler",
          Modifier.isPrivate(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("RenderTargetImp must retain fireCleared(RenderTargetRenderEvent)");
    }
  }

  @Test
  public void renderTargetImp_fireRenderedStaysPrivate() {
    assertNotNull("Precondition: RenderTargetImp must exist", renderTargetImpClass);
    Class<?> eventClass = tryLoad("edu.cmu.cs.dennisc.render.event.RenderTargetRenderEvent");
    assertNotNull("RenderTargetRenderEvent must be on classpath", eventClass);
    try {
      Method m = renderTargetImpClass.getDeclaredMethod("fireRendered", eventClass);
      assertTrue("fireRendered() must remain private — not needed by handler",
          Modifier.isPrivate(m.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("RenderTargetImp must retain fireRendered(RenderTargetRenderEvent)");
    }
  }

  // ══════════════════════════════════════════════════════════════════
  // Utility: walk up from CWD to find source files (Surefire CWD varies)
  // ══════════════════════════════════════════════════════════════════

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
