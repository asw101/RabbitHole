package org.alice.stageide;

import org.junit.Test;

import static org.junit.Assert.*;

public class StageIDELogicTest {
  @Test
  public void isDropDownDesiredBlocksKnownSuppressionCases() {
    assertFalse(StageIDELogic.isDropDownDesired(true, true, false, false, false, false));
    assertFalse(StageIDELogic.isDropDownDesired(true, false, true, false, false, false));
    assertFalse(StageIDELogic.isDropDownDesired(true, false, false, true, false, false));
    assertFalse(StageIDELogic.isDropDownDesired(true, false, false, false, true, false));
    assertFalse(StageIDELogic.isDropDownDesired(true, false, false, false, false, true));
    assertTrue(StageIDELogic.isDropDownDesired(true, false, false, false, false, false));
  }
}
