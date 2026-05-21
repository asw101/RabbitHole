package org.lgna.croquet.codecs;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EnumCodecLocalizationTest {
  @Test
  public void appendRepresentation_usesBundleAndCustomizerWhenAvailable() {
    EnumCodec<LocalizedEnum> codec = EnumCodec.createInstance(LocalizedEnum.class, (text, value) -> text + "!" + value.ordinal());
    StringBuilder sb = new StringBuilder();

    codec.appendRepresentation(sb, LocalizedEnum.FIRST);

    assertEquals("Premier!0", sb.toString());
  }

  @Test
  public void appendRepresentation_fallsBackToEnumNameWhenBundleEntryMissing() {
    EnumCodec<LocalizedEnum> codec = EnumCodec.createInstance(LocalizedEnum.class, null);
    StringBuilder sb = new StringBuilder();

    codec.appendRepresentation(sb, LocalizedEnum.THIRD);

    assertEquals("THIRD", sb.toString());
  }
}
