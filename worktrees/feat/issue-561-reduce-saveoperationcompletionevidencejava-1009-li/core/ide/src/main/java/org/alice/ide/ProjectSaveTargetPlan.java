package org.alice.ide;

import org.alice.ide.uricontent.FileProjectLoader;
import org.alice.ide.uricontent.UriProjectLoader;

import java.io.File;

final class ProjectSaveTargetPlan {
  private final FileProjectLoader nextLoader;
  private final boolean copyDefaultBackupDirectory;

  private ProjectSaveTargetPlan(FileProjectLoader nextLoader, boolean copyDefaultBackupDirectory) {
    this.nextLoader = nextLoader;
    this.copyDefaultBackupDirectory = copyDefaultBackupDirectory;
  }

  static ProjectSaveTargetPlan choose(UriProjectLoader currentLoader, File file) {
    FileProjectLoader nextLoader = new FileProjectLoader(file);
    boolean copyDefaultBackupDirectory = currentLoader.isNewProject()
        || (currentLoader.isDefaultBackup() && !nextLoader.isDefaultBackup());
    return new ProjectSaveTargetPlan(nextLoader, copyDefaultBackupDirectory);
  }

  FileProjectLoader getNextLoader() {
    return nextLoader;
  }

  boolean shouldCopyDefaultBackupDirectory() {
    return copyDefaultBackupDirectory;
  }

  boolean isBackupSave() {
    return nextLoader.isBackup();
  }
}
