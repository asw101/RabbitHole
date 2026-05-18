package org.alice.ide.croquet.codecs.typeeditor;

import org.alice.ide.declarationseditor.DeclarationComposite;
import org.junit.Test;
import org.lgna.croquet.ItemCodec;

import static org.junit.Assert.*;

/**
 * Tests for {@link DeclarationCompositeCodec} — enum-singleton codec
 * for DeclarationComposite instances.
 */
public class DeclarationCompositeCodecTest {

  // ---- enum singleton ----

  @Test
  public void singleton_isNotNull() {
    assertNotNull(DeclarationCompositeCodec.SINGLETON);
  }

  @Test
  public void singleton_isSameAsValueOf() {
    assertSame(DeclarationCompositeCodec.SINGLETON,
        DeclarationCompositeCodec.valueOf("SINGLETON"));
  }

  @Test
  public void values_hasExactlyOneElement() {
    assertEquals(1, DeclarationCompositeCodec.values().length);
  }

  @Test
  public void singleton_ordinal_isZero() {
    assertEquals(0, DeclarationCompositeCodec.SINGLETON.ordinal());
  }

  @Test
  public void singleton_name_isSINGLETON() {
    assertEquals("SINGLETON", DeclarationCompositeCodec.SINGLETON.name());
  }

  // ---- getValueClass ----

  @Test
  public void getValueClass_returnsDeclarationCompositeClass() {
    Class<DeclarationComposite<?, ?>> cls = DeclarationCompositeCodec.SINGLETON.getValueClass();
    assertNotNull(cls);
    assertTrue(DeclarationComposite.class.isAssignableFrom(cls));
  }

  // ---- appendRepresentation null-safety ----

  @Test
  public void appendRepresentation_withNull_appendsNullString() {
    StringBuilder sb = new StringBuilder();
    DeclarationCompositeCodec.SINGLETON.appendRepresentation(sb, null);
    String result = sb.toString();
    assertEquals("null", result);
  }

  // ---- ItemCodec interface compliance ----

  @Test
  public void implementsItemCodec() {
    assertTrue(DeclarationCompositeCodec.SINGLETON instanceof ItemCodec);
  }

  @Test
  public void implementsItemCodec_ofDeclarationComposite() {
    ItemCodec<DeclarationComposite<?, ?>> codec = DeclarationCompositeCodec.SINGLETON;
    assertNotNull(codec);
  }

  // ---- enum contract ----

  @Test(expected = IllegalArgumentException.class)
  public void valueOf_invalidName_throws() {
    DeclarationCompositeCodec.valueOf("INVALID");
  }

  @Test(expected = NullPointerException.class)
  public void valueOf_null_throws() {
    DeclarationCompositeCodec.valueOf(null);
  }
}
