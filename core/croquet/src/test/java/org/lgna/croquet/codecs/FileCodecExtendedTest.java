package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Tests for {@link FileCodec} — getValueClass, null encode/decode,
 * non-null RuntimeException branches, singleton enum, and appendRepresentation.
 */
public class FileCodecExtendedTest {

  private final FileCodec codec = FileCodec.SINGLETON;

  // ── getValueClass ──────────────────────────────────────────────────

  @Test
  public void getValueClass_returnsFileClass() {
    assertEquals(File.class, codec.getValueClass());
  }

  // ── encode + decode: null round-trip ───────────────────────────────

  @Test
  public void encodeNull_decodesNull() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, null);
    BinaryDecoder decoder = encoder.createDecoder();
    File decoded = codec.decodeValue(decoder);
    assertNull(decoded);
  }

  @Test
  public void encodeNullTwice_bothDecodeNull() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, null);
    codec.encodeValue(encoder, null);
    BinaryDecoder decoder = encoder.createDecoder();
    assertNull(codec.decodeValue(decoder));
    assertNull(codec.decodeValue(decoder));
  }

  // ── encode non-null: expected RuntimeException ─────────────────────

  @Test(expected = RuntimeException.class)
  public void encodeNonNull_throwsRuntimeException() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, new File("/tmp/test.txt"));
  }

  @Test(expected = RuntimeException.class)
  public void encodeNonNull_directoryPath_throwsRuntimeException() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, new File("/tmp"));
  }

  // ── decode non-null: expected RuntimeException ─────────────────────
  // We simulate what would happen if a non-null boolean was encoded

  @Test(expected = RuntimeException.class)
  public void decodeNonNull_throwsRuntimeException() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    // Manually encode "true" for the isNotNull boolean
    encoder.encode(true);
    BinaryDecoder decoder = encoder.createDecoder();
    codec.decodeValue(decoder);
  }

  // ── appendRepresentation ───────────────────────────────────────────

  @Test
  public void appendRepresentation_nonNull_appendsFileToString() {
    File f = new File("/home/user/file.txt");
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, f);
    assertEquals(f.toString(), sb.toString());
  }

  @Test
  public void appendRepresentation_null_appendsNull() {
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_appendsToExistingContent() {
    StringBuilder sb = new StringBuilder("file=");
    File f = new File("test.txt");
    codec.appendRepresentation(sb, f);
    assertTrue(sb.toString().startsWith("file="));
    assertTrue(sb.toString().contains("test.txt"));
  }

  @Test
  public void appendRepresentation_emptyPath() {
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, new File(""));
    // File("").toString() is empty string, so sb remains empty
    assertEquals("", sb.toString());
  }

  // ── singleton identity ─────────────────────────────────────────────

  @Test
  public void singleton_isSameInstance() {
    assertSame(FileCodec.SINGLETON, FileCodec.SINGLETON);
  }

  @Test
  public void singleton_onlyOneEnumConstant() {
    assertEquals(1, FileCodec.values().length);
  }

  @Test
  public void valueOf_SINGLETON_returnsSingleton() {
    assertSame(FileCodec.SINGLETON, FileCodec.valueOf("SINGLETON"));
  }

  // ── null encoding is deterministic ─────────────────────────────────

  @Test
  public void nullEncoding_isDeterministic_decodesBothNull() {
    ByteArrayBinaryEncoder enc = new ByteArrayBinaryEncoder();
    codec.encodeValue(enc, null);
    codec.encodeValue(enc, null);

    BinaryDecoder dec = enc.createDecoder();
    assertNull(codec.decodeValue(dec));
    assertNull(codec.decodeValue(dec));
  }

  // ── RuntimeException message ───────────────────────────────────────

  @Test
  public void encodeNonNull_exceptionMessage_containsTodo() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    try {
      codec.encodeValue(encoder, new File("x"));
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("todo", e.getMessage());
    }
  }

  @Test
  public void decodeNonNull_exceptionMessage_containsTodo() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encode(true);
    BinaryDecoder decoder = encoder.createDecoder();
    try {
      codec.decodeValue(decoder);
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("todo", e.getMessage());
    }
  }
}
