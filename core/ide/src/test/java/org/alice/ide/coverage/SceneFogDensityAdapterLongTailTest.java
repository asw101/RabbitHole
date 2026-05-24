package org.alice.ide.coverage;

import org.junit.Test;

public class SceneFogDensityAdapterLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.ide.properties.adapter.SceneFogDensityAdapter");
    stats.assertLoaded("org.alice.ide.properties.adapter.SceneFogDensityAdapter");
  }
}
