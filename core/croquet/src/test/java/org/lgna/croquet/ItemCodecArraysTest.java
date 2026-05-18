package org.lgna.croquet;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link ItemCodec.Arrays} — encode/decode round-trips for arrays
 * with the static helper methods.
 */
public class ItemCodecArraysTest {

  private static final ItemCodec<String> STRING_CODEC = CroquetTestUtils.STRING_CODEC;

  // ── Encode + Decode non-null array ────────────────────────────────

  @Test
  public void encodeDecodeArray_nonNull_roundTrips() {
    String[] original = {"alpha", "bravo", "charlie"};
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    ItemCodec.Arrays.encodeArray(encoder, STRING_CODEC, original);
    BinaryDecoder decoder = encoder.createDecoder();
    String[] decoded = ItemCodec.Arrays.decodeArray(decoder, STRING_CODEC);
    assertArrayEquals(original, decoded);
  }

  @Test
  public void encodeDecodeArray_emptyArray_roundTrips() {
    String[] original = {};
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    ItemCodec.Arrays.encodeArray(encoder, STRING_CODEC, original);
    BinaryDecoder decoder = encoder.createDecoder();
    String[] decoded = ItemCodec.Arrays.decodeArray(decoder, STRING_CODEC);
    assertNotNull(decoded);
    assertEquals(0, decoded.length);
  }

  @Test
  public void encodeDecodeArray_singleElement_roundTrips() {
    String[] original = {"only"};
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    ItemCodec.Arrays.encodeArray(encoder, STRING_CODEC, original);
    BinaryDecoder decoder = encoder.createDecoder();
    String[] decoded = ItemCodec.Arrays.decodeArray(decoder, STRING_CODEC);
    assertArrayEquals(original, decoded);
  }

  // ── Encode + Decode null array ────────────────────────────────────

  @Test
  public void encodeDecodeArray_null_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    ItemCodec.Arrays.encodeArray(encoder, STRING_CODEC, null);
    BinaryDecoder decoder = encoder.createDecoder();
    String[] decoded = ItemCodec.Arrays.decodeArray(decoder, STRING_CODEC);
    assertNull(decoded);
  }

  // ── Large array ───────────────────────────────────────────────────

  @Test
  public void encodeDecodeArray_largeArray_roundTrips() {
    String[] original = new String[100];
    for (int i = 0; i < 100; i++) {
      original[i] = "item_" + i;
    }
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    ItemCodec.Arrays.encodeArray(encoder, STRING_CODEC, original);
    BinaryDecoder decoder = encoder.createDecoder();
    String[] decoded = ItemCodec.Arrays.decodeArray(decoder, STRING_CODEC);
    assertArrayEquals(original, decoded);
  }

  // ── Integer codec round-trip ──────────────────────────────────────

  @Test
  public void encodeDecodeArray_integerCodec_roundTrips() {
    ItemCodec<Integer> intCodec = new ItemCodec<Integer>() {
      @Override
      public Class<Integer> getValueClass() { return Integer.class; }

      @Override
      public Integer decodeValue(BinaryDecoder d) { return d.decodeInt(); }

      @Override
      public void encodeValue(BinaryEncoder e, Integer v) { e.encode(v); }

      @Override
      public void appendRepresentation(StringBuilder sb, Integer v) { sb.append(v); }
    };

    Integer[] original = {1, 2, 3, 42, -7};
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    ItemCodec.Arrays.encodeArray(encoder, intCodec, original);
    BinaryDecoder decoder = encoder.createDecoder();
    Integer[] decoded = ItemCodec.Arrays.decodeArray(decoder, intCodec);
    assertArrayEquals(original, decoded);
  }

  // ── ItemCodec interface ───────────────────────────────────────────

  @Test
  public void stringCodec_getValueClass() {
    assertEquals(String.class, STRING_CODEC.getValueClass());
  }

  @Test
  public void stringCodec_appendRepresentation() {
    StringBuilder sb = new StringBuilder();
    STRING_CODEC.appendRepresentation(sb, "hello");
    assertEquals("hello", sb.toString());
  }

  @Test
  public void stringCodec_encodeDecodeValue() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    STRING_CODEC.encodeValue(encoder, "test");
    BinaryDecoder decoder = encoder.createDecoder();
    String decoded = STRING_CODEC.decodeValue(decoder);
    assertEquals("test", decoded);
  }

  @Test
  public void stringCodec_multipleValues_roundTrip() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    STRING_CODEC.encodeValue(encoder, "first");
    STRING_CODEC.encodeValue(encoder, "second");
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("first", STRING_CODEC.decodeValue(decoder));
    assertEquals("second", STRING_CODEC.decodeValue(decoder));
  }
}
