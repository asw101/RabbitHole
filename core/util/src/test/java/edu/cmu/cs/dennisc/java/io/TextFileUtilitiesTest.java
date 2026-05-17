package edu.cmu.cs.dennisc.java.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.*;

/**
 * Tests for TextFileUtilities — read/write round-trips via
 * File, InputStream, Reader, and String path.
 */
public class TextFileUtilitiesTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private static final String SEP = System.getProperty("line.separator");

  // --- read/write via File ---

  @Test
  public void readWrite_file_roundTrip() throws IOException {
    File f = tempFolder.newFile("roundtrip.txt");
    String content = "Hello, Alice!\nLine 2\nLine 3";
    TextFileUtilities.write(f, content);
    String result = TextFileUtilities.read(f);
    // read() uses readLine→append(SEPARATOR) so adds trailing separator
    assertEquals(content + SEP, result);
  }

  @Test
  public void readWrite_file_emptyContent() throws IOException {
    File f = tempFolder.newFile("empty.txt");
    TextFileUtilities.write(f, "");
    String result = TextFileUtilities.read(f);
    assertEquals("", result);
  }

  @Test
  public void readWrite_file_unicode() throws IOException {
    File f = tempFolder.newFile("unicode.txt");
    String content = "café \u2603 \u00E9\u00E8\u00EA";
    TextFileUtilities.write(f, content);
    String result = TextFileUtilities.read(f);
    assertEquals(content + SEP, result);
  }

  @Test
  public void readWrite_file_multiline() throws IOException {
    File f = tempFolder.newFile("multiline.txt");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      sb.append("Line ").append(i).append("\n");
    }
    String content = sb.toString();
    TextFileUtilities.write(f, content);
    String result = TextFileUtilities.read(f);
    // Each line gets SEP appended; last \n becomes an empty trailing line
    assertNotNull(result);
    assertTrue(result.contains("Line 0"));
    assertTrue(result.contains("Line 99"));
  }

  // --- read via String path ---

  @Test
  public void read_byPath() throws IOException {
    File f = tempFolder.newFile("bypath.txt");
    Files.writeString(f.toPath(), "path-content");
    String result = TextFileUtilities.read(f.getAbsolutePath());
    assertEquals("path-content" + SEP, result);
  }

  // --- read via InputStream ---

  @Test
  public void read_inputStream() {
    String content = "stream-content\nline2";
    InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    String result = TextFileUtilities.read(is);
    assertEquals("stream-content" + SEP + "line2" + SEP, result);
  }

  @Test
  public void read_inputStream_empty() {
    InputStream is = new ByteArrayInputStream(new byte[0]);
    String result = TextFileUtilities.read(is);
    assertEquals("", result);
  }

  // --- read via Reader ---

  @Test
  public void read_reader() {
    String content = "reader-content";
    StringReader reader = new StringReader(content);
    String result = TextFileUtilities.read(reader);
    assertEquals(content + SEP, result);
  }

  @Test
  public void read_reader_empty() {
    StringReader reader = new StringReader("");
    String result = TextFileUtilities.read(reader);
    assertEquals("", result);
  }

  // --- write via Writer ---

  @Test
  public void write_writer() {
    StringWriter writer = new StringWriter();
    TextFileUtilities.write(writer, "writer-content");
    assertEquals("writer-content", writer.toString());
  }

  @Test
  public void write_writer_empty() {
    StringWriter writer = new StringWriter();
    TextFileUtilities.write(writer, "");
    assertEquals("", writer.toString());
  }

  // --- Large content ---

  @Test
  public void readWrite_largeContent() throws IOException {
    File f = tempFolder.newFile("large.txt");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      sb.append("ABCDEFGHIJ");
    }
    String content = sb.toString();
    TextFileUtilities.write(f, content);
    String result = TextFileUtilities.read(f);
    assertEquals(content + SEP, result);
  }
}
