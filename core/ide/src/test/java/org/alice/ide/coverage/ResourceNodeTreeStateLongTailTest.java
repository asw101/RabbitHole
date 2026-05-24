package org.alice.ide.coverage;

import org.junit.Test;

public class ResourceNodeTreeStateLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.stageide.modelresource.ResourceNodeTreeState");
    stats.assertLoaded("org.alice.stageide.modelresource.ResourceNodeTreeState");
  }
}
