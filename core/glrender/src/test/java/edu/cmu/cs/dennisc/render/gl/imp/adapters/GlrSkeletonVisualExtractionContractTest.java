package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Characterization tests for GlrSkeletonVisual extraction (#616).
 * Ensures public API is preserved and line-count target is met.
 */
public class GlrSkeletonVisualExtractionContractTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  private static final String PKG = "edu.cmu.cs.dennisc.render.gl.imp.adapters";
  private static final String SRC = "src/main/java/edu/cmu/cs/dennisc/render/gl/imp/adapters/";

  // --- WeightedMeshControl is now a top-level public class ---

  @org.junit.Before

  @Test
  public void weightedMeshControl_isTopLevelPublicClass() throws Exception {
    Class<?> cls = Class.forName(PKG + ".WeightedMeshControl");
    assertTrue("WeightedMeshControl should be public", Modifier.isPublic(cls.getModifiers()));
    assertNull("WeightedMeshControl should be top-level (no enclosing class)", cls.getEnclosingClass());
  }

  @Test
  public void weightedMeshControl_hasInitializeMethod() throws Exception {
    Class<?> cls = Class.forName(PKG + ".WeightedMeshControl");
    Method m = cls.getMethod("initialize", Class.forName("edu.cmu.cs.dennisc.scenegraph.WeightedMesh"));
    assertTrue("initialize should be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void weightedMeshControl_hasRenderGeometry() throws Exception {
    Class<?> cls = Class.forName(PKG + ".WeightedMeshControl");
    Method m = cls.getMethod("renderGeometry", Class.forName("edu.cmu.cs.dennisc.render.gl.imp.RenderContext"));
    assertTrue("renderGeometry should be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void weightedMeshControl_hasPickGeometry() throws Exception {
    Class<?> cls = Class.forName(PKG + ".WeightedMeshControl");
    Method m = cls.getMethod("pickGeometry", Class.forName("edu.cmu.cs.dennisc.render.gl.imp.PickContext"), boolean.class);
    assertTrue("pickGeometry should be public", Modifier.isPublic(m.getModifiers()));
  }

  // --- SkeletonWeightProcessor is a package-private helper ---

  @Test
  public void skeletonWeightProcessor_exists() throws Exception {
    Class<?> cls = Class.forName(PKG + ".SkeletonWeightProcessor");
    assertFalse("SkeletonWeightProcessor should be package-private", Modifier.isPublic(cls.getModifiers()));
    assertTrue("SkeletonWeightProcessor should be final", Modifier.isFinal(cls.getModifiers()));
  }

  // --- GlrSkeletonVisual no longer contains WeightedMeshControl inner class ---

  @Test
  public void glrSkeletonVisual_noWeightedMeshControlInnerClass() {
    for (Class<?> inner : GlrSkeletonVisual.class.getDeclaredClasses()) {
      assertNotEquals("WeightedMeshControl should not be an inner class of GlrSkeletonVisual",
          "WeightedMeshControl", inner.getSimpleName());
    }
  }

  // --- Public API of GlrSkeletonVisual is preserved ---

  @Test
  public void glrSkeletonVisual_hasProcessWeightedMesh() throws Exception {
    Method m = GlrSkeletonVisual.class.getMethod("processWeightedMesh");
    assertTrue("processWeightedMesh should be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void glrSkeletonVisual_implementsPropertyListener() {
    assertTrue("Should implement PropertyListener",
        edu.cmu.cs.dennisc.property.event.PropertyListener.class.isAssignableFrom(GlrSkeletonVisual.class));
  }

  @Test
  public void glrSkeletonVisual_implementsBoundingBoxTracker() {
    assertTrue("Should implement SkeletonVisualBoundingBoxTracker",
        edu.cmu.cs.dennisc.scenegraph.SkeletonVisualBoundingBoxTracker.class.isAssignableFrom(GlrSkeletonVisual.class));
  }

  // --- Line count target ---

  @Test
  public void glrSkeletonVisual_underFiveHundredLines() throws Exception {
    Path srcFile = Paths.get(SRC + "GlrSkeletonVisual.java");
    assertTrue("Source file must exist at " + srcFile, Files.exists(srcFile));
    long lineCount = Files.lines(srcFile).count();
    assertTrue("GlrSkeletonVisual.java should be under 500 lines but was " + lineCount, lineCount < 500);
  }
}
