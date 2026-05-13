package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.pattern.Tuple2;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ModelResourceJointTreeUtilitiesTest {
  @Test
  public void codeReadyTreeReturnsNullForMissingJointList() {
    assertNull(ModelResourceJointTreeUtilities.makeCodeReadyTree(null, false));
  }

  @Test
  public void codeReadyTreeOrdersParentsBeforeChildren() {
    List<Tuple2<String, String>> joints = Arrays.asList(
        Tuple2.createInstance("hand", "arm"),
        Tuple2.createInstance("root", null),
        Tuple2.createInstance("finger", "hand"),
        Tuple2.createInstance("arm", "root"));

    List<Tuple2<String, String>> sorted = ModelResourceJointTreeUtilities.makeCodeReadyTree(joints, false);

    assertJoint(sorted.get(0), "root", null);
    assertJoint(sorted.get(1), "arm", "root");
    assertJoint(sorted.get(2), "hand", "arm");
    assertJoint(sorted.get(3), "finger", "hand");
  }

  @Test
  public void codeReadyTreeKeepsRootJointWhenRootRemovalDisabled() {
    List<Tuple2<String, String>> joints = Arrays.asList(
        Tuple2.createInstance("root", null),
        Tuple2.createInstance("pelvis", "root"));

    List<Tuple2<String, String>> sorted = ModelResourceJointTreeUtilities.makeCodeReadyTree(joints, false);

    assertEquals(2, sorted.size());
    assertJoint(sorted.get(0), "root", null);
    assertJoint(sorted.get(1), "pelvis", "root");
  }

  @Test
  public void codeReadyTreeCanRemoveRootAndPromoteItsChildren() {
    List<Tuple2<String, String>> joints = Arrays.asList(
        Tuple2.createInstance("root", null),
        Tuple2.createInstance("pelvis", "root"),
        Tuple2.createInstance("spine", "pelvis"));

    List<Tuple2<String, String>> sorted = ModelResourceJointTreeUtilities.makeCodeReadyTree(joints, true);

    assertEquals(2, sorted.size());
    assertJoint(sorted.get(0), "pelvis", null);
    assertJoint(sorted.get(1), "spine", "pelvis");
  }

  @Test(timeout = 1000)
  public void codeReadyTreeRejectsMissingParentInsteadOfLooping() {
    List<Tuple2<String, String>> joints = Arrays.asList(
        Tuple2.createInstance("root", null),
        Tuple2.createInstance("orphan", "missingParent"));

    try {
      ModelResourceJointTreeUtilities.makeCodeReadyTree(joints, false);
      fail("Expected missing parent to be rejected");
    } catch (IllegalArgumentException exception) {
      assertTrue(exception.getMessage().contains("orphan -> missingParent"));
    }
  }

  private static void assertJoint(Tuple2<String, String> joint, String expectedName, String expectedParent) {
    assertEquals(expectedName, joint.getA());
    assertEquals(expectedParent, joint.getB());
  }
}
