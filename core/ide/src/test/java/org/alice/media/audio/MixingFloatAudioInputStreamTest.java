package org.alice.media.audio;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link MixingFloatAudioInputStream} — static utility methods
 * and constructor-adjacent logic that can be tested without audio resources.
 */
public class MixingFloatAudioInputStreamTest {

  // ---- decibel2linear conversion ----

  @Test
  public void decibel2linear_zeroDbIsUnity() {
    float linear = MixingFloatAudioInputStream.decibel2linear(0f);
    assertEquals(1.0f, linear, 1e-5f);
  }

  @Test
  public void decibel2linear_minus6DbIsAboutHalf() {
    float linear = MixingFloatAudioInputStream.decibel2linear(-6.0206f);
    assertEquals(0.5f, linear, 1e-3f);
  }

  @Test
  public void decibel2linear_minus20DbIsOneTenth() {
    float linear = MixingFloatAudioInputStream.decibel2linear(-20f);
    assertEquals(0.1f, linear, 1e-3f);
  }

  @Test
  public void decibel2linear_positive6DbIsAboutTwo() {
    float linear = MixingFloatAudioInputStream.decibel2linear(6.0206f);
    assertEquals(2.0f, linear, 1e-2f);
  }

  @Test
  public void decibel2linear_largeNegativeApproachesZero() {
    float linear = MixingFloatAudioInputStream.decibel2linear(-100f);
    assertTrue(linear > 0f);
    assertTrue(linear < 0.001f);
  }

  @Test
  public void decibel2linear_symmetricProperty() {
    // +X dB and -X dB should multiply to 1.0
    float pos = MixingFloatAudioInputStream.decibel2linear(10f);
    float neg = MixingFloatAudioInputStream.decibel2linear(-10f);
    assertEquals(1.0f, pos * neg, 1e-4f);
  }
}
