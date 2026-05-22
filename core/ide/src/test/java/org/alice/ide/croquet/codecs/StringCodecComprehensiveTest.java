package org.alice.ide.croquet.codecs;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringCodecComprehensiveTest {
  @Test public void singleton_notNull() { assertNotNull(StringCodec.SINGLETON); }
  @Test public void getValueClass_returnsStringClass() { assertEquals(String.class, StringCodec.SINGLETON.getValueClass()); }
  @Test public void appendRepresentation_simpleString() {
    StringBuilder sb = new StringBuilder();
    StringCodec.SINGLETON.appendRepresentation(sb, "hello");
    assertEquals("hello", sb.toString());
  }
  @Test public void appendRepresentation_emptyString() {
    StringBuilder sb = new StringBuilder();
    StringCodec.SINGLETON.appendRepresentation(sb, "");
    assertEquals("", sb.toString());
  }
  @Test public void appendRepresentation_unicodeString() {
    StringBuilder sb = new StringBuilder();
    StringCodec.SINGLETON.appendRepresentation(sb, "éclair");
    assertEquals("éclair", sb.toString());
  }
  @Test public void appendRepresentation_nullString() {
    StringBuilder sb = new StringBuilder();
    StringCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }
  @Test public void appendRepresentation_appendsToExistingBuilder() {
    StringBuilder sb = new StringBuilder("prefix:");
    StringCodec.SINGLETON.appendRepresentation(sb, "value");
    assertEquals("prefix:value", sb.toString());
  }
}
