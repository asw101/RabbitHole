package edu.cmu.cs.dennisc.codec;

import edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner;
import edu.cmu.cs.dennisc.property.InstanceProperty;
import org.alice.math.immutable.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class InputStreamBinaryDecoderTest {
  private interface EncoderAction {
    void encode(OutputStreamBinaryEncoder encoder) throws Exception;
  }

  private static final File BASE_DIRECTORY = new File("target/test-artifacts/InputStreamBinaryDecoderTest");

  private InputStreamBinaryDecoder decoderFor(EncoderAction action) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    action.encode(encoder);
    encoder.flush();
    return new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
  }

  private File encodedFile(String name, EncoderAction action) throws Exception {
    if (!BASE_DIRECTORY.exists()) {
      BASE_DIRECTORY.mkdirs();
    }
    File file = new File(BASE_DIRECTORY, name);
    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(outputStream);
      action.encode(encoder);
      encoder.flush();
    }
    return file;
  }

  public enum SampleEnum {
    ALPHA,
    BETA,
    GAMMA
  }

  public static final class ConstructorDecodedValue implements BinaryEncodableAndDecodable {
    private final String text;
    private final int number;

    public ConstructorDecodedValue(String text, int number) {
      this.text = text;
      this.number = number;
    }

    public ConstructorDecodedValue(BinaryDecoder decoder) {
      this(decoder.decodeString(), decoder.decodeInt());
    }

    public String getText() {
      return this.text;
    }

    public int getNumber() {
      return this.number;
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(this.text);
      binaryEncoder.encode(this.number);
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

    public String getText() {
      return this.text;
    }

    public int getNumber() {
      return this.number;
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(this.text);
      binaryEncoder.encode(this.number);
    }
  }

  public static final class ContextDecodedValue implements BinaryEncodableAndDecodable {
    private final Object context;
    private final String payload;

    public ContextDecodedValue(String payload) {
      this.context = null;
      this.payload = payload;
    }

    public ContextDecodedValue(BinaryDecoder decoder, Object context) {
      this.context = context;
      this.payload = decoder.decodeString();
    }

    public String getCombinedText() {
      return this.context + ":" + this.payload;
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(this.payload);
    }
  }

  private record SampleRecord(String text, int number) implements Serializable {
  }

  @Test
  public void constructorWithInputStreamDecodesPrimitiveSequence() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode(true);
      encoder.encode(42);
      encoder.encode("decoder");
    });

    assertTrue(decoder.decodeBoolean());
    assertEquals(42, decoder.decodeInt());
    assertEquals("decoder", decoder.decodeString());
  }

  @Test
  public void constructorWithFileDecodesString() throws Exception {
    File file = encodedFile("string.bin", encoder -> encoder.encode("from-file"));
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(file);
    assertEquals("from-file", decoder.decodeString());
  }

  @Test
  public void constructorWithStringPathDecodesUuid() throws Exception {
    UUID expected = UUID.randomUUID();
    File file = encodedFile("uuid.bin", encoder -> encoder.encode(expected));
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(file.getAbsolutePath());
    assertEquals(expected, decoder.decodeId());
  }

  @Test
  public void readFullyReadsWrittenBytes() throws Exception {
    byte[] expected = new byte[] {3, 1, 4, 1, 5, 9};
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.write(expected));
    assertArrayEquals(expected, decoder.readFully(new byte[expected.length]));
  }

  @Test
  public void decodeRecordReturnsSerializedRecord() throws Exception {
    SampleRecord expected = new SampleRecord("record", 77);
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encodeRecord(expected));

    assertEquals(SampleRecord.class.getName(), decoder.decodeString());
    assertEquals(expected, decoder.decodeRecord());
  }

  @Test
  public void decodeBinaryEncodableUsesBinaryDecoderConstructor() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encode(new ConstructorDecodedValue("alpha", 12)));
    ConstructorDecodedValue value = decoder.decodeBinaryEncodableAndDecodable();

    assertEquals("alpha", value.getText());
    assertEquals(12, value.getNumber());
  }

  @Test
  public void decodeBinaryEncodableUsesDecodeMethodFallback() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encode(new MethodDecodedValue("beta", 34)));
    MethodDecodedValue value = decoder.decodeBinaryEncodableAndDecodable();

    assertEquals("beta", value.getText());
    assertEquals(34, value.getNumber());
  }

  @Test
  public void decodeBinaryEncodableWithContextUsesContextConstructor() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encode(new ContextDecodedValue("payload")));
    ContextDecodedValue value = decoder.decodeBinaryEncodableAndDecodable("prefix");

    assertEquals("prefix:payload", value.getCombinedText());
  }

  @Test
  public void decodeEnumAndArraysRoundTrip() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode(SampleEnum.GAMMA);
      encoder.encode(new int[] {7, 8, 9});
      encoder.encode(new String[] {"a", "b"});
    });

    assertEquals(SampleEnum.GAMMA, decoder.<SampleEnum>decodeEnum());
    assertArrayEquals(new int[] {7, 8, 9}, decoder.decodeIntArray());
    assertArrayEquals(new String[] {"a", "b"}, decoder.decodeStringArray());
  }

  @Test
  public void readFullyReadsBytesWrittenWithOffsetAndLength() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.write(new byte[] {9, 8, 7, 6}, 1, 2));

    assertArrayEquals(new byte[] {8, 7}, decoder.readFully(new byte[2]));
  }

  @Test
  public void decodeBinaryEncodableSupportsRecordImplementations() throws Exception {
    BinaryRecord expected = new BinaryRecord("record", 91);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    java.io.ObjectOutputStream objectOutputStream = new java.io.ObjectOutputStream(baos);
    objectOutputStream.writeBoolean(true);
    objectOutputStream.writeUTF(BinaryRecord.class.getName());
    objectOutputStream.writeObject(expected);
    objectOutputStream.flush();
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));

    BinaryRecord actual = decoder.decodeBinaryEncodableAndDecodable();

    assertEquals(expected, actual);
  }

  @Test
  public void decodeReferenceableValuesReuseExistingInstances() throws Exception {
    SimpleReferenceableValue shared = new SimpleReferenceableValue("shared");
    Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodeMap = new HashMap<>();
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode(shared, encodeMap);
      encoder.encode(shared, encodeMap);
    });

    Map<Integer, ReferenceableBinaryEncodableAndDecodable> decodeMap = new HashMap<>();
    SimpleReferenceableValue first = decoder.decodeReferenceableBinaryEncodableAndDecodable(decodeMap);
    SimpleReferenceableValue second = decoder.decodeReferenceableBinaryEncodableAndDecodable(decodeMap);

    assertSame(first, second);
    assertEquals("shared", first.text);
  }

  @Test
  public void encodeAndDecodePropertiesRoundTripMultipleValueKinds() throws Exception {
    SimpleReferenceableValue shared = new SimpleReferenceableValue("shared");
    PropertyOwner source = PropertyOwner.createPopulated(shared);
    source.binaryArrayValue.setValue(null);
    source.referenceableArrayValue.setValue(null);
    source.objectArrayValue.setValue(null);
    source.byteBufferValue.setValue(null);
    source.charBufferValue.setValue(null);
    source.shortBufferValue.setValue(null);
    source.intBufferValue.setValue(null);
    source.longBufferValue.setValue(null);
    source.floatBufferValue.setValue(null);
    source.doubleBufferValue.setValue(null);
    source.binaryValue.setValue(null);
    PropertyOwner target = new PropertyOwner();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);

    encoder.encodeProperties(source, new HashMap<ReferenceableBinaryEncodableAndDecodable, Integer>());
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
    decoder.decodeProperties(target, new HashMap<Integer, ReferenceableBinaryEncodableAndDecodable>());

    assertEquals(Boolean.TRUE, target.booleanValue.getValue());
    assertEquals(Byte.valueOf((byte) 3), target.byteValue.getValue());
    assertEquals(Character.valueOf('z'), target.charValue.getValue());
    assertEquals(Double.valueOf(1.25), target.doubleValue.getValue());
    assertEquals(Float.valueOf(2.5f), target.floatValue.getValue());
    assertEquals(Integer.valueOf(7), target.integerValue.getValue());
    assertEquals(Long.valueOf(9L), target.longValue.getValue());
    assertEquals(Short.valueOf((short) 11), target.shortValue.getValue());
    assertEquals("hello", target.stringValue.getValue());
    assertEquals(SampleEnum.BETA, target.enumValue.getValue());
    assertArrayEquals(new int[] {1, 2, 3}, (int[]) target.intArrayValue.getValue());
    assertArrayEquals(new String[] {"x", "y"}, (String[]) target.stringArrayValue.getValue());
    assertArrayEquals(new SampleEnum[] {SampleEnum.ALPHA, SampleEnum.GAMMA}, (SampleEnum[]) target.enumArrayValue.getValue());
    assertNull(target.binaryArrayValue.getValue());
    assertNull(target.referenceableArrayValue.getValue());
    assertNull(target.objectArrayValue.getValue());
    assertNull(target.byteBufferValue.getValue());
    assertNull(target.charBufferValue.getValue());
    assertNull(target.shortBufferValue.getValue());
    assertNull(target.intBufferValue.getValue());
    assertNull(target.longBufferValue.getValue());
    assertNull(target.floatBufferValue.getValue());
    assertNull(target.doubleBufferValue.getValue());
    assertNull(target.binaryValue.getValue());
    assertNull(target.nullValue.getValue());
  }

  @Test
  public void decodeBinaryEncodableSupportsLegacyMathClassNames() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode("edu.cmu.cs.dennisc.math.EulerAngles");
      encoder.encode(1.0);
      encoder.encode(2.0);
      encoder.encode(3.0);
      encoder.encode(EulerAngles.Order.PITCH_YAW_ROLL);
      encoder.encode("edu.cmu.cs.dennisc.math.Matrix3x3");
      encoder.encode(1.0);
      encoder.encode(0.0);
      encoder.encode(0.0);
      encoder.encode(0.0);
      encoder.encode(1.0);
      encoder.encode(0.0);
      encoder.encode(0.0);
      encoder.encode(0.0);
      encoder.encode(1.0);
      encoder.encode("edu.cmu.cs.dennisc.math.AxisAlignedBox");
      encoder.encode(1.0);
      encoder.encode(2.0);
      encoder.encode(3.0);
      encoder.encode(4.0);
      encoder.encode(5.0);
      encoder.encode(6.0);
      encoder.encode("edu.cmu.cs.dennisc.math.AffineMatrix4x4");
      encoder.encode(1.0);
      encoder.encode(0.0);
      encoder.encode(0.0);
      encoder.encode(0.0);
      encoder.encode(1.0);
      encoder.encode(0.0);
      encoder.encode(0.0);
      encoder.encode(0.0);
      encoder.encode(1.0);
      encoder.encode(7.0);
      encoder.encode(8.0);
      encoder.encode(9.0);
      encoder.encode("edu.cmu.cs.dennisc.math.Vector3f");
      encoder.encode(1.5f);
      encoder.encode(2.5f);
      encoder.encode(3.5f);
    });

    EulerAngles eulerAngles = decoder.decodeBinaryEncodableAndDecodable();
    Matrix3x3 matrix = decoder.decodeBinaryEncodableAndDecodable();
    AxisAlignedBox axisAlignedBox = decoder.decodeBinaryEncodableAndDecodable();
    AffineMatrix4x4 affineMatrix4x4 = decoder.decodeBinaryEncodableAndDecodable();
    Vector3f vector3f = decoder.decodeBinaryEncodableAndDecodable();

    assertEquals(1.0, eulerAngles.pitch().getAsRadians(), 0.0);
    assertEquals(EulerAngles.Order.PITCH_YAW_ROLL, eulerAngles.order());
    assertNotNull(matrix);
    assertEquals(1.0, axisAlignedBox.minimum().x(), 0.0);
    assertEquals(9.0, affineMatrix4x4.translation().z(), 0.0);
    assertEquals(1.5f, vector3f.x(), 0.0f);
  }

  private static byte[] byteArray(ByteBuffer buffer) {
    ByteBuffer copy = buffer.duplicate();
    byte[] values = new byte[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static char[] charArray(CharBuffer buffer) {
    CharBuffer copy = buffer.duplicate();
    char[] values = new char[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static short[] shortArray(ShortBuffer buffer) {
    ShortBuffer copy = buffer.duplicate();
    short[] values = new short[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static int[] intArray(IntBuffer buffer) {
    IntBuffer copy = buffer.duplicate();
    int[] values = new int[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static long[] longArray(LongBuffer buffer) {
    LongBuffer copy = buffer.duplicate();
    long[] values = new long[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static float[] floatArray(FloatBuffer buffer) {
    FloatBuffer copy = buffer.duplicate();
    float[] values = new float[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static double[] doubleArray(DoubleBuffer buffer) {
    DoubleBuffer copy = buffer.duplicate();
    double[] values = new double[copy.remaining()];
    copy.get(values);
    return values;
  }

  private record BinaryRecord(String text, int number) implements BinaryEncodableAndDecodable, Serializable {
    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encodeRecord(this);
    }
  }

  public static final class SimpleReferenceableValue implements ReferenceableBinaryEncodableAndDecodable {
    private String text;

    public SimpleReferenceableValue() {
    }

    public SimpleReferenceableValue(String text) {
      this.text = text;
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

  public static final class PropertyOwner extends AbstractInstancePropertyOwner {
    public final InstanceProperty<Object> booleanValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> byteValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> charValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> doubleValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> floatValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> integerValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> longValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> shortValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> stringValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> enumValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> intArrayValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> stringArrayValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> enumArrayValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> binaryArrayValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> referenceableArrayValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> objectArrayValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> byteBufferValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> charBufferValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> shortBufferValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> intBufferValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> longBufferValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> floatBufferValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> doubleBufferValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> binaryValue = new InstanceProperty<>(this, null);
    public final InstanceProperty<Object> nullValue = new InstanceProperty<>(this, null);

    public static PropertyOwner createPopulated(SimpleReferenceableValue shared) {
      PropertyOwner owner = new PropertyOwner();
      owner.booleanValue.setValue(Boolean.TRUE);
      owner.byteValue.setValue((byte) 3);
      owner.charValue.setValue('z');
      owner.doubleValue.setValue(1.25);
      owner.floatValue.setValue(2.5f);
      owner.integerValue.setValue(7);
      owner.longValue.setValue(9L);
      owner.shortValue.setValue((short) 11);
      owner.stringValue.setValue("hello");
      owner.enumValue.setValue(SampleEnum.BETA);
      owner.intArrayValue.setValue(new int[] {1, 2, 3});
      owner.stringArrayValue.setValue(new String[] {"x", "y"});
      owner.enumArrayValue.setValue(new SampleEnum[] {SampleEnum.ALPHA, SampleEnum.GAMMA});
      owner.binaryArrayValue.setValue(new ConstructorDecodedValue[] {new ConstructorDecodedValue("array", 12)});
      owner.referenceableArrayValue.setValue(new SimpleReferenceableValue[] {shared, shared});
      owner.objectArrayValue.setValue(new Integer[] {4, 5});
      owner.byteBufferValue.setValue(ByteBuffer.wrap(new byte[] {6, 7}));
      owner.charBufferValue.setValue(CharBuffer.wrap(new char[] {'a', 'b'}));
      owner.shortBufferValue.setValue(ShortBuffer.wrap(new short[] {8, 9}));
      owner.intBufferValue.setValue(IntBuffer.wrap(new int[] {10, 11}));
      owner.longBufferValue.setValue(LongBuffer.wrap(new long[] {12L, 13L}));
      owner.floatBufferValue.setValue(FloatBuffer.wrap(new float[] {14.0f, 15.0f}));
      owner.doubleBufferValue.setValue(DoubleBuffer.wrap(new double[] {16.0, 17.0}));
      owner.binaryValue.setValue(new MethodDecodedValue("method", 44));
      owner.nullValue.setValue(null);
      return owner;
    }
  }
}
