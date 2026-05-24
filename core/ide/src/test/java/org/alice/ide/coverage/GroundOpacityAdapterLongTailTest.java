package org.alice.ide.coverage;

import org.junit.Test;

public class GroundOpacityAdapterLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.stageide.properties.GroundOpacityAdapter");
    stats.assertLoaded("org.alice.stageide.properties.GroundOpacityAdapter");
  }
}
