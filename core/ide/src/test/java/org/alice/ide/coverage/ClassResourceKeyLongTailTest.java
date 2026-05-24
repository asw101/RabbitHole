package org.alice.ide.coverage;

import org.junit.Test;

public class ClassResourceKeyLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.stageide.modelresource.ClassResourceKey");
    stats.assertLoaded("org.alice.stageide.modelresource.ClassResourceKey");
  }
}
