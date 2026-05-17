package edu.cmu.cs.dennisc.codec;

import org.junit.Test;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.junit.Assert.*;

public class CodecRoundtripTest {

  private BinaryDecoder decoderFor(ByteArrayBinaryEncoder encoder) {
    return encoder.createDecoder();
  }

  @Test
  public void roundTrip_floatBuffer() {
    float[] data = {1.0f, 2.5f, -3.14f, 0.0f};
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    BufferUtilities.encode(encoder, FloatBuffer.wrap(data));

    BinaryDecoder decoder = decoderFor(encoder);
    FloatBuffer result = BufferUtilities.decodeFloatBuffer(decoder);
    assertNotNull(result);
    assertEquals(data.length, result.remaining());
    for (float expected : data) {
      assertEquals(expected, result.get(), 0.0001f);
    }
  }

  @Test
  public void roundTrip_intBuffer() {
    int[] data = {-1, 0, 1, Integer.MAX_VALUE, Integer.MIN_VALUE};
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    BufferUtilities.encode(encoder, IntBuffer.wrap(data));

    BinaryDecoder decoder = decoderFor(encoder);
    IntBuffer result = BufferUtilities.decodeIntBuffer(decoder);
    assertNotNull(result);
    assertEquals(data.length, result.remaining());
    for (int expected : data) {
      assertEquals(expected, result.get());
    }
  }

  @Test
  public void roundTrip_doubleBuffer() {
    double[] data = {1.0, 2.5, -3.14, 0.0, 999.999};
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    BufferUtilities.encode(encoder, DoubleBuffer.wrap(data));

    BinaryDecoder decoder = decoderFor(encoder);
    DoubleBuffer result = BufferUtilities.decodeDoubleBuffer(decoder);
    assertNotNull(result);
    assertEquals(data.length, result.remaining());
    for (double expected : data) {
      assertEquals(expected, result.get(), 0.0001);
    }
  }

  @Test
  public void roundTrip_emptyFloatBuffer() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    BufferUtilities.encode(encoder, FloatBuffer.wrap(new float[0]));

    BinaryDecoder decoder = decoderFor(encoder);
    FloatBuffer result = BufferUtilities.decodeFloatBuffer(decoder);
    assertNotNull(result);
    assertEquals(0, result.remaining());
  }

  @Test
  public void roundTrip_singleIntBuffer() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    BufferUtilities.encode(encoder, IntBuffer.wrap(new int[]{42}));

    BinaryDecoder decoder = decoderFor(encoder);
    IntBuffer result = BufferUtilities.decodeIntBuffer(decoder);
    assertNotNull(result);
    assertEquals(1, result.remaining());
    assertEquals(42, result.get());
  }

  @Test
  public void roundTrip_singleDoubleBuffer() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    BufferUtilities.encode(encoder, DoubleBuffer.wrap(new double[]{3.14159}));

    BinaryDecoder decoder = decoderFor(encoder);
    DoubleBuffer result = BufferUtilities.decodeDoubleBuffer(decoder);
    assertNotNull(result);
    assertEquals(1, result.remaining());
    assertEquals(3.14159, result.get(), 0.00001);
  }
}
