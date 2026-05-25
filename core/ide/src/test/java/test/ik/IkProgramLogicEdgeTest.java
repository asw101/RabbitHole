package test.ik;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IkProgramLogicEdgeTest {
  @Test
  public void buildInfoTextIncludesJointHeaderEvenWithoutJointTransform() {
    assertEquals("jointA:\n\ntarget:\ntargetTransform\n",
        IkProgramLogic.buildInfoText("jointA", null, "targetTransform"));
  }

  @Test
  public void buildInfoTextStillRendersTargetSectionWhenTargetTransformIsNull() {
    assertEquals("\ntarget:\n\n", IkProgramLogic.buildInfoText(null, null, null));
  }
}
