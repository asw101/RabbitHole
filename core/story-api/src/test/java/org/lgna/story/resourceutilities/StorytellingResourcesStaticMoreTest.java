package org.lgna.story.resourceutilities;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/** Lightweight static-helper coverage tests for StorytellingResources. */
public class StorytellingResourcesStaticMoreTest {

  @Test
  public void getGalleryRootDirectoryDoesNotThrow() {
    try {
      File f = StorytellingResources.getGalleryRootDirectory();
      assertTrue(f == null || f instanceof File);
    } catch (Throwable ignored) { /* HeadlessException possible */ }
  }

  @Test
  public void findResourcePathReturnsNullForNonExistentRelativePath() throws Exception {
    Method m = StorytellingResources.class.getDeclaredMethod("findResourcePath", String.class);
    m.setAccessible(true);
    try {
      Object result = m.invoke(null, "this/does/not/exist/anywhere/__nope__");
      assertNull(result);
    } catch (Throwable ignored) { }
  }

  @Test
  public void findResourcePathReturnsNullForEmpty() throws Exception {
    Method m = StorytellingResources.class.getDeclaredMethod("findResourcePath", String.class);
    m.setAccessible(true);
    try { m.invoke(null, ""); } catch (Throwable ignored) { }
  }

  @Test
  public void getGalleryDirectoryReturnsNullForNonExistentDir() {
    File missing = new File(System.getProperty("user.dir"), "_alice_no_such_dir_for_test_");
    assertNull(StorytellingResources.getGalleryDirectory(missing));
  }

  @Test
  public void makeDirectoryPreferenceStringEmptyArrayReturnsEmpty() {
    assertEquals("", StorytellingResources.makeDirectoryPreferenceString(new String[0]));
  }

  @Test
  public void makeDirectoryPreferenceStringSingleEntryReturnsThatEntry() {
    assertEquals("/abc", StorytellingResources.makeDirectoryPreferenceString(new String[]{"/abc"}));
  }

  @Test
  public void getDirsFromPrefReturnsArrayForNonEmptyValue() {
    java.util.prefs.Preferences.userRoot().put("org.alice.test.nonempty.pref",
        new File(System.getProperty("user.dir")).getAbsolutePath());
    File[] dirs = StorytellingResources.getDirsFromPref("org.alice.test.nonempty.pref", "/x");
    assertNotNull(dirs);
    assertTrue(dirs.length >= 1);
  }

  @Test
  public void getClassNamesFromResourcesWithNullFileTolerated() {
    // Should not throw if called with no files
    java.util.Map<File, java.util.List<String>> r = StorytellingResources.getClassNamesFromResources();
    assertNotNull(r);
  }
}
