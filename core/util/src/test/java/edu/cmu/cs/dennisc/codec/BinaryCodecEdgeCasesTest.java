package edu.cmu.cs.dennisc.codec;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static org.junit.Assert.*;

public class BinaryCodecEdgeCasesTest {
  private interface EncoderAction {
    void encode(OutputStreamBinaryEncoder encoder) throws Exception;
  }

  private interface DecoderAction<T> {
    T decode(InputStreamBinaryDecoder decoder);
  }

  private <T> T roundTrip(EncoderAction encoderAction, DecoderAction<T> decoderAction) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    encoderAction.encode(encoder);
    encoder.flush();
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
    return decoderAction.decode(decoder);
  }

  private static final class SampleValue implements BinaryEncodableAndDecodable {
    private final int value;

    private SampleValue(int value) {
      this.value = value;
    }

    public SampleValue(BinaryDecoder decoder) {
      this(decoder.decodeInt());
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(this.value);
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof SampleValue other && this.value == other.value;
    }

    @Override
    public int hashCode() {
      return Integer.hashCode(this.value);
    }
  }

  @Test
  public void emptyByteArrayRoundTrip() throws Exception {
    byte[] decoded = roundTrip(encoder -> encoder.encode(new byte[0]), InputStreamBinaryDecoder::decodeByteArray);
    assertArrayEquals(new byte[0], decoded);
  }

  @Test
  public void largeByteArrayRoundTrip() throws Exception {
    byte[] source = new byte[8192];
    for (int i = 0; i < source.length; i++) {
      source[i] = (byte) (i * 31);
    }

    byte[] decoded = roundTrip(encoder -> encoder.encode(source), InputStreamBinaryDecoder::decodeByteArray);
    assertArrayEquals(source, decoded);
  }

  @Test
  public void emptyIntArrayRoundTrip() throws Exception {
    int[] decoded = roundTrip(encoder -> encoder.encode(new int[0]), InputStreamBinaryDecoder::decodeIntArray);
    assertArrayEquals(new int[0], decoded);
  }

  @Test
  public void largeIntArrayRoundTrip() throws Exception {
    int[] source = new int[2048];
    for (int i = 0; i < source.length; i++) {
      source[i] = (i * i) - 17;
    }

    int[] decoded = roundTrip(encoder -> encoder.encode(source), InputStreamBinaryDecoder::decodeIntArray);
    assertArrayEquals(source, decoded);
  }

  @Test
  public void emptyStringArrayRoundTrip() throws Exception {
    String[] decoded = roundTrip(encoder -> encoder.encode(new String[0]), InputStreamBinaryDecoder::decodeStringArray);
    assertArrayEquals(new String[0], decoded);
  }

  @Test
  public void emptyBinaryEncodableArrayRoundTrip() throws Exception {
    SampleValue[] decoded = roundTrip(
        encoder -> encoder.encode(new SampleValue[0]),
        decoder -> decoder.decodeBinaryEncodableAndDecodableArray(SampleValue.class));
    assertArrayEquals(new SampleValue[0], decoded);
  }

  @Test
  public void nullStringRoundTrip() throws Exception {
    String decoded = roundTrip(encoder -> encoder.encode((String) null), InputStreamBinaryDecoder::decodeString);
    assertNull(decoded);
  }

  @Test
  public void nullUuidRoundTrip() throws Exception {
    UUID decoded = roundTrip(encoder -> encoder.encode((UUID) null), InputStreamBinaryDecoder::decodeId);
    assertNull(decoded);
  }

  @Test
  public void alternatingBooleanArrayRoundTrip() throws Exception {
    boolean[] source = new boolean[] {true, false, true, false, false, true, true, false};
    boolean[] decoded = roundTrip(encoder -> encoder.encode(source), InputStreamBinaryDecoder::decodeBooleanArray);
    assertArrayEquals(source, decoded);
  }
}
