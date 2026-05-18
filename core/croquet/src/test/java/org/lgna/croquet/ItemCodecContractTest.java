package org.lgna.croquet;

import org.lgna.croquet.codecs.EnumCodec;
import org.lgna.croquet.history.UserActivity;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link ItemCodec} interface contract and its
 * inner {@link ItemCodec.Arrays} utility class.
 */
public class ItemCodecContractTest {

  @Test
  public void stringCodec_getValueClass() {
    assertEquals(String.class, CroquetTestUtils.STRING_CODEC.getValueClass());
  }

  @Test
  public void stringCodec_appendRepresentation_value() {
    StringBuilder sb = new StringBuilder();
    CroquetTestUtils.STRING_CODEC.appendRepresentation(sb, "hello");
    assertEquals("hello", sb.toString());
  }

  @Test
  public void stringCodec_appendRepresentation_null() {
    StringBuilder sb = new StringBuilder();
    CroquetTestUtils.STRING_CODEC.appendRepresentation(sb, null);
    // String codec appends null
    assertEquals("null", sb.toString());
  }

  @Test
  public void stringCodec_appendRepresentation_empty() {
    StringBuilder sb = new StringBuilder();
    CroquetTestUtils.STRING_CODEC.appendRepresentation(sb, "");
    assertEquals("", sb.toString());
  }

  @Test
  public void stringCodec_appendRepresentation_multipleAppends() {
    StringBuilder sb = new StringBuilder();
    CroquetTestUtils.STRING_CODEC.appendRepresentation(sb, "a");
    CroquetTestUtils.STRING_CODEC.appendRepresentation(sb, "b");
    assertEquals("ab", sb.toString());
  }

  private enum TestDirection { NORTH, SOUTH, EAST, WEST }

  @Test
  public void enumCodec_getValueClass() {
    EnumCodec<TestDirection> codec = EnumCodec.getInstance(TestDirection.class);
    assertEquals(TestDirection.class, codec.getValueClass());
  }

  @Test
  public void enumCodec_appendRepresentation() {
    EnumCodec<TestDirection> codec = EnumCodec.getInstance(TestDirection.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, TestDirection.NORTH);
    assertEquals("NORTH", sb.toString());
  }

  @Test
  public void enumCodec_allValues() {
    EnumCodec<TestDirection> codec = EnumCodec.getInstance(TestDirection.class);
    for (TestDirection dir : TestDirection.values()) {
      StringBuilder sb = new StringBuilder();
      codec.appendRepresentation(sb, dir);
      assertEquals(dir.name(), sb.toString());
    }
  }
}
