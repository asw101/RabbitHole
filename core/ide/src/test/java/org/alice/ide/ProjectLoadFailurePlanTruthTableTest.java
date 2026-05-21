package org.alice.ide;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ProjectLoadFailurePlanTruthTableTest {
  @Test
  public void choose_prioritizesBackupErrorsThenBackupPromptThenCorruption() {
    File main = new File("world.a3p");
    File backup = new File("backup.a3p");
    assertEquals(ProjectLoadFailurePlan.Action.SHOW_BACKUP_LOAD_ERROR,
        ProjectLoadFailurePlan.choose(true, false, false, false, backup, main).getAction());
    assertEquals(ProjectLoadFailurePlan.Action.PROMPT_LOAD_BACKUP,
        ProjectLoadFailurePlan.choose(false, false, true, false, backup, main).getAction());
    assertEquals(ProjectLoadFailurePlan.Action.SHOW_PROJECT_AND_ALL_BACKUPS_LOAD_ERROR,
        ProjectLoadFailurePlan.choose(false, false, true, false, null, main).getAction());
    assertEquals(ProjectLoadFailurePlan.Action.PROMPT_LOAD_MAIN_PROJECT,
        ProjectLoadFailurePlan.choose(false, false, false, false, null, main).getAction());
  }
}
