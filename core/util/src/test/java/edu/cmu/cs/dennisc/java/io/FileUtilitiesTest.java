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

  // ====== Extension parsing ======

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
  public void getExtension_trailingDot() {
    assertEquals("", FileUtilities.getExtension("test."));
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
  public void getExtension_longExtension() {
    assertEquals("properties", FileUtilities.getExtension("config.properties"));
  }

  @Test
  public void getExtension_numericExtension() {
    assertEquals("123", FileUtilities.getExtension("file.123"));
  }

  // ====== Basename parsing ======

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

  // ====== File validation ======

  @Test
  public void isValidFile_existingFile() throws IOException {
    File f = tempFolder.newFile("valid.txt");
    assertTrue(FileUtilities.isValidFile(f));
  }

  @Test
  public void isValidFile_nonExistent() {
    File f = new File(tempFolder.getRoot(), "nonexistent.txt");
    assertTrue(FileUtilities.isValidFile(f));
  }

  @Test
  public void isValidFile_null() {
    assertFalse(FileUtilities.isValidFile(null));
  }

  @Test
  public void isValidFile_directory() throws IOException {
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
    assertTrue(FileUtilities.isValidPath("/nonexistent/path/file.txt"));
  }

  @Test
  public void isValidPath_null() {
    assertFalse(FileUtilities.isValidPath(null));
  }

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

  // ====== getCanonicalPathIfPossible ======

  @Test
  public void getCanonicalPathIfPossible_normalFile() throws IOException {
    File f = tempFolder.newFile("canonical.txt");
    String path = FileUtilities.getCanonicalPathIfPossible(f);
    assertNotNull(path);
    assertTrue(path.endsWith("canonical.txt"));
  }

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
  public void getCanonicalPathIfPossible_withDot_resolves() throws IOException {
    File f = new File(tempFolder.getRoot(), "./file.txt");
    String path = FileUtilities.getCanonicalPathIfPossible(f);
    assertNotNull(path);
    assertFalse("Should resolve .", path.contains("/./"));
  }

  @Test
  public void getCanonicalPathIfPossible_directory() throws IOException {
    File dir = tempFolder.newFolder("canondir");
    String path = FileUtilities.getCanonicalPathIfPossible(dir);
    assertNotNull(path);
    assertTrue(path.endsWith("canondir"));
  }

  // ====== createParentDirectoriesIfNecessary ======

  @Test
  public void createParentDirectoriesIfNecessary_createsParents() {
    File nested = new File(tempFolder.getRoot(), "a/b/c/file.txt");
    boolean result = FileUtilities.createParentDirectoriesIfNecessary(nested);
    assertTrue(result);
    assertTrue(nested.getParentFile().exists());
  }

  @Test
  public void createParentDirectoriesIfNecessary_alreadyExists() throws IOException {
    File dir = tempFolder.newFolder("existing");
    File f = new File(dir, "file.txt");
    boolean result = FileUtilities.createParentDirectoriesIfNecessary(f);
    assertFalse(result);
  }

  @Test
  public void createParentDirectoriesIfNecessary_deepNesting() {
    File nested = new File(tempFolder.getRoot(), "a/b/c/d/e/f/g/file.txt");
    FileUtilities.createParentDirectoriesIfNecessary(nested);
    assertTrue(nested.getParentFile().exists());
    assertTrue(nested.getParentFile().isDirectory());
  }

  @Test
  public void createParentDirectoriesIfNecessary_singleLevel() {
    File f = new File(tempFolder.getRoot(), "newdir/file.txt");
    FileUtilities.createParentDirectoriesIfNecessary(f);
    assertTrue(f.getParentFile().exists());
  }

  // ====== File listing ======

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
    new File(dir, "b.txt").createNewFile();

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
    assertNotNull(files);
    assertEquals(3, files.length);
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
    new File(root, "b.txt").createNewFile();

    File[] dirs = FileUtilities.listDirectories(root);
    assertNotNull(dirs);
    assertEquals(0, dirs.length);
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

  @Test
  public void listFiles_withFilter_matchesNone() throws IOException {
    File root = tempFolder.newFolder("nomatchfilter");
    new File(root, "a.txt").createNewFile();

    File[] files = FileUtilities.listFiles(root, f -> false);
    assertNotNull(files);
    assertEquals(0, files.length);
  }

  @Test
  public void listFiles_withFilter_matchesAll() throws IOException {
    File root = tempFolder.newFolder("allfilter");
    new File(root, "a.txt").createNewFile();
    new File(root, "b.log").createNewFile();
    File sub = new File(root, "subdir");
    sub.mkdir();

    File[] files = FileUtilities.listFiles(root, f -> true);
    assertNotNull(files);
    assertEquals(3, files.length); // 2 files + 1 dir
  }

  // ====== listDescendants ======

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
    for (File f : depth1) {
      assertTrue(f.getName().endsWith(".txt"));
    }
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
    assertNotNull(depth0);
    // depth=0 means no recursion into subdirectories
    assertEquals(1, depth0.length);
    assertEquals("top.txt", depth0[0].getName());
  }

  @Test
  public void listDescendants_depthMinusOne_fullRecursion() throws IOException {
    File root = tempFolder.newFolder("fullrecurse");
    File l1 = new File(root, "l1");
    l1.mkdir();
    File l2 = new File(l1, "l2");
    l2.mkdir();
    File l3 = new File(l2, "l3");
    l3.mkdir();
    new File(root, "a.txt").createNewFile();
    new File(l1, "b.txt").createNewFile();
    new File(l2, "c.txt").createNewFile();
    new File(l3, "d.txt").createNewFile();

    FileFilter txtFilter = f -> f.getName().endsWith(".txt");
    File[] all = FileUtilities.listDescendants(root, txtFilter, -1);
    assertNotNull(all);
    assertEquals(4, all.length);
  }

  @Test
  public void listDescendants_emptyDir() throws IOException {
    File root = tempFolder.newFolder("emptydescendant");
    FileFilter allFilter = f -> true;
    File[] result = FileUtilities.listDescendants(root, allFilter);
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  public void listDescendants_filesAndDirs() throws IOException {
    File root = tempFolder.newFolder("mixed");
    File sub = new File(root, "sub");
    sub.mkdir();
    new File(root, "file.txt").createNewFile();
    new File(sub, "nested.txt").createNewFile();

    File[] all = FileUtilities.listDescendants(root, f -> true, -1);
    assertNotNull(all);
    // root level: file.txt + sub; sub level: nested.txt
    assertTrue(all.length >= 3);
  }

  @Test
  public void listDescendants_byExtension_noMatch() throws IOException {
    File root = tempFolder.newFolder("noext");
    new File(root, "file.txt").createNewFile();

    File[] result = FileUtilities.listDescendants(root, "java");
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  // ====== Filter factories ======

  @Test
  public void createFilenameFilter() {
    FilenameFilter filter = FileUtilities.createFilenameFilter("java");
    assertNotNull(filter);
    assertTrue(filter.accept(null, "Test.java"));
    assertFalse(filter.accept(null, "Test.txt"));
  }

  @Test
  public void createFilenameFilter_caseInsensitive() {
    FilenameFilter filter = FileUtilities.createFilenameFilter("java");
    assertTrue(filter.accept(null, "Test.JAVA"));
    assertTrue(filter.accept(null, "Test.Java"));
    assertTrue(filter.accept(null, "Test.java"));
  }

  @Test
  public void createFilenameFilter_noMatch() {
    FilenameFilter filter = FileUtilities.createFilenameFilter("xml");
    assertFalse(filter.accept(null, "file.txt"));
    assertFalse(filter.accept(null, "file.java"));
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
  public void createFileWithExtensionFilter_rejectsDirectories() throws IOException {
    FileFilter filter = FileUtilities.createFileWithExtensionFilter("txt");
    File dir = tempFolder.newFolder("dir.txt");
    assertFalse("Directories should be rejected", filter.accept(dir));
  }

  @Test
  public void createFileWithExtensionFilter_caseInsensitive() throws IOException {
    FileFilter filter = FileUtilities.createFileWithExtensionFilter("txt");
    File upper = tempFolder.newFile("file.TXT");
    assertTrue(filter.accept(upper));
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

  @Test
  public void createDirectoryFilter_nonExistent() {
    FileFilter filter = FileUtilities.createDirectoryFilter();
    File nonExistent = new File(tempFolder.getRoot(), "nodir");
    assertFalse(filter.accept(nonExistent));
  }

  // ====== copyFile ======

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
    File src = tempFolder.newFile("src-overwrite.txt");
    Files.writeString(src.toPath(), "new content");
    File dst = tempFolder.newFile("dst-overwrite.txt");
    Files.writeString(dst.toPath(), "old content");

    FileUtilities.copyFile(src, dst);

    assertEquals("new content", Files.readString(dst.toPath()));
  }

  @Test
  public void copyFile_binaryContent() throws IOException {
    File src = tempFolder.newFile("binary.bin");
    byte[] data = new byte[]{0x00, 0x01, 0x7F, (byte) 0x80, (byte) 0xFE, (byte) 0xFF};
    Files.write(src.toPath(), data);
    File dst = new File(tempFolder.getRoot(), "binary-copy.bin");

    FileUtilities.copyFile(src, dst);

    assertArrayEquals(data, Files.readAllBytes(dst.toPath()));
  }

  // ====== Timestamps ======

  @Test
  public void getModifiedDateTime_existingFile() throws IOException {
    File f = tempFolder.newFile("timestamp.txt");
    LocalDateTime modified = FileUtilities.getModifiedDateTime(f);
    assertNotNull(modified);
    assertTrue(modified.isAfter(LocalDateTime.now().minusMinutes(1)));
  }

  @Test
  public void getCreatedDateTime_existingFile() throws IOException {
    File f = tempFolder.newFile("created.txt");
    LocalDateTime created = FileUtilities.getCreatedDateTime(f);
    assertNotNull(created);
    assertTrue(created.isAfter(LocalDateTime.now().minusMinutes(1)));
  }

  @Test
  public void getModifiedDateTime_null_returnsMin() {
    LocalDateTime result = FileUtilities.getModifiedDateTime(null);
    assertEquals(LocalDateTime.MIN, result);
  }

  @Test
  public void getModifiedDateTime_nonExistent_returnsMin() {
    File f = new File(tempFolder.getRoot(), "nonexistent.txt");
    LocalDateTime result = FileUtilities.getModifiedDateTime(f);
    assertEquals(LocalDateTime.MIN, result);
  }

  @Test(expected = NullPointerException.class)
  public void getCreatedDateTime_null_throwsNPE() {
    FileUtilities.getCreatedDateTime(null);
  }

  @Test
  public void getCreatedDateTime_nonExistent_returnsMin() {
    File f = new File(tempFolder.getRoot(), "nonexistent-created.txt");
    LocalDateTime result = FileUtilities.getCreatedDateTime(f);
    assertEquals(LocalDateTime.MIN, result);
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
    Thread.sleep(10); // ensure time passes
    Files.writeString(f.toPath(), "updated");
    LocalDateTime modified = FileUtilities.getModifiedDateTime(f);
    assertTrue(modified.isAfter(LocalDateTime.now().minusSeconds(5)));
  }

  @Test
  public void getModifiedDateTime_twoFiles_differentTimes() throws IOException, InterruptedException {
    File f1 = tempFolder.newFile("first.txt");
    Thread.sleep(50);
    File f2 = tempFolder.newFile("second.txt");
    LocalDateTime t1 = FileUtilities.getModifiedDateTime(f1);
    LocalDateTime t2 = FileUtilities.getModifiedDateTime(f2);
    // t2 should be >= t1 (same or later)
    assertFalse("Second file should not be before first", t2.isBefore(t1));
  }

  // ====== Additional edge cases ======

  @Test
  public void listFiles_byExtension_mixedContent() throws IOException {
    File dir = tempFolder.newFolder("mixedlist");
    new File(dir, "a.java").createNewFile();
    new File(dir, "b.java").createNewFile();
    new File(dir, "c.txt").createNewFile();
    new File(dir, "sub").mkdir();

    File[] javaFiles = FileUtilities.listFiles(dir, "java");
    assertEquals(2, javaFiles.length);
  }

  @Test
  public void listFiles_byExtension_directoryNotMatched() throws IOException {
    File dir = tempFolder.newFolder("dirext");
    File sub = new File(dir, "subdir.txt");
    sub.mkdir(); // dir ending in .txt
    new File(dir, "real.txt").createNewFile();

    File[] files = FileUtilities.listFiles(dir, "txt");
    // Only files, not directories
    assertEquals(1, files.length);
  }

  @Test
  public void isValidFile_withSymlink() throws IOException {
    File target = tempFolder.newFile("target.txt");
    File link = new File(tempFolder.getRoot(), "link.txt");
    Files.createSymbolicLink(link.toPath(), target.toPath());
    assertTrue(FileUtilities.isValidFile(link));
  }

  @Test
  public void getCanonicalPathIfPossible_symlink() throws IOException {
    File target = tempFolder.newFile("symtarget.txt");
    File link = new File(tempFolder.getRoot(), "symlink.txt");
    Files.createSymbolicLink(link.toPath(), target.toPath());
    String path = FileUtilities.getCanonicalPathIfPossible(link);
    assertNotNull(path);
    // Canonical path resolves symlinks
    assertTrue(path.contains("symtarget.txt"));
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
    File l1 = new File(root, "l1");
    l1.mkdir();
    File l2 = new File(l1, "l2");
    l2.mkdir();
    new File(root, "r.txt").createNewFile();
    new File(l1, "l1.txt").createNewFile();
    new File(l2, "l2.txt").createNewFile();

    FileFilter txtFilter = f -> f.getName().endsWith(".txt");
    File[] d1 = FileUtilities.listDescendants(root, txtFilter, 1);
    // depth=1: root files + l1 files, but not l2 files
    assertEquals(2, d1.length);
  }

  @Test
  public void getExtension_windowsPath() {
    assertEquals("exe", FileUtilities.getExtension("C:\\Program Files\\app.exe"));
  }

  @Test
  public void getBaseName_windowsPath() {
    assertEquals("app", FileUtilities.getBaseName("C:\\Program Files\\app.exe"));
  }

  // ====== Additional extension edge cases ======

  @Test
  public void getExtension_singleCharExtension() {
    assertEquals("c", FileUtilities.getExtension("main.c"));
  }

  @Test
  public void getExtension_veryLongExtension() {
    assertEquals("verylongextensionname", FileUtilities.getExtension("file.verylongextensionname"));
  }

  @Test
  public void getExtension_dotDot() {
    assertEquals("", FileUtilities.getExtension("file.."));
  }

  @Test
  public void getExtension_withPathSeparators() {
    assertEquals("txt", FileUtilities.getExtension("/path/to/file.txt"));
  }

  @Test
  public void getExtension_hiddenFileWithExtension() {
    assertEquals("log", FileUtilities.getExtension(".hidden.log"));
  }

  @Test
  public void getExtension_fromFile_noExtension() throws IOException {
    File f = new File(tempFolder.getRoot(), "Makefile");
    f.createNewFile();
    assertNull(FileUtilities.getExtension(f));
  }

  @Test
  public void getExtension_fromFile_multiDot() throws IOException {
    File f = tempFolder.newFile("archive.tar.gz");
    assertEquals("gz", FileUtilities.getExtension(f));
  }

  @Test
  public void getExtension_specialExtension() {
    assertEquals("a3p", FileUtilities.getExtension("project.a3p"));
  }

  // ====== Additional basename edge cases ======

  @Test
  public void getBaseName_singleCharName() {
    assertEquals("a", FileUtilities.getBaseName("a.txt"));
  }

  @Test
  public void getBaseName_onlyDots() {
    assertEquals(".", FileUtilities.getBaseName(".."));
  }

  @Test
  public void getBaseName_deepUnixPath() {
    assertEquals("target", FileUtilities.getBaseName("/a/b/c/d/e/target.xml"));
  }

  @Test
  public void getBaseName_deepWindowsPath() {
    assertEquals("target", FileUtilities.getBaseName("C:\\a\\b\\c\\d\\target.xml"));
  }

  @Test
  public void getBaseName_fromFile_noExtension() throws IOException {
    File f = new File(tempFolder.getRoot(), "README");
    f.createNewFile();
    assertEquals("README", FileUtilities.getBaseName(f));
  }

  @Test
  public void getBaseName_endsWithSeparator() {
    assertEquals("", FileUtilities.getBaseName("path/to/dir/"));
  }

  // ====== Additional file validation tests ======

  @Test
  public void isValidFile_tempFile() throws IOException {
    File f = tempFolder.newFile("temp.dat");
    assertTrue(FileUtilities.isValidFile(f));
  }

  @Test
  public void isValidPath_emptyStringIsValid() {
    assertTrue(FileUtilities.isValidPath(""));
  }

  @Test
  public void isValidFile_deepNonExistent() {
    File f = new File("/a/b/c/d/e/f/g/h/file.txt");
    assertTrue(FileUtilities.isValidFile(f));
  }

  @Test
  public void isValidPath_withSpaces() {
    assertTrue(FileUtilities.isValidPath("/path with spaces/file.txt"));
  }

  @Test
  public void isValidFile_currentDir() {
    assertTrue(FileUtilities.isValidFile(new File(".")));
  }

  @Test
  public void isValidFile_parentDir() {
    assertTrue(FileUtilities.isValidFile(new File("..")));
  }

  // ====== Additional getCanonicalPathIfPossible tests ======

  @Test
  public void getCanonicalPathIfPossible_relativeFile() {
    File f = new File("relative.txt");
    String path = FileUtilities.getCanonicalPathIfPossible(f);
    assertNotNull(path);
    assertTrue(path.endsWith("relative.txt"));
  }

  @Test
  public void getCanonicalPathIfPossible_absolutePath() throws IOException {
    File f = tempFolder.newFile("absolute.txt");
    String path = FileUtilities.getCanonicalPathIfPossible(f);
    assertTrue(path.startsWith("/"));
  }

  @Test
  public void getCanonicalPathIfPossible_rootDir() {
    String path = FileUtilities.getCanonicalPathIfPossible(new File("/"));
    assertNotNull(path);
    assertEquals("/", path);
  }

  @Test
  public void getCanonicalPathIfPossible_currentDir() {
    String path = FileUtilities.getCanonicalPathIfPossible(new File("."));
    assertNotNull(path);
    assertFalse(path.endsWith("."));
  }

  // ====== Additional createParentDirectories tests ======

  @Test
  public void createParentDirectoriesIfNecessary_veryDeep() {
    File f = new File(tempFolder.getRoot(), "l1/l2/l3/l4/l5/l6/l7/l8/file.txt");
    FileUtilities.createParentDirectoriesIfNecessary(f);
    assertTrue(f.getParentFile().exists());
    assertTrue(f.getParentFile().isDirectory());
  }

  @Test
  public void createParentDirectoriesIfNecessary_idempotent() {
    File f = new File(tempFolder.getRoot(), "idempotent/dir/file.txt");
    FileUtilities.createParentDirectoriesIfNecessary(f);
    FileUtilities.createParentDirectoriesIfNecessary(f);
    assertTrue(f.getParentFile().exists());
  }

  // ====== Additional listing tests ======

  @Test
  public void listFiles_withFilter_directoriesOnly() throws IOException {
    File root = tempFolder.newFolder("dirfilter");
    new File(root, "sub1").mkdir();
    new File(root, "sub2").mkdir();
    new File(root, "file.txt").createNewFile();
    File[] dirs = FileUtilities.listFiles(root, File::isDirectory);
    assertEquals(2, dirs.length);
  }

  @Test
  public void listFiles_withFilter_filesOnly() throws IOException {
    File root = tempFolder.newFolder("filefilter");
    new File(root, "sub1").mkdir();
    new File(root, "a.txt").createNewFile();
    new File(root, "b.txt").createNewFile();
    File[] files = FileUtilities.listFiles(root, File::isFile);
    assertEquals(2, files.length);
  }

  @Test
  public void listDirectories_withMixedContent() throws IOException {
    File root = tempFolder.newFolder("mixeddirlist");
    new File(root, "dir1").mkdir();
    new File(root, "dir2").mkdir();
    new File(root, "dir3").mkdir();
    new File(root, "file1.txt").createNewFile();
    new File(root, "file2.log").createNewFile();
    File[] dirs = FileUtilities.listDirectories(root);
    assertEquals(3, dirs.length);
  }

  @Test
  public void listFiles_byExtension_manyExtensions() throws IOException {
    File root = tempFolder.newFolder("multiext");
    new File(root, "a.java").createNewFile();
    new File(root, "b.py").createNewFile();
    new File(root, "c.java").createNewFile();
    new File(root, "d.rb").createNewFile();
    new File(root, "e.java").createNewFile();
    assertEquals(3, FileUtilities.listFiles(root, "java").length);
    assertEquals(1, FileUtilities.listFiles(root, "py").length);
    assertEquals(1, FileUtilities.listFiles(root, "rb").length);
  }

  @Test
  public void listDescendants_depth2() throws IOException {
    File root = tempFolder.newFolder("depth2test");
    File l1 = new File(root, "l1");
    l1.mkdir();
    File l2 = new File(l1, "l2");
    l2.mkdir();
    File l3 = new File(l2, "l3");
    l3.mkdir();
    new File(root, "r.txt").createNewFile();
    new File(l1, "l1.txt").createNewFile();
    new File(l2, "l2.txt").createNewFile();
    new File(l3, "l3.txt").createNewFile();

    FileFilter txtFilter = f -> f.getName().endsWith(".txt");
    File[] d2 = FileUtilities.listDescendants(root, txtFilter, 2);
    // depth=2: root + l1 + l2 files (3), but not l3
    assertEquals(3, d2.length);
  }

  @Test
  public void listDescendants_byExtension_caseMixed() throws IOException {
    File root = tempFolder.newFolder("casemixdesc");
    new File(root, "a.TXT").createNewFile();
    new File(root, "b.txt").createNewFile();
    new File(root, "c.Txt").createNewFile();
    File[] result = FileUtilities.listDescendants(root, "txt");
    assertEquals(3, result.length);
  }

  @Test
  public void listDescendants_emptySubdirectories() throws IOException {
    File root = tempFolder.newFolder("emptysubs");
    new File(root, "sub1").mkdir();
    new File(root, "sub2").mkdir();
    new File(root, "root.txt").createNewFile();
    File[] result = FileUtilities.listDescendants(root, "txt");
    assertEquals(1, result.length);
  }

  // ====== Additional filter factory tests ======

  @Test
  public void createFilenameFilter_emptyExtension() {
    FilenameFilter filter = FileUtilities.createFilenameFilter("");
    assertTrue(filter.accept(null, "anyfile.txt"));
  }

  @Test
  public void createFilenameFilter_extensionWithDot() {
    FilenameFilter filter = FileUtilities.createFilenameFilter(".java");
    assertTrue(filter.accept(null, "Test.java"));
  }

  @Test
  public void createFileWithExtensionFilter_emptyExtension() throws IOException {
    FileFilter filter = FileUtilities.createFileWithExtensionFilter("");
    File f = tempFolder.newFile("file.txt");
    assertTrue(filter.accept(f));
  }

  @Test
  public void createDirectoryFilter_nestedDir() throws IOException {
    FileFilter filter = FileUtilities.createDirectoryFilter();
    File outer = tempFolder.newFolder("outer");
    File inner = new File(outer, "inner");
    inner.mkdir();
    assertTrue(filter.accept(inner));
  }

  // ====== Additional copy tests ======

  @Test
  public void copyFile_unicodeContent() throws IOException {
    File src = tempFolder.newFile("unicode-src.txt");
    String content = "caf\u00e9 \u2603 \u4e16\u754c";
    Files.writeString(src.toPath(), content);
    File dst = new File(tempFolder.getRoot(), "unicode-dst.txt");
    FileUtilities.copyFile(src, dst);
    assertEquals(content, Files.readString(dst.toPath()));
  }

  @Test
  public void copyFile_deepNestedDestination() throws IOException {
    File src = tempFolder.newFile("deep-src.txt");
    Files.writeString(src.toPath(), "deep copy test");
    File dst = new File(tempFolder.getRoot(), "a/b/c/d/e/deep-dst.txt");
    FileUtilities.copyFile(src, dst);
    assertTrue(dst.exists());
    assertEquals("deep copy test", Files.readString(dst.toPath()));
  }

  @Test
  public void copyFile_preservesExactSize() throws IOException {
    File src = tempFolder.newFile("sized.bin");
    byte[] data = new byte[12345];
    for (int i = 0; i < data.length; i++) data[i] = (byte) (i % 256);
    Files.write(src.toPath(), data);
    File dst = new File(tempFolder.getRoot(), "sized-copy.bin");
    FileUtilities.copyFile(src, dst);
    assertEquals(src.length(), dst.length());
    assertArrayEquals(data, Files.readAllBytes(dst.toPath()));
  }

  // ====== Additional timestamp tests ======

  @Test
  public void getModifiedDateTime_notMin_forExistingFile() throws IOException {
    File f = tempFolder.newFile("notmin.txt");
    LocalDateTime modified = FileUtilities.getModifiedDateTime(f);
    assertNotEquals(LocalDateTime.MIN, modified);
  }

  @Test
  public void getCreatedDateTime_notMin_forExistingFile() throws IOException {
    File f = tempFolder.newFile("notmin2.txt");
    LocalDateTime created = FileUtilities.getCreatedDateTime(f);
    assertNotEquals(LocalDateTime.MIN, created);
  }

  @Test
  public void getModifiedDateTime_existingDir_notMin() throws IOException {
    File dir = tempFolder.newFolder("tsdircheck");
    assertNotEquals(LocalDateTime.MIN, FileUtilities.getModifiedDateTime(dir));
  }

  @Test
  public void getCreatedDateTime_existingDir_notMin() throws IOException {
    File dir = tempFolder.newFolder("tsdircheck2");
    assertNotEquals(LocalDateTime.MIN, FileUtilities.getCreatedDateTime(dir));
  }

  @Test
  public void getModifiedDateTime_multipleFiles_allValid() throws IOException {
    for (int i = 0; i < 5; i++) {
      File f = tempFolder.newFile("multi" + i + ".txt");
      LocalDateTime modified = FileUtilities.getModifiedDateTime(f);
      assertNotNull(modified);
      assertNotEquals(LocalDateTime.MIN, modified);
    }
  }

  @Test
  public void getModifiedDateTime_afterContent_isRecent() throws IOException {
    File f = tempFolder.newFile("afterwrite.txt");
    Files.writeString(f.toPath(), "some content to make modified time recent");
    LocalDateTime modified = FileUtilities.getModifiedDateTime(f);
    assertTrue(modified.isAfter(LocalDateTime.now().minusSeconds(30)));
  }

  // ====== Misc edge cases ======

  @Test
  public void listFiles_nonExistentDir_returnsEmpty() {
    File dir = new File(tempFolder.getRoot(), "nonexistent_dir");
    File[] result = FileUtilities.listFiles(dir, f -> true);
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  public void listDirectories_singleDir() throws IOException {
    File root = tempFolder.newFolder("singledir");
    new File(root, "only").mkdir();
    File[] dirs = FileUtilities.listDirectories(root);
    assertEquals(1, dirs.length);
    assertEquals("only", dirs[0].getName());
  }

  @Test
  public void getExtension_fromFile_directory() throws IOException {
    File dir = tempFolder.newFolder("dir.ext");
    assertEquals("ext", FileUtilities.getExtension(dir));
  }

  @Test
  public void getBaseName_fromFile_directory() throws IOException {
    File dir = tempFolder.newFolder("dirname.ext");
    assertEquals("dirname", FileUtilities.getBaseName(dir));
  }

  @Test
  public void copyFile_sameSizeVerification() throws IOException {
    File src = tempFolder.newFile("verify-src.bin");
    byte[] data = new byte[4096];
    for (int i = 0; i < data.length; i++) data[i] = (byte) (i % 128);
    Files.write(src.toPath(), data);
    File dst = new File(tempFolder.getRoot(), "verify-dst.bin");
    FileUtilities.copyFile(src, dst);
    assertEquals(4096, dst.length());
  }

  @Test
  public void listFiles_byExtension_noFilesOnlyDirs() throws IOException {
    File root = tempFolder.newFolder("onlydirs");
    new File(root, "a").mkdir();
    new File(root, "b").mkdir();
    File[] files = FileUtilities.listFiles(root, "txt");
    assertEquals(0, files.length);
  }
}
