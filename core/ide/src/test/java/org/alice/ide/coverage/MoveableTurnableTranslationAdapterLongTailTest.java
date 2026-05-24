package org.alice.ide.coverage;

import org.junit.Test;

public class MoveableTurnableTranslationAdapterLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.stageide.properties.MoveableTurnableTranslationAdapter");
    stats.assertLoaded("org.alice.stageide.properties.MoveableTurnableTranslationAdapter");
  }
}
