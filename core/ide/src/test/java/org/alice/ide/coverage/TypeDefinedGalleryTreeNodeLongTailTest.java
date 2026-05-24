package org.alice.ide.coverage;

import org.junit.Test;

public class TypeDefinedGalleryTreeNodeLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.lgna.story.resourceutilities.TypeDefinedGalleryTreeNode");
    stats.assertLoaded("org.lgna.story.resourceutilities.TypeDefinedGalleryTreeNode");
  }
}
