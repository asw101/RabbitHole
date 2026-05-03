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
