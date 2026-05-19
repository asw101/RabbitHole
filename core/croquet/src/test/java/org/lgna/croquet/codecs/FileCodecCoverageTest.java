package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;
import org.lgna.croquet.ItemCodec;

import java.io.File;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link FileCodec} — singleton access, getValueClass,
 * null encode/decode round-trip, non-null encode/decode characterization
 * (RuntimeException), appendRepresentation, and class structure.
 */
public class FileCodecCoverageTest {

  // ── Singleton access ───────────────────────────────────────────────

  @Test
  public void singleton_isNotNull() {
    assertNotNull(FileCodec.SINGLETON);
  }

  @Test
  public void singleton_isEnum() {
    assertTrue(FileCodec.SINGLETON instanceof Enum);
  }

  @Test
  public void singleton_identity() {
    assertSame(FileCodec.SINGLETON, FileCodec.valueOf("SINGLETON"));
  }

  @Test
  public void values_hasSingleElement() {
    assertEquals(1, FileCodec.values().length);
  }

  // ── getValueClass ──────────────────────────────────────────────────

  @Test
  public void getValueClass_returnsFile() {
    assertEquals(File.class, FileCodec.SINGLETON.getValueClass());
  }

  // ── null encode/decode round-trip ──────────────────────────────────

  @Test
  public void encodeAndDecode_null_returnsNull() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    FileCodec.SINGLETON.encodeValue(encoder, null);
    BinaryDecoder decoder = encoder.createDecoder();
    assertNull(FileCodec.SINGLETON.decodeValue(decoder));
  }

  @Test
  public void roundTrip_multipleNulls_sequential() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    FileCodec.SINGLETON.encodeValue(encoder, null);
    FileCodec.SINGLETON.encodeValue(encoder, null);
    BinaryDecoder decoder = encoder.createDecoder();
    assertNull(FileCodec.SINGLETON.decodeValue(decoder));
    assertNull(FileCodec.SINGLETON.decodeValue(decoder));
  }

  // ── non-null encode characterization: throws RuntimeException("todo") ─

  @Test
  public void encodeValue_nonNull_throwsRuntimeExceptionWithTodoMessage() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    try {
      FileCodec.SINGLETON.encodeValue(encoder, new File("/tmp/test.txt"));
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("todo", e.getMessage());
    }
  }

  // ── non-null decode characterization: throws RuntimeException("todo") ─

  @Test
  public void decodeValue_nonNull_throwsRuntimeExceptionWithTodoMessage() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encode(true); // write isNotNull = true
    BinaryDecoder decoder = encoder.createDecoder();
    try {
      FileCodec.SINGLETON.decodeValue(decoder);
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("todo", e.getMessage());
    }
  }

  // ── appendRepresentation ──────────────────────────────────────────

  @Test
  public void appendRepresentation_file() {
    StringBuilder sb = new StringBuilder();
    File file = new File("/tmp/test.txt");
    FileCodec.SINGLETON.appendRepresentation(sb, file);
    assertTrue(sb.toString().contains("test.txt"));
  }

  @Test
  public void appendRepresentation_null() {
    StringBuilder sb = new StringBuilder();
    FileCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_appendsToExisting() {
    StringBuilder sb = new StringBuilder("file:");
    FileCodec.SINGLETON.appendRepresentation(sb, new File("data.csv"));
    assertTrue(sb.toString().startsWith("file:"));
    assertTrue(sb.length() > "file:".length());
  }

  @Test
  public void appendRepresentation_directoryPath() {
    StringBuilder sb = new StringBuilder();
    FileCodec.SINGLETON.appendRepresentation(sb, new File("/usr/local/bin"));
    assertFalse(sb.toString().isEmpty());
  }

  // ── class structure ───────────────────────────────────────────────

  @Test
  public void fileCodec_implementsItemCodec() {
    assertTrue(ItemCodec.class.isAssignableFrom(FileCodec.class));
  }

  @Test
  public void fileCodec_isEnum() {
    assertTrue(FileCodec.class.isEnum());
  }

  @Test
  public void fileCodec_isPublic() {
    assertTrue(Modifier.isPublic(FileCodec.class.getModifiers()));
  }

  @Test
  public void fileCodec_isInCodecsPackage() {
    assertEquals("org.lgna.croquet.codecs",
        FileCodec.class.getPackage().getName());
  }
}
