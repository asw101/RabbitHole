package org.alice.ide;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectLoadFailureDispatchPlanTruthTableTest {
  @Test
  public void afterUserChoice_mapsPromptActionsToTargets() {
    ProjectLoadFailureDispatchPlan backupAccepted = ProjectLoadFailureDispatchPlan.afterUserChoice(ProjectLoadFailurePlan.Action.PROMPT_LOAD_BACKUP, true);
    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.BACKUP, backupAccepted.getLoadTarget());
    assertFalse(backupAccepted.shouldShowNewProject());

    ProjectLoadFailureDispatchPlan backupDeclined = ProjectLoadFailureDispatchPlan.afterUserChoice(ProjectLoadFailurePlan.Action.PROMPT_LOAD_BACKUP, false);
    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.NONE, backupDeclined.getLoadTarget());
    assertTrue(backupDeclined.shouldShowNewProject());

    ProjectLoadFailureDispatchPlan mainAccepted = ProjectLoadFailureDispatchPlan.afterUserChoice(ProjectLoadFailurePlan.Action.PROMPT_LOAD_MAIN_PROJECT, true);
    assertEquals(ProjectLoadFailureDispatchPlan.LoadTarget.MAIN_PROJECT, mainAccepted.getLoadTarget());
  }
}
