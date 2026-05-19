package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.ItemCodec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class EnumCodecTest {

  private enum TestColor {
    RED, GREEN, BLUE
  }

  private enum TestShape {
    CIRCLE, SQUARE
  }

  private EnumCodec<TestColor> cachedCodec;
  private EnumCodec<TestColor> freshCodec;

  @Before
  public void setUp() {
    this.cachedCodec = EnumCodec.getInstance(TestColor.class);
    this.freshCodec = EnumCodec.createInstance(TestColor.class, null);
  }

  // ── Factory methods and hierarchy ────────────────────────────────

  @Test
  public void createInstance_returns_nonNull() {
    assertNotNull(this.freshCodec);
  }

  @Test
  public void getInstance_returns_same_instance_for_same_enum_class() {
    assertSame(this.cachedCodec, EnumCodec.getInstance(TestColor.class));
  }

  @Test
  public void multiple_enum_types_produce_different_codecs() {
    EnumCodec<TestShape> shapeCodec = EnumCodec.getInstance(TestShape.class);

    assertNotSame(this.cachedCodec, shapeCodec);
  }

  @Test
  public void class_implementsItemCodec() {
    assertTrue(ItemCodec.class.isAssignableFrom(EnumCodec.class));
  }

  @Test
  public void constructor_is_private_and_matches_source_signature() throws Exception {
    Constructor<?> constructor = EnumCodec.class.getDeclaredConstructor(Class.class, EnumCodec.LocalizationCustomizer.class);

    assertNotNull(constructor);
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  // ── Public API ────────────────────────────────────────────────────

  @Test
  public void getValueClass_returns_correct_class() {
    assertEquals(TestColor.class, this.cachedCodec.getValueClass());
  }

  @Test
  public void createInstance_with_null_customizer_preserves_null_customizer() {
    assertNull(this.freshCodec.getLocalizationCustomizer());
  }

  @Test
  public void appendRepresentation_appends_enum_name() {
    StringBuilder builder = new StringBuilder();

    this.freshCodec.appendRepresentation(builder, TestColor.GREEN);

    assertEquals("GREEN", builder.toString());
  }

  @Test
  public void appendRepresentation_null_appends_null_literal() {
    StringBuilder builder = new StringBuilder();

    this.freshCodec.appendRepresentation(builder, null);

    assertEquals("null", builder.toString());
  }

  @Test
  public void toString_contains_class_info() {
    String text = this.cachedCodec.toString();

    assertTrue(text.contains(EnumCodec.class.getName()));
    assertTrue(text.contains(TestColor.class.getName()));
  }

  @Test
  public void localizationCustomizer_inner_interface_exists() throws Exception {
    Class<?> customizerType = Class.forName("org.lgna.croquet.codecs.EnumCodec$LocalizationCustomizer");

    assertTrue(customizerType.isInterface());
    assertEquals(EnumCodec.class, customizerType.getDeclaringClass());
  }

  @Test
  public void encodeValue_and_decodeValue_methods_have_expected_signatures() throws Exception {
    Method encodeValue = EnumCodec.class.getDeclaredMethod("encodeValue", BinaryEncoder.class, Enum.class);
    Method decodeValue = EnumCodec.class.getDeclaredMethod("decodeValue", BinaryDecoder.class);

    assertEquals(void.class, encodeValue.getReturnType());
    assertEquals(Enum.class, decodeValue.getReturnType());
    assertTrue(Modifier.isPublic(encodeValue.getModifiers()));
    assertTrue(Modifier.isPublic(decodeValue.getModifiers()));
  }

  // ── Runtime behavior ──────────────────────────────────────────────

  @Test
  public void encodeValue_and_decodeValue_roundTrip() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();

    this.cachedCodec.encodeValue(encoder, TestColor.BLUE);
    TestColor decoded = this.cachedCodec.decodeValue(encoder.createDecoder());

    assertEquals(TestColor.BLUE, decoded);
  }
}
