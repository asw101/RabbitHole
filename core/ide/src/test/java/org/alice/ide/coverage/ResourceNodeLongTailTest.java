package org.alice.ide.coverage;

import org.junit.Test;

public class ResourceNodeLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.stageide.modelresource.ResourceNode");
    stats.assertLoaded("org.alice.stageide.modelresource.ResourceNode");
  }
}
