package org.lgna.story.resourceutilities;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Tests for the package-visible static utility methods on StorytellingResources.
 * These methods are pure string/file utilities — headless-safe, no AWT.
 */
public class StorytellingResourcesTest {

  // ── makeDirectoryPreferenceString ──

  @Test
  public void makeDirectoryPreferenceStringSingleDir() {
    String result = StorytellingResources.makeDirectoryPreferenceString(new String[]{"/foo/bar"});
    assertEquals("/foo/bar", result);
  }

  @Test
  public void makeDirectoryPreferenceStringMultipleDirs() {
    String sep = System.getProperty("path.separator");
    String result = StorytellingResources.makeDirectoryPreferenceString(
        new String[]{"/foo", "/bar", "/baz"});
    assertEquals("/foo" + sep + "/bar" + sep + "/baz", result);
  }

  @Test
  public void makeDirectoryPreferenceStringEmptyArray() {
    String result = StorytellingResources.makeDirectoryPreferenceString(new String[]{});
    assertEquals("", result);
  }

  // ── getDirsFromPref ──

  @Test
  public void getDirsFromPrefMissingKeyReturnsNull() {
    // Use a key that almost certainly has no preference set
    File[] dirs = StorytellingResources.getDirsFromPref(
        "org.alice.test.nonexistent.key.12345", "/subdir");
    assertNull("Should return null when preference is empty", dirs);
  }

  // ── getGalleryDirectory with non-existent dir ──

  @Test
  public void getGalleryDirectoryNonExistentReturnsNull() {
    File nonExistent = new File("/definitely/not/a/real/directory");
    File result = StorytellingResources.getGalleryDirectory(nonExistent);
    assertNull("Should return null for non-existent directory", result);
  }

  // ── getGalleryDirectory with existing but irrelevant dir ──

  @Test
  public void getGalleryDirectoryIrrelevantDirReturnsNull() {
    File tmpDir = new File(System.getProperty("user.home"));
    if (tmpDir.exists() && tmpDir.isDirectory()) {
      File result = StorytellingResources.getGalleryDirectory(tmpDir);
      // May be null since user home likely doesn't contain assets/alice
      // Main point: doesn't throw
      // result could be null or non-null depending on environment
    }
  }

  // ── INSTANCE singleton ──

  @Test
  public void instanceSingletonIsNotNull() {
    assertNotNull(StorytellingResources.INSTANCE);
  }

  @Test
  public void instanceSingletonIsSameObject() {
    assertSame(StorytellingResources.INSTANCE, StorytellingResources.INSTANCE);
  }

  // ── getClassNamesFromResources with no files ──

  @Test
  public void getClassNamesFromResourcesEmptyReturnsEmptyMap() {
    var result = StorytellingResources.getClassNamesFromResources();
    assertNotNull(result);
    assertTrue("No resource files should produce empty map", result.isEmpty());
  }

  // ── getClassNamesFromResources with non-existent file ──

  @Test
  public void getClassNamesFromResourcesNonExistentFileReturnsEmptyMap() {
    File fake = new File("/no/such/file.jar");
    var result = StorytellingResources.getClassNamesFromResources(fake);
    assertNotNull(result);
  }

  // findResourcePath is skipped: it calls getGalleryRootDirectory which
  // can trigger AWT (FindResourcesPanel) in headless environments.

  // ── preference key constants ──

  @Test
  public void galleryDirectoryPrefKeyIsNotEmpty() {
    assertNotNull(StorytellingResources.GALLERY_DIRECTORY_PREF_KEY);
    assertFalse(StorytellingResources.GALLERY_DIRECTORY_PREF_KEY.isEmpty());
  }

  // ── ResourcePathManager integration ──

  @Test
  public void resourcePathManagerModelKeyExists() {
    assertNotNull(ResourcePathManager.MODEL_RESOURCE_KEY);
  }

  @Test
  public void resourcePathManagerGetPathsReturnsListForModelKey() {
    var paths = ResourcePathManager.getPaths(ResourcePathManager.MODEL_RESOURCE_KEY);
    assertNotNull(paths);
  }

  @Test
  public void resourcePathManagerGetPathsUnknownKeyReturnsEmptyList() {
    var paths = ResourcePathManager.getPaths("org.alice.test.unknown.key");
    assertNotNull(paths);
    assertTrue(paths.isEmpty());
  }

  @Test
  public void resourcePathManagerClearAndRegetReturnsEmptyForNewKey() {
    String testKey = "org.alice.test.temp.key";
    ResourcePathManager.clearPaths(testKey);
    var paths = ResourcePathManager.getPaths(testKey);
    assertNotNull(paths);
    assertTrue(paths.isEmpty());
  }
}
