package org.alice.stageide;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StageIDELogicBehaviorTest {
  @Test
  public void isDropDownDesiredRequiresSuperclassDecisionFirst() {
    assertFalse(StageIDELogic.isDropDownDesired(false, false, false, false, false, false));
    assertFalse(StageIDELogic.isDropDownDesired(false, true, true, true, true, true));
  }

  @Test
  public void isDropDownDesiredRejectsEachKnownSuppressionCase() {
    assertFalse(StageIDELogic.isDropDownDesired(true, true, false, false, false, false));
    assertFalse(StageIDELogic.isDropDownDesired(true, false, true, false, false, false));
    assertFalse(StageIDELogic.isDropDownDesired(true, false, false, true, false, false));
    assertFalse(StageIDELogic.isDropDownDesired(true, false, false, false, true, false));
    assertFalse(StageIDELogic.isDropDownDesired(true, false, false, false, false, true));
    assertTrue(StageIDELogic.isDropDownDesired(true, false, false, false, false, false));
  }
}
