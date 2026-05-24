package org.alice.ide.coverage;

import org.junit.Test;

public class EnumConstantResourceKeyListDataLongTailTest {
  @Test
  public void exercisesTargetClass() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise("org.alice.stageide.gallerybrowser.enumconstant.data.EnumConstantResourceKeyListData");
    stats.assertLoaded("org.alice.stageide.gallerybrowser.enumconstant.data.EnumConstantResourceKeyListData");
  }
}
