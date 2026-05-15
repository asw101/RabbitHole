package org.lgna.story.resourceutilities;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;

/**
 * TDD tests for {@link ResourceClassLoader} — a static utility class
 * extracted from {@code StorytellingResources} that handles discovery and
 * loading of {@code ModelResource} classes from jar/directory trees.
 *
 * <p><b>RED PHASE:</b> These tests will not compile until
 * {@code ResourceClassLoader} and its inner {@code LoadResult} class are
 * created. Once the class exists they should compile, and once the
 * implementation is complete they should all pass.</p>
 *
 * @see ResourceClassLoader
 */
public class ResourceClassLoaderTest {

  @Rule
  public TemporaryFolder tempDir = new TemporaryFolder();

  private File resourceDir;

  @Before
  public void setUp() throws IOException {
    resourceDir = tempDir.newFolder("resources");
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  getClassNamesFromResources — directory scanning
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void getClassNamesFromResources_emptyDirectory_returnsEmptyMap() {
    Map<File, List<String>> result =
        ResourceClassLoader.getClassNamesFromResources(resourceDir);

    assertNotNull(result);
    assertTrue("Empty directory should produce empty map", result.isEmpty());
  }

  @Test
  public void getClassNamesFromResources_directoryWithXmlFiles_findsClassNames() throws IOException {
    // Create a nested structure mimicking Alice resource layout:
    //   resources/org/lgna/story/resources/prop/Chair.xml
    File packageDir = new File(resourceDir, "org/lgna/story/resources/prop");
    assertTrue(packageDir.mkdirs());
    new File(packageDir, "Chair.xml").createNewFile();

    Map<File, List<String>> result =
        ResourceClassLoader.getClassNamesFromResources(resourceDir);

    assertNotNull(result);
    assertTrue("Should find at least one resource file entry", result.containsKey(resourceDir));
    List<String> classNames = result.get(resourceDir);
    assertEquals(1, classNames.size());
    // The class name should end with "Resource" (RESOURCE_SUFFIX)
    assertTrue("Class name should end with Resource suffix",
        classNames.get(0).endsWith("Resource"));
    // Should contain the package path
    assertTrue("Class name should contain package segments",
        classNames.get(0).contains("org.lgna.story.resources.prop"));
  }

  @Test
  public void getClassNamesFromResources_skipsInnerClassFiles() throws IOException {
    // Inner class files contain '$' — should be excluded
    File packageDir = new File(resourceDir, "org/lgna/story/resources");
    assertTrue(packageDir.mkdirs());
    new File(packageDir, "Outer.xml").createNewFile();
    new File(packageDir, "Outer$Inner.xml").createNewFile();

    Map<File, List<String>> result =
        ResourceClassLoader.getClassNamesFromResources(resourceDir);

    List<String> classNames = result.get(resourceDir);
    assertEquals("Should find only the outer class, not inner", 1, classNames.size());
  }

  @Test
  public void getClassNamesFromResources_multipleXmlFiles_findsAll() throws IOException {
    File packageDir = new File(resourceDir, "org/lgna/story/resources");
    assertTrue(packageDir.mkdirs());
    new File(packageDir, "Chair.xml").createNewFile();
    new File(packageDir, "Table.xml").createNewFile();
    new File(packageDir, "Lamp.xml").createNewFile();

    Map<File, List<String>> result =
        ResourceClassLoader.getClassNamesFromResources(resourceDir);

    List<String> classNames = result.get(resourceDir);
    assertEquals("Should find all three XML files", 3, classNames.size());
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  getClassNamesFromResources — zip/jar scanning
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void getClassNamesFromResources_zipFile_findsClassNames() throws IOException {
    File zipFile = createTestZip("test.jar",
        "org/lgna/story/resources/prop/Chair.xml",
        "org/lgna/story/resources/prop/Table.xml");

    Map<File, List<String>> result =
        ResourceClassLoader.getClassNamesFromResources(zipFile);

    assertNotNull(result);
    assertTrue(result.containsKey(zipFile));
    List<String> classNames = result.get(zipFile);
    assertEquals("Should find two classes in zip", 2, classNames.size());
  }

  @Test
  public void getClassNamesFromResources_zipFile_skipsInnerClassEntries() throws IOException {
    File zipFile = createTestZip("test.jar",
        "org/lgna/story/resources/Chair.xml",
        "org/lgna/story/resources/Chair$Variant.xml");

    Map<File, List<String>> result =
        ResourceClassLoader.getClassNamesFromResources(zipFile);

    List<String> classNames = result.get(zipFile);
    assertEquals("Should skip inner class entry in zip", 1, classNames.size());
  }

  @Test
  public void getClassNamesFromResources_zipFile_ignoresNonXmlEntries() throws IOException {
    File zipFile = createTestZip("test.jar",
        "org/lgna/story/resources/Chair.xml",
        "org/lgna/story/resources/Chair.class",
        "META-INF/MANIFEST.MF");

    Map<File, List<String>> result =
        ResourceClassLoader.getClassNamesFromResources(zipFile);

    List<String> classNames = result.get(zipFile);
    assertEquals("Should find only the XML entry", 1, classNames.size());
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  getClassNamesFromResources — mixed & edge cases
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void getClassNamesFromResources_multipleResourceFiles_separateEntries() throws IOException {
    // Create two separate resource directories
    File dir1 = tempDir.newFolder("resources1", "org", "lgna");
    new File(dir1, "Chair.xml").createNewFile();

    File dir2 = tempDir.newFolder("resources2", "org", "lgna");
    new File(dir2, "Table.xml").createNewFile();

    File root1 = new File(tempDir.getRoot(), "resources1");
    File root2 = new File(tempDir.getRoot(), "resources2");

    Map<File, List<String>> result =
        ResourceClassLoader.getClassNamesFromResources(root1, root2);

    assertEquals("Should have separate entries for each resource root", 2, result.size());
    assertTrue(result.containsKey(root1));
    assertTrue(result.containsKey(root2));
  }

  @Test
  public void getClassNamesFromResources_noArgs_returnsEmptyMap() {
    Map<File, List<String>> result =
        ResourceClassLoader.getClassNamesFromResources();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  LoadResult value class
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void loadResult_isEmpty_trueWhenNoClasses() {
    ResourceClassLoader.LoadResult result =
        ResourceClassLoader.loadClassesFromResourceFiles(
            Collections.emptyList(), resourceDir);

    assertNotNull(result);
    assertTrue("Empty class list should produce isEmpty() == true", result.isEmpty());
    assertNotNull(result.classes());
    assertTrue(result.classes().isEmpty());
  }

  @Test
  public void loadResult_classLoaders_neverNull() {
    ResourceClassLoader.LoadResult result =
        ResourceClassLoader.loadClassesFromResourceFiles(
            Collections.emptyList(), resourceDir);

    assertNotNull("classLoaders() should never be null", result.classLoaders());
  }

  @Test
  public void loadResult_classLoaders_containsCreatedClassLoader() {
    ResourceClassLoader.LoadResult result =
        ResourceClassLoader.loadClassesFromResourceFiles(
            Collections.emptyList(), resourceDir);

    // Even with no classes to load, the URLClassLoader is created for the resource path
    assertFalse("Should contain the URLClassLoader created for the resource files",
        result.classLoaders().isEmpty());
    assertTrue("ClassLoader should be a URLClassLoader",
        result.classLoaders().get(0) instanceof URLClassLoader);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  loadClassesFromResourceFiles
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void loadClassesFromResourceFiles_unknownClass_returnsEmptyClasses() {
    List<String> fakeClassNames = List.of("com.nonexistent.FakeModelResource");

    ResourceClassLoader.LoadResult result =
        ResourceClassLoader.loadClassesFromResourceFiles(fakeClassNames, resourceDir);

    assertNotNull(result);
    assertTrue("Unknown classes should not appear in result", result.classes().isEmpty());
    // The classloader should still be created
    assertFalse(result.classLoaders().isEmpty());
  }

  @Test
  public void loadClassesFromResourceFiles_multipleResourceFiles_singleClassLoader() throws IOException {
    File dir1 = tempDir.newFolder("lib1");
    File dir2 = tempDir.newFolder("lib2");

    ResourceClassLoader.LoadResult result =
        ResourceClassLoader.loadClassesFromResourceFiles(
            Collections.emptyList(), dir1, dir2);

    // Multiple resource files should be combined into one URLClassLoader
    assertEquals("Should create exactly one classloader for the batch",
        1, result.classLoaders().size());
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  getAndLoadModelResourceClasses — full pipeline
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void getAndLoadModelResourceClasses_emptyPathList_returnsEmptyResult() {
    ResourceClassLoader.LoadResult result =
        ResourceClassLoader.getAndLoadModelResourceClasses(Collections.emptyList());

    assertNotNull(result);
    assertTrue("Empty path list should produce empty result", result.isEmpty());
  }

  @Test
  public void getAndLoadModelResourceClasses_nonExistentPath_returnsEmptyResult() {
    File nonExistent = new File(tempDir.getRoot(), "does-not-exist");

    ResourceClassLoader.LoadResult result =
        ResourceClassLoader.getAndLoadModelResourceClasses(List.of(nonExistent));

    assertNotNull(result);
    assertTrue("Non-existent paths should be skipped", result.isEmpty());
  }

  @Test
  public void getAndLoadModelResourceClasses_directoryWithJars_expandsAndScans() throws IOException {
    // Create a directory containing a jar with XML entries
    File libDir = tempDir.newFolder("gallery");
    createTestZipIn(libDir, "models.jar",
        "org/lgna/story/resources/Chair.xml");

    ResourceClassLoader.LoadResult result =
        ResourceClassLoader.getAndLoadModelResourceClasses(List.of(libDir));

    // Even though the actual class won't load (it's a test), the pipeline should execute
    assertNotNull(result);
    // The classloaders list should be non-empty (a URLClassLoader was created)
    assertFalse("Pipeline should create classloader(s)", result.classLoaders().isEmpty());
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Class name derivation (tested via public getClassNamesFromResources)
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void classNameDerivation_convertsPathSeparatorsToPackageDots() throws IOException {
    File packageDir = new File(resourceDir, "org/lgna/story/resources");
    assertTrue(packageDir.mkdirs());
    new File(packageDir, "Chair.xml").createNewFile();

    Map<File, List<String>> result =
        ResourceClassLoader.getClassNamesFromResources(resourceDir);
    String className = result.get(resourceDir).get(0);

    assertFalse("Class name should not contain path separators",
        className.contains("/") || className.contains("\\"));
    assertTrue("Class name should contain dot-separated packages",
        className.contains("org.lgna.story.resources"));
  }

  @Test
  public void classNameDerivation_stripsXmlExtensionAndAppendsSuffix() throws IOException {
    File packageDir = new File(resourceDir, "models");
    assertTrue(packageDir.mkdirs());
    new File(packageDir, "Chair.xml").createNewFile();

    Map<File, List<String>> result =
        ResourceClassLoader.getClassNamesFromResources(resourceDir);
    String className = result.get(resourceDir).get(0);

    assertFalse("Class name should not contain .xml extension",
        className.contains(".xml"));
    assertTrue("Class name should end with Resource suffix",
        className.endsWith("Resource"));
  }

  @Test
  public void classNameDerivation_noLeadingDot() throws IOException {
    File packageDir = new File(resourceDir, "models");
    assertTrue(packageDir.mkdirs());
    new File(packageDir, "Tree.xml").createNewFile();

    Map<File, List<String>> result =
        ResourceClassLoader.getClassNamesFromResources(resourceDir);
    String className = result.get(resourceDir).get(0);

    assertFalse("Class name should not start with a dot",
        className.startsWith("."));
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Structural contract — ResourceClassLoader is a static utility
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void resourceClassLoader_isFinalClass() {
    assertTrue("ResourceClassLoader should be final",
        java.lang.reflect.Modifier.isFinal(ResourceClassLoader.class.getModifiers()));
  }

  @Test
  public void resourceClassLoader_hasNoPublicConstructor() {
    assertEquals("ResourceClassLoader should have no public constructors",
        0, ResourceClassLoader.class.getConstructors().length);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Test helpers
  // ═══════════════════════════════════════════════════════════════════════

  private File createTestZip(String name, String... entries) throws IOException {
    File zip = new File(tempDir.getRoot(), name);
    return writeZip(zip, entries);
  }

  private File createTestZipIn(File dir, String name, String... entries) throws IOException {
    File zip = new File(dir, name);
    return writeZip(zip, entries);
  }

  private File writeZip(File zip, String... entries) throws IOException {
    try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip))) {
      for (String entry : entries) {
        zos.putNextEntry(new ZipEntry(entry));
        zos.write(new byte[0]);
        zos.closeEntry();
      }
    }
    return zip;
  }
}
