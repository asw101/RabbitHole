package edu.cmu.cs.dennisc.codec;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.UUID;

import static org.junit.Assert.*;

public class InputStreamBinaryDecoderTest {
  private interface EncoderAction {
    void encode(OutputStreamBinaryEncoder encoder) throws Exception;
  }

  private static final File BASE_DIRECTORY = new File("target/test-artifacts/InputStreamBinaryDecoderTest");

  private InputStreamBinaryDecoder decoderFor(EncoderAction action) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    action.encode(encoder);
    encoder.flush();
    return new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
  }

  private File encodedFile(String name, EncoderAction action) throws Exception {
    if (!BASE_DIRECTORY.exists()) {
      BASE_DIRECTORY.mkdirs();
    }
    File file = new File(BASE_DIRECTORY, name);
    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(outputStream);
      action.encode(encoder);
      encoder.flush();
    }
    return file;
  }

  public enum SampleEnum {
    ALPHA,
    BETA,
    GAMMA
  }

  public static final class ConstructorDecodedValue implements BinaryEncodableAndDecodable {
    private final String text;
    private final int number;

    public ConstructorDecodedValue(String text, int number) {
      this.text = text;
      this.number = number;
    }

    public ConstructorDecodedValue(BinaryDecoder decoder) {
      this(decoder.decodeString(), decoder.decodeInt());
    }

    public String getText() {
      return this.text;
    }

    public int getNumber() {
      return this.number;
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(this.text);
      binaryEncoder.encode(this.number);
    }
  }

  public static final class MethodDecodedValue implements BinaryEncodableAndDecodable {
    private String text;
    private int number;

    public MethodDecodedValue() {
    }

    public MethodDecodedValue(String text, int number) {
      this.text = text;
      this.number = number;
    }

    public void decode(BinaryDecoder decoder) {
      this.text = decoder.decodeString();
      this.number = decoder.decodeInt();
    }

    public String getText() {
      return this.text;
    }

    public int getNumber() {
      return this.number;
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(this.text);
      binaryEncoder.encode(this.number);
    }
  }

  public static final class ContextDecodedValue implements BinaryEncodableAndDecodable {
    private final Object context;
    private final String payload;

    public ContextDecodedValue(String payload) {
      this.context = null;
      this.payload = payload;
    }

    public ContextDecodedValue(BinaryDecoder decoder, Object context) {
      this.context = context;
      this.payload = decoder.decodeString();
    }

    public String getCombinedText() {
      return this.context + ":" + this.payload;
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(this.payload);
    }
  }

  private record SampleRecord(String text, int number) implements Serializable {
  }

  @Test
  public void constructorWithInputStreamDecodesPrimitiveSequence() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode(true);
      encoder.encode(42);
      encoder.encode("decoder");
    });

    assertTrue(decoder.decodeBoolean());
    assertEquals(42, decoder.decodeInt());
    assertEquals("decoder", decoder.decodeString());
  }

  @Test
  public void constructorWithFileDecodesString() throws Exception {
    File file = encodedFile("string.bin", encoder -> encoder.encode("from-file"));
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(file);
    assertEquals("from-file", decoder.decodeString());
  }

  @Test
  public void constructorWithStringPathDecodesUuid() throws Exception {
    UUID expected = UUID.randomUUID();
    File file = encodedFile("uuid.bin", encoder -> encoder.encode(expected));
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(file.getAbsolutePath());
    assertEquals(expected, decoder.decodeId());
  }

  @Test
  public void readFullyReadsWrittenBytes() throws Exception {
    byte[] expected = new byte[] {3, 1, 4, 1, 5, 9};
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.write(expected));
    assertArrayEquals(expected, decoder.readFully(new byte[expected.length]));
  }

  @Test
  public void decodeRecordReturnsSerializedRecord() throws Exception {
    SampleRecord expected = new SampleRecord("record", 77);
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encodeRecord(expected));

    assertEquals(SampleRecord.class.getName(), decoder.decodeString());
    assertEquals(expected, decoder.decodeRecord());
  }

  @Test
  public void decodeBinaryEncodableUsesBinaryDecoderConstructor() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encode(new ConstructorDecodedValue("alpha", 12)));
    ConstructorDecodedValue value = decoder.decodeBinaryEncodableAndDecodable();

    assertEquals("alpha", value.getText());
    assertEquals(12, value.getNumber());
  }

  @Test
  public void decodeBinaryEncodableUsesDecodeMethodFallback() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encode(new MethodDecodedValue("beta", 34)));
    MethodDecodedValue value = decoder.decodeBinaryEncodableAndDecodable();

    assertEquals("beta", value.getText());
    assertEquals(34, value.getNumber());
  }

  @Test
  public void decodeBinaryEncodableWithContextUsesContextConstructor() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encode(new ContextDecodedValue("payload")));
    ContextDecodedValue value = decoder.decodeBinaryEncodableAndDecodable("prefix");

    assertEquals("prefix:payload", value.getCombinedText());
  }

  @Test
  public void decodeEnumAndArraysRoundTrip() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode(SampleEnum.GAMMA);
      encoder.encode(new int[] {7, 8, 9});
      encoder.encode(new String[] {"a", "b"});
    });

    assertEquals(SampleEnum.GAMMA, decoder.<SampleEnum>decodeEnum());
    assertArrayEquals(new int[] {7, 8, 9}, decoder.decodeIntArray());
    assertArrayEquals(new String[] {"a", "b"}, decoder.decodeStringArray());
  }
}
