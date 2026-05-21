package org.alice.ide.frametitle;

import org.alice.ide.uricontent.UriProjectLoader;
import org.lgna.project.Project;

import java.io.File;
import java.net.URI;

final class AliceIdeFrameTitleGeneratorTestSupport {
  private AliceIdeFrameTitleGeneratorTestSupport() {
  }

  static UriProjectLoader loader(File mainFile, boolean backup) {
    return new UriProjectLoader(false) {
      @Override
      public boolean isNewProject() {
        return false;
      }

      @Override
      public URI getUri() {
        return mainFile.toURI();
      }

      @Override
      protected Project load() {
        return null;
      }

      @Override
      public boolean isBackup() {
        return backup;
      }

      @Override
      public File getMainProjectFile() {
        return mainFile;
      }
    };
  }
}
