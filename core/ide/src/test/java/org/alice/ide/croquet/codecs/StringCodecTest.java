package org.alice.ide.croquet.codecs;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringCodecTest {
  @Test
  public void singleton_notNull() {
    assertNotNull(StringCodec.SINGLETON);
  }

  @Test
  public void getValueClass_isString() {
    assertEquals(String.class, StringCodec.SINGLETON.getValueClass());
  }

  @Test
  public void appendRepresentation_addsValue() {
    StringBuilder sb = new StringBuilder();
    StringCodec.SINGLETON.appendRepresentation(sb, "hello");
    assertTrue(sb.toString().contains("hello"));
  }

  @Test
  public void appendRepresentation_nullValue() {
    StringBuilder sb = new StringBuilder();
    StringCodec.SINGLETON.appendRepresentation(sb, null);
    assertNotNull(sb.toString());
  }
}
