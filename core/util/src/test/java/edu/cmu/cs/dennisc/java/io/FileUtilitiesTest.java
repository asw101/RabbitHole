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
    // .gitignore → implementation returns "gitignore" as extension
    String ext = FileUtilities.getExtension(".gitignore");
    assertNotNull(ext);
    assertEquals("gitignore", ext);
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

  // ====== Additional extension tests ======

  @Test
  public void getExtension_nullString_returnsNull() {
    assertNull(FileUtilities.getExtension((String) null));
  }

  @Test
  public void getExtension_nullFile_returnsNull() {
    assertNull(FileUtilities.getExtension((File) null));
  }

  @Test
  public void getExtension_dotOnly() {
    assertEquals("", FileUtilities.getExtension("file."));
  }

  @Test
  public void getExtension_multipleDots() {
    assertEquals("bak", FileUtilities.getExtension("my.file.name.bak"));
  }

  @Test
  public void getExtension_spacesInName() {
    assertEquals("txt", FileUtilities.getExtension("my file.txt"));
  }

  @Test
  public void getExtension_uppercase() {
    assertEquals("TXT", FileUtilities.getExtension("FILE.TXT"));
  }

  @Test
  public void getExtension_mixedCase() {
    assertEquals("Java", FileUtilities.getExtension("MyClass.Java"));
  }

  @Test
  public void getExtension_numericExtension() {
    assertEquals("123", FileUtilities.getExtension("file.123"));
  }

  @Test
  public void getExtension_singleCharExtension() {
    assertEquals("c", FileUtilities.getExtension("main.c"));
  }

  @Test
  public void getExtension_hiddenFileWithExtension() {
    assertEquals("log", FileUtilities.getExtension(".hidden.log"));
  }

  @Test
  public void getExtension_windowsPath() {
    assertEquals("exe", FileUtilities.getExtension("C:\\Program Files\\app.exe"));
  }

  @Test
  public void getExtension_fromFile_multiDot() throws IOException {
    File f = tempFolder.newFile("archive.tar.gz");
    assertEquals("gz", FileUtilities.getExtension(f));
  }

  // ====== Additional basename tests ======

  @Test
  public void getBaseName_nullString_returnsNull() {
    assertNull(FileUtilities.getBaseName((String) null));
  }

  @Test
  public void getBaseName_nullFile_returnsNull() {
    assertNull(FileUtilities.getBaseName((File) null));
  }

  @Test
  public void getBaseName_dotFile() {
    assertEquals("", FileUtilities.getBaseName(".gitignore"));
  }

  @Test
  public void getBaseName_emptyString() {
    assertEquals("", FileUtilities.getBaseName(""));
  }

  @Test
  public void getBaseName_pathWithSlash() {
    assertEquals("file", FileUtilities.getBaseName("path/to/file.txt"));
  }

  @Test
  public void getBaseName_pathWithBackslash() {
    assertEquals("file", FileUtilities.getBaseName("path\\to\\file.txt"));
  }

  @Test
  public void getBaseName_pathWithMixedSeparators() {
    assertEquals("file", FileUtilities.getBaseName("path/to\\file.txt"));
  }

  @Test
  public void getBaseName_trailingDot() {
    assertEquals("file", FileUtilities.getBaseName("file."));
  }

  @Test
  public void getBaseName_multipleDots() {
    assertEquals("my.file.name", FileUtilities.getBaseName("my.file.name.bak"));
  }

  @Test
  public void getBaseName_noExtensionWithPath() {
    assertEquals("Makefile", FileUtilities.getBaseName("/usr/src/Makefile"));
  }

  @Test
  public void getBaseName_windowsPath() {
    assertEquals("app", FileUtilities.getBaseName("C:\\Program Files\\app.exe"));
  }

  // ====== Additional validation tests ======

  @Test
  public void isValidPath_emptyString() {
    assertTrue(FileUtilities.isValidPath(""));
  }

  @Test
  public void isValidFile_rootDir() {
    assertTrue(FileUtilities.isValidFile(new File("/")));
  }

  @Test
  public void isValidFile_relativeFile() {
    assertTrue(FileUtilities.isValidFile(new File("relative.txt")));
  }

  // ====== Additional canonical path tests ======

  @Test
  public void getCanonicalPathIfPossible_null_returnsNull() {
    assertNull(FileUtilities.getCanonicalPathIfPossible(null));
  }

  @Test
  public void getCanonicalPathIfPossible_nonExistent_returnsPath() {
    File f = new File(tempFolder.getRoot(), "nonexistent.txt");
    String path = FileUtilities.getCanonicalPathIfPossible(f);
    assertNotNull(path);
    assertTrue(path.contains("nonexistent.txt"));
  }

  @Test
  public void getCanonicalPathIfPossible_withDotDot_resolves() throws IOException {
    File dir = tempFolder.newFolder("parent");
    File f = new File(dir, "../parent/file.txt");
    String path = FileUtilities.getCanonicalPathIfPossible(f);
    assertNotNull(path);
    assertFalse("Should resolve ..", path.contains(".."));
  }

  @Test
  public void getCanonicalPathIfPossible_directory() throws IOException {
    File dir = tempFolder.newFolder("canondir");
    String path = FileUtilities.getCanonicalPathIfPossible(dir);
    assertNotNull(path);
    assertTrue(path.endsWith("canondir"));
  }

  // ====== Additional parent directory tests ======

  @Test
  public void createParentDirectoriesIfNecessary_deepNesting() {
    File nested = new File(tempFolder.getRoot(), "a/b/c/d/e/f/g/file.txt");
    FileUtilities.createParentDirectoriesIfNecessary(nested);
    assertTrue(nested.getParentFile().exists());
    assertTrue(nested.getParentFile().isDirectory());
  }

  // ====== Additional listing tests ======

  @Test
  public void listFiles_emptyDirectory() throws IOException {
    File dir = tempFolder.newFolder("emptydir");
    File[] files = FileUtilities.listFiles(dir, f -> true);
    assertNotNull(files);
    assertEquals(0, files.length);
  }

  @Test
  public void listFiles_noMatchingExtension() throws IOException {
    File dir = tempFolder.newFolder("nomatch");
    new File(dir, "a.txt").createNewFile();
    File[] files = FileUtilities.listFiles(dir, "java");
    assertNotNull(files);
    assertEquals(0, files.length);
  }

  @Test
  public void listFiles_extensionCaseInsensitive() throws IOException {
    File dir = tempFolder.newFolder("caseext");
    new File(dir, "upper.TXT").createNewFile();
    new File(dir, "lower.txt").createNewFile();
    new File(dir, "mixed.Txt").createNewFile();
    File[] files = FileUtilities.listFiles(dir, "txt");
    assertEquals(3, files.length);
  }

  @Test
  public void listDirectories_emptyDir() throws IOException {
    File root = tempFolder.newFolder("emptyroot");
    File[] dirs = FileUtilities.listDirectories(root);
    assertNotNull(dirs);
    assertEquals(0, dirs.length);
  }

  @Test
  public void listDirectories_onlyFiles_noDirectories() throws IOException {
    File root = tempFolder.newFolder("filesonly");
    new File(root, "a.txt").createNewFile();
    File[] dirs = FileUtilities.listDirectories(root);
    assertEquals(0, dirs.length);
  }

  @Test
  public void listDescendants_depthZero_onlyDirectChildren() throws IOException {
    File root = tempFolder.newFolder("depth0");
    File sub = new File(root, "child");
    sub.mkdir();
    new File(root, "top.txt").createNewFile();
    new File(sub, "deep.txt").createNewFile();
    FileFilter txtFilter = f -> f.getName().endsWith(".txt");
    File[] depth0 = FileUtilities.listDescendants(root, txtFilter, 0);
    assertEquals(1, depth0.length);
    assertEquals("top.txt", depth0[0].getName());
  }

  @Test
  public void listDescendants_depthMinusOne_fullRecursion() throws IOException {
    File root = tempFolder.newFolder("fullrecurse");
    File l1 = new File(root, "l1"); l1.mkdir();
    File l2 = new File(l1, "l2"); l2.mkdir();
    File l3 = new File(l2, "l3"); l3.mkdir();
    new File(root, "a.txt").createNewFile();
    new File(l1, "b.txt").createNewFile();
    new File(l2, "c.txt").createNewFile();
    new File(l3, "d.txt").createNewFile();
    FileFilter txtFilter = f -> f.getName().endsWith(".txt");
    File[] all = FileUtilities.listDescendants(root, txtFilter, -1);
    assertEquals(4, all.length);
  }

  @Test
  public void listDescendants_emptyDir() throws IOException {
    File root = tempFolder.newFolder("emptydescendant");
    File[] result = FileUtilities.listDescendants(root, f -> true);
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  public void listDescendants_byExtension_noMatch() throws IOException {
    File root = tempFolder.newFolder("noext");
    new File(root, "file.txt").createNewFile();
    File[] result = FileUtilities.listDescendants(root, "java");
    assertEquals(0, result.length);
  }

  @Test
  public void listDescendants_manyFiles() throws IOException {
    File root = tempFolder.newFolder("manyfiles");
    for (int i = 0; i < 20; i++) {
      new File(root, "file" + i + ".txt").createNewFile();
    }
    File[] result = FileUtilities.listDescendants(root, "txt");
    assertEquals(20, result.length);
  }

  @Test
  public void listDescendants_depth1_findsFirstLevel() throws IOException {
    File root = tempFolder.newFolder("depth1test");
    File l1 = new File(root, "l1"); l1.mkdir();
    File l2 = new File(l1, "l2"); l2.mkdir();
    new File(root, "r.txt").createNewFile();
    new File(l1, "l1.txt").createNewFile();
    new File(l2, "l2.txt").createNewFile();
    FileFilter txtFilter = f -> f.getName().endsWith(".txt");
    File[] d1 = FileUtilities.listDescendants(root, txtFilter, 1);
    assertEquals(2, d1.length);
  }

  // ====== Additional filter factory tests ======

  @Test
  public void createFilenameFilter_caseInsensitive() {
    FilenameFilter filter = FileUtilities.createFilenameFilter("java");
    assertTrue(filter.accept(null, "Test.JAVA"));
    assertTrue(filter.accept(null, "Test.Java"));
  }

  @Test
  public void createFileWithExtensionFilter_rejectsDirectories() throws IOException {
    FileFilter filter = FileUtilities.createFileWithExtensionFilter("txt");
    File dir = tempFolder.newFolder("dir.txt");
    assertFalse(filter.accept(dir));
  }

  @Test
  public void createFileWithExtensionFilter_caseInsensitive() throws IOException {
    FileFilter filter = FileUtilities.createFileWithExtensionFilter("txt");
    File upper = tempFolder.newFile("file.TXT");
    assertTrue(filter.accept(upper));
  }

  @Test
  public void createDirectoryFilter_nonExistent() {
    FileFilter filter = FileUtilities.createDirectoryFilter();
    assertFalse(filter.accept(new File(tempFolder.getRoot(), "nodir")));
  }

  // ====== Additional copy tests ======

  @Test
  public void copyFile_createsParentDirectories() throws IOException {
    File src = tempFolder.newFile("copysrc.txt");
    Files.writeString(src.toPath(), "parent-test");
    File dst = new File(tempFolder.getRoot(), "nested/dir/dest.txt");
    FileUtilities.copyFile(src, dst);
    assertTrue(dst.exists());
    assertEquals("parent-test", Files.readString(dst.toPath()));
  }

  @Test
  public void copyFile_emptyFile() throws IOException {
    File src = tempFolder.newFile("empty.bin");
    File dst = new File(tempFolder.getRoot(), "empty-copy.bin");
    FileUtilities.copyFile(src, dst);
    assertTrue(dst.exists());
    assertEquals(0, dst.length());
  }

  @Test
  public void copyFile_singleByte() throws IOException {
    File src = tempFolder.newFile("single.bin");
    Files.write(src.toPath(), new byte[]{42});
    File dst = new File(tempFolder.getRoot(), "single-copy.bin");
    FileUtilities.copyFile(src, dst);
    byte[] result = Files.readAllBytes(dst.toPath());
    assertEquals(1, result.length);
    assertEquals(42, result[0]);
  }

  @Test
  public void copyFile_overwritesExisting() throws IOException {
    File src = tempFolder.newFile("src-over.txt");
    Files.writeString(src.toPath(), "new content");
    File dst = tempFolder.newFile("dst-over.txt");
    Files.writeString(dst.toPath(), "old content");
    FileUtilities.copyFile(src, dst);
    assertEquals("new content", Files.readString(dst.toPath()));
  }

  @Test
  public void copyFile_binaryContent() throws IOException {
    File src = tempFolder.newFile("binary.bin");
    byte[] data = {0x00, 0x01, 0x7F, (byte) 0x80, (byte) 0xFE, (byte) 0xFF};
    Files.write(src.toPath(), data);
    File dst = new File(tempFolder.getRoot(), "binary-copy.bin");
    FileUtilities.copyFile(src, dst);
    assertArrayEquals(data, Files.readAllBytes(dst.toPath()));
  }

  // ====== Additional timestamp tests ======

  @Test
  public void getModifiedDateTime_null_returnsMin() {
    assertEquals(LocalDateTime.MIN, FileUtilities.getModifiedDateTime(null));
  }

  @Test
  public void getModifiedDateTime_nonExistent_returnsMin() {
    File f = new File(tempFolder.getRoot(), "nonexistent.txt");
    assertEquals(LocalDateTime.MIN, FileUtilities.getModifiedDateTime(f));
  }

  @Test(expected = NullPointerException.class)
  public void getCreatedDateTime_null_throwsNPE() {
    FileUtilities.getCreatedDateTime(null);
  }

  @Test
  public void getCreatedDateTime_nonExistent_returnsMin() {
    File f = new File(tempFolder.getRoot(), "ne-created.txt");
    assertEquals(LocalDateTime.MIN, FileUtilities.getCreatedDateTime(f));
  }

  @Test
  public void getModifiedDateTime_directory() throws IOException {
    File dir = tempFolder.newFolder("tsdir");
    LocalDateTime modified = FileUtilities.getModifiedDateTime(dir);
    assertNotNull(modified);
    assertTrue(modified.isAfter(LocalDateTime.now().minusMinutes(1)));
  }

  @Test
  public void getCreatedDateTime_directory() throws IOException {
    File dir = tempFolder.newFolder("createdir");
    LocalDateTime created = FileUtilities.getCreatedDateTime(dir);
    assertNotNull(created);
    assertTrue(created.isAfter(LocalDateTime.now().minusMinutes(1)));
  }

  @Test
  public void getModifiedDateTime_afterWrite_isRecent() throws IOException, InterruptedException {
    File f = tempFolder.newFile("rewrite.txt");
    Files.writeString(f.toPath(), "initial");
    Thread.sleep(10);
    Files.writeString(f.toPath(), "updated");
    assertTrue(FileUtilities.getModifiedDateTime(f).isAfter(LocalDateTime.now().minusSeconds(5)));
  }

  @Test
  public void getModifiedDateTime_twoFiles_differentTimes() throws IOException, InterruptedException {
    File f1 = tempFolder.newFile("first.txt");
    Thread.sleep(50);
    File f2 = tempFolder.newFile("second.txt");
    assertFalse(FileUtilities.getModifiedDateTime(f2).isBefore(FileUtilities.getModifiedDateTime(f1)));
  }

  @Test
  public void listFiles_byExtension_directoryNotMatched() throws IOException {
    File dir = tempFolder.newFolder("dirext");
    File sub = new File(dir, "subdir.txt"); sub.mkdir();
    new File(dir, "real.txt").createNewFile();
    assertEquals(1, FileUtilities.listFiles(dir, "txt").length);
  }

  @Test
  public void isValidFile_withSymlink() throws IOException {
    File target = tempFolder.newFile("target.txt");
    File link = new File(tempFolder.getRoot(), "link.txt");
    Files.createSymbolicLink(link.toPath(), target.toPath());
    assertTrue(FileUtilities.isValidFile(link));
  }
}
