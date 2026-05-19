package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;
import org.lgna.croquet.ItemCodec;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link AbstractItemCodec} via a concrete test subclass.
 * Tests getValueClass, appendRepresentation, and codec contract.
 */
public class AbstractItemCodecCoverageTest {

  private static class TestStringCodec extends AbstractItemCodec<String> {
    TestStringCodec() {
      super(String.class);
    }

    @Override
    public String decodeValue(BinaryDecoder binaryDecoder) {
      return binaryDecoder.decodeString();
    }

    @Override
    public void encodeValue(BinaryEncoder binaryEncoder, String value) {
      binaryEncoder.encode(value);
    }
  }

  private static class TestIntegerCodec extends AbstractItemCodec<Integer> {
    TestIntegerCodec() {
      super(Integer.class);
    }

    @Override
    public Integer decodeValue(BinaryDecoder binaryDecoder) {
      return binaryDecoder.decodeInt();
    }

    @Override
    public void encodeValue(BinaryEncoder binaryEncoder, Integer value) {
      binaryEncoder.encode(value);
    }
  }

  // ── getValueClass ─────────────────────────────────────────────────

  @Test
  public void getValueClass_string() {
    assertEquals(String.class, new TestStringCodec().getValueClass());
  }

  @Test
  public void getValueClass_integer() {
    assertEquals(Integer.class, new TestIntegerCodec().getValueClass());
  }

  // ── appendRepresentation (default impl) ───────────────────────────

  @Test
  public void appendRepresentation_string() {
    TestStringCodec codec = new TestStringCodec();
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, "hello");
    assertEquals("hello", sb.toString());
  }

  @Test
  public void appendRepresentation_integer() {
    TestIntegerCodec codec = new TestIntegerCodec();
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, 42);
    assertEquals("42", sb.toString());
  }

  @Test
  public void appendRepresentation_null() {
    TestStringCodec codec = new TestStringCodec();
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_emptyString() {
    TestStringCodec codec = new TestStringCodec();
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, "");
    assertEquals("", sb.toString());
  }

  // ── Round-trip encode/decode ──────────────────────────────────────

  @Test
  public void roundTrip_string() {
    TestStringCodec codec = new TestStringCodec();
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, "test");
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("test", codec.decodeValue(decoder));
  }

  @Test
  public void roundTrip_integer() {
    TestIntegerCodec codec = new TestIntegerCodec();
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, 99);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(Integer.valueOf(99), codec.decodeValue(decoder));
  }

  @Test
  public void roundTrip_emptyString() {
    TestStringCodec codec = new TestStringCodec();
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, "");
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("", codec.decodeValue(decoder));
  }

  @Test
  public void roundTrip_negativeInteger() {
    TestIntegerCodec codec = new TestIntegerCodec();
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, -1);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(Integer.valueOf(-1), codec.decodeValue(decoder));
  }

  // ── Implements ItemCodec ──────────────────────────────────────────

  @Test
  public void implementsItemCodec() {
    assertTrue(ItemCodec.class.isAssignableFrom(AbstractItemCodec.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(AbstractItemCodec.class.getModifiers()));
  }
}
