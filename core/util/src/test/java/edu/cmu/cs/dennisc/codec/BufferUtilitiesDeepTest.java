package edu.cmu.cs.dennisc.codec;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.*;

import static org.junit.Assert.*;

public class BufferUtilitiesDeepTest {

  private OutputStreamBinaryEncoder createEncoder(ByteArrayOutputStream baos) {
    return new OutputStreamBinaryEncoder(baos);
  }

  private InputStreamBinaryDecoder createDecoder(byte[] data) {
    return new InputStreamBinaryDecoder(new ByteArrayInputStream(data));
  }

  @Test
  public void roundTrip_floatBuffer() throws Exception {
    FloatBuffer original = FloatBuffer.allocate(3);
    original.put(1.0f).put(2.0f).put(3.0f);
    original.rewind();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (OutputStreamBinaryEncoder enc = createEncoder(baos)) {
      BufferUtilities.encode(enc, original);
    }

    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    FloatBuffer decoded = BufferUtilities.decodeFloatBuffer(dec);
    assertNotNull(decoded);
    assertEquals(3, decoded.limit());
    assertEquals(1.0f, decoded.get(0), 1e-6);
    assertEquals(2.0f, decoded.get(1), 1e-6);
    assertEquals(3.0f, decoded.get(2), 1e-6);
  }

  @Test
  public void roundTrip_intBuffer() throws Exception {
    IntBuffer original = IntBuffer.allocate(4);
    original.put(10).put(20).put(30).put(40);
    original.rewind();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (OutputStreamBinaryEncoder enc = createEncoder(baos)) {
      BufferUtilities.encode(enc, original);
    }

    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    IntBuffer decoded = BufferUtilities.decodeIntBuffer(dec);
    assertNotNull(decoded);
    assertEquals(4, decoded.limit());
    assertEquals(10, decoded.get(0));
    assertEquals(40, decoded.get(3));
  }

  @Test
  public void roundTrip_doubleBuffer() throws Exception {
    DoubleBuffer original = DoubleBuffer.allocate(2);
    original.put(1.111).put(2.222);
    original.rewind();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (OutputStreamBinaryEncoder enc = createEncoder(baos)) {
      BufferUtilities.encode(enc, original);
    }

    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    DoubleBuffer decoded = BufferUtilities.decodeDoubleBuffer(dec);
    assertNotNull(decoded);
    assertEquals(2, decoded.limit());
    assertEquals(1.111, decoded.get(0), 1e-10);
    assertEquals(2.222, decoded.get(1), 1e-10);
  }

  @Test
  public void roundTrip_shortBuffer() throws Exception {
    ShortBuffer original = ShortBuffer.allocate(3);
    original.put((short) 100).put((short) 200).put((short) -300);
    original.rewind();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (OutputStreamBinaryEncoder enc = createEncoder(baos)) {
      BufferUtilities.encode(enc, original);
    }

    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    ShortBuffer decoded = BufferUtilities.decodeShortBuffer(dec);
    assertNotNull(decoded);
    assertEquals(3, decoded.limit());
    assertEquals(100, decoded.get(0));
    assertEquals(200, decoded.get(1));
    assertEquals(-300, decoded.get(2));
  }

  @Test
  public void roundTrip_longBuffer() throws Exception {
    LongBuffer original = LongBuffer.allocate(2);
    original.put(Long.MAX_VALUE).put(Long.MIN_VALUE);
    original.rewind();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (OutputStreamBinaryEncoder enc = createEncoder(baos)) {
      BufferUtilities.encode(enc, original);
    }

    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    LongBuffer decoded = BufferUtilities.decodeLongBuffer(dec);
    assertNotNull(decoded);
    assertEquals(2, decoded.limit());
    assertEquals(Long.MAX_VALUE, decoded.get(0));
    assertEquals(Long.MIN_VALUE, decoded.get(1));
  }

  @Test
  public void roundTrip_charBuffer() throws Exception {
    CharBuffer original = CharBuffer.allocate(4);
    original.put('A').put('B').put('C').put('Z');
    original.rewind();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (OutputStreamBinaryEncoder enc = createEncoder(baos)) {
      BufferUtilities.encode(enc, original);
    }

    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    CharBuffer decoded = BufferUtilities.decodeCharBuffer(dec);
    assertNotNull(decoded);
    assertEquals(4, decoded.limit());
    assertEquals('A', decoded.get(0));
    assertEquals('Z', decoded.get(3));
  }

  @Test
  public void roundTrip_byteBuffer() throws Exception {
    ByteBuffer original = ByteBuffer.allocate(5);
    original.put((byte) 1).put((byte) 2).put((byte) 3).put((byte) 4).put((byte) 5);
    original.rewind();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (OutputStreamBinaryEncoder enc = createEncoder(baos)) {
      BufferUtilities.encode(enc, original);
    }

    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    ByteBuffer decoded = BufferUtilities.decodeByteBuffer(dec);
    assertNotNull(decoded);
    assertEquals(5, decoded.limit());
    assertEquals(1, decoded.get(0));
    assertEquals(5, decoded.get(4));
  }

  @Test
  public void roundTrip_singleElementBuffer() throws Exception {
    FloatBuffer original = FloatBuffer.allocate(1);
    original.put(99.9f);
    original.rewind();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (OutputStreamBinaryEncoder enc = createEncoder(baos)) {
      BufferUtilities.encode(enc, original);
    }

    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    FloatBuffer decoded = BufferUtilities.decodeFloatBuffer(dec);
    assertEquals(1, decoded.limit());
    assertEquals(99.9f, decoded.get(0), 1e-6);
  }
}
