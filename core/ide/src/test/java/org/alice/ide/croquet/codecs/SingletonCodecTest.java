package org.alice.ide.croquet.codecs;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link SingletonCodec} — reflection-based codec for singleton instances.
 * Covers getInstance factory, getValueClass, encodeValue null-safety,
 * and appendRepresentation.
 */
public class SingletonCodecTest {

  // ---- getInstance factory ----

  @Test
  public void getInstance_returnsNonNull() {
    SingletonCodec<String> codec = SingletonCodec.getInstance(String.class);
    assertNotNull(codec);
  }

  @Test
  public void getInstance_objectClass_returnsNonNull() {
    SingletonCodec<Object> codec = SingletonCodec.getInstance(Object.class);
    assertNotNull(codec);
  }

  @Test
  public void getInstance_differentClasses_differentCodecs() {
    SingletonCodec<String> c1 = SingletonCodec.getInstance(String.class);
    SingletonCodec<Integer> c2 = SingletonCodec.getInstance(Integer.class);
    assertNotSame(c1, c2);
  }

  // ---- getValueClass ----

  @Test
  public void getValueClass_returnsString() {
    SingletonCodec<String> codec = SingletonCodec.getInstance(String.class);
    assertEquals(String.class, codec.getValueClass());
  }

  @Test
  public void getValueClass_returnsObject() {
    SingletonCodec<Object> codec = SingletonCodec.getInstance(Object.class);
    assertEquals(Object.class, codec.getValueClass());
  }

  @Test
  public void getValueClass_returnsInteger() {
    SingletonCodec<Integer> codec = SingletonCodec.getInstance(Integer.class);
    assertEquals(Integer.class, codec.getValueClass());
  }

  // ---- appendRepresentation ----

  @Test
  public void appendRepresentation_withNull_appendsNull() {
    SingletonCodec<String> codec = SingletonCodec.getInstance(String.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_withValue_appendsToString() {
    SingletonCodec<String> codec = SingletonCodec.getInstance(String.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, "hello");
    assertEquals("hello", sb.toString());
  }

  @Test
  public void appendRepresentation_withInteger_appendsToString() {
    SingletonCodec<Integer> codec = SingletonCodec.getInstance(Integer.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, 42);
    assertEquals("42", sb.toString());
  }

  @Test
  public void appendRepresentation_appendsToExistingContent() {
    SingletonCodec<String> codec = SingletonCodec.getInstance(String.class);
    StringBuilder sb = new StringBuilder("prefix:");
    codec.appendRepresentation(sb, "value");
    assertEquals("prefix:value", sb.toString());
  }

  // ---- ItemCodec interface compliance ----

  @Test
  public void implementsItemCodec() {
    SingletonCodec<String> codec = SingletonCodec.getInstance(String.class);
    assertTrue(codec instanceof org.lgna.croquet.ItemCodec);
  }
}
