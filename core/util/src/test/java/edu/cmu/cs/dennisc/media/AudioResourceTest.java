package edu.cmu.cs.dennisc.media;

import org.junit.Test;
import org.lgna.common.resources.AudioResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

public class AudioResourceTest {
  private static final File BASE_DIRECTORY = new File("target/test-artifacts/AudioResourceDeepTest");

  private File writeFile(String name, byte[] data) throws Exception {
    if (!BASE_DIRECTORY.exists()) {
      BASE_DIRECTORY.mkdirs();
    }
    File file = new File(BASE_DIRECTORY, name);
    File parent = file.getParentFile();
    if (parent != null && !parent.exists()) {
      parent.mkdirs();
    }
    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      outputStream.write(data);
    }
    return file;
  }

  @Test
  public void getContentTypeForFileUsesFileExtension() throws Exception {
    File file = writeFile("sound.WAV", new byte[] {1, 2, 3});
    assertEquals("audio.x_wav", AudioResource.getContentType(file));
  }

  @Test
  public void fileExtensionsSetIsUnmodifiable() {
    Set<String> extensions = AudioResource.getFileExtensions();
    try {
      extensions.add("ogg");
      fail();
    } catch (UnsupportedOperationException expected) {
      assertTrue(extensions.contains("wav"));
    }
  }

  @Test
  public void filenameFilterRejectsDirectoriesWhenDisabled() throws Exception {
    File directory = writeFile("nested/clip.wav", new byte[] {1}).getParentFile();
    FilenameFilter filter = AudioResource.createFilenameFilter(false);
    assertFalse(filter.accept(directory.getParentFile(), directory.getName()));
  }

  @Test
  public void filenameFilterAcceptsDirectoriesWhenEnabled() throws Exception {
    File directory = writeFile("dir-enabled/clip.wav", new byte[] {1}).getParentFile();
    FilenameFilter filter = AudioResource.createFilenameFilter(true);
    assertTrue(filter.accept(directory.getParentFile(), directory.getName()));
  }

  @Test
  public void filenameFilterAcceptsAudioFiles() throws Exception {
    File file = writeFile("filtered.mp3", new byte[] {7, 8, 9});
    FilenameFilter filter = AudioResource.createFilenameFilter(false);
    assertTrue(filter.accept(file.getParentFile(), file.getName()));
  }

  @Test
  public void fileConstructorLoadsDataAndMetadata() throws Exception {
    File file = writeFile("clip.wav", new byte[] {9, 8, 7});
    AudioResource resource = new AudioResource(file);

    assertEquals("clip.wav", resource.getName());
    assertEquals("clip.wav", resource.getOriginalFileName());
    assertEquals("audio.x_wav", resource.getContentType());
    assertArrayEquals(new byte[] {9, 8, 7}, resource.getData());
  }

  @Test
  public void valueOfCachesByUuid() {
    UUID uuid = UUID.randomUUID();
    assertSame(AudioResource.valueOf(uuid.toString()), AudioResource.valueOf(uuid.toString()));
  }

  @Test
  public void encodeAndDecodeAttributesRoundTripDurationAndMetadata() throws Exception {
    AudioResource source = new AudioResource(UUID.randomUUID());
    source.setName("song.mp3");
    source.setOriginalFileName("original-song.mp3");
    source.setContent("audio.mpeg", new byte[] {4, 5});
    source.setDuration(12.75);

    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element element = document.createElement("audio");
    source.encodeAttributes(element);

    AudioResource decoded = new AudioResource(UUID.randomUUID());
    decoded.decodeAttributes(element, new byte[] {4, 5});

    assertEquals("song.mp3", decoded.getName());
    assertEquals("original-song.mp3", decoded.getOriginalFileName());
    assertEquals("audio.mpeg", decoded.getContentType());
    assertEquals(12.75, decoded.getDuration(), 0.0);
  }

  @Test
  public void toStringContainsUsefulFields() {
    UUID uuid = UUID.randomUUID();
    AudioResource resource = new AudioResource(uuid);
    resource.setName("clip.wav");
    resource.setContent("audio.x_wav", new byte[] {1});

    String text = resource.toString();
    assertTrue(text.contains("clip.wav"));
    assertTrue(text.contains("audio.x_wav"));
    assertTrue(text.contains(uuid.toString()));
  }
}
