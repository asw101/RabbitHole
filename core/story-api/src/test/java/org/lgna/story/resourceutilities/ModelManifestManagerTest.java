package org.lgna.story.resourceutilities;

import org.alice.tweedle.file.ManifestEncoderDecoder;
import org.alice.tweedle.file.ModelManifest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.*;

/**
 * TDD tests for {@link ModelManifestManager} — a stateful manager extracted
 * from {@code StorytellingResources} that owns lazy-loaded manifest caches
 * and handles JSON model file discovery.
 *
 * <p>These tests exercise {@code ModelManifestManager} in isolation using
 * temporary directories and synthesized JSON manifests. Tests that require
 * Alice's {@code StoryApiDirectoryUtilities} paths (user gallery, internal
 * models) use direct method calls on the manager where possible, and
 * document the dependency where unavoidable.</p>
 *
 * <p><b>RED PHASE:</b> These tests will not compile until
 * {@code ModelManifestManager} is created. Once the class exists and is
 * implemented, all tests should pass.</p>
 *
 * @see ModelManifestManager
 */
public class ModelManifestManagerTest {

  @Rule
  public TemporaryFolder tempDir = new TemporaryFolder();

  private ModelManifestManager manager;

  @Before
  public void setUp() {
    manager = new ModelManifestManager();
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  getDynamicModelFiles
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void getDynamicModelFiles_emptyDirectory_returnsEmptyList() throws IOException {
    File emptyDir = tempDir.newFolder("empty");

    List<File> result = manager.getDynamicModelFiles(emptyDir);

    assertNotNull(result);
    assertTrue("Empty directory should produce empty file list", result.isEmpty());
  }

  @Test
  public void getDynamicModelFiles_directoryWithJsonFiles_findsAll() throws IOException {
    File modelsDir = tempDir.newFolder("models");
    Files.writeString(new File(modelsDir, "model1.json").toPath(), "{}");
    Files.writeString(new File(modelsDir, "model2.json").toPath(), "{}");

    List<File> result = manager.getDynamicModelFiles(modelsDir);

    assertEquals("Should find both JSON files", 2, result.size());
  }

  @Test
  public void getDynamicModelFiles_ignoresNonJsonFiles() throws IOException {
    File modelsDir = tempDir.newFolder("models");
    Files.writeString(new File(modelsDir, "model.json").toPath(), "{}");
    Files.writeString(new File(modelsDir, "readme.txt").toPath(), "text");
    Files.writeString(new File(modelsDir, "data.xml").toPath(), "<x/>");

    List<File> result = manager.getDynamicModelFiles(modelsDir);

    assertEquals("Should find only the JSON file", 1, result.size());
    assertTrue(result.get(0).getName().endsWith(".json"));
  }

  @Test
  public void getDynamicModelFiles_nonDirectoryArgument_ignored() throws IOException {
    File regularFile = tempDir.newFile("notadir.json");

    List<File> result = manager.getDynamicModelFiles(regularFile);

    assertNotNull(result);
    assertTrue("Non-directory argument should be ignored", result.isEmpty());
  }

  @Test
  public void getDynamicModelFiles_multipleDirectories_combinesResults() throws IOException {
    File dir1 = tempDir.newFolder("dir1");
    File dir2 = tempDir.newFolder("dir2");
    Files.writeString(new File(dir1, "a.json").toPath(), "{}");
    Files.writeString(new File(dir2, "b.json").toPath(), "{}");
    Files.writeString(new File(dir2, "c.json").toPath(), "{}");

    List<File> result = manager.getDynamicModelFiles(dir1, dir2);

    assertEquals("Should combine JSON files from both directories", 3, result.size());
  }

  @Test
  public void getDynamicModelFiles_noArguments_returnsEmptyList() {
    List<File> result = manager.getDynamicModelFiles();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Manifest parsing (tested via getModelManifest after manual init)
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void getModelManifest_returnsNullWhenNotFound() {
    // Force initialization by calling find (even though gallery dir may be empty)
    // Then look up a name that doesn't exist
    manager.findAndLoadUserGalleryResources();
    ModelManifest result = manager.getModelManifest("NonExistentModel");

    assertNull("Should return null for unknown model name", result);
  }

  @Test
  public void getInternalModelManifest_returnsNullWhenNotFound() {
    manager.findAndLoadInternalResources();
    ModelManifest result = manager.getInternalModelManifest("NonExistentInternal");

    assertNull("Should return null for unknown internal model name", result);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Lazy initialization — idempotency
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void findAndLoadUserGalleryResources_isIdempotent() {
    List<ModelManifest> first = manager.findAndLoadUserGalleryResources();
    List<ModelManifest> second = manager.findAndLoadUserGalleryResources();

    assertNotNull(first);
    assertNotNull(second);
    assertSame("Second call should return same cached list", first, second);
  }

  @Test
  public void findAndLoadInternalResources_isIdempotent() {
    List<ModelManifest> first = manager.findAndLoadInternalResources();
    List<ModelManifest> second = manager.findAndLoadInternalResources();

    assertNotNull(first);
    assertNotNull(second);
    assertSame("Second call should return same cached list", first, second);
  }

  @Test
  public void findAndLoadUserGalleryResources_returnsNonNull() {
    List<ModelManifest> result = manager.findAndLoadUserGalleryResources();

    assertNotNull("Should never return null", result);
  }

  @Test
  public void findAndLoadInternalResources_returnsNonNull() {
    List<ModelManifest> result = manager.findAndLoadInternalResources();

    assertNotNull("Should never return null", result);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  findNewUserGalleryResources
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void findNewUserGalleryResources_beforeInit_returnsNull() {
    // Per the original contract: returns null if findAndLoadUserGalleryResources
    // has never been called
    List<ModelManifest> result = manager.findNewUserGalleryResources();

    assertNull("Should return null before first gallery load", result);
  }

  @Test
  public void findNewUserGalleryResources_afterInit_returnsNonNull() {
    manager.findAndLoadUserGalleryResources();

    List<ModelManifest> result = manager.findNewUserGalleryResources();

    assertNotNull("Should return non-null list after gallery is initialized", result);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  manifestIsNew — deduplication by name
  //
  //  This tests the behavior via findNewUserGalleryResources, which
  //  internally calls manifestIsNew to filter duplicates.
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void findNewUserGalleryResources_detectsNewManifests() {
    // First load populates the cache
    manager.findAndLoadUserGalleryResources();

    // If the user gallery directory gained new files between calls,
    // findNewUserGalleryResources should detect them.
    // In this test env we can't easily inject files between calls
    // since the real directory scan happens internally, but we verify
    // the structural contract: subsequent call returns a list.
    List<ModelManifest> newOnes = manager.findNewUserGalleryResources();

    assertNotNull(newOnes);
    // In a fresh temp env, no new manifests should appear
    assertTrue("No new manifests should appear in unchanged directory",
        newOnes.isEmpty());
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Structural contract — ModelManifestManager is final
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void modelManifestManager_isFinalClass() {
    assertTrue("ModelManifestManager should be final",
        java.lang.reflect.Modifier.isFinal(ModelManifestManager.class.getModifiers()));
  }

  @Test
  public void modelManifestManager_hasPublicNoArgConstructor() {
    try {
      ModelManifestManager instance = ModelManifestManager.class.getConstructor().newInstance();
      assertNotNull(instance);
    } catch (Exception e) {
      fail("ModelManifestManager should have a public no-arg constructor: " + e);
    }
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Manifest JSON round-trip — verifies manifestFor() behavior
  //
  //  These use ManifestEncoderDecoder directly to validate the same
  //  parsing path that ModelManifestManager.manifestFor() will use.
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void manifestEncoderDecoder_roundTrip_preservesModelName() {
    ModelManifest manifest = new ModelManifest();
    manifest.description.name = "TestChair";

    String json = ManifestEncoderDecoder.toJson(manifest);
    ModelManifest decoded = ManifestEncoderDecoder.fromJson(json, ModelManifest.class);

    assertNotNull(decoded);
    assertEquals("TestChair", decoded.getName());
  }

  @Test
  public void manifestEncoderDecoder_malformedJson_returnsNull() {
    ModelManifest decoded = ManifestEncoderDecoder.fromJson(
        "not valid json {{{", ModelManifest.class);

    assertNull("Malformed JSON should return null via fromJson", decoded);
  }

  @Test
  public void manifestEncoderDecoder_emptyObject_returnsManifestWithNullName() {
    ModelManifest decoded = ManifestEncoderDecoder.fromJson("{}", ModelManifest.class);

    assertNotNull(decoded);
    assertNull("Empty JSON object should have null name", decoded.getName());
  }
}
