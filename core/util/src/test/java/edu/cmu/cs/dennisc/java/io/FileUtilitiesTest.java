package edu.cmu.cs.dennisc.java.io;

import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class FileUtilitiesTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private static class BrokenCanonicalFile extends File {
    BrokenCanonicalFile(String path) {
      super(path);
    }

    @Override
    public String getCanonicalPath() throws IOException {
      throw new IOException("broken canonical path");
    }
  }

  private List<String> names(File[] files) {
    List<String> result = new ArrayList<String>();
    for (File file : files) {
      result.add(file.getName());
    }
    Collections.sort(result);
    return result;
  }

  private byte[] createBytes(int size) {
    byte[] data = new byte[size];
    for (int i = 0; i < size; i++) {
      data[i] = (byte) ((i * 17) % 253);
    }
    return data;
  }

  private void writeText(File file, String text) throws IOException {
    if (file.getParentFile() != null) {
      file.getParentFile().mkdirs();
    }
    Files.write(file.toPath(), text.getBytes(StandardCharsets.UTF_8));
  }

  private void writeBytes(File file, byte[] bytes) throws IOException {
    if (file.getParentFile() != null) {
      file.getParentFile().mkdirs();
    }
    Files.write(file.toPath(), bytes);
  }

  @Test
  public void getExtensionFromSimpleFilename() throws Exception {
    String extension = FileUtilities.getExtension("notes.txt");
    assertEquals("txt", extension);
    assertEquals(3, extension.length());
  }

  @Test
  public void getExtensionFromMultiDotFilename() throws Exception {
    String extension = FileUtilities.getExtension("archive.tar.gz");
    assertEquals("gz", extension);
    assertNotEquals("tar", extension);
  }

  @Test
  public void getExtensionFromFilenameWithoutExtension() throws Exception {
    String extension = FileUtilities.getExtension("README");
    assertNull(extension);
  }

  @Test
  public void getExtensionFromDotFile() throws Exception {
    String extension = FileUtilities.getExtension(".gitignore");
    assertEquals("gitignore", extension);
    assertTrue(extension.startsWith("git"));
  }

  @Test
  public void getExtensionFromNullFilename() throws Exception {
    String extension = FileUtilities.getExtension((String) null);
    assertNull(extension);
  }

  @Test
  public void getExtensionFromEmptyFilename() throws Exception {
    String extension = FileUtilities.getExtension("");
    assertNull(extension);
  }

  @Test
  public void getExtensionFromFileInstance() throws Exception {
    File file = temporaryFolder.newFile("script.java");
    String extension = FileUtilities.getExtension(file);
    assertEquals("java", extension);
  }

  @Test
  public void getExtensionPreservesUppercaseCharacters() throws Exception {
    String extension = FileUtilities.getExtension("IMAGE.PNG");
    assertEquals("PNG", extension);
  }

  @Test
  public void getExtensionPreservesMixedCaseCharacters() throws Exception {
    String extension = FileUtilities.getExtension("Document.TxT");
    assertEquals("TxT", extension);
  }

  @Test
  public void getExtensionSupportsNumericExtension() throws Exception {
    String extension = FileUtilities.getExtension("backup.001");
    assertEquals("001", extension);
  }

  @Test
  public void getExtensionSupportsWindowsStylePathString() throws Exception {
    String extension = FileUtilities.getExtension("C:\\temp\\example.LOG");
    assertEquals("LOG", extension);
  }

  @Test
  public void getExtensionSupportsTrailingDot() throws Exception {
    String extension = FileUtilities.getExtension("name.");
    assertEquals("", extension);
  }

  @Test
  public void getBaseNameFromSimpleFilename() throws Exception {
    String baseName = FileUtilities.getBaseName("notes.txt");
    assertEquals("notes", baseName);
  }

  @Test
  public void getBaseNameFromMultiDotFilename() throws Exception {
    String baseName = FileUtilities.getBaseName("archive.tar.gz");
    assertEquals("archive.tar", baseName);
  }

  @Test
  public void getBaseNameFromFilenameWithoutExtension() throws Exception {
    String baseName = FileUtilities.getBaseName("README");
    assertEquals("README", baseName);
  }

  @Test
  public void getBaseNameFromNullFilename() throws Exception {
    String baseName = FileUtilities.getBaseName((String) null);
    assertNull(baseName);
  }

  @Test
  public void getBaseNameTrimsUnixPathSeparators() throws Exception {
    String baseName = FileUtilities.getBaseName("a/b/c/example.txt");
    assertEquals("example", baseName);
  }

  @Test
  public void getBaseNameTrimsWindowsPathSeparators() throws Exception {
    String baseName = FileUtilities.getBaseName("a\\b\\c\\example.txt");
    assertEquals("example", baseName);
  }

  @Test
  public void getBaseNameSupportsMixedPathSeparators() throws Exception {
    String baseName = FileUtilities.getBaseName("a/b\\c/mixed.name.txt");
    assertEquals("mixed.name", baseName);
  }

  @Test
  public void getBaseNameFromFileInstance() throws Exception {
    File file = temporaryFolder.newFile("Movie.mp4");
    String baseName = FileUtilities.getBaseName(file);
    assertEquals("Movie", baseName);
  }

  @Test
  public void getBaseNameForDotFileDropsLeadingDotExtension() throws Exception {
    String baseName = FileUtilities.getBaseName(".gitignore");
    assertEquals("", baseName);
  }

  @Test
  public void getBaseNameForTrailingDotDropsDot() throws Exception {
    String baseName = FileUtilities.getBaseName("name.");
    assertEquals("name", baseName);
  }

  @Test
  public void isValidFileReturnsTrueForExistingFile() throws Exception {
    File file = temporaryFolder.newFile("existing.txt");
    assertTrue(FileUtilities.isValidFile(file));
    assertTrue(file.exists());
  }

  @Test
  public void isValidFileReturnsTrueForNonexistentFileWithCanonicalPath() throws Exception {
    File file = new File(temporaryFolder.getRoot(), "missing.txt");
    assertFalse(file.exists());
    assertTrue(FileUtilities.isValidFile(file));
  }

  @Test
  public void isValidFileReturnsFalseForNull() throws Exception {
    assertFalse(FileUtilities.isValidFile(null));
  }

  @Test
  public void isValidFileReturnsTrueForDirectory() throws Exception {
    File directory = temporaryFolder.newFolder("dir");
    assertTrue(FileUtilities.isValidFile(directory));
    assertTrue(directory.isDirectory());
  }

  @Test
  public void isValidFileReturnsFalseWhenCanonicalPathThrows() throws Exception {
    File file = new BrokenCanonicalFile("broken-file");
    assertFalse(FileUtilities.isValidFile(file));
  }

  @Test
  public void isValidPathReturnsTrueForExistingPath() throws Exception {
    File file = temporaryFolder.newFile("path.txt");
    assertTrue(FileUtilities.isValidPath(file.getAbsolutePath()));
  }

  @Test
  public void isValidPathReturnsTrueForNonexistentPath() throws Exception {
    File file = new File(temporaryFolder.getRoot(), "still-missing.txt");
    assertTrue(FileUtilities.isValidPath(file.getAbsolutePath()));
  }

  @Test
  public void isValidPathReturnsFalseForNull() throws Exception {
    assertFalse(FileUtilities.isValidPath(null));
  }

  @Test
  public void getCanonicalPathIfPossibleReturnsCanonicalPathForNormalFile() throws Exception {
    File file = temporaryFolder.newFile("canonical.txt");
    assertEquals(file.getCanonicalPath(), FileUtilities.getCanonicalPathIfPossible(file));
  }

  @Test
  public void getCanonicalPathIfPossibleReturnsNullForNullFile() throws Exception {
    assertNull(FileUtilities.getCanonicalPathIfPossible(null));
  }

  @Test
  public void getCanonicalPathIfPossibleResolvesDotDotSegments() throws Exception {
    File directory = temporaryFolder.newFolder("outer", "inner");
    File file = new File(directory, ".." + File.separator + "inner" + File.separator + "data.txt");
    writeText(file, "data");
    String canonicalPath = FileUtilities.getCanonicalPathIfPossible(file);
    assertTrue(canonicalPath.endsWith("inner" + File.separator + "data.txt"));
  }

  @Test
  public void getCanonicalPathIfPossibleFallsBackToAbsolutePathWhenCanonicalPathFails() throws Exception {
    File file = new BrokenCanonicalFile("broken-fallback.txt");
    String pathValue = FileUtilities.getCanonicalPathIfPossible(file);
    assertEquals(file.getAbsolutePath(), pathValue);
  }

  @Test
  public void getCanonicalPathIfPossibleResolvesSymbolicLinkWhenSupported() throws Exception {
    File target = temporaryFolder.newFile("target.txt");
    writeText(target, "target");
    File link = new File(temporaryFolder.getRoot(), "link.txt");
    try {
      Files.createSymbolicLink(link.toPath(), target.toPath());
    } catch (Exception exception) {
      Assume.assumeNoException(exception);
    }
    assertEquals(target.getCanonicalPath(), FileUtilities.getCanonicalPathIfPossible(link));
  }

  @Test
  public void createParentDirectoriesIfNecessaryCreatesDeepParents() throws Exception {
    File file = new File(temporaryFolder.getRoot(), "a/b/c/d/file.txt");
    boolean created = FileUtilities.createParentDirectoriesIfNecessary(file);
    assertTrue(created);
    assertTrue(file.getParentFile().exists());
  }

  @Test
  public void createParentDirectoriesIfNecessaryIsIdempotent() throws Exception {
    File file = new File(temporaryFolder.getRoot(), "repeat/again/file.txt");
    assertTrue(FileUtilities.createParentDirectoriesIfNecessary(file));
    assertFalse(FileUtilities.createParentDirectoriesIfNecessary(file));
    assertTrue(file.getParentFile().exists());
  }

  @Test
  public void createParentDirectoriesIfNecessaryReturnsFalseWhenParentAlreadyExists() throws Exception {
    File parent = temporaryFolder.newFolder("existing-parent");
    File file = new File(parent, "file.txt");
    boolean created = FileUtilities.createParentDirectoriesIfNecessary(file);
    assertFalse(created);
  }

  @Test
  public void createParentDirectoriesIfNecessarySupportsSiblingFiles() throws Exception {
    File first = new File(temporaryFolder.getRoot(), "siblings/one.txt");
    File second = new File(temporaryFolder.getRoot(), "siblings/two.txt");
    assertTrue(FileUtilities.createParentDirectoriesIfNecessary(first));
    assertFalse(FileUtilities.createParentDirectoriesIfNecessary(second));
    assertTrue(second.getParentFile().exists());
  }

  @Test
  public void createParentDirectoriesIfNecessaryCreatesSingleLevelParent() throws Exception {
    File file = new File(temporaryFolder.getRoot(), "single/file.txt");
    assertTrue(FileUtilities.createParentDirectoriesIfNecessary(file));
    assertTrue(file.getParentFile().isDirectory());
  }

  @Test
  public void listFilesByExtensionFindsMatchingFiles() throws Exception {
    File directory = temporaryFolder.newFolder("listing");
    writeText(new File(directory, "a.txt"), "a");
    writeText(new File(directory, "b.txt"), "b");
    writeText(new File(directory, "c.java"), "c");
    File[] files = FileUtilities.listFiles(directory, "txt");
    assertEquals(Arrays.asList("a.txt", "b.txt"), names(files));
  }

  @Test
  public void listFilesByExtensionReturnsEmptyArrayForEmptyDirectory() throws Exception {
    File directory = temporaryFolder.newFolder("empty-listing");
    File[] files = FileUtilities.listFiles(directory, "txt");
    assertNotNull(files);
    assertEquals(0, files.length);
  }

  @Test
  public void listFilesByExtensionReturnsEmptyArrayWhenNoMatchExists() throws Exception {
    File directory = temporaryFolder.newFolder("no-match");
    writeText(new File(directory, "a.java"), "a");
    File[] files = FileUtilities.listFiles(directory, "txt");
    assertEquals(0, files.length);
  }

  @Test
  public void listFilesByExtensionIsCaseInsensitive() throws Exception {
    File directory = temporaryFolder.newFolder("case-insensitive");
    writeText(new File(directory, "a.TxT"), "a");
    writeText(new File(directory, "b.TXT"), "b");
    File[] files = FileUtilities.listFiles(directory, "txt");
    assertEquals(Arrays.asList("a.TxT", "b.TXT"), names(files));
  }

  @Test
  public void listFilesWithFileFilterUsesProvidedFilter() throws Exception {
    File directory = temporaryFolder.newFolder("filter");
    writeText(new File(directory, "keep.me"), "a");
    writeText(new File(directory, "skip.me"), "b");
    FileFilter filter = new FileFilter() {
      @Override
      public boolean accept(File file) {
        return file.getName().startsWith("keep");
      }
    };
    File[] files = FileUtilities.listFiles(directory, filter);
    assertEquals(Arrays.asList("keep.me"), names(files));
  }

  @Test
  public void listFilesByStringRootPathDelegatesToFileVersion() throws Exception {
    File directory = temporaryFolder.newFolder("string-root");
    writeText(new File(directory, "data.xml"), "xml");
    File[] files = FileUtilities.listFiles(directory.getAbsolutePath(), "xml");
    assertEquals(Arrays.asList("data.xml"), names(files));
  }

  @Test
  public void listFilesReturnsEmptyArrayForMissingRoot() throws Exception {
    File directory = new File(temporaryFolder.getRoot(), "missing-root");
    File[] files = FileUtilities.listFiles(directory, FileUtilities.createDirectoryFilter());
    assertNotNull(files);
    assertEquals(0, files.length);
  }

  @Test
  public void listFilesByExtensionIgnoresDirectories() throws Exception {
    File directory = temporaryFolder.newFolder("dir-and-file");
    temporaryFolder.newFolder("dir-and-file", "folder.txt");
    writeText(new File(directory, "real.txt"), "value");
    File[] files = FileUtilities.listFiles(directory, "txt");
    assertEquals(Arrays.asList("real.txt"), names(files));
  }

  @Test
  public void listFilesByExtensionSupportsUppercaseSearchString() throws Exception {
    File directory = temporaryFolder.newFolder("upper-ext");
    writeText(new File(directory, "alpha.txt"), "alpha");
    File[] files = FileUtilities.listFiles(directory, "TXT");
    assertEquals(Arrays.asList("alpha.txt"), names(files));
  }

  @Test
  public void listDirectoriesReturnsImmediateDirectoriesOnly() throws Exception {
    File directory = temporaryFolder.newFolder("directories");
    temporaryFolder.newFolder("directories", "a");
    temporaryFolder.newFolder("directories", "b");
    writeText(new File(directory, "file.txt"), "value");
    File[] files = FileUtilities.listDirectories(directory);
    assertEquals(Arrays.asList("a", "b"), names(files));
  }

  @Test
  public void listDirectoriesReturnsEmptyForDirectoryWithoutChildren() throws Exception {
    File directory = temporaryFolder.newFolder("no-directories");
    File[] files = FileUtilities.listDirectories(directory);
    assertEquals(0, files.length);
  }

  @Test
  public void listDirectoriesIgnoresPlainFiles() throws Exception {
    File directory = temporaryFolder.newFolder("ignore-files");
    writeText(new File(directory, "plain.txt"), "value");
    File[] files = FileUtilities.listDirectories(directory);
    assertEquals(0, files.length);
  }

  @Test
  public void listDirectoriesReturnsEmptyForMissingRoot() throws Exception {
    File directory = new File(temporaryFolder.getRoot(), "missing-dirs");
    File[] files = FileUtilities.listDirectories(directory);
    assertEquals(0, files.length);
  }

  @Test
  public void listDescendantsRecursivelyFindsFiles() throws Exception {
    File root = temporaryFolder.newFolder("descendants-all");
    writeText(new File(root, "root.txt"), "root");
    writeText(new File(root, "level1/child.txt"), "child");
    writeText(new File(root, "level1/level2/grandchild.txt"), "grandchild");
    File[] files = FileUtilities.listDescendants(root, FileUtilities.createFileWithExtensionFilter(".txt"));
    assertEquals(Arrays.asList("child.txt", "grandchild.txt", "root.txt"), names(files));
  }

  @Test
  public void listDescendantsDepthZeroOnlyIncludesRootLevelMatches() throws Exception {
    File root = temporaryFolder.newFolder("depth-zero");
    writeText(new File(root, "root.txt"), "root");
    writeText(new File(root, "child/child.txt"), "child");
    File[] files = FileUtilities.listDescendants(root, FileUtilities.createFileWithExtensionFilter(".txt"), 0);
    assertEquals(Arrays.asList("root.txt"), names(files));
  }

  @Test
  public void listDescendantsDepthOneIncludesChildDirectoryMatches() throws Exception {
    File root = temporaryFolder.newFolder("depth-one");
    writeText(new File(root, "root.txt"), "root");
    writeText(new File(root, "child/child.txt"), "child");
    writeText(new File(root, "child/grand/grand.txt"), "grand");
    File[] files = FileUtilities.listDescendants(root, FileUtilities.createFileWithExtensionFilter(".txt"), 1);
    assertEquals(Arrays.asList("child.txt", "root.txt"), names(files));
  }

  @Test
  public void listDescendantsDepthMinusOneTraversesEntireTree() throws Exception {
    File root = temporaryFolder.newFolder("depth-all");
    writeText(new File(root, "root.txt"), "root");
    writeText(new File(root, "child/child.txt"), "child");
    writeText(new File(root, "child/grand/grand.txt"), "grand");
    File[] files = FileUtilities.listDescendants(root, FileUtilities.createFileWithExtensionFilter(".txt"), -1);
    assertEquals(Arrays.asList("child.txt", "grand.txt", "root.txt"), names(files));
  }

  @Test
  public void listDescendantsReturnsEmptyForEmptyDirectory() throws Exception {
    File root = temporaryFolder.newFolder("descendants-empty");
    File[] files = FileUtilities.listDescendants(root, FileUtilities.createFileWithExtensionFilter(".txt"));
    assertEquals(0, files.length);
  }

  @Test
  public void listDescendantsByExtensionFindsMatchingFiles() throws Exception {
    File root = temporaryFolder.newFolder("descendants-ext");
    writeText(new File(root, "a.txt"), "a");
    writeText(new File(root, "child/b.txt"), "b");
    writeText(new File(root, "child/c.java"), "c");
    File[] files = FileUtilities.listDescendants(root, "txt");
    assertEquals(Arrays.asList("a.txt", "b.txt"), names(files));
  }

  @Test
  public void listDescendantsByExtensionIsCaseInsensitive() throws Exception {
    File root = temporaryFolder.newFolder("descendants-case");
    writeText(new File(root, "a.TXT"), "a");
    writeText(new File(root, "child/b.TxT"), "b");
    File[] files = FileUtilities.listDescendants(root, "txt");
    assertEquals(Arrays.asList("a.TXT", "b.TxT"), names(files));
  }

  @Test
  public void listDescendantsWithCustomFilterCanLimitByPrefix() throws Exception {
    File root = temporaryFolder.newFolder("descendants-filter");
    writeText(new File(root, "keep-one.txt"), "a");
    writeText(new File(root, "skip-two.txt"), "b");
    FileFilter filter = new FileFilter() {
      @Override
      public boolean accept(File file) {
        return file.isFile() && file.getName().startsWith("keep");
      }
    };
    File[] files = FileUtilities.listDescendants(root, filter);
    assertEquals(Arrays.asList("keep-one.txt"), names(files));
  }

  @Test
  public void listDescendantsWithDirectoryFilterReturnsDirectories() throws Exception {
    File root = temporaryFolder.newFolder("descendant-directories");
    temporaryFolder.newFolder("descendant-directories", "a");
    temporaryFolder.newFolder("descendant-directories", "a", "b");
    File[] files = FileUtilities.listDescendants(root, FileUtilities.createDirectoryFilter());
    assertEquals(Arrays.asList("a", "b"), names(files));
  }

  @Test
  public void createFilenameFilterMatchesExpectedSuffix() throws Exception {
    FilenameFilter filter = FileUtilities.createFilenameFilter(".txt");
    assertTrue(filter.accept(temporaryFolder.getRoot(), "alpha.txt"));
    assertFalse(filter.accept(temporaryFolder.getRoot(), "alpha.java"));
  }

  @Test
  public void createFilenameFilterIsCaseInsensitive() throws Exception {
    FilenameFilter filter = FileUtilities.createFilenameFilter(".TXT");
    assertTrue(filter.accept(temporaryFolder.getRoot(), "alpha.txt"));
    assertTrue(filter.accept(temporaryFolder.getRoot(), "beta.TxT"));
  }

  @Test
  public void createFilenameFilterRejectsDifferentExtensions() throws Exception {
    FilenameFilter filter = FileUtilities.createFilenameFilter(".png");
    assertFalse(filter.accept(temporaryFolder.getRoot(), "alpha.txt"));
  }

  @Test
  public void createFileWithExtensionFilterAcceptsMatchingFile() throws Exception {
    FileFilter filter = FileUtilities.createFileWithExtensionFilter(".xml");
    File file = temporaryFolder.newFile("data.xml");
    assertTrue(filter.accept(file));
  }

  @Test
  public void createFileWithExtensionFilterRejectsDirectory() throws Exception {
    FileFilter filter = FileUtilities.createFileWithExtensionFilter(".xml");
    File directory = temporaryFolder.newFolder("xml-dir");
    assertFalse(filter.accept(directory));
  }

  @Test
  public void createFileWithExtensionFilterIsCaseInsensitive() throws Exception {
    FileFilter filter = FileUtilities.createFileWithExtensionFilter(".TXT");
    File file = temporaryFolder.newFile("notes.txt");
    assertTrue(filter.accept(file));
  }

  @Test
  public void createDirectoryFilterAcceptsDirectory() throws Exception {
    FileFilter filter = FileUtilities.createDirectoryFilter();
    File directory = temporaryFolder.newFolder("directory-filter");
    assertTrue(filter.accept(directory));
  }

  @Test
  public void createDirectoryFilterRejectsRegularFile() throws Exception {
    FileFilter filter = FileUtilities.createDirectoryFilter();
    File file = temporaryFolder.newFile("regular.txt");
    assertFalse(filter.accept(file));
  }

  @Test
  public void copyFilePreservesTextContent() throws Exception {
    File source = temporaryFolder.newFile("source.txt");
    writeText(source, "copy me");
    File destination = new File(temporaryFolder.getRoot(), "destination.txt");
    FileUtilities.copyFile(source, destination);
    assertEquals("copy me", Files.readString(destination.toPath(), StandardCharsets.UTF_8));
  }

  @Test
  public void copyFilePreservesLargeContent() throws Exception {
    File source = temporaryFolder.newFile("large.bin");
    byte[] expected = createBytes(100000);
    writeBytes(source, expected);
    File destination = new File(temporaryFolder.getRoot(), "large-copy.bin");
    FileUtilities.copyFile(source, destination);
    assertArrayEquals(expected, Files.readAllBytes(destination.toPath()));
  }

  @Test
  public void copyFileCreatesParentDirectories() throws Exception {
    File source = temporaryFolder.newFile("parent-source.txt");
    writeText(source, "nested");
    File destination = new File(temporaryFolder.getRoot(), "a/b/c/parent-destination.txt");
    FileUtilities.copyFile(source, destination);
    assertTrue(destination.exists());
    assertEquals("nested", Files.readString(destination.toPath(), StandardCharsets.UTF_8));
  }

  @Test
  public void copyFileSupportsEmptyFiles() throws Exception {
    File source = temporaryFolder.newFile("empty-source.txt");
    writeText(source, "");
    File destination = new File(temporaryFolder.getRoot(), "empty-destination.txt");
    FileUtilities.copyFile(source, destination);
    assertEquals(0L, Files.size(destination.toPath()));
  }

  @Test
  public void copyFileSupportsBinaryData() throws Exception {
    File source = temporaryFolder.newFile("binary-source.bin");
    byte[] expected = new byte[] {0, 1, 2, -1, 127, -128};
    writeBytes(source, expected);
    File destination = new File(temporaryFolder.getRoot(), "binary-destination.bin");
    FileUtilities.copyFile(source, destination);
    assertArrayEquals(expected, Files.readAllBytes(destination.toPath()));
  }

  @Test
  public void copyFileOverwritesExistingDestination() throws Exception {
    File source = temporaryFolder.newFile("overwrite-source.txt");
    writeText(source, "new-value");
    File destination = temporaryFolder.newFile("overwrite-destination.txt");
    writeText(destination, "old-value");
    FileUtilities.copyFile(source, destination);
    assertEquals("new-value", Files.readString(destination.toPath(), StandardCharsets.UTF_8));
  }

  @Test
  public void copyFileSupportsUnicodeText() throws Exception {
    File source = temporaryFolder.newFile("unicode-source.txt");
    writeText(source, "こんにちは مرحبا 🙂");
    File destination = new File(temporaryFolder.getRoot(), "unicode-destination.txt");
    FileUtilities.copyFile(source, destination);
    assertEquals("こんにちは مرحبا 🙂", Files.readString(destination.toPath(), StandardCharsets.UTF_8));
  }

  @Test
  public void copyFileSupportsExistingNestedParentDirectory() throws Exception {
    File source = temporaryFolder.newFile("existing-parent-source.txt");
    writeText(source, "data");
    File parent = temporaryFolder.newFolder("existing-parent-copy");
    File destination = new File(parent, "copied.txt");
    FileUtilities.copyFile(source, destination);
    assertEquals("data", Files.readString(destination.toPath(), StandardCharsets.UTF_8));
  }

  @Test
  public void getCreatedDateTimeReturnsValueForExistingFile() throws Exception {
    File file = temporaryFolder.newFile("created-file.txt");
    LocalDateTime created = FileUtilities.getCreatedDateTime(file);
    assertNotNull(created);
    assertNotEquals(LocalDateTime.MIN, created);
  }

  @Test
  public void getCreatedDateTimeReturnsValueForExistingDirectory() throws Exception {
    File directory = temporaryFolder.newFolder("created-directory");
    LocalDateTime created = FileUtilities.getCreatedDateTime(directory);
    assertNotNull(created);
    assertNotEquals(LocalDateTime.MIN, created);
  }

  @Test
  public void getCreatedDateTimeReturnsMinForNonexistentFile() throws Exception {
    File file = new File(temporaryFolder.getRoot(), "missing-created.txt");
    LocalDateTime created = FileUtilities.getCreatedDateTime(file);
    assertEquals(LocalDateTime.MIN, created);
  }

  @Test
  public void getCreatedDateTimeNullThrowsNullPointerException() throws Exception {
    try {
      FileUtilities.getCreatedDateTime(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException npe) {
      assertNotNull(npe);
    }
  }

  @Test
  public void getModifiedDateTimeReturnsValueForExistingFile() throws Exception {
    File file = temporaryFolder.newFile("modified-file.txt");
    LocalDateTime modified = FileUtilities.getModifiedDateTime(file);
    assertNotNull(modified);
    assertNotEquals(LocalDateTime.MIN, modified);
  }

  @Test
  public void getModifiedDateTimeReturnsValueForExistingDirectory() throws Exception {
    File directory = temporaryFolder.newFolder("modified-directory");
    LocalDateTime modified = FileUtilities.getModifiedDateTime(directory);
    assertNotNull(modified);
    assertNotEquals(LocalDateTime.MIN, modified);
  }

  @Test
  public void getModifiedDateTimeReturnsMinForNullFile() throws Exception {
    LocalDateTime modified = FileUtilities.getModifiedDateTime(null);
    assertEquals(LocalDateTime.MIN, modified);
  }

  @Test
  public void getModifiedDateTimeReturnsMinForNonexistentFile() throws Exception {
    File file = new File(temporaryFolder.getRoot(), "missing-modified.txt");
    LocalDateTime modified = FileUtilities.getModifiedDateTime(file);
    assertEquals(LocalDateTime.MIN, modified);
  }

  @Test
  public void getModifiedDateTimeChangesAfterWritingFile() throws Exception {
    File file = temporaryFolder.newFile("write-time.txt");
    writeText(file, "before");
    LocalDateTime before = FileUtilities.getModifiedDateTime(file);
    Thread.sleep(30L);
    writeText(file, "after");
    LocalDateTime after = FileUtilities.getModifiedDateTime(file);
    assertTrue(!after.isBefore(before));
  }

}
