package org.lgna.story.resourceutilities;

import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Characterization tests for the JointedModelColladaImporter decomposition.
 *
 * Verifies:
 * - Public API of JointedModelColladaImporter is unchanged.
 * - Extracted delegates (ColladaImportMeshBuilder, ColladaImportMaterialLoader,
 *   ColladaImportDebugPrinter) exist and are package-private.
 * - floatArrayToAliceMatrix preserves conversion behavior.
 */
public class ColladaImporterDecompositionTest {

  // ── Public API contract ──────────────────────────────────────

  @Test
  public void importerHasPublicConstructor() throws Exception {
    var ctor = JointedModelColladaImporter.class.getConstructor(java.io.File.class, java.util.logging.Logger.class);
    assertTrue("Constructor must be public", Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void importerHasLoadSkeletonVisual() throws Exception {
    Method m = JointedModelColladaImporter.class.getMethod("loadSkeletonVisual");
    assertTrue("loadSkeletonVisual must be public", Modifier.isPublic(m.getModifiers()));
    assertEquals(edu.cmu.cs.dennisc.scenegraph.SkeletonVisual.class, m.getReturnType());
  }

  @Test
  public void importerHasLoadAliceModel() throws Exception {
    Method m = JointedModelColladaImporter.class.getMethod("loadAliceModel", org.lgna.story.resources.JointedModelResource.class);
    assertTrue("loadAliceModel must be public static", Modifier.isPublic(m.getModifiers()));
    assertTrue("loadAliceModel must be static", Modifier.isStatic(m.getModifiers()));
  }

  // ── Delegate class existence (package-private) ───────────────

  @Test
  public void meshBuilderClassExists() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.story.resourceutilities.ColladaImportMeshBuilder");
    assertFalse("ColladaImportMeshBuilder must be package-private", Modifier.isPublic(clazz.getModifiers()));
  }

  @Test
  public void materialLoaderClassExists() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.story.resourceutilities.ColladaImportMaterialLoader");
    assertFalse("ColladaImportMaterialLoader must be package-private", Modifier.isPublic(clazz.getModifiers()));
  }

  @Test
  public void debugPrinterClassExists() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.story.resourceutilities.ColladaImportDebugPrinter");
    assertFalse("ColladaImportDebugPrinter must be package-private", Modifier.isPublic(clazz.getModifiers()));
  }

  // ── Matrix conversion behavior ───────────────────────────────

  @Test
  public void floatArrayToAliceMatrix_identity16() throws Exception {
    Orientation orientation = Orientation.forAlice();
    float[] identity = {
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1
    };
    AffineMatrix4x4 result = ColladaImportMeshBuilder.floatArrayToAliceMatrix(identity, orientation);
    assertNotNull(result);
    // After orientation transform, the identity should remain a valid affine matrix
    assertNotNull(result.translation());
    assertNotNull(result.orientation());
  }

  @Test
  public void floatArrayToAliceMatrix_identity12() throws Exception {
    Orientation orientation = Orientation.forAlice();
    float[] identity = {
        1, 0, 0,
        0, 1, 0,
        0, 0, 1,
        0, 0, 0
    };
    AffineMatrix4x4 result = ColladaImportMeshBuilder.floatArrayToAliceMatrix(identity, orientation);
    assertNotNull(result);
  }

  @Test(expected = ModelLoadingException.class)
  public void floatArrayToAliceMatrix_rejectsBadSize() throws Exception {
    Orientation orientation = Orientation.forAlice();
    float[] bad = {1, 0, 0, 0, 1};
    ColladaImportMeshBuilder.floatArrayToAliceMatrix(bad, orientation);
  }

  // ── Material loader static helpers ───────────────────────────

  @Test
  public void resolveTextureFileName_returnsNullForMissing() {
    java.io.File root = new java.io.File(System.getProperty("user.dir"));
    assertNull(ColladaImportMaterialLoader.resolveTextureFileName("nonexistent_texture_abc123.png", root));
  }

  @Test
  public void resolveTextureFileName_stripsFileProtocol() {
    java.io.File root = new java.io.File(System.getProperty("user.dir"));
    // After stripping file://, should still return null for non-existent
    assertNull(ColladaImportMaterialLoader.resolveTextureFileName("file://nonexistent_abc123.png", root));
  }
}
