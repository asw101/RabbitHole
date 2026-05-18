package edu.cmu.cs.dennisc.java.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class TextFileUtilitiesTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private static final String SEP = System.getProperty("line.separator");

  private String expectedRead(String... values) {
    StringBuilder builder = new StringBuilder();
    for (String value : values) {
      builder.append(value);
      builder.append(SEP);
    }
    return builder.toString();
  }

  private void writeRaw(File file, String contents) throws Exception {
    if (file.getParentFile() != null) {
      file.getParentFile().mkdirs();
    }
    Files.write(file.toPath(), contents.getBytes(StandardCharsets.UTF_8));
  }

  @Test
  public void fileRoundTripBasicContent() throws Exception {
    File file = temporaryFolder.newFile("basic.txt");
    TextFileUtilities.write(file, "alpha");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("alpha"), value);
  }

  @Test
  public void fileRoundTripEmptyContent() throws Exception {
    File file = temporaryFolder.newFile("empty.txt");
    TextFileUtilities.write(file, "");
    String value = TextFileUtilities.read(file);
    assertEquals("", value);
  }

  @Test
  public void fileRoundTripUnicodeContent() throws Exception {
    File file = temporaryFolder.newFile("unicode.txt");
    TextFileUtilities.write(file, "こんにちは مرحبا 🙂");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("こんにちは مرحبا 🙂"), value);
  }

  @Test
  public void fileRoundTripMultilineContent() throws Exception {
    File file = temporaryFolder.newFile("multiline.txt");
    TextFileUtilities.write(file, "alpha\nbeta\ngamma");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("alpha", "beta", "gamma"), value);
  }

  @Test
  public void fileRoundTripSingleCharacter() throws Exception {
    File file = temporaryFolder.newFile("single.txt");
    TextFileUtilities.write(file, "x");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("x"), value);
  }

  @Test
  public void fileRoundTripTabs() throws Exception {
    File file = temporaryFolder.newFile("tabs.txt");
    TextFileUtilities.write(file, "a\tb\tc");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("a\tb\tc"), value);
  }

  @Test
  public void fileRoundTripSpecialCharacters() throws Exception {
    File file = temporaryFolder.newFile("special.txt");
    TextFileUtilities.write(file, "!@#$%^&*()[]{}<>");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("!@#$%^&*()[]{}<>"), value);
  }

  @Test
  public void fileRoundTripBackslashes() throws Exception {
    File file = temporaryFolder.newFile("backslashes.txt");
    TextFileUtilities.write(file, "C:\\alice\\bin");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("C:\\alice\\bin"), value);
  }

  @Test
  public void fileWriteOverwritesPreviousContent() throws Exception {
    File file = temporaryFolder.newFile("overwrite.txt");
    TextFileUtilities.write(file, "first");
    TextFileUtilities.write(file, "second");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("second"), value);
  }

  @Test
  public void fileRoundTripLongLine() throws Exception {
    File file = temporaryFolder.newFile("long-line.txt");
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 500; i++) {
      builder.append("abc123");
    }
    TextFileUtilities.write(file, builder.toString());
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead(builder.toString()), value);
  }

  @Test
  public void fileRoundTripCsvLikeContent() throws Exception {
    File file = temporaryFolder.newFile("data.csv");
    TextFileUtilities.write(file, "name,age\nAlice,20\nBob,21");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("name,age", "Alice,20", "Bob,21"), value);
  }

  @Test
  public void fileRoundTripJsonLikeContent() throws Exception {
    File file = temporaryFolder.newFile("data.json");
    TextFileUtilities.write(file, "{\n  \"name\": \"Alice\"\n}");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("{", "  \"name\": \"Alice\"", "}"), value);
  }

  @Test
  public void fileRoundTripXmlLikeContent() throws Exception {
    File file = temporaryFolder.newFile("data.xml");
    TextFileUtilities.write(file, "<root>\n  <child/>\n</root>");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("<root>", "  <child/>", "</root>"), value);
  }

  @Test
  public void fileRoundTripJapaneseArabicEmojiCharacters() throws Exception {
    File file = temporaryFolder.newFile("international.txt");
    TextFileUtilities.write(file, "こんにちは\nمرحبا\n🙂");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("こんにちは", "مرحبا", "🙂"), value);
  }

  @Test
  public void fileRoundTripManyLines() throws Exception {
    File file = temporaryFolder.newFile("many-lines.txt");
    TextFileUtilities.write(file, "one\ntwo\nthree\nfour\nfive");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("one", "two", "three", "four", "five"), value);
  }

  @Test
  public void fileRoundTripWhitespaceOnlyContent() throws Exception {
    File file = temporaryFolder.newFile("whitespace.txt");
    TextFileUtilities.write(file, "   ");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("   "), value);
  }

  @Test
  public void fileRoundTripLineSeparatorOnlyContent() throws Exception {
    File file = temporaryFolder.newFile("separator-only.txt");
    TextFileUtilities.write(file, "\n");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead(""), value);
  }

  @Test
  public void readByStringPathMatchesFileRead() throws Exception {
    File file = temporaryFolder.newFile("path.txt");
    TextFileUtilities.write(file, "path-content");
    String value = TextFileUtilities.read(file.getAbsolutePath());
    assertEquals(expectedRead("path-content"), value);
  }

  @Test
  public void readInputStreamBasicContent() {
    ByteArrayInputStream inputStream = new ByteArrayInputStream("alpha".getBytes(StandardCharsets.UTF_8));
    String value = TextFileUtilities.read(inputStream);
    assertEquals(expectedRead("alpha"), value);
  }

  @Test
  public void readInputStreamEmptyContent() {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
    String value = TextFileUtilities.read(inputStream);
    assertEquals("", value);
  }

  @Test
  public void readInputStreamSingleLine() {
    ByteArrayInputStream inputStream = new ByteArrayInputStream("single-line".getBytes(StandardCharsets.UTF_8));
    String value = TextFileUtilities.read(inputStream);
    assertEquals(expectedRead("single-line"), value);
  }

  @Test
  public void readInputStreamUnicodeContent() {
    ByteArrayInputStream inputStream = new ByteArrayInputStream("héllo 世界 🙂".getBytes(StandardCharsets.UTF_8));
    String value = TextFileUtilities.read(inputStream);
    assertEquals(expectedRead("héllo 世界 🙂"), value);
  }

  @Test
  public void readInputStreamManyLines() {
    ByteArrayInputStream inputStream = new ByteArrayInputStream("a\nb\nc\nd".getBytes(StandardCharsets.UTF_8));
    String value = TextFileUtilities.read(inputStream);
    assertEquals(expectedRead("a", "b", "c", "d"), value);
  }

  @Test
  public void readInputStreamSpecialCharacters() {
    ByteArrayInputStream inputStream = new ByteArrayInputStream("!@#\n$%^".getBytes(StandardCharsets.UTF_8));
    String value = TextFileUtilities.read(inputStream);
    assertEquals(expectedRead("!@#", "$%^"), value);
  }

  @Test
  public void readInputStreamTabsAndSpaces() {
    ByteArrayInputStream inputStream = new ByteArrayInputStream("a\tb\n   c".getBytes(StandardCharsets.UTF_8));
    String value = TextFileUtilities.read(inputStream);
    assertEquals(expectedRead("a\tb", "   c"), value);
  }

  @Test
  public void readInputStreamWindowsLineEndings() {
    ByteArrayInputStream inputStream = new ByteArrayInputStream("a\r\nb\r\nc".getBytes(StandardCharsets.UTF_8));
    String value = TextFileUtilities.read(inputStream);
    assertEquals(expectedRead("a", "b", "c"), value);
  }

  @Test
  public void readReaderBasicContent() {
    StringReader reader = new StringReader("alpha");
    String value = TextFileUtilities.read(reader);
    assertEquals(expectedRead("alpha"), value);
  }

  @Test
  public void readReaderEmptyContent() {
    StringReader reader = new StringReader("");
    String value = TextFileUtilities.read(reader);
    assertEquals("", value);
  }

  @Test
  public void readReaderMultilineContent() {
    StringReader reader = new StringReader("alpha\nbeta");
    String value = TextFileUtilities.read(reader);
    assertEquals(expectedRead("alpha", "beta"), value);
  }

  @Test
  public void readReaderNewlineOnly() {
    StringReader reader = new StringReader("\n");
    String value = TextFileUtilities.read(reader);
    assertEquals(expectedRead(""), value);
  }

  @Test
  public void readReaderWhitespaceOnly() {
    StringReader reader = new StringReader("   \n\t");
    String value = TextFileUtilities.read(reader);
    assertEquals(expectedRead("   ", "\t"), value);
  }

  @Test
  public void readReaderMixedLineEndings() {
    StringReader reader = new StringReader("a\r\nb\nc\rd");
    String value = TextFileUtilities.read(reader);
    assertEquals(expectedRead("a", "b", "c", "d"), value);
  }

  @Test
  public void readReaderManyLines() {
    StringReader reader = new StringReader("one\ntwo\nthree\nfour");
    String value = TextFileUtilities.read(reader);
    assertEquals(expectedRead("one", "two", "three", "four"), value);
  }

  @Test
  public void writeWriterBasicContent() {
    StringWriter writer = new StringWriter();
    TextFileUtilities.write(writer, "alpha");
    assertEquals("alpha", writer.toString());
  }

  @Test
  public void writeWriterEmptyContent() {
    StringWriter writer = new StringWriter();
    TextFileUtilities.write(writer, "");
    assertEquals("", writer.toString());
  }

  @Test
  public void writeWriterMultilineContent() {
    StringWriter writer = new StringWriter();
    TextFileUtilities.write(writer, "alpha\nbeta");
    assertEquals("alpha\nbeta", writer.toString());
  }

  @Test
  public void writeWriterSpecialCharacters() {
    StringWriter writer = new StringWriter();
    TextFileUtilities.write(writer, "!@#$%^&*()");
    assertEquals("!@#$%^&*()", writer.toString());
  }

  @Test
  public void writeWriterUnicodeContent() {
    StringWriter writer = new StringWriter();
    TextFileUtilities.write(writer, "こんにちは مرحبا 🙂");
    assertEquals("こんにちは مرحبا 🙂", writer.toString());
  }

  @Test
  public void writeWriterLongContent() {
    StringWriter writer = new StringWriter();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 300; i++) {
      builder.append("xyz");
    }
    TextFileUtilities.write(writer, builder.toString());
    assertEquals(builder.toString(), writer.toString());
  }

  @Test
  public void writeWriterJsonLikeContent() {
    StringWriter writer = new StringWriter();
    TextFileUtilities.write(writer, "{\"enabled\":true}");
    assertEquals("{\"enabled\":true}", writer.toString());
  }

  @Test
  public void writeWriterXmlLikeContent() {
    StringWriter writer = new StringWriter();
    TextFileUtilities.write(writer, "<root><child/></root>");
    assertEquals("<root><child/></root>", writer.toString());
  }

  @Test
  public void writeFileCreatesParentDirectories() throws Exception {
    File file = new File(temporaryFolder.getRoot(), "nested/path/file.txt");
    TextFileUtilities.write(file, "nested");
    assertTrue(file.exists());
    assertEquals(expectedRead("nested"), TextFileUtilities.read(file));
  }

  @Test
  public void readMissingFileThrowsRuntimeException() {
    try {
      TextFileUtilities.read(new File(temporaryFolder.getRoot(), "missing.txt"));
      fail("Expected RuntimeException");
    } catch (RuntimeException runtimeException) {
      assertNotNull(runtimeException.getCause());
    }
  }

  @Test
  public void readMissingPathThrowsRuntimeException() {
    try {
      TextFileUtilities.read(new File(temporaryFolder.getRoot(), "missing-path.txt").getAbsolutePath());
      fail("Expected RuntimeException");
    } catch (RuntimeException runtimeException) {
      assertNotNull(runtimeException.getCause());
    }
  }

  @Test
  public void readWindowsLineEndingsNormalizesToSeparator() throws Exception {
    File file = temporaryFolder.newFile("windows-lines.txt");
    writeRaw(file, "a\r\nb\r\nc");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("a", "b", "c"), value);
  }

  @Test
  public void readMixedLineEndingsNormalizesToSeparator() throws Exception {
    File file = temporaryFolder.newFile("mixed-lines.txt");
    writeRaw(file, "a\r\nb\nc\rd");
    String value = TextFileUtilities.read(file);
    assertEquals(expectedRead("a", "b", "c", "d"), value);
  }

  @Test
  public void readLargeContentPreservesAllLines() throws Exception {
    File file = temporaryFolder.newFile("large.txt");
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 200; i++) {
      builder.append("line-").append(i).append("\n");
    }
    writeRaw(file, builder.toString());
    String value = TextFileUtilities.read(file);
    assertTrue(value.contains("line-0" + SEP));
    assertTrue(value.contains("line-199" + SEP));
  }

  @Test
  public void roundTripConsistencyMatchesNormalizedExpectation() throws Exception {
    File file = temporaryFolder.newFile("consistency.txt");
    String original = "left\nright";
    TextFileUtilities.write(file, original);
    assertEquals(expectedRead("left", "right"), TextFileUtilities.read(file));
  }

  @Test
  public void writeNullContentThrowsNullPointerException() {
    try {
      TextFileUtilities.write(new StringWriter(), null);
      fail("Expected NullPointerException");
    } catch (NullPointerException npe) {
      assertNotNull(npe);
    }
  }

  @Test
  public void writeNullFileContentThrowsNullPointerException() throws Exception {
    File file = temporaryFolder.newFile("null-file.txt");
    try {
      TextFileUtilities.write(file, null);
      fail("Expected NullPointerException");
    } catch (NullPointerException npe) {
      assertNotNull(npe);
    }
  }

  @Test
  public void repeatedWriteReadCyclesRemainConsistent() throws Exception {
    File file = temporaryFolder.newFile("cycles.txt");
    for (int i = 0; i < 5; i++) {
      TextFileUtilities.write(file, "value-" + i);
      assertEquals(expectedRead("value-" + i), TextFileUtilities.read(file));
    }
  }

  @Test
  public void readPathAfterNestedWriteMatchesFileRead() throws Exception {
    File file = new File(temporaryFolder.getRoot(), "deep/nested/path.txt");
    TextFileUtilities.write(file, "path-data");
    assertEquals(expectedRead("path-data"), TextFileUtilities.read(file.getAbsolutePath()));
  }

  @Test
  public void writeReadLongRepeatedPattern() throws Exception {
    File file = temporaryFolder.newFile("pattern.txt");
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      builder.append("ab");
    }
    TextFileUtilities.write(file, builder.toString());
    assertEquals(expectedRead(builder.toString()), TextFileUtilities.read(file));
  }

  @Test
  public void readInputWithTrailingNewlineProducesSingleExpectedSeparator() throws Exception {
    File file = temporaryFolder.newFile("trailing-newline.txt");
    writeRaw(file, "alpha\n");
    assertEquals(expectedRead("alpha"), TextFileUtilities.read(file));
  }

}
