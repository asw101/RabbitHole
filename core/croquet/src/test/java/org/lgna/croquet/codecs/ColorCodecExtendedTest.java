package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;

import java.awt.Color;

import static org.junit.Assert.*;

/**
 * Extended tests for {@link ColorCodec} — round-trip encode/decode, null handling,
 * alpha channel, boundary values, and appendRepresentation.
 */
public class ColorCodecExtendedTest {

  private final ColorCodec codec = ColorCodec.SINGLETON;

  // ── getValueClass ──────────────────────────────────────────────────

  @Test
  public void getValueClass_returnsColorClass() {
    assertEquals(Color.class, codec.getValueClass());
  }

  // ── encode + decode: basic colors ──────────────────────────────────

  @Test
  public void roundTrip_red() {
    assertRoundTrip(Color.RED);
  }

  @Test
  public void roundTrip_green() {
    assertRoundTrip(Color.GREEN);
  }

  @Test
  public void roundTrip_blue() {
    assertRoundTrip(Color.BLUE);
  }

  @Test
  public void roundTrip_white() {
    assertRoundTrip(Color.WHITE);
  }

  @Test
  public void roundTrip_black() {
    assertRoundTrip(Color.BLACK);
  }

  // ── encode + decode: alpha channel ─────────────────────────────────

  @Test
  public void roundTrip_semiTransparent() {
    assertRoundTrip(new Color(128, 64, 32, 100));
  }

  @Test
  public void roundTrip_fullyTransparent() {
    assertRoundTrip(new Color(0, 0, 0, 0));
  }

  @Test
  public void roundTrip_fullyOpaque() {
    assertRoundTrip(new Color(255, 255, 255, 255));
  }

  // ── encode + decode: boundary channel values ───────────────────────

  @Test
  public void roundTrip_minValues() {
    assertRoundTrip(new Color(0, 0, 0, 0));
  }

  @Test
  public void roundTrip_maxValues() {
    assertRoundTrip(new Color(255, 255, 255, 255));
  }

  @Test
  public void roundTrip_mixedBoundary() {
    assertRoundTrip(new Color(0, 255, 0, 128));
  }

  @Test
  public void roundTrip_preservesAllFourChannels() {
    Color original = new Color(10, 20, 30, 40);
    Color decoded = encodeDecode(original);
    assertEquals(10, decoded.getRed());
    assertEquals(20, decoded.getGreen());
    assertEquals(30, decoded.getBlue());
    assertEquals(40, decoded.getAlpha());
  }

  // ── encode + decode: null ──────────────────────────────────────────

  @Test
  public void roundTrip_null_returnsNull() {
    Color decoded = encodeDecode(null);
    assertNull(decoded);
  }

  @Test
  public void encodeNull_thenNonNull_bothDecodable() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, null);
    codec.encodeValue(encoder, Color.CYAN);

    BinaryDecoder decoder = encoder.createDecoder();
    assertNull(codec.decodeValue(decoder));
    assertEquals(Color.CYAN, codec.decodeValue(decoder));
  }

  // ── consecutive encode/decode ──────────────────────────────────────

  @Test
  public void multipleColors_encodeDecodeInSequence() {
    Color[] colors = {Color.RED, null, Color.GREEN, Color.BLUE, null, new Color(1, 2, 3, 4)};
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    for (Color c : colors) {
      codec.encodeValue(encoder, c);
    }
    BinaryDecoder decoder = encoder.createDecoder();
    for (Color expected : colors) {
      Color decoded = codec.decodeValue(decoder);
      assertEquals(expected, decoded);
    }
  }

  // ── appendRepresentation ───────────────────────────────────────────

  @Test
  public void appendRepresentation_nonNull_appendsToStringOfColor() {
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, Color.RED);
    assertTrue(sb.length() > 0);
    assertEquals(Color.RED.toString(), sb.toString());
  }

  @Test
  public void appendRepresentation_null_appendsNullString() {
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_appendsToExistingContent() {
    StringBuilder sb = new StringBuilder("prefix:");
    codec.appendRepresentation(sb, Color.BLUE);
    assertTrue(sb.toString().startsWith("prefix:"));
    assertTrue(sb.toString().length() > "prefix:".length());
  }

  // ── singleton identity ─────────────────────────────────────────────

  @Test
  public void singleton_isSameInstance() {
    assertSame(ColorCodec.SINGLETON, ColorCodec.SINGLETON);
  }

  @Test
  public void singleton_onlyOneEnumConstant() {
    assertEquals(1, ColorCodec.values().length);
  }

  @Test
  public void valueOf_SINGLETON_returnsSingleton() {
    assertSame(ColorCodec.SINGLETON, ColorCodec.valueOf("SINGLETON"));
  }

  // ── deterministic encoding ─────────────────────────────────────────

  @Test
  public void sameColor_encodesTwice_decodesBothIdentically() {
    Color c = new Color(42, 84, 126, 200);
    ByteArrayBinaryEncoder enc = new ByteArrayBinaryEncoder();
    codec.encodeValue(enc, c);
    codec.encodeValue(enc, c);

    BinaryDecoder dec = enc.createDecoder();
    Color d1 = codec.decodeValue(dec);
    Color d2 = codec.decodeValue(dec);
    assertEquals(d1, d2);
    assertEquals(c, d1);
  }

  @Test
  public void differentColors_decodeToDifferentValues() {
    ByteArrayBinaryEncoder enc = new ByteArrayBinaryEncoder();
    codec.encodeValue(enc, Color.RED);
    codec.encodeValue(enc, Color.BLUE);

    BinaryDecoder dec = enc.createDecoder();
    Color d1 = codec.decodeValue(dec);
    Color d2 = codec.decodeValue(dec);
    assertNotEquals(d1, d2);
  }

  @Test
  public void nullThenNonNull_decodeBothCorrectly() {
    ByteArrayBinaryEncoder enc = new ByteArrayBinaryEncoder();
    codec.encodeValue(enc, null);
    codec.encodeValue(enc, Color.MAGENTA);

    BinaryDecoder dec = enc.createDecoder();
    assertNull(codec.decodeValue(dec));
    assertEquals(Color.MAGENTA, codec.decodeValue(dec));
  }

  // ── various custom colors ──────────────────────────────────────────

  @Test
  public void roundTrip_grayWithAlpha() {
    assertRoundTrip(new Color(128, 128, 128, 200));
  }

  @Test
  public void roundTrip_orangeWithFullAlpha() {
    assertRoundTrip(new Color(255, 165, 0, 255));
  }

  @Test
  public void roundTrip_pinkLowAlpha() {
    assertRoundTrip(new Color(255, 192, 203, 10));
  }

  @Test
  public void roundTrip_unitAlpha() {
    assertRoundTrip(new Color(100, 100, 100, 1));
  }

  @Test
  public void roundTrip_nearMaxAlpha() {
    assertRoundTrip(new Color(100, 100, 100, 254));
  }

  // ── helpers ────────────────────────────────────────────────────────

  private void assertRoundTrip(Color original) {
    Color decoded = encodeDecode(original);
    assertEquals(original, decoded);
  }

  private Color encodeDecode(Color value) {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, value);
    BinaryDecoder decoder = encoder.createDecoder();
    return codec.decodeValue(decoder);
  }
}
