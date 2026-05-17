package edu.cmu.cs.dennisc.java.util;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import static org.junit.Assert.*;

/**
 * Tests for BufferUtilities — NIO buffer↔array conversions,
 * direct buffer creation, and buffer copying.
 */
public class BufferUtilitiesTest {

  private static final double DELTA = 0.0;

  // --- convertXxxBufferToArray ---

  @Test
  public void convertByteBufferToArray_heapBuffer() {
    byte[] original = {1, 2, 3, 4, 5};
    ByteBuffer buf = ByteBuffer.wrap(original);
    byte[] result = BufferUtilities.convertByteBufferToArray(buf);
    assertArrayEquals(original, result);
  }

  @Test
  public void convertByteBufferToArray_directBuffer() {
    byte[] original = {10, 20, 30};
    ByteBuffer buf = ByteBuffer.allocateDirect(3);
    buf.put(original);
    buf.flip();
    byte[] result = BufferUtilities.convertByteBufferToArray(buf);
    assertArrayEquals(original, result);
  }

  @Test
  public void convertByteBufferToArray_empty() {
    ByteBuffer buf = ByteBuffer.allocate(0);
    byte[] result = BufferUtilities.convertByteBufferToArray(buf);
    assertEquals(0, result.length);
  }

  @Test
  public void convertCharBufferToArray() {
    char[] original = {'a', 'b', 'c'};
    CharBuffer buf = CharBuffer.wrap(original);
    char[] result = BufferUtilities.convertCharBufferToArray(buf);
    assertArrayEquals(original, result);
  }

  @Test
  public void convertShortBufferToArray() {
    short[] original = {1, -1, Short.MAX_VALUE, Short.MIN_VALUE};
    ShortBuffer buf = ShortBuffer.wrap(original);
    short[] result = BufferUtilities.convertShortBufferToArray(buf);
    assertArrayEquals(original, result);
  }

  @Test
  public void convertIntBufferToArray() {
    int[] original = {0, 1, -1, Integer.MAX_VALUE};
    IntBuffer buf = IntBuffer.wrap(original);
    int[] result = BufferUtilities.convertIntBufferToArray(buf);
    assertArrayEquals(original, result);
  }

  @Test
  public void convertLongBufferToArray() {
    long[] original = {0L, Long.MAX_VALUE, Long.MIN_VALUE};
    LongBuffer buf = LongBuffer.wrap(original);
    long[] result = BufferUtilities.convertLongBufferToArray(buf);
    assertArrayEquals(original, result);
  }

  @Test
  public void convertFloatBufferToArray() {
    float[] original = {1.0f, -2.5f, Float.NaN, Float.POSITIVE_INFINITY};
    FloatBuffer buf = FloatBuffer.wrap(original);
    float[] result = BufferUtilities.convertFloatBufferToArray(buf);
    assertEquals(original.length, result.length);
    assertEquals(original[0], result[0], 0.0f);
    assertEquals(original[1], result[1], 0.0f);
    assertTrue(Float.isNaN(result[2]));
    assertEquals(Float.POSITIVE_INFINITY, result[3], 0.0f);
  }

  @Test
  public void convertDoubleBufferToArray() {
    double[] original = {3.14, -2.71, Double.NaN};
    DoubleBuffer buf = DoubleBuffer.wrap(original);
    double[] result = BufferUtilities.convertDoubleBufferToArray(buf);
    assertEquals(original.length, result.length);
    assertEquals(original[0], result[0], DELTA);
    assertEquals(original[1], result[1], DELTA);
    assertTrue(Double.isNaN(result[2]));
  }

  // --- createDirectXxxBuffer ---

  @Test
  public void createDirectDoubleBuffer() {
    double[] data = {1.0, 2.0, 3.0};
    DoubleBuffer buf = BufferUtilities.createDirectDoubleBuffer(data);
    assertNotNull(buf);
    assertTrue(buf.isDirect());
    assertEquals(3, buf.capacity());
    assertEquals(1.0, buf.get(0), DELTA);
    assertEquals(3.0, buf.get(2), DELTA);
  }

  @Test
  public void createDirectFloatBuffer() {
    float[] data = {1.5f, 2.5f};
    FloatBuffer buf = BufferUtilities.createDirectFloatBuffer(data);
    assertNotNull(buf);
    assertTrue(buf.isDirect());
    assertEquals(2, buf.capacity());
    assertEquals(1.5f, buf.get(0), 0.0f);
  }

  @Test
  public void createDirectIntBuffer() {
    int[] data = {10, 20, 30, 40};
    IntBuffer buf = BufferUtilities.createDirectIntBuffer(data);
    assertNotNull(buf);
    assertTrue(buf.isDirect());
    assertEquals(4, buf.capacity());
    assertEquals(10, buf.get(0));
    assertEquals(40, buf.get(3));
  }

  @Test
  public void createDirectLongBuffer() {
    long[] data = {100L, 200L};
    LongBuffer buf = BufferUtilities.createDirectLongBuffer(data);
    assertNotNull(buf);
    assertTrue(buf.isDirect());
    assertEquals(100L, buf.get(0));
  }

  @Test
  public void createDirectByteBuffer() {
    byte[] data = {1, 2, 3};
    ByteBuffer buf = BufferUtilities.createDirectByteBuffer(data);
    assertNotNull(buf);
    assertTrue(buf.isDirect());
    assertEquals(3, buf.capacity());
  }

  @Test
  public void createDirectCharBuffer() {
    char[] data = {'x', 'y'};
    CharBuffer buf = BufferUtilities.createDirectCharBuffer(data);
    assertNotNull(buf);
    assertTrue(buf.isDirect());
    assertEquals('x', buf.get(0));
  }

  @Test
  public void createDirectShortBuffer() {
    short[] data = {1, 2, 3};
    ShortBuffer buf = BufferUtilities.createDirectShortBuffer(data);
    assertNotNull(buf);
    assertTrue(buf.isDirect());
    assertEquals(3, buf.capacity());
  }

  // --- copyXxxBuffer ---

  @Test
  public void copyDoubleBuffer() {
    double[] data = {1.0, 2.0, 3.0};
    DoubleBuffer original = DoubleBuffer.wrap(data);
    DoubleBuffer copy = BufferUtilities.copyDoubleBuffer(original);
    assertNotNull(copy);
    assertNotSame(original, copy);
    assertEquals(original.capacity(), copy.capacity());
    for (int i = 0; i < data.length; i++) {
      assertEquals(data[i], copy.get(i), DELTA);
    }
  }

  @Test
  public void copyFloatBuffer() {
    float[] data = {1.5f, 2.5f};
    FloatBuffer original = FloatBuffer.wrap(data);
    FloatBuffer copy = BufferUtilities.copyFloatBuffer(original);
    assertNotNull(copy);
    assertNotSame(original, copy);
    assertEquals(1.5f, copy.get(0), 0.0f);
  }

  @Test
  public void copyIntBuffer() {
    int[] data = {100, 200};
    IntBuffer original = IntBuffer.wrap(data);
    IntBuffer copy = BufferUtilities.copyIntBuffer(original);
    assertNotNull(copy);
    assertEquals(100, copy.get(0));
    assertEquals(200, copy.get(1));
  }

  @Test
  public void copyLongBuffer() {
    long[] data = {999L, -999L};
    LongBuffer original = LongBuffer.wrap(data);
    LongBuffer copy = BufferUtilities.copyLongBuffer(original);
    assertNotNull(copy);
    assertEquals(999L, copy.get(0));
    assertEquals(-999L, copy.get(1));
  }

  @Test
  public void copyByteBuffer() {
    byte[] data = {5, 10, 15};
    ByteBuffer original = ByteBuffer.wrap(data);
    ByteBuffer copy = BufferUtilities.copyByteBuffer(original);
    assertNotNull(copy);
    assertEquals(3, copy.capacity());
  }

  @Test
  public void copyCharBuffer() {
    char[] data = {'a', 'b'};
    CharBuffer original = CharBuffer.wrap(data);
    CharBuffer copy = BufferUtilities.copyCharBuffer(original);
    assertNotNull(copy);
    assertEquals('a', copy.get(0));
  }

  @Test
  public void copyShortBuffer() {
    short[] data = {1, 2};
    ShortBuffer original = ShortBuffer.wrap(data);
    ShortBuffer copy = BufferUtilities.copyShortBuffer(original);
    assertNotNull(copy);
    assertEquals((short) 1, copy.get(0));
  }

  // --- Mutation isolation: changes to copy don't affect original ---

  @Test
  public void copyDoubleBuffer_isolatedFromOriginal() {
    double[] data = {1.0, 2.0};
    DoubleBuffer original = BufferUtilities.createDirectDoubleBuffer(data);
    DoubleBuffer copy = BufferUtilities.copyDoubleBuffer(original);
    copy.put(0, 99.0);
    assertEquals(1.0, original.get(0), DELTA);
    assertEquals(99.0, copy.get(0), DELTA);
  }

  @Test
  public void copyIntBuffer_isolatedFromOriginal() {
    int[] data = {10, 20};
    IntBuffer original = BufferUtilities.createDirectIntBuffer(data);
    IntBuffer copy = BufferUtilities.copyIntBuffer(original);
    copy.put(0, 999);
    assertEquals(10, original.get(0));
    assertEquals(999, copy.get(0));
  }

  // --- Null handling ---

  @Test
  public void copyDoubleBuffer_null() {
    DoubleBuffer result = BufferUtilities.copyDoubleBuffer(null);
    assertNull(result);
  }

  @Test
  public void copyFloatBuffer_null() {
    FloatBuffer result = BufferUtilities.copyFloatBuffer(null);
    assertNull(result);
  }

  @Test
  public void copyIntBuffer_null() {
    IntBuffer result = BufferUtilities.copyIntBuffer(null);
    assertNull(result);
  }

  @Test
  public void copyLongBuffer_null() {
    LongBuffer result = BufferUtilities.copyLongBuffer(null);
    assertNull(result);
  }

  @Test
  public void copyByteBuffer_null() {
    ByteBuffer result = BufferUtilities.copyByteBuffer(null);
    assertNull(result);
  }

  @Test
  public void copyCharBuffer_null() {
    CharBuffer result = BufferUtilities.copyCharBuffer(null);
    assertNull(result);
  }

  @Test
  public void copyShortBuffer_null() {
    ShortBuffer result = BufferUtilities.copyShortBuffer(null);
    assertNull(result);
  }

  // --- Round-trip: array→direct buffer→array ---

  @Test
  public void roundTrip_doubleArrayThroughDirectBuffer() {
    double[] original = {1.1, 2.2, 3.3};
    DoubleBuffer buf = BufferUtilities.createDirectDoubleBuffer(original);
    double[] result = BufferUtilities.convertDoubleBufferToArray(buf);
    assertArrayEquals(original, result, DELTA);
  }

  @Test
  public void roundTrip_floatArrayThroughDirectBuffer() {
    float[] original = {1.1f, 2.2f};
    FloatBuffer buf = BufferUtilities.createDirectFloatBuffer(original);
    float[] result = BufferUtilities.convertFloatBufferToArray(buf);
    assertArrayEquals(original, result, 0.0f);
  }

  @Test
  public void roundTrip_intArrayThroughDirectBuffer() {
    int[] original = {7, 14, 21};
    IntBuffer buf = BufferUtilities.createDirectIntBuffer(original);
    int[] result = BufferUtilities.convertIntBufferToArray(buf);
    assertArrayEquals(original, result);
  }
}
