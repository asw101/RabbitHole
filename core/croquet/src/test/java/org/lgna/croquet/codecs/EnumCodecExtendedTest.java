package org.lgna.croquet.codecs;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link EnumCodec} — codec for enum values with localization support.
 * (Note: EnumCodecTest already exists — this tests additional behavior.)
 */
public class EnumCodecExtendedTest {

  private enum TestColor { RED, GREEN, BLUE }
  private enum TestSize { SMALL, MEDIUM, LARGE }

  @Test
  public void getInstance_returnsNonNull() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    assertNotNull(codec);
  }

  @Test
  public void getInstance_sameClass_returnsSameInstance() {
    EnumCodec<TestColor> c1 = EnumCodec.getInstance(TestColor.class);
    EnumCodec<TestColor> c2 = EnumCodec.getInstance(TestColor.class);
    assertSame(c1, c2);
  }

  @Test
  public void getInstance_differentClasses_differentInstances() {
    EnumCodec<TestColor> c1 = EnumCodec.getInstance(TestColor.class);
    EnumCodec<TestSize> c2 = EnumCodec.getInstance(TestSize.class);
    assertNotSame(c1, c2);
  }

  @Test
  public void getValueClass_returnsEnumClass() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    assertEquals(TestColor.class, codec.getValueClass());
  }

  @Test
  public void appendRepresentation_usesEnumName_noResourceBundle() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, TestColor.RED);
    assertEquals("RED", sb.toString());
  }

  @Test
  public void appendRepresentation_green() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, TestColor.GREEN);
    assertEquals("GREEN", sb.toString());
  }

  @Test
  public void appendRepresentation_nullValue() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void createInstance_withCustomizer() {
    EnumCodec.LocalizationCustomizer<TestColor> customizer = (loc, val) -> "custom_" + val.name();
    EnumCodec<TestColor> codec = EnumCodec.createInstance(TestColor.class, customizer);
    assertNotNull(codec);
    assertSame(customizer, codec.getLocalizationCustomizer());
  }

  @Test
  public void createInstance_noCustomizer() {
    EnumCodec<TestColor> codec = EnumCodec.createInstance(TestColor.class, null);
    assertNull(codec.getLocalizationCustomizer());
  }

  @Test
  public void toString_containsClassName() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    String str = codec.toString();
    assertTrue(str.contains("EnumCodec"));
    assertTrue(str.contains("TestColor"));
  }

  @Test
  public void toString_containsBrackets() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    String str = codec.toString();
    assertTrue(str.contains("["));
    assertTrue(str.contains("]"));
  }

  @Test
  public void appendRepresentation_allEnumConstants() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    for (TestColor color : TestColor.values()) {
      StringBuilder sb = new StringBuilder();
      codec.appendRepresentation(sb, color);
      assertFalse(sb.toString().isEmpty());
    }
  }
}
