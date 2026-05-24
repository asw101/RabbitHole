package org.alice.ide.coverage;

import org.junit.Test;

public class GraphicsPropertiesAttachmentLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.ide.issue.GraphicsPropertiesAttachment");
    stats.assertLoaded("org.alice.ide.issue.GraphicsPropertiesAttachment");
  }
}
