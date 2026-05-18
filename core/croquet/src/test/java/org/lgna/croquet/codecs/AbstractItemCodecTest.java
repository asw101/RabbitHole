package org.lgna.croquet.codecs;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link AbstractItemCodec} — base class providing getValueClass()
 * and appendRepresentation() defaults. Tested through {@link DefaultItemCodec}.
 */
public class AbstractItemCodecTest {

  @Test
  public void getValueClass_returnsConstructorArg() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    assertEquals(String.class, codec.getValueClass());
  }

  @Test
  public void getValueClass_integerClass() {
    DefaultItemCodec<Integer> codec = DefaultItemCodec.createInstance(Integer.class);
    assertEquals(Integer.class, codec.getValueClass());
  }

  @Test
  public void appendRepresentation_usesToString() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, "hello");
    assertEquals("hello", sb.toString());
  }

  @Test
  public void appendRepresentation_integer() {
    DefaultItemCodec<Integer> codec = DefaultItemCodec.createInstance(Integer.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, 42);
    assertEquals("42", sb.toString());
  }

  @Test
  public void appendRepresentation_nullValue() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test(expected = RuntimeException.class)
  public void decodeValue_throwsTodo() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    codec.decodeValue(null);
  }

  @Test(expected = RuntimeException.class)
  public void encodeValue_throwsTodo() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    codec.encodeValue(null, "value");
  }
}
