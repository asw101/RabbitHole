package org.alice.ide;

final class ProjectLoadFailureDispatchPlan {
  private final LoadTarget loadTarget;
  private final boolean showNewProject;

  private ProjectLoadFailureDispatchPlan(LoadTarget loadTarget, boolean showNewProject) {
    this.loadTarget = loadTarget;
    this.showNewProject = showNewProject;
  }

  static ProjectLoadFailureDispatchPlan afterUserChoice(ProjectLoadFailurePlan.Action action, boolean accepted) {
    return switch (action) {
      case SHOW_BACKUP_LOAD_ERROR ->
          new ProjectLoadFailureDispatchPlan(LoadTarget.NONE, false);
      case PROMPT_LOAD_BACKUP -> accepted
          ? new ProjectLoadFailureDispatchPlan(LoadTarget.BACKUP, false)
          : new ProjectLoadFailureDispatchPlan(LoadTarget.NONE, true);
      case SHOW_UNSAVED_BACKUPS_LOAD_ERROR, SHOW_PROJECT_AND_ALL_BACKUPS_LOAD_ERROR ->
          new ProjectLoadFailureDispatchPlan(LoadTarget.NONE, true);
      case PROMPT_LOAD_MAIN_PROJECT -> accepted
          ? new ProjectLoadFailureDispatchPlan(LoadTarget.MAIN_PROJECT, false)
          : new ProjectLoadFailureDispatchPlan(LoadTarget.NONE, false);
    };
  }

  LoadTarget getLoadTarget() {
    return loadTarget;
  }

  boolean shouldShowNewProject() {
    return showNewProject;
  }

  enum LoadTarget {
    NONE,
    BACKUP,
    MAIN_PROJECT
  }
}
