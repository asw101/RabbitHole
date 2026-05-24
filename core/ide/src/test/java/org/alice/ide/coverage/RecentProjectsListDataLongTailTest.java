package org.alice.ide.coverage;

import org.junit.Test;

public class RecentProjectsListDataLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.ide.recentprojects.RecentProjectsListData");
    stats.assertLoaded("org.alice.ide.recentprojects.RecentProjectsListData");
  }
}
