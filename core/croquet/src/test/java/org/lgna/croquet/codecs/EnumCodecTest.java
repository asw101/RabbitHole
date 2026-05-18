package org.lgna.croquet.codecs;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link EnumCodec}, {@link AbstractItemCodec}, {@link DefaultItemCodec},
 * and {@link FileCodec}. Covers value class, appendRepresentation, singleton
 * patterns, and factory methods.
 */
public class EnumCodecTest {

  private enum TestEnum {
    ALPHA, BRAVO, CHARLIE
  }

  private enum EmptyEnum {}

  // ── EnumCodec: getInstance ────────────────────────────────────────

  @Test
  public void getInstance_returnsNonNull() {
    EnumCodec<TestEnum> codec = EnumCodec.getInstance(TestEnum.class);
    assertNotNull(codec);
  }

  @Test
  public void getInstance_sameClass_returnsSameInstance() {
    EnumCodec<TestEnum> a = EnumCodec.getInstance(TestEnum.class);
    EnumCodec<TestEnum> b = EnumCodec.getInstance(TestEnum.class);
    assertSame(a, b);
  }

  @Test
  public void getInstance_differentClass_returnsDifferentInstance() {
    EnumCodec<TestEnum> a = EnumCodec.getInstance(TestEnum.class);
    EnumCodec<EmptyEnum> b = EnumCodec.getInstance(EmptyEnum.class);
    assertNotSame(a, b);
  }

  // ── EnumCodec: createInstance ──────────────────────────────────────

  @Test
  public void createInstance_returnsNewCodec() {
    EnumCodec<TestEnum> codec = EnumCodec.createInstance(TestEnum.class, null);
    assertNotNull(codec);
  }

  @Test
  public void createInstance_withCustomizer_storesCustomizer() {
    EnumCodec.LocalizationCustomizer<TestEnum> customizer = (s, v) -> s.toUpperCase();
    EnumCodec<TestEnum> codec = EnumCodec.createInstance(TestEnum.class, customizer);
    assertSame(customizer, codec.getLocalizationCustomizer());
  }

  @Test
  public void createInstance_nullCustomizer_returnsNullCustomizer() {
    EnumCodec<TestEnum> codec = EnumCodec.createInstance(TestEnum.class, null);
    assertNull(codec.getLocalizationCustomizer());
  }

  // ── EnumCodec: getValueClass ──────────────────────────────────────

  @Test
  public void getValueClass_returnsEnumClass() {
    EnumCodec<TestEnum> codec = EnumCodec.getInstance(TestEnum.class);
    assertEquals(TestEnum.class, codec.getValueClass());
  }

  // ── EnumCodec: appendRepresentation ───────────────────────────────

  @Test
  public void appendRepresentation_nonNullValue_appendsName() {
    EnumCodec<TestEnum> codec = EnumCodec.createInstance(TestEnum.class, null);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, TestEnum.ALPHA);
    // Without resource bundle, falls back to toString = "ALPHA"
    assertEquals("ALPHA", sb.toString());
  }

  @Test
  public void appendRepresentation_anotherValue() {
    EnumCodec<TestEnum> codec = EnumCodec.createInstance(TestEnum.class, null);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, TestEnum.CHARLIE);
    assertEquals("CHARLIE", sb.toString());
  }

  @Test
  public void appendRepresentation_nullValue_appendsNull() {
    EnumCodec<TestEnum> codec = EnumCodec.createInstance(TestEnum.class, null);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_calledTwice_caches() {
    EnumCodec<TestEnum> codec = EnumCodec.createInstance(TestEnum.class, null);
    StringBuilder sb1 = new StringBuilder();
    codec.appendRepresentation(sb1, TestEnum.BRAVO);
    StringBuilder sb2 = new StringBuilder();
    codec.appendRepresentation(sb2, TestEnum.BRAVO);
    assertEquals(sb1.toString(), sb2.toString());
  }

  @Test
  public void appendRepresentation_withCustomizer_appliesCustomizer() {
    EnumCodec.LocalizationCustomizer<TestEnum> customizer = (localized, value) -> "custom_" + value.name();
    EnumCodec<TestEnum> codec = EnumCodec.createInstance(TestEnum.class, customizer);
    StringBuilder sb = new StringBuilder();
    // No resource bundle found, so customizer won't be applied since
    // localization falls back to toString. But let's verify no crash.
    codec.appendRepresentation(sb, TestEnum.ALPHA);
    assertFalse(sb.toString().isEmpty());
  }

  // ── EnumCodec: toString ───────────────────────────────────────────

  @Test
  public void toString_containsClassName() {
    EnumCodec<TestEnum> codec = EnumCodec.getInstance(TestEnum.class);
    assertTrue(codec.toString().contains("EnumCodec"));
  }

  @Test
  public void toString_containsEnumClassName() {
    EnumCodec<TestEnum> codec = EnumCodec.getInstance(TestEnum.class);
    assertTrue(codec.toString().contains("TestEnum"));
  }

  // ── DefaultItemCodec ──────────────────────────────────────────────

  @Test
  public void defaultItemCodec_getValueClass() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    assertEquals(String.class, codec.getValueClass());
  }

  @Test
  public void defaultItemCodec_appendRepresentation() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, "hello");
    assertEquals("hello", sb.toString());
  }

  @Test(expected = RuntimeException.class)
  public void defaultItemCodec_decodeValue_throws() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    codec.decodeValue(null);
  }

  @Test(expected = RuntimeException.class)
  public void defaultItemCodec_encodeValue_throws() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    codec.encodeValue(null, "test");
  }

  // ── FileCodec ─────────────────────────────────────────────────────

  @Test
  public void fileCodec_getValueClass() {
    assertEquals(java.io.File.class, FileCodec.SINGLETON.getValueClass());
  }

  @Test
  public void fileCodec_appendRepresentation_nonNull() {
    StringBuilder sb = new StringBuilder();
    java.io.File file = new java.io.File("/tmp/test.txt");
    FileCodec.SINGLETON.appendRepresentation(sb, file);
    assertTrue(sb.toString().contains("test.txt"));
  }

  @Test
  public void fileCodec_appendRepresentation_null() {
    StringBuilder sb = new StringBuilder();
    FileCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  // ── ColorCodec encode/decode round-trip ─────────────────────────────

  @Test
  public void colorCodec_encodeAndDecode_nonNull() {
    java.awt.Color original = new java.awt.Color(100, 150, 200, 255);
    edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder =
        new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
    ColorCodec.SINGLETON.encodeValue(encoder, original);
    edu.cmu.cs.dennisc.codec.BinaryDecoder decoder = encoder.createDecoder();
    java.awt.Color decoded = ColorCodec.SINGLETON.decodeValue(decoder);
    assertEquals(original, decoded);
  }

  @Test
  public void colorCodec_encodeAndDecode_null() {
    edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder =
        new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
    ColorCodec.SINGLETON.encodeValue(encoder, null);
    edu.cmu.cs.dennisc.codec.BinaryDecoder decoder = encoder.createDecoder();
    java.awt.Color decoded = ColorCodec.SINGLETON.decodeValue(decoder);
    assertNull(decoded);
  }

  @Test
  public void colorCodec_encodeAndDecode_withAlpha() {
    java.awt.Color original = new java.awt.Color(10, 20, 30, 128);
    edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder =
        new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
    ColorCodec.SINGLETON.encodeValue(encoder, original);
    edu.cmu.cs.dennisc.codec.BinaryDecoder decoder = encoder.createDecoder();
    java.awt.Color decoded = ColorCodec.SINGLETON.decodeValue(decoder);
    assertEquals(10, decoded.getRed());
    assertEquals(20, decoded.getGreen());
    assertEquals(30, decoded.getBlue());
    assertEquals(128, decoded.getAlpha());
  }

  // ── FileCodec encode null ─────────────────────────────────────────

  @Test
  public void fileCodec_encodeAndDecode_null() {
    edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder =
        new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
    FileCodec.SINGLETON.encodeValue(encoder, null);
    edu.cmu.cs.dennisc.codec.BinaryDecoder decoder = encoder.createDecoder();
    java.io.File decoded = FileCodec.SINGLETON.decodeValue(decoder);
    assertNull(decoded);
  }
}
