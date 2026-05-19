package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;
import org.lgna.croquet.ItemCodec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link DefaultItemCodec} — factory method, getValueClass,
 * appendRepresentation, encode/decode characterization (RuntimeException),
 * and class structure.
 */
public class DefaultItemCodecCoverageTest {

  // ── Factory method ────────────────────────────────────────────────

  @Test
  public void createInstance_returnsNonNull() {
    assertNotNull(DefaultItemCodec.createInstance(String.class));
  }

  @Test
  public void createInstance_differentCallsReturnDifferentInstances() {
    DefaultItemCodec<String> a = DefaultItemCodec.createInstance(String.class);
    DefaultItemCodec<String> b = DefaultItemCodec.createInstance(String.class);
    assertNotSame(a, b);
  }

  @Test
  public void createInstance_integerType() {
    DefaultItemCodec<Integer> codec = DefaultItemCodec.createInstance(Integer.class);
    assertNotNull(codec);
  }

  @Test
  public void createInstance_objectType() {
    DefaultItemCodec<Object> codec = DefaultItemCodec.createInstance(Object.class);
    assertNotNull(codec);
  }

  // ── getValueClass ─────────────────────────────────────────────────

  @Test
  public void getValueClass_string() {
    assertEquals(String.class, DefaultItemCodec.createInstance(String.class).getValueClass());
  }

  @Test
  public void getValueClass_integer() {
    assertEquals(Integer.class, DefaultItemCodec.createInstance(Integer.class).getValueClass());
  }

  @Test
  public void getValueClass_double() {
    assertEquals(Double.class, DefaultItemCodec.createInstance(Double.class).getValueClass());
  }

  @Test
  public void getValueClass_object() {
    assertEquals(Object.class, DefaultItemCodec.createInstance(Object.class).getValueClass());
  }

  // ── appendRepresentation (inherited from AbstractItemCodec) ───────

  @Test
  public void appendRepresentation_string() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, "hello");
    assertEquals("hello", sb.toString());
  }

  @Test
  public void appendRepresentation_integer() {
    DefaultItemCodec<Integer> codec = DefaultItemCodec.createInstance(Integer.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, 42);
    assertEquals("42", sb.toString());
  }

  @Test
  public void appendRepresentation_null() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_appendsToExisting() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    StringBuilder sb = new StringBuilder("prefix:");
    codec.appendRepresentation(sb, "val");
    assertEquals("prefix:val", sb.toString());
  }

  // ── encode characterization: throws RuntimeException ──────────────

  @Test(expected = RuntimeException.class)
  public void encodeValue_throwsRuntimeException() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, "test");
  }

  @Test
  public void encodeValue_exceptionMessage_containsTodo() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    try {
      codec.encodeValue(encoder, "test");
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("todo", e.getMessage());
    }
  }

  // ── decode characterization: throws RuntimeException ──────────────

  @Test(expected = RuntimeException.class)
  public void decodeValue_throwsRuntimeException() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.decodeValue(encoder.createDecoder());
  }

  @Test
  public void decodeValue_exceptionMessage_containsTodo() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    try {
      codec.decodeValue(encoder.createDecoder());
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("todo", e.getMessage());
    }
  }

  // ── class structure ───────────────────────────────────────────────

  @Test
  public void defaultItemCodec_extendsAbstractItemCodec() {
    assertTrue(AbstractItemCodec.class.isAssignableFrom(DefaultItemCodec.class));
  }

  @Test
  public void defaultItemCodec_implementsItemCodec() {
    assertTrue(ItemCodec.class.isAssignableFrom(DefaultItemCodec.class));
  }

  @Test
  public void defaultItemCodec_isPublic() {
    assertTrue(Modifier.isPublic(DefaultItemCodec.class.getModifiers()));
  }

  @Test
  public void defaultItemCodec_isNotAbstract() {
    assertFalse(Modifier.isAbstract(DefaultItemCodec.class.getModifiers()));
  }

  @Test
  public void defaultItemCodec_constructorIsPrivate() {
    Constructor<?>[] ctors = DefaultItemCodec.class.getDeclaredConstructors();
    assertTrue("Should have at least one constructor", ctors.length > 0);
    for (Constructor<?> ctor : ctors) {
      assertTrue("Constructor should be private", Modifier.isPrivate(ctor.getModifiers()));
    }
  }

  @Test
  public void defaultItemCodec_hasCreateInstanceFactory() throws NoSuchMethodException {
    java.lang.reflect.Method m = DefaultItemCodec.class.getMethod("createInstance", Class.class);
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void defaultItemCodec_isInCodecsPackage() {
    assertEquals("org.lgna.croquet.codecs",
        DefaultItemCodec.class.getPackage().getName());
  }
}
