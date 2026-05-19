package org.lgna.croquet.codecs;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link EnumCodec} — round-trip BinaryEncoder, singleton cache,
 * multiple enum types, factory methods, and edge cases.
 */
public class EnumCodecCoverageTest {

  private enum Color { RED, GREEN, BLUE }
  private enum Size { SMALL, MEDIUM, LARGE, EXTRA_LARGE }
  private enum Empty {}
  private enum Single { ONLY }

  // ── getInstance singleton ─────────────────────────────────────────

  @Test
  public void getInstance_sameClass_returnsSameInstance() {
    EnumCodec<Color> a = EnumCodec.getInstance(Color.class);
    EnumCodec<Color> b = EnumCodec.getInstance(Color.class);
    assertSame(a, b);
  }

  @Test
  public void getInstance_differentClass_returnsDifferentInstance() {
    EnumCodec<Color> a = EnumCodec.getInstance(Color.class);
    EnumCodec<Size> b = EnumCodec.getInstance(Size.class);
    assertNotSame(a, b);
  }

  @Test
  public void getInstance_emptyEnum() {
    EnumCodec<Empty> codec = EnumCodec.getInstance(Empty.class);
    assertNotNull(codec);
    assertEquals(Empty.class, codec.getValueClass());
  }

  @Test
  public void getInstance_singleValueEnum() {
    EnumCodec<Single> codec = EnumCodec.getInstance(Single.class);
    assertEquals(Single.class, codec.getValueClass());
  }

  // ── createInstance ────────────────────────────────────────────────

  @Test
  public void createInstance_returnsNewInstance() {
    EnumCodec<Color> a = EnumCodec.createInstance(Color.class, null);
    EnumCodec<Color> b = EnumCodec.createInstance(Color.class, null);
    assertNotSame(a, b);
  }

  @Test
  public void createInstance_withCustomizer_storesIt() {
    EnumCodec.LocalizationCustomizer<Color> customizer = (s, v) -> s;
    EnumCodec<Color> codec = EnumCodec.createInstance(Color.class, customizer);
    assertSame(customizer, codec.getLocalizationCustomizer());
  }

  @Test
  public void createInstance_nullCustomizer() {
    EnumCodec<Color> codec = EnumCodec.createInstance(Color.class, null);
    assertNull(codec.getLocalizationCustomizer());
  }

  // ── getValueClass ─────────────────────────────────────────────────

  @Test
  public void getValueClass_returnsCorrectClass() {
    assertEquals(Color.class, EnumCodec.getInstance(Color.class).getValueClass());
    assertEquals(Size.class, EnumCodec.getInstance(Size.class).getValueClass());
  }

  // ── encode/decode round-trip ──────────────────────────────────────

  @Test
  public void roundTrip_allColorValues() {
    EnumCodec<Color> codec = EnumCodec.getInstance(Color.class);
    for (Color c : Color.values()) {
      edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder =
          new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
      codec.encodeValue(encoder, c);
      edu.cmu.cs.dennisc.codec.BinaryDecoder decoder = encoder.createDecoder();
      assertEquals(c, codec.decodeValue(decoder));
    }
  }

  @Test
  public void roundTrip_allSizeValues() {
    EnumCodec<Size> codec = EnumCodec.getInstance(Size.class);
    for (Size s : Size.values()) {
      edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder =
          new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
      codec.encodeValue(encoder, s);
      edu.cmu.cs.dennisc.codec.BinaryDecoder decoder = encoder.createDecoder();
      assertEquals(s, codec.decodeValue(decoder));
    }
  }

  @Test
  public void roundTrip_singleValue() {
    EnumCodec<Single> codec = EnumCodec.getInstance(Single.class);
    edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder =
        new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, Single.ONLY);
    edu.cmu.cs.dennisc.codec.BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(Single.ONLY, codec.decodeValue(decoder));
  }

  // ── appendRepresentation ──────────────────────────────────────────

  @Test
  public void appendRepresentation_nonNull_appendsName() {
    EnumCodec<Color> codec = EnumCodec.createInstance(Color.class, null);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, Color.RED);
    assertEquals("RED", sb.toString());
  }

  @Test
  public void appendRepresentation_green() {
    EnumCodec<Color> codec = EnumCodec.createInstance(Color.class, null);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, Color.GREEN);
    assertEquals("GREEN", sb.toString());
  }

  @Test
  public void appendRepresentation_null_appendsNull() {
    EnumCodec<Color> codec = EnumCodec.createInstance(Color.class, null);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_cachedAcrossCalls() {
    EnumCodec<Color> codec = EnumCodec.createInstance(Color.class, null);
    StringBuilder sb1 = new StringBuilder();
    codec.appendRepresentation(sb1, Color.BLUE);
    StringBuilder sb2 = new StringBuilder();
    codec.appendRepresentation(sb2, Color.BLUE);
    assertEquals(sb1.toString(), sb2.toString());
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_containsClassName() {
    assertTrue(EnumCodec.getInstance(Color.class).toString().contains("EnumCodec"));
  }

  @Test
  public void toString_containsEnumName() {
    assertTrue(EnumCodec.getInstance(Color.class).toString().contains("Color"));
  }

  @Test
  public void toString_differentEnums() {
    String colorStr = EnumCodec.getInstance(Color.class).toString();
    String sizeStr = EnumCodec.getInstance(Size.class).toString();
    assertNotEquals(colorStr, sizeStr);
  }

  // ── class structure ───────────────────────────────────────────────

  @Test
  public void enumCodec_implementsItemCodec() {
    assertTrue(org.lgna.croquet.ItemCodec.class.isAssignableFrom(EnumCodec.class));
  }

  @Test
  public void enumCodec_isPublic() {
    assertTrue(Modifier.isPublic(EnumCodec.class.getModifiers()));
  }

  @Test
  public void enumCodec_hasPrivateConstructor() {
    Constructor<?>[] ctors = EnumCodec.class.getDeclaredConstructors();
    for (Constructor<?> ctor : ctors) {
      assertTrue(Modifier.isPrivate(ctor.getModifiers()));
    }
  }

  @Test
  public void localizationCustomizerInterface_exists() {
    assertNotNull(EnumCodec.LocalizationCustomizer.class);
    assertTrue(EnumCodec.LocalizationCustomizer.class.isInterface());
  }
}
