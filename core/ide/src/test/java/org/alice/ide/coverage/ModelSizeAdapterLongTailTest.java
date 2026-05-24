package org.alice.ide.coverage;

import org.junit.Test;

public class ModelSizeAdapterLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.stageide.properties.ModelSizeAdapter");
    stats.assertLoaded("org.alice.stageide.properties.ModelSizeAdapter");
  }
}
