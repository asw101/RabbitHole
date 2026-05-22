package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;

import java.awt.Color;

import static org.junit.Assert.*;

public class ColorCodecRoundTripBehaviorTest {
  @Test
  public void encodeDecode_nonNullColor_preservesRgbaChannels() {
    Color original = new Color(12, 34, 56, 78);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();

    ColorCodec.SINGLETON.encodeValue(encoder, original);
    BinaryDecoder decoder = encoder.createDecoder();
    Color decoded = ColorCodec.SINGLETON.decodeValue(decoder);

    assertEquals(original.getRed(), decoded.getRed());
    assertEquals(original.getGreen(), decoded.getGreen());
    assertEquals(original.getBlue(), decoded.getBlue());
    assertEquals(original.getAlpha(), decoded.getAlpha());
  }

  @Test
  public void encodeDecode_nullColor_roundTripsToNull() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();

    ColorCodec.SINGLETON.encodeValue(encoder, null);
    BinaryDecoder decoder = encoder.createDecoder();

    assertNull(ColorCodec.SINGLETON.decodeValue(decoder));
  }

  @Test
  public void appendRepresentation_usesColorToString() {
    StringBuilder sb = new StringBuilder();

    ColorCodec.SINGLETON.appendRepresentation(sb, new Color(1, 2, 3, 4));

    assertTrue(sb.toString().contains("java.awt.Color"));
  }
}
