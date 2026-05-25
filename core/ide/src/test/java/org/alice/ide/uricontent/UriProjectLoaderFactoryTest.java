package org.alice.ide.uricontent;

import org.alice.ide.projecturi.ProjectSnapshot;
import org.alice.stageide.openprojectpane.models.TemplateUriState;
import org.junit.Test;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.*;

public class UriProjectLoaderFactoryTest {
  @Test
  public void createInstanceReturnsFileLoaderForFileSnapshots() {
    File file = new File("virtual/project.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(file.toURI());

    UriProjectLoader loader = UriProjectLoader.createInstance(snapshot, false);

    assertTrue(loader instanceof FileProjectLoader);
    assertEquals(file.toURI(), loader.getUri());
    assertFalse(loader.isNewProject());
  }

  @Test
  public void createInstanceReturnsStarterLoaderForStarterSnapshots() {
    File file = new File("starter/project.a3p");
    URI starterUri = StarterProjectUtilities.toUri(file);
    ProjectSnapshot snapshot = new ProjectSnapshot(starterUri);

    UriProjectLoader loader = UriProjectLoader.createInstance(snapshot, true);

    assertTrue(loader instanceof StarterProjectFileLoader);
    assertEquals(starterUri, loader.getUri());
    assertTrue(loader.isNewProject());
    assertTrue(loader.shouldMakeVrReady());
  }

  @Test
  public void createInstanceReturnsBlankSlateLoaderForTemplateSnapshots() {
    ProjectSnapshot snapshot = TemplateUriState.Template.GRASS.getProjectSnapshot();

    UriProjectLoader loader = UriProjectLoader.createInstance(snapshot, true);

    assertTrue(loader instanceof BlankSlateProjectLoader);
    assertEquals(snapshot.getUri(), loader.getUri());
    assertTrue(loader.isNewProject());
  }

  @Test
  public void createInstanceReturnsNullForNullOrUnsupportedSnapshots() {
    assertNull(UriProjectLoader.createInstance(null, false));

    ProjectSnapshot unsupported = new ProjectSnapshot(URI.create("mailto:test@example.com"), "mail", null);
    assertNull(UriProjectLoader.createInstance(unsupported, false));
  }
}
