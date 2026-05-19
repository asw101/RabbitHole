package org.lgna.common.resources;

import org.junit.Test;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

public class AudioResourceDeepTest {

  @Test
  public void getContentType_wav() {
    assertEquals("audio.x_wav", AudioResource.getContentType("file.wav"));
  }

  @Test
  public void getContentType_mp3() {
    assertEquals("audio.mpeg", AudioResource.getContentType("file.mp3"));
  }

  @Test
  public void getContentType_au() {
    assertEquals("audio.basic", AudioResource.getContentType("file.au"));
  }

  @Test
  public void getContentType_unknownExtension() {
    assertNull(AudioResource.getContentType("file.txt"));
  }

  @Test
  public void getContentType_noExtension() {
    assertNull(AudioResource.getContentType("noextension"));
  }

  @Test
  public void getContentType_mixedCase() {
    assertEquals("audio.x_wav", AudioResource.getContentType("file.WAV"));
  }

  @Test
  public void getContentType_uppercaseMp3() {
    assertEquals("audio.mpeg", AudioResource.getContentType("file.MP3"));
  }

  @Test
  public void getContentType_fromFile() {
    assertEquals("audio.mpeg", AudioResource.getContentType(new File("test.mp3")));
  }

  @Test
  public void getFileExtensions_containsExpected() {
    Set<String> extensions = AudioResource.getFileExtensions();

    assertTrue(extensions.contains("au"));
    assertTrue(extensions.contains("wav"));
    assertTrue(extensions.contains("mp3"));
    assertEquals(3, extensions.size());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void getFileExtensions_isUnmodifiable() {
    AudioResource.getFileExtensions().add("ogg");
  }

  @Test
  public void uuidConstructor_storesUuid() {
    UUID uuid = UUID.randomUUID();

    assertEquals(uuid, new AudioResource(uuid).getId());
  }

  @Test
  public void duration_defaultIsNaN() {
    assertTrue(Double.isNaN(new AudioResource(UUID.randomUUID()).getDuration()));
  }

  @Test
  public void setDuration_getDuration_roundTrip() {
    AudioResource resource = new AudioResource(UUID.randomUUID());

    resource.setDuration(5.5);

    assertEquals(5.5, resource.getDuration(), 1e-10);
  }

  @Test
  public void setDuration_zero() {
    AudioResource resource = new AudioResource(UUID.randomUUID());

    resource.setDuration(0.0);

    assertEquals(0.0, resource.getDuration(), 1e-10);
  }

  @Test
  public void setDuration_multipleAssignments_lastWins() {
    AudioResource resource = new AudioResource(UUID.randomUUID());

    resource.setDuration(1.0);
    resource.setDuration(2.5);

    assertEquals(2.5, resource.getDuration(), 1e-10);
  }

  @Test
  public void valueOf_returnsSameInstanceForSameUuid() {
    UUID uuid = UUID.randomUUID();

    assertSame(AudioResource.valueOf(uuid.toString()), AudioResource.valueOf(uuid.toString()));
  }

  @Test
  public void valueOf_createsResourceWithMatchingUuid() {
    UUID uuid = UUID.randomUUID();

    assertEquals(uuid, AudioResource.valueOf(uuid.toString()).getId());
  }

  @Test
  public void valueOf_differentUuidsReturnDifferentInstances() {
    UUID first = UUID.randomUUID();
    UUID second = UUID.randomUUID();

    assertNotSame(AudioResource.valueOf(first.toString()), AudioResource.valueOf(second.toString()));
  }

  @Test
  public void createFilenameFilter_notNull() {
    assertNotNull(AudioResource.createFilenameFilter(false));
  }

  @Test
  public void setName_getName_roundTrip() {
    AudioResource resource = new AudioResource(UUID.randomUUID());

    resource.setName("myaudio.mp3");

    assertEquals("myaudio.mp3", resource.getName());
  }

  @Test
  public void setOriginalFileName_getOriginalFileName() {
    AudioResource resource = new AudioResource(UUID.randomUUID());

    resource.setOriginalFileName("original.wav");

    assertEquals("original.wav", resource.getOriginalFileName());
  }

  @Test
  public void setContent_setsContentType() {
    AudioResource resource = new AudioResource(UUID.randomUUID());

    resource.setContent("audio.mpeg", new byte[] { 1, 2, 3 });

    assertEquals("audio.mpeg", resource.getContentType());
  }

  @Test
  public void setContent_setsData() {
    AudioResource resource = new AudioResource(UUID.randomUUID());
    byte[] data = { 1, 2, 3, 4 };

    resource.setContent("audio.basic", data);

    assertArrayEquals(data, resource.getData());
  }

  @Test
  public void toString_containsClassName() {
    AudioResource resource = new AudioResource(UUID.randomUUID());

    resource.setName("test.mp3");

    assertTrue(resource.toString().contains("Resource"));
  }

  @Test
  public void getContentType_pathWithDirectories() {
    assertEquals("audio.mpeg", AudioResource.getContentType("some/path/file.mp3"));
  }
}
