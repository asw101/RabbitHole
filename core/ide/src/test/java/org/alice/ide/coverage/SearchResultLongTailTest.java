package org.alice.ide.coverage;

import org.junit.Test;

public class SearchResultLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.ide.croquet.models.project.find.core.SearchResult");
    stats.assertLoaded("org.alice.ide.croquet.models.project.find.core.SearchResult");
  }
}
