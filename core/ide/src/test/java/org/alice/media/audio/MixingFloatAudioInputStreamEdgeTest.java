package org.alice.media.audio;

import org.junit.Test;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MixingFloatAudioInputStreamEdgeTest {
  private static final AudioFormat FORMAT = new AudioFormat(10.0f, 16, 1, true, false);

  @Test
  public void readSkipsStreamsThatHaveNotStartedYet() throws IOException {
    TrackingInputStream input = new TrackingInputStream(new byte[] {1, 2, 3, 4}, true);
    TrackingScheduledAudioStream scheduled = new TrackingScheduledAudioStream(input, 1.0);
    MixingFloatAudioInputStream mixer = new MixingFloatAudioInputStream(FORMAT, List.of(scheduled), 0.2);
    byte[] buffer = new byte[4];

    int bytesRead = mixer.read(buffer, 0, buffer.length);

    assertEquals(4, bytesRead);
    assertArrayEquals(new byte[] {0, 0, 0, 0}, buffer);
    assertEquals(0, scheduled.readCalls);
    assertEquals(4, input.available());
  }

  @Test
  public void skipMarkAndResetPropagateToUnderlyingStreams() throws IOException {
    TrackingInputStream input = new TrackingInputStream(new byte[] {10, 20, 30, 40, 50, 60}, true);
    TrackingScheduledAudioStream scheduled = new TrackingScheduledAudioStream(input, 0.0);
    MixingFloatAudioInputStream mixer = new MixingFloatAudioInputStream(FORMAT, List.of(scheduled), 0.3);

    mixer.mark(128);
    long skipped = mixer.skip(2);
    byte[] buffer = new byte[2];
    mixer.read(buffer, 0, buffer.length);
    mixer.reset();

    assertEquals(2L, skipped);
    assertEquals(1, input.markCalls);
    assertEquals(1, input.resetCalls);
    assertEquals(2L, input.totalSkipped);
    assertEquals(0.0f, mixer.secondsProcessed(), 0.001f);
  }

  @Test
  public void markSupportedReturnsFalseWhenUnderlyingStreamDoesNotSupportMarks() {
    TrackingInputStream input = new TrackingInputStream(new byte[] {1, 2, 3, 4}, false);
    TrackingScheduledAudioStream scheduled = new TrackingScheduledAudioStream(input, 0.0);
    MixingFloatAudioInputStream mixer = new MixingFloatAudioInputStream(FORMAT, List.of(scheduled), 0.2);

    assertFalse(mixer.markSupported());
  }

  @Test
  public void markSupportedReturnsFalseWhenUnderlyingStreamIsMissing() {
    MixingFloatAudioInputStream mixer = new MixingFloatAudioInputStream(FORMAT, List.of(new NullScheduledAudioStream()), 0.1);

    assertFalse(mixer.markSupported());
  }

  private static final class TrackingScheduledAudioStream extends ScheduledAudioStream {
    private final AudioInputStream stream;
    private int readCalls;

    private TrackingScheduledAudioStream(InputStream input, double startTime) {
      super(null, startTime);
      this.stream = new AudioInputStream(input, FORMAT, AudioSystemSupport.frameLength(input));
    }

    @Override
    public AudioInputStream getAudioStream() {
      return stream;
    }

    @Override
    public int read(byte[] buffer, int offset, int toRead) throws IOException {
      readCalls += 1;
      return stream.read(buffer, offset, toRead);
    }
  }

  private static final class NullScheduledAudioStream extends ScheduledAudioStream {
    private NullScheduledAudioStream() {
      super(null, 0.0);
    }

    @Override
    public AudioInputStream getAudioStream() {
      return null;
    }
  }

  private static final class TrackingInputStream extends ByteArrayInputStream {
    private final boolean markSupported;
    private int markCalls;
    private int resetCalls;
    private long totalSkipped;

    private TrackingInputStream(byte[] data, boolean markSupported) {
      super(data);
      this.markSupported = markSupported;
    }

    @Override
    public synchronized void mark(int readAheadLimit) {
      markCalls += 1;
      super.mark(readAheadLimit);
    }

    @Override
    public synchronized void reset() {
      resetCalls += 1;
      super.reset();
    }

    @Override
    public long skip(long n) {
      long skipped = super.skip(n);
      totalSkipped += skipped;
      return skipped;
    }

    @Override
    public boolean markSupported() {
      return markSupported;
    }
  }

  private static final class AudioSystemSupport {
    private static long frameLength(InputStream input) {
      if (input instanceof ByteArrayInputStream stream) {
        return stream.available() / FORMAT.getFrameSize();
      }
      return 0;
    }
  }
}
