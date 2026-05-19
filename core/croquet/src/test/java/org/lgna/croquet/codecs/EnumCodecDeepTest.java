package org.lgna.croquet.codecs;

import org.junit.Test;
import org.lgna.croquet.ItemCodec;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link EnumCodec} — singleton caching, appendRepresentation
 * with multiple enum types, toString, and factory methods.
 */
public class EnumCodecDeepTest {

  private enum TestColor { RED, GREEN, BLUE }
  private enum TestSize { SMALL, MEDIUM, LARGE }
  private enum SingleValue { ONLY }
  private enum Empty {}

  // ── getInstance ───────────────────────────────────────────────────

  @Test
  public void getInstance_nonNull() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    assertNotNull(codec);
  }

  @Test
  public void getInstance_sameClass_sameInstance() {
    EnumCodec<TestColor> a = EnumCodec.getInstance(TestColor.class);
    EnumCodec<TestColor> b = EnumCodec.getInstance(TestColor.class);
    assertSame(a, b);
  }

  @Test
  public void getInstance_differentClass_differentInstance() {
    EnumCodec<TestColor> a = EnumCodec.getInstance(TestColor.class);
    EnumCodec<TestSize> b = EnumCodec.getInstance(TestSize.class);
    assertNotSame(a, b);
  }

  // ── getValueClass ─────────────────────────────────────────────────

  @Test
  public void getValueClass_matchesColor() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    assertEquals(TestColor.class, codec.getValueClass());
  }

  @Test
  public void getValueClass_matchesSize() {
    EnumCodec<TestSize> codec = EnumCodec.getInstance(TestSize.class);
    assertEquals(TestSize.class, codec.getValueClass());
  }

  // ── appendRepresentation ──────────────────────────────────────────

  @Test
  public void appendRepresentation_nonNullValue_appendsText() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, TestColor.RED);
    assertTrue(sb.length() > 0);
  }

  @Test
  public void appendRepresentation_usesEnumName_whenNoBundle() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, TestColor.GREEN);
    assertTrue(sb.toString().contains("GREEN"));
  }

  @Test
  public void appendRepresentation_nullValue_appendsNull() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_allValues() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    for (TestColor c : TestColor.values()) {
      StringBuilder sb = new StringBuilder();
      codec.appendRepresentation(sb, c);
      assertTrue(sb.length() > 0);
    }
  }

  @Test
  public void appendRepresentation_singleValueEnum() {
    EnumCodec<SingleValue> codec = EnumCodec.getInstance(SingleValue.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, SingleValue.ONLY);
    assertTrue(sb.toString().contains("ONLY"));
  }

  // ── createInstance with customizer ────────────────────────────────

  @Test
  public void createInstance_withCustomizer_nonNull() {
    EnumCodec<TestColor> codec = EnumCodec.createInstance(TestColor.class,
        (loc, val) -> loc + "!");
    assertNotNull(codec);
  }

  @Test
  public void createInstance_getLocalizationCustomizer_nonNull() {
    EnumCodec<TestColor> codec = EnumCodec.createInstance(TestColor.class,
        (loc, val) -> loc + "!");
    assertNotNull(codec.getLocalizationCustomizer());
  }

  @Test
  public void getInstance_getLocalizationCustomizer_null() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    assertNull(codec.getLocalizationCustomizer());
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_containsClassName() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    assertTrue(codec.toString().contains("EnumCodec"));
  }

  @Test
  public void toString_containsEnumClassName() {
    EnumCodec<TestColor> codec = EnumCodec.getInstance(TestColor.class);
    assertTrue(codec.toString().contains("TestColor"));
  }

  @Test
  public void toString_nonEmpty() {
    EnumCodec<TestSize> codec = EnumCodec.getInstance(TestSize.class);
    assertFalse(codec.toString().isEmpty());
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_implementsItemCodec() {
    assertTrue(ItemCodec.class.isAssignableFrom(EnumCodec.class));
  }

  @Test
  public void class_isNotAbstract() {
    assertFalse(Modifier.isAbstract(EnumCodec.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(EnumCodec.class.getModifiers()));
  }

  // ── Inner interface ───────────────────────────────────────────────

  @Test
  public void innerInterface_LocalizationCustomizer_exists() {
    Class<?>[] inners = EnumCodec.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> inner : inners) {
      if (inner.getSimpleName().equals("LocalizationCustomizer")) {
        found = true;
        assertTrue(inner.isInterface());
      }
    }
    assertTrue("Expected LocalizationCustomizer interface", found);
  }

  // ── Method existence ──────────────────────────────────────────────

  @Test
  public void method_decodeValue_exists() throws Exception {
    Method m = EnumCodec.class.getMethod("decodeValue",
        edu.cmu.cs.dennisc.codec.BinaryDecoder.class);
    assertNotNull(m);
    assertTrue(Modifier.isFinal(m.getModifiers()));
  }

  @Test
  public void method_encodeValue_exists() throws Exception {
    boolean found = false;
    for (Method m : EnumCodec.class.getMethods()) {
      if (m.getName().equals("encodeValue")) {
        found = true;
        break;
      }
    }
    assertTrue("encodeValue method should exist", found);
  }

  @Test
  public void method_getInstance_exists() throws Exception {
    Method m = EnumCodec.class.getMethod("getInstance", Class.class);
    assertNotNull(m);
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void method_createInstance_exists() throws Exception {
    Method m = EnumCodec.class.getMethod("createInstance", Class.class,
        EnumCodec.LocalizationCustomizer.class);
    assertNotNull(m);
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  // ── Constructor is private ────────────────────────────────────────

  @Test
  public void constructor_isPrivate() {
    var ctors = EnumCodec.class.getDeclaredConstructors();
    for (var ctor : ctors) {
      assertTrue(Modifier.isPrivate(ctor.getModifiers()));
    }
  }
}
