package org.lgna.story.resourceutilities;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * TDD tests verifying the decomposition of {@code StorytellingResources}
 * into {@code ResourceClassLoader} and {@code ModelManifestManager}.
 *
 * <p>These tests verify:</p>
 * <ul>
 *   <li>Delegation: facade methods produce the same results as calling
 *       the extracted class directly</li>
 *   <li>Structure: fields moved, dead code removed, DIR_FILE_FILTER replaced</li>
 *   <li>API preservation: all public/package-private method signatures unchanged</li>
 *   <li>Cross-module compatibility: package-private members still accessible</li>
 * </ul>
 *
 * <p><b>RED PHASE:</b> Structural tests referencing {@code ResourceClassLoader}
 * and {@code ModelManifestManager} will not compile until those classes are
 * created. Reflection-based tests compile but fail at runtime until the
 * refactoring is complete.</p>
 *
 * @see StorytellingResources
 * @see ResourceClassLoader
 * @see ModelManifestManager
 */
public class StorytellingResourcesDecompositionTest {

  // ═══════════════════════════════════════════════════════════════════════
  //  Delegation: getClassNamesFromResources
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void getClassNamesFromResources_delegatesToResourceClassLoader() {
    // Both the facade (StorytellingResources) and the extracted class
    // should produce identical results for the same input
    File tempDir = new File(System.getProperty("java.io.tmpdir"));

    Map<File, List<String>> facadeResult =
        StorytellingResources.getClassNamesFromResources(tempDir);
    Map<File, List<String>> directResult =
        ResourceClassLoader.getClassNamesFromResources(tempDir);

    assertEquals("Facade should delegate to ResourceClassLoader",
        directResult, facadeResult);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Delegation: getModelManifest / getInternalModelManifest
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void getModelManifest_delegatesToManifestManager() {
    // Calling with a name that won't exist should return null from both
    assertNull("Facade should return null for unknown model",
        StorytellingResources.INSTANCE.getModelManifest("__test_nonexistent__"));
  }

  @Test
  public void getInternalModelManifest_delegatesToManifestManager() {
    assertNull("Facade should return null for unknown internal model",
        StorytellingResources.INSTANCE.getInternalModelManifest("__test_nonexistent__"));
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Structural: StorytellingResources holds a ModelManifestManager field
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void storyResources_hasManifestManagerField() {
    boolean found = false;
    for (Field f : StorytellingResources.class.getDeclaredFields()) {
      if (f.getType() == ModelManifestManager.class) {
        found = true;
        assertTrue("manifestManager field should be private",
            Modifier.isPrivate(f.getModifiers()));
        break;
      }
    }
    assertTrue("StorytellingResources should hold a ModelManifestManager field", found);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Structural: DIR_FILE_FILTER field removed
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void dirFileFilter_isRemoved() {
    for (Field f : StorytellingResources.class.getDeclaredFields()) {
      assertNotEquals("DIR_FILE_FILTER should be removed (replaced with File::isDirectory)",
          "DIR_FILE_FILTER", f.getName());
    }
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Structural: manifest fields moved to ModelManifestManager
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void userGalleryModelManifests_movedOutOfStorytellingResources() {
    Set<String> fieldNames = Arrays.stream(StorytellingResources.class.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toSet());

    assertFalse("userGalleryModelManifests should be moved to ModelManifestManager",
        fieldNames.contains("userGalleryModelManifests"));
  }

  @Test
  public void internalModelManifests_movedOutOfStorytellingResources() {
    Set<String> fieldNames = Arrays.stream(StorytellingResources.class.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toSet());

    assertFalse("internalModelManifests should be moved to ModelManifestManager",
        fieldNames.contains("internalModelManifests"));
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Structural: key fields that STAY on StorytellingResources
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void resourceClassLoaders_remainsOnStorytellingResources() {
    boolean found = Arrays.stream(StorytellingResources.class.getDeclaredFields())
        .anyMatch(f -> f.getName().equals("resourceClassLoaders"));

    assertTrue("resourceClassLoaders field should remain on StorytellingResources", found);
  }

  @Test
  public void installedAliceClassesLoaded_remainsOnStorytellingResources() {
    boolean found = Arrays.stream(StorytellingResources.class.getDeclaredFields())
        .anyMatch(f -> f.getName().equals("installedAliceClassesLoaded"));

    assertTrue("installedAliceClassesLoaded field should remain on StorytellingResources", found);
  }

  @Test
  public void galleryDirectoryPrefKey_remainsAccessible() {
    // Package-private constant used by NebulousStorytellingResources
    boolean found = Arrays.stream(StorytellingResources.class.getDeclaredFields())
        .anyMatch(f -> f.getName().equals("GALLERY_DIRECTORY_PREF_KEY"));

    assertTrue("GALLERY_DIRECTORY_PREF_KEY must remain on StorytellingResources", found);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  API preservation: public method signatures unchanged
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void publicApi_getClassNamesFromResources_signaturePreserved() throws NoSuchMethodException {
    Method m = StorytellingResources.class.getMethod("getClassNamesFromResources", File[].class);
    assertTrue("Should remain public", Modifier.isPublic(m.getModifiers()));
    assertTrue("Should remain static", Modifier.isStatic(m.getModifiers()));
    assertEquals(Map.class, m.getReturnType());
  }

  @Test
  public void publicApi_getModelManifest_signaturePreserved() throws NoSuchMethodException {
    Method m = StorytellingResources.class.getMethod("getModelManifest", String.class);
    assertTrue("Should remain public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void publicApi_getInternalModelManifest_signaturePreserved() throws NoSuchMethodException {
    Method m = StorytellingResources.class.getMethod("getInternalModelManifest", String.class);
    assertTrue("Should remain public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void publicApi_getGalleryDirectory_signaturePreserved() throws NoSuchMethodException {
    Method m = StorytellingResources.class.getMethod("getGalleryDirectory", File.class);
    assertTrue("Should remain public", Modifier.isPublic(m.getModifiers()));
    assertTrue("Should remain static", Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void publicApi_getAliceResource_signaturePreserved() throws NoSuchMethodException {
    Method m = StorytellingResources.class.getMethod("getAliceResource", String.class);
    assertTrue("Should remain public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void publicApi_getAliceResourceAsStream_signaturePreserved() throws NoSuchMethodException {
    Method m = StorytellingResources.class.getMethod("getAliceResourceAsStream", String.class);
    assertTrue("Should remain public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void publicApi_setAliceResourceDirs_signaturePreserved() throws NoSuchMethodException {
    Method m = StorytellingResources.class.getMethod("setAliceResourceDirs", String[].class);
    assertTrue("Should remain public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void publicApi_getAliceDirsFromPref_signaturePreserved() throws NoSuchMethodException {
    Method m = StorytellingResources.class.getMethod("getAliceDirsFromPref");
    assertTrue("Should remain public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void publicApi_setGalleryResourceDirs_signaturePreserved() throws NoSuchMethodException {
    Method m = StorytellingResources.class.getMethod("setGalleryResourceDirs", String[].class);
    assertTrue("Should remain public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void publicApi_getGalleryLocationFromUser_signaturePreserved() throws NoSuchMethodException {
    Method m = StorytellingResources.class.getMethod("getGalleryLocationFromUser");
    assertTrue("Should remain public", Modifier.isPublic(m.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  API preservation: package-private methods still present
  //  (used by NebulousStorytellingResources & StorytellingResourcesTreeUtils)
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void packagePrivateApi_getDirsFromPref_preserved() throws NoSuchMethodException {
    Method m = StorytellingResources.class.getDeclaredMethod("getDirsFromPref", String.class, String.class);
    assertFalse("getDirsFromPref should not be private", Modifier.isPrivate(m.getModifiers()));
    assertFalse("getDirsFromPref should not be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void packagePrivateApi_makeDirectoryPreferenceString_preserved() throws NoSuchMethodException {
    Method m = StorytellingResources.class.getDeclaredMethod("makeDirectoryPreferenceString", String[].class);
    assertFalse("Should not be private", Modifier.isPrivate(m.getModifiers()));
    assertFalse("Should not be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void packagePrivateApi_findResourcePath_preserved() throws NoSuchMethodException {
    Method m = StorytellingResources.class.getDeclaredMethod("findResourcePath", String.class);
    assertFalse("Should not be private", Modifier.isPrivate(m.getModifiers()));
    assertFalse("Should not be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void packagePrivateApi_findAndLoadInstalledAliceResourcesIfNecessary_preserved() {
    boolean found = Arrays.stream(StorytellingResources.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("findAndLoadInstalledAliceResourcesIfNecessary"));

    assertTrue("findAndLoadInstalledAliceResourcesIfNecessary must remain on StorytellingResources", found);
  }

  @Test
  public void packagePrivateApi_findAndLoadUserGalleryResourcesIfNecessary_preserved() {
    // This must remain as a delegate on StorytellingResources because
    // StorytellingResourcesTreeUtils in core/ide calls it by this name
    boolean found = Arrays.stream(StorytellingResources.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("findAndLoadUserGalleryResourcesIfNecessary"));

    assertTrue("findAndLoadUserGalleryResourcesIfNecessary must remain as delegate", found);
  }

  @Test
  public void packagePrivateApi_findNewUserGalleryResources_preserved() {
    // StorytellingResourcesTreeUtils calls this
    boolean found = Arrays.stream(StorytellingResources.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("findNewUserGalleryResources"));

    assertTrue("findNewUserGalleryResources must remain as delegate", found);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Dead code removal verification
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void deadCode_getPathFromProperties_removed() {
    // The commented-out getPathFromProperties method should be removed entirely
    // We verify by checking no method with that name exists
    boolean found = Arrays.stream(StorytellingResources.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getPathFromProperties"));

    assertFalse("Commented-out getPathFromProperties should be removed", found);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  ResourceClassLoader structural presence
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void resourceClassLoader_classExists() {
    try {
      Class<?> cls = Class.forName("org.lgna.story.resourceutilities.ResourceClassLoader");
      assertNotNull(cls);
      assertTrue("Should be public", Modifier.isPublic(cls.getModifiers()));
      assertTrue("Should be final", Modifier.isFinal(cls.getModifiers()));
    } catch (ClassNotFoundException e) {
      fail("ResourceClassLoader class not found — implementation needed");
    }
  }

  @Test
  public void resourceClassLoader_loadResultInnerClass_exists() {
    try {
      Class<?> cls = Class.forName("org.lgna.story.resourceutilities.ResourceClassLoader$LoadResult");
      assertNotNull(cls);
      assertTrue("LoadResult should be public", Modifier.isPublic(cls.getModifiers()));
      assertTrue("LoadResult should be static", Modifier.isStatic(cls.getModifiers()));
      assertTrue("LoadResult should be final", Modifier.isFinal(cls.getModifiers()));
    } catch (ClassNotFoundException e) {
      fail("ResourceClassLoader.LoadResult inner class not found — implementation needed");
    }
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  ModelManifestManager structural presence
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void modelManifestManager_classExists() {
    try {
      Class<?> cls = Class.forName("org.lgna.story.resourceutilities.ModelManifestManager");
      assertNotNull(cls);
      assertTrue("Should be public", Modifier.isPublic(cls.getModifiers()));
      assertTrue("Should be final", Modifier.isFinal(cls.getModifiers()));
    } catch (ClassNotFoundException e) {
      fail("ModelManifestManager class not found — implementation needed");
    }
  }

  @Test
  public void modelManifestManager_hasExpectedMethods() {
    try {
      Class<?> cls = Class.forName("org.lgna.story.resourceutilities.ModelManifestManager");
      Set<String> methodNames = Arrays.stream(cls.getDeclaredMethods())
          .map(Method::getName)
          .collect(Collectors.toSet());

      assertTrue("Should have findAndLoadUserGalleryResources",
          methodNames.contains("findAndLoadUserGalleryResources"));
      assertTrue("Should have findAndLoadInternalResources",
          methodNames.contains("findAndLoadInternalResources"));
      assertTrue("Should have findNewUserGalleryResources",
          methodNames.contains("findNewUserGalleryResources"));
      assertTrue("Should have getModelManifest",
          methodNames.contains("getModelManifest"));
      assertTrue("Should have getInternalModelManifest",
          methodNames.contains("getInternalModelManifest"));
      assertTrue("Should have getDynamicModelFiles",
          methodNames.contains("getDynamicModelFiles"));
    } catch (ClassNotFoundException e) {
      fail("ModelManifestManager class not found — implementation needed");
    }
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Line count contract (informational — verified post-build)
  //  This test documents the target, actual verification is via wc -l
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void lineCountTarget_storyResources_under500() {
    // This test serves as documentation of the target.
    // The actual line count is verified by the build script:
    //   wc -l StorytellingResources.java < 500
    // At this point we just assert the class compiles and is an enum.
    assertTrue("StorytellingResources should remain an enum",
        StorytellingResources.class.isEnum());
    assertEquals("Should have exactly one enum constant (INSTANCE)",
        1, StorytellingResources.class.getEnumConstants().length);
  }
}
