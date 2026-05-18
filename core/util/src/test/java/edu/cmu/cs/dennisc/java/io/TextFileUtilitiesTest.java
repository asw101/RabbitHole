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

  // ====== read/write via File ======

  @Test
  public void readWrite_file_roundTrip() throws IOException {
    File f = tempFolder.newFile("roundtrip.txt");
    String content = "Hello, Alice!\nLine 2\nLine 3";
    TextFileUtilities.write(f, content);
    String result = TextFileUtilities.read(f);
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
    assertNotNull(result);
    assertTrue(result.contains("Line 0"));
    assertTrue(result.contains("Line 99"));
  }

  @Test
  public void readWrite_file_singleChar() throws IOException {
    File f = tempFolder.newFile("singlechar.txt");
    TextFileUtilities.write(f, "X");
    String result = TextFileUtilities.read(f);
    assertEquals("X" + SEP, result);
  }

  @Test
  public void readWrite_file_singleLine() throws IOException {
    File f = tempFolder.newFile("singleline.txt");
    String content = "just one line";
    TextFileUtilities.write(f, content);
    assertEquals(content + SEP, TextFileUtilities.read(f));
  }

  @Test
  public void readWrite_file_tabs() throws IOException {
    File f = tempFolder.newFile("tabs.txt");
    String content = "col1\tcol2\tcol3";
    TextFileUtilities.write(f, content);
    String result = TextFileUtilities.read(f);
    assertEquals(content + SEP, result);
  }

  @Test
  public void readWrite_file_specialChars() throws IOException {
    File f = tempFolder.newFile("special.txt");
    String content = "angle <brackets> & ampersand \"quotes\" 'apostrophe'";
    TextFileUtilities.write(f, content);
    String result = TextFileUtilities.read(f);
    assertEquals(content + SEP, result);
  }

  @Test
  public void readWrite_file_backslashes() throws IOException {
    File f = tempFolder.newFile("backslash.txt");
    String content = "path\\to\\file";
    TextFileUtilities.write(f, content);
    assertEquals(content + SEP, TextFileUtilities.read(f));
  }

  @Test
  public void readWrite_file_emptyLines() throws IOException {
    File f = tempFolder.newFile("emptylines.txt");
    String content = "line1\n\n\nline4";
    TextFileUtilities.write(f, content);
    String result = TextFileUtilities.read(f);
    assertNotNull(result);
    assertTrue(result.contains("line1"));
    assertTrue(result.contains("line4"));
  }

  @Test
  public void readWrite_file_overwrite() throws IOException {
    File f = tempFolder.newFile("overwrite.txt");
    TextFileUtilities.write(f, "original");
    TextFileUtilities.write(f, "replaced");
    assertEquals("replaced" + SEP, TextFileUtilities.read(f));
  }

  @Test
  public void readWrite_file_longLine() throws IOException {
    File f = tempFolder.newFile("longline.txt");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      sb.append('A');
    }
    String content = sb.toString();
    TextFileUtilities.write(f, content);
    assertEquals(content + SEP, TextFileUtilities.read(f));
  }

  @Test
  public void write_createsParentDirectories() throws IOException {
    File f = new File(tempFolder.getRoot(), "deep/nested/dir/file.txt");
    TextFileUtilities.write(f, "nested content");
    assertTrue(f.exists());
    assertEquals("nested content" + SEP, TextFileUtilities.read(f));
  }

  @Test
  public void write_createsParentDirectories_multiLevel() throws IOException {
    File f = new File(tempFolder.getRoot(), "a/b/c/d/e/output.txt");
    TextFileUtilities.write(f, "deep");
    assertTrue(f.exists());
    assertTrue(f.getParentFile().isDirectory());
  }

  @Test(expected = RuntimeException.class)
  public void read_nonExistentFile_throwsRuntimeException() {
    File f = new File(tempFolder.getRoot(), "nonexistent.txt");
    TextFileUtilities.read(f);
  }

  @Test(expected = RuntimeException.class)
  public void read_nonExistentPath_throwsRuntimeException() {
    TextFileUtilities.read(new File(tempFolder.getRoot(), "nofile.txt").getAbsolutePath());
  }

  // ====== read via String path ======

  @Test
  public void read_byPath() throws IOException {
    File f = tempFolder.newFile("bypath.txt");
    Files.writeString(f.toPath(), "path-content");
    String result = TextFileUtilities.read(f.getAbsolutePath());
    assertEquals("path-content" + SEP, result);
  }

  @Test
  public void read_byPath_multiline() throws IOException {
    File f = tempFolder.newFile("multipath.txt");
    Files.writeString(f.toPath(), "line1\nline2\nline3");
    String result = TextFileUtilities.read(f.getAbsolutePath());
    assertTrue(result.contains("line1"));
    assertTrue(result.contains("line3"));
  }

  // ====== read via InputStream ======

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

  @Test
  public void read_inputStream_singleLine() {
    InputStream is = new ByteArrayInputStream("one line".getBytes(StandardCharsets.UTF_8));
    String result = TextFileUtilities.read(is);
    assertEquals("one line" + SEP, result);
  }

  @Test
  public void read_inputStream_unicode() {
    String content = "\u00e9\u00e8\u00ea café";
    InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    String result = TextFileUtilities.read(is);
    assertEquals(content + SEP, result);
  }

  @Test
  public void read_inputStream_manyLines() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 50; i++) {
      sb.append("line").append(i).append("\n");
    }
    InputStream is = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
    String result = TextFileUtilities.read(is);
    assertTrue(result.contains("line0"));
    assertTrue(result.contains("line49"));
  }

  @Test
  public void read_inputStream_specialChars() {
    String content = "<tag attr=\"val\">&amp;</tag>";
    InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    String result = TextFileUtilities.read(is);
    assertEquals(content + SEP, result);
  }

  @Test
  public void read_inputStream_tabs() {
    String content = "col1\tcol2\tcol3";
    InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    assertEquals(content + SEP, TextFileUtilities.read(is));
  }

  // ====== read via Reader ======

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

  @Test
  public void read_reader_multiline() {
    StringReader reader = new StringReader("a\nb\nc");
    String result = TextFileUtilities.read(reader);
    assertEquals("a" + SEP + "b" + SEP + "c" + SEP, result);
  }

  @Test
  public void read_reader_singleNewline() {
    StringReader reader = new StringReader("\n");
    String result = TextFileUtilities.read(reader);
    assertEquals(SEP, result);
  }

  @Test
  public void read_reader_onlyWhitespace() {
    StringReader reader = new StringReader("   ");
    String result = TextFileUtilities.read(reader);
    assertEquals("   " + SEP, result);
  }

  @Test
  public void read_reader_longContent() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 5000; i++) {
      sb.append("x");
    }
    StringReader reader = new StringReader(sb.toString());
    String result = TextFileUtilities.read(reader);
    assertEquals(sb.toString() + SEP, result);
  }

  // ====== write via Writer ======

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

  @Test
  public void write_writer_multiline() {
    StringWriter writer = new StringWriter();
    TextFileUtilities.write(writer, "line1\nline2\nline3");
    assertEquals("line1\nline2\nline3", writer.toString());
  }

  @Test
  public void write_writer_specialChars() {
    StringWriter writer = new StringWriter();
    TextFileUtilities.write(writer, "<>&\"'");
    assertEquals("<>&\"'", writer.toString());
  }

  @Test
  public void write_writer_unicode() {
    StringWriter writer = new StringWriter();
    TextFileUtilities.write(writer, "\u2603\u00e9\u00e8");
    assertEquals("\u2603\u00e9\u00e8", writer.toString());
  }

  @Test
  public void write_writer_longContent() {
    StringWriter writer = new StringWriter();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      sb.append("Z");
    }
    TextFileUtilities.write(writer, sb.toString());
    assertEquals(sb.toString(), writer.toString());
  }

  // ====== Large content ======

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

  @Test
  public void readWrite_manyLines() throws IOException {
    File f = tempFolder.newFile("manylines.txt");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      sb.append("Line ").append(i).append("\n");
    }
    TextFileUtilities.write(f, sb.toString());
    String result = TextFileUtilities.read(f);
    assertTrue(result.contains("Line 0"));
    assertTrue(result.contains("Line 999"));
  }

  // ====== Round-trip consistency ======

  @Test
  public void roundTrip_file_thenPath_sameResult() throws IOException {
    File f = tempFolder.newFile("roundtrip2.txt");
    String content = "consistency check";
    TextFileUtilities.write(f, content);
    String byFile = TextFileUtilities.read(f);
    String byPath = TextFileUtilities.read(f.getAbsolutePath());
    assertEquals(byFile, byPath);
  }

  @Test
  public void roundTrip_file_thenInputStream_sameContent() throws IOException {
    File f = tempFolder.newFile("roundtrip3.txt");
    String content = "stream comparison";
    TextFileUtilities.write(f, content);
    String byFile = TextFileUtilities.read(f);
    InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    String byStream = TextFileUtilities.read(is);
    assertEquals(byFile, byStream);
  }

  @Test
  public void readWrite_windowsLineEndings() throws IOException {
    File f = tempFolder.newFile("crlf.txt");
    String content = "line1\r\nline2\r\nline3";
    TextFileUtilities.write(f, content);
    String result = TextFileUtilities.read(f);
    // readLine strips \r\n, then appends system separator
    assertTrue(result.contains("line1"));
    assertTrue(result.contains("line2"));
    assertTrue(result.contains("line3"));
  }

  @Test
  public void readWrite_mixedLineEndings() throws IOException {
    File f = tempFolder.newFile("mixed.txt");
    String content = "unix\nwindows\r\nold-mac\r";
    TextFileUtilities.write(f, content);
    String result = TextFileUtilities.read(f);
    assertTrue(result.contains("unix"));
    assertTrue(result.contains("windows"));
  }

  // ====== Edge cases ======

  @Test
  public void write_nullContent_writesNull() throws IOException {
    // Java's Writer.write(null) throws NPE, which becomes RuntimeException
    File f = tempFolder.newFile("nullcontent.txt");
    try {
      TextFileUtilities.write(f, null);
      fail("Expected RuntimeException for null content");
    } catch (RuntimeException e) {
      // Expected — NullPointerException is a RuntimeException subclass
    }
  }

  @Test
  public void readWrite_onlyNewlines() throws IOException {
    File f = tempFolder.newFile("newlines.txt");
    TextFileUtilities.write(f, "\n\n\n");
    String result = TextFileUtilities.read(f);
    assertNotNull(result);
    // Three newlines produce three empty lines with separators
    assertTrue(result.length() > 0);
  }

  @Test
  public void readWrite_trailingNewline() throws IOException {
    File f = tempFolder.newFile("trailing.txt");
    TextFileUtilities.write(f, "content\n");
    String result = TextFileUtilities.read(f);
    assertTrue(result.startsWith("content"));
  }

  @Test
  public void readWrite_leadingNewline() throws IOException {
    File f = tempFolder.newFile("leading.txt");
    TextFileUtilities.write(f, "\ncontent");
    String result = TextFileUtilities.read(f);
    assertTrue(result.contains("content"));
  }

  @Test
  public void readWrite_whitespaceOnly() throws IOException {
    File f = tempFolder.newFile("whitespace.txt");
    TextFileUtilities.write(f, "   ");
    String result = TextFileUtilities.read(f);
    assertEquals("   " + SEP, result);
  }

  // ====== Additional write/read edge cases ======

  @Test
  public void readWrite_file_allPrintableAscii() throws IOException {
    File f = tempFolder.newFile("printable.txt");
    StringBuilder sb = new StringBuilder();
    for (char c = 32; c < 127; c++) {
      sb.append(c);
    }
    String content = sb.toString();
    TextFileUtilities.write(f, content);
    assertEquals(content + SEP, TextFileUtilities.read(f));
  }

  @Test
  public void readWrite_file_escapedChars() throws IOException {
    File f = tempFolder.newFile("escaped.txt");
    String content = "line1\\nstill-line1\\ttab";
    TextFileUtilities.write(f, content);
    assertEquals(content + SEP, TextFileUtilities.read(f));
  }

  @Test
  public void readWrite_file_japaneseChars() throws IOException {
    File f = tempFolder.newFile("japanese.txt");
    String content = "\u3053\u3093\u306b\u3061\u306f\u4e16\u754c";
    TextFileUtilities.write(f, content);
    String result = TextFileUtilities.read(f);
    assertEquals(content + SEP, result);
  }

  @Test
  public void readWrite_file_arabicChars() throws IOException {
    File f = tempFolder.newFile("arabic.txt");
    String content = "\u0645\u0631\u062d\u0628\u0627";
    TextFileUtilities.write(f, content);
    assertEquals(content + SEP, TextFileUtilities.read(f));
  }

  @Test
  public void readWrite_file_emojiContent() throws IOException {
    File f = tempFolder.newFile("emoji.txt");
    String content = "\uD83D\uDE00 \uD83D\uDE80 \uD83C\uDF1F";
    TextFileUtilities.write(f, content);
    String result = TextFileUtilities.read(f);
    assertEquals(content + SEP, result);
  }

  @Test
  public void readWrite_file_mixedUnicodeAndAscii() throws IOException {
    File f = tempFolder.newFile("mixed-unicode.txt");
    String content = "Hello \u00e9\u00e8\u00ea World \u2603";
    TextFileUtilities.write(f, content);
    assertEquals(content + SEP, TextFileUtilities.read(f));
  }

  @Test
  public void readWrite_file_manyShortLines() throws IOException {
    File f = tempFolder.newFile("shortlines.txt");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 500; i++) {
      sb.append("L").append(i).append("\n");
    }
    TextFileUtilities.write(f, sb.toString());
    String result = TextFileUtilities.read(f);
    assertTrue(result.contains("L0"));
    assertTrue(result.contains("L499"));
  }

  @Test
  public void readWrite_file_fewVeryLongLines() throws IOException {
    File f = tempFolder.newFile("longlines.txt");
    StringBuilder sb = new StringBuilder();
    for (int line = 0; line < 5; line++) {
      for (int i = 0; i < 5000; i++) {
        sb.append((char) ('A' + (i % 26)));
      }
      sb.append("\n");
    }
    TextFileUtilities.write(f, sb.toString());
    String result = TextFileUtilities.read(f);
    assertNotNull(result);
    assertTrue(result.length() > 25000);
  }

  @Test
  public void readWrite_file_carriageReturnOnly() throws IOException {
    File f = tempFolder.newFile("cr.txt");
    String content = "line1\rline2\rline3";
    TextFileUtilities.write(f, content);
    String result = TextFileUtilities.read(f);
    assertTrue(result.contains("line1"));
    assertTrue(result.contains("line3"));
  }

  @Test
  public void write_multipleOverwrites_lastWins() throws IOException {
    File f = tempFolder.newFile("multiwrite.txt");
    TextFileUtilities.write(f, "first");
    TextFileUtilities.write(f, "second");
    TextFileUtilities.write(f, "third");
    assertEquals("third" + SEP, TextFileUtilities.read(f));
  }

  // ====== Additional InputStream read tests ======

  @Test
  public void read_inputStream_binaryLikeText() {
    String content = "\u0000\u0001\u0002";
    InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    String result = TextFileUtilities.read(is);
    assertNotNull(result);
  }

  @Test
  public void read_inputStream_longLine() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 10000; i++) sb.append('X');
    InputStream is = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
    String result = TextFileUtilities.read(is);
    assertEquals(sb.toString() + SEP, result);
  }

  @Test
  public void read_inputStream_hundredLines() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 100; i++) sb.append("Line ").append(i).append("\n");
    InputStream is = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
    String result = TextFileUtilities.read(is);
    assertTrue(result.contains("Line 0"));
    assertTrue(result.contains("Line 99"));
  }

  @Test
  public void read_inputStream_allWhitespace() {
    InputStream is = new ByteArrayInputStream("   \t\t   ".getBytes(StandardCharsets.UTF_8));
    String result = TextFileUtilities.read(is);
    assertEquals("   \t\t   " + SEP, result);
  }

  @Test
  public void read_inputStream_onlyNewlines() {
    InputStream is = new ByteArrayInputStream("\n\n\n".getBytes(StandardCharsets.UTF_8));
    String result = TextFileUtilities.read(is);
    assertNotNull(result);
    assertTrue(result.length() > 0);
  }

  // ====== Additional Reader tests ======

  @Test
  public void read_reader_unicode() {
    StringReader reader = new StringReader("caf\u00e9 \u2603");
    assertEquals("caf\u00e9 \u2603" + SEP, TextFileUtilities.read(reader));
  }

  @Test
  public void read_reader_tabsAndSpaces() {
    StringReader reader = new StringReader("\t \t mixed");
    assertEquals("\t \t mixed" + SEP, TextFileUtilities.read(reader));
  }

  @Test
  public void read_reader_manyLines() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 200; i++) sb.append("r").append(i).append("\n");
    StringReader reader = new StringReader(sb.toString());
    String result = TextFileUtilities.read(reader);
    assertTrue(result.contains("r0"));
    assertTrue(result.contains("r199"));
  }

  @Test
  public void read_reader_singleChar() {
    StringReader reader = new StringReader("X");
    assertEquals("X" + SEP, TextFileUtilities.read(reader));
  }

  @Test
  public void read_reader_carriageReturn() {
    StringReader reader = new StringReader("a\rb");
    String result = TextFileUtilities.read(reader);
    assertTrue(result.contains("a"));
    assertTrue(result.contains("b"));
  }

  // ====== Additional Writer tests ======

  @Test
  public void write_writer_tabs() {
    StringWriter writer = new StringWriter();
    TextFileUtilities.write(writer, "a\tb\tc");
    assertEquals("a\tb\tc", writer.toString());
  }

  @Test
  public void write_writer_newlines() {
    StringWriter writer = new StringWriter();
    TextFileUtilities.write(writer, "\n\n\n");
    assertEquals("\n\n\n", writer.toString());
  }

  @Test
  public void write_writer_longText() {
    StringWriter writer = new StringWriter();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 20000; i++) sb.append('Q');
    TextFileUtilities.write(writer, sb.toString());
    assertEquals(20000, writer.toString().length());
  }

  @Test
  public void write_writer_carriageReturnLineFeed() {
    StringWriter writer = new StringWriter();
    TextFileUtilities.write(writer, "a\r\nb\r\nc");
    assertEquals("a\r\nb\r\nc", writer.toString());
  }

  // ====== Additional File path read tests ======

  @Test
  public void read_byPath_emptyContent() throws IOException {
    File f = tempFolder.newFile("emptypath.txt");
    TextFileUtilities.write(f, "");
    assertEquals("", TextFileUtilities.read(f.getAbsolutePath()));
  }

  @Test
  public void read_byPath_unicode() throws IOException {
    File f = tempFolder.newFile("unicodepath.txt");
    String content = "\u00e9\u00e8\u00ea";
    TextFileUtilities.write(f, content);
    assertEquals(content + SEP, TextFileUtilities.read(f.getAbsolutePath()));
  }

  @Test
  public void read_byPath_tabs() throws IOException {
    File f = tempFolder.newFile("tabpath.txt");
    TextFileUtilities.write(f, "col1\tcol2");
    assertEquals("col1\tcol2" + SEP, TextFileUtilities.read(f.getAbsolutePath()));
  }

  // ====== Parent directory creation tests ======

  @Test
  public void write_createsParentDirectories_singleLevel() throws IOException {
    File f = new File(tempFolder.getRoot(), "newparent/file.txt");
    TextFileUtilities.write(f, "parent test");
    assertTrue(f.exists());
    assertEquals("parent test" + SEP, TextFileUtilities.read(f));
  }

  @Test
  public void write_createsParentDirectories_alreadyExists() throws IOException {
    File dir = tempFolder.newFolder("existing_parent");
    File f = new File(dir, "child.txt");
    TextFileUtilities.write(f, "existing parent");
    assertTrue(f.exists());
    assertEquals("existing parent" + SEP, TextFileUtilities.read(f));
  }

  // ====== Round-trip consistency additional tests ======

  @Test
  public void roundTrip_multipleCycles() throws IOException {
    File f = tempFolder.newFile("cycles.txt");
    String content = "cycle test content";
    for (int i = 0; i < 5; i++) {
      TextFileUtilities.write(f, content);
      assertEquals(content + SEP, TextFileUtilities.read(f));
    }
  }

  @Test
  public void roundTrip_differentContent() throws IOException {
    File f = tempFolder.newFile("diffcontent.txt");
    for (int i = 0; i < 10; i++) {
      String content = "content-" + i;
      TextFileUtilities.write(f, content);
      assertEquals(content + SEP, TextFileUtilities.read(f));
    }
  }

  @Test
  public void roundTrip_increasingLength() throws IOException {
    File f = tempFolder.newFile("increasing.txt");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 50; i++) {
      sb.append("X");
      TextFileUtilities.write(f, sb.toString());
      assertEquals(sb.toString() + SEP, TextFileUtilities.read(f));
    }
  }

  @Test
  public void readWrite_file_xmlLikeContent() throws IOException {
    File f = tempFolder.newFile("xmllike.txt");
    String content = "<root><child attr=\"val\">text &amp; more</child></root>";
    TextFileUtilities.write(f, content);
    assertEquals(content + SEP, TextFileUtilities.read(f));
  }

  @Test
  public void readWrite_file_jsonLikeContent() throws IOException {
    File f = tempFolder.newFile("jsonlike.txt");
    String content = "{\"key\": \"value\", \"array\": [1, 2, 3]}";
    TextFileUtilities.write(f, content);
    assertEquals(content + SEP, TextFileUtilities.read(f));
  }

  @Test
  public void readWrite_file_csvLikeContent() throws IOException {
    File f = tempFolder.newFile("csvlike.txt");
    String content = "name,age,city\nAlice,30,NYC\nBob,25,LA";
    TextFileUtilities.write(f, content);
    String result = TextFileUtilities.read(f);
    assertTrue(result.contains("Alice"));
    assertTrue(result.contains("Bob"));
  }
}
