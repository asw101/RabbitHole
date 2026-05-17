package edu.cmu.cs.dennisc.codec;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Round-trip encode→decode tests for the binary codec system.
 * Covers OutputStreamBinaryEncoder, InputStreamBinaryDecoder,
 * AbstractBinaryEncoder, and AbstractBinaryDecoder.
 */
public class BinaryCodecRoundTripTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private ByteArrayOutputStream baos;
  private OutputStreamBinaryEncoder encoder;

  @Before
  public void setUp() {
    baos = new ByteArrayOutputStream();
    encoder = new OutputStreamBinaryEncoder(baos);
  }

  private InputStreamBinaryDecoder decoderFromEncoded() {
    encoder.flush();
    return new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
  }

  // --- Primitive round-trips ---

  @Test
  public void roundTrip_boolean_true() {
    encoder.encode(true);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertTrue(decoder.decodeBoolean());
  }

  @Test
  public void roundTrip_boolean_false() {
    encoder.encode(false);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertFalse(decoder.decodeBoolean());
  }

  @Test
  public void roundTrip_byte() {
    encoder.encode((byte) 42);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals((byte) 42, decoder.decodeByte());
  }

  @Test
  public void roundTrip_byte_minMax() {
    encoder.encode(Byte.MIN_VALUE);
    encoder.encode(Byte.MAX_VALUE);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals(Byte.MIN_VALUE, decoder.decodeByte());
    assertEquals(Byte.MAX_VALUE, decoder.decodeByte());
  }

  @Test
  public void roundTrip_char() {
    encoder.encode('Z');
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals('Z', decoder.decodeChar());
  }

  @Test
  public void roundTrip_char_unicode() {
    encoder.encode('\u00E9'); // é
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals('\u00E9', decoder.decodeChar());
  }

  @Test
  public void roundTrip_double() {
    encoder.encode(3.141592653589793);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals(3.141592653589793, decoder.decodeDouble(), 0.0);
  }

  @Test
  public void roundTrip_double_specialValues() {
    encoder.encode(Double.NaN);
    encoder.encode(Double.POSITIVE_INFINITY);
    encoder.encode(Double.NEGATIVE_INFINITY);
    encoder.encode(Double.MIN_VALUE);
    encoder.encode(Double.MAX_VALUE);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertTrue(Double.isNaN(decoder.decodeDouble()));
    assertEquals(Double.POSITIVE_INFINITY, decoder.decodeDouble(), 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, decoder.decodeDouble(), 0.0);
    assertEquals(Double.MIN_VALUE, decoder.decodeDouble(), 0.0);
    assertEquals(Double.MAX_VALUE, decoder.decodeDouble(), 0.0);
  }

  @Test
  public void roundTrip_float() {
    encoder.encode(2.71828f);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals(2.71828f, decoder.decodeFloat(), 0.0f);
  }

  @Test
  public void roundTrip_float_specialValues() {
    encoder.encode(Float.NaN);
    encoder.encode(Float.POSITIVE_INFINITY);
    encoder.encode(Float.NEGATIVE_INFINITY);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertTrue(Float.isNaN(decoder.decodeFloat()));
    assertEquals(Float.POSITIVE_INFINITY, decoder.decodeFloat(), 0.0f);
    assertEquals(Float.NEGATIVE_INFINITY, decoder.decodeFloat(), 0.0f);
  }

  @Test
  public void roundTrip_int() {
    encoder.encode(Integer.MAX_VALUE);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals(Integer.MAX_VALUE, decoder.decodeInt());
  }

  @Test
  public void roundTrip_int_negative() {
    encoder.encode(-1);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals(-1, decoder.decodeInt());
  }

  @Test
  public void roundTrip_int_zero() {
    encoder.encode(0);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals(0, decoder.decodeInt());
  }

  @Test
  public void roundTrip_long() {
    encoder.encode(Long.MIN_VALUE);
    encoder.encode(Long.MAX_VALUE);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals(Long.MIN_VALUE, decoder.decodeLong());
    assertEquals(Long.MAX_VALUE, decoder.decodeLong());
  }

  @Test
  public void roundTrip_short() {
    encoder.encode((short) 12345);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals((short) 12345, decoder.decodeShort());
  }

  @Test
  public void roundTrip_short_minMax() {
    encoder.encode(Short.MIN_VALUE);
    encoder.encode(Short.MAX_VALUE);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals(Short.MIN_VALUE, decoder.decodeShort());
    assertEquals(Short.MAX_VALUE, decoder.decodeShort());
  }

  // --- String round-trips ---

  @Test
  public void roundTrip_string() {
    encoder.encode("Hello, Alice!");
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals("Hello, Alice!", decoder.decodeString());
  }

  @Test
  public void roundTrip_string_empty() {
    encoder.encode("");
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals("", decoder.decodeString());
  }

  @Test
  public void roundTrip_string_null() {
    encoder.encode((String) null);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertNull(decoder.decodeString());
  }

  @Test
  public void roundTrip_string_unicode() {
    String unicode = "café \u2603 \uD83D\uDE00";
    encoder.encode(unicode);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals(unicode, decoder.decodeString());
  }

  // --- UUID round-trips ---

  @Test
  public void roundTrip_uuid() {
    UUID id = UUID.randomUUID();
    encoder.encode(id);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals(id, decoder.decodeId());
  }

  @Test
  public void roundTrip_uuid_null() {
    encoder.encode((UUID) null);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertNull(decoder.decodeId());
  }

  // --- Enum round-trips (via AbstractBinaryEncoder/Decoder) ---

  private enum TestColor { RED, GREEN, BLUE }

  @Test
  public void roundTrip_enum() {
    encoder.encode(TestColor.GREEN);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals(TestColor.GREEN, decoder.<TestColor>decodeEnum());
  }

  @Test
  public void roundTrip_enum_null() {
    encoder.encode((Enum<?>) null);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertNull(decoder.<TestColor>decodeEnum());
  }

  // --- Primitive array round-trips (via AbstractBinaryEncoder/Decoder) ---

  @Test
  public void roundTrip_booleanArray() {
    boolean[] data = {true, false, true, true, false};
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertArrayEquals(data, decoder.decodeBooleanArray());
  }

  @Test
  public void roundTrip_booleanArray_empty() {
    boolean[] data = {};
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertArrayEquals(data, decoder.decodeBooleanArray());
  }

  @Test(expected = NullPointerException.class)
  public void roundTrip_booleanArray_null() {
    // Encoder writes -1 for null arrays, but decoder NPEs on decode
    encoder.encode((boolean[]) null);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    decoder.decodeBooleanArray();
  }

  @Test
  public void roundTrip_byteArray() {
    byte[] data = {0, 1, -1, Byte.MIN_VALUE, Byte.MAX_VALUE};
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertArrayEquals(data, decoder.decodeByteArray());
  }

  @Test
  public void roundTrip_charArray() {
    char[] data = {'a', 'Z', '\u00E9', '0'};
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertArrayEquals(data, decoder.decodeCharArray());
  }

  @Test
  public void roundTrip_doubleArray() {
    double[] data = {0.0, -1.5, Double.MAX_VALUE, Double.NaN};
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    double[] result = decoder.decodeDoubleArray();
    assertEquals(data.length, result.length);
    assertEquals(data[0], result[0], 0.0);
    assertEquals(data[1], result[1], 0.0);
    assertEquals(data[2], result[2], 0.0);
    assertTrue(Double.isNaN(result[3]));
  }

  @Test
  public void roundTrip_floatArray() {
    float[] data = {1.0f, -2.5f, Float.MIN_VALUE};
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertArrayEquals(data, decoder.decodeFloatArray(), 0.0f);
  }

  @Test
  public void roundTrip_intArray() {
    int[] data = {0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE};
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertArrayEquals(data, decoder.decodeIntArray());
  }

  @Test
  public void roundTrip_longArray() {
    long[] data = {0L, Long.MAX_VALUE, Long.MIN_VALUE, 42L};
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertArrayEquals(data, decoder.decodeLongArray());
  }

  @Test
  public void roundTrip_shortArray() {
    short[] data = {0, 1, -1, Short.MAX_VALUE, Short.MIN_VALUE};
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertArrayEquals(data, decoder.decodeShortArray());
  }

  // --- String array round-trips ---

  @Test
  public void roundTrip_stringArray() {
    String[] data = {"hello", "world", "", null};
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertArrayEquals(data, decoder.decodeStringArray());
  }

  @Test
  public void roundTrip_stringArray_empty() {
    String[] data = {};
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertArrayEquals(data, decoder.decodeStringArray());
  }

  @Test(expected = NullPointerException.class)
  public void roundTrip_stringArray_null() {
    encoder.encode((String[]) null);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    decoder.decodeStringArray();
  }

  // --- Enum array round-trips ---

  @Test
  public void roundTrip_enumArray() {
    TestColor[] data = {TestColor.RED, TestColor.BLUE, TestColor.GREEN};
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertArrayEquals(data, decoder.decodeEnumArray(TestColor.class));
  }

  @Test(expected = NullPointerException.class)
  public void roundTrip_enumArray_null() {
    encoder.encode((Enum<?>[]) null);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    decoder.decodeEnumArray(TestColor.class);
  }

  // --- UUID array round-trips ---

  @Test
  public void roundTrip_uuidArray() {
    UUID[] data = {UUID.randomUUID(), UUID.randomUUID(), null};
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertArrayEquals(data, decoder.decodeIdArray());
  }

  @Test
  public void roundTrip_uuidArray_empty() {
    UUID[] data = {};
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertArrayEquals(data, decoder.decodeIdArray());
  }

  // --- Mixed types in sequence ---

  @Test
  public void roundTrip_mixedPrimitives() {
    encoder.encode(true);
    encoder.encode((byte) 7);
    encoder.encode('X');
    encoder.encode(3.14);
    encoder.encode(2.72f);
    encoder.encode(42);
    encoder.encode(123456789L);
    encoder.encode((short) 999);
    encoder.encode("mixed");

    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertTrue(decoder.decodeBoolean());
    assertEquals((byte) 7, decoder.decodeByte());
    assertEquals('X', decoder.decodeChar());
    assertEquals(3.14, decoder.decodeDouble(), 0.0);
    assertEquals(2.72f, decoder.decodeFloat(), 0.0f);
    assertEquals(42, decoder.decodeInt());
    assertEquals(123456789L, decoder.decodeLong());
    assertEquals((short) 999, decoder.decodeShort());
    assertEquals("mixed", decoder.decodeString());
  }

  // --- Raw byte write/readFully ---

  @Test
  public void roundTrip_writeAndReadFully() {
    byte[] data = {10, 20, 30, 40, 50};
    encoder.write(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    byte[] result = new byte[5];
    decoder.readFully(result);
    assertArrayEquals(data, result);
  }

  @Test
  public void roundTrip_writeWithOffsetLength() {
    byte[] data = {10, 20, 30, 40, 50};
    encoder.write(data, 1, 3);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    byte[] result = new byte[3];
    decoder.readFully(result);
    assertArrayEquals(new byte[]{20, 30, 40}, result);
  }

  // --- flush ---

  @Test
  public void flush_doesNotThrow() {
    encoder.encode(42);
    encoder.flush();
    // Verify data is actually flushed and readable
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(42, decoder.decodeInt());
  }

  // --- File-based decoder constructor ---

  @Test
  public void decoder_fromFile() throws IOException {
    encoder.encode("file-test");
    encoder.flush();

    File tempFile = tempFolder.newFile("codec-test.bin");
    java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile);
    fos.write(baos.toByteArray());
    fos.close();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(tempFile);
    assertEquals("file-test", decoder.decodeString());
  }

  @Test
  public void decoder_fromPath() throws IOException {
    encoder.encode(99);
    encoder.flush();

    File tempFile = tempFolder.newFile("codec-path-test.bin");
    java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile);
    fos.write(baos.toByteArray());
    fos.close();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(tempFile.getAbsolutePath());
    assertEquals(99, decoder.decodeInt());
  }

  // --- Large data ---

  @Test
  public void roundTrip_largeIntArray() {
    int[] data = new int[10000];
    for (int i = 0; i < data.length; i++) {
      data[i] = i * 7 - 5000;
    }
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertArrayEquals(data, decoder.decodeIntArray());
  }

  @Test
  public void roundTrip_largeString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      sb.append((char) ('A' + (i % 26)));
    }
    String data = sb.toString();
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals(data, decoder.decodeString());
  }

  // --- BinaryEncodableAndDecodable round-trips ---

  public static class SimpleEncodable implements BinaryEncodableAndDecodable {
    private int value;
    private String name;

    public SimpleEncodable() {}

    public SimpleEncodable(int value, String name) {
      this.value = value;
      this.name = name;
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(value);
      binaryEncoder.encode(name);
    }

    public void decode(BinaryDecoder binaryDecoder) {
      value = binaryDecoder.decodeInt();
      name = binaryDecoder.decodeString();
    }

    public int getValue() { return value; }
    public String getName() { return name; }
  }

  @Test
  public void roundTrip_binaryEncodable() {
    SimpleEncodable original = new SimpleEncodable(42, "test-item");
    encoder.encode(original);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    SimpleEncodable result = decoder.decodeBinaryEncodableAndDecodable();
    assertNotNull(result);
    assertEquals(42, result.getValue());
    assertEquals("test-item", result.getName());
  }

  @Test
  public void roundTrip_binaryEncodable_null() {
    encoder.encode((BinaryEncodableAndDecodable) null);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertNull(decoder.decodeBinaryEncodableAndDecodable());
  }

  @Test
  public void roundTrip_binaryEncodableArray() {
    SimpleEncodable[] data = {
        new SimpleEncodable(1, "first"),
        new SimpleEncodable(2, "second")
    };
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    SimpleEncodable[] result = decoder.decodeBinaryEncodableAndDecodableArray(SimpleEncodable.class);
    assertNotNull(result);
    assertEquals(2, result.length);
    assertEquals(1, result[0].getValue());
    assertEquals("second", result[1].getName());
  }

  @Test(expected = NullPointerException.class)
  public void roundTrip_binaryEncodableArray_null() {
    encoder.encode((BinaryEncodableAndDecodable[]) null);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    decoder.decodeBinaryEncodableAndDecodableArray(SimpleEncodable.class);
  }

  @Test
  public void roundTrip_binaryEncodableArray_empty() {
    SimpleEncodable[] data = {};
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    SimpleEncodable[] result = decoder.decodeBinaryEncodableAndDecodableArray(SimpleEncodable.class);
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  // --- NIO Buffer round-trip tests for codec.BufferUtilities ---

  @Test
  public void roundTrip_floatBuffer() {
    java.nio.FloatBuffer fb = java.nio.FloatBuffer.wrap(new float[]{1.0f, 2.5f, -3.14f, 0.0f});
    BufferUtilities.encode(encoder, fb);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.nio.FloatBuffer result = BufferUtilities.decodeFloatBuffer(decoder);
    assertEquals(4, result.limit());
    assertEquals(1.0f, result.get(0), 1e-6f);
    assertEquals(2.5f, result.get(1), 1e-6f);
    assertEquals(-3.14f, result.get(2), 1e-3f);
    assertEquals(0.0f, result.get(3), 1e-6f);
  }

  @Test
  public void roundTrip_doubleBuffer() {
    java.nio.DoubleBuffer db = java.nio.DoubleBuffer.wrap(new double[]{1.0, 2.5, -3.14, 0.0, 999.999});
    BufferUtilities.encode(encoder, db);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.nio.DoubleBuffer result = BufferUtilities.decodeDoubleBuffer(decoder);
    assertEquals(5, result.limit());
    assertEquals(1.0, result.get(0), 1e-10);
    assertEquals(999.999, result.get(4), 1e-10);
  }

  @Test
  public void roundTrip_intBuffer() {
    java.nio.IntBuffer ib = java.nio.IntBuffer.wrap(new int[]{-1, 0, 1, Integer.MAX_VALUE, Integer.MIN_VALUE});
    BufferUtilities.encode(encoder, ib);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.nio.IntBuffer result = BufferUtilities.decodeIntBuffer(decoder);
    assertEquals(5, result.limit());
    assertEquals(-1, result.get(0));
    assertEquals(Integer.MAX_VALUE, result.get(3));
    assertEquals(Integer.MIN_VALUE, result.get(4));
  }

  @Test
  public void roundTrip_intBuffer_nativeOptional() {
    java.nio.IntBuffer ib = java.nio.IntBuffer.wrap(new int[]{10, 20, 30});
    BufferUtilities.encodeNativeOptional(encoder, ib);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.nio.IntBuffer result = BufferUtilities.decodeIntBuffer(decoder);
    assertEquals(3, result.limit());
    assertEquals(10, result.get(0));
    assertEquals(20, result.get(1));
    assertEquals(30, result.get(2));
  }

  @Test
  public void roundTrip_shortBuffer() {
    java.nio.ShortBuffer sb = java.nio.ShortBuffer.wrap(new short[]{1, -2, 3, Short.MAX_VALUE});
    BufferUtilities.encode(encoder, sb);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.nio.ShortBuffer result = BufferUtilities.decodeShortBuffer(decoder);
    assertEquals(4, result.limit());
    assertEquals(1, result.get(0));
    assertEquals(Short.MAX_VALUE, result.get(3));
  }

  @Test
  public void roundTrip_longBuffer() {
    java.nio.LongBuffer lb = java.nio.LongBuffer.wrap(new long[]{Long.MIN_VALUE, 0L, Long.MAX_VALUE});
    BufferUtilities.encode(encoder, lb);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.nio.LongBuffer result = BufferUtilities.decodeLongBuffer(decoder);
    assertEquals(3, result.limit());
    assertEquals(Long.MIN_VALUE, result.get(0));
    assertEquals(Long.MAX_VALUE, result.get(2));
  }

  @Test
  public void roundTrip_charBuffer() {
    java.nio.CharBuffer cb = java.nio.CharBuffer.wrap(new char[]{'A', 'B', 'Z', '0'});
    BufferUtilities.encode(encoder, cb);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.nio.CharBuffer result = BufferUtilities.decodeCharBuffer(decoder);
    assertEquals(4, result.limit());
    assertEquals('A', result.get(0));
    assertEquals('Z', result.get(2));
  }

  @Test
  public void roundTrip_byteBuffer_encode() {
    java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(new byte[]{1, 2, 3, 4});
    // Just verify encoding doesn't throw
    BufferUtilities.encode(encoder, bb);
    encoder.flush();
    assertTrue(baos.size() > 0);
  }

  @Test
  public void roundTrip_enum_extended() {
    encoder.encode(java.util.concurrent.TimeUnit.SECONDS);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.util.concurrent.TimeUnit result = decoder.decodeEnum();
    assertEquals(java.util.concurrent.TimeUnit.SECONDS, result);
  }

  // --- Additional typed decode tests for AbstractBinaryEncoder/Decoder ---

  @Test
  public void roundTrip_enumArray_typed() {
    java.util.concurrent.TimeUnit[] data = {
        java.util.concurrent.TimeUnit.DAYS,
        java.util.concurrent.TimeUnit.HOURS,
        java.util.concurrent.TimeUnit.MINUTES
    };
    encoder.encode(data);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.util.concurrent.TimeUnit[] result = decoder.decodeEnumArray(java.util.concurrent.TimeUnit.class);
    assertArrayEquals(data, result);
  }

  @Test
  public void roundTrip_enum_typed() {
    encoder.encode(java.util.concurrent.TimeUnit.DAYS);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.util.concurrent.TimeUnit result = decoder.decodeEnum(java.util.concurrent.TimeUnit.class);
    assertEquals(java.util.concurrent.TimeUnit.DAYS, result);
  }

  // --- ReferenceableBinaryEncodableAndDecodable round-trips ---

  public static class SimpleReferenceable implements ReferenceableBinaryEncodableAndDecodable {
    private int id;
    private String label;

    public SimpleReferenceable() {}

    public SimpleReferenceable(int id, String label) {
      this.id = id;
      this.label = label;
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder, java.util.Map<ReferenceableBinaryEncodableAndDecodable, Integer> map) {
      binaryEncoder.encode(id);
      binaryEncoder.encode(label);
    }

    @Override
    public void decode(BinaryDecoder binaryDecoder, java.util.Map<Integer, ReferenceableBinaryEncodableAndDecodable> map) {
      id = binaryDecoder.decodeInt();
      label = binaryDecoder.decodeString();
    }

    public int getId() { return id; }
    public String getLabel() { return label; }

    @Override
    public int hashCode() {
      return id;
    }
  }

  @Test
  public void roundTrip_referenceableSingle() {
    SimpleReferenceable original = new SimpleReferenceable(1, "ref-one");
    java.util.Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodeMap = new java.util.HashMap<>();
    encoder.encode(original, encodeMap);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.util.Map<Integer, ReferenceableBinaryEncodableAndDecodable> decodeMap = new java.util.HashMap<>();
    SimpleReferenceable result = decoder.decodeReferenceableBinaryEncodableAndDecodable(decodeMap);
    assertNotNull(result);
    assertEquals(1, result.getId());
    assertEquals("ref-one", result.getLabel());
  }

  @Test
  public void roundTrip_referenceableNull() {
    java.util.Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodeMap = new java.util.HashMap<>();
    encoder.encode((ReferenceableBinaryEncodableAndDecodable) null, encodeMap);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.util.Map<Integer, ReferenceableBinaryEncodableAndDecodable> decodeMap = new java.util.HashMap<>();
    ReferenceableBinaryEncodableAndDecodable result = decoder.decodeReferenceableBinaryEncodableAndDecodable(decodeMap);
    assertNull(result);
  }

  @Test
  public void roundTrip_referenceableArray() {
    SimpleReferenceable[] originals = {
        new SimpleReferenceable(10, "ten"),
        new SimpleReferenceable(20, "twenty")
    };
    java.util.Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodeMap = new java.util.HashMap<>();
    encoder.encode(originals, encodeMap);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.util.Map<Integer, ReferenceableBinaryEncodableAndDecodable> decodeMap = new java.util.HashMap<>();
    SimpleReferenceable[] results = decoder.decodeReferenceableBinaryEncodableAndDecodableArray(SimpleReferenceable.class, decodeMap);
    assertNotNull(results);
    assertEquals(2, results.length);
    assertEquals(10, results[0].getId());
    assertEquals("twenty", results[1].getLabel());
  }

  @Test
  public void roundTrip_referenceableArray_empty() {
    SimpleReferenceable[] originals = {};
    java.util.Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodeMap = new java.util.HashMap<>();
    encoder.encode(originals, encodeMap);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.util.Map<Integer, ReferenceableBinaryEncodableAndDecodable> decodeMap = new java.util.HashMap<>();
    SimpleReferenceable[] results = decoder.decodeReferenceableBinaryEncodableAndDecodableArray(SimpleReferenceable.class, decodeMap);
    assertNotNull(results);
    assertEquals(0, results.length);
  }

  @Test
  public void roundTrip_referenceableDuplicateReference() {
    // Encode the same object twice — second should be a reference
    SimpleReferenceable shared = new SimpleReferenceable(42, "shared");
    java.util.Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodeMap = new java.util.HashMap<>();
    encoder.encode(shared, encodeMap);
    encoder.encode(shared, encodeMap);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.util.Map<Integer, ReferenceableBinaryEncodableAndDecodable> decodeMap = new java.util.HashMap<>();
    SimpleReferenceable first = decoder.decodeReferenceableBinaryEncodableAndDecodable(decodeMap);
    SimpleReferenceable second = decoder.decodeReferenceableBinaryEncodableAndDecodable(decodeMap);
    assertNotNull(first);
    assertNotNull(second);
    assertEquals(42, first.getId());
    assertSame(first, second);
  }

  @Test
  public void roundTrip_referenceableArrayWithDuplicates() {
    SimpleReferenceable item = new SimpleReferenceable(7, "seven");
    SimpleReferenceable[] originals = {item, item};
    java.util.Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodeMap = new java.util.HashMap<>();
    encoder.encode(originals, encodeMap);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.util.Map<Integer, ReferenceableBinaryEncodableAndDecodable> decodeMap = new java.util.HashMap<>();
    SimpleReferenceable[] results = decoder.decodeReferenceableBinaryEncodableAndDecodableArray(SimpleReferenceable.class, decodeMap);
    assertEquals(2, results.length);
    assertSame(results[0], results[1]);
  }

  @Test
  public void roundTrip_referenceableMultipleDistinct() {
    SimpleReferenceable a = new SimpleReferenceable(1, "alpha");
    SimpleReferenceable b = new SimpleReferenceable(2, "beta");
    SimpleReferenceable c = new SimpleReferenceable(3, "gamma");
    java.util.Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodeMap = new java.util.HashMap<>();
    encoder.encode(a, encodeMap);
    encoder.encode(b, encodeMap);
    encoder.encode(c, encodeMap);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.util.Map<Integer, ReferenceableBinaryEncodableAndDecodable> decodeMap = new java.util.HashMap<>();
    SimpleReferenceable ra = decoder.decodeReferenceableBinaryEncodableAndDecodable(decodeMap);
    SimpleReferenceable rb = decoder.decodeReferenceableBinaryEncodableAndDecodable(decodeMap);
    SimpleReferenceable rc = decoder.decodeReferenceableBinaryEncodableAndDecodable(decodeMap);
    assertEquals("alpha", ra.getLabel());
    assertEquals("beta", rb.getLabel());
    assertEquals("gamma", rc.getLabel());
  }

  @Test
  public void roundTrip_referenceableSingle_typed() {
    SimpleReferenceable original = new SimpleReferenceable(5, "five");
    java.util.Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodeMap = new java.util.HashMap<>();
    encoder.encode(original, encodeMap);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.util.Map<Integer, ReferenceableBinaryEncodableAndDecodable> decodeMap = new java.util.HashMap<>();
    SimpleReferenceable result = decoder.decodeReferenceableBinaryEncodableAndDecodable(SimpleReferenceable.class, decodeMap);
    assertNotNull(result);
    assertEquals(5, result.getId());
    assertEquals("five", result.getLabel());
  }

  @Test
  public void roundTrip_referenceableEncodeMap_populated() {
    SimpleReferenceable item = new SimpleReferenceable(99, "mapped");
    java.util.Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodeMap = new java.util.HashMap<>();
    encoder.encode(item, encodeMap);
    assertTrue(encodeMap.containsKey(item));
    assertEquals(Integer.valueOf(item.hashCode()), encodeMap.get(item));
  }

  @Test
  public void roundTrip_referenceableDecodeMap_populated() {
    SimpleReferenceable original = new SimpleReferenceable(88, "decoded");
    java.util.Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodeMap = new java.util.HashMap<>();
    encoder.encode(original, encodeMap);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.util.Map<Integer, ReferenceableBinaryEncodableAndDecodable> decodeMap = new java.util.HashMap<>();
    SimpleReferenceable result = decoder.decodeReferenceableBinaryEncodableAndDecodable(decodeMap);
    assertFalse(decodeMap.isEmpty());
    assertTrue(decodeMap.containsValue(result));
  }

  @Test
  public void roundTrip_referenceableMixedWithPrimitives() {
    encoder.encode(42);
    SimpleReferenceable ref = new SimpleReferenceable(100, "mixed");
    java.util.Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodeMap = new java.util.HashMap<>();
    encoder.encode(ref, encodeMap);
    encoder.encode("after-ref");
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals(42, decoder.decodeInt());
    java.util.Map<Integer, ReferenceableBinaryEncodableAndDecodable> decodeMap = new java.util.HashMap<>();
    SimpleReferenceable result = decoder.decodeReferenceableBinaryEncodableAndDecodable(decodeMap);
    assertEquals(100, result.getId());
    assertEquals("after-ref", decoder.decodeString());
  }

  // --- encodeProperties / decodeProperties round-trips ---

  /** Encode owner's properties, then decode into restored and return it. */
  private <T extends edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner> T roundTripProperties(T owner, T restored) {
    java.util.Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodeMap = new java.util.HashMap<>();
    encoder.encodeProperties(owner, encodeMap);
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    java.util.Map<Integer, ReferenceableBinaryEncodableAndDecodable> decodeMap = new java.util.HashMap<>();
    decoder.decodeProperties(restored, decodeMap);
    return restored;
  }

  public static class TwoPropertyOwner extends edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner {
    public final edu.cmu.cs.dennisc.property.InstanceProperty<Integer> alpha =
        new edu.cmu.cs.dennisc.property.InstanceProperty<>(this, 0);
    public final edu.cmu.cs.dennisc.property.InstanceProperty<String> beta =
        new edu.cmu.cs.dennisc.property.InstanceProperty<>(this, null);
  }

  @Test
  public void roundTrip_encodeDecodeProperties_intAndString() {
    TwoPropertyOwner owner = new TwoPropertyOwner();
    owner.alpha.setValue(42);
    owner.beta.setValue("hello");
    TwoPropertyOwner restored = roundTripProperties(owner, new TwoPropertyOwner());
    assertEquals(Integer.valueOf(42), restored.alpha.getValue());
    assertEquals("hello", restored.beta.getValue());
  }

  @Test
  public void roundTrip_encodeDecodeProperties_nullValue() {
    TwoPropertyOwner owner = new TwoPropertyOwner();
    owner.alpha.setValue(7);
    owner.beta.setValue(null);
    TwoPropertyOwner restored = roundTripProperties(owner, new TwoPropertyOwner());
    assertEquals(Integer.valueOf(7), restored.alpha.getValue());
    assertNull(restored.beta.getValue());
  }

  public static class DoublePropertyOwner extends edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner {
    public final edu.cmu.cs.dennisc.property.InstanceProperty<Double> value =
        new edu.cmu.cs.dennisc.property.InstanceProperty<>(this, 0.0);
  }

  @Test
  public void roundTrip_encodeDecodeProperties_double() {
    DoublePropertyOwner owner = new DoublePropertyOwner();
    owner.value.setValue(3.14);
    DoublePropertyOwner restored = roundTripProperties(owner, new DoublePropertyOwner());
    assertEquals(3.14, restored.value.getValue(), 1e-10);
  }

  public static class BooleanPropertyOwner extends edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner {
    public final edu.cmu.cs.dennisc.property.InstanceProperty<Boolean> flag =
        new edu.cmu.cs.dennisc.property.InstanceProperty<>(this, false);
  }

  @Test
  public void roundTrip_encodeDecodeProperties_boolean() {
    BooleanPropertyOwner owner = new BooleanPropertyOwner();
    owner.flag.setValue(true);
    BooleanPropertyOwner restored = roundTripProperties(owner, new BooleanPropertyOwner());
    assertTrue(restored.flag.getValue());
  }

  public static class IntArrayPropertyOwner extends edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner {
    public final edu.cmu.cs.dennisc.property.InstanceProperty<int[]> nums =
        new edu.cmu.cs.dennisc.property.InstanceProperty<>(this, null);
  }

  @Test
  public void roundTrip_encodeDecodeProperties_intArray() {
    IntArrayPropertyOwner owner = new IntArrayPropertyOwner();
    owner.nums.setValue(new int[]{10, 20, 30});
    IntArrayPropertyOwner restored = roundTripProperties(owner, new IntArrayPropertyOwner());
    assertArrayEquals(new int[]{10, 20, 30}, (int[]) restored.nums.getValue());
  }

  public static class DoubleArrayPropertyOwner extends edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner {
    public final edu.cmu.cs.dennisc.property.InstanceProperty<double[]> vals =
        new edu.cmu.cs.dennisc.property.InstanceProperty<>(this, null);
  }

  @Test
  public void roundTrip_encodeDecodeProperties_doubleArray() {
    DoubleArrayPropertyOwner owner = new DoubleArrayPropertyOwner();
    owner.vals.setValue(new double[]{1.1, 2.2});
    DoubleArrayPropertyOwner restored = roundTripProperties(owner, new DoubleArrayPropertyOwner());
    double[] result = (double[]) restored.vals.getValue();
    assertEquals(2, result.length);
    assertEquals(1.1, result[0], 1e-10);
    assertEquals(2.2, result[1], 1e-10);
  }

  public static class ByteArrayPropertyOwner extends edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner {
    public final edu.cmu.cs.dennisc.property.InstanceProperty<byte[]> data =
        new edu.cmu.cs.dennisc.property.InstanceProperty<>(this, null);
  }

  @Test
  public void roundTrip_encodeDecodeProperties_byteArray() {
    ByteArrayPropertyOwner owner = new ByteArrayPropertyOwner();
    owner.data.setValue(new byte[]{1, 2, 3});
    ByteArrayPropertyOwner restored = roundTripProperties(owner, new ByteArrayPropertyOwner());
    assertArrayEquals(new byte[]{1, 2, 3}, (byte[]) restored.data.getValue());
  }

  public static class FloatArrayPropertyOwner extends edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner {
    public final edu.cmu.cs.dennisc.property.InstanceProperty<float[]> data =
        new edu.cmu.cs.dennisc.property.InstanceProperty<>(this, null);
  }

  @Test
  public void roundTrip_encodeDecodeProperties_floatArray() {
    FloatArrayPropertyOwner owner = new FloatArrayPropertyOwner();
    owner.data.setValue(new float[]{1.5f, 2.5f});
    FloatArrayPropertyOwner restored = roundTripProperties(owner, new FloatArrayPropertyOwner());
    float[] result = (float[]) restored.data.getValue();
    assertEquals(1.5f, result[0], 1e-5f);
    assertEquals(2.5f, result[1], 1e-5f);
  }

  public static class BooleanArrayPropertyOwner extends edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner {
    public final edu.cmu.cs.dennisc.property.InstanceProperty<boolean[]> flags =
        new edu.cmu.cs.dennisc.property.InstanceProperty<>(this, null);
  }

  @Test
  public void roundTrip_encodeDecodeProperties_booleanArray() {
    BooleanArrayPropertyOwner owner = new BooleanArrayPropertyOwner();
    owner.flags.setValue(new boolean[]{true, false, true});
    BooleanArrayPropertyOwner restored = roundTripProperties(owner, new BooleanArrayPropertyOwner());
    assertArrayEquals(new boolean[]{true, false, true}, (boolean[]) restored.flags.getValue());
  }

  public static class ShortArrayPropertyOwner extends edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner {
    public final edu.cmu.cs.dennisc.property.InstanceProperty<short[]> data =
        new edu.cmu.cs.dennisc.property.InstanceProperty<>(this, null);
  }

  @Test
  public void roundTrip_encodeDecodeProperties_shortArray() {
    ShortArrayPropertyOwner owner = new ShortArrayPropertyOwner();
    owner.data.setValue(new short[]{5, 10});
    ShortArrayPropertyOwner restored = roundTripProperties(owner, new ShortArrayPropertyOwner());
    assertArrayEquals(new short[]{5, 10}, (short[]) restored.data.getValue());
  }

  public static class LongArrayPropertyOwner extends edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner {
    public final edu.cmu.cs.dennisc.property.InstanceProperty<long[]> data =
        new edu.cmu.cs.dennisc.property.InstanceProperty<>(this, null);
  }

  @Test
  public void roundTrip_encodeDecodeProperties_longArray() {
    LongArrayPropertyOwner owner = new LongArrayPropertyOwner();
    owner.data.setValue(new long[]{100L, 200L});
    LongArrayPropertyOwner restored = roundTripProperties(owner, new LongArrayPropertyOwner());
    assertArrayEquals(new long[]{100L, 200L}, (long[]) restored.data.getValue());
  }

  public static class CharArrayPropertyOwner extends edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner {
    public final edu.cmu.cs.dennisc.property.InstanceProperty<char[]> data =
        new edu.cmu.cs.dennisc.property.InstanceProperty<>(this, null);
  }

  @Test
  public void roundTrip_encodeDecodeProperties_charArray() {
    CharArrayPropertyOwner owner = new CharArrayPropertyOwner();
    owner.data.setValue(new char[]{'a', 'b'});
    CharArrayPropertyOwner restored = roundTripProperties(owner, new CharArrayPropertyOwner());
    assertArrayEquals(new char[]{'a', 'b'}, (char[]) restored.data.getValue());
  }

  public static class StringArrayPropertyOwner extends edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner {
    public final edu.cmu.cs.dennisc.property.InstanceProperty<String[]> data =
        new edu.cmu.cs.dennisc.property.InstanceProperty<>(this, null);
  }

  @Test
  public void roundTrip_encodeDecodeProperties_stringArray() {
    StringArrayPropertyOwner owner = new StringArrayPropertyOwner();
    owner.data.setValue(new String[]{"foo", "bar"});
    StringArrayPropertyOwner restored = roundTripProperties(owner, new StringArrayPropertyOwner());
    assertArrayEquals(new String[]{"foo", "bar"}, (String[]) restored.data.getValue());
  }

  public static class EnumPropertyOwner extends edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner {
    public final edu.cmu.cs.dennisc.property.InstanceProperty<java.util.concurrent.TimeUnit> unit =
        new edu.cmu.cs.dennisc.property.InstanceProperty<>(this, null);
  }

  @Test
  public void roundTrip_encodeDecodeProperties_enum() {
    EnumPropertyOwner owner = new EnumPropertyOwner();
    owner.unit.setValue(java.util.concurrent.TimeUnit.HOURS);
    EnumPropertyOwner restored = roundTripProperties(owner, new EnumPropertyOwner());
    assertEquals(java.util.concurrent.TimeUnit.HOURS, restored.unit.getValue());
  }
}
