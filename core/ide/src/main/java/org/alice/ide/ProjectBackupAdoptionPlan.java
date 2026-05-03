package org.alice.ide;

import edu.cmu.cs.dennisc.javax.swing.option.YesNoCancelResult;

final class ProjectBackupAdoptionPlan {
  private final Action action;

  private ProjectBackupAdoptionPlan(Action action) {
    this.action = action;
  }

  static ProjectBackupAdoptionPlan afterUserChoice(YesNoCancelResult result) {
    return switch (result) {
      case YES -> new ProjectBackupAdoptionPlan(Action.SAVE_BACKUP_TO_ORIGINAL_PROJECT);
      case NO -> new ProjectBackupAdoptionPlan(Action.KEEP_BACKUP_AS_CURRENT_PROJECT);
      case CANCEL -> new ProjectBackupAdoptionPlan(Action.RELOAD_ORIGINAL_PROJECT);
    };
  }

  Action getAction() {
    return action;
  }

  enum Action {
    SAVE_BACKUP_TO_ORIGINAL_PROJECT,
    KEEP_BACKUP_AS_CURRENT_PROJECT,
    RELOAD_ORIGINAL_PROJECT
  }
}
