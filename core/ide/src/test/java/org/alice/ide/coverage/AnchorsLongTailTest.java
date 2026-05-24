package org.alice.ide.coverage;

import org.junit.Test;

public class AnchorsLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.lgna.ik.poser.anchors.Anchors");
    stats.assertLoaded("org.lgna.ik.poser.anchors.Anchors");
  }
}
