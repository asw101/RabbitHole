package org.lgna.story.resourceutilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ResourcePathManagerTest {
  private static final String TEST_KEY = "org.alice.test.resourcePath";

  private Map<String, List<File>> originalPaths;

  @Before
  public void snapshotState() throws Exception {
    originalPaths = deepCopy(resourcePathMap());
    System.clearProperty(TEST_KEY);
  }

  @After
  public void restoreState() throws Exception {
    Map<String, List<File>> map = resourcePathMap();
    map.clear();
    map.putAll(deepCopy(originalPaths));
    System.clearProperty(TEST_KEY);
  }

  @Test
  public void initializePathKeepsOnlyExistingDirectories() throws Exception {
    File first = artifact("first");
    File second = artifact("second");
    File missing = new File(artifactRoot(), "missing-dir");

    System.setProperty(TEST_KEY,
        first.getAbsolutePath() + File.pathSeparator + missing.getAbsolutePath() + File.pathSeparator + second.getAbsolutePath());
    initializePath(TEST_KEY);

    assertEquals(list(first, second), ResourcePathManager.getPaths(TEST_KEY));
  }

  @Test
  public void addPathRejectsUnknownKeysDuplicatesAndNonDirectories() throws Exception {
    File first = artifact("existing");
    File second = artifact("second");
    File missing = new File(artifactRoot(), "not-a-directory");

    System.setProperty(TEST_KEY, first.getAbsolutePath());
    initializePath(TEST_KEY);

    assertFalse(ResourcePathManager.addPath("org.alice.test.unknown", first));
    assertFalse(ResourcePathManager.addPath(TEST_KEY, first));
    assertFalse(ResourcePathManager.addPath(TEST_KEY, missing));
    assertTrue(ResourcePathManager.addPath(TEST_KEY, second));
    assertEquals(list(first, second), ResourcePathManager.getPaths(TEST_KEY));
  }

  @Test
  public void clearPathsRemovesEntryAndMissingKeyGetsFreshEmptyList() throws Exception {
    File only = artifact("clearable");
    System.setProperty(TEST_KEY, only.getAbsolutePath());
    initializePath(TEST_KEY);

    ResourcePathManager.clearPaths(TEST_KEY);

    List<File> firstLookup = ResourcePathManager.getPaths(TEST_KEY);
    assertTrue(firstLookup.isEmpty());
    firstLookup.add(new File("should-not-stick"));
    assertTrue(ResourcePathManager.getPaths(TEST_KEY).isEmpty());
  }

  @Test
  public void constructorThrowsAssertionError() throws Exception {
    Constructor<ResourcePathManager> constructor = ResourcePathManager.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    try {
      constructor.newInstance();
      fail("Expected constructor to reject instantiation");
    } catch (InvocationTargetException exception) {
      assertTrue(exception.getCause() instanceof AssertionError);
    }
  }

  private static void initializePath(String key) throws Exception {
    Method method = ResourcePathManager.class.getDeclaredMethod("initializePath", String.class);
    method.setAccessible(true);
    method.invoke(null, key);
  }

  @SuppressWarnings("unchecked")
  private static Map<String, List<File>> resourcePathMap() throws Exception {
    Field field = ResourcePathManager.class.getDeclaredField("resourcePathMap");
    field.setAccessible(true);
    return (Map<String, List<File>>) field.get(null);
  }

  private static Map<String, List<File>> deepCopy(Map<String, List<File>> source) {
    Map<String, List<File>> copy = new LinkedHashMap<>();
    for (Map.Entry<String, List<File>> entry : source.entrySet()) {
      copy.put(entry.getKey(), new LinkedList<>(entry.getValue()));
    }
    return copy;
  }

  private static List<File> list(File... files) {
    List<File> values = new LinkedList<>();
    for (File file : files) {
      values.add(file.getAbsoluteFile());
    }
    return values;
  }

  private static File artifact(String name) {
    File dir = new File(artifactRoot(), name).getAbsoluteFile();
    assertTrue(dir.mkdirs() || dir.isDirectory());
    return dir;
  }

  private static File artifactRoot() {
    File dir = new File("target/test-artifacts/ResourcePathManagerTest").getAbsoluteFile();
    assertTrue(dir.mkdirs() || dir.isDirectory());
    return dir;
  }
}
