package org.alice.ide.croquet.codecs.typeeditor;

import org.alice.ide.croquet.codecs.typeeditor.DeclarationCompositeCodec;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link DeclarationCompositeCodec} — enum singleton codec
 * for DeclarationComposite. Only stateless methods are tested headless;
 * decodeValue depends on IDE.getActiveInstance().
 */
public class DeclarationCompositeCodecTest {

  // ---- enum singleton ----

  @Test
  public void singleton_isNotNull() {
    assertNotNull(DeclarationCompositeCodec.SINGLETON);
  }

  @Test
  public void singleton_hasSingleConstant() {
    DeclarationCompositeCodec[] values = DeclarationCompositeCodec.values();
    assertEquals(1, values.length);
  }

  @Test
  public void valueOf_SINGLETON() {
    DeclarationCompositeCodec codec = DeclarationCompositeCodec.valueOf("SINGLETON");
    assertNotNull(codec);
    assertSame(DeclarationCompositeCodec.SINGLETON, codec);
  }

  // ---- getValueClass ----

  @Test
  public void getValueClass_returnsDeclarationComposite() {
    Class<?> cls = DeclarationCompositeCodec.SINGLETON.getValueClass();
    assertNotNull(cls);
    assertEquals("DeclarationComposite", cls.getSimpleName());
  }

  // ---- appendRepresentation null safety ----

  @Test
  public void appendRepresentation_withNull_doesNotThrow() {
    StringBuilder sb = new StringBuilder();
    DeclarationCompositeCodec.SINGLETON.appendRepresentation(sb, null);
    assertTrue(sb.toString().contains("null"));
  }

  @Test
  public void appendRepresentation_withNull_appendsSomething() {
    StringBuilder sb = new StringBuilder();
    DeclarationCompositeCodec.SINGLETON.appendRepresentation(sb, null);
    assertFalse(sb.toString().isEmpty());
  }

  // ---- ItemCodec interface compliance ----

  @Test
  public void implementsItemCodec() {
    assertTrue(DeclarationCompositeCodec.SINGLETON instanceof org.lgna.croquet.ItemCodec);
  }

  @Test
  public void getValueClass_isNotNull() {
    assertNotNull(DeclarationCompositeCodec.SINGLETON.getValueClass());
  }
}
