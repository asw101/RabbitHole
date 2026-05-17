package edu.cmu.cs.dennisc.java.net;

import org.junit.Test;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.*;

public class UriUtilitiesTest {

  @Test
  public void getFile_fileUri() {
    URI uri = new File("/some/path/file.txt").toURI();
    File result = UriUtilities.getFile(uri);
    assertNotNull(result);
    assertTrue(result.getPath().endsWith("file.txt"));
  }

  @Test
  public void getFile_httpUri() {
    URI uri = URI.create("http://example.com/page");
    File result = UriUtilities.getFile(uri);
    assertNull(result);
  }

  @Test
  public void getFile_nullUri() {
    assertNull(UriUtilities.getFile(null));
  }

  @Test
  public void getFile_fileUri_directory() {
    File dir = new File("/some/directory/");
    URI uri = dir.toURI();
    File result = UriUtilities.getFile(uri);
    assertNotNull(result);
    assertEquals(dir.getAbsolutePath(), result.getAbsolutePath());
  }
}
