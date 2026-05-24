package org.alice.ide.coverage;

import org.junit.Test;

public class SearchGalleryWorkerLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.stageide.gallerybrowser.search.core.SearchGalleryWorker");
    stats.assertLoaded("org.alice.stageide.gallerybrowser.search.core.SearchGalleryWorker");
  }
}
