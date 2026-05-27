package edu.cmu.cs.dennisc.texture;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.codec.BinaryEncodableAndDecodable;
import edu.cmu.cs.dennisc.codec.ReferenceableBinaryEncodableAndDecodable;
import edu.cmu.cs.dennisc.property.InstancePropertyOwner;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TextureCoordinate2fBehaviorTest {

  @Test
  public void copyConstructorAndEqualityPreserveCoordinates() {
    TextureCoordinate2f original = new TextureCoordinate2f(0.25f, 0.75f);
    TextureCoordinate2f copy = new TextureCoordinate2f(original);

    assertEquals(original, copy);
    assertEquals(original.hashCode(), copy.hashCode());
    assertNotEquals(original, new TextureCoordinate2f(0.25f, 0.5f));
  }

  @Test
  public void createNaNProducesNaNCoordinate() {
    TextureCoordinate2f coordinate = TextureCoordinate2f.createNaN();

    assertTrue(coordinate.isNaN());
  }

  @Test
  public void binaryDecoderConstructorReadsFloatsInOrder() {
    TextureCoordinate2f coordinate = new TextureCoordinate2f(new StubBinaryDecoder(1.5f, -2.25f));

    assertEquals(1.5f, coordinate.u, 0.0f);
    assertEquals(-2.25f, coordinate.v, 0.0f);
  }

  @Test
  public void encodeWritesBothCoordinates() {
    TextureCoordinate2f coordinate = new TextureCoordinate2f(3.0f, 4.5f);
    RecordingBinaryEncoder encoder = new RecordingBinaryEncoder();

    coordinate.encode(encoder);

    assertEquals(java.util.Arrays.asList(3.0f, 4.5f), encoder.floats);
  }

  private static final class StubBinaryDecoder implements BinaryDecoder {
    private final float[] values;
    private int index;

    private StubBinaryDecoder(float... values) {
      this.values = values;
    }

    @Override public float decodeFloat() { return values[index++]; }
    @Override public byte[] readFully(byte[] rv) { throw new UnsupportedOperationException(); }
    @Override public boolean decodeBoolean() { throw new UnsupportedOperationException(); }
    @Override public byte decodeByte() { throw new UnsupportedOperationException(); }
    @Override public char decodeChar() { throw new UnsupportedOperationException(); }
    @Override public double decodeDouble() { throw new UnsupportedOperationException(); }
    @Override public int decodeInt() { throw new UnsupportedOperationException(); }
    @Override public long decodeLong() { throw new UnsupportedOperationException(); }
    @Override public short decodeShort() { throw new UnsupportedOperationException(); }
    @Override public String decodeString() { throw new UnsupportedOperationException(); }
    @Override public <E extends Enum<E>> E decodeEnum() { throw new UnsupportedOperationException(); }
    @Override public UUID decodeId() { throw new UnsupportedOperationException(); }
    @Override public <E extends BinaryEncodableAndDecodable> E decodeBinaryEncodableAndDecodable() { throw new UnsupportedOperationException(); }
    @Override public <E extends BinaryEncodableAndDecodable> E decodeBinaryEncodableAndDecodable(Object context) { throw new UnsupportedOperationException(); }
    @Override public <E extends ReferenceableBinaryEncodableAndDecodable> E decodeReferenceableBinaryEncodableAndDecodable(Map<Integer, ReferenceableBinaryEncodableAndDecodable> map) { throw new UnsupportedOperationException(); }
    @Override public boolean[] decodeBooleanArray() { throw new UnsupportedOperationException(); }
    @Override public byte[] decodeByteArray() { throw new UnsupportedOperationException(); }
    @Override public char[] decodeCharArray() { throw new UnsupportedOperationException(); }
    @Override public double[] decodeDoubleArray() { throw new UnsupportedOperationException(); }
    @Override public float[] decodeFloatArray() { throw new UnsupportedOperationException(); }
    @Override public int[] decodeIntArray() { throw new UnsupportedOperationException(); }
    @Override public long[] decodeLongArray() { throw new UnsupportedOperationException(); }
    @Override public short[] decodeShortArray() { throw new UnsupportedOperationException(); }
    @Override public String[] decodeStringArray() { throw new UnsupportedOperationException(); }
    @Override public <E extends Enum<E>> E[] decodeEnumArray(Class<E> cls) { throw new UnsupportedOperationException(); }
    @Override public UUID[] decodeIdArray() { throw new UnsupportedOperationException(); }
    @Override public <E extends BinaryEncodableAndDecodable> E[] decodeBinaryEncodableAndDecodableArray(Class<E> componentCls) { throw new UnsupportedOperationException(); }
    @Override public <E extends ReferenceableBinaryEncodableAndDecodable> E[] decodeReferenceableBinaryEncodableAndDecodableArray(Class<E> componentCls, Map<Integer, ReferenceableBinaryEncodableAndDecodable> map) { throw new UnsupportedOperationException(); }
    @Override public void decodeProperties(InstancePropertyOwner owner, Map<Integer, ReferenceableBinaryEncodableAndDecodable> map) { throw new UnsupportedOperationException(); }
    @Override public <C> C decodeRecord() { throw new UnsupportedOperationException(); }
  }

  private static final class RecordingBinaryEncoder implements BinaryEncoder {
    private final List<Float> floats = new ArrayList<Float>();

    @Override public void encode(float value) { floats.add(value); }
    @Override public void write(byte[] data) { throw new UnsupportedOperationException(); }
    @Override public void write(byte[] data, int offset, int length) { throw new UnsupportedOperationException(); }
    @Override public void encode(boolean value) { throw new UnsupportedOperationException(); }
    @Override public void encode(byte value) { throw new UnsupportedOperationException(); }
    @Override public void encode(char value) { throw new UnsupportedOperationException(); }
    @Override public void encode(double value) { throw new UnsupportedOperationException(); }
    @Override public void encode(int value) { throw new UnsupportedOperationException(); }
    @Override public void encode(long value) { throw new UnsupportedOperationException(); }
    @Override public void encode(short value) { throw new UnsupportedOperationException(); }
    @Override public void encode(String value) { throw new UnsupportedOperationException(); }
    @Override public void encode(Enum<?> value) { throw new UnsupportedOperationException(); }
    @Override public void encode(UUID value) { throw new UnsupportedOperationException(); }
    @Override public void encode(BinaryEncodableAndDecodable value) { throw new UnsupportedOperationException(); }
    @Override public void encode(ReferenceableBinaryEncodableAndDecodable value, Map<ReferenceableBinaryEncodableAndDecodable, Integer> map) { throw new UnsupportedOperationException(); }
    @Override public void encode(boolean[] array) { throw new UnsupportedOperationException(); }
    @Override public void encode(byte[] array) { throw new UnsupportedOperationException(); }
    @Override public void encode(char[] array) { throw new UnsupportedOperationException(); }
    @Override public void encode(double[] array) { throw new UnsupportedOperationException(); }
    @Override public void encode(float[] array) { throw new UnsupportedOperationException(); }
    @Override public void encode(int[] array) { throw new UnsupportedOperationException(); }
    @Override public void encode(long[] array) { throw new UnsupportedOperationException(); }
    @Override public void encode(short[] array) { throw new UnsupportedOperationException(); }
    @Override public void encode(String[] array) { throw new UnsupportedOperationException(); }
    @Override public void encode(Enum<?>[] array) { throw new UnsupportedOperationException(); }
    @Override public void encode(UUID[] array) { throw new UnsupportedOperationException(); }
    @Override public void encode(BinaryEncodableAndDecodable[] array) { throw new UnsupportedOperationException(); }
    @Override public void encode(ReferenceableBinaryEncodableAndDecodable[] array, Map<ReferenceableBinaryEncodableAndDecodable, Integer> map) { throw new UnsupportedOperationException(); }
    @Override public void flush() { }
    @Override public void encodeProperties(InstancePropertyOwner owner, Map<ReferenceableBinaryEncodableAndDecodable, Integer> map) { throw new UnsupportedOperationException(); }
    @Override public void encodeRecord(Record record) { throw new UnsupportedOperationException(); }
  }
}
