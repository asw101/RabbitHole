package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.ItemCodec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class DefaultItemCodecCoverageTest {

  private DefaultItemCodec<String> codec;

  @Before
  public void setUp() {
    this.codec = DefaultItemCodec.createInstance(String.class);
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsAbstractItemCodec() {
    assertEquals(AbstractItemCodec.class, DefaultItemCodec.class.getSuperclass());
  }

  @Test
  public void class_implementsItemCodec_transitively() {
    assertTrue(ItemCodec.class.isAssignableFrom(DefaultItemCodec.class));
  }

  @Test
  public void class_hierarchy_depth_is_two_levels_beforeObject() {
    assertEquals(2, getHierarchyDepth(DefaultItemCodec.class));
  }

  // ── Constructor and inherited API ────────────────────────────────

  @Test
  public void constructor_signature_is_private_class_constructor() throws Exception {
    Constructor<?> constructor = DefaultItemCodec.class.getDeclaredConstructor(Class.class);

    assertNotNull(constructor);
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void getValueClass_returns_valueClass() {
    assertEquals(String.class, this.codec.getValueClass());
  }

  @Test
  public void appendRepresentation_method_exists_and_appends_value() throws Exception {
    Method method = DefaultItemCodec.class.getMethod("appendRepresentation", StringBuilder.class, Object.class);
    StringBuilder builder = new StringBuilder();

    this.codec.appendRepresentation(builder, "alpha");

    assertNotNull(method);
    assertEquals("alpha", builder.toString());
  }

  // ── Exception behavior ────────────────────────────────────────────

  @Test
  public void decodeValue_throwsRuntimeException() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encode("value");
    BinaryDecoder decoder = encoder.createDecoder();

    try {
      this.codec.decodeValue(decoder);
      fail("Expected RuntimeException");
    } catch (RuntimeException exception) {
      assertEquals("todo", exception.getMessage());
    }
  }

  @Test
  public void encodeValue_throwsRuntimeException() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();

    try {
      this.codec.encodeValue(encoder, "value");
      fail("Expected RuntimeException");
    } catch (RuntimeException exception) {
      assertEquals("todo", exception.getMessage());
    }
  }

  private static int getHierarchyDepth(Class<?> type) {
    int depth = 0;
    Class<?> current = type;
    while ((current != null) && (current != Object.class)) {
      depth++;
      current = current.getSuperclass();
    }
    return depth;
  }
}
