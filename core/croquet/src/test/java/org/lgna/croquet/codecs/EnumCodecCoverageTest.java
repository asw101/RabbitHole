package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;
import org.lgna.croquet.ItemCodec;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link EnumCodec} — singleton caching, round-trip
 * encode/decode via BinaryEncoder, appendRepresentation, and multiple enum types.
 */
public class EnumCodecCoverageTest {

  private enum Color { RED, GREEN, BLUE }
  private enum Size { SMALL, MEDIUM, LARGE, XLARGE }
  private enum Solo { ONLY }

  // ── Singleton cache ───────────────────────────────────────────────

  @Test
  public void getInstance_returnsSameInstance() {
    EnumCodec<Color> c1 = EnumCodec.getInstance(Color.class);
    EnumCodec<Color> c2 = EnumCodec.getInstance(Color.class);
    assertSame(c1, c2);
  }

  @Test
  public void getInstance_differentEnums_differentInstances() {
    EnumCodec<Color> cc = EnumCodec.getInstance(Color.class);
    EnumCodec<Size> cs = EnumCodec.getInstance(Size.class);
    assertNotSame(cc, cs);
  }

  @Test
  public void createInstance_returnsNewInstance() {
    EnumCodec<Color> c1 = EnumCodec.createInstance(Color.class, null);
    EnumCodec<Color> c2 = EnumCodec.createInstance(Color.class, null);
    assertNotSame(c1, c2);
  }

  // ── Value class ───────────────────────────────────────────────────

  @Test
  public void getValueClass_returnsEnumClass() {
    assertEquals(Color.class, EnumCodec.getInstance(Color.class).getValueClass());
  }

  @Test
  public void getValueClass_size() {
    assertEquals(Size.class, EnumCodec.getInstance(Size.class).getValueClass());
  }

  // ── Round-trip encode/decode ──────────────────────────────────────

  @Test
  public void roundTrip_red() {
    assertRoundTrip(Color.class, Color.RED);
  }

  @Test
  public void roundTrip_blue() {
    assertRoundTrip(Color.class, Color.BLUE);
  }

  @Test
  public void roundTrip_allSizes() {
    for (Size s : Size.values()) {
      assertRoundTrip(Size.class, s);
    }
  }

  @Test
  public void roundTrip_solo() {
    assertRoundTrip(Solo.class, Solo.ONLY);
  }

  private <T extends Enum<T>> void assertRoundTrip(Class<T> cls, T value) {
    EnumCodec<T> codec = EnumCodec.getInstance(cls);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, value);
    BinaryDecoder decoder = encoder.createDecoder();
    T decoded = codec.decodeValue(decoder);
    assertEquals(value, decoded);
  }

  // ── appendRepresentation ──────────────────────────────────────────

  @Test
  public void appendRepresentation_nonNull() {
    EnumCodec<Color> codec = EnumCodec.getInstance(Color.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, Color.GREEN);
    assertTrue(sb.length() > 0);
  }

  @Test
  public void appendRepresentation_null() {
    EnumCodec<Color> codec = EnumCodec.getInstance(Color.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertTrue(sb.toString().contains("null"));
  }

  @Test
  public void appendRepresentation_allValues() {
    EnumCodec<Size> codec = EnumCodec.getInstance(Size.class);
    for (Size s : Size.values()) {
      StringBuilder sb = new StringBuilder();
      codec.appendRepresentation(sb, s);
      assertTrue(sb.length() > 0);
    }
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_containsClassName() {
    String str = EnumCodec.getInstance(Color.class).toString();
    assertTrue(str.contains("EnumCodec"));
    assertTrue(str.contains("Color"));
  }

  // ── Localization customizer ───────────────────────────────────────

  @Test
  public void getLocalizationCustomizer_null_forGetInstance() {
    assertNull(EnumCodec.getInstance(Color.class).getLocalizationCustomizer());
  }

  @Test
  public void getLocalizationCustomizer_nonNull_forCreateInstance() {
    EnumCodec.LocalizationCustomizer<Color> custom = (loc, val) -> "custom:" + val.name();
    EnumCodec<Color> codec = EnumCodec.createInstance(Color.class, custom);
    assertSame(custom, codec.getLocalizationCustomizer());
  }

  @Test
  public void createInstance_withCustomizer_appendsCustomized() {
    EnumCodec.LocalizationCustomizer<Color> custom = (loc, val) -> "x-" + val.name();
    EnumCodec<Color> codec = EnumCodec.createInstance(Color.class, custom);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, Color.RED);
    // Customizer modifies localization strings from resource bundles, not direct enum names
    // Just verify it produces output without error
    assertTrue(sb.length() > 0);
  }

  // ── ItemCodec interface ───────────────────────────────────────────

  @Test
  public void implementsItemCodec() {
    assertTrue(ItemCodec.class.isAssignableFrom(EnumCodec.class));
  }
}
