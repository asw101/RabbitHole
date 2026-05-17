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
    InputStreamBinaryDecoder decoder = decoderFromEncoded();
    assertEquals(Long.MIN_VALUE, decoder.decodeLong());
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
}
