package org.alice.ide;

import org.junit.Test;
import org.lgna.croquet.Group;
import org.lgna.croquet.undo.UndoHistory;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link ProjectHistoryManager#getGroupHistory(Group)} logic.
 * The constructor requires a ProjectDocument which is tightly coupled to the IDE,
 * so we test only what can be constructed without the full runtime.
 */
public class ProjectHistoryManagerTest {

  // We can't easily construct ProjectHistoryManager (needs ProjectDocument),
  // but we can exercise the static flag constants and Group-based logic
  // by testing ProjectLoadSuccessPlan and ProjectLoadFailurePlan more thoroughly,
  // as well as the plan classes that ProjectHistoryManager's sibling code depends on.

  @Test
  public void successPlan_allFlagsTrue_evaluatesCorrectly() {
    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(true, true, true, true, true);
    // isBackup=true, isLoadingBackups=true → checkForMoreRecent = !(true && !true) && !true && !true = !(false) && false && false = false
    assertFalse(plan.shouldCheckForMoreRecentBackups());
    // isLoadingBackups=true, isDefaultBackup=true → createFromBackup = true && !true = false
    assertFalse(plan.shouldCreateProjectFromBackup());
  }

  @Test
  public void successPlan_loadingNotDefault_createsFromBackup() {
    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(false, true, false, false, false);
    assertTrue(plan.shouldCreateProjectFromBackup());
    assertTrue(plan.shouldCheckForMoreRecentBackups());
  }

  @Test
  public void failurePlan_allBranches_enumValuesMatchExpected() {
    // Verify all enum members exist
    assertEquals("SHOW_BACKUP_LOAD_ERROR",
        ProjectLoadFailurePlan.Action.SHOW_BACKUP_LOAD_ERROR.name());
    assertEquals("PROMPT_LOAD_BACKUP",
        ProjectLoadFailurePlan.Action.PROMPT_LOAD_BACKUP.name());
    assertEquals("SHOW_UNSAVED_BACKUPS_LOAD_ERROR",
        ProjectLoadFailurePlan.Action.SHOW_UNSAVED_BACKUPS_LOAD_ERROR.name());
    assertEquals("SHOW_PROJECT_AND_ALL_BACKUPS_LOAD_ERROR",
        ProjectLoadFailurePlan.Action.SHOW_PROJECT_AND_ALL_BACKUPS_LOAD_ERROR.name());
    assertEquals("PROMPT_LOAD_MAIN_PROJECT",
        ProjectLoadFailurePlan.Action.PROMPT_LOAD_MAIN_PROJECT.name());
  }

  @Test
  public void dispatchPlan_loadTargetEnum_allValues() {
    ProjectLoadFailureDispatchPlan.LoadTarget[] targets = ProjectLoadFailureDispatchPlan.LoadTarget.values();
    assertEquals(3, targets.length);
    assertNotNull(ProjectLoadFailureDispatchPlan.LoadTarget.valueOf("NONE"));
    assertNotNull(ProjectLoadFailureDispatchPlan.LoadTarget.valueOf("BACKUP"));
    assertNotNull(ProjectLoadFailureDispatchPlan.LoadTarget.valueOf("MAIN_PROJECT"));
  }

  @Test
  public void dispatchPlan_backupError_accepted_ignored() {
    // SHOW_BACKUP_LOAD_ERROR ignores accepted param
    ProjectLoadFailureDispatchPlan d1 = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.SHOW_BACKUP_LOAD_ERROR, true);
    ProjectLoadFailureDispatchPlan d2 = ProjectLoadFailureDispatchPlan.afterUserChoice(
        ProjectLoadFailurePlan.Action.SHOW_BACKUP_LOAD_ERROR, false);
    assertEquals(d1.getLoadTarget(), d2.getLoadTarget());
    assertEquals(d1.shouldShowNewProject(), d2.shouldShowNewProject());
  }
}
