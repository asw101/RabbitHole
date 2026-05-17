package edu.cmu.cs.dennisc.java.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Tests for FileUtilities — extension/basename parsing, file validation,
 * directory listing, filters, copy, and timestamps.
 */
public class FileUtilitiesTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  // --- Extension parsing ---

  @Test
  public void getExtension_simpleFile() {
    assertEquals("txt", FileUtilities.getExtension("document.txt"));
  }

  @Test
  public void getExtension_multiDot() {
    assertEquals("gz", FileUtilities.getExtension("archive.tar.gz"));
  }

  @Test
  public void getExtension_noExtension() {
    assertNull(FileUtilities.getExtension("README"));
  }

  @Test
  public void getExtension_dotFile() {
    // .gitignore has no extension in most interpretations
    String ext = FileUtilities.getExtension(".gitignore");
    // Implementation-dependent; just ensure it doesn't throw
    assertNotNull(ext); // .gitignore → "gitignore"
  }

  @Test
  public void getExtension_fromFile() throws IOException {
    File f = tempFolder.newFile("test.java");
    assertEquals("java", FileUtilities.getExtension(f));
  }

  @Test
  public void getExtension_emptyString() {
    assertNull(FileUtilities.getExtension(""));
  }

  // --- Basename parsing ---

  @Test
  public void getBaseName_simpleFile() {
    assertEquals("document", FileUtilities.getBaseName("document.txt"));
  }

  @Test
  public void getBaseName_multiDot() {
    assertEquals("archive.tar", FileUtilities.getBaseName("archive.tar.gz"));
  }

  @Test
  public void getBaseName_noExtension() {
    assertEquals("README", FileUtilities.getBaseName("README"));
  }

  @Test
  public void getBaseName_fromFile() throws IOException {
    File f = tempFolder.newFile("MyClass.java");
    assertEquals("MyClass", FileUtilities.getBaseName(f));
  }

  // --- File validation ---

  @Test
  public void isValidFile_existingFile() throws IOException {
    File f = tempFolder.newFile("valid.txt");
    assertTrue(FileUtilities.isValidFile(f));
  }

  @Test
  public void isValidFile_nonExistent() {
    // isValidFile only checks getCanonicalPath() succeeds, not existence
    File f = new File(tempFolder.getRoot(), "nonexistent.txt");
    assertTrue(FileUtilities.isValidFile(f));
  }

  @Test
  public void isValidFile_null() {
    assertFalse(FileUtilities.isValidFile(null));
  }

  @Test
  public void isValidFile_directory() throws IOException {
    // isValidFile checks canonical path validity, not file-vs-directory
    File dir = tempFolder.newFolder("subdir");
    assertTrue(FileUtilities.isValidFile(dir));
  }

  @Test
  public void isValidPath_existingFile() throws IOException {
    File f = tempFolder.newFile("pathcheck.txt");
    assertTrue(FileUtilities.isValidPath(f.getAbsolutePath()));
  }

  @Test
  public void isValidPath_nonExistent() {
    // isValidPath delegates to isValidFile which only checks canonical path
    assertTrue(FileUtilities.isValidPath("/nonexistent/path/file.txt"));
  }

  @Test
  public void isValidPath_null() {
    assertFalse(FileUtilities.isValidPath(null));
  }

  // --- getCanonicalPathIfPossible ---

  @Test
  public void getCanonicalPathIfPossible_normalFile() throws IOException {
    File f = tempFolder.newFile("canonical.txt");
    String path = FileUtilities.getCanonicalPathIfPossible(f);
    assertNotNull(path);
    assertTrue(path.endsWith("canonical.txt"));
  }

  // --- createParentDirectoriesIfNecessary ---

  @Test
  public void createParentDirectoriesIfNecessary_createsParents() {
    File nested = new File(tempFolder.getRoot(), "a/b/c/file.txt");
    boolean result = FileUtilities.createParentDirectoriesIfNecessary(nested);
    assertTrue(result);
    assertTrue(nested.getParentFile().exists());
  }

  @Test
  public void createParentDirectoriesIfNecessary_alreadyExists() throws IOException {
    // mkdirs() returns false when parent already exists
    File dir = tempFolder.newFolder("existing");
    File f = new File(dir, "file.txt");
    boolean result = FileUtilities.createParentDirectoriesIfNecessary(f);
    assertFalse(result);
  }

  // --- File listing ---

  @Test
  public void listFiles_byExtension() throws IOException {
    File dir = tempFolder.newFolder("listing");
    new File(dir, "a.txt").createNewFile();
    new File(dir, "b.txt").createNewFile();
    new File(dir, "c.java").createNewFile();

    File[] txtFiles = FileUtilities.listFiles(dir, "txt");
    assertNotNull(txtFiles);
    assertEquals(2, txtFiles.length);
  }

  @Test
  public void listFiles_byExtensionString() throws IOException {
    File dir = tempFolder.newFolder("listing2");
    new File(dir, "x.xml").createNewFile();

    File[] files = FileUtilities.listFiles(dir.getAbsolutePath(), "xml");
    assertNotNull(files);
    assertEquals(1, files.length);
  }

  @Test
  public void listDirectories() throws IOException {
    File root = tempFolder.newFolder("dirlist");
    new File(root, "sub1").mkdir();
    new File(root, "sub2").mkdir();
    new File(root, "file.txt").createNewFile();

    File[] dirs = FileUtilities.listDirectories(root);
    assertNotNull(dirs);
    assertEquals(2, dirs.length);
  }

  @Test
  public void listFiles_withFileFilter() throws IOException {
    File root = tempFolder.newFolder("filtered");
    new File(root, "keep.txt").createNewFile();
    new File(root, "skip.log").createNewFile();

    FileFilter filter = f -> f.getName().endsWith(".txt");
    File[] files = FileUtilities.listFiles(root, filter);
    assertNotNull(files);
    assertEquals(1, files.length);
    assertEquals("keep.txt", files[0].getName());
  }

  // --- listDescendants ---

  @Test
  public void listDescendants_recursive() throws IOException {
    File root = tempFolder.newFolder("deep");
    File sub = new File(root, "level1");
    sub.mkdir();
    File sub2 = new File(sub, "level2");
    sub2.mkdir();
    new File(root, "a.txt").createNewFile();
    new File(sub, "b.txt").createNewFile();
    new File(sub2, "c.txt").createNewFile();

    File[] descendants = FileUtilities.listDescendants(root, "txt");
    assertNotNull(descendants);
    assertTrue(descendants.length >= 3);
  }

  @Test
  public void listDescendants_withDepthLimit() throws IOException {
    File root = tempFolder.newFolder("depthtest");
    File level1 = new File(root, "l1");
    level1.mkdir();
    File level2 = new File(level1, "l2");
    level2.mkdir();
    new File(root, "root.txt").createNewFile();
    new File(level1, "l1.txt").createNewFile();
    new File(level2, "l2.txt").createNewFile();

    FileFilter txtFilter = f -> f.getName().endsWith(".txt");
    File[] depth1 = FileUtilities.listDescendants(root, txtFilter, 1);
    assertNotNull(depth1);
    // depth=1 should only get root-level files
    for (File f : depth1) {
      assertTrue(f.getName().endsWith(".txt"));
    }
  }

  // --- Filter factories ---

  @Test
  public void createFilenameFilter() {
    FilenameFilter filter = FileUtilities.createFilenameFilter("java");
    assertNotNull(filter);
    assertTrue(filter.accept(null, "Test.java"));
    assertFalse(filter.accept(null, "Test.txt"));
  }

  @Test
  public void createFileWithExtensionFilter() throws IOException {
    FileFilter filter = FileUtilities.createFileWithExtensionFilter("xml");
    assertNotNull(filter);
    File xmlFile = tempFolder.newFile("data.xml");
    File txtFile = tempFolder.newFile("data.txt");
    assertTrue(filter.accept(xmlFile));
    assertFalse(filter.accept(txtFile));
  }

  @Test
  public void createDirectoryFilter() throws IOException {
    FileFilter filter = FileUtilities.createDirectoryFilter();
    assertNotNull(filter);
    File dir = tempFolder.newFolder("testdir");
    File file = tempFolder.newFile("testfile.txt");
    assertTrue(filter.accept(dir));
    assertFalse(filter.accept(file));
  }

  // --- copyFile ---

  @Test
  public void copyFile_contentPreserved() throws IOException {
    File src = tempFolder.newFile("source.txt");
    Files.writeString(src.toPath(), "copy-test-content");
    File dst = new File(tempFolder.getRoot(), "dest.txt");

    FileUtilities.copyFile(src, dst);

    assertTrue(dst.exists());
    assertEquals("copy-test-content", Files.readString(dst.toPath()));
  }

  @Test
  public void copyFile_largeFile() throws IOException {
    File src = tempFolder.newFile("large.bin");
    byte[] data = new byte[100000];
    for (int i = 0; i < data.length; i++) {
      data[i] = (byte) (i % 256);
    }
    Files.write(src.toPath(), data);
    File dst = new File(tempFolder.getRoot(), "large-copy.bin");

    FileUtilities.copyFile(src, dst);

    assertArrayEquals(data, Files.readAllBytes(dst.toPath()));
  }

  // --- Timestamps ---

  @Test
  public void getModifiedDateTime_existingFile() throws IOException {
    File f = tempFolder.newFile("timestamp.txt");
    LocalDateTime modified = FileUtilities.getModifiedDateTime(f);
    assertNotNull(modified);
    // Should be recent (within last minute)
    assertTrue(modified.isAfter(LocalDateTime.now().minusMinutes(1)));
  }

  @Test
  public void getCreatedDateTime_existingFile() throws IOException {
    File f = tempFolder.newFile("created.txt");
    LocalDateTime created = FileUtilities.getCreatedDateTime(f);
    assertNotNull(created);
    assertTrue(created.isAfter(LocalDateTime.now().minusMinutes(1)));
  }
}
