package org.lgna.common.resources;

import org.junit.Test;

import java.io.FilenameFilter;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

public class AudioResourceTest {

  @Test
  public void getContentType_wav() {
    assertEquals("audio.x_wav", AudioResource.getContentType("test.wav"));
  }

  @Test
  public void getContentType_mp3() {
    assertEquals("audio.mpeg", AudioResource.getContentType("test.mp3"));
  }

  @Test
  public void getContentType_au() {
    assertEquals("audio.basic", AudioResource.getContentType("test.au"));
  }

  @Test
  public void getContentType_unknown() {
    assertNull(AudioResource.getContentType("test.txt"));
  }

  @Test
  public void getContentType_caseInsensitive() {
    assertEquals("audio.x_wav", AudioResource.getContentType("TEST.WAV"));
  }

  @Test
  public void getContentType_noExtension() {
    assertNull(AudioResource.getContentType("noextension"));
  }

  @Test
  public void getFileExtensions_containsExpected() {
    Set<String> exts = AudioResource.getFileExtensions();
    assertTrue(exts.contains("wav"));
    assertTrue(exts.contains("mp3"));
    assertTrue(exts.contains("au"));
    assertEquals(3, exts.size());
  }

  @Test
  public void createFilenameFilter_acceptsAudioFile() {
    FilenameFilter filter = AudioResource.createFilenameFilter(false);
    assertNotNull(filter);
  }

  @Test
  public void valueOf_createsResource() {
    UUID id = UUID.randomUUID();
    AudioResource r = AudioResource.valueOf(id.toString());
    assertNotNull(r);
    assertEquals(id, r.getId());
  }

  @Test
  public void constructor_uuid() {
    UUID id = UUID.randomUUID();
    AudioResource r = new AudioResource(id);
    assertEquals(id, r.getId());
    assertTrue(Double.isNaN(r.getDuration()));
  }

  @Test
  public void duration_setAndGet() {
    AudioResource r = new AudioResource(UUID.randomUUID());
    r.setDuration(5.5);
    assertEquals(5.5, r.getDuration(), 1e-10);
  }
}
