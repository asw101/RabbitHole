package org.lgna.croquet;

import org.junit.Test;
import org.lgna.croquet.codecs.ColorCodec;
import org.lgna.croquet.codecs.DefaultItemCodec;
import org.lgna.croquet.codecs.EnumCodec;
import org.lgna.croquet.codecs.FileCodec;

import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import edu.cmu.cs.dennisc.codec.BinaryDecoder;

import java.awt.Color;
import java.io.File;

import static org.junit.Assert.*;

/**
 * Extended tests for all codec implementations — round-trip encode/decode,
 * appendRepresentation, edge cases, and multi-value sequences.
 */
public class CodecExtendedTest {

  // ── ColorCodec round-trip ─────────────────────────────────────────

  @Test
  public void colorCodec_roundTrip_opaqueRed() {
    Color original = Color.RED;
    Color decoded = roundTripColor(original);
    assertEquals(original, decoded);
  }

  @Test
  public void colorCodec_roundTrip_opaqueBlue() {
    Color original = Color.BLUE;
    assertEquals(original, roundTripColor(original));
  }

  @Test
  public void colorCodec_roundTrip_transparentGreen() {
    Color original = new Color(0, 255, 0, 128);
    Color decoded = roundTripColor(original);
    assertEquals(0, decoded.getRed());
    assertEquals(255, decoded.getGreen());
    assertEquals(0, decoded.getBlue());
    assertEquals(128, decoded.getAlpha());
  }

  @Test
  public void colorCodec_roundTrip_black() {
    assertEquals(Color.BLACK, roundTripColor(Color.BLACK));
  }

  @Test
  public void colorCodec_roundTrip_white() {
    assertEquals(Color.WHITE, roundTripColor(Color.WHITE));
  }

  @Test
  public void colorCodec_roundTrip_fullyTransparent() {
    Color original = new Color(100, 100, 100, 0);
    Color decoded = roundTripColor(original);
    assertEquals(0, decoded.getAlpha());
  }

  @Test
  public void colorCodec_roundTrip_null() {
    assertNull(roundTripColor(null));
  }

  @Test
  public void colorCodec_getValueClass() {
    assertEquals(Color.class, ColorCodec.SINGLETON.getValueClass());
  }

  @Test
  public void colorCodec_appendRepresentation_nonNull() {
    StringBuilder sb = new StringBuilder();
    ColorCodec.SINGLETON.appendRepresentation(sb, Color.RED);
    assertFalse(sb.toString().isEmpty());
  }

  @Test
  public void colorCodec_appendRepresentation_null() {
    StringBuilder sb = new StringBuilder();
    ColorCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void colorCodec_multipleRoundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    Color c1 = Color.RED;
    Color c2 = Color.GREEN;
    Color c3 = null;
    ColorCodec.SINGLETON.encodeValue(encoder, c1);
    ColorCodec.SINGLETON.encodeValue(encoder, c2);
    ColorCodec.SINGLETON.encodeValue(encoder, c3);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(c1, ColorCodec.SINGLETON.decodeValue(decoder));
    assertEquals(c2, ColorCodec.SINGLETON.decodeValue(decoder));
    assertNull(ColorCodec.SINGLETON.decodeValue(decoder));
  }

  // ── FileCodec round-trip ──────────────────────────────────────────

  @Test
  public void fileCodec_roundTrip_nonNull_throwsTodo() {
    // FileCodec.encodeValue throws RuntimeException("todo") for non-null files
    try {
      roundTripFile(new File("/tmp/test.txt"));
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("todo", e.getMessage());
    }
  }

  @Test
  public void fileCodec_roundTrip_null() {
    assertNull(roundTripFile(null));
  }

  @Test
  public void fileCodec_roundTrip_directory() {
    // FileCodec.encodeValue throws RuntimeException("todo") for non-null.
    // Verify the exception behavior
    try {
      roundTripFile(new File("/home/user/docs"));
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("todo", e.getMessage());
    }
  }

  @Test
  public void fileCodec_roundTrip_relativePath() {
    // FileCodec.encodeValue throws RuntimeException("todo") for non-null.
    try {
      roundTripFile(new File("relative/path.txt"));
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("todo", e.getMessage());
    }
  }

  @Test
  public void fileCodec_getValueClass() {
    assertEquals(File.class, FileCodec.SINGLETON.getValueClass());
  }

  @Test
  public void fileCodec_appendRepresentation_nonNull() {
    StringBuilder sb = new StringBuilder();
    FileCodec.SINGLETON.appendRepresentation(sb, new File("/tmp/x.txt"));
    assertTrue(sb.toString().contains("x.txt"));
  }

  @Test
  public void fileCodec_appendRepresentation_null() {
    StringBuilder sb = new StringBuilder();
    FileCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  // ── EnumCodec ─────────────────────────────────────────────────────

  private enum TestEnum { ALPHA, BRAVO, CHARLIE }

  @Test
  public void enumCodec_roundTrip_alpha() {
    EnumCodec<TestEnum> codec = EnumCodec.getInstance(TestEnum.class);
    ByteArrayBinaryEncoder enc = new ByteArrayBinaryEncoder();
    codec.encodeValue(enc, TestEnum.ALPHA);
    BinaryDecoder dec = enc.createDecoder();
    assertEquals(TestEnum.ALPHA, codec.decodeValue(dec));
  }

  @Test
  public void enumCodec_roundTrip_bravo() {
    EnumCodec<TestEnum> codec = EnumCodec.getInstance(TestEnum.class);
    ByteArrayBinaryEncoder enc = new ByteArrayBinaryEncoder();
    codec.encodeValue(enc, TestEnum.BRAVO);
    BinaryDecoder dec = enc.createDecoder();
    assertEquals(TestEnum.BRAVO, codec.decodeValue(dec));
  }

  @Test
  public void enumCodec_roundTrip_charlie() {
    EnumCodec<TestEnum> codec = EnumCodec.getInstance(TestEnum.class);
    ByteArrayBinaryEncoder enc = new ByteArrayBinaryEncoder();
    codec.encodeValue(enc, TestEnum.CHARLIE);
    BinaryDecoder dec = enc.createDecoder();
    assertEquals(TestEnum.CHARLIE, codec.decodeValue(dec));
  }

  @Test
  public void enumCodec_multipleRoundTrips() {
    EnumCodec<TestEnum> codec = EnumCodec.getInstance(TestEnum.class);
    ByteArrayBinaryEncoder enc = new ByteArrayBinaryEncoder();
    codec.encodeValue(enc, TestEnum.ALPHA);
    codec.encodeValue(enc, TestEnum.CHARLIE);
    BinaryDecoder dec = enc.createDecoder();
    assertEquals(TestEnum.ALPHA, codec.decodeValue(dec));
    assertEquals(TestEnum.CHARLIE, codec.decodeValue(dec));
  }

  @Test
  public void enumCodec_getValueClass() {
    EnumCodec<TestEnum> codec = EnumCodec.getInstance(TestEnum.class);
    assertEquals(TestEnum.class, codec.getValueClass());
  }

  @Test
  public void enumCodec_appendRepresentation_nonNull() {
    EnumCodec<TestEnum> codec = EnumCodec.createInstance(TestEnum.class, null);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, TestEnum.BRAVO);
    assertEquals("BRAVO", sb.toString());
  }

  @Test
  public void enumCodec_appendRepresentation_null() {
    EnumCodec<TestEnum> codec = EnumCodec.createInstance(TestEnum.class, null);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void enumCodec_getInstance_sameInstance() {
    assertSame(EnumCodec.getInstance(TestEnum.class), EnumCodec.getInstance(TestEnum.class));
  }

  @Test
  public void enumCodec_toString_containsEnumName() {
    EnumCodec<TestEnum> codec = EnumCodec.getInstance(TestEnum.class);
    assertTrue(codec.toString().contains("TestEnum"));
  }

  // ── DefaultItemCodec ──────────────────────────────────────────────

  @Test
  public void defaultItemCodec_getValueClass_string() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    assertEquals(String.class, codec.getValueClass());
  }

  @Test
  public void defaultItemCodec_getValueClass_integer() {
    DefaultItemCodec<Integer> codec = DefaultItemCodec.createInstance(Integer.class);
    assertEquals(Integer.class, codec.getValueClass());
  }

  @Test
  public void defaultItemCodec_appendRepresentation_string() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, "test");
    assertEquals("test", sb.toString());
  }

  @Test
  public void defaultItemCodec_appendRepresentation_integer() {
    DefaultItemCodec<Integer> codec = DefaultItemCodec.createInstance(Integer.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, 42);
    assertEquals("42", sb.toString());
  }

  @Test(expected = RuntimeException.class)
  public void defaultItemCodec_decodeValue_throws() {
    DefaultItemCodec.createInstance(String.class).decodeValue(null);
  }

  @Test(expected = RuntimeException.class)
  public void defaultItemCodec_encodeValue_throws() {
    DefaultItemCodec.createInstance(String.class).encodeValue(null, "test");
  }

  // ── CroquetTestUtils.STRING_CODEC ─────────────────────────────────

  @Test
  public void stringCodec_roundTrip() {
    ByteArrayBinaryEncoder enc = new ByteArrayBinaryEncoder();
    CroquetTestUtils.STRING_CODEC.encodeValue(enc, "hello");
    BinaryDecoder dec = enc.createDecoder();
    assertEquals("hello", CroquetTestUtils.STRING_CODEC.decodeValue(dec));
  }

  @Test
  public void stringCodec_roundTrip_empty() {
    ByteArrayBinaryEncoder enc = new ByteArrayBinaryEncoder();
    CroquetTestUtils.STRING_CODEC.encodeValue(enc, "");
    BinaryDecoder dec = enc.createDecoder();
    assertEquals("", CroquetTestUtils.STRING_CODEC.decodeValue(dec));
  }

  @Test
  public void stringCodec_roundTrip_unicode() {
    String unicode = "こんにちは世界";
    ByteArrayBinaryEncoder enc = new ByteArrayBinaryEncoder();
    CroquetTestUtils.STRING_CODEC.encodeValue(enc, unicode);
    BinaryDecoder dec = enc.createDecoder();
    assertEquals(unicode, CroquetTestUtils.STRING_CODEC.decodeValue(dec));
  }

  @Test
  public void stringCodec_roundTrip_longString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      sb.append("x");
    }
    String longStr = sb.toString();
    ByteArrayBinaryEncoder enc = new ByteArrayBinaryEncoder();
    CroquetTestUtils.STRING_CODEC.encodeValue(enc, longStr);
    BinaryDecoder dec = enc.createDecoder();
    assertEquals(longStr, CroquetTestUtils.STRING_CODEC.decodeValue(dec));
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private static Color roundTripColor(Color original) {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    ColorCodec.SINGLETON.encodeValue(encoder, original);
    BinaryDecoder decoder = encoder.createDecoder();
    return ColorCodec.SINGLETON.decodeValue(decoder);
  }

  private static File roundTripFile(File original) {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    FileCodec.SINGLETON.encodeValue(encoder, original);
    BinaryDecoder decoder = encoder.createDecoder();
    return FileCodec.SINGLETON.decodeValue(decoder);
  }
}
