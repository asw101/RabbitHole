package org.lgna.story.resourceutilities;

import org.junit.Test;
import org.lgna.story.SBiped;
import org.lgna.story.SFlyer;
import org.lgna.story.SQuadruped;
import org.lgna.story.SSwimmer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class PipelineNamingUtilitiesTest {
  @Test
  public void exposesExpectedDefaultMappings() {
    assertEquals("PenguinResource", PipelineNamingUtilities.getResourceClassName("Penguin"));

    assertTrue(PipelineNamingUtilities.sJointsToSuppressByClass.containsKey(SBiped.class));
    assertTrue(PipelineNamingUtilities.sJointsToSuppressByClass.containsKey(SFlyer.class));
    assertTrue(PipelineNamingUtilities.sJointsToSuppressByClass.containsKey(SQuadruped.class));
    assertTrue(PipelineNamingUtilities.sJointsToSuppressByClass.containsKey(SSwimmer.class));
    assertTrue(Arrays.asList(PipelineNamingUtilities.sJointsToSuppressByClass.get(SBiped.class)).contains("LEFT_TOES"));
    assertTrue(Arrays.asList(PipelineNamingUtilities.sJointsToSuppressByClass.get(SSwimmer.class)).contains("BACK_TOP_FIN_TIP"));

    assertEquals("RIGHT_PINKY_FINGER", PipelineNamingUtilities.sRemapJointsMap.get("RIGHT_RING_FINGER"));
    assertEquals("LEFT_PINKY_FINGER_KNUCKLE", PipelineNamingUtilities.sRemapJointsMap.get("LEFT_RING_FINGER_KNUCKLE"));

    Map<String, String> customArrayNames = PipelineNamingUtilities.getDefaultCustomArrayNameMap();
    assertEquals("WHEELS", customArrayNames.get("WHEEL"));
    assertEquals("TAIL", customArrayNames.get("TAIL"));

    assertTrue(PipelineNamingUtilities.getDefaultArraysToExposeFirstElementOf().contains("TAIL"));
    assertTrue(PipelineNamingUtilities.getDefaultArraysToExposeFirstElementOf().contains("NECK"));
    assertTrue(Arrays.asList(PipelineNamingUtilities.getDefaultArrayNamesToSkip()).contains("DOOR"));
  }

  @Test
  public void enumNameHelpersHandleDirectionalTokens() {
    assertEquals("LEFT_ARM", PipelineNamingUtilities.getAliceEnumNameForMayaJoint("arm_l"));
    assertEquals("TOP_RIGHT_WING", PipelineNamingUtilities.getAliceEnumNameForMayaJoint("wing_r_t"));
    assertEquals("BACK_TAIL", PipelineNamingUtilities.getAliceEnumNameForMayaJoint("tail_b"));
    assertEquals("FRONT_BOTTOM_FIN", PipelineNamingUtilities.getAliceEnumNameForMayaJoint("fin_f_b"));

    assertEquals("LEFT_HAND", PipelineNamingUtilities.getEnumNameForNameParts(Arrays.asList("left", "hand")));
    assertEquals("TOP_CLAW", PipelineNamingUtilities.getEnumNameForNameParts(new String[]{"top", "claw"}));
  }

  @Test
  public void preserveNameCheckIsCaseInsensitive() {
    assertTrue(PipelineNamingUtilities.shouldPreserveName("RobotArmature", Arrays.asList("arm", "wing")));
    assertFalse(PipelineNamingUtilities.shouldPreserveName("RobotLeg", Collections.singletonList("wing")));
  }

  @Test
  public void mayaJointNamesResolveThroughMapsGeneratedNamesAndPreservedNames() {
    assertEquals("PELVIS_LOWER_BODY", PipelineNamingUtilities.getAliceJointNameForMayaJointName("pelvis", false));
    assertEquals("LEFT_ARM", PipelineNamingUtilities.getAliceJointNameForMayaJointName("arm_l", false));
    assertEquals("HOT_AIR_BALLOON", PipelineNamingUtilities.getAliceJointNameForMayaJointName("hotAirBalloon", true));
  }

  @Test
  public void arrayJointNamesUseExplicitOrDerivedArrayNames() {
    List<String> joints = new ArrayList<>();
    for (int i = 0; i <= 10; i++) {
      joints.add("WHEEL_" + i);
    }

    assertEquals("CUSTOM_02", PipelineNamingUtilities.getAliceArrayJointName("WHEEL_2", joints, "CUSTOM"));
    assertEquals("WHEEL_02", PipelineNamingUtilities.getAliceArrayJointName("WHEEL_2", joints, null));
  }
}
