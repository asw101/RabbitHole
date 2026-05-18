package edu.cmu.cs.dennisc.codec;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class BinaryCodecDeepTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private interface EncoderAction {
    void encode(OutputStreamBinaryEncoder encoder) throws Exception;
  }

  private InputStreamBinaryDecoder decoderFor(EncoderAction action) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    action.encode(encoder);
    encoder.flush();
    return new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
  }

  private File fileFor(EncoderAction action, String name) throws Exception {
    File file = temporaryFolder.newFile(name);
    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(outputStream);
      action.encode(encoder);
      encoder.flush();
    }
    return file;
  }

  private enum SampleEnum {
    ALPHA,
    BETA,
    GAMMA
  }

  private static final class ConstructorDecodedValue implements BinaryEncodableAndDecodable {
    private final String text;
    private final int number;

    public ConstructorDecodedValue(String text, int number) {
      this.text = text;
      this.number = number;
    }

    public ConstructorDecodedValue(BinaryDecoder decoder) {
      this.text = decoder.decodeString();
      this.number = decoder.decodeInt();
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(this.text);
      binaryEncoder.encode(this.number);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof ConstructorDecodedValue other)) {
        return false;
      }
      if (this.number != other.number) {
        return false;
      }
      if (this.text == null) {
        return other.text == null;
      }
      return this.text.equals(other.text);
    }

    @Override
    public int hashCode() {
      return this.number * 31 + (this.text != null ? this.text.hashCode() : 0);
    }
  }

  public static final class MethodDecodedValue implements BinaryEncodableAndDecodable {
    private String text;
    private int number;

    public MethodDecodedValue() {
    }

    public MethodDecodedValue(String text, int number) {
      this.text = text;
      this.number = number;
    }

    public void decode(BinaryDecoder decoder) {
      this.text = decoder.decodeString();
      this.number = decoder.decodeInt();
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(this.text);
      binaryEncoder.encode(this.number);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof MethodDecodedValue other)) {
        return false;
      }
      if (this.number != other.number) {
        return false;
      }
      if (this.text == null) {
        return other.text == null;
      }
      return this.text.equals(other.text);
    }

    @Override
    public int hashCode() {
      return this.number * 31 + (this.text != null ? this.text.hashCode() : 0);
    }
  }

  public static final class ContextDecodedValue implements BinaryEncodableAndDecodable {
    private final String prefix;
    private final String payload;

    public ContextDecodedValue(String prefix, String payload) {
      this.prefix = prefix;
      this.payload = payload;
    }

    public ContextDecodedValue(BinaryDecoder decoder, Object context) {
      this.prefix = String.valueOf(context);
      this.payload = decoder.decodeString();
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(this.payload);
    }

    public String getCombinedText() {
      return this.prefix + ":" + this.payload;
    }
  }

  public static final class ReferenceValue implements ReferenceableBinaryEncodableAndDecodable {
    private String text;

    public ReferenceValue() {
    }

    public ReferenceValue(String text) {
      this.text = text;
    }

    public String getText() {
      return this.text;
    }

    @Override
    public void decode(BinaryDecoder binaryDecoder, Map<Integer, ReferenceableBinaryEncodableAndDecodable> map) {
      this.text = binaryDecoder.decodeString();
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder, Map<ReferenceableBinaryEncodableAndDecodable, Integer> map) {
      binaryEncoder.encode(this.text);
    }
  }

  private record SampleRecord(String name, int value) implements Serializable {
  }

  @Test
  public void primitiveValuesRoundTripInOrder() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode(true);
      encoder.encode((byte) 12);
      encoder.encode('Q');
      encoder.encode(12.5d);
      encoder.encode(7.25f);
      encoder.encode(42);
      encoder.encode(77L);
      encoder.encode((short) 9);
      encoder.encode("codec");
    });

    assertTrue(decoder.decodeBoolean());
    assertEquals((byte) 12, decoder.decodeByte());
    assertEquals('Q', decoder.decodeChar());
    assertEquals(12.5d, decoder.decodeDouble(), 0.0d);
    assertEquals(7.25f, decoder.decodeFloat(), 0.0f);
    assertEquals(42, decoder.decodeInt());
    assertEquals(77L, decoder.decodeLong());
    assertEquals((short) 9, decoder.decodeShort());
    assertEquals("codec", decoder.decodeString());
  }

  @Test
  public void primitiveSpecialValuesRoundTrip() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode(Double.NaN);
      encoder.encode(Double.POSITIVE_INFINITY);
      encoder.encode(Double.NEGATIVE_INFINITY);
      encoder.encode(Float.NaN);
      encoder.encode(Float.POSITIVE_INFINITY);
      encoder.encode(Float.NEGATIVE_INFINITY);
    });

    assertTrue(Double.isNaN(decoder.decodeDouble()));
    assertEquals(Double.POSITIVE_INFINITY, decoder.decodeDouble(), 0.0d);
    assertEquals(Double.NEGATIVE_INFINITY, decoder.decodeDouble(), 0.0d);
    assertTrue(Float.isNaN(decoder.decodeFloat()));
    assertEquals(Float.POSITIVE_INFINITY, decoder.decodeFloat(), 0.0f);
    assertEquals(Float.NEGATIVE_INFINITY, decoder.decodeFloat(), 0.0f);
  }

  @Test
  public void byteArrayWriteAndReadFullyRoundTrip() throws Exception {
    byte[] payload = new byte[] {1, 3, 5, 7, 9};
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.write(payload));
    assertArrayEquals(payload, decoder.readFully(new byte[payload.length]));
  }

  @Test
  public void byteArrayWriteWithOffsetAndLengthRoundTrip() throws Exception {
    byte[] payload = new byte[] {4, 8, 15, 16, 23, 42};
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.write(payload, 2, 3));
    assertArrayEquals(new byte[] {15, 16, 23}, decoder.readFully(new byte[3]));
  }

  @Test
  public void primitiveArraysRoundTrip() throws Exception {
    boolean[] booleans = new boolean[] {true, false, true};
    byte[] bytes = new byte[] {2, 4, 6};
    char[] chars = new char[] {'a', 'b', 'ç'};
    double[] doubles = new double[] {1.5d, -2.5d};
    float[] floats = new float[] {3.5f, -4.5f};
    int[] ints = new int[] {1, 2, 3, 4};
    long[] longs = new long[] {5L, 6L};
    short[] shorts = new short[] {7, 8};
    String[] strings = new String[] {"alpha", null, "gamma"};

    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode(booleans);
      encoder.encode(bytes);
      encoder.encode(chars);
      encoder.encode(doubles);
      encoder.encode(floats);
      encoder.encode(ints);
      encoder.encode(longs);
      encoder.encode(shorts);
      encoder.encode(strings);
    });

    assertArrayEquals(booleans, decoder.decodeBooleanArray());
    assertArrayEquals(bytes, decoder.decodeByteArray());
    assertArrayEquals(chars, decoder.decodeCharArray());
    assertArrayEquals(doubles, decoder.decodeDoubleArray(), 0.0d);
    assertArrayEquals(floats, decoder.decodeFloatArray(), 0.0f);
    assertArrayEquals(ints, decoder.decodeIntArray());
    assertArrayEquals(longs, decoder.decodeLongArray());
    assertArrayEquals(shorts, decoder.decodeShortArray());
    assertArrayEquals(strings, decoder.decodeStringArray());
  }

  @Test
  public void enumAndUuidRoundTrip() throws Exception {
    UUID uuid = UUID.randomUUID();
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode(SampleEnum.BETA);
      encoder.encode(uuid);
      encoder.encode(new SampleEnum[] {SampleEnum.ALPHA, null, SampleEnum.GAMMA});
      encoder.encode(new UUID[] {uuid, null});
    });

    assertEquals(SampleEnum.BETA, decoder.<SampleEnum>decodeEnum());
    assertEquals(uuid, decoder.decodeId());
    assertArrayEquals(new SampleEnum[] {SampleEnum.ALPHA, null, SampleEnum.GAMMA}, decoder.decodeEnumArray(SampleEnum.class));
    assertArrayEquals(new UUID[] {uuid, null}, decoder.decodeIdArray());
  }

  @Test
  public void nullSingularValuesRoundTrip() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode((String) null);
      encoder.encode((Enum<?>) null);
      encoder.encode((UUID) null);
      encoder.encode((BinaryEncodableAndDecodable) null);
      encoder.encode((ReferenceableBinaryEncodableAndDecodable) null, new HashMap<>());
    });

    assertNull(decoder.decodeString());
    assertNull(decoder.<SampleEnum>decodeEnum());
    assertNull(decoder.decodeId());
    assertNull(decoder.decodeBinaryEncodableAndDecodable());
    assertNull(decoder.decodeReferenceableBinaryEncodableAndDecodable(new HashMap<>()));
  }

  @Test(expected = NullPointerException.class)
  public void nullBooleanArrayEncodingDecodingThrows() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encode((boolean[]) null));
    decoder.decodeBooleanArray();
  }

  @Test(expected = NullPointerException.class)
  public void nullByteArrayEncodingThrowsDuringWrite() throws Exception {
    decoderFor(encoder -> encoder.encode((byte[]) null));
  }

  @Test(expected = NullPointerException.class)
  public void nullStringArrayEncodingDecodingThrows() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encode((String[]) null));
    decoder.decodeStringArray();
  }

  @Test
  public void binaryEncodableWithConstructorRoundTrips() throws Exception {
    ConstructorDecodedValue value = new ConstructorDecodedValue("constructor", 101);
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encode(value));
    ConstructorDecodedValue decoded = decoder.decodeBinaryEncodableAndDecodable();
    assertEquals(value, decoded);
  }

  @Test
  public void binaryEncodableWithDecodeMethodRoundTrips() throws Exception {
    MethodDecodedValue value = new MethodDecodedValue("method", 202);
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encode(value));
    MethodDecodedValue decoded = decoder.decodeBinaryEncodableAndDecodable();
    assertEquals(value, decoded);
  }

  @Test
  public void binaryEncodableWithContextUsesContextConstructor() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encode(new ContextDecodedValue("prefix", "payload")));
    ContextDecodedValue decoded = decoder.decodeBinaryEncodableAndDecodable("ctx");
    assertEquals("ctx:payload", decoded.getCombinedText());
  }

  @Test
  public void binaryEncodableArrayRoundTrips() throws Exception {
    ConstructorDecodedValue[] values = new ConstructorDecodedValue[] {
        new ConstructorDecodedValue("one", 1),
        null,
        new ConstructorDecodedValue("two", 2)
    };
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encode(values));
    ConstructorDecodedValue[] decoded = decoder.decodeBinaryEncodableAndDecodableArray(ConstructorDecodedValue.class);
    assertEquals(values.length, decoded.length);
    assertEquals(values[0], decoded[0]);
    assertNull(decoded[1]);
    assertEquals(values[2], decoded[2]);
  }

  @Test
  public void referenceableValueRoundTripsAndPreservesIdentity() throws Exception {
    ReferenceValue shared = new ReferenceValue("shared");
    Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodingMap = new HashMap<>();
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode(shared, encodingMap);
      encoder.encode(shared, encodingMap);
    });

    Map<Integer, ReferenceableBinaryEncodableAndDecodable> decodingMap = new HashMap<>();
    ReferenceValue first = decoder.decodeReferenceableBinaryEncodableAndDecodable(decodingMap);
    ReferenceValue second = decoder.decodeReferenceableBinaryEncodableAndDecodable(decodingMap);
    assertSame(first, second);
    assertEquals("shared", first.getText());
  }

  @Test
  public void referenceableArrayRoundTripsAndPreservesDuplicateEntries() throws Exception {
    ReferenceValue shared = new ReferenceValue("shared");
    ReferenceValue unique = new ReferenceValue("unique");
    Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodingMap = new HashMap<>();
    InputStreamBinaryDecoder decoder = decoderFor(encoder ->
        encoder.encode(new ReferenceValue[] {shared, unique, shared}, encodingMap));

    Map<Integer, ReferenceableBinaryEncodableAndDecodable> decodingMap = new HashMap<>();
    ReferenceValue[] decoded = decoder.decodeReferenceableBinaryEncodableAndDecodableArray(ReferenceValue.class, decodingMap);
    assertEquals(3, decoded.length);
    assertEquals("shared", decoded[0].getText());
    assertEquals("unique", decoded[1].getText());
    assertSame(decoded[0], decoded[2]);
  }

  @Test
  public void decodeRecordReadsSerializedRecordAfterTypeName() throws Exception {
    SampleRecord record = new SampleRecord("record", 33);
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encodeRecord(record));
    assertEquals(SampleRecord.class.getName(), decoder.decodeString());
    assertEquals(record, decoder.<SampleRecord>decodeRecord());
  }

  @Test
  public void decoderCanBeConstructedFromFile() throws Exception {
    File file = fileFor(encoder -> encoder.encode(1234), "binary-decoder-file.bin");
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(file);
    assertEquals(1234, decoder.decodeInt());
  }

  @Test
  public void decoderCanBeConstructedFromPath() throws Exception {
    File file = fileFor(encoder -> encoder.encode("path-value"), "binary-decoder-path.bin");
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(file.getAbsolutePath());
    assertEquals("path-value", decoder.decodeString());
  }

  @Test
  public void byteArrayEncoderCreatesWorkingDecoder() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encode(55);
    encoder.encode("fifty-five");
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(55, decoder.decodeInt());
    assertEquals("fifty-five", decoder.decodeString());
  }
}
