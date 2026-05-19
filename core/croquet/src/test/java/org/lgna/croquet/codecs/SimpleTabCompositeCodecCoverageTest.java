package org.lgna.croquet.codecs;

import org.junit.Test;
import org.lgna.croquet.ItemCodec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Reflection-based coverage tests for {@link SimpleTabCompositeCodec}.
 * Constructor requires AbstractTabComposite subclass, limiting full behavioral testing.
 */
public class SimpleTabCompositeCodecCoverageTest {

  // ── Class structure ───────────────────────────────────────────────

  @Test
  public void implementsItemCodec() {
    assertTrue(ItemCodec.class.isAssignableFrom(SimpleTabCompositeCodec.class));
  }

  @Test
  public void class_isConcrete() {
    assertFalse(Modifier.isAbstract(SimpleTabCompositeCodec.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(SimpleTabCompositeCodec.class.getModifiers()));
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructor_isPrivate() throws Exception {
    Constructor<?>[] ctors = SimpleTabCompositeCodec.class.getDeclaredConstructors();
    assertTrue("Expected at least one constructor", ctors.length > 0);
    for (Constructor<?> c : ctors) {
      assertTrue("Expected private constructor", Modifier.isPrivate(c.getModifiers()));
    }
  }

  // ── Static factory ────────────────────────────────────────────────

  @Test
  public void getInstance_methodExists() throws Exception {
    Method m = SimpleTabCompositeCodec.class.getDeclaredMethod("getInstance", Class.class);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertTrue(Modifier.isSynchronized(m.getModifiers()));
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_getValueClass_exists() throws Exception {
    Method m = SimpleTabCompositeCodec.class.getMethod("getValueClass");
    assertNotNull(m);
    assertEquals(Class.class, m.getReturnType());
  }

  @Test
  public void method_decodeValue_exists() throws Exception {
    Method m = SimpleTabCompositeCodec.class.getMethod("decodeValue",
        edu.cmu.cs.dennisc.codec.BinaryDecoder.class);
    assertNotNull(m);
  }

  @Test
  public void method_encodeValue_exists() throws Exception {
    Method m = SimpleTabCompositeCodec.class.getMethod("encodeValue",
        edu.cmu.cs.dennisc.codec.BinaryEncoder.class, Object.class);
    assertNotNull(m);
  }

  @Test
  public void method_appendRepresentation_exists() throws Exception {
    Method m = SimpleTabCompositeCodec.class.getMethod("appendRepresentation",
        StringBuilder.class, Object.class);
    assertNotNull(m);
  }

  // ── Type parameter ────────────────────────────────────────────────

  @Test
  public void class_hasOneTypeParameter() {
    assertEquals(1, SimpleTabCompositeCodec.class.getTypeParameters().length);
  }
}
