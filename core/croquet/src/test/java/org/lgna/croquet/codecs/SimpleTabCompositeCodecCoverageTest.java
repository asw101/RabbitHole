package org.lgna.croquet.codecs;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Reflection-based structure tests for {@link SimpleTabCompositeCodec}.
 * Cannot instantiate without AbstractTabComposite infrastructure.
 */
public class SimpleTabCompositeCodecCoverageTest {

  // ── Class structure ───────────────────────────────────────────────

  @Test
  public void simpleTabCompositeCodec_implementsItemCodec() {
    assertTrue(org.lgna.croquet.ItemCodec.class.isAssignableFrom(SimpleTabCompositeCodec.class));
  }

  @Test
  public void simpleTabCompositeCodec_isPublic() {
    assertTrue(Modifier.isPublic(SimpleTabCompositeCodec.class.getModifiers()));
  }

  @Test
  public void simpleTabCompositeCodec_isNotAbstract() {
    assertFalse(Modifier.isAbstract(SimpleTabCompositeCodec.class.getModifiers()));
  }

  // ── Method presence ───────────────────────────────────────────────

  @Test
  public void hasGetValueClass() {
    boolean found = false;
    for (Method m : SimpleTabCompositeCodec.class.getMethods()) {
      if ("getValueClass".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Should have getValueClass", found);
  }

  @Test
  public void hasDecodeValue() {
    boolean found = false;
    for (Method m : SimpleTabCompositeCodec.class.getMethods()) {
      if ("decodeValue".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Should have decodeValue", found);
  }

  @Test
  public void hasEncodeValue() {
    boolean found = false;
    for (Method m : SimpleTabCompositeCodec.class.getMethods()) {
      if ("encodeValue".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Should have encodeValue", found);
  }

  @Test
  public void hasAppendRepresentation() {
    boolean found = false;
    for (Method m : SimpleTabCompositeCodec.class.getMethods()) {
      if ("appendRepresentation".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Should have appendRepresentation", found);
  }

  // ── Constructor presence ──────────────────────────────────────────

  @Test
  public void hasConstructor() {
    Constructor<?>[] ctors = SimpleTabCompositeCodec.class.getDeclaredConstructors();
    assertTrue("Should have at least one constructor", ctors.length > 0);
  }

  // ── Package ───────────────────────────────────────────────────────

  @Test
  public void isInCodecsPackage() {
    assertEquals("org.lgna.croquet.codecs", SimpleTabCompositeCodec.class.getPackage().getName());
  }

  // ── DefaultItemCodec tests ────────────────────────────────────────

  @Test
  public void defaultItemCodec_getValueClass() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    assertEquals(String.class, codec.getValueClass());
  }

  @Test
  public void defaultItemCodec_isPublic() {
    assertTrue(Modifier.isPublic(DefaultItemCodec.class.getModifiers()));
  }

  @Test
  public void defaultItemCodec_extendsAbstractItemCodec() {
    assertTrue(AbstractItemCodec.class.isAssignableFrom(DefaultItemCodec.class));
  }

  @Test
  public void defaultItemCodec_appendRepresentation_string() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, "test");
    assertEquals("test", sb.toString());
  }

  @Test
  public void defaultItemCodec_appendRepresentation_null() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  // ── FileCodec tests ───────────────────────────────────────────────

  @Test
  public void fileCodec_implementsItemCodec() {
    assertTrue(org.lgna.croquet.ItemCodec.class.isAssignableFrom(FileCodec.class));
  }

  @Test
  public void fileCodec_getValueClass() {
    assertEquals(java.io.File.class, FileCodec.SINGLETON.getValueClass());
  }

  @Test
  public void fileCodec_singleton_notNull() {
    assertNotNull(FileCodec.SINGLETON);
  }

  @Test
  public void fileCodec_appendRepresentation_file() {
    StringBuilder sb = new StringBuilder();
    FileCodec.SINGLETON.appendRepresentation(sb, new java.io.File("/tmp/x.txt"));
    assertFalse(sb.toString().isEmpty());
  }
}
