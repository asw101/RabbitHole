package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.pattern.Tuple2;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ModelResourceArrayUtilitiesTest {
  @Test
  public void arrayIndexParsesTrailingJointIndex() {
    assertEquals(0, ModelResourceArrayUtilities.getArrayIndexForJoint("WHEEL_000"));
    assertEquals(12, ModelResourceArrayUtilities.getArrayIndexForJoint("WHEEL_12"));
    assertEquals(-1, ModelResourceArrayUtilities.getArrayIndexForJoint("WHEEL"));
  }

  @Test
  public void arrayNameUsesCustomMappingAndSkipList() {
    Map<String, String> customNames = new HashMap<String, String>();
    customNames.put("FRONT_WHEEL", "WHEEL");

    assertEquals("WHEEL", ModelResourceArrayUtilities.getArrayNameForJoint("FRONT_WHEEL_01", customNames, null));
    assertNull(ModelResourceArrayUtilities.getArrayNameForJoint("FRONT_WHEEL_01", null, new String[] {"FRONT_WHEEL"}));
  }

  @Test
  public void arrayEntriesGroupSortAndSuppressJointNames() throws Exception {
    List<String> joints = Arrays.asList("WHEEL_02", "AXLE_00", "WHEEL_00", "WHEEL_01");

    Map<String, List<String>> entries = ModelResourceArrayUtilities.getArrayEntries(joints, null, Collections.singletonList("AXLE_00"), null);

    assertEquals(Collections.singleton("WHEEL"), entries.keySet());
    assertEquals(Arrays.asList("WHEEL_00", "WHEEL_01", "WHEEL_02"), entries.get("WHEEL"));
  }

  @Test
  public void arrayEntriesAcceptJointPairs() throws Exception {
    List<Tuple2<String, String>> joints = Arrays.asList(
        Tuple2.createInstance("FINGER_01", "HAND"),
        Tuple2.createInstance("FINGER_00", "HAND"));

    assertTrue(ModelResourceArrayUtilities.hasArray("FINGER", joints));
    assertFalse(ModelResourceArrayUtilities.hasArray("TOE", joints));
    assertEquals(Arrays.asList("FINGER_00", "FINGER_01"),
        ModelResourceArrayUtilities.getArrayEntriesFromJointList(joints, null, null, null).get("FINGER"));
  }
}
