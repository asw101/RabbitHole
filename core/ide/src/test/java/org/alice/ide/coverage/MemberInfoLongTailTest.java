package org.alice.ide.coverage;

import org.junit.Test;

public class MemberInfoLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.ide.ast.export.MemberInfo");
    stats.assertLoaded("org.alice.ide.ast.export.MemberInfo");
  }
}
