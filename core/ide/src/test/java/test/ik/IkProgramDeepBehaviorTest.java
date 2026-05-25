package test.ik;

import org.junit.Test;

import static org.junit.Assert.*;

public class IkProgramDeepBehaviorTest {
  @Test
  public void buildInfoTextIncludesJointAndTargetSectionsWhenPresent() {
    String text = IkProgramLogic.buildInfoText("leftArm", "jointTransform", "targetTransform");

    assertTrue(text.contains("leftArm:\n"));
    assertTrue(text.contains("jointTransform"));
    assertTrue(text.contains("target:\n"));
    assertTrue(text.endsWith("targetTransform\n"));
  }

  @Test
  public void buildInfoTextStillIncludesTargetSectionWithoutJointData() {
    String text = IkProgramLogic.buildInfoText(null, null, "targetOnly");

    assertTrue(text.startsWith("\ntarget:\n"));
    assertTrue(text.contains("targetOnly"));
  }
}
