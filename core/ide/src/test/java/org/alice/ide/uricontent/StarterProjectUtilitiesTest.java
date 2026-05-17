package org.alice.ide.uricontent;

import org.junit.Test;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.*;

/**
 * Tests for {@link StarterProjectUtilities} — URI prefix encoding/decoding.
 */
public class StarterProjectUtilitiesTest {

  @Test
  public void toUri_prependsStarterPrefix() {
    File file = new File("/tmp/testProject.a3p");
    URI uri = StarterProjectUtilities.toUri(file);
    assertTrue(uri.toString().startsWith("starter"));
    assertTrue(uri.toString().contains("testProject.a3p"));
  }

  @Test
  public void toFileUriFromStarterUri_stripsPrefix() {
    File file = new File("/tmp/testProject.a3p");
    URI starterUri = StarterProjectUtilities.toUri(file);
    URI fileUri = StarterProjectUtilities.toFileUriFromStarterUri(starterUri);
    assertFalse(fileUri.toString().startsWith("starter"));
    assertEquals(file.toURI().toString(), fileUri.toString());
  }

  @Test
  public void toFile_roundTrips() {
    File original = new File("/tmp/testProject.a3p");
    URI starterUri = StarterProjectUtilities.toUri(original);
    File result = StarterProjectUtilities.toFile(starterUri);
    assertEquals(original.getAbsolutePath(), result.getAbsolutePath());
  }

  @Test
  public void toUri_differentFiles_produceDifferentUris() {
    URI uri1 = StarterProjectUtilities.toUri(new File("/tmp/a.a3p"));
    URI uri2 = StarterProjectUtilities.toUri(new File("/tmp/b.a3p"));
    assertNotEquals(uri1, uri2);
  }

  @Test
  public void toFile_fromConstructedStarterUri_worksCorrectly() {
    File file = new File("/home/user/projects/hello.a3p");
    URI starterUri = StarterProjectUtilities.toUri(file);
    File recovered = StarterProjectUtilities.toFile(starterUri);
    assertEquals(file.getAbsoluteFile(), recovered.getAbsoluteFile());
  }
}
