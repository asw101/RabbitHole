package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;
import org.lgna.croquet.ItemCodec;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link AbstractItemCodec} via a concrete test subclass —
 * getValueClass, appendRepresentation default behavior, and codec contract.
 */
public class AbstractItemCodecCoverageTest {

  // ── getValueClass ─────────────────────────────────────────────────

  @Test
  public void getValueClass_returnsConstructorArg() {
    TestStringCodec codec = new TestStringCodec();
    assertEquals(String.class, codec.getValueClass());
  }

  @Test
  public void getValueClass_integerCodec() {
    TestIntegerCodec codec = new TestIntegerCodec();
    assertEquals(Integer.class, codec.getValueClass());
  }

  // ── appendRepresentation (default implementation) ─────────────────

  @Test
  public void appendRepresentation_defaultUsesToString() {
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
  public void appendRepresentation_appendsToExisting() {
    TestStringCodec codec = new TestStringCodec();
    StringBuilder sb = new StringBuilder("prefix:");
    codec.appendRepresentation(sb, "value");
    assertEquals("prefix:value", sb.toString());
  }

  // ── encode/decode round-trip ──────────────────────────────────────

  @Test
  public void encodeAndDecode_string_roundTrips() {
    TestStringCodec codec = new TestStringCodec();
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, "test");
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("test", codec.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecode_integer_roundTrips() {
    TestIntegerCodec codec = new TestIntegerCodec();
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, 99);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(Integer.valueOf(99), codec.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecode_emptyString() {
    TestStringCodec codec = new TestStringCodec();
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, "");
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("", codec.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecode_multipleValues() {
    TestStringCodec codec = new TestStringCodec();
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, "alpha");
    codec.encodeValue(encoder, "bravo");
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("alpha", codec.decodeValue(decoder));
    assertEquals("bravo", codec.decodeValue(decoder));
  }

  // ── class structure ───────────────────────────────────────────────

  @Test
  public void abstractItemCodec_implementsItemCodec() {
    assertTrue(ItemCodec.class.isAssignableFrom(AbstractItemCodec.class));
  }

  @Test
  public void abstractItemCodec_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractItemCodec.class.getModifiers()));
  }

  @Test
  public void abstractItemCodec_isPublic() {
    assertTrue(Modifier.isPublic(AbstractItemCodec.class.getModifiers()));
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestStringCodec extends AbstractItemCodec<String> {
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

  static class TestIntegerCodec extends AbstractItemCodec<Integer> {
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
}
