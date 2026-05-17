package edu.cmu.cs.dennisc.java.util.zip;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;

/**
 * Tests for ZipUtilities — zip/unzip round-trips, directory zipping,
 * filtered extraction, and DataSource write.
 */
public class ZipUtilitiesTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  // --- Helper methods ---

  private File createDirectoryWithFiles(String dirName, String... fileNames) throws IOException {
    File dir = tempFolder.newFolder(dirName);
    for (String name : fileNames) {
      File f = new File(dir, name);
      f.getParentFile().mkdirs();
      Files.writeString(f.toPath(), "content-of-" + name);
    }
    return dir;
  }

  // --- zip/unzip round-trip ---

  @Test
  public void zipUnzip_roundTrip_preservesContent() throws IOException {
    File srcDir = createDirectoryWithFiles("src", "a.txt", "b.txt");
    File zipFile = new File(tempFolder.getRoot(), "test.zip");
    File destDir = tempFolder.newFolder("dest");

    ZipUtilities.zip(srcDir, zipFile);
    assertTrue(zipFile.exists());
    assertTrue(zipFile.length() > 0);

    ZipUtilities.unzip(zipFile, destDir);

    // Verify content round-tripped
    File extractedA = new File(destDir, "a.txt");
    File extractedB = new File(destDir, "b.txt");
    assertTrue(extractedA.exists());
    assertTrue(extractedB.exists());
    assertEquals("content-of-a.txt", Files.readString(extractedA.toPath()));
    assertEquals("content-of-b.txt", Files.readString(extractedB.toPath()));
  }

  @Test
  public void zipUnzip_roundTrip_withStrings() throws IOException {
    File srcDir = createDirectoryWithFiles("srcstr", "file.dat");
    File zipFile = new File(tempFolder.getRoot(), "strings.zip");
    File destDir = tempFolder.newFolder("deststr");

    ZipUtilities.zip(srcDir.getAbsolutePath(), zipFile.getAbsolutePath());
    ZipUtilities.unzip(zipFile.getAbsolutePath(), destDir.getAbsolutePath());

    assertTrue(new File(destDir, "file.dat").exists());
  }

  @Test
  public void zip_stringSrcPathFileObj() throws IOException {
    File srcDir = createDirectoryWithFiles("srcmix1", "data.csv");
    File zipFile = new File(tempFolder.getRoot(), "mix1.zip");

    ZipUtilities.zip(srcDir.getAbsolutePath(), zipFile);
    assertTrue(zipFile.exists());
  }

  @Test
  public void zip_fileSrcStringPath() throws IOException {
    File srcDir = createDirectoryWithFiles("srcmix2", "data.csv");
    String zipPath = new File(tempFolder.getRoot(), "mix2.zip").getAbsolutePath();

    ZipUtilities.zip(srcDir, zipPath);
    assertTrue(new File(zipPath).exists());
  }

  @Test
  public void unzip_fileObjStringPath() throws IOException {
    File srcDir = createDirectoryWithFiles("unsrcfp", "x.txt");
    File zipFile = new File(tempFolder.getRoot(), "fp.zip");
    ZipUtilities.zip(srcDir, zipFile);

    String destPath = tempFolder.newFolder("destfp").getAbsolutePath();
    ZipUtilities.unzip(zipFile, destPath);
    assertTrue(new File(destPath, "x.txt").exists());
  }

  @Test
  public void unzip_stringPathFileObj() throws IOException {
    File srcDir = createDirectoryWithFiles("unsrcsp", "y.txt");
    File zipFile = new File(tempFolder.getRoot(), "sp.zip");
    ZipUtilities.zip(srcDir, zipFile);

    File destDir = tempFolder.newFolder("destsp");
    ZipUtilities.unzip(zipFile.getAbsolutePath(), destDir);
    assertTrue(new File(destDir, "y.txt").exists());
  }

  // --- Nested directory zipping ---

  @Test
  public void zip_nestedDirectories() throws IOException {
    File srcDir = createDirectoryWithFiles("nested",
        "top.txt", "sub/middle.txt", "sub/deep/bottom.txt");
    File zipFile = new File(tempFolder.getRoot(), "nested.zip");
    File destDir = tempFolder.newFolder("nested-dest");

    ZipUtilities.zip(srcDir, zipFile);
    ZipUtilities.unzip(zipFile, destDir);

    assertTrue(new File(destDir, "top.txt").exists());
    assertTrue(new File(destDir, "sub/middle.txt").exists());
    assertTrue(new File(destDir, "sub/deep/bottom.txt").exists());
  }

  // --- Filtered zipping ---

  @Test
  public void zipFilesInDirectory_withFilter() throws IOException {
    File srcDir = createDirectoryWithFiles("filtered", "keep.txt", "skip.log", "also.txt");
    File zipFile = new File(tempFolder.getRoot(), "filtered.zip");

    FileFilter txtOnly = f -> f.getName().endsWith(".txt");
    ZipUtilities.zipFilesInDirectory(srcDir, zipFile, txtOnly);

    Map<String, byte[]> entries = ZipUtilities.extract(zipFile);
    boolean hasTxt = false;
    boolean hasLog = false;
    for (String key : entries.keySet()) {
      if (key.endsWith(".txt")) hasTxt = true;
      if (key.endsWith(".log")) hasLog = true;
    }
    assertTrue("Should contain .txt files", hasTxt);
    assertFalse("Should not contain .log files", hasLog);
  }

  @Test
  public void zipFilesInDirectory_noFilter() throws IOException {
    File srcDir = createDirectoryWithFiles("unfiltered", "a.txt", "b.log");
    File zipFile = new File(tempFolder.getRoot(), "unfiltered.zip");

    ZipUtilities.zipFilesInDirectory(srcDir, zipFile);

    Map<String, byte[]> entries = ZipUtilities.extract(zipFile);
    assertTrue(entries.size() >= 2);
  }

  // --- extract methods ---

  @Test
  public void extract_fromFile() throws IOException {
    File srcDir = createDirectoryWithFiles("exfile", "data.txt");
    File zipFile = new File(tempFolder.getRoot(), "exfile.zip");
    ZipUtilities.zip(srcDir, zipFile);

    Map<String, byte[]> entries = ZipUtilities.extract(zipFile);
    assertFalse(entries.isEmpty());
  }

  @Test
  public void extract_fromString() throws IOException {
    File srcDir = createDirectoryWithFiles("exstr", "info.txt");
    File zipFile = new File(tempFolder.getRoot(), "exstr.zip");
    ZipUtilities.zip(srcDir, zipFile);

    Map<String, byte[]> entries = ZipUtilities.extract(zipFile.getAbsolutePath());
    assertFalse(entries.isEmpty());
  }

  @Test
  public void extract_fromInputStream() throws IOException {
    File srcDir = createDirectoryWithFiles("exis", "stream.txt");
    File zipFile = new File(tempFolder.getRoot(), "exis.zip");
    ZipUtilities.zip(srcDir, zipFile);

    Map<String, byte[]> entries = ZipUtilities.extract(
        new java.io.FileInputStream(zipFile));
    assertFalse(entries.isEmpty());
  }

  @Test
  public void extract_withFilter() throws IOException {
    File srcDir = createDirectoryWithFiles("exfilt", "wanted.txt", "unwanted.log");
    File zipFile = new File(tempFolder.getRoot(), "exfilt.zip");
    ZipUtilities.zip(srcDir, zipFile);

    Collection<String> filter = Arrays.asList("wanted.txt");
    Map<String, byte[]> entries = ZipUtilities.extract(zipFile, filter);
    // Should only contain filtered entries
    for (String key : entries.keySet()) {
      assertTrue(key.contains("wanted"));
    }
  }

  @Test
  public void extract_fromFileWithFilter() throws IOException {
    File srcDir = createDirectoryWithFiles("exff", "match.dat", "nomatch.xml");
    File zipFile = new File(tempFolder.getRoot(), "exff.zip");
    ZipUtilities.zip(srcDir, zipFile);

    Collection<String> filter = new HashSet<>(Arrays.asList("match.dat"));
    Map<String, byte[]> entries = ZipUtilities.extract(zipFile.getAbsolutePath(), filter);
    assertNotNull(entries);
  }

  // --- extractBytes ---

  @Test
  public void extractBytes_fromZipEntry() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);
    zos.putNextEntry(new ZipEntry("test-entry.txt"));
    byte[] content = "entry-content".getBytes();
    zos.write(content);
    zos.closeEntry();
    zos.close();

    ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(baos.toByteArray()));
    ZipEntry entry = zis.getNextEntry();
    assertNotNull(entry);
    byte[] extracted = ZipUtilities.extractBytes(zis, entry);
    assertArrayEquals(content, extracted);
  }

  // --- DataSource write ---

  @Test
  public void write_dataSource() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    DataSource ds = new DataSource() {
      @Override
      public String getName() {
        return "test-datasource.bin";
      }

      @Override
      public void write(OutputStream os) throws IOException {
        os.write(new byte[]{1, 2, 3, 4, 5});
      }
    };

    ZipUtilities.write(zos, ds);
    zos.close();

    // Verify the DataSource was written into the zip
    Map<String, byte[]> entries = ZipUtilities.extract(
        new ByteArrayInputStream(baos.toByteArray()));
    assertTrue(entries.containsKey("test-datasource.bin"));
    assertArrayEquals(new byte[]{1, 2, 3, 4, 5}, entries.get("test-datasource.bin"));
  }

  // --- addFileToZipStream ---

  @Test
  public void addFileToZipStream_singleFile() throws IOException {
    File file = tempFolder.newFile("single.txt");
    Files.writeString(file.toPath(), "single-content");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);
    ZipUtilities.addFileToZipStream(file, zos, "prefix/");
    zos.close();

    Map<String, byte[]> entries = ZipUtilities.extract(
        new ByteArrayInputStream(baos.toByteArray()));
    assertFalse(entries.isEmpty());
  }

  @Test
  public void addFileToZipStream_withRootDir() throws IOException {
    File root = tempFolder.newFolder("root");
    File file = new File(root, "child.txt");
    Files.writeString(file.toPath(), "child-content");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);
    ZipUtilities.addFileToZipStream(root, file, zos, "");
    zos.close();

    Map<String, byte[]> entries = ZipUtilities.extract(
        new ByteArrayInputStream(baos.toByteArray()));
    assertFalse(entries.isEmpty());
  }

  // --- addDirToZipStream ---

  @Test
  public void addDirToZipStream() throws IOException {
    File dir = createDirectoryWithFiles("adddir", "one.txt", "two.txt");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);
    ZipUtilities.addDirToZipStream(dir, zos, "");
    zos.close();

    Map<String, byte[]> entries = ZipUtilities.extract(
        new ByteArrayInputStream(baos.toByteArray()));
    assertTrue(entries.size() >= 2);
  }

  @Test
  public void addDirContentsToZipStream() throws IOException {
    File dir = createDirectoryWithFiles("addcontents", "alpha.txt", "beta.txt");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);
    ZipUtilities.addDirContentsToZipStream(dir, zos, "custom/");
    zos.close();

    Map<String, byte[]> entries = ZipUtilities.extract(
        new ByteArrayInputStream(baos.toByteArray()));
    assertFalse(entries.isEmpty());
    // Verify prefix
    for (String key : entries.keySet()) {
      assertTrue("Entry should start with custom/: " + key, key.startsWith("custom/"));
    }
  }

  // --- Empty directory ---

  @Test
  public void zip_emptyDirectory() throws IOException {
    File emptyDir = tempFolder.newFolder("empty");
    File zipFile = new File(tempFolder.getRoot(), "empty.zip");

    ZipUtilities.zip(emptyDir, zipFile);
    assertTrue(zipFile.exists());
  }
}
