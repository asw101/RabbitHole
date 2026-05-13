package org.alice.ide;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProjectLoadFailureDispatchPlanTest {
  @Test
  public void backupLoadErrorOnlyShowsErrorDialog() {
    ProjectLoadFailureDispatchPlan dispatch = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.SHOW_BACKUP_LOAD_ERROR, false);

    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.NONE, dispatch.getLoadTarget());
    assertFalse(dispatch.shouldShowNewProject());
  }

  @Test
  public void acceptedBackupPromptLoadsBackup() {
    ProjectLoadFailureDispatchPlan dispatch = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.PROMPT_LOAD_BACKUP, true);

    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.BACKUP, dispatch.getLoadTarget());
    assertFalse(dispatch.shouldShowNewProject());
  }

  @Test
  public void declinedBackupPromptShowsNewProject() {
    ProjectLoadFailureDispatchPlan dispatch = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.PROMPT_LOAD_BACKUP, false);

    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.NONE, dispatch.getLoadTarget());
    assertTrue(dispatch.shouldShowNewProject());
  }

  @Test
  public void unsavedBackupsFailureShowsNewProject() {
    ProjectLoadFailureDispatchPlan dispatch = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.SHOW_UNSAVED_BACKUPS_LOAD_ERROR, false);

    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.NONE, dispatch.getLoadTarget());
    assertTrue(dispatch.shouldShowNewProject());
  }

  @Test
  public void allBackupsFailureShowsNewProject() {
    ProjectLoadFailureDispatchPlan dispatch = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.SHOW_PROJECT_AND_ALL_BACKUPS_LOAD_ERROR, false);

    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.NONE, dispatch.getLoadTarget());
    assertTrue(dispatch.shouldShowNewProject());
  }

  @Test
  public void acceptedMainProjectPromptLoadsMainProject() {
    ProjectLoadFailureDispatchPlan dispatch = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.PROMPT_LOAD_MAIN_PROJECT, true);

    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.MAIN_PROJECT, dispatch.getLoadTarget());
    assertFalse(dispatch.shouldShowNewProject());
  }

  @Test
  public void declinedMainProjectPromptDoesNothing() {
    ProjectLoadFailureDispatchPlan dispatch = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.PROMPT_LOAD_MAIN_PROJECT, false);

    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.NONE, dispatch.getLoadTarget());
    assertFalse(dispatch.shouldShowNewProject());
  }
}
