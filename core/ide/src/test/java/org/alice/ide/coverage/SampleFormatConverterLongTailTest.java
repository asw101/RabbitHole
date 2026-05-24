package org.alice.ide.coverage;

import org.junit.Test;

public class SampleFormatConverterLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.media.audio.SampleFormatConverter");
    stats.assertLoaded("org.alice.media.audio.SampleFormatConverter");
  }
}
