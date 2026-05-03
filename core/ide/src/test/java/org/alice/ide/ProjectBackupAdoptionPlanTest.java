package org.alice.ide;

import edu.cmu.cs.dennisc.javax.swing.option.YesNoCancelResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProjectBackupAdoptionPlanTest {
  @Test
  public void acceptingBackupPromptSavesBackupOverOriginalProject() {
    ProjectBackupAdoptionPlan plan = ProjectBackupAdoptionPlan.afterUserChoice(YesNoCancelResult.YES);

    assertEquals(
        ProjectBackupAdoptionPlan.Action.SAVE_BACKUP_TO_ORIGINAL_PROJECT,
        plan.getAction());
  }

  @Test
  public void decliningBackupPromptKeepsBackupLoadedAsCurrentProject() {
    ProjectBackupAdoptionPlan plan = ProjectBackupAdoptionPlan.afterUserChoice(YesNoCancelResult.NO);

    assertEquals(
        ProjectBackupAdoptionPlan.Action.KEEP_BACKUP_AS_CURRENT_PROJECT,
        plan.getAction());
  }

  @Test
  public void cancelingBackupPromptReloadsOriginalProject() {
    ProjectBackupAdoptionPlan plan = ProjectBackupAdoptionPlan.afterUserChoice(YesNoCancelResult.CANCEL);

    assertEquals(
        ProjectBackupAdoptionPlan.Action.RELOAD_ORIGINAL_PROJECT,
        plan.getAction());
  }
}
