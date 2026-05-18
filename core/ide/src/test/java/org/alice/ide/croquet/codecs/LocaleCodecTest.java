package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class LocaleCodecTest {

  @Test
  public void getValueClass_returnsLocaleClass() {
    assertEquals(Locale.class, LocaleCodec.SINGLETON.getValueClass());
  }

  @Test
  public void appendRepresentation_nullLocale_appendsNull() {
    StringBuilder sb = new StringBuilder();

    LocaleCodec.SINGLETON.appendRepresentation(sb, null);

    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_nonNullLocale_appendsDisplayName() {
    StringBuilder sb = new StringBuilder();

    LocaleCodec.SINGLETON.appendRepresentation(sb, Locale.US);

    assertFalse(sb.toString().isEmpty());
  }

  @Test
  public void appendRepresentation_appendsToExistingBuilder() {
    StringBuilder sb = new StringBuilder("locale=");

    LocaleCodec.SINGLETON.appendRepresentation(sb, Locale.ENGLISH);

    assertTrue(sb.toString().startsWith("locale="));
    assertTrue(sb.length() > "locale=".length());
  }

  @Test
  public void encodeDecode_nullLocale_roundTrips() {
    assertNull(roundTrip(null));
  }

  @Test
  public void encodeDecode_localeWithLanguageCountryVariant_roundTrips() {
    Locale locale = Locale.of("en", "US", "POSIX");

    assertEquals(locale, roundTrip(locale));
  }

  private static Locale roundTrip(Locale locale) {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    LocaleCodec.SINGLETON.encodeValue(encoder, locale);
    return LocaleCodec.SINGLETON.decodeValue(encoder.createDecoder());
  }
}
