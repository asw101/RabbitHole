package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SmallClassCoverageSweepTest {
  @Test
  public void exercisesLongTailClassesHeadlessly() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exerciseSweepTargets();

    assertTrue("expected many discovered target classes", stats.getDiscoveredTargetCount() >= 110);
    assertTrue("expected many classes to load", stats.getLoadedCount() >= 90);
    assertTrue("expected many classes to instantiate", stats.getInstantiatedCount() >= 40);
    assertTrue("expected few headless exercise failures", stats.getFailureCount() <= 20);
  }
}
