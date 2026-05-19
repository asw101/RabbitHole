package edu.cmu.cs.dennisc.codec;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class AbstractBinaryCodecDeepTest {

  private OutputStreamBinaryEncoder createEncoder(ByteArrayOutputStream baos) {
    return new OutputStreamBinaryEncoder(baos);
  }

  private InputStreamBinaryDecoder createDecoder(byte[] data) {
    return new InputStreamBinaryDecoder(new ByteArrayInputStream(data));
  }

  @Test
  public void roundTrip_booleanArray() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(new boolean[]{true, false, true, true, false});
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    boolean[] result = dec.decodeBooleanArray();
    assertArrayEquals(new boolean[]{true, false, true, true, false}, result);
  }

  @Test
  public void roundTrip_byteArray() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(new byte[]{1, 2, 3, -128, 127});
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    byte[] result = dec.decodeByteArray();
    assertArrayEquals(new byte[]{1, 2, 3, -128, 127}, result);
  }

  @Test
  public void roundTrip_charArray() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(new char[]{'a', 'b', 'z'});
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    char[] result = dec.decodeCharArray();
    assertArrayEquals(new char[]{'a', 'b', 'z'}, result);
  }

  @Test
  public void roundTrip_doubleArray() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(new double[]{1.1, 2.2, 3.3});
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    double[] result = dec.decodeDoubleArray();
    assertArrayEquals(new double[]{1.1, 2.2, 3.3}, result, 1e-15);
  }

  @Test
  public void roundTrip_floatArray() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(new float[]{1.5f, 2.5f});
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    float[] result = dec.decodeFloatArray();
    assertEquals(2, result.length);
    assertEquals(1.5f, result[0], 1e-6);
    assertEquals(2.5f, result[1], 1e-6);
  }

  @Test
  public void roundTrip_intArray() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    int[] result = dec.decodeIntArray();
    assertArrayEquals(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE}, result);
  }

  @Test
  public void roundTrip_longArray() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(new long[]{Long.MIN_VALUE, 0L, Long.MAX_VALUE});
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    long[] result = dec.decodeLongArray();
    assertArrayEquals(new long[]{Long.MIN_VALUE, 0L, Long.MAX_VALUE}, result);
  }

  @Test
  public void roundTrip_shortArray() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(new short[]{Short.MIN_VALUE, 0, Short.MAX_VALUE});
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    short[] result = dec.decodeShortArray();
    assertArrayEquals(new short[]{Short.MIN_VALUE, 0, Short.MAX_VALUE}, result);
  }

  @Test
  public void roundTrip_stringArray() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(new String[]{"hello", "world", ""});
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    String[] result = dec.decodeStringArray();
    assertArrayEquals(new String[]{"hello", "world", ""}, result);
  }

  @Test
  public void roundTrip_enum() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(Thread.State.RUNNABLE);
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    Thread.State result = dec.decodeEnum();
    assertEquals(Thread.State.RUNNABLE, result);
  }

  @Test
  public void roundTrip_nullEnum() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode((Enum<?>) null);
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    Thread.State result = dec.decodeEnum();
    assertNull(result);
  }

  @Test
  public void roundTrip_uuid() throws Exception {
    java.util.UUID uuid = java.util.UUID.randomUUID();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(uuid);
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    java.util.UUID result = dec.decodeId();
    assertEquals(uuid, result);
  }

  @Test
  public void roundTrip_nullUuid() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode((java.util.UUID) null);
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    java.util.UUID result = dec.decodeId();
    assertNull(result);
  }

  @Test
  public void roundTrip_uuidArray() throws Exception {
    java.util.UUID[] ids = {java.util.UUID.randomUUID(), java.util.UUID.randomUUID()};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(ids);
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    java.util.UUID[] result = dec.decodeIdArray();
    assertArrayEquals(ids, result);
  }

  @Test
  public void roundTrip_enumArray() throws Exception {
    Thread.State[] states = {Thread.State.NEW, Thread.State.RUNNABLE, Thread.State.TERMINATED};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(states);
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    Thread.State[] result = dec.decodeEnumArray(Thread.State.class);
    assertArrayEquals(states, result);
  }

  @Test
  public void roundTrip_emptyBooleanArray() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(new boolean[0]);
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    boolean[] result = dec.decodeBooleanArray();
    assertEquals(0, result.length);
  }

  @Test
  public void roundTrip_emptyIntArray() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(new int[0]);
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    int[] result = dec.decodeIntArray();
    assertEquals(0, result.length);
  }

  @Test
  public void roundTrip_singleElementArrays() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder enc = createEncoder(baos);
    enc.encode(new boolean[]{true});
    enc.encode(new int[]{42});
    enc.encode(new double[]{3.14});
    enc.encode(new String[]{"solo"});
    enc.flush();
    InputStreamBinaryDecoder dec = createDecoder(baos.toByteArray());
    assertArrayEquals(new boolean[]{true}, dec.decodeBooleanArray());
    assertArrayEquals(new int[]{42}, dec.decodeIntArray());
    assertArrayEquals(new double[]{3.14}, dec.decodeDoubleArray(), 1e-15);
    assertArrayEquals(new String[]{"solo"}, dec.decodeStringArray());
  }
}
