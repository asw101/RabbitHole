package org.alice.ide.coverage;

import org.junit.Test;

public class CurrentProjectAttachmentLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.ide.issue.CurrentProjectAttachment");
    stats.assertLoaded("org.alice.ide.issue.CurrentProjectAttachment");
  }
}
