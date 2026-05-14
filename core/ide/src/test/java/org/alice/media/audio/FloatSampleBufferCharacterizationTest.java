/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.alice.media.audio;

import org.junit.Test;

import javax.sound.sampled.AudioFormat;

import static org.junit.Assert.*;

/**
 * Characterization tests capturing baseline behaviour of
 * {@link FloatSampleBuffer} before and after the extraction of
 * {@link SampleFormatConverter}.
 */
public class FloatSampleBufferCharacterizationTest {

  // ---- construction / init ----

  @Test
  public void defaultConstructorYieldsEmptyBuffer() {
    FloatSampleBuffer buf = new FloatSampleBuffer();
    assertEquals(0, buf.getChannelCount());
    assertEquals(0, buf.getSampleCount());
  }

  @Test
  public void sizedConstructorCreatesSilentChannels() {
    FloatSampleBuffer buf = new FloatSampleBuffer(2, 128, 44100f);
    assertEquals(2, buf.getChannelCount());
    assertEquals(128, buf.getSampleCount());
    assertEquals(44100f, buf.getSampleRate(), 0f);
    float[] ch0 = buf.getChannel(0);
    assertTrue(ch0.length >= 128);
  }

  // ---- byte → float → byte round-trip (16-bit signed LE) ----

  @Test
  public void roundTrip16BitSignedLittleEndian() {
    AudioFormat fmt = new AudioFormat(44100f, 16, 1, true, false);
    int sampleCount = 4;
    byte[] original = new byte[sampleCount * 2];
    // encode a few known 16-bit LE signed samples
    short[] shorts = {0, 16384, -16384, 32767};
    for (int i = 0; i < shorts.length; i++) {
      original[i * 2] = (byte) (shorts[i] & 0xFF);
      original[i * 2 + 1] = (byte) (shorts[i] >> 8);
    }

    FloatSampleBuffer buf = new FloatSampleBuffer(original, 0, original.length, fmt);
    buf.setDitherMode(FloatSampleBuffer.DITHER_MODE_OFF);

    assertEquals(1, buf.getChannelCount());
    assertEquals(sampleCount, buf.getSampleCount());

    // float values should be in [-1, 1]
    float[] ch = buf.getChannel(0);
    assertEquals(0f, ch[0], 1e-5f);
    assertEquals(0.5f, ch[1], 1e-3f);
    assertEquals(-0.5f, ch[2], 1e-3f);

    // convert back
    byte[] result = new byte[original.length];
    buf.convertToByteArray(result, 0, fmt);
    assertArrayEquals(original, result);
  }

  // ---- byte → float → byte round-trip (8-bit unsigned) ----

  @Test
  public void roundTrip8BitUnsigned() {
    AudioFormat fmt = new AudioFormat(22050f, 8, 1, false, false);
    byte[] original = {(byte) 128, (byte) 255, 0, (byte) 192};

    FloatSampleBuffer buf = new FloatSampleBuffer(original, 0, original.length, fmt);
    buf.setDitherMode(FloatSampleBuffer.DITHER_MODE_OFF);

    float[] ch = buf.getChannel(0);
    assertEquals(0f, ch[0], 1e-3f);     // 128 → 0.0
    assertTrue(ch[1] > 0.9f);           // 255 → ~1.0

    byte[] result = new byte[original.length];
    buf.convertToByteArray(result, 0, fmt);
    assertArrayEquals(original, result);
  }

  // ---- channel management ----

  @Test
  public void addAndRemoveChannel() {
    FloatSampleBuffer buf = new FloatSampleBuffer(1, 64, 44100f);
    assertEquals(1, buf.getChannelCount());

    buf.addChannel(true);
    assertEquals(2, buf.getChannelCount());

    buf.removeChannel(0);
    assertEquals(1, buf.getChannelCount());
  }

  @Test
  public void insertChannelAtIndex() {
    FloatSampleBuffer buf = new FloatSampleBuffer(2, 32, 44100f);
    buf.insertChannel(1, true);
    assertEquals(3, buf.getChannelCount());
  }

  // ---- sample resize ----

  @Test
  public void changeSampleCountPreservesExisting() {
    FloatSampleBuffer buf = new FloatSampleBuffer(1, 4, 44100f);
    float[] ch = buf.getChannel(0);
    ch[0] = 0.25f;
    ch[1] = 0.5f;

    buf.changeSampleCount(8, true);
    float[] ch2 = buf.getChannel(0);
    assertEquals(0.25f, ch2[0], 0f);
    assertEquals(0.5f, ch2[1], 0f);
    // new samples should be silent
    assertEquals(0f, ch2[4], 0f);
    assertEquals(8, buf.getSampleCount());
  }

  // ---- silence ----

  @Test
  public void makeSilenceZeroesAllChannels() {
    FloatSampleBuffer buf = new FloatSampleBuffer(2, 4, 44100f);
    buf.getChannel(0)[0] = 0.9f;
    buf.getChannel(1)[0] = 0.8f;

    buf.makeSilence();
    assertEquals(0f, buf.getChannel(0)[0], 0f);
    assertEquals(0f, buf.getChannel(1)[0], 0f);
  }

  // ---- copy channel ----

  @Test
  public void copyChannelDuplicatesData() {
    FloatSampleBuffer buf = new FloatSampleBuffer(2, 4, 44100f);
    buf.getChannel(0)[0] = 0.7f;
    buf.copyChannel(0, 1);
    assertEquals(0.7f, buf.getChannel(1)[0], 0f);
  }

  // ---- dither accessors ----

  @Test
  public void ditherBitsAccessor() {
    FloatSampleBuffer buf = new FloatSampleBuffer();
    buf.setDitherBits(0.5f);
    assertEquals(0.5f, buf.getDitherBits(), 0f);
  }

  @Test
  public void ditherModeAccessor() {
    FloatSampleBuffer buf = new FloatSampleBuffer();
    buf.setDitherMode(FloatSampleBuffer.DITHER_MODE_ON);
    assertEquals(FloatSampleBuffer.DITHER_MODE_ON, buf.getDitherMode());
  }

  @Test(expected = IllegalArgumentException.class)
  public void setDitherBitsRejectsZero() {
    new FloatSampleBuffer().setDitherBits(0f);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setDitherModeRejectsInvalid() {
    new FloatSampleBuffer().setDitherMode(99);
  }

  // ---- getFormatType ----

  @Test
  public void getFormatType16BitSigned() {
    FloatSampleBuffer buf = new FloatSampleBuffer();
    int ft = buf.getFormatType(16, true, false);
    assertTrue(ft != 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getFormatTypeRejectsUnsupported() {
    new FloatSampleBuffer().getFormatType(12, true, false);
  }

  // ---- reset ----

  @Test
  public void resetClearsBuffer() {
    FloatSampleBuffer buf = new FloatSampleBuffer(2, 128, 44100f);
    buf.reset();
    assertEquals(0, buf.getChannelCount());
    assertEquals(0, buf.getSampleCount());
  }

  // ---- initFromFloatSampleBuffer ----

  @Test
  public void initFromFloatSampleBufferCopiesData() {
    FloatSampleBuffer src = new FloatSampleBuffer(1, 4, 44100f);
    src.getChannel(0)[0] = 0.42f;

    FloatSampleBuffer dst = new FloatSampleBuffer();
    dst.initFromFloatSampleBuffer(src);

    assertEquals(1, dst.getChannelCount());
    assertEquals(4, dst.getSampleCount());
    assertEquals(0.42f, dst.getChannel(0)[0], 0f);
  }

  // ---- getAllChannels ----

  @Test
  public void getAllChannelsReturnsCorrectCount() {
    FloatSampleBuffer buf = new FloatSampleBuffer(3, 16, 44100f);
    Object[] all = buf.getAllChannels();
    assertEquals(3, all.length);
  }

  // ---- getByteArrayBufferSize ----

  @Test
  public void getByteArrayBufferSizeMatchesFormat() {
    FloatSampleBuffer buf = new FloatSampleBuffer(2, 100, 44100f);
    AudioFormat fmt = new AudioFormat(44100f, 16, 2, true, false);
    assertEquals(400, buf.getByteArrayBufferSize(fmt));
  }

  // ---- 24-bit round-trip ----

  @Test
  public void roundTrip24BitSignedBigEndian() {
    AudioFormat fmt = new AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED, 44100f, 24, 1, 3, 44100f, true);
    // encode one sample: 0x3F_FF_FF = 4194303 → 4194303 / 8388608 ≈ 0.5
    byte[] original = {0x3F, (byte) 0xFF, (byte) 0xFF};

    FloatSampleBuffer buf = new FloatSampleBuffer(original, 0, original.length, fmt);
    buf.setDitherMode(FloatSampleBuffer.DITHER_MODE_OFF);

    float[] ch = buf.getChannel(0);
    assertEquals(0.5f, ch[0], 1e-2f);

    byte[] result = new byte[original.length];
    buf.convertToByteArray(result, 0, fmt);
    assertArrayEquals(original, result);
  }
}
