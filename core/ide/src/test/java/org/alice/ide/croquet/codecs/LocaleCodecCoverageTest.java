package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;

import static org.junit.Assert.*;

public class LocaleCodecCoverageTest {
  @Test
  public void appendRepresentation_usesDisplayNameInOwnLocale() {
    StringBuilder sb = new StringBuilder();
    LocaleCodec.SINGLETON.appendRepresentation(sb, Locale.CANADA_FRENCH);
    assertEquals(Locale.CANADA_FRENCH.getDisplayName(Locale.CANADA_FRENCH), sb.toString());
  }

  @Test
  public void roundTrip_rootLocale_preservesLanguageCountryAndVariant() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    LocaleCodec.SINGLETON.encodeValue(encoder, Locale.ROOT);
    encoder.flush();
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(Locale.ROOT, LocaleCodec.SINGLETON.decodeValue(decoder));
  }

  @Test
  public void getValueClass_returnsLocaleType() {
    assertEquals(Locale.class, LocaleCodec.SINGLETON.getValueClass());
  }
}
