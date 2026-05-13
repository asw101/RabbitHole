package org.alice.ide;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProjectLoadSuccessPlanTest {
  @Test
  public void manuallyOpenedBackupDoesNotProbeForNewerBackupsOrPromptToReplaceProject() {
    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(true, false, false, false, false);

    assertFalse(plan.shouldCheckForMoreRecentBackups());
    assertFalse(plan.shouldCreateProjectFromBackup());
  }

  @Test
  public void normalProjectLoadChecksForMoreRecentBackups() {
    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(false, false, false, false, false);

    assertTrue(plan.shouldCheckForMoreRecentBackups());
    assertFalse(plan.shouldCreateProjectFromBackup());
  }

  @Test
  public void newProjectLoadDoesNotCheckForMoreRecentBackups() {
    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(false, false, false, true, false);

    assertFalse(plan.shouldCheckForMoreRecentBackups());
    assertFalse(plan.shouldCreateProjectFromBackup());
  }

  @Test
  public void recoveryBackupWithPriorUnloadableFilesSkipsNewerBackupProbeButPromptsToCreateProject() {
    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(true, true, false, false, true);

    assertFalse(plan.shouldCheckForMoreRecentBackups());
    assertTrue(plan.shouldCreateProjectFromBackup());
  }

  @Test
  public void recoveryBackupWithoutPriorUnloadableFilesChecksForNewerBackupsThenPromptsToCreateProject() {
    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(true, true, false, false, false);

    assertTrue(plan.shouldCheckForMoreRecentBackups());
    assertTrue(plan.shouldCreateProjectFromBackup());
  }

  @Test
  public void defaultBackupRecoveryDoesNotPromptToReplaceProjectFile() {
    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(true, true, true, false, false);

    assertTrue(plan.shouldCheckForMoreRecentBackups());
    assertFalse(plan.shouldCreateProjectFromBackup());
  }
}
