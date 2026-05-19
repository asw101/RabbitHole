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
    assertEquals(String.class, STRING_CODEC.getValueClass());
  }

  @Test
  public void getValueClass_integerCodec() {
    assertEquals(Integer.class, INTEGER_CODEC.getValueClass());
  }

  // ── appendRepresentation (default implementation) ─────────────────

  @Test
  public void appendRepresentation_defaultUsesToString() {
    StringBuilder sb = new StringBuilder();
    STRING_CODEC.appendRepresentation(sb, "hello");
    assertEquals("hello", sb.toString());
  }

  @Test
  public void appendRepresentation_integer() {
    StringBuilder sb = new StringBuilder();
    INTEGER_CODEC.appendRepresentation(sb, 42);
    assertEquals("42", sb.toString());
  }

  @Test
  public void appendRepresentation_null() {
    StringBuilder sb = new StringBuilder();
    STRING_CODEC.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_appendsToExisting() {
    StringBuilder sb = new StringBuilder("prefix:");
    STRING_CODEC.appendRepresentation(sb, "value");
    assertEquals("prefix:value", sb.toString());
  }

  // ── encode/decode round-trip ──────────────────────────────────────

  @Test
  public void encodeAndDecode_string_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    STRING_CODEC.encodeValue(encoder, "test");
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("test", STRING_CODEC.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecode_integer_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    INTEGER_CODEC.encodeValue(encoder, 99);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(Integer.valueOf(99), INTEGER_CODEC.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecode_emptyString() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    STRING_CODEC.encodeValue(encoder, "");
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("", STRING_CODEC.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecode_multipleValues() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    STRING_CODEC.encodeValue(encoder, "alpha");
    STRING_CODEC.encodeValue(encoder, "bravo");
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("alpha", STRING_CODEC.decodeValue(decoder));
    assertEquals("bravo", STRING_CODEC.decodeValue(decoder));
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

  private static final TestStringCodec STRING_CODEC = new TestStringCodec();
  private static final TestIntegerCodec INTEGER_CODEC = new TestIntegerCodec();

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
