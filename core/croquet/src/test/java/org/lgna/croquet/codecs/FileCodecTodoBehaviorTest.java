package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class FileCodecTodoBehaviorTest {
  @Test
  public void encodeDecode_nullFile_roundTripsToNull() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();

    FileCodec.SINGLETON.encodeValue(encoder, null);
    BinaryDecoder decoder = encoder.createDecoder();

    assertNull(FileCodec.SINGLETON.decodeValue(decoder));
  }

  @Test
  public void encode_nonNullFile_throwsTodoException() {
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> FileCodec.SINGLETON.encodeValue(new ByteArrayBinaryEncoder(), new File("alpha.txt")));

    assertEquals("todo", exception.getMessage());
  }

  @Test
  public void decode_trueMarker_throwsTodoException() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encode(true);

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> FileCodec.SINGLETON.decodeValue(encoder.createDecoder()));

    assertEquals("todo", exception.getMessage());
  }

  @Test
  public void appendRepresentation_includesPathText() {
    StringBuilder sb = new StringBuilder();

    FileCodec.SINGLETON.appendRepresentation(sb, new File("alpha.txt"));

    assertTrue(sb.toString().contains("alpha.txt"));
  }
}
