package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;
import org.lgna.croquet.ItemCodec;

import java.awt.Color;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link ColorCodec} — encode/decode round-trip with various
 * Color values, null handling, singleton access, appendRepresentation, and
 * class structure.
 */
public class ColorCodecCoverageTest {

  // ── Singleton access ───────────────────────────────────────────────

  @Test
  public void singleton_isNotNull() {
    assertNotNull(ColorCodec.SINGLETON);
  }

  @Test
  public void singleton_isEnum() {
    assertTrue(ColorCodec.SINGLETON instanceof Enum);
  }

  @Test
  public void singleton_identity() {
    assertSame(ColorCodec.SINGLETON, ColorCodec.valueOf("SINGLETON"));
  }

  @Test
  public void values_hasSingleElement() {
    assertEquals(1, ColorCodec.values().length);
  }

  // ── getValueClass ──────────────────────────────────────────────────

  @Test
  public void getValueClass_returnsColor() {
    assertEquals(Color.class, ColorCodec.SINGLETON.getValueClass());
  }

  // ── encode/decode round-trip ───────────────────────────────────────

  @Test
  public void roundTrip_red() {
    assertColorRoundTrip(Color.RED);
  }

  @Test
  public void roundTrip_green() {
    assertColorRoundTrip(Color.GREEN);
  }

  @Test
  public void roundTrip_blue() {
    assertColorRoundTrip(Color.BLUE);
  }

  @Test
  public void roundTrip_black() {
    assertColorRoundTrip(Color.BLACK);
  }

  @Test
  public void roundTrip_white() {
    assertColorRoundTrip(Color.WHITE);
  }

  @Test
  public void roundTrip_customRGBA() {
    assertColorRoundTrip(new Color(100, 150, 200, 128));
  }

  @Test
  public void roundTrip_fullyTransparent() {
    assertColorRoundTrip(new Color(0, 0, 0, 0));
  }

  @Test
  public void roundTrip_fullyOpaque() {
    assertColorRoundTrip(new Color(255, 255, 255, 255));
  }

  @Test
  public void roundTrip_minRGB() {
    assertColorRoundTrip(new Color(0, 0, 0));
  }

  @Test
  public void roundTrip_maxRGB() {
    assertColorRoundTrip(new Color(255, 255, 255));
  }

  @Test
  public void roundTrip_preservesAlpha() {
    Color original = new Color(10, 20, 30, 40);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    ColorCodec.SINGLETON.encodeValue(encoder, original);
    BinaryDecoder decoder = encoder.createDecoder();
    Color decoded = ColorCodec.SINGLETON.decodeValue(decoder);
    assertEquals(original.getAlpha(), decoded.getAlpha());
  }

  @Test
  public void roundTrip_preservesRed() {
    Color original = new Color(123, 0, 0);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    ColorCodec.SINGLETON.encodeValue(encoder, original);
    BinaryDecoder decoder = encoder.createDecoder();
    Color decoded = ColorCodec.SINGLETON.decodeValue(decoder);
    assertEquals(123, decoded.getRed());
  }

  @Test
  public void roundTrip_preservesGreen() {
    Color original = new Color(0, 200, 0);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    ColorCodec.SINGLETON.encodeValue(encoder, original);
    BinaryDecoder decoder = encoder.createDecoder();
    Color decoded = ColorCodec.SINGLETON.decodeValue(decoder);
    assertEquals(200, decoded.getGreen());
  }

  @Test
  public void roundTrip_preservesBlue() {
    Color original = new Color(0, 0, 77);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    ColorCodec.SINGLETON.encodeValue(encoder, original);
    BinaryDecoder decoder = encoder.createDecoder();
    Color decoded = ColorCodec.SINGLETON.decodeValue(decoder);
    assertEquals(77, decoded.getBlue());
  }

  // ── null handling ──────────────────────────────────────────────────

  @Test
  public void encodeAndDecode_null_returnsNull() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    ColorCodec.SINGLETON.encodeValue(encoder, null);
    BinaryDecoder decoder = encoder.createDecoder();
    assertNull(ColorCodec.SINGLETON.decodeValue(decoder));
  }

  @Test
  public void encode_null_doesNotThrow() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    ColorCodec.SINGLETON.encodeValue(encoder, null);
    // success if no exception
  }

  // ── multiple sequential encode/decode ──────────────────────────────

  @Test
  public void roundTrip_multipleColors_sequential() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    ColorCodec.SINGLETON.encodeValue(encoder, Color.RED);
    ColorCodec.SINGLETON.encodeValue(encoder, null);
    ColorCodec.SINGLETON.encodeValue(encoder, Color.BLUE);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(Color.RED, ColorCodec.SINGLETON.decodeValue(decoder));
    assertNull(ColorCodec.SINGLETON.decodeValue(decoder));
    assertEquals(Color.BLUE, ColorCodec.SINGLETON.decodeValue(decoder));
  }

  // ── appendRepresentation ──────────────────────────────────────────

  @Test
  public void appendRepresentation_nonNullColor() {
    StringBuilder sb = new StringBuilder();
    ColorCodec.SINGLETON.appendRepresentation(sb, Color.RED);
    assertFalse(sb.toString().isEmpty());
  }

  @Test
  public void appendRepresentation_nullColor() {
    StringBuilder sb = new StringBuilder();
    ColorCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_appendsToExisting() {
    StringBuilder sb = new StringBuilder("before:");
    ColorCodec.SINGLETON.appendRepresentation(sb, Color.GREEN);
    assertTrue(sb.toString().startsWith("before:"));
    assertTrue(sb.length() > "before:".length());
  }

  // ── class structure ───────────────────────────────────────────────

  @Test
  public void colorCodec_implementsItemCodec() {
    assertTrue(ItemCodec.class.isAssignableFrom(ColorCodec.class));
  }

  @Test
  public void colorCodec_isEnum() {
    assertTrue(ColorCodec.class.isEnum());
  }

  @Test
  public void colorCodec_isPublic() {
    assertTrue(Modifier.isPublic(ColorCodec.class.getModifiers()));
  }

  @Test
  public void colorCodec_isInCodecsPackage() {
    assertEquals("org.lgna.croquet.codecs",
        ColorCodec.class.getPackage().getName());
  }

  // ── helper ────────────────────────────────────────────────────────

  private void assertColorRoundTrip(Color expected) {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    ColorCodec.SINGLETON.encodeValue(encoder, expected);
    BinaryDecoder decoder = encoder.createDecoder();
    Color actual = ColorCodec.SINGLETON.decodeValue(decoder);
    assertEquals(expected, actual);
  }
}
