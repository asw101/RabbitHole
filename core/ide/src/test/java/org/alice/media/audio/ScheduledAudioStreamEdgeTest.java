package org.alice.media.audio;

import org.junit.Test;
import org.lgna.common.resources.AudioResource;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ScheduledAudioStreamEdgeTest {
  private static final AudioFormat FORMAT = new AudioFormat(10.0f, 16, 1, true, false);

  @Test
  public void getAudioStreamInitializesValidWaveResourceAndHonorsEntryPoint() throws Exception {
    ScheduledAudioStream stream = new ScheduledAudioStream(createWaveResource((short) 100, (short) 200, (short) 300), 0.0, 0.2);

    AudioInputStream audioStream = stream.getAudioStream();
    byte[] bytes = audioStream.readNBytes(2);

    assertNotNull(audioStream);
    assertEquals(2, bytes.length);
    assertEquals(0x2C, bytes[0] & 0xFF);
    assertEquals(0x01, bytes[1] & 0xFF);
  }

  @Test
  public void getAudioStreamReturnsNullWhenWaveDataIsInvalid() {
    AudioResource invalid = new AudioResource(UUID.randomUUID());
    invalid.setOriginalFileName("broken.wav");
    invalid.setName("broken.wav");
    invalid.setContent("audio.x_wav", new byte[] {1, 2, 3, 4});

    ScheduledAudioStream stream = new ScheduledAudioStream(invalid, 0.0);

    assertNull(stream.getAudioStream());
  }

  @Test
  public void readHonorsEndPointAndTracksSecondsRead() throws Exception {
    ScheduledAudioStream stream = new ScheduledAudioStream(createWaveResource((short) 1, (short) 2, (short) 3, (short) 4), 0.0, 0.1, 0.3, 0.5);
    byte[] buffer = new byte[16];

    assertNotNull(stream.getAudioStream());
    int firstRead = stream.read(buffer, 0, buffer.length);

    assertTrue(firstRead > 0);
    assertTrue(firstRead <= 4);
    assertEquals(firstRead / 20.0, stream.secondsRead(), 1.0e-9);
    assertEquals(0.5, stream.getVolume(), 0.0);
  }

  private static AudioResource createWaveResource(short... samples) throws Exception {
    byte[] pcm = new byte[samples.length * 2];
    for (int i = 0; i < samples.length; i++) {
      pcm[i * 2] = (byte) (samples[i] & 0xFF);
      pcm[i * 2 + 1] = (byte) ((samples[i] >>> 8) & 0xFF);
    }

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try (AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(pcm), FORMAT, samples.length)) {
      AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, output);
    }

    AudioResource resource = new AudioResource(UUID.randomUUID());
    resource.setOriginalFileName("clip.wav");
    resource.setName("clip.wav");
    resource.setContent("audio.x_wav", output.toByteArray());
    return resource;
  }
}
