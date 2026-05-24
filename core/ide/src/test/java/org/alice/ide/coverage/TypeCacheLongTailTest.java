package org.alice.ide.coverage;

import org.junit.Test;

public class TypeCacheLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.ide.type.TypeCache");
    stats.assertLoaded("org.alice.ide.type.TypeCache");
  }
}
