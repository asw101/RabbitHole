package org.alice.ide.project.codecs;

import org.alice.ide.ProjectDocument;
import org.junit.Test;
import org.lgna.croquet.ItemCodec;

import static org.junit.Assert.*;

public class ProjectDocumentCodecTest {
  @Test
  public void singletonEnumImplementsItemCodecForProjectDocument() {
    assertTrue(ProjectDocumentCodec.class.isEnum());
    assertTrue(ItemCodec.class.isAssignableFrom(ProjectDocumentCodec.class));
    assertEquals(ProjectDocument.class, ProjectDocumentCodec.SINGLETON.getValueClass());
  }

  @Test
  public void decodeAndEncodeAreExplicitlyUnsupported() {
    try {
      ProjectDocumentCodec.SINGLETON.decodeValue(null);
      fail("Expected RuntimeException");
    } catch (RuntimeException expected) {
      assertNotNull(expected);
    }

    try {
      ProjectDocumentCodec.SINGLETON.encodeValue(null, null);
      fail("Expected RuntimeException");
    } catch (RuntimeException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void appendRepresentationAppendsNullLiteralForNullValue() {
    StringBuilder sb = new StringBuilder("prefix:");
    ProjectDocumentCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("prefix:null", sb.toString());
  }
}
