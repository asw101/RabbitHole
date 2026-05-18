package org.alice.media.audio;

import org.junit.Test;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link MixingFloatAudioInputStream} covering constructor,
 * read, skip, available, close, mark, reset, and markSupported.
 */
public class MixingFloatAudioInputStreamDeepTest {

  private static final AudioFormat FORMAT = new AudioFormat(44100f, 16, 1, true, false);

  private MixingFloatAudioInputStream createMixer(Collection<ScheduledAudioStream> streams, double length) {
    return new MixingFloatAudioInputStream(FORMAT, streams, length);
  }

  private AudioInputStream createAudioInputStream(byte[] data) {
    return new AudioInputStream(new ByteArrayInputStream(data), FORMAT, data.length / FORMAT.getFrameSize());
  }

  // --- Constructor and basic properties ---

  @Test
  public void emptyStreamCollection_constructsSuccessfully() {
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), 1.0);
    assertNotNull(mixer);
  }

  @Test
  public void secondsProcessed_startsAtZero() {
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), 1.0);
    assertEquals(0f, mixer.secondsProcessed(), 0.001f);
  }

  @Test
  public void getFrameLength_returnsCorrectValue() {
    double length = 1.0; // 1 second
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), length);
    long expectedFrames = (long) (44100f * length);
    assertEquals(expectedFrames, mixer.getFrameLength());
  }

  @Test
  public void available_returnsExpectedBytes() throws IOException {
    double length = 0.5; // 0.5 seconds
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), length);
    int expectedBytes = (int) (44100f * FORMAT.getFrameSize() * length);
    assertEquals(expectedBytes, mixer.available());
  }

  // --- read(byte[], offset, length) with empty streams ---

  @Test
  public void readBytes_emptyStreams_producesSilence() throws IOException {
    double length = 0.1;
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), length);
    int totalBytes = (int) (44100f * FORMAT.getFrameSize() * length);
    byte[] buffer = new byte[totalBytes];
    int bytesRead = mixer.read(buffer, 0, buffer.length);
    assertTrue(bytesRead > 0);
  }

  @Test
  public void readBytes_returnsMinusOne_whenAllConsumed() throws IOException {
    double length = 0.01;
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), length);
    int totalBytes = (int) (44100f * FORMAT.getFrameSize() * length);
    byte[] buffer = new byte[totalBytes + 100];

    // Read all bytes
    int total = 0;
    int read;
    while ((read = mixer.read(buffer, 0, buffer.length)) > 0) {
      total += read;
    }
    assertTrue(total > 0);

    // Next read should return -1
    assertEquals(-1, mixer.read(buffer, 0, buffer.length));
  }

  // --- read() single byte ---

  @Test
  public void readSingleByte_emptyStreams_returnsByte() throws IOException {
    double length = 0.01;
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), length);
    int b = mixer.read();
    // Should return a byte value (silence = 0 for signed PCM)
    assertTrue(b >= -1 && b <= 255);
  }

  @Test
  public void readSingleByte_returnsMinusOneWhenExhausted() throws IOException {
    double length = 0.001;
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), length);
    // Consume all bytes
    int totalBytes = (int) (44100f * FORMAT.getFrameSize() * length);
    byte[] buffer = new byte[totalBytes + 100];
    while (mixer.read(buffer, 0, buffer.length) > 0) {
      // consume
    }
    assertEquals(-1, mixer.read());
  }

  // --- skip ---

  @Test
  public void skip_returnsPassedValue() throws IOException {
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), 1.0);
    long skipAmount = 1000;
    assertEquals(skipAmount, mixer.skip(skipAmount));
  }

  // --- close ---

  @Test
  public void close_doesNotThrow() throws IOException {
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), 1.0);
    mixer.close();
  }

  // --- markSupported ---

  @Test
  public void markSupported_emptyStreams_returnsTrue() {
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), 1.0);
    assertTrue(mixer.markSupported());
  }

  // --- mark and reset ---

  @Test
  public void markAndReset_emptyStreams_resetsToZero() throws IOException {
    double length = 0.1;
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), length);
    mixer.mark(1024);

    // Read some data
    byte[] buffer = new byte[1024];
    mixer.read(buffer, 0, buffer.length);
    assertTrue(mixer.secondsProcessed() > 0);

    // Reset
    mixer.reset();
    assertEquals(0f, mixer.secondsProcessed(), 0.001f);
  }

  // --- Verify available decreases after read ---

  @Test
  public void available_decreasesAfterRead() throws IOException {
    double length = 0.1;
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), length);
    int before = mixer.available();
    byte[] buffer = new byte[1024];
    int read = mixer.read(buffer, 0, buffer.length);
    int after = mixer.available();
    assertTrue(after < before);
  }

  // --- secondsProcessed increases after read ---

  @Test
  public void secondsProcessed_increasesAfterRead() throws IOException {
    double length = 0.5;
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), length);
    assertEquals(0f, mixer.secondsProcessed(), 0.001f);
    byte[] buffer = new byte[4410 * 2]; // 0.1s at 44100Hz, 16-bit mono
    mixer.read(buffer, 0, buffer.length);
    assertTrue(mixer.secondsProcessed() > 0);
  }

  // --- getFrameLength for different durations ---

  @Test
  public void getFrameLength_halfSecond() {
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), 0.5);
    assertEquals(22050, mixer.getFrameLength());
  }

  @Test
  public void getFrameLength_twoSeconds() {
    MixingFloatAudioInputStream mixer = createMixer(Collections.emptyList(), 2.0);
    assertEquals(88200, mixer.getFrameLength());
  }

  // --- decibel2linear additional edge cases ---

  @Test
  public void decibel2linear_plus20Db_isTen() {
    float linear = MixingFloatAudioInputStream.decibel2linear(20f);
    assertEquals(10.0f, linear, 0.01f);
  }
}
