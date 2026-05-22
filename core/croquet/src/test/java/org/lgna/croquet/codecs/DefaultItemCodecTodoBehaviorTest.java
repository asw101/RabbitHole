package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultItemCodecTodoBehaviorTest {
  @Test
  public void createInstance_exposesValueClassAndRepresentation() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);
    StringBuilder sb = new StringBuilder();

    codec.appendRepresentation(sb, "value");

    assertEquals(String.class, codec.getValueClass());
    assertEquals("value", sb.toString());
  }

  @Test
  public void encodeValue_throwsTodoException() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> codec.encodeValue(new ByteArrayBinaryEncoder(), "value"));

    assertEquals("todo", exception.getMessage());
  }

  @Test
  public void decodeValue_throwsTodoException() {
    DefaultItemCodec<String> codec = DefaultItemCodec.createInstance(String.class);

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> codec.decodeValue(new ByteArrayBinaryEncoder().createDecoder()));

    assertEquals("todo", exception.getMessage());
  }
}
