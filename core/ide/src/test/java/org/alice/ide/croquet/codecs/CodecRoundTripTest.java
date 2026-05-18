package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Encode/decode round-trip tests for codec classes that serialize
 * data to/from binary streams — the external I/O boundary.
 */
public class CodecRoundTripTest {

  // ---- StringCodec round-trips ----

  @Test
  public void stringCodec_roundTrip_simpleString() {
    String original = "hello world";
    String decoded = stringRoundTrip(original);
    assertEquals(original, decoded);
  }

  @Test
  public void stringCodec_roundTrip_emptyString() {
    String original = "";
    String decoded = stringRoundTrip(original);
    assertEquals(original, decoded);
  }

  @Test
  public void stringCodec_roundTrip_unicode() {
    String original = "\u00e9\u00e8\u00ea \u4e16\u754c";
    String decoded = stringRoundTrip(original);
    assertEquals(original, decoded);
  }

  @Test
  public void stringCodec_roundTrip_specialChars() {
    String original = "<tag attr=\"val\">&amp;\n\t</tag>";
    String decoded = stringRoundTrip(original);
    assertEquals(original, decoded);
  }

  @Test
  public void stringCodec_roundTrip_longString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      sb.append("abcdefghij");
    }
    String original = sb.toString();
    String decoded = stringRoundTrip(original);
    assertEquals(original, decoded);
  }

  // ---- LocaleCodec round-trips ----

  @Test
  public void localeCodec_roundTrip_usLocale() {
    Locale decoded = localeRoundTrip(Locale.US);
    assertEquals(Locale.US, decoded);
  }

  @Test
  public void localeCodec_roundTrip_frenchLocale() {
    Locale decoded = localeRoundTrip(Locale.FRANCE);
    assertEquals(Locale.FRANCE, decoded);
  }

  @Test
  public void localeCodec_roundTrip_germanLocale() {
    Locale decoded = localeRoundTrip(Locale.GERMANY);
    assertEquals(Locale.GERMANY, decoded);
  }

  @Test
  public void localeCodec_roundTrip_japaneseLocale() {
    Locale decoded = localeRoundTrip(Locale.JAPAN);
    assertEquals(Locale.JAPAN, decoded);
  }

  @Test
  public void localeCodec_roundTrip_englishOnly() {
    Locale original = Locale.ENGLISH;
    Locale decoded = localeRoundTrip(original);
    assertEquals(original.getLanguage(), decoded.getLanguage());
  }

  @Test
  public void localeCodec_roundTrip_nullLocale() {
    Locale decoded = localeRoundTrip(null);
    assertNull(decoded);
  }

  @Test
  public void localeCodec_roundTrip_customLocale() {
    Locale original = Locale.of("es", "MX", "");
    Locale decoded = localeRoundTrip(original);
    assertEquals("es", decoded.getLanguage());
    assertEquals("MX", decoded.getCountry());
  }

  @Test
  public void localeCodec_roundTrip_localeWithVariant() {
    Locale original = Locale.of("no", "NO", "NY");
    Locale decoded = localeRoundTrip(original);
    assertEquals("no", decoded.getLanguage());
    assertEquals("NO", decoded.getCountry());
    assertEquals("NY", decoded.getVariant());
  }

  // ---- StringCodec null handling ----

  @Test
  public void stringCodec_encodeNull_decodesNull() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    StringCodec.SINGLETON.encodeValue(encoder, null);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    String decoded = StringCodec.SINGLETON.decodeValue(decoder);
    assertNull(decoded);
  }

  // ---- multiple sequential round-trips ----

  @Test
  public void stringCodec_multipleRoundTrips_inSequence() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);

    StringCodec.SINGLETON.encodeValue(encoder, "first");
    StringCodec.SINGLETON.encodeValue(encoder, "second");
    StringCodec.SINGLETON.encodeValue(encoder, "third");
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));

    assertEquals("first", StringCodec.SINGLETON.decodeValue(decoder));
    assertEquals("second", StringCodec.SINGLETON.decodeValue(decoder));
    assertEquals("third", StringCodec.SINGLETON.decodeValue(decoder));
  }

  @Test
  public void localeCodec_multipleRoundTrips_inSequence() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);

    LocaleCodec.SINGLETON.encodeValue(encoder, Locale.US);
    LocaleCodec.SINGLETON.encodeValue(encoder, null);
    LocaleCodec.SINGLETON.encodeValue(encoder, Locale.JAPAN);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));

    assertEquals(Locale.US, LocaleCodec.SINGLETON.decodeValue(decoder));
    assertNull(LocaleCodec.SINGLETON.decodeValue(decoder));
    assertEquals(Locale.JAPAN, LocaleCodec.SINGLETON.decodeValue(decoder));
  }

  // ---- helpers ----

  private String stringRoundTrip(String value) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    StringCodec.SINGLETON.encodeValue(encoder, value);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    return StringCodec.SINGLETON.decodeValue(decoder);
  }

  private Locale localeRoundTrip(Locale value) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    LocaleCodec.SINGLETON.encodeValue(encoder, value);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    return LocaleCodec.SINGLETON.decodeValue(decoder);
  }
}
