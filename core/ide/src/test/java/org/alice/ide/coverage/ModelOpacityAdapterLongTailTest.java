package org.alice.ide.coverage;

import org.junit.Test;

public class ModelOpacityAdapterLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.stageide.properties.ModelOpacityAdapter");
    stats.assertLoaded("org.alice.stageide.properties.ModelOpacityAdapter");
  }
}
