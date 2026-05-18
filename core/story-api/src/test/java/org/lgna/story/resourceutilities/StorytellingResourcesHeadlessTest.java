package org.lgna.story.resourceutilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import static org.junit.Assert.*;

/** Additional headless-safe tests for StorytellingResources preference and path logic. */
public class StorytellingResourcesHeadlessTest {
  private static final String ALICE_RESOURCE_DIRECTORY_PREF_KEY = "ALICE_RESOURCE_DIRECTORY_PREF_KEY";
  private String originalAlicePref;
  private String originalGalleryPref;

  @Before
  public void savePreferences() {
    Preferences prefs = Preferences.userRoot();
    originalAlicePref = prefs.get(ALICE_RESOURCE_DIRECTORY_PREF_KEY, "");
    originalGalleryPref = prefs.get(StorytellingResources.GALLERY_DIRECTORY_PREF_KEY, "");
  }

  @After
  public void restorePreferences() {
    Preferences prefs = Preferences.userRoot();
    prefs.put(ALICE_RESOURCE_DIRECTORY_PREF_KEY, originalAlicePref == null ? "" : originalAlicePref);
    prefs.put(StorytellingResources.GALLERY_DIRECTORY_PREF_KEY, originalGalleryPref == null ? "" : originalGalleryPref);
  }

  @Test
  public void reflectedGalleryPathParsingReturnsExistingBaseDirectory() throws Exception {
    String base = new File(System.getProperty("user.dir")).getAbsolutePath();
    String parsed = (String) invokePrivate("getGalleryPathFromResourcePath", base + "/assets/alice/characters");
    assertEquals(base, parsed);
  }

  @Test
  public void reflectedGalleryPathParsingDeduplicatesMultipleEntries() throws Exception {
    String base = new File(System.getProperty("user.dir")).getAbsolutePath();
    String separator = System.getProperty("path.separator");
    String combined = base + "/assets/alice" + separator + base + "/assets/alice/models";
    String[] parsed = (String[]) invokePrivate("getGalleryPathsFromResourcePath", combined);
    assertEquals(1, parsed.length);
    assertEquals(base, parsed[0]);
  }

  @Test
  public void setGalleryResourceDirsStoresPathsAccessibleThroughGetDirsFromPref() {
    String base = new File(System.getProperty("user.dir")).getAbsolutePath();
    StorytellingResources.INSTANCE.setGalleryResourceDirs(new String[]{base});

    File[] dirs = StorytellingResources.getDirsFromPref(StorytellingResources.GALLERY_DIRECTORY_PREF_KEY, "/assets/alice");

    assertNotNull(dirs);
    assertEquals(new File(base + "/assets/alice"), dirs[0]);
  }

  @Test
  public void setAliceResourceDirsRoundTripsThroughGetAliceDirsFromPref() {
    String base = new File(System.getProperty("user.dir")).getAbsolutePath();
    StorytellingResources.INSTANCE.setAliceResourceDirs(new String[]{base});

    File[] dirs = StorytellingResources.INSTANCE.getAliceDirsFromPref();

    assertNotNull(dirs);
    assertEquals(new File(base), dirs[0]);
  }

  @Test
  public void getGalleryDirectoryReturnsNullForRegularFile() {
    assertNull(StorytellingResources.getGalleryDirectory(new File("pom.xml")));
  }

  @Test
  public void getDirsFromPrefReturnsNullWhenPreferenceIsBlank() {
    Preferences.userRoot().put("org.alice.test.blank.pref", "");
    assertNull(StorytellingResources.getDirsFromPref("org.alice.test.blank.pref", "/assets/alice"));
  }

  @Test
  public void getClassNamesFromResourcesWithNoArgumentsReturnsEmptyMap() {
    Map<File, List<String>> result = StorytellingResources.getClassNamesFromResources();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void makeDirectoryPreferenceStringPreservesOrder() {
    String separator = System.getProperty("path.separator");
    String joined = StorytellingResources.makeDirectoryPreferenceString(new String[]{"/alpha", "/beta"});
    assertEquals("/alpha" + separator + "/beta", joined);
  }

  @Test
  public void reflectedGalleryPathParsingReturnsNullForNullInput() throws Exception {
    assertNull(invokePrivate("getGalleryPathFromResourcePath", null));
  }

  private static Object invokePrivate(String methodName, Object argument) throws Exception {
    Method method = StorytellingResources.class.getDeclaredMethod(methodName, String.class);
    method.setAccessible(true);
    return method.invoke(null, argument);
  }
}
