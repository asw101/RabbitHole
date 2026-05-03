package org.alice.ide.uricontent;

import org.lgna.project.Project;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.*;

public class FileProjectLoaderTest {

  @Test
  public void loadDelegatesIoFailureToHookAndReturnsNull() throws IOException {
    File corruptProject = File.createTempFile("corrupt-project", ".a3p");
    corruptProject.deleteOnExit();
    CapturingFileProjectLoader loader = new CapturingFileProjectLoader(corruptProject);

    Project project = loader.load();

    assertNull(project);
    assertEquals(corruptProject, loader.file);
    assertTrue(loader.exception instanceof IOException);
  }

  @Test
  public void nonVrLoaderUsesOriginalProjectUriAndDoesNotRequireSaveWhenFileExists() throws IOException {
    File project = File.createTempFile("saved-project", ".a3p");
    project.deleteOnExit();
    FileProjectLoader loader = new FileProjectLoader(project);

    assertEquals(project.toURI(), loader.getUri());
    assertFalse(loader.shouldBeSaved());
  }

  @Test
  public void vrReadyLoaderUsesRenamedProjectUriAndRequiresSaveWhenVrCopyDoesNotExist() throws IOException {
    File project = File.createTempFile("saved-project", ".a3p");
    project.deleteOnExit();
    File vrProject = vrProjectFor(project);
    vrProject.delete();
    vrProject.deleteOnExit();
    FileProjectLoader loader = new FileProjectLoader(project, true);

    assertEquals(vrProject.toURI(), loader.getUri());
    assertTrue(loader.shouldBeSaved());
  }

  @Test
  public void vrReadyLoaderDoesNotRequireSaveWhenVrCopyAlreadyExists() throws IOException {
    File project = File.createTempFile("saved-project", ".a3p");
    project.deleteOnExit();
    File vrProject = vrProjectFor(project);
    assertTrue(vrProject.createNewFile());
    vrProject.deleteOnExit();
    FileProjectLoader loader = new FileProjectLoader(project, true);

    assertEquals(vrProject.toURI(), loader.getUri());
    assertFalse(loader.shouldBeSaved());
  }

  private static File vrProjectFor(File project) {
    String source = project.getAbsolutePath();
    return new File(source.substring(0, source.length() - 4) + " VR" + source.substring(source.length() - 4));
  }

  private static class CapturingFileProjectLoader extends AbstractFileProjectLoader {
    private File file;
    private Exception exception;

    CapturingFileProjectLoader(File file) {
      super(file, false);
    }

    @Override
    public URI getUri() {
      return getFile().toURI();
    }

    @Override
    protected void handleLoadException(File file, Exception e) {
      this.file = file;
      this.exception = e;
    }

    @Override
    public boolean isNewProject() {
      return false;
    }
  }
}
