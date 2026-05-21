package org.lgna.story.resourceutilities;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class StorytellingResourcesPathTest {
  @Test
  public void galleryDirectoryFindsAncestorContainingAssetsAlice() {
    File root = artifact("gallery-root");
    File aliceDir = new File(root, "nested/assets/alice");
    assertTrue(aliceDir.mkdirs() || aliceDir.isDirectory());

    File result = StorytellingResources.getGalleryDirectory(root);

    assertEquals(new File(root, "nested").getAbsoluteFile(), result.getAbsoluteFile());
  }

  @Test
  public void galleryPathHelperReturnsGalleryRootAndTrimsTrailingSeparators() throws Exception {
    File root = artifact("gallery-helper");
    File resourceDir = new File(root, "assets/alice/characters");
    assertTrue(resourceDir.mkdirs() || resourceDir.isDirectory());

    String value = (String) privateMethod("getGalleryPathFromResourcePath", String.class)
        .invoke(null, resourceDir.getAbsolutePath() + File.separator);

    assertEquals(root.getAbsolutePath(), value);
  }

  @Test
  public void galleryPathsHelperNormalizesSeparatorsAndDeduplicates() throws Exception {
    File first = artifact("paths-a");
    File second = artifact("paths-b");
    assertTrue(new File(first, "assets/alice").mkdirs() || new File(first, "assets/alice").isDirectory());
    assertTrue(new File(second, "assets/alice").mkdirs() || new File(second, "assets/alice").isDirectory());

    String separator = System.getProperty("path.separator");
    String resourcePaths = first.getAbsolutePath().replace(File.separatorChar, '\\') + "\\assets\\alice" + separator
        + second.getAbsolutePath() + File.separator + "assets" + File.separator + "alice" + separator
        + first.getAbsolutePath() + File.separator + "assets" + File.separator + "alice";

    String[] galleryPaths = (String[]) privateMethod("getGalleryPathsFromResourcePath", String.class).invoke(null, resourcePaths);

    assertArrayEquals(new String[] {first.getAbsolutePath(), second.getAbsolutePath()}, galleryPaths);
  }

  @Test
  public void getDirsFromPrefReturnsConfiguredDirectoriesWithRelativeSuffix() {
    StorytellingResources.INSTANCE.setGalleryResourceDirs(new String[] {artifact("pref-one").getAbsolutePath(), artifact("pref-two").getAbsolutePath()});

    File[] dirs = StorytellingResources.getDirsFromPref(StorytellingResources.GALLERY_DIRECTORY_PREF_KEY, "/assets/alice");

    assertEquals(2, dirs.length);
    assertTrue(dirs[0].getPath().endsWith("assets" + File.separator + "alice"));
    assertTrue(dirs[1].getPath().endsWith("assets" + File.separator + "alice"));
  }

  private static Method privateMethod(String name, Class<?>... parameterTypes) throws Exception {
    Method method = StorytellingResources.class.getDeclaredMethod(name, parameterTypes);
    method.setAccessible(true);
    return method;
  }

  private static File artifact(String name) {
    File dir = new File("target/test-artifacts/StorytellingResourcesPathTest/" + name).getAbsoluteFile();
    dir.mkdirs();
    return dir;
  }
}
