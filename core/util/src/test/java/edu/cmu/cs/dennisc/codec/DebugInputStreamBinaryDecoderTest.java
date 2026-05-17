package edu.cmu.cs.dennisc.codec;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

/**
 * Round-trip tests: encode with DebugOutputStreamBinaryEncoder,
 * decode with DebugInputStreamBinaryDecoder.
 */
public class DebugInputStreamBinaryDecoderTest {

  private byte[] encode(EncoderAction action) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DebugOutputStreamBinaryEncoder encoder = new DebugOutputStreamBinaryEncoder(baos);
    action.encode(encoder);
    encoder.flush();
    return baos.toByteArray();
  }

  private DebugInputStreamBinaryDecoder decoder(byte[] data) {
    return new DebugInputStreamBinaryDecoder(new ByteArrayInputStream(data));
  }

  @FunctionalInterface
  interface EncoderAction {
    void encode(DebugOutputStreamBinaryEncoder encoder);
  }

  // --- boolean ---

  @Test
  public void roundTrip_booleanTrue() {
    byte[] data = encode(e -> e.encode(true));
    assertTrue(decoder(data).decodeBoolean());
  }

  @Test
  public void roundTrip_booleanFalse() {
    byte[] data = encode(e -> e.encode(false));
    assertFalse(decoder(data).decodeBoolean());
  }

  // --- int ---

  @Test
  public void roundTrip_int_zero() {
    byte[] data = encode(e -> e.encode(0));
    assertEquals(0, decoder(data).decodeInt());
  }

  @Test
  public void roundTrip_int_positive() {
    byte[] data = encode(e -> e.encode(42));
    assertEquals(42, decoder(data).decodeInt());
  }

  @Test
  public void roundTrip_int_negative() {
    byte[] data = encode(e -> e.encode(-100));
    assertEquals(-100, decoder(data).decodeInt());
  }

  @Test
  public void roundTrip_int_maxValue() {
    byte[] data = encode(e -> e.encode(Integer.MAX_VALUE));
    assertEquals(Integer.MAX_VALUE, decoder(data).decodeInt());
  }

  // --- long ---

  @Test
  public void roundTrip_long() {
    byte[] data = encode(e -> e.encode(123456789L));
    assertEquals(123456789L, decoder(data).decodeLong());
  }

  @Test
  public void roundTrip_long_minValue() {
    byte[] data = encode(e -> e.encode(Long.MIN_VALUE));
    assertEquals(Long.MIN_VALUE, decoder(data).decodeLong());
  }

  // --- short ---

  @Test
  public void roundTrip_short() {
    byte[] data = encode(e -> e.encode((short) 1234));
    assertEquals((short) 1234, decoder(data).decodeShort());
  }

  @Test
  public void roundTrip_short_negative() {
    byte[] data = encode(e -> e.encode((short) -500));
    assertEquals((short) -500, decoder(data).decodeShort());
  }

  // --- double ---

  @Test
  public void roundTrip_double() {
    byte[] data = encode(e -> e.encode(3.14159265));
    assertEquals(3.14159265, decoder(data).decodeDouble(), 0.0);
  }

  @Test
  public void roundTrip_double_negativeInfinity() {
    byte[] data = encode(e -> e.encode(Double.NEGATIVE_INFINITY));
    assertEquals(Double.NEGATIVE_INFINITY, decoder(data).decodeDouble(), 0.0);
  }

  // --- char ---

  @Test
  public void roundTrip_char() {
    byte[] data = encode(e -> e.encode('Z'));
    assertEquals('Z', decoder(data).decodeChar());
  }

  @Test
  public void roundTrip_char_unicode() {
    byte[] data = encode(e -> e.encode('\u00E9'));
    assertEquals('\u00E9', decoder(data).decodeChar());
  }

  // --- String ---

  @Test
  public void roundTrip_string() {
    byte[] data = encode(e -> e.encode("hello world"));
    assertEquals("hello world", decoder(data).decodeString());
  }

  @Test
  public void roundTrip_string_empty() {
    byte[] data = encode(e -> e.encode(""));
    assertEquals("", decoder(data).decodeString());
  }

  @Test
  public void roundTrip_string_null() {
    byte[] data = encode(e -> e.encode((String) null));
    assertNull(decoder(data).decodeString());
  }

  // --- Multiple values in sequence ---

  @Test
  public void roundTrip_multipleTypes() {
    byte[] data = encode(e -> {
      e.encode(true);
      e.encode(42);
      e.encode("test");
      e.encode(99L);
      e.encode('A');
      e.encode((short) 7);
      e.encode(2.718);
    });

    DebugInputStreamBinaryDecoder dec = decoder(data);
    assertTrue(dec.decodeBoolean());
    assertEquals(42, dec.decodeInt());
    assertEquals("test", dec.decodeString());
    assertEquals(99L, dec.decodeLong());
    assertEquals('A', dec.decodeChar());
    assertEquals((short) 7, dec.decodeShort());
    assertEquals(2.718, dec.decodeDouble(), 0.0);
  }
}
