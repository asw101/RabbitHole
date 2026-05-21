package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class StringCodecCoverageTest {
  @Test
  public void appendRepresentation_appendsExactValue() {
    StringBuilder sb = new StringBuilder("prefix:");
    StringCodec.SINGLETON.appendRepresentation(sb, "value");
    assertEquals("prefix:value", sb.toString());
  }

  @Test
  public void roundTrip_nullString_returnsNull() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    StringCodec.SINGLETON.encodeValue(encoder, null);
    encoder.flush();
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
    assertNull(StringCodec.SINGLETON.decodeValue(decoder));
  }

  @Test
  public void getValueClass_returnsStringClass() {
    assertEquals(String.class, StringCodec.SINGLETON.getValueClass());
  }
}
