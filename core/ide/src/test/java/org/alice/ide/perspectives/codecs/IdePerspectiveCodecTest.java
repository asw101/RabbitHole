package org.alice.ide.perspectives.codecs;

import org.alice.ide.perspectives.ProjectPerspective;
import org.junit.Test;
import org.lgna.croquet.ItemCodec;

import static org.junit.Assert.*;

public class IdePerspectiveCodecTest {
  @Test
  public void singletonEnumImplementsItemCodecForProjectPerspective() {
    assertTrue(IdePerspectiveCodec.class.isEnum());
    assertTrue(ItemCodec.class.isAssignableFrom(IdePerspectiveCodec.class));
    assertEquals(ProjectPerspective.class, IdePerspectiveCodec.SINGLETON.getValueClass());
  }

  @Test
  public void decodeAndEncodeAreExplicitlyUnsupported() {
    try {
      IdePerspectiveCodec.SINGLETON.decodeValue(null);
      fail("Expected RuntimeException");
    } catch (RuntimeException expected) {
      assertNotNull(expected);
    }

    try {
      IdePerspectiveCodec.SINGLETON.encodeValue(null, null);
      fail("Expected RuntimeException");
    } catch (RuntimeException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void appendRepresentationAppendsNullLiteralForNullValue() {
    StringBuilder sb = new StringBuilder("prefix:");
    IdePerspectiveCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("prefix:null", sb.toString());
  }
}
