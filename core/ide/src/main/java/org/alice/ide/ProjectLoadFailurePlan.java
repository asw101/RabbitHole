package org.alice.ide;

import java.io.File;

final class ProjectLoadFailurePlan {
  private final Action action;
  private final File backupToLoad;
  private final String failedBackupName;

  private ProjectLoadFailurePlan(Action action, File backupToLoad, String failedBackupName) {
    this.action = action;
    this.backupToLoad = backupToLoad;
    this.failedBackupName = failedBackupName;
  }

  static ProjectLoadFailurePlan choose(boolean isBackup, boolean isLoadingBackups,
                                       boolean isMainProjectCorrupted, boolean isDefaultBackup,
                                       File backup, File failedProjectFile) {
    if (isBackup && !isLoadingBackups) {
      return new ProjectLoadFailurePlan(Action.SHOW_BACKUP_LOAD_ERROR, null, null);
    }

    if (backup != null) {
      String failedBackupName = isBackup
          ? failedProjectFile.getName()
          : null;
      return new ProjectLoadFailurePlan(Action.PROMPT_LOAD_BACKUP, backup, failedBackupName);
    }

    if (isMainProjectCorrupted) {
      Action action = isDefaultBackup
          ? Action.SHOW_UNSAVED_BACKUPS_LOAD_ERROR
          : Action.SHOW_PROJECT_AND_ALL_BACKUPS_LOAD_ERROR;
      return new ProjectLoadFailurePlan(
          action,
          null,
          null);
    }

    return new ProjectLoadFailurePlan(Action.PROMPT_LOAD_MAIN_PROJECT, null, null);
  }

  Action getAction() {
    return action;
  }

  File getBackupToLoad() {
    return backupToLoad;
  }

  String getFailedBackupName() {
    return failedBackupName;
  }

  enum Action {
    SHOW_BACKUP_LOAD_ERROR,
    PROMPT_LOAD_BACKUP,
    SHOW_UNSAVED_BACKUPS_LOAD_ERROR,
    SHOW_PROJECT_AND_ALL_BACKUPS_LOAD_ERROR,
    PROMPT_LOAD_MAIN_PROJECT
  }
}
