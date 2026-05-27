package edu.cmu.cs.dennisc.codec;

import org.junit.Test;

import java.nio.*;

import static org.junit.Assert.*;

public class BufferUtilitiesBehaviorTest {
  @Test
  public void byteBufferEncodingCurrentlyCannotBeDecodedWithoutThrowing() {
    ByteBuffer original = ByteBuffer.allocateDirect(4);
    original.put(new byte[] {9, 8, 7, 6});
    original.flip();

    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    BufferUtilities.encode(encoder, original.asReadOnlyBuffer());

    try {
      BufferUtilities.decodeByteBuffer(encoder.createDecoder());
      fail("Expected byte-buffer decoding to throw");
    } catch (RuntimeException expected) {
      assertNotNull(expected.getCause());
    }
  }

  @Test
  public void scalarBufferRoundTripsPreserveValuesAcrossPrimitiveWidths() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    BufferUtilities.encode(encoder, ShortBuffer.wrap(new short[] {1, 2, 3}));
    BufferUtilities.encode(encoder, LongBuffer.wrap(new long[] {4L, 5L}));
    BufferUtilities.encode(encoder, FloatBuffer.wrap(new float[] {1.25f, 2.5f}));
    BufferUtilities.encode(encoder, DoubleBuffer.wrap(new double[] {3.75, 4.5}));

    BinaryDecoder decoder = encoder.createDecoder();
    assertArrayEquals(new short[] {1, 2, 3}, toShortArray(BufferUtilities.decodeShortBuffer(decoder)));
    assertArrayEquals(new long[] {4L, 5L}, toLongArray(BufferUtilities.decodeLongBuffer(decoder)));
    assertArrayEquals(new float[] {1.25f, 2.5f}, toFloatArray(BufferUtilities.decodeFloatBuffer(decoder)), 0.0f);
    assertArrayEquals(new double[] {3.75, 4.5}, toDoubleArray(BufferUtilities.decodeDoubleBuffer(decoder)), 0.0);
  }

  @Test
  public void encodeNativeOptionalStillDecodesIntoAnEquivalentIntBuffer() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    BufferUtilities.encodeNativeOptional(encoder, IntBuffer.wrap(new int[] {11, 12, 13}));

    IntBuffer decoded = BufferUtilities.decodeIntBuffer(encoder.createDecoder());

    assertArrayEquals(new int[] {11, 12, 13}, toIntArray(decoded));
  }

  private static short[] toShortArray(ShortBuffer buffer) {
    ShortBuffer copy = buffer.duplicate();
    copy.rewind();
    short[] values = new short[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static int[] toIntArray(IntBuffer buffer) {
    IntBuffer copy = buffer.duplicate();
    copy.rewind();
    int[] values = new int[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static long[] toLongArray(LongBuffer buffer) {
    LongBuffer copy = buffer.duplicate();
    copy.rewind();
    long[] values = new long[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static float[] toFloatArray(FloatBuffer buffer) {
    FloatBuffer copy = buffer.duplicate();
    copy.rewind();
    float[] values = new float[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static double[] toDoubleArray(DoubleBuffer buffer) {
    DoubleBuffer copy = buffer.duplicate();
    copy.rewind();
    double[] values = new double[copy.remaining()];
    copy.get(values);
    return values;
  }
}
