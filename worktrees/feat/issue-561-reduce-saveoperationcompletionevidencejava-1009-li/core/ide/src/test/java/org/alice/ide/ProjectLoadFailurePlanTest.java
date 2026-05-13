package org.alice.ide;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ProjectLoadFailurePlanTest {
  private final File failedProject = new File("world.a3p");
  private final File failedBackup = new File("auto20240102_120000.a3p");
  private final File nextBackup = new File("auto20240102_130000.a3p");

  @Test
  public void manuallyLoadedCorruptBackupShowsBackupLoadError() {
    ProjectLoadFailurePlan plan = ProjectLoadFailurePlan.choose(true, false, true, false, nextBackup, failedBackup);

    assertEquals(ProjectLoadFailurePlan.Action.SHOW_BACKUP_LOAD_ERROR, plan.getAction());
    assertNull(plan.getBackupToLoad());
    assertNull(plan.getFailedBackupName());
  }

  @Test
  public void corruptMainProjectWithBackupPromptsToLoadBackup() {
    ProjectLoadFailurePlan plan = ProjectLoadFailurePlan.choose(false, false, true, false, nextBackup, failedProject);

    assertEquals(ProjectLoadFailurePlan.Action.PROMPT_LOAD_BACKUP, plan.getAction());
    assertEquals(nextBackup, plan.getBackupToLoad());
    assertNull(plan.getFailedBackupName());
  }

  @Test
  public void corruptBackupDuringRecoveryPromptsToLoadNextBackupWithFailedBackupName() {
    ProjectLoadFailurePlan plan = ProjectLoadFailurePlan.choose(true, true, true, false, nextBackup, failedBackup);

    assertEquals(ProjectLoadFailurePlan.Action.PROMPT_LOAD_BACKUP, plan.getAction());
    assertEquals(nextBackup, plan.getBackupToLoad());
    assertEquals(failedBackup.getName(), plan.getFailedBackupName());
  }

  @Test
  public void corruptDefaultBackupWithoutMoreBackupsShowsUnsavedBackupsError() {
    ProjectLoadFailurePlan plan = ProjectLoadFailurePlan.choose(false, false, true, true, null, failedProject);

    assertEquals(ProjectLoadFailurePlan.Action.SHOW_UNSAVED_BACKUPS_LOAD_ERROR, plan.getAction());
    assertNull(plan.getBackupToLoad());
  }

  @Test
  public void corruptSavedProjectWithoutMoreBackupsShowsAllBackupsError() {
    ProjectLoadFailurePlan plan = ProjectLoadFailurePlan.choose(false, false, true, false, null, failedProject);

    assertEquals(ProjectLoadFailurePlan.Action.SHOW_PROJECT_AND_ALL_BACKUPS_LOAD_ERROR, plan.getAction());
    assertNull(plan.getBackupToLoad());
  }

  @Test
  public void failedRecentBackupProbePromptsToLoadMainProject() {
    ProjectLoadFailurePlan plan = ProjectLoadFailurePlan.choose(true, true, false, false, null, failedBackup);

    assertEquals(ProjectLoadFailurePlan.Action.PROMPT_LOAD_MAIN_PROJECT, plan.getAction());
    assertNull(plan.getBackupToLoad());
  }
}
