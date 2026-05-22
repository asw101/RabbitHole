package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;
import org.lgna.croquet.codecs.testenums.LocalizedFruit;

import java.util.Locale;

import static org.junit.Assert.*;

public class EnumCodecLocalizationRoundTripTest {
  @Test
  public void appendRepresentation_usesLocalizedBundleText() {
    EnumCodec<LocalizedFruit> codec = EnumCodec.getInstance(LocalizedFruit.class);
    StringBuilder sb = new StringBuilder();

    codec.appendRepresentation(sb, LocalizedFruit.APPLE);

    assertEquals("Apfel", sb.toString());
  }

  @Test
  public void createInstance_customizerTransformsLocalizedText() {
    EnumCodec<LocalizedFruit> codec = EnumCodec.createInstance(LocalizedFruit.class,
        (text, value) -> text.toUpperCase(Locale.ROOT) + "!");
    StringBuilder sb = new StringBuilder();

    codec.appendRepresentation(sb, LocalizedFruit.PEAR);

    assertEquals("BIRNE!", sb.toString());
  }

  @Test
  public void encodeDecode_roundTripPreservesEnumValue() {
    EnumCodec<LocalizedFruit> codec = EnumCodec.getInstance(LocalizedFruit.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();

    codec.encodeValue(encoder, LocalizedFruit.PEAR);
    BinaryDecoder decoder = encoder.createDecoder();

    assertSame(LocalizedFruit.PEAR, codec.decodeValue(decoder));
  }
}
