package org.alice.ide.croquet.codecs.typeeditor;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.alice.ide.declarationseditor.DeclarationComposite;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class DeclarationCompositeCodecCoverageTest {
  @Test
  public void encodeNull_thenDecode_returnsNull() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    DeclarationCompositeCodec.SINGLETON.encodeValue(encoder, null);
    encoder.flush();
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
    assertNull(DeclarationCompositeCodec.SINGLETON.decodeValue(decoder));
  }

  @Test
  public void appendRepresentation_withNull_appendsNullText() {
    StringBuilder sb = new StringBuilder();
    DeclarationCompositeCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void getValueClass_returnsDeclarationCompositeType() {
    assertTrue(DeclarationComposite.class.isAssignableFrom(DeclarationCompositeCodec.SINGLETON.getValueClass()));
  }
}
