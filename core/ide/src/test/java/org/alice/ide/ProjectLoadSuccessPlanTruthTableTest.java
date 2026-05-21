package org.alice.ide;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectLoadSuccessPlanTruthTableTest {
  @Test
  public void choose_marksOnlyEligibleMainLoadsForBackupCheck() {
    assertTrue(ProjectLoadSuccessPlan.choose(false, false, false, false, false).shouldCheckForMoreRecentBackups());
    assertFalse(ProjectLoadSuccessPlan.choose(true, false, false, false, false).shouldCheckForMoreRecentBackups());
    assertFalse(ProjectLoadSuccessPlan.choose(false, false, false, true, false).shouldCheckForMoreRecentBackups());
    assertFalse(ProjectLoadSuccessPlan.choose(false, false, false, false, true).shouldCheckForMoreRecentBackups());
  }
}
