package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link DefaultItemCodec} and {@link AbstractItemCodec} —
 * factory method, getValueClass, appendRepresentation, and RuntimeException
 * behavior for encode/decode.
 */
public class DefaultItemCodecExtendedTest {

  // ── createInstance ──────────────────────────────────────────────────

  @Test
  public void createInstance_returnsNonNull() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    assertNotNull(codec);
  }

  @Test
  public void createInstance_differentCalls_returnDifferentInstances() {
    DefaultItemCodec<String> a = DefaultItemCodec.createInstance(String.class);
    DefaultItemCodec<String> b = DefaultItemCodec.createInstance(String.class);
    assertNotSame(a, b);
  }

  @Test
  public void createInstance_integerClass() {
    DefaultItemCodec<Integer> codec = DefaultItemCodec.createInstance(Integer.class);
    assertNotNull(codec);
    assertEquals(Integer.class, codec.getValueClass());
  }

  // ── getValueClass (inherited from AbstractItemCodec) ───────────────

  @Test
  public void getValueClass_string_returnsStringClass() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    assertEquals(String.class, codec.getValueClass());
  }

  @Test
  public void getValueClass_double_returnsDoubleClass() {
    DefaultItemCodec<Double> codec = DefaultItemCodec.createInstance(Double.class);
    assertEquals(Double.class, codec.getValueClass());
  }

  @Test
  public void getValueClass_boolean_returnsBooleanClass() {
    DefaultItemCodec<Boolean> codec = DefaultItemCodec.createInstance(Boolean.class);
    assertEquals(Boolean.class, codec.getValueClass());
  }

  @Test
  public void getValueClass_customClass() {
    DefaultItemCodec<StringBuilder> codec = DefaultItemCodec.createInstance(StringBuilder.class);
    assertEquals(StringBuilder.class, codec.getValueClass());
  }

  // ── appendRepresentation (inherited from AbstractItemCodec) ────────

  @Test
  public void appendRepresentation_string() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, "hello");
    assertEquals("hello", sb.toString());
  }

  @Test
  public void appendRepresentation_null() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_integer() {
    DefaultItemCodec<Integer> codec = DefaultItemCodec.createInstance(Integer.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, 42);
    assertEquals("42", sb.toString());
  }

  @Test
  public void appendRepresentation_appendsToExisting() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    StringBuilder sb = new StringBuilder("before:");
    codec.appendRepresentation(sb, "after");
    assertEquals("before:after", sb.toString());
  }

  @Test
  public void appendRepresentation_emptyString() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, "");
    assertEquals("", sb.toString());
  }

  // ── decodeValue: throws RuntimeException ───────────────────────────

  @Test(expected = RuntimeException.class)
  public void decodeValue_throwsRuntimeException() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encode("dummy");
    BinaryDecoder decoder = encoder.createDecoder();
    codec.decodeValue(decoder);
  }

  @Test
  public void decodeValue_exceptionMessage_isTodo() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encode("dummy");
    BinaryDecoder decoder = encoder.createDecoder();
    try {
      codec.decodeValue(decoder);
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("todo", e.getMessage());
    }
  }

  // ── encodeValue: throws RuntimeException ───────────────────────────

  @Test(expected = RuntimeException.class)
  public void encodeValue_throwsRuntimeException() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, "anything");
  }

  @Test
  public void encodeValue_exceptionMessage_isTodo() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    try {
      codec.encodeValue(encoder, "anything");
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("todo", e.getMessage());
    }
  }

  @Test(expected = RuntimeException.class)
  public void encodeValue_null_throwsRuntimeException() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, null);
  }

  // ── type safety via generics ───────────────────────────────────────

  @Test
  public void genericType_preservedAcrossOperations() {
    DefaultItemCodec<Long> codec = DefaultItemCodec.createInstance(Long.class);
    assertEquals(Long.class, codec.getValueClass());

    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, 999L);
    assertEquals("999", sb.toString());
  }

  @Test
  public void multipleInstances_independentValueClasses() {
    DefaultItemCodec<String> strCodec = DefaultItemCodec.createInstance(String.class);
    DefaultItemCodec<Integer> intCodec = DefaultItemCodec.createInstance(Integer.class);
    assertNotEquals(strCodec.getValueClass(), intCodec.getValueClass());
  }
}
