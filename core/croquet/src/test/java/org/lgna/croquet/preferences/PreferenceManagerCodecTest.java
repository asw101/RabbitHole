package org.lgna.croquet.preferences;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.lgna.croquet.ItemCodec;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests for the encode/decode helper methods in {@link PreferenceManager}.
 * These are private static methods tested via replicated logic (same algorithm)
 * to exercise the codec round-trip paths that PreferenceManager uses.
 */
public class PreferenceManagerCodecTest {

  // ── encodeItem / decodeItem round-trip ─────────────────────────────

  @Test
  public void encodeDecodeItem_string_roundTrip() {
    StringCodec codec = new StringCodec();
    String original = "hello world";
    byte[] encoded = encodeItem(original, codec);
    assertNotNull(encoded);
    assertTrue(encoded.length > 0);
    String decoded = decodeItem(encoded, codec);
    assertEquals(original, decoded);
  }

  @Test
  public void encodeDecodeItem_emptyString_roundTrip() {
    StringCodec codec = new StringCodec();
    byte[] encoded = encodeItem("", codec);
    String decoded = decodeItem(encoded, codec);
    assertEquals("", decoded);
  }

  @Test
  public void encodeDecodeItem_nullValue_roundTrip() {
    StringCodec codec = new StringCodec();
    byte[] encoded = encodeItem(null, codec);
    String decoded = decodeItem(encoded, codec);
    assertNull(decoded);
  }

  @Test
  public void encodeDecodeItem_unicode_roundTrip() {
    StringCodec codec = new StringCodec();
    String original = "日本語テスト 🎉";
    byte[] encoded = encodeItem(original, codec);
    String decoded = decodeItem(encoded, codec);
    assertEquals(original, decoded);
  }

  @Test
  public void encodeDecodeItem_longString_roundTrip() {
    StringCodec codec = new StringCodec();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      sb.append("abcdefghij");
    }
    String original = sb.toString();
    byte[] encoded = encodeItem(original, codec);
    String decoded = decodeItem(encoded, codec);
    assertEquals(original, decoded);
  }

  // ── encodeArray / decodeArray round-trip ───────────────────────────

  @Test
  public void encodeDecodeArray_nonNull_roundTrip() {
    StringCodec codec = new StringCodec();
    String[] original = {"alpha", "beta", "gamma"};
    byte[] encoded = encodeArray(original, codec);
    assertNotNull(encoded);
    String[] decoded = decodeArray(encoded, codec);
    assertArrayEquals(original, decoded);
  }

  @Test
  public void encodeDecodeArray_emptyArray_roundTrip() {
    StringCodec codec = new StringCodec();
    String[] original = {};
    byte[] encoded = encodeArray(original, codec);
    String[] decoded = decodeArray(encoded, codec);
    assertNotNull(decoded);
    assertEquals(0, decoded.length);
  }

  @Test
  public void encodeDecodeArray_singleElement_roundTrip() {
    StringCodec codec = new StringCodec();
    String[] original = {"only"};
    byte[] encoded = encodeArray(original, codec);
    String[] decoded = decodeArray(encoded, codec);
    assertArrayEquals(original, decoded);
  }

  @Test
  public void encodeDecodeArray_nullArray_roundTrip() {
    StringCodec codec = new StringCodec();
    byte[] encoded = encodeArray(null, codec);
    String[] decoded = decodeArray(encoded, codec);
    assertNull(decoded);
  }

  @Test
  public void encodeDecodeArray_withNullElements_roundTrip() {
    StringCodec codec = new StringCodec();
    String[] original = {"a", null, "c"};
    byte[] encoded = encodeArray(original, codec);
    String[] decoded = decodeArray(encoded, codec);
    assertArrayEquals(original, decoded);
  }

  @Test
  public void encodeDecodeArray_largeArray_roundTrip() {
    StringCodec codec = new StringCodec();
    String[] original = new String[100];
    for (int i = 0; i < 100; i++) {
      original[i] = "item-" + i;
    }
    byte[] encoded = encodeArray(original, codec);
    String[] decoded = decodeArray(encoded, codec);
    assertArrayEquals(original, decoded);
  }

  // ── Encoding determinism ──────────────────────────────────────────

  @Test
  public void encodeItem_sameInput_sameOutput() {
    StringCodec codec = new StringCodec();
    byte[] a = encodeItem("test", codec);
    byte[] b = encodeItem("test", codec);
    assertArrayEquals(a, b);
  }

  @Test
  public void encodeArray_sameInput_sameOutput() {
    StringCodec codec = new StringCodec();
    String[] input = {"x", "y"};
    byte[] a = encodeArray(input, codec);
    byte[] b = encodeArray(input, codec);
    assertArrayEquals(a, b);
  }

  @Test
  public void encodeItem_differentInputs_differentOutput() {
    StringCodec codec = new StringCodec();
    byte[] a = encodeItem("foo", codec);
    byte[] b = encodeItem("bar", codec);
    assertFalse(java.util.Arrays.equals(a, b));
  }

  // ── encodeArray isNotNull flag ────────────────────────────────────

  @Test
  public void encodeArray_nullArray_startsWithFalse() {
    StringCodec codec = new StringCodec();
    byte[] encoded = encodeArray(null, codec);
    ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
    BinaryDecoder decoder = new InputStreamBinaryDecoder(bais);
    assertFalse(decoder.decodeBoolean());
  }

  @Test
  public void encodeArray_nonNullArray_startsWithTrue() {
    StringCodec codec = new StringCodec();
    byte[] encoded = encodeArray(new String[]{"a"}, codec);
    ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
    BinaryDecoder decoder = new InputStreamBinaryDecoder(bais);
    assertTrue(decoder.decodeBoolean());
    assertEquals(1, decoder.decodeInt());
  }

  // ── getKey via reflection ─────────────────────────────────────────

  @Test
  public void privateConstructor_throwsAssertionError() {
    try {
      java.lang.reflect.Constructor<PreferenceManager> ctor =
          PreferenceManager.class.getDeclaredConstructor();
      ctor.setAccessible(true);
      ctor.newInstance();
      fail("Should have thrown");
    } catch (java.lang.reflect.InvocationTargetException e) {
      assertTrue(e.getCause() instanceof AssertionError);
    } catch (Exception e) {
      fail("Unexpected exception: " + e);
    }
  }

  // ── getUserPreferences without Application ────────────────────────

  @Test
  public void getUserPreferences_noApplication_returnsNullOrValidNode() {
    // Without an active Application, getUserPreferences may return null
    // or a valid Preferences node depending on the platform's Preferences implementation.
    // On some CI systems, java.util.prefs returns a node even without an Application context.
    java.util.prefs.Preferences prefs = PreferenceManager.getUserPreferences();
    // Just verify no exception is thrown; result is platform-dependent
    if (prefs != null) {
      assertNotNull(prefs.absolutePath());
    }
  }

  @Test
  public void decodeListData_noApplication_returnsDefault() {
    StringCodec codec = new StringCodec();
    String[] defaults = {"default1", "default2"};
    String[] result = PreferenceManager.decodeListData("testKey", codec, defaults);
    assertSame(defaults, result);
  }

  // ── Helper methods replicating PreferenceManager's private logic ──

  private static <T> T decodeItem(byte[] data, ItemCodec<T> codec) {
    ByteArrayInputStream bais = new ByteArrayInputStream(data);
    BinaryDecoder decoder = new InputStreamBinaryDecoder(bais);
    return codec.decodeValue(decoder);
  }

  private static <T> byte[] encodeItem(T value, ItemCodec<T> codec) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, value);
    encoder.flush();
    return baos.toByteArray();
  }

  @SuppressWarnings("unchecked")
  private static <T> T[] decodeArray(byte[] data, ItemCodec<T> codec) {
    ByteArrayInputStream bais = new ByteArrayInputStream(data);
    BinaryDecoder decoder = new InputStreamBinaryDecoder(bais);
    boolean isNotNull = decoder.decodeBoolean();
    if (isNotNull) {
      final int N = decoder.decodeInt();
      Class<T> componentType = codec.getValueClass();
      T[] rv = (T[]) Array.newInstance(componentType, N);
      for (int i = 0; i < rv.length; i++) {
        rv[i] = codec.decodeValue(decoder);
      }
      return rv;
    } else {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> byte[] encodeArray(T[] value, ItemCodec<T> codec) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    boolean isNotNull = value != null;
    encoder.encode(isNotNull);
    if (isNotNull) {
      encoder.encode(value.length);
      for (T element : value) {
        codec.encodeValue(encoder, element);
      }
    }
    encoder.flush();
    return baos.toByteArray();
  }

  // ── StringCodec ───────────────────────────────────────────────────

  private static class StringCodec implements ItemCodec<String> {
    @Override
    public Class<String> getValueClass() {
      return String.class;
    }

    @Override
    public void encodeValue(BinaryEncoder encoder, String value) {
      encoder.encode(value);
    }

    @Override
    public String decodeValue(BinaryDecoder decoder) {
      return decoder.decodeString();
    }

    @Override
    public void appendRepresentation(StringBuilder sb, String value) {
      sb.append(value);
    }
  }
}
