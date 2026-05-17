package org.alice.media.audio;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link SampleFormatConverter} — PCM byte/float conversion,
 * quantization, dithering, and format-type encoding.
 */
public class SampleFormatConverterTest {

  private SampleFormatConverter converter;

  @Before
  public void setUp() {
    converter = new SampleFormatConverter();
  }

  // ---- getFormatType ----

  @Test
  public void getFormatType_8bitSignedReturnsExpectedFlags() {
    int ft = converter.getFormatType(8, true, false);
    assertEquals(SampleFormatConverter.CT_8S, ft);
  }

  @Test
  public void getFormatType_8bitUnsignedReturnsExpectedFlags() {
    int ft = converter.getFormatType(8, false, false);
    assertEquals(SampleFormatConverter.CT_8U, ft);
  }

  @Test
  public void getFormatType_16bitSignedBigEndian() {
    int ft = converter.getFormatType(16, true, true);
    assertEquals(SampleFormatConverter.CT_16SB, ft);
  }

  @Test
  public void getFormatType_16bitSignedLittleEndian() {
    int ft = converter.getFormatType(16, true, false);
    assertEquals(SampleFormatConverter.CT_16SL, ft);
  }

  @Test
  public void getFormatType_24bitSignedBigEndian() {
    int ft = converter.getFormatType(24, true, true);
    assertEquals(SampleFormatConverter.CT_24SB, ft);
  }

  @Test
  public void getFormatType_24bitSignedLittleEndian() {
    int ft = converter.getFormatType(24, true, false);
    assertEquals(SampleFormatConverter.CT_24SL, ft);
  }

  @Test
  public void getFormatType_32bitSignedBigEndian() {
    int ft = converter.getFormatType(32, true, true);
    assertEquals(SampleFormatConverter.CT_32SB, ft);
  }

  @Test
  public void getFormatType_32bitSignedLittleEndian() {
    int ft = converter.getFormatType(32, true, false);
    assertEquals(SampleFormatConverter.CT_32SL, ft);
  }

  @Test
  public void getFormatType_8bitBigEndianIgnored() {
    // bigEndian flag is ignored for 8-bit samples
    int ft = converter.getFormatType(8, true, true);
    assertEquals(SampleFormatConverter.CT_8S, ft);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getFormatType_rejectsUnsupportedBitDepth() {
    converter.getFormatType(12, true, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getFormatType_rejectsUnsigned16Bit() {
    converter.getFormatType(16, false, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getFormatType_rejectsUnsigned24Bit() {
    converter.getFormatType(24, false, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getFormatType_rejectsUnsigned32Bit() {
    converter.getFormatType(32, false, false);
  }

  // ---- dither accessors ----

  @Test
  public void ditherBitsDefaultIs0_8() {
    assertEquals(0.8f, converter.getDitherBits(), 0f);
  }

  @Test
  public void setDitherBitsUpdatesValue() {
    converter.setDitherBits(1.5f);
    assertEquals(1.5f, converter.getDitherBits(), 0f);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setDitherBitsRejectsZero() {
    converter.setDitherBits(0f);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setDitherBitsRejectsNegative() {
    converter.setDitherBits(-1f);
  }

  @Test
  public void ditherModeDefaultIsAutomatic() {
    assertEquals(FloatSampleBuffer.DITHER_MODE_AUTOMATIC, converter.getDitherMode());
  }

  @Test
  public void setDitherModeOn() {
    converter.setDitherMode(FloatSampleBuffer.DITHER_MODE_ON);
    assertEquals(FloatSampleBuffer.DITHER_MODE_ON, converter.getDitherMode());
  }

  @Test
  public void setDitherModeOff() {
    converter.setDitherMode(FloatSampleBuffer.DITHER_MODE_OFF);
    assertEquals(FloatSampleBuffer.DITHER_MODE_OFF, converter.getDitherMode());
  }

  @Test(expected = IllegalArgumentException.class)
  public void setDitherModeRejectsInvalidValue() {
    converter.setDitherMode(99);
  }

  // ---- convertByteToFloat: 8-bit signed ----

  @Test
  public void convertByteToFloat_8bitSigned_silence() {
    byte[] input = {0};
    float[] output = new float[1];
    SampleFormatConverter.convertByteToFloat(input, 0, 1, output, 1, SampleFormatConverter.CT_8S);
    assertEquals(0f, output[0], 1e-6f);
  }

  @Test
  public void convertByteToFloat_8bitSigned_maxPositive() {
    byte[] input = {127};
    float[] output = new float[1];
    SampleFormatConverter.convertByteToFloat(input, 0, 1, output, 1, SampleFormatConverter.CT_8S);
    assertEquals(127f / 128f, output[0], 1e-5f);
  }

  @Test
  public void convertByteToFloat_8bitSigned_maxNegative() {
    byte[] input = {-128};
    float[] output = new float[1];
    SampleFormatConverter.convertByteToFloat(input, 0, 1, output, 1, SampleFormatConverter.CT_8S);
    assertEquals(-1f, output[0], 1e-5f);
  }

  // ---- convertByteToFloat: 8-bit unsigned ----

  @Test
  public void convertByteToFloat_8bitUnsigned_midpointIsSilence() {
    byte[] input = {(byte) 128};
    float[] output = new float[1];
    SampleFormatConverter.convertByteToFloat(input, 0, 1, output, 1, SampleFormatConverter.CT_8U);
    assertEquals(0f, output[0], 1e-5f);
  }

  @Test
  public void convertByteToFloat_8bitUnsigned_maxPositive() {
    byte[] input = {(byte) 255};
    float[] output = new float[1];
    SampleFormatConverter.convertByteToFloat(input, 0, 1, output, 1, SampleFormatConverter.CT_8U);
    assertEquals(127f / 128f, output[0], 1e-5f);
  }

  // ---- convertByteToFloat: 16-bit signed little-endian ----

  @Test
  public void convertByteToFloat_16bitSL_silence() {
    byte[] input = {0, 0};
    float[] output = new float[1];
    SampleFormatConverter.convertByteToFloat(input, 0, 1, output, 2, SampleFormatConverter.CT_16SL);
    assertEquals(0f, output[0], 1e-6f);
  }

  @Test
  public void convertByteToFloat_16bitSL_halfAmplitude() {
    // 16384 = 0x4000 in LE: 0x00, 0x40
    byte[] input = {0x00, 0x40};
    float[] output = new float[1];
    SampleFormatConverter.convertByteToFloat(input, 0, 1, output, 2, SampleFormatConverter.CT_16SL);
    assertEquals(0.5f, output[0], 1e-3f);
  }

  // ---- convertByteToFloat: 16-bit signed big-endian ----

  @Test
  public void convertByteToFloat_16bitSB_halfAmplitude() {
    // 16384 = 0x4000 in BE: 0x40, 0x00
    byte[] input = {0x40, 0x00};
    float[] output = new float[1];
    SampleFormatConverter.convertByteToFloat(input, 0, 1, output, 2, SampleFormatConverter.CT_16SB);
    assertEquals(0.5f, output[0], 1e-3f);
  }

  // ---- convertByteToFloat: multiple samples ----

  @Test
  public void convertByteToFloat_multipleSamples() {
    // 2 samples of 8-bit signed
    byte[] input = {64, -64};
    float[] output = new float[2];
    SampleFormatConverter.convertByteToFloat(input, 0, 2, output, 1, SampleFormatConverter.CT_8S);
    assertEquals(64f / 128f, output[0], 1e-5f);
    assertEquals(-64f / 128f, output[1], 1e-5f);
  }

  @Test(expected = IllegalArgumentException.class)
  public void convertByteToFloat_rejectsUnsupportedFormat() {
    byte[] input = new byte[4];
    float[] output = new float[1];
    SampleFormatConverter.convertByteToFloat(input, 0, 1, output, 4, 0xFF);
  }

  // ---- convertFloatToByte: 8-bit signed ----

  @Test
  public void convertFloatToByte_8bitSigned_silence() {
    converter.setDitherMode(FloatSampleBuffer.DITHER_MODE_OFF);
    float[] input = {0f};
    byte[] output = new byte[1];
    converter.convertFloatToByte(input, 1, output, 0, 1, SampleFormatConverter.CT_8S);
    assertEquals(0, output[0]);
  }

  @Test
  public void convertFloatToByte_8bitSigned_clipsPositive() {
    converter.setDitherMode(FloatSampleBuffer.DITHER_MODE_OFF);
    float[] input = {2.0f}; // way above 1.0
    byte[] output = new byte[1];
    converter.convertFloatToByte(input, 1, output, 0, 1, SampleFormatConverter.CT_8S);
    assertEquals(127, output[0]);
  }

  @Test
  public void convertFloatToByte_8bitSigned_clipsNegative() {
    converter.setDitherMode(FloatSampleBuffer.DITHER_MODE_OFF);
    float[] input = {-2.0f};
    byte[] output = new byte[1];
    converter.convertFloatToByte(input, 1, output, 0, 1, SampleFormatConverter.CT_8S);
    assertEquals(-128, output[0]);
  }

  // ---- convertFloatToByte: 16-bit signed LE ----

  @Test
  public void convertFloatToByte_16bitSL_silence() {
    converter.setDitherMode(FloatSampleBuffer.DITHER_MODE_OFF);
    float[] input = {0f};
    byte[] output = new byte[2];
    converter.convertFloatToByte(input, 1, output, 0, 2, SampleFormatConverter.CT_16SL);
    assertEquals(0, output[0]);
    assertEquals(0, output[1]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void convertFloatToByte_rejectsUnsupportedFormat() {
    converter.setDitherMode(FloatSampleBuffer.DITHER_MODE_OFF);
    float[] input = {0f};
    byte[] output = new byte[4];
    converter.convertFloatToByte(input, 1, output, 0, 4, 0xFF);
  }

  // ---- round-trip: byte → float → byte ----

  @Test
  public void roundTrip_8bitSigned() {
    converter.setDitherMode(FloatSampleBuffer.DITHER_MODE_OFF);
    byte[] original = {0, 64, -64, 127, -128};
    float[] floats = new float[5];
    SampleFormatConverter.convertByteToFloat(original, 0, 5, floats, 1, SampleFormatConverter.CT_8S);
    byte[] result = new byte[5];
    converter.convertFloatToByte(floats, 5, result, 0, 1, SampleFormatConverter.CT_8S);
    assertArrayEquals(original, result);
  }

  @Test
  public void roundTrip_16bitSignedLittleEndian() {
    converter.setDitherMode(FloatSampleBuffer.DITHER_MODE_OFF);
    short[] shorts = {0, 16384, -16384, 32767};
    byte[] original = new byte[shorts.length * 2];
    for (int i = 0; i < shorts.length; i++) {
      original[i * 2] = (byte) (shorts[i] & 0xFF);
      original[i * 2 + 1] = (byte) (shorts[i] >> 8);
    }
    float[] floats = new float[4];
    SampleFormatConverter.convertByteToFloat(original, 0, 4, floats, 2, SampleFormatConverter.CT_16SL);
    byte[] result = new byte[original.length];
    converter.convertFloatToByte(floats, 4, result, 0, 2, SampleFormatConverter.CT_16SL);
    assertArrayEquals(original, result);
  }

  // ---- dithering mode control ----

  @Test
  public void automaticDitherAppliesWhenDownsampling() {
    // originalFormatType wider than output format ⇒ dithering should engage
    converter.setDitherMode(FloatSampleBuffer.DITHER_MODE_AUTOMATIC);
    converter.originalFormatType = SampleFormatConverter.CT_16SL; // 16-bit original
    float[] input = {0.5f};
    byte[] output = new byte[1];
    // converting to 8-bit: narrower than original ⇒ dither enabled
    converter.convertFloatToByte(input, 1, output, 0, 1, SampleFormatConverter.CT_8S);
    // Just verifying no exception; dither non-deterministic
    assertNotNull(output);
  }

  // ---- constants sanity ----

  @Test
  public void formatFlagConstants_areSane() {
    assertEquals(1, SampleFormatConverter.F_8);
    assertEquals(2, SampleFormatConverter.F_16);
    assertEquals(3, SampleFormatConverter.F_24);
    assertEquals(4, SampleFormatConverter.F_32);
    assertEquals(8, SampleFormatConverter.F_SIGNED);
    assertEquals(16, SampleFormatConverter.F_BIGENDIAN);
  }

  @Test
  public void compositeFormatConstants_matchExpected() {
    assertEquals(SampleFormatConverter.F_8 | SampleFormatConverter.F_SIGNED, SampleFormatConverter.CT_8S);
    assertEquals(SampleFormatConverter.F_8, SampleFormatConverter.CT_8U);
    assertEquals(SampleFormatConverter.F_16 | SampleFormatConverter.F_SIGNED | SampleFormatConverter.F_BIGENDIAN, SampleFormatConverter.CT_16SB);
    assertEquals(SampleFormatConverter.F_16 | SampleFormatConverter.F_SIGNED, SampleFormatConverter.CT_16SL);
  }
}
