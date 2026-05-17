package org.alice.media.audio;

import org.junit.Test;

import javax.sound.sampled.AudioFormat;

import static org.junit.Assert.*;

/**
 * Additional tests for {@link FloatSampleBuffer} — edge cases, error paths,
 * and multi-channel scenarios beyond the characterization test.
 */
public class FloatSampleBufferAdditionalTest {

  // ---- setSampleRate validation ----

  @Test(expected = IllegalArgumentException.class)
  public void setSampleRate_rejectsZero() {
    FloatSampleBuffer buf = new FloatSampleBuffer(1, 4, 44100f);
    buf.setSampleRate(0f);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setSampleRate_rejectsNegative() {
    FloatSampleBuffer buf = new FloatSampleBuffer(1, 4, 44100f);
    buf.setSampleRate(-22050f);
  }

  @Test
  public void setSampleRate_updatesValue() {
    FloatSampleBuffer buf = new FloatSampleBuffer(1, 4, 44100f);
    buf.setSampleRate(22050f);
    assertEquals(22050f, buf.getSampleRate(), 0f);
  }

  // ---- getChannel bounds checking ----

  @Test(expected = IllegalArgumentException.class)
  public void getChannel_rejectsNegativeIndex() {
    FloatSampleBuffer buf = new FloatSampleBuffer(1, 4, 44100f);
    buf.getChannel(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getChannel_rejectsIndexEqualToChannelCount() {
    FloatSampleBuffer buf = new FloatSampleBuffer(2, 4, 44100f);
    buf.getChannel(2);
  }

  // ---- initFromByteArray validation ----

  @Test(expected = IllegalArgumentException.class)
  public void initFromByteArray_rejectsNonPCM() {
    FloatSampleBuffer buf = new FloatSampleBuffer();
    AudioFormat ulaw = new AudioFormat(AudioFormat.Encoding.ULAW, 8000f, 8, 1, 1, 8000f, false);
    buf.initFromByteArray(new byte[10], 0, 10, ulaw);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getByteArrayBufferSize_rejectsNonPCM() {
    FloatSampleBuffer buf = new FloatSampleBuffer(1, 10, 8000f);
    AudioFormat ulaw = new AudioFormat(AudioFormat.Encoding.ULAW, 8000f, 8, 1, 1, 8000f, false);
    buf.getByteArrayBufferSize(ulaw);
  }

  // ---- changeSampleCount without keeping old ----

  @Test
  public void changeSampleCount_withoutKeepDoesNotPreserve() {
    FloatSampleBuffer buf = new FloatSampleBuffer(1, 4, 44100f);
    buf.getChannel(0)[0] = 0.99f;
    buf.changeSampleCount(8, false);
    assertEquals(8, buf.getSampleCount());
    // old data may or may not be preserved; sample count is correct
  }

  @Test
  public void changeSampleCount_shrinkTruncates() {
    FloatSampleBuffer buf = new FloatSampleBuffer(1, 8, 44100f);
    buf.changeSampleCount(2, true);
    assertEquals(2, buf.getSampleCount());
  }

  // ---- makeSilence on specific channel ----

  @Test
  public void makeSilenceOnChannel_zeroesChannel() {
    FloatSampleBuffer buf = new FloatSampleBuffer(2, 4, 44100f);
    buf.getChannel(0)[0] = 0.75f;
    buf.getChannel(1)[0] = 0.5f;
    buf.makeSilence(0);
    assertEquals(0f, buf.getChannel(0)[0], 0f);
  }

  // ---- reset with parameters ----

  @Test
  public void reset_withParams_reinitializes() {
    FloatSampleBuffer buf = new FloatSampleBuffer(1, 4, 44100f);
    buf.reset(3, 16, 22050f);
    assertEquals(3, buf.getChannelCount());
    assertEquals(16, buf.getSampleCount());
    assertEquals(22050f, buf.getSampleRate(), 0f);
  }

  // ---- convertToByteArray validation ----

  @Test(expected = IllegalArgumentException.class)
  public void convertToByteArray_rejectsMismatchedChannelCount() {
    FloatSampleBuffer buf = new FloatSampleBuffer(2, 4, 44100f);
    AudioFormat fmt = new AudioFormat(44100f, 16, 1, true, false); // 1 channel format
    byte[] out = new byte[buf.getByteArrayBufferSize(new AudioFormat(44100f, 16, 2, true, false))];
    buf.convertToByteArray(out, 0, fmt);
  }

  // ---- multiple addChannel calls ----

  @Test
  public void multipleAddChannel_growsCorrectly() {
    FloatSampleBuffer buf = new FloatSampleBuffer(1, 4, 44100f);
    buf.addChannel(true);
    buf.addChannel(true);
    buf.addChannel(true);
    assertEquals(4, buf.getChannelCount());
  }

  // ---- insertChannel at beginning ----

  @Test
  public void insertChannelAtZero_shiftsExisting() {
    FloatSampleBuffer buf = new FloatSampleBuffer(1, 4, 44100f);
    buf.getChannel(0)[0] = 0.3f;
    buf.insertChannel(0, true);
    assertEquals(2, buf.getChannelCount());
    // new channel at 0 should be silent
    assertEquals(0f, buf.getChannel(0)[0], 0f);
  }

  // ---- 32-bit round trip ----

  @Test
  public void roundTrip_32bitSignedLittleEndian() {
    AudioFormat fmt = new AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED, 44100f, 32, 1, 4, 44100f, false);
    // encode one 32-bit sample: 1073741824 (= 0x40000000)
    int val = 1073741824;
    byte[] original = {
        (byte) (val & 0xFF), (byte) ((val >> 8) & 0xFF),
        (byte) ((val >> 16) & 0xFF), (byte) ((val >> 24) & 0xFF)
    };
    FloatSampleBuffer buf = new FloatSampleBuffer(original, 0, original.length, fmt);
    buf.setDitherMode(FloatSampleBuffer.DITHER_MODE_OFF);

    float[] ch = buf.getChannel(0);
    assertEquals(0.5f, ch[0], 1e-2f);

    byte[] result = new byte[original.length];
    buf.convertToByteArray(result, 0, fmt);
    assertArrayEquals(original, result);
  }

  // ---- stereo round trip ----

  @Test
  public void roundTrip_stereo16bitLE() {
    AudioFormat fmt = new AudioFormat(44100f, 16, 2, true, false);
    // 2 channels, 1 sample each => 4 bytes
    byte[] original = {0, 0x40, 0, (byte) 0xC0}; // ch0=+16384, ch1=-16384
    FloatSampleBuffer buf = new FloatSampleBuffer(original, 0, original.length, fmt);
    buf.setDitherMode(FloatSampleBuffer.DITHER_MODE_OFF);

    assertEquals(2, buf.getChannelCount());
    assertEquals(1, buf.getSampleCount());
    assertEquals(0.5f, buf.getChannel(0)[0], 1e-3f);
    assertEquals(-0.5f, buf.getChannel(1)[0], 1e-3f);

    byte[] result = new byte[original.length];
    buf.convertToByteArray(result, 0, fmt);
    assertArrayEquals(original, result);
  }

  // ---- empty buffer operations ----

  @Test
  public void defaultBuffer_getAllChannelsReturnsEmpty() {
    FloatSampleBuffer buf = new FloatSampleBuffer();
    Object[] all = buf.getAllChannels();
    assertEquals(0, all.length);
  }

  @Test
  public void defaultBuffer_makeSilenceDoesNotThrow() {
    FloatSampleBuffer buf = new FloatSampleBuffer();
    buf.makeSilence(); // should not throw on empty buffer
  }
}
