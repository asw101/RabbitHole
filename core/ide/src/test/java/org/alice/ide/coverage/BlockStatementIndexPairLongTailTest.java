package org.alice.ide.coverage;

import org.junit.Test;

public class BlockStatementIndexPairLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.ide.ast.draganddrop.BlockStatementIndexPair");
    stats.assertLoaded("org.alice.ide.ast.draganddrop.BlockStatementIndexPair");
  }
}
