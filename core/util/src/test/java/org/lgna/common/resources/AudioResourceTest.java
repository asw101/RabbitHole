package org.lgna.common.resources;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
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

  @Test
  public void constructor_fileNameContentTypeData() {
    AudioResource r = new AudioResource(UUID.randomUUID());
    r.setContent("audio.x_wav", new byte[]{1, 2, 3});
    r.setName("clip.wav");
    r.setOriginalFileName("clip.wav");
    assertEquals("audio.x_wav", r.getContentType());
    assertEquals("clip.wav", r.getName());
  }

  @Test
  public void encodeAttributes_includesDuration() throws Exception {
    AudioResource r = new AudioResource(UUID.randomUUID());
    r.setName("song.mp3");
    r.setOriginalFileName("song.mp3");
    r.setContent("audio.mpeg", new byte[]{10, 20});
    r.setDuration(120.5);

    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element elem = doc.createElement("audio");
    r.encodeAttributes(elem);

    assertEquals("120.5", elem.getAttribute("duration"));
    assertEquals("song.mp3", elem.getAttribute("name"));
    assertEquals("audio.mpeg", elem.getAttribute("contentType"));
  }

  @Test
  public void decodeAttributes_restoresDuration() throws Exception {
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element elem = doc.createElement("audio");
    elem.setAttribute("name", "clip.wav");
    elem.setAttribute("originalFileName", "clip.wav");
    elem.setAttribute("contentType", "audio.x_wav");
    elem.setAttribute("duration", "42.5");

    AudioResource r = new AudioResource(UUID.randomUUID());
    r.decodeAttributes(elem, new byte[]{1, 2});
    assertEquals("clip.wav", r.getName());
    assertEquals(42.5, r.getDuration(), 1e-10);
    assertEquals("audio.x_wav", r.getContentType());
  }

  @Test
  public void valueOf_sameUuid_returnsCached() {
    UUID id = UUID.randomUUID();
    AudioResource r1 = AudioResource.valueOf(id.toString());
    AudioResource r2 = AudioResource.valueOf(id.toString());
    assertSame(r1, r2);
  }

  @Test
  public void duration_defaultIsNaN() {
    AudioResource r = new AudioResource(UUID.randomUUID());
    assertTrue(Double.isNaN(r.getDuration()));
  }
}
