package org.alice.ide;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Tests for {@link ProjectLoadFailurePlan}, {@link ProjectLoadSuccessPlan},
 * and {@link ProjectLoadFailureDispatchPlan} — the pure-logic plan objects
 * extracted from ProjectLoader. These collectively cover the branching
 * logic that ProjectLoader delegates to when loading projects.
 */
public class ProjectLoaderTest {

  private final File mainProject = new File("myWorld.a3p");
  private final File backup1 = new File("backup_2024_01.a3p");
  private final File backup2 = new File("backup_2024_02.a3p");

  // ---- ProjectLoadSuccessPlan coverage ----

  @Test
  public void successPlan_normalLoad_checksForBackups() {
    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(
        false, false, false, false, false);
    assertTrue(plan.shouldCheckForMoreRecentBackups());
    assertFalse(plan.shouldCreateProjectFromBackup());
  }

  @Test
  public void successPlan_newProject_skipsBackupCheck() {
    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(
        false, false, false, true, false);
    assertFalse(plan.shouldCheckForMoreRecentBackups());
    assertFalse(plan.shouldCreateProjectFromBackup());
  }

  @Test
  public void successPlan_loadingBackupsNotDefault_createsFromBackup() {
    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(
        true, true, false, false, false);
    assertTrue(plan.shouldCreateProjectFromBackup());
  }

  @Test
  public void successPlan_loadingBackupsDefaultBackup_noCreateFromBackup() {
    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(
        true, true, true, false, false);
    assertFalse(plan.shouldCreateProjectFromBackup());
  }

  @Test
  public void successPlan_manualBackupNotLoadingBackups_skipsBackupCheck() {
    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(
        true, false, false, false, false);
    assertFalse(plan.shouldCheckForMoreRecentBackups());
  }

  @Test
  public void successPlan_hasUnloadableFiles_skipsBackupCheck() {
    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(
        false, false, false, false, true);
    assertFalse(plan.shouldCheckForMoreRecentBackups());
  }

  @Test
  public void successPlan_backupLoadNewProject_neitherCheckNorCreate() {
    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(
        true, false, true, true, false);
    assertFalse(plan.shouldCheckForMoreRecentBackups());
    assertFalse(plan.shouldCreateProjectFromBackup());
  }

  // ---- ProjectLoadFailurePlan additional coverage ----

  @Test
  public void failurePlan_noBackupAndMainCorruptedAndDefaultBackup_showsUnsavedError() {
    ProjectLoadFailurePlan plan = ProjectLoadFailurePlan.choose(
        false, true, true, true, null, mainProject);
    assertEquals(ProjectLoadFailurePlan.Action.SHOW_UNSAVED_BACKUPS_LOAD_ERROR, plan.getAction());
    assertNull(plan.getBackupToLoad());
  }

  @Test
  public void failurePlan_noBackupAndMainCorruptedNotDefault_showsAllBackupsError() {
    ProjectLoadFailurePlan plan = ProjectLoadFailurePlan.choose(
        false, true, true, false, null, mainProject);
    assertEquals(ProjectLoadFailurePlan.Action.SHOW_PROJECT_AND_ALL_BACKUPS_LOAD_ERROR, plan.getAction());
    assertNull(plan.getBackupToLoad());
  }

  @Test
  public void failurePlan_noBackupNotCorrupted_promptsLoadMain() {
    ProjectLoadFailurePlan plan = ProjectLoadFailurePlan.choose(
        false, true, false, false, null, mainProject);
    assertEquals(ProjectLoadFailurePlan.Action.PROMPT_LOAD_MAIN_PROJECT, plan.getAction());
  }

  @Test
  public void failurePlan_backupWithNonNullNext_promptsLoadBackup() {
    ProjectLoadFailurePlan plan = ProjectLoadFailurePlan.choose(
        false, false, false, false, backup2, mainProject);
    assertEquals(ProjectLoadFailurePlan.Action.PROMPT_LOAD_BACKUP, plan.getAction());
    assertEquals(backup2, plan.getBackupToLoad());
    assertNull(plan.getFailedBackupName());
  }

  @Test
  public void failurePlan_backupIteratingWithNext_reportsFailedBackupName() {
    ProjectLoadFailurePlan plan = ProjectLoadFailurePlan.choose(
        true, true, true, false, backup2, backup1);
    assertEquals(ProjectLoadFailurePlan.Action.PROMPT_LOAD_BACKUP, plan.getAction());
    assertEquals(backup2, plan.getBackupToLoad());
    assertEquals(backup1.getName(), plan.getFailedBackupName());
  }

  // ---- ProjectLoadFailureDispatchPlan coverage ----

  @Test
  public void dispatch_showBackupLoadError_noLoadTarget() {
    ProjectLoadFailureDispatchPlan dispatch = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.SHOW_BACKUP_LOAD_ERROR, false);
    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.NONE, dispatch.getLoadTarget());
    assertFalse(dispatch.shouldShowNewProject());
  }

  @Test
  public void dispatch_promptBackup_accepted_loadsBackup() {
    ProjectLoadFailureDispatchPlan dispatch = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.PROMPT_LOAD_BACKUP, true);
    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.BACKUP, dispatch.getLoadTarget());
    assertFalse(dispatch.shouldShowNewProject());
  }

  @Test
  public void dispatch_promptBackup_declined_showsNewProject() {
    ProjectLoadFailureDispatchPlan dispatch = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.PROMPT_LOAD_BACKUP, false);
    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.NONE, dispatch.getLoadTarget());
    assertTrue(dispatch.shouldShowNewProject());
  }

  @Test
  public void dispatch_unsavedBackupsError_showsNewProject() {
    ProjectLoadFailureDispatchPlan dispatch = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.SHOW_UNSAVED_BACKUPS_LOAD_ERROR, false);
    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.NONE, dispatch.getLoadTarget());
    assertTrue(dispatch.shouldShowNewProject());
  }

  @Test
  public void dispatch_allBackupsError_showsNewProject() {
    ProjectLoadFailureDispatchPlan dispatch = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.SHOW_PROJECT_AND_ALL_BACKUPS_LOAD_ERROR, false);
    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.NONE, dispatch.getLoadTarget());
    assertTrue(dispatch.shouldShowNewProject());
  }

  @Test
  public void dispatch_promptMain_accepted_loadsMain() {
    ProjectLoadFailureDispatchPlan dispatch = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.PROMPT_LOAD_MAIN_PROJECT, true);
    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.MAIN_PROJECT, dispatch.getLoadTarget());
    assertFalse(dispatch.shouldShowNewProject());
  }

  @Test
  public void dispatch_promptMain_declined_noTarget() {
    ProjectLoadFailureDispatchPlan dispatch = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.PROMPT_LOAD_MAIN_PROJECT, false);
    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.NONE, dispatch.getLoadTarget());
    assertFalse(dispatch.shouldShowNewProject());
  }

  // ---- enum values coverage ----

  @Test
  public void failureAction_allValuesExist() {
    ProjectLoadFailurePlan.Action[] actions = ProjectLoadFailurePlan.Action.values();
    assertEquals(5, actions.length);
  }

  @Test
  public void loadTarget_allValuesExist() {
    ProjectLoadFailureDispatchPlan.LoadTarget[] targets = ProjectLoadFailureDispatchPlan.LoadTarget.values();
    assertEquals(3, targets.length);
  }
}
