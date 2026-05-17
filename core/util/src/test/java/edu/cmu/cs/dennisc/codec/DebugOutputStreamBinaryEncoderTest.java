package edu.cmu.cs.dennisc.codec;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class DebugOutputStreamBinaryEncoderTest {

  @Test
  public void encode_boolean_producesOutput() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DebugOutputStreamBinaryEncoder encoder = new DebugOutputStreamBinaryEncoder(baos);
    encoder.encode(true);
    encoder.flush();
    assertTrue(baos.size() > 0);
  }

  @Test
  public void encode_int_producesOutput() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DebugOutputStreamBinaryEncoder encoder = new DebugOutputStreamBinaryEncoder(baos);
    encoder.encode(42);
    encoder.flush();
    assertTrue(baos.size() > 0);
  }

  @Test
  public void encode_double_producesOutput() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DebugOutputStreamBinaryEncoder encoder = new DebugOutputStreamBinaryEncoder(baos);
    encoder.encode(3.14);
    encoder.flush();
    assertTrue(baos.size() > 0);
  }

  @Test
  public void encode_string_producesOutput() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DebugOutputStreamBinaryEncoder encoder = new DebugOutputStreamBinaryEncoder(baos);
    encoder.encode("hello");
    encoder.flush();
    assertTrue(baos.size() > 0);
  }

  @Test
  public void encode_char_producesOutput() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DebugOutputStreamBinaryEncoder encoder = new DebugOutputStreamBinaryEncoder(baos);
    encoder.encode('A');
    encoder.flush();
    assertTrue(baos.size() > 0);
  }

  @Test
  public void encode_long_producesOutput() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DebugOutputStreamBinaryEncoder encoder = new DebugOutputStreamBinaryEncoder(baos);
    encoder.encode(123456789L);
    encoder.flush();
    assertTrue(baos.size() > 0);
  }

  @Test
  public void encode_short_producesOutput() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DebugOutputStreamBinaryEncoder encoder = new DebugOutputStreamBinaryEncoder(baos);
    encoder.encode((short) 999);
    encoder.flush();
    assertTrue(baos.size() > 0);
  }

  @Test
  public void encode_producesMoreBytesThanRegular() {
    ByteArrayOutputStream debugBaos = new ByteArrayOutputStream();
    DebugOutputStreamBinaryEncoder debugEnc = new DebugOutputStreamBinaryEncoder(debugBaos);
    debugEnc.encode(42);
    debugEnc.flush();

    ByteArrayOutputStream normalBaos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder normalEnc = new OutputStreamBinaryEncoder(normalBaos);
    normalEnc.encode(42);
    normalEnc.flush();

    assertTrue("Debug encoder should produce more bytes due to markers",
        debugBaos.size() > normalBaos.size());
  }
}
