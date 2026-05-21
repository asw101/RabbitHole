package org.alice.stageide.apis.org.lgna.story.codecs;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.junit.Test;
import org.lgna.story.Key;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class KeyCodecTest {
  private InputStreamBinaryDecoder decoderFor(Key value) throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(outputStream);
    KeyCodec.SINGLETON.encodeValue(encoder, value);
    encoder.flush();
    return new InputStreamBinaryDecoder(new ByteArrayInputStream(outputStream.toByteArray()));
  }

  @Test
  public void getValueClassReturnsKeyClass() {
    assertEquals(Key.class, KeyCodec.SINGLETON.getValueClass());
  }

  @Test
  public void encodeAndDecodeRoundTripPreservesConcreteKey() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(Key.LEFT);
    assertEquals(Key.LEFT, KeyCodec.SINGLETON.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecodeRoundTripPreservesNull() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(null);
    assertNull(KeyCodec.SINGLETON.decodeValue(decoder));
  }

  @Test
  public void appendRepresentationUsesKeyToString() {
    StringBuilder sb = new StringBuilder("prefix:");
    KeyCodec.SINGLETON.appendRepresentation(sb, Key.RIGHT);
    assertEquals("prefix:" + Key.RIGHT, sb.toString());
  }
}
