package edu.cmu.cs.dennisc.codec;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.UUID;

import static org.junit.Assert.*;

public class OutputStreamBinaryEncoderTest {
  private interface EncoderAction {
    void encode(OutputStreamBinaryEncoder encoder) throws Exception;
  }

  private InputStreamBinaryDecoder decoderFor(EncoderAction action) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    action.encode(encoder);
    encoder.flush();
    return new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
  }

  public static final class SampleValue implements BinaryEncodableAndDecodable {
    private final String text;

    public SampleValue(String text) {
      this.text = text;
    }

    public SampleValue(BinaryDecoder decoder) {
      this(decoder.decodeString());
    }

    public String getText() {
      return this.text;
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(this.text);
    }
  }

  private record SampleRecord(String text, int number) implements Serializable {
  }

  private enum SampleEnum {
    RED,
    GREEN
  }

  @Test
  public void writeBytesRoundTripViaReadFully() throws Exception {
    byte[] expected = new byte[] {10, 20, 30, 40};
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.write(expected));
    assertArrayEquals(expected, decoder.readFully(new byte[expected.length]));
  }

  @Test
  public void writeBytesWithOffsetAndLengthRoundTrip() throws Exception {
    byte[] source = new byte[] {4, 8, 15, 16, 23, 42};
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.write(source, 1, 3));
    assertArrayEquals(new byte[] {8, 15, 16}, decoder.readFully(new byte[3]));
  }

  @Test
  public void encodePrimitiveSequenceRoundTrip() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode(false);
      encoder.encode((byte) 7);
      encoder.encode('Q');
      encoder.encode(3.5d);
      encoder.encode(2.25f);
      encoder.encode(99);
      encoder.encode(123456789L);
      encoder.encode((short) 11);
    });

    assertFalse(decoder.decodeBoolean());
    assertEquals((byte) 7, decoder.decodeByte());
    assertEquals('Q', decoder.decodeChar());
    assertEquals(3.5d, decoder.decodeDouble(), 0.0d);
    assertEquals(2.25f, decoder.decodeFloat(), 0.0f);
    assertEquals(99, decoder.decodeInt());
    assertEquals(123456789L, decoder.decodeLong());
    assertEquals((short) 11, decoder.decodeShort());
  }

  @Test
  public void encodeStringNullAndUnicodeRoundTrip() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode((String) null);
      encoder.encode("café ☕");
    });

    assertNull(decoder.decodeString());
    assertEquals("café ☕", decoder.decodeString());
  }

  @Test
  public void encodeEnumAndUuidRoundTrip() throws Exception {
    UUID id = UUID.randomUUID();
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode(SampleEnum.GREEN);
      encoder.encode(id);
    });

    assertEquals(SampleEnum.GREEN, decoder.<SampleEnum>decodeEnum());
    assertEquals(id, decoder.decodeId());
  }

  @Test
  public void encodePrimitiveArraysRoundTrip() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> {
      encoder.encode(new boolean[] {true, false, true});
      encoder.encode(new byte[] {1, 2, 3});
      encoder.encode(new String[] {"x", "y"});
    });

    assertArrayEquals(new boolean[] {true, false, true}, decoder.decodeBooleanArray());
    assertArrayEquals(new byte[] {1, 2, 3}, decoder.decodeByteArray());
    assertArrayEquals(new String[] {"x", "y"}, decoder.decodeStringArray());
  }

  @Test
  public void encodeBinaryEncodableValueRoundTrip() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encode(new SampleValue("payload")));
    SampleValue decoded = decoder.decodeBinaryEncodableAndDecodable();

    assertEquals("payload", decoded.getText());
  }

  @Test
  public void encodeNullBinaryEncodableRoundTrip() throws Exception {
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encode((BinaryEncodableAndDecodable) null));
    assertNull(decoder.decodeBinaryEncodableAndDecodable());
  }

  @Test
  public void encodeRecordWritesClassNameThenRecord() throws Exception {
    SampleRecord expected = new SampleRecord("name", 21);
    InputStreamBinaryDecoder decoder = decoderFor(encoder -> encoder.encodeRecord(expected));

    assertEquals(SampleRecord.class.getName(), decoder.decodeString());
    assertEquals(expected, decoder.decodeRecord());
  }
}
