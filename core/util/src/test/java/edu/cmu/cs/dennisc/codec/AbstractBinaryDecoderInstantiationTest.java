package edu.cmu.cs.dennisc.codec;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AbstractBinaryDecoderInstantiationTest {
  public static final class ConstructorBinary implements BinaryEncodableAndDecodable {
    private final String value;

    private ConstructorBinary(String value) {
      this.value = value;
    }

    public ConstructorBinary(BinaryDecoder decoder) {
      this(decoder.decodeString());
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(value);
    }
  }

  public static final class ContextBinary implements BinaryEncodableAndDecodable {
    private final String value;

    private ContextBinary(String value) {
      this.value = value;
    }

    public ContextBinary(BinaryDecoder decoder, Object context) {
      this(context + ":" + decoder.decodeString());
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(value.substring(value.indexOf(':') + 1));
    }
  }

  public static final class DecodeMethodBinary implements BinaryEncodableAndDecodable {
    private String value;

    public DecodeMethodBinary() {
    }

    private DecodeMethodBinary(String value) {
      this.value = value;
    }

    public void decode(BinaryDecoder decoder) {
      this.value = decoder.decodeString();
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(value);
    }
  }

  @Test
  public void decodeBinaryEncodableAndDecodableUsesBinaryDecoderConstructorWhenAvailable() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encode(new ConstructorBinary("camera"));

    ConstructorBinary decoded = encoder.createDecoder().decodeBinaryEncodableAndDecodable();

    assertEquals("camera", decoded.value);
  }

  @Test
  public void decodeBinaryEncodableAndDecodableUsesContextAwareConstructorWhenRequested() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encode(new ContextBinary("seed:transform"));

    ContextBinary decoded = encoder.createDecoder().decodeBinaryEncodableAndDecodable("scene");

    assertEquals("scene:transform", decoded.value);
  }

  @Test
  public void decodeBinaryEncodableAndDecodableFallsBackToDecodeMethodWhenConstructorIsMissing() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encode(new DecodeMethodBinary("vehicle"));

    DecodeMethodBinary decoded = encoder.createDecoder().decodeBinaryEncodableAndDecodable();

    assertEquals("vehicle", decoded.value);
  }

  @Test
  public void decodeBinaryEncodableAndDecodableArrayUsesRequestedComponentType() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encode(new BinaryEncodableAndDecodable[] {
        new ConstructorBinary("first"),
        new ConstructorBinary("second")
    });

    ConstructorBinary[] decoded = encoder.createDecoder().decodeBinaryEncodableAndDecodableArray(ConstructorBinary.class);

    assertEquals("first", decoded[0].value);
    assertEquals("second", decoded[1].value);
  }
}
