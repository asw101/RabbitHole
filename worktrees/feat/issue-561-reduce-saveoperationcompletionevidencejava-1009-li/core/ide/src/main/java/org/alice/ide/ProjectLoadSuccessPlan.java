package org.alice.ide;

final class ProjectLoadSuccessPlan {
  private final boolean checkForMoreRecentBackups;
  private final boolean createProjectFromBackup;

  private ProjectLoadSuccessPlan(boolean checkForMoreRecentBackups, boolean createProjectFromBackup) {
    this.checkForMoreRecentBackups = checkForMoreRecentBackups;
    this.createProjectFromBackup = createProjectFromBackup;
  }

  static ProjectLoadSuccessPlan choose(boolean isBackup, boolean isLoadingBackups, boolean isDefaultBackup,
                                       boolean isNewProject, boolean hasUnloadableFiles) {
    boolean checkForMoreRecentBackups = !(isBackup && !isLoadingBackups)
        && !hasUnloadableFiles
        && !isNewProject;
    boolean createProjectFromBackup = isLoadingBackups && !isDefaultBackup;

    return new ProjectLoadSuccessPlan(checkForMoreRecentBackups, createProjectFromBackup);
  }

  boolean shouldCheckForMoreRecentBackups() {
    return checkForMoreRecentBackups;
  }

  boolean shouldCreateProjectFromBackup() {
    return createProjectFromBackup;
  }
}
