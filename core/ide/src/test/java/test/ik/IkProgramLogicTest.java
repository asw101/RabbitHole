package test.ik;

import org.junit.Test;

import static org.junit.Assert.*;

public class IkProgramLogicTest {
  @Test
  public void buildInfoTextIncludesOptionalJointSection() {
    String text = IkProgramLogic.buildInfoText("jointA", "transformA", "targetTransform");

    assertTrue(text.contains("jointA:\ntransformA"));
    assertTrue(text.contains("target:\ntargetTransform"));
  }

  @Test
  public void buildInfoTextStillIncludesTargetWithoutJoint() {
    String text = IkProgramLogic.buildInfoText(null, null, "targetTransform");

    assertEquals("\ntarget:\ntargetTransform\n", text);
  }
}
