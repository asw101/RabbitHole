package org.alice.media.audio;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SampleFormatConverterEdgeTest {
  private SampleFormatConverter converter;

  @Before
  public void setUp() {
    converter = new SampleFormatConverter();
    converter.setDitherMode(FloatSampleBuffer.DITHER_MODE_OFF);
  }

  @Test
  public void convertByteToFloat24BitLittleEndianHonorsFrameStride() {
    byte[] input = {
        1, 0, 0, 99,
        2, 0, 0, 88
    };
    float[] output = new float[2];

    SampleFormatConverter.convertByteToFloat(input, 0, 2, output, 4, SampleFormatConverter.CT_24SL);

    assertEquals(1.0f / 8388608.0f, output[0], 1.0e-9f);
    assertEquals(2.0f / 8388608.0f, output[1], 1.0e-9f);
  }

  @Test
  public void convertFloatToByte24BitLittleEndianClipsNegative() {
    byte[] output = new byte[3];

    converter.convertFloatToByte(new float[] {-2.0f}, 1, output, 0, 3, SampleFormatConverter.CT_24SL);

    assertArrayEquals(new byte[] {0, 0, (byte) 0x80}, output);
  }

  @Test
  public void convertFloatToByte32BitBigEndianClipsPositive() {
    byte[] output = new byte[4];

    converter.convertFloatToByte(new float[] {2.0f}, 1, output, 0, 4, SampleFormatConverter.CT_32SB);

    assertArrayEquals(new byte[] {0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, output);
  }
}
